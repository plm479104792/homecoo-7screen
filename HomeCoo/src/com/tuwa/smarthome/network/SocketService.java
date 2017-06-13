package com.tuwa.smarthome.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.google.gson.Gson;
import com.homecoolink.global.MyApp;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.activity.GatewayManegeActivity;
import com.tuwa.smarthome.activity.MusicMainActivity;
import com.tuwa.smarthome.activity.TimeTaskActivity;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.dao.AlarmMessageDao;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.GateWayDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.AlarmMessage;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.entity.Mp3;
import com.tuwa.smarthome.entity.Packet;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.global.TranObjectType;
import com.tuwa.smarthome.util.DataConvertUtil;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.NetWorkUtil;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;

public class SocketService extends Service {
	private final String TAG="SocketService";
	 private Packet jpushPacket;
	 private long alarmTime;
	 private long lastAlarmTime,nowAlarmTime;   //上次报警时间
		
	 // 单个线程机制测试
	 private Handler mHandler,sentHandler;
	 private Handler visitServerHandler;   //定时访问服务器线程
	 public AlertDialog outerChefangDialog;  //刷新外网弹出报警对话框
	 private boolean handrunflag=false;  //定时线程是否启动
     public SocketPacket socketPackage;
     public SocketPacket sentPackage;     //待发送的报文
     public SocketPacket recivePacket=new SocketPacket();         //待接收的报文
     public SocketPacket alertPacket=new SocketPacket();         //安防报文
	    
     private Socket socket = null;
     private InetSocketAddress isaddr = null; 
     public SocketCallBack socketCallBack;
     public AlertDialog chefangDialog;  //撤防对话框
     public AlertDialog sceneExecuteDialog;  //情景执行对话

     public  List<SocketPacket> devPacketList =  new ArrayList<SocketPacket>();
     List<Space> spacelist = new ArrayList<Space>();     //房间对象列表
     private Vibrator vibratorAlert,vibrator;   //震动
     private  Ringtone Alertrong,Scenertong;  //报警铃声
     
 	public  List<ThemeDevice>  themeStatelist=new ArrayList<ThemeDevice>();
	
 	private OutputStream output=null;
    private  InputStream input =null;
    private boolean inputStartflag=false;  //接收线程是否启动的标志
    private ByteBuffer  buff;    //字节缓冲区
    
	private InfraredBroadCast receiver;  //接收红外demo的广播
	private JpushReceive mMessageReceiver;//接收服务器推送的极光广播
	public static final String MESSAGE_RECEIVED_ACTION = "com.tuwa.smarthome.permission.JPUSH_MESSAGE";
	private JpushReceiveBroadCast jpushReceiveBroadCast;  //广播实例
	
	private String pAlertDeviceNo;  //外网情况下，撤防的安防设备编号
	public  List<String> devAlertList =  new ArrayList<String>();//弹对话框的安防设备列表
	
	private LocationService locationService;
	
	public byte[] recivebuf;
	
	
	//音乐Service集成
	private MediaPlayer  mMediaPlayer;  
    private int currentTime = 0;//歌曲播放进度
	private int currentListItme = -1;//当前播放第几首歌
	private List<Mp3> songs;//要播放的歌曲集合
	private Object duration;
	private static String a;
	
	
     /** 
      * 返回一个Binder对象 
      */  
	    @Override
	    public IBinder onBind(Intent intent) {
	        // TODO Auto-generated method stub
	    	return new SocketBinder();
	    }
	    
	    public class SocketBinder extends Binder {
	    	  /** 
	         * 获取当前Service的实例 
	         * @return 
	         */  
	        public SocketService getService(){  
	            return SocketService.this;  
	        }  
	    }
	    
 	     /*定时访问服务器动态同步数据*/
	
		Runnable gatewayRunnable=new Runnable() {  
		    @Override  
		    public void run() {  
		    	
		    	//断网重连机制
		    	SocketPacket socketPacket = null;
		    	if(NetValue.sIsConneted && NetValue.socketauthen){
		    		socketPacket = WebPacketUtil.getSynchroPacket();    //认证通过后每5秒发送心跳包
		    		sentPacket(socketPacket);   //发送定时同步报文到网关
		    		Log.i(TAG, "发送同步报文");
		    	}else{
		    		NetValue.autoFlag = true;  //断网重连标志
		    		socketConnect(NetValue.LOCAL_IP);
		    		Log.i(TAG, "发送认证报文");
		    	}
		   	    sentHandler.removeCallbacks(gatewayRunnable);//关闭心跳包
				sentHandler.postDelayed(this, NetValue.PULSE_TIME);  
		    }  
		};
		
		//定时解码
		Runnable readRunnable=new Runnable() {  
		    @Override  
		    public void run() { 
		    	
		    	try {
					decodePacketFromByteBuffer();  //从bytebuffer中解码
				} catch (Exception e) {
					System.out.println("捕获到解码异常...");
					e.printStackTrace();
				}
		    	mHandler.postDelayed(this, 300);    //每隔0.5秒解码
		    }  
		};
     

	    @Override
	    public void onCreate() {
	        super.onCreate();
	        System.out.println("创建了socketservice!+onCreate");
	        
	        //【1】创建字节缓冲区
	        buff=ByteBuffer.allocate(20480);
	        
	        recivebuf = new byte[20480];
	        
	        //socket连接，定时解析报文线程
	        HandlerThread thread = new HandlerThread("MyHandlerThread");
	        thread.start();//创建一个HandlerThread并启动它
	        mHandler = new Handler(thread.getLooper());
	       
	        //发送报文线程
	        HandlerThread sentThread = new HandlerThread("SentHandlerThread");
	        sentThread.start();//创建一个HandlerThread并启动它
	        sentHandler=new Handler(sentThread.getLooper());  //发送数据线程
	        
            //循环请求服务器数据  //20160502定时刷新安防设备屏蔽，由服务器推送过来
		    HandlerThread serverThread = new HandlerThread("ServerHandlerThread");
		    serverThread.start();//创建一个HandlerThread并启动它
	        visitServerHandler=new Handler(serverThread.getLooper());  //发送数据线程
	        
	        receiver = new InfraredBroadCast();
			IntentFilter filter = new IntentFilter();
			filter.addAction("send");
			filter.addAction("INFRA_DEVID");
			registerReceiver(receiver, filter);
			
//		
			//注册接收极光服务器的广播
			registerMessageReceiver();
			
			 // 注册广播接收
            jpushReceiveBroadCast = new JpushReceiveBroadCast();
            IntentFilter jpushfilter = new IntentFilter();
            jpushfilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
            jpushfilter.addAction("ACTION_JPUSH_MESSAGE");    //只有持有相同的action的接受者才能接收此广播
            registerReceiver(jpushReceiveBroadCast, jpushfilter);
            
            initLocationService();  //初始化并启动定位
            
            if (mMediaPlayer == null) {
    			mMediaPlayer = new MediaPlayer();
    		}
            
             MyApp.app.init();
	    }
	    
	    
		private void initLocationService() {
			// 获取位置服务
//			locationService = ((LocweatherApplication) getApplication()).locationService;
			System.out.println("初始化定位服务");
			
			locationService= new  LocationService(SocketService.this);
			// 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
			locationService.registerListener(mListener);
			// 注册监听
//			int type = getIntent().getIntExtra("from", 0);
			int type=0;
			if (type == 0) {
				locationService.setLocationOption(locationService
						.getDefaultLocationClientOption());
			} else if (type == 1) {
				locationService.setLocationOption(locationService.getOption());
			}
			locationService.start();
			
		}


		@Override
	    public void onStart(Intent intent, int startId) {
	        super.onStart(intent, startId);
	    
//	        System.out.println("启动了socketservice!+onStart");
	      
	    }
		

	    @SuppressWarnings("deprecation")
		@Override
	    public void onDestroy() {
	    	unregisterReceiverSafe(receiver);
	       
	        //销毁线程
	        socketClose();  //关闭socket和流
	        
	        unregisterReceiver(receiver);
	        unregisterReceiver(mMessageReceiver);  //取消接收服务器极光广播
	        unregisterReceiver(jpushReceiveBroadCast);  //取消接收极光广播
	        
//	        System.out.println("销毁了socketservice!");
	        super.onDestroy();
	    }

	    @Override
	    public boolean onUnbind(Intent intent) {
	        return super.onUnbind(intent);
	    }
	    
		 
		  //外网情况下，撤防后10秒内，不弹出报警对话框
		  Runnable delayCancelAlertRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(10000);
						devAlertList.remove(pAlertDeviceNo); //用户处理过后，接受安防情景触发 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
		//接收到极光推送消息时添加网络当前时间戳
		  Runnable getNetTimeRunnable = new Runnable() {
				@Override
				public void run() {
					judgeJpushMessage(System.currentTimeMillis());
				}
			};	
			
			
	    
        // 用于子线程与主线程通信的Handler  
        final Handler msgHandler = new Handler() {  
    	
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            // 将返回值回调到callBack的参数中  
            switch (msg.what) {
			case 0x121:    //情景学习消息
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); 
				Scenertong = RingtoneManager.getRingtone(getApplicationContext(), notification);  
				Scenertong.play();
				
//			    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); 
			 
			    vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
		        long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启   
		        vibrator.vibrate(pattern,-1);   
				
				break;

			case 0x122:     //设备状态反馈
				if (socketCallBack!=null) {
					socketCallBack.callBack((TranObject) msg.obj); 
				}
				
				break; 
			case 0x123:     //弹出安防报警对话框
//			    Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			    Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); 
                Alertrong = RingtoneManager.getRingtone(getApplicationContext(), notification1);  
                Alertrong.play();
                
				SocketPacket alertPacket=(SocketPacket)msg.obj;
				showAlertSceneDialog(alertPacket);
				
			    vibratorAlert = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
		        long [] pattern1 = {100,400,100,400};   // 停止 开启 停止 开启   
		        vibratorAlert.vibrate(pattern1,-1); 
				break;
			case 0x124:    //底层情景开关触发执行情景
				
				break;
			}
        }  
    }; 
	    
	    
	    /**
      * socket连接函数
      */
	    public void socketConnect(final String ip) {
	    	//线程机制
	        Runnable socketConnetRunnable = new Runnable() {
	        @Override
	         public void run() {
		        	try  
		        	{
		        	    socket = new Socket();
						isaddr = new InetSocketAddress(ip, NetValue.LOCAL_PORT);
						socket.connect(isaddr, 1000);
						
						input=socket.getInputStream();
						output=socket.getOutputStream();
						System.out.println("==网络切换建立建立socket连接==="+ip);
						
						if ((socket.isConnected()==true)&&(socket.isClosed()==false)) {
							NetValue.sIsConneted = true; // 连接了socket连接
							if (!inputStartflag) { // 判断接收线程是否启动
								inputThread.start();
								inputStartflag = true;
							}
								// 每次建立socket连接时请求认证
							SocketPacket authPacket = WebPacketUtil.getAuthenticPacket();
							sentPacket(authPacket); // 通过内网验证网关的合法性
						}
		        	   
					   NetValue.IP_CONNECT_FLAG=true;
		        	   NetValue.LOCAL_IP=ip;
		        	   
		        	 } 
		        	catch(IOException e)  
		        	{  
		        	   e.printStackTrace();  
//		        	   socketClose();       //断开socket连接，则停止心跳包
		        	   //将网络状态封装为回调对象丢给主线程
//		        	   if (NetValue.callbackflag) { //网关ip适配过之后且非断网重连，网络连接异常才会提示
		        		 TranObject tranObject = new TranObject(NetValue.NONET, TranObjectType.NETMSG);
	 			         msgHandler.sendMessage(msgHandler.obtainMessage(0x122,tranObject));
//		        	   }
		        	}  
	            }
	         };
	         sentHandler.post(socketConnetRunnable);//将线程post到Handler中
		}
	    
	    /**
	      * socket发送报文函数
	      */
		    public  void sentPacket(SocketPacket socketPacket) {
		    	 this.sentPackage=socketPacket;
		    	
		    	//发送报文的sentRunable
		        Runnable sentPacketRunnable = new Runnable() {
		        @Override
		         public void run() {
		        	   try {
							socket.sendUrgentData(0xFF);  //判断socket服务器端是否断开链接
		 	    	        NetValue.inputflag=true;       //打开输入流
							
							byte[] sentBytes = null;
							try {
								sentBytes = WebPacketUtil.packetToByteStream(sentPackage);
								output.write(sentBytes);
								output.flush();
								
								DataConvertUtil.tprintHexString(sentBytes);  //发送的数据打印
								
							} catch (Exception e) {
								System.out.println("捕获到编码异常...");
								e.printStackTrace();
							}
						
						} catch (IOException e) {
							e.printStackTrace();
							NetValue.sIsConneted=false;   //socket连接正常
							NetValue.inputflag=false;       //打开输入流20150511
							System.out.println("发送时socket链接异常,链接信息："+NetValue.LOCAL_IP);
						} 
		            }
		         };
		         sentHandler.post(sentPacketRunnable);//将线程post到Handler中
			}
		    
	    
	       //socket连接后，启动接收线程
//		   Thread inputThread=new Thread(new Runnable() {
//				public void run() {
//			    while(true)
//				{
//			    	if((NetValue.sIsConneted==true)&&(socket.isClosed()==false)){
//	 				    try {
//							input=socket.getInputStream();
//							  //判断并验证报文
//			 				 readAndconvertInputStream(input);
//						} catch (IOException e) {
//							NetValue.sIsConneted=false;
////							System.out.println("捕获到socket已经关闭");
//							e.printStackTrace();
//						}
//				   }
//				 }
//				}
//			 });
		    
		  Thread inputThread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				byte[] recivebuf=new byte[20480];
				int len;
				while(true)
				{
					if((NetValue.sIsConneted==true)&&(socket.isClosed()==false)){
							try {
				        		   while((len = input.read(recivebuf))>0){     //可以输出打印流
				        		         putBytes(recivebuf,len);   //将接收的字节报文放回bytebuffer
				        		     	 mHandler.post(readRunnable);  //添加一个解码任务
					               }
							} catch (IOException e) {
								e.printStackTrace();
							}
					  }
				}
				
			}
		});
		   
		   
//       //判断报文字节流
//		private void readAndconvertInputStream(InputStream input) {
//			
//			    byte[] recivebuf = new byte[20480];
//	            int len;  //一次读取的字节长度
//	        	try {
//	        		   while((len = input.read(recivebuf))>0){     //可以输出打印流
//	        		       putBytes(recivebuf,len);   //将接收的字节报文放回bytebuffer
//		            	   mHandler.post(readRunnable);
//		            	 }
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//		}
		
		
	
		
	    /**
	     * 解析字节缓存为报文
	     */
        public void decodePacketFromByteBuffer(){
        	 byte[] head =new byte[4];
	         byte[] body =new byte[30];   //报文head后，data前
	         byte[] datalen =new byte[2];   //报文datalen
        	 readBytes(head,4);        //读取报头
        	 String strhead=new String(head);
        	 if (strhead.equals("AADD")) {
        		 readBytes(body,30);
        		 System.arraycopy(body, 28, datalen, 0, 2);
        		 short sdataLen=DataConvertUtil.byte2Short(datalen);  //dataLen字节转换为short 00 01
        		 
        		 int packetLen=sdataLen+30;   //整个报文的长度
        	    int bufavaillen=getBufferAvailability();
        		 
        	    if (bufavaillen>=packetLen) { //buffer中可读的字节大于等于报文的长度
        	    	 byte[] rBytePacket=new byte[packetLen];  //实际接收的报文字节数组
        	    	 getBytes(rBytePacket,packetLen);  //从bytebuffer中取走报文
        	    	 
        	    	 DataConvertUtil.rprintHexString(rBytePacket);  //接收的数据打印
		              //字节数组转换为对象
		             recivePacket=WebPacketUtil.byteToSocketPacket(rBytePacket);
		             
		             //将设备反馈的报文封装为回调对象丢给主线程
		             TranObject tranObject = new TranObject(recivePacket, TranObjectType.DEVMSG);
		             msgHandler.sendMessage(msgHandler.obtainMessage(0x122,	tranObject));
		             
		             //【判断并处理报文，情景学习，联动；安防联动】 
			         judgeAndConvertPacket(recivePacket);
		           
				}else {
					System.out.println("读取的报文比报文的实际长度小");
				}
        	 }
        }


	    

        /**
         * 回调函数
         * @param callBack
         */
		public void callSocket( SocketCallBack callBack) {  
	    	   this.socketCallBack=callBack;    //注意全局变量的使用    
	    } 

	    /** 
	     * socket接收数据回调接口
	     */  
	    public interface SocketCallBack {  
	        public void callBack(TranObject tranObject);  
	    }  
	    
	    /**
      * 关闭本地socket
      */
		public void socketClose() {
			   try  
			   {  
				   if(socket != null)  
				   { 
					 System.out.println("socke关闭");
					 NetValue.sIsConneted=false;  //socket未连接
					 NetValue.socketauthen=false; //关闭socket，认证标志为false
		        	 NetValue.inputflag=false;   //socket异常，关闭输入流
		        	 
//		        	 System.out.println("=====socket关闭===输入流标志==="+NetValue.inputflag);
		        	if (input!=null) {
		        	   input.close();
					}
		         	 mHandler.removeCallbacks(readRunnable);//关闭解析报文包
					 sentHandler.removeCallbacks(gatewayRunnable);//关闭心跳包
					 if(inputThread!= null && inputThread.isAlive()){
						  inputThread.interrupt();
			         }
					 socket.close();  
				   }  
			   }
			   catch(IOException e)  
			   {  
//				   System.out.println("socke关闭异常");
			        e.printStackTrace();  
			   }  
		}
	    

		private Builder builder;
		/**
	    * 判断并处理报文
	    * @param recivePacket
	    */
		public  void judgeAndConvertPacket(SocketPacket rpacket) {
			if(rpacket!=null){
				short datatype=rpacket.getDataType();
				String devsate=(String)rpacket.getData(); 
				if (datatype==NetValue.DATA_ACK_AUTH) {  //认证返回
					if (devsate.equals("0")) {
//						System.out.println("=====认证通过====deviceSysnFlag==="+SystemValue.deviceSysnFlag);
						NetValue.socketauthen=true;  //认证通过
						NetValue.IP_CONNECT_FLAG=true;   //网关已经ping通，内网存在网关
						NetValue.netFlag = NetValue.INTRANET;
						
						
						 //===***===同步设备的最新状态===***===
						if(SystemValue.deviceSysnFlag==false){
							
							 //===***===请求风扇状态===***===
							SocketPacket fanPacket = WebPacketUtil
									   .getDevFanStatePacket(SystemValue.gatewayid);
							sentPacket(fanPacket); // 发送请求所有设备状态
							System.out.println("请求风扇状态。。。");
							
							new Handler().postDelayed(new Runnable(){    
				    		    public void run() {    
				    				 //===***===同步设备的最新状态===***===
									if(SystemValue.deviceSysnFlag==false){
										SystemValue.deviceSysnFlag=true;
										System.out.println("请求所有设备的状态。。。");
										//手机端从网关认证通过，向网关请求所有设备状态
										SocketPacket devAllPacket = WebPacketUtil
												   .getDevAllStatePacket(SystemValue.gatewayid);
										sentPacket(devAllPacket); // 发送请求所有设备状态
									}  
				    		    }    
				    		 }, 2000);  
						
						}
						
					    //认证通过，打开socket心跳包线程
					    sentHandler.postDelayed(gatewayRunnable,NetValue.PULSE_TIME);
					}else {
						NetValue.socketauthen=false;  //认证未通过
						socketClose();  //网关认证失败后，socket服务端关闭，socket客户端对应也要关闭
					}
				}else {  //普通设备状态返回
					int devwidetype=WebPacketUtil.findWidetypeByDevtype(rpacket.getDevType());
//					Log.i("screensocket",devsate+"  设备类型 "+String.valueOf(devwidetype)+"  收到的报文 ："+rpacket.toString());
					switch (devwidetype) {
					case SystemValue.SENSOR:   //传感器类设备
						String deviceNo=rpacket.getDevId();
						if (devsate.equals("01000000")) {  //硬件报警0
							
							//TODO 这里需要判断下 传过来的报警设置 是否联动了音乐，如果有则需要 跳转音乐界面并播放音乐，如果没有不出来
							APPThemeMusic appThemeMusic=new APPThemeMusicDao(SocketService.this).GetAppThemeMusicByDeviceNo(deviceNo);
							if (appThemeMusic==null || appThemeMusic.equals("")) {
							}else {
								//后天播放联动音乐  2016-10-05
								String jpushMessage=MusicUtil.GetMusicOrderScenePanel(appThemeMusic);
								MusicUtil.CtrlMusic(jpushMessage, SocketService.this);
							}
							
							long time=System.currentTimeMillis();
							InserAlarmMsgToLocal(rpacket,time);  //报警消息插入到本地数据库总
						
							NetValue.isAcceptAlert=acceptOrRefuseAlertDailog(deviceNo);
							if (NetValue.isAcceptAlert) {  //处于布防状态,屏蔽同一时刻连续上报警状态
						
							    Message msg=new Message();
					            msg.what=0x123;
					            msg.obj=rpacket;
					            msgHandler.sendMessage(msg);
//					            NetValue.isAcceptAlert=false;  //弹出对话框，不再接受触发
							 }
						}else if (devsate.equals("10000000")) { //其它客户端对硬件撤防
							if(chefangDialog!=null){
								 chefangDialog.dismiss();
								 Alertrong.stop();
								 devAlertList.remove(deviceNo); //用户处理过后，接受安防情景触发 
//								NetValue.isAcceptAlert=true;   //用户处理过后，接受安防情景触发
							}
						}
						break;
					case SystemValue.SCENE:
						/** 硬件情景开关
						* TODO 这里需要判断下是否硬件情景也设置了情景音乐 有播放，没有不出来
						* TODO  update 2016-10-05   后台播放情景音乐
						* 根据deviceNo和设备状态
						*/
						String deviceNo1=rpacket.getDevId();
						APPThemeMusic appThemeMusic=new APPThemeMusicDao(this).GetAppThemeMusicByDeviceNoAndDeviceState(deviceNo1, devsate);
//						Log.i("screensocket","没有进来？    硬件面板"+appThemeMusic.toString());
						String jpushString= MusicUtil.GetMusicOrderScenePanel(appThemeMusic);
						MusicUtil.CtrlMusic(jpushString, SocketService.this);
						break;
						
					default:  //1开关    3门窗   5插座
						break;
					}
				}
			}
		}

      /**
       * 根据deviceNo判断对应安防产品报警对话框是否弹出
       * @param deviceNo
       * @return
       */
      private boolean acceptOrRefuseAlertDailog(String deviceNo) {
    	
    	  for(int i=0;i<devAlertList.size();i++){
    		  if(devAlertList.get(i).equals(deviceNo)){
    			  return false;
    		  }
    	  }
    	  devAlertList.add(deviceNo);
		  return true;
	 }


		//安防报警对话框
  		private void showAlertSceneDialog(final SocketPacket alertpacket) {
//            System.out.println("======弹弹弹出情景对话框==========");
            final String deviceNo=alertpacket.getDevId();
            String gatewayid=SystemValue.gatewayid;
            Device devdto=new DevdtoDao(null).findDevByDeviceNoAndGatewayNo(deviceNo, gatewayid);
            UserSpaceDevice useSpacedev=new UserSpaceDevDao(null).findDeviceSpace(SystemValue.phonenum, deviceNo);
            
            String alertname="";
            String spacename="";
            if (devdto!=null) {
               alertname=devdto.getDeviceName();  //安防设备名称
		    }
            if(useSpacedev!=null){
            	  spacename = WebPacketUtil.getSpaceName(useSpacedev.getSpaceNo()); //安防设备位置 
            }
         		builder = new AlertDialog.Builder(this); 
        		   builder.setTitle(alertname+"报警提示:");
        		     builder.setMessage("检测到"+spacename+alertname+"发生报警，系统正在为您处理！");
        		     builder.setPositiveButton("撤防",
        	    		 new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int whichButton) {
        					    alertpacket.setData("10");
    							SocketPacket cancelAlertpkt=WebPacketUtil.getCancelAlertPacket(alertpacket);
      					
    							//=======直接通过内网发送撤防指令=======
//    							sentPacket(cancelAlertpkt);  //发送安防设备撤防报文
    							sentCmdByServerOrGateway(cancelAlertpkt);  //判断并通过内网或外网发送
    							
    							if(NetValue.netFlag==NetValue.OUTERNET){//外网撤防后10s内，同一个设备报警不弹框
    								pAlertDeviceNo = deviceNo;
    								visitServerHandler.post(delayCancelAlertRunnable);
    							}else{
    								devAlertList.remove(deviceNo); //用户处理过后，接受安防情景触发 
    							}
    						
//      						NetValue.isAcceptAlert=true;   //用户处理过后，接受安防情景触发
      						Alertrong.stop();  //停止报警铃声
        						}
        					});
        		     builder.setNegativeButton("取消",
        		    	 new DialogInterface.OnClickListener() {
  						
  						@Override
  						public void onClick(DialogInterface dialog, int which) {
  							devAlertList.remove(deviceNo); //用户处理过后，接受安防情景触发 
//  							NetValue.isAcceptAlert=true;   //用户处理过后，接受安防情景触发
  							Alertrong.stop();  //停止安防报警铃声
  						}
  					});
//        	}
            
        	   chefangDialog = builder.create();  
      		     //在dialog  show方法之前添加如下代码，表示该dialog是一个系统的dialog**  
      		   chefangDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)); 
      		   chefangDialog.show();
  		}
  		
  		
		/**
		 * 根据网络类型从内网或者外网发送
		 * @param socketPacket
		 */
		private void sentCmdByServerOrGateway(SocketPacket socketPacket) {
			switch (NetValue.netFlag) {
			case NetValue.OUTERNET: // 外网
				//将命令封装为字符串发送到服务器
				byte[] sentBytes=WebPacketUtil.packetToByteStream(socketPacket);
				
				sendCmdToServer(sentBytes);   //发送到服务器的命令串

				break;
			case NetValue.INTRANET: // 内网
				sentPacket(socketPacket); // 发送请求认证报文到网关
				break;
			}
	  
		}
		
		
		  /**
	  	 * 发送字节字符串到服务器
	  	 */
	  	public void sendCmdToServer(byte[] sentBytes) {
	  		String  strCmd=DataConvertUtil.toHexUpString(sentBytes);
	  		int strLength=strCmd.length();
	  		String strSub="42424141"+strCmd.substring(8, strLength);
	  		strCmd=strSub;
	  		
	  		RequestParams params = new RequestParams();
	  		params.addBodyParameter("devicePacketJson",strCmd);
	  		
	  		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
	  		utils.send(HttpMethod.POST, NetValue.DEVICE_CONTROL_URL, params,new RequestCallBack<String>() {

	  			@Override
	  			public void onFailure(HttpException arg0, String arg1) {
	  				ToastUtils.showToast(SocketService.this, "访问服务器失败，请检查网络连接",SystemValue.MSG_TIME);
	  			}

	  			@Override
	  			public void onSuccess(ResponseInfo<String> arg0) {
	  				Gson gson = new Gson();
	  				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
	  				if (message != null) {
	  					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
//	  						System.out.println("控制设备服务器返回"+ message.getMessageInfo());
	  					} else {
//	  						showCustomToast(message.getMessageInfo());
	  					}
	  				}
	  			}
	  		});
	  	}
  		
			
	   /**
	    * 开启定时访问网关线程，socket心跳包
	    */
		public void startVisitGatewayThread() {
			if (!handrunflag) {
//				System.out.println("=====扔定时访问网关线程=======");
				sentHandler.postDelayed(gatewayRunnable,NetValue.PULSE_TIME);//开启定时访问网关心跳包
				handrunflag=true;
			}
		}
		
		//关闭访问服务器线程
		public void stopVisitGatewayThread() {
			if (handrunflag) {
//				System.out.println("=====取消定时访问网关线程=======");
				 sentHandler.removeCallbacks(gatewayRunnable);//关闭定时访问网关心跳包
				handrunflag=false;
			}
		}
   

		
				
	    /**
	     * 接收红外转发器的红外码
	     * @author WSN-520
	     *
	     */
		private class InfraredBroadCast extends BroadcastReceiver {     //收到广播后关闭AddCameraActivity

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				    String action=arg1.getAction();
				    if (action.equals("INFRA_DEVID")) {  //接收红外设备id
						String devid=arg1.getStringExtra("devid");
						NetValue.DEVID_INFRA=devid;
//						System.out.println("===接收到的红外设备id是=="+NetValue.DEVID_INFRA);
					}else if (action.equals("send")) {  //接收红外码
						byte[] infraredbuf=arg1.getByteArrayExtra("infrared");
						String str=DataConvertUtil.toHexUpString(infraredbuf);
//						System.out.println("接收到的红外码==="+str);
						DataConvertUtil.rprintHexString(infraredbuf);
						SocketPacket infrarePacket=WebPacketUtil.infraredConverToPacket(str);
						if (NetValue.socketauthen) {  //socket已建立连接通过认证，重新认证网关合法性
							//封装并发送红外报文
						
						    sentPacket(infrarePacket);   //通过内网验证网关的合法性
					       }else { 
//					    	   socketConnect(NetValue.LOCAL_IP);  //重新建立连接，认证
					    	   byte[] sentBytes=WebPacketUtil.packetToByteStream(infrarePacket);
					    	   WebPacketUtil.sendCmdToServer(sentBytes,0);
					   }
					}
			}

		}
		
		//取消动态注册的广播
		private void unregisterReceiverSafe(BroadcastReceiver receiver) {
		    try {
		       unregisterReceiver(receiver);
		    } catch (IllegalArgumentException e) {
		        // ignore
		    }
		}
		
		
		//注册极光推送接收端
		public void registerMessageReceiver() {
			mMessageReceiver = new JpushReceive();
			IntentFilter filter = new IntentFilter();
			filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
			filter.addAction(MESSAGE_RECEIVED_ACTION);
			registerReceiver(mMessageReceiver, filter);
//			System.out.println("===极光接收端已经启动===");
		}
		

		/**
		 * 极光转播设备状态广播接收端
		 * @author WSN-520
		 *
		 */
		public class JpushReceiveBroadCast extends BroadcastReceiver {
	
			@Override
			public void onReceive(Context context, Intent intent) {
				// 得到广播中得到的数据，并显示出来
				String message = intent.getStringExtra("jpushMessage");
				
				Gson gson=new Gson();
				final Jpush jpush=gson.fromJson(message,Jpush.class);
				
				String packtJson=(String) jpush.getObject();
			    jpushPacket = gson.fromJson(packtJson,Packet.class);
			    
			    byte[] rJpushPacket = null;
				try {
					String strPacket=jpushPacket.getPacket();
					rJpushPacket = DataConvertUtil.toByteArray(strPacket);
				} catch (Exception e) {
					System.out.println("捕获到极光消息推送解码异常...");
					e.printStackTrace();
				}
				
				// 字节数组转换为对象
				recivePacket = WebPacketUtil.byteToSocketPacket(rJpushPacket);
	
				// 将设备反馈的报文封装为回调对象丢给主线程
				TranObject tranObject = new TranObject(recivePacket,
						TranObjectType.DEVMSG);
				msgHandler.sendMessage(msgHandler.obtainMessage(0x122, tranObject));
			    
			    
			    int jpushMsgType=jpush.getMesssageType();
			    if(jpushMsgType==2){  //安防类型的极光消息
//			    	System.out.println("======接收到安防消息推送=====");
					     
                   nowAlarmTime=System.currentTimeMillis();
                   long time=nowAlarmTime-lastAlarmTime;
                   Log.i("343", "nowAlarmTime"+nowAlarmTime+"lastAlarmTime"+lastAlarmTime);
                   if(time>3000){
                	   lastAlarmTime=nowAlarmTime;
                	   alarmTime = jpush.getTime();
    		    	
    		    	   alertPacket=recivePacket;
//    		   		   visitServerHandler.post(getNetTimeRunnable);
    		    	 
    			       judgeJpushMessage(System.currentTimeMillis());
                   }
			    	
                 
	
			    }
			}
		}
		
		//处理极光的消息
		public void judgeJpushMessage(long nowTime){
		    
		    new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		    
		    new Date(alarmTime);
		    new Date(nowTime);
		    
//		    System.out.println("===极光推送的时间"+date);
//		    System.out.println("===极光推送的时间"+formatter.format(date));
//		    System.out.println("===手机端接收的时间"+formatter.format(datenow));
		    
            long timeLength=nowTime-alarmTime;
            if(timeLength<60*1000){
            	// 【判断并处理报文，情景学习，联动；安防联动】
    			judgeAndConvertPacket(alertPacket);
//    		   System.out.println("发送接收的时间差小于1分钟，弹出报警对话框");
            }
            
//               System.out.println("报警超时，报警消息插入报警列表");
//               String deviceNo=alertPacket.getDevId();
//               String gatewayNo=alertPacket.getGatewayId();
//               AlarmMessage msg=new AlarmMessage();
//               msg.setDeviceNo(deviceNo);
//               msg.setGatewayNo(gatewayNo);
//               msg.setTime(alarmTime);
//               Log.i("343", "报警消息"+msg.toString());
//               new AlarmMessageDao(SocketService.this).add(msg);
            
            String msgData=(String) alertPacket.getData();
            if(msgData.equals("01000000")){ 
            InserAlarmMsgToLocal(alertPacket,alarmTime);
            }
		}
            
            
    		/**
    		 * 将报警信息插入数据库消息表中
    		 * @param alertPacket
    		 * @param time
    		 */
    		private void InserAlarmMsgToLocal(SocketPacket alertPacket,long time){
    			   String deviceNo=alertPacket.getDevId();
                   String gatewayNo=alertPacket.getGatewayId();
                   AlarmMessage msg=new AlarmMessage();
                   msg.setDeviceNo(deviceNo);
                   msg.setGatewayNo(gatewayNo);
                   msg.setTime(time);
                   new AlarmMessageDao(SocketService.this).add(msg);
    		}
     
		
		
		
//		/**
//		 * 获取网络时间
//		 */
//		public  long getNetWorkTime(){
//			long time=System.currentTimeMillis();
//			  try {
//					URL url=new URL("http://www.bjtime.cn");//取得资源对象
//					  URLConnection uc=url.openConnection();//生成连接对象
//					  uc.connect(); //发出连接
//					  time=uc.getDate(); //取得网站日期时间
//					  
////					  System.out.println("===网络时间"+time);
//					  Date date=new Date(time); //转换为标准时间对象
//					  SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
////					  System.out.println("===手机接收的当前时间"+formatter.format(date));
//					  
//				} catch (MalformedURLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			return time;
//		}
//		
		
		
		
		
		
		/**
		* add bytes to the buffer, if buffer is full, remove the oldest one and add
		* @param input,如果缓存已满，从头开始一个一个字节移除
		*/
		synchronized public void putBytes(byte input[], int len) {
		   for (int m = 0; m < len; m++) {
			   try {
			       buff.put(input[m]);
			   } catch (BufferOverflowException e) {
				 buff.flip();
				 buff.get();
				 buff.compact();
				 buff.put(input[m]);
			   }
		   }
		}

		/**
		* read, but not take bytes from the buffer
		* @param output
		* @param length
		* @return actual read length
		* 从缓存中读字节，但是不取出
		*/
		synchronized private int readBytes(byte output[], int length) {
		//parameter check
		    if (output.length < length) {
		    return -1;
		}

		int cnt = 0;

		buff.flip(); //set read mode
		int p = buff.position();
		for (int m = 0; m < length; m ++) {
		  try {
		        output[m] = buff.get();
		        cnt ++;
		  } catch (BufferUnderflowException e) {
		        buff.position(p);
		        buff.compact(); //set back to write mode
		      return cnt;
		  }
		}
		   buff.position(p);
		   buff.compact(); //set back to write mode
		   return cnt;
		}

		/**
		* get bytes from the buffer,
		* @param output
		* @param length
		* @return the actual read length of the buffer, length or the remaining of the buffer
		* 从缓存中取字节
		*/
		synchronized private int getBytes(byte output[], int length) {
		//parameter check
		    if (output.length < length) {
	   	       return -1;
		    }

		int cnt = 0;
		buff.flip();
		for (int m = 0; m < length; m ++) {
		try {
		    output[m] = buff.get();
		    cnt ++;
		  } catch (BufferUnderflowException e) {
		    buff.compact(); //set back to write mode
		    return cnt;
		  }
		}
		buff.compact(); //set back to write mode
		return cnt;
		}

		/**
		 * @return
		 */
		synchronized private int getBufferAvailability () {

		      return buff.capacity() - buff.remaining();
		}
		
		
		/*****
		 * @see copy funtion to you project
		 *      定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
		 */
		private BDLocationListener mListener = new BDLocationListener() {

			public void onReceiveLocation(BDLocation location) {
				if (null != location
						&& location.getLocType() != BDLocation.TypeServerError) {
					StringBuffer sb = new StringBuffer(256);
					sb.append("time : ");
					/**
					 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
					 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
					 */
					sb.append(location.getTime()); // 当前时间年月日 时分秒
					sb.append("\nerror code : ");
					sb.append(location.getLocType());
					sb.append("\nlatitude : ");
					sb.append(location.getLatitude());
					sb.append("\nlontitude : ");
					sb.append(location.getLongitude());
					sb.append("\nradius : ");
					sb.append(location.getRadius());
					sb.append("\nCountryCode : ");
					sb.append(location.getCountryCode());
					sb.append("\nCountry : ");
					sb.append(location.getCountry());
					sb.append("\ncitycode : ");
					sb.append(location.getCityCode());
					sb.append("\ncity : ");
					sb.append(location.getCity()); // 当前所在城市
					sb.append("\nDistrict : ");
					sb.append(location.getDistrict());
					sb.append("\nStreet : ");
					sb.append(location.getStreet());
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
					sb.append("\nDescribe: ");
					sb.append(location.getLocationDescribe());
					sb.append("\nDirection(not all devices have value): ");
					sb.append(location.getDirection());
					sb.append("\nPoi: ");
					if (location.getPoiList() != null
							&& !location.getPoiList().isEmpty()) {
						for (int i = 0; i < location.getPoiList().size(); i++) {
							Poi poi = (Poi) location.getPoiList().get(i);
							sb.append(poi.getName() + ";");
						}
					}
					if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
						sb.append("\nspeed : ");
						sb.append(location.getSpeed());// 单位：km/h
						sb.append("\nsatellite : ");
						sb.append(location.getSatelliteNumber());
						sb.append("\nheight : ");
						sb.append(location.getAltitude());// 单位：米
						sb.append("\ndescribe : ");
						sb.append("gps定位成功");
					} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
						// 运营商信息
						sb.append("\noperationers : ");
						sb.append(location.getOperators());
						sb.append("\ndescribe : ");
						sb.append("网络定位成功");
					} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
						sb.append("\ndescribe : ");
						sb.append("离线定位成功，离线定位结果也是有效的");
					} else if (location.getLocType() == BDLocation.TypeServerError) {
						sb.append("\ndescribe : ");
						sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
					} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
						sb.append("\ndescribe : ");
						sb.append("网络不同导致定位失败，请检查网络是否通畅");
					} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
						sb.append("\ndescribe : ");
						sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
					}
					// logMsg(sb.toString());
					locationService.stop();

					if (location.getCity() != null) {
						SystemValue.city = location.getCity().substring(0,
								location.getCity().length() - 1);
						System.out.println("====当前定位===city===" + SystemValue.city);
					}
				}
			}

		};
		
		
		
		
		
		//音乐集成
		/**
		 *得到当前播放进度 
		 */
		public int getCurrent() {
			if (mMediaPlayer.isPlaying()) {
				return mMediaPlayer.getCurrentPosition();
			} else {
				return currentTime;
			}
		}
		
		/**
		 *	跳到输入的进度 
		 */
		public void movePlay(int progress) {
			mMediaPlayer.seekTo(progress);
			currentTime = progress;
		}
		/**
		 * 随机播放
		 * */
		public void RandomPlayMusic(String path){
			System.out.println("randomplaymusic	:	"+path);
			try {
		//		mMediaPlayer.reset();
				mMediaPlayer.setDataSource(path);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				mMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
					@Override
					public void onCompletion(MediaPlayer mp) {
						RandomNext();
					}
					
				});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		public void RandomNext(){
			if(++currentListItme >=songs.size()){
				currentListItme=0;
			}
			Math.random();
			int b=(int) (1+Math.random()*songs.size());
			currentListItme=b-1;
			System.out.println("randomNext"+currentListItme);
			try {
				playMusic(songs.get(currentListItme).getUrl());
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		 * 单曲循环播放
		 * */
		public void singlePlay(String path){
			if(a==null){
				a=path;
			}
			System.out.println("singlePlay");
			try {
		//		mMediaPlayer.reset();
				mMediaPlayer.setDataSource(path);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						singlePlay(a);
					}
				});
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		/**
		 *	根据歌曲存储路径播放歌曲 n
		 */
		public void playMusic(String path) {
			try {
				/* 重置MediaPlayer */
				if (path!=null) {
					a=path;
				}
				mMediaPlayer.reset();
				/* 设置要播放的文件的路径 */
				mMediaPlayer.setDataSource(path);
				// mMediaPlayer = MediaPlayer.create(this,
				// R.drawable.bbb);播放资源文件中的歌曲
				/* 准备播放 */
				System.out.println("mediaplayer");
				mMediaPlayer.prepare();
				System.out.println("3333333333333333333333333");
				/* 开始播放 */
				mMediaPlayer.start();
				
				mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					public void onCompletion(MediaPlayer arg0) {
						// 播放完成一首之后进行下一首
//						nextMusic();
						/**
						 * @Description:音乐播放风格
						 * @Date:2016-06-04
						 * @author xiaobai
						 * */
						if (SystemValue.MUSIC_STYLE.equals(SystemValue.MUSIC_STYLE_LIST)) {
//							System.out.println(" 6666666666666666666	"+SystemValue.MUSIC_STYLE);
							nextMusic();
						}else if (SystemValue.MUSIC_STYLE.equals(SystemValue.MUSIC_STYLE_RANDOM)) {
//							System.out.println(" 77777777777777777	"+SystemValue.MUSIC_STYLE);
							RandomNext();
						}else if (SystemValue.MUSIC_STYLE.equals(SystemValue.MUSIC_STYLE_SINGER)) {
//							System.out.println("  5555555555555555 "+SystemValue.MUSIC_STYLE);
							playMusic(a);
						}else{
//							System.out.println(" 1  2  3  4"+SystemValue.MUSIC_STYLE);
							nextMusic();
						}
					}
				});
			} catch (IOException e) {
			}
		}
		
		/* 下一首 */
		public void nextMusic() {
			if (++currentListItme >= songs.size()) {
				currentListItme = 0;
			}
			playMusic(songs.get(currentListItme).getUrl());
		}

		/* 上一首 */
		public void frontMusic() {
			Log.v("itme", currentListItme + "hree");
			if (--currentListItme < 0) {
				currentListItme = songs.size() - 1;
			}
			playMusic(songs.get(currentListItme).getUrl());
		}
		
		/**
		 *	歌曲是否真在播放 
		 */
		public boolean isPlay() {
			return mMediaPlayer.isPlaying();
		}
		
		/**
		 *	暂停或开始播放歌曲 
		 */
		public void pausePlay() {
			if (mMediaPlayer.isPlaying()) {
				currentTime = mMediaPlayer.getCurrentPosition();
				mMediaPlayer.pause();
			} else {
				mMediaPlayer.start();
			}
		}
		


		public String getSongName(){
			return songs.get(currentListItme).getName();
		}
		
		public String getSingerName(){
			return songs.get(currentListItme).getSingerName();
		}
		
		public MediaPlayer getmMediaPlayer() {
			return mMediaPlayer;
		}

		public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
			this.mMediaPlayer = mMediaPlayer;
		}

		public int getCurrentListItme() {
			return currentListItme;
		}

		public void setCurrentListItme(int currentListItme) {
			this.currentListItme = currentListItme;
		}

		public int getDuration() {
			return mMediaPlayer.getDuration();
		}
		public void setDuration(int i){
			this.duration = duration;
		}

		public List<Mp3> getSongs() {
			return songs;
		}

		public void setSongs(List<Mp3> songs) {
			this.songs = songs;
		}
	  	 
}

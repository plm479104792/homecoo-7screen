package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTouch;

import com.alibaba.fastjson.JSONObject;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.entity.Mp3;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.ThemeData;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.MusicPlayService;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.MusicUtils;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SceneModelActivity extends BaseActivity {
	//SharedPreferences共享数据
      SharedPreferences preferences;
      SharedPreferences.Editor editor;
      
	//activity绑定service
	private  SocketService musicService ;
	
	private MusicPlayService playService;
	
	@Bind(R.id.tv_head_submit)  TextView tvExit;
	@Bind(R.id.tv_head_back) TextView tvBack;
	@Bind(R.id.tv_head_title) TextView tvTitle;
	@Bind(R.id.gv_scenelist) GridView gvScene;
	@Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
	
	private List<Theme>  themeList=new ArrayList<Theme>();
	private List<ThemeDevice>  themeDevicelist=new ArrayList<ThemeDevice>();
	private int j=0;     //正在执行的情景关联设备指针
	private SceneAdapter sceneAdpter=null;
	private int vPsotion;   //当前点击后情景全局指针
	boolean initSceneFlag=false ;   //初始化四个默认的情景
	private static List<Mp3> songs;// 歌曲集合
	
	
	//手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
	float x1 = 0;
	float x2 = 0;
	float y1 = 0;
	float y2 = 0;
	
	  /*辅线程动态刷新页面*/   
    Handler handler=new Handler(){
	   	 @Override
	   	 public void handleMessage(Message msg){
	   		 switch(msg.what){
	   		 case 0x129:
	   			sceneAdpter=new SceneAdapter(); 
	   			gvScene.setAdapter(sceneAdpter);
	   			break;
	   		 case 0x121:
	   			String themename=(String) msg.obj;
	   			showLoadingDialog(themename+"情景联动正在执行...");
	   		
		   		break;
	   		 }
	   	 }
	   };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scene_model);
		ButterKnife.bind(SceneModelActivity.this);//注解工具声明
		
		// 获取只能被本应用程序读、写的SharedPreferences对象
		 preferences = getSharedPreferences("tuwa",Context.MODE_PRIVATE);
		 editor = preferences.edit();
		
		// Activity和service绑定2
 		Intent service = new Intent(SceneModelActivity.this,SocketService.class);
 		bindService(service, devconn, Context.BIND_AUTO_CREATE);
		initViews();
		initDatas();
		playService=SystemValue.musicPlayService;
		
	}
	
	//获取SocketService实例对象
	  ServiceConnection devconn = new ServiceConnection() {  
	        @Override  
	        public void onServiceDisconnected(ComponentName name) {  
	              
	        }  
	          
	        @Override  
	        public void onServiceConnected(ComponentName name, IBinder service) {  
	            //返回一个SocketService对象  
	        	musicService = ((SocketService.SocketBinder)service).getService();  
	            
	        	musicService.callSocket(new SocketCallBack() {
	        		@Override 
	    			public void callBack(TranObject tranObject) {
	    				
	    				switch (tranObject.getTranType()) {
	    				case NETMSG:   
	    					int netstatue = (Integer) tranObject.getObject();
							if (NetValue.NONET == netstatue) { // 本地连接失败
								if(!NetValue.autoFlag){
									ToastUtils.showToast(SceneModelActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;
	    				case DEVMSG:
	    				default:
	    					break;
	    				}
	    			}
				});      	
	        }  
	    };
	    
		@Override
		protected void initViews() {
			tvExit.setText("退出");
			tvTitle.setText("情景模式");
			tvBack.setVisibility(View.VISIBLE);
			
			if (NetValue.netFlag==NetValue.INTRANET) {
				tvbttomNetwork.setText("本地");   //任务栏显示网络状态
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				tvbttomNetwork.setText("远程");    //任务栏显示网络状态
			} 
			
		}
		
		@Override
		protected void initDatas() {
			//根据网关号从数据库加载相应设备
			  List<Theme> allThemeList=new ArrayList<Theme>();
			  allThemeList=new ThemeDao(SceneModelActivity.this)
                             .themeListByGatewayNo(SystemValue.gatewayid);
			  themeList=WebPacketUtil.findCustomThemeFromThemesAll(allThemeList);
			  
			  if(themeList.size()>0){
				   //  异步进程更新界面
		 		    Message msg=new Message();
		            msg.what=0x129;
		            handler.sendMessage(msg);
			  }else{
				  ToastUtils.showToast(SceneModelActivity.this,"请在设置页面中添加情景！",1000);
			  }
			  

		}
	
	   //情景列表适配器
		public class SceneAdapter  extends BaseAdapter {
			@Override
			public int getCount() {
				return themeList.size();
			}
			@Override
			public Object getItem(int position) {
				return themeList.get(position);
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			@Override
			public View getView( final int position, View view, ViewGroup parent) {
				ViewHolder holder;
				if (view != null) {
				      holder = (ViewHolder) view.getTag();
				    } else {
				      view = View.inflate(SceneModelActivity.this,R.layout.item_scene,null);
				      holder = new ViewHolder(view);
				      view.setTag(holder);
				    }
				
				Theme theme = themeList.get(position);
				int themeType=theme.getThemeType();
				String deviceNo=theme.getDeviceNo();
				if(themeType==SystemValue.SCENE_HARD){  //硬件情景显示硬件情景开关的位置
					UserSpaceDevice userSpace = new UserSpaceDevDao(SceneModelActivity.this)
					    .findDeviceSpace(SystemValue.phonenum, deviceNo);
					if (userSpace != null) {
						String spacename = WebPacketUtil.getSpaceName(userSpace
								.getSpaceNo()); // 根据phonespaceid获取spacename
						holder.tvScenename.setText(spacename+"/"+theme.getThemeName());
					} else {
						Device devdto=new DevdtoDao(SceneModelActivity.this).findDevByDeviceNoAndGatewayNo(deviceNo, SystemValue.gatewayid);
						if(devdto==null){
							devdto=new Device();
							devdto.setSpaceNo("0");
						}
						String spacename = WebPacketUtil.getSpaceName(devdto.getSpaceNo()); // 根据phonespaceid获取spacename
						holder.tvScenename.setText(spacename+"/"+theme.getThemeName());
					}
				}else{
					holder.tvScenename.setText(theme.getThemeName());
				}
				
				holder.imSetting.setVisibility(View.INVISIBLE);
				holder.tgSceneSwitch.setVisibility(View.VISIBLE);
				
				vPsotion = preferences.getInt("scenepoint", -1);       //获取已经启动的scenepoint
				//刷新情景当前点亮图标
				if (vPsotion==position) {
					holder.tgSceneSwitch.setChecked(true);
				}else {
					holder.tgSceneSwitch.setChecked(false);
				}
				
				sceneViewOnClick(holder,position); //列表中开关按键点击事件监听
				
				return view;
			}
   
			class ViewHolder {    
				@Bind(R.id.tv_list_scenename)  TextView tvScenename;
				@Bind(R.id.im_setting)  ImageView imSetting;
				@Bind(R.id.tg_scene_switch)  ToggleButton tgSceneSwitch;
				
			    public ViewHolder(View view) {
			    	ButterKnife.bind(this,view);
			    }
			  }
			
			 /**
			  * 情景启动按钮点击事件
			  * @param holder
			  * @param position
			  */
			 private void sceneViewOnClick(ViewHolder holder, final int position) {
				   holder.tgSceneSwitch.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
						 Theme theme=themeList.get(position);
//						 System.out.println(theme.getThemeName()+"情景模式启动了");
						 List<APPThemeMusic> list=new APPThemeMusicDao(SceneModelActivity.this).GetAppThemeMusicListByThemeNo(theme.getThemeNo());
						 Log.i("2016","七寸屏本地点击情景  "+list.toString());
							//填充当前点击位置填充全局变量
						 vPsotion=position;  
						 editor.putInt("scenepoint", position);
				         editor.commit(); 
				         sceneAdpter.notifyDataSetChanged();
//				         System.out.println("启动的情景位置："+position);
//				         Log.i("343", "启动的情景位置："+position);
				 		//方案一：发送情景控制报文到手机
				   			ThemeData themeData=new ThemeData();
				    		themeData.setThemeNo(theme.getThemeNo());
				    		themeData.setThemeState(theme.getThemeState());
				    		SocketPacket socketPacket=WebPacketUtil.sceneControl2Packet(themeData);
				    		sentCmdByServerOrGateway(socketPacket);  //判断并通过内网或外网发送
				  	     
				    		//这里判断内网情况   该情景定义了 音乐  跳转音乐界面并播放音乐
								if (list.size()>0) {
									Log.i("2016","需要播放的歌曲   "+list.get(0).toString());
									SystemValue.THEME_MUSIC_THEME=1;
									SystemValue.theme_music_themeno=theme.getThemeNo();
									//TODO
//									CtrlMusic(jpushmessage);
									String jpushString=MusicUtil.MusicOrderToMusicOrder(list.get(0));
									Log.i("2016","传到musicutil "+jpushString);
									MusicUtil.CtrlMusic(jpushString,SceneModelActivity.this);
								}
						}
					});
				}
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
				sendCmdToServer(sentBytes,0); // 发送到服务器的命令串

				break;
			case NetValue.INTRANET: // 内网
			
				musicService.sentPacket(socketPacket); // 发送请求认证报文到网关
				System.out.println("========内网发送");
				break;
			}
	  
		}
		
	   //连续执行情景线程
		Handler timerhandler=new Handler();  
		Runnable timerrunnable=new Runnable() {  
		    @Override  
		    public void run() {  
		    	themeExecuteLoop();
		    	try {
					Thread.sleep(500); //每隔2秒执行一个情景状态
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }  
		};
		/******循环发送情景表中的关联设备状态*******/
		private void themeExecuteLoop() {
			
	        if(j<themeDevicelist.size()){
	        	ThemeDevice themedevice=themeDevicelist.get(j);
	        	String gatewayNo=SystemValue.gatewayid;
	        	String deviceNo=themedevice.getDeviceNo();
	            Device devdto=new DevdtoDao(SceneModelActivity.this).findDevByDeviceNoAndGatewayNo(deviceNo,gatewayNo);
	            if (devdto!=null) {
	            	 devdto.setDeviceStateCmd(themedevice.getDeviceStateCmd());
	            	 SocketPacket devPacket=WebPacketUtil.devConvertToPacket(devdto);
	            	 musicService.sentPacket(devPacket);   //发送联动设备状态
	  	             //更新本地数据库的设备状态，不等底层返回
	  	           new DevdtoDao(null).updateDevStateByDeviceNo(devdto);
				}
	            timerhandler.post(timerrunnable);
			    j++;
			    System.out.println("====当前执行情景的第========"+j);
		   }else {
				dismissLoadingDialog();
		  }
		}
		
		/**
		 * 通过内网执行情景
		 * @param theme
		 */
		private void executeThemeByInternet(final Theme theme) {
			
			//根据网关号从数据库加载相应设备
			themeDevicelist=new ThemeDeviceDao(SceneModelActivity.this).findDevstateBythemeNo(theme.getThemeNo());
            j=0;  //每当新情景，指针重置为0
			timerhandler.post(timerrunnable);
		}
		   
		   /***退出系统***/
		    @OnClick(R.id.tv_head_submit)
		    public void systemExit(){
		    	initExitDialog();
		    }
		    /***返回***/
			@OnTouch(R.id.tv_head_back)
			public boolean back() {
		    	Intent intent=new Intent(SceneModelActivity.this,HomeActivity.class);
				startActivity(intent);		
				finish();
				return false;
		    }
		    
		    /***空间***/
		    @OnCheckedChanged(R.id.rb_navi_space)
		    public void  spaceDeviceShow(){
		    	Intent intent=new Intent(SceneModelActivity.this,SpaceDevicesActivity.class);
				startActivity(intent);		
				finish();
		    }
//		    /***情景模式***/
//			@OnClick(R.id.tv_bottom_scene)
//			public void sceneMode(){
//				Intent sceneIntent=new Intent(SceneModelActivity.this,SceneModelActivity.class);
//				startActivity(sceneIntent);		
//				finish();
//			}
		    
		    
		    /***网络切换***/
			@OnClick(R.id.tv_bottom_network)
			public void networkSwitchClick(){
				
				netWorkSwitch(musicService,tvbttomNetwork);  //切换网络状态
			}
			
			/***防区管理***/
			@OnCheckedChanged(R.id.rb_navi_alert)
			public void DefenceAreaClick(){
				Intent sceneIntent=new Intent(SceneModelActivity.this,DefenceAreaActivity.class);
				startActivity(sceneIntent);		
				finish();
			}
		    /***系统设置***/
			 @OnCheckedChanged(R.id.rb_navi_set)
		    public void  systemSet(){
		    	Intent intent=new Intent(SceneModelActivity.this,SetActivity.class);
				startActivity(intent);		
				finish();
		    }
		

		@Override  
	    protected void onDestroy() {  
			unbindService(devconn);
	        super.onDestroy(); //注意先后  s
	    }  
		
//		/*****************左右滑动事件监听******************/
//		@Override
//		public boolean onTouchEvent(MotionEvent event) {
//			//继承了Activity的onTouchEvent方法，直接监听点击事件
//			if(event.getAction() == MotionEvent.ACTION_DOWN) {
//				//当手指按下的时候
//				x1 = event.getX();
//				y1 = event.getY();
//			}
//			if(event.getAction() == MotionEvent.ACTION_UP) {
//				//当手指离开的时候
//				x2 = event.getX();
//				y2 = event.getY();
//			    if(x1 - x2 > 50) { //向左滑动
//			    	System.out.println("==《《《=向左滑动====");
//			    	Intent intent=new Intent(SceneModelActivity.this,CameraKindsActivity.class);
//					startActivity(intent);		
//					finish();
//					overridePendingTransition(R.anim.out_to_left,
//							R.anim.in_from_right);
//				} else if(x2 - x1 > 50) {  //向右滑动
//					System.out.println("===向右滑动==》》》==");
//					Intent intent=new Intent(SceneModelActivity.this,MusicMainActivity.class);
//					startActivity(intent);		
//					finish();
//					overridePendingTransition(R.anim.out_to_right,
//							R.anim.in_from_left);
//				}
//			}
//			return super.onTouchEvent(event);
//		}


		private void CtrlMusic(String musicOrder) {/*
			// 获取收到的 jpush 的数据 转换为
			Jpush jpush = JSONObject.parseObject(musicOrder, Jpush.class);
			try {
				MusicOrder order = JSONObject.parseObject( jpush.getObject().toString(), MusicOrder.class);
				*//**
				 * 根据pause判断是否是暂停控制 然后根据歌曲名称进行查询播放
				 *//*
				if (order.getOrder().equals(SystemValue.MUSIC_CTRL_PAUSE)){
					if (order.getBz()!=null || !order.getBz().equals("")) {
						//情景音乐中 order.bz 有值   用于解决暂停情景音乐 点击两次会播放音乐
//						musicService.getmMediaPlayer().pause();
						playService.getmMediaPlayer().pause();
					}else{
						//单控音乐order.bz没有值
//						musicService.pausePlay();
						playService.pausePlay();
					}
				} else {
					songs = MusicUtils.getAllSongs(SceneModelActivity.this);
					for (int i = 0; i < songs.size(); i++) {
						Mp3 mp3 = songs.get(i);
						if (order.getSongName().equals(mp3.getName())) {
//							musicService.setCurrentListItme(i);
//							musicService.setDuration(songs.get(i).getDuration());
//							musicService.setSongs(songs);
							
							playService.setCurrentListItme(i);
							playService.setDuration(songs.get(i).getDuration());
							playService.setSongs(songs);
							int musicorder = MusicUtil.JudgeMusicOrder(order);
							if (musicorder == SystemValue.MUSIC_CTRL_ORDER) {
//								musicService.playMusic(mp3.getUrl());
								playService.playMusic(mp3.getUrl());
							} else if (musicorder == SystemValue.MUSIC_STYLE_ORDER) {
//								if (!musicService.isPlay()) { // 当前未在播放
//									musicService.playMusic(mp3.getUrl());
//								}
								if (!playService.isPlay()) { // 当前未在播放
									playService.playMusic(mp3.getUrl());
								}
								SystemValue.MUSIC_STYLE = order.getOrder();
							} else if (musicorder == SystemValue.MUSIC_ERROR_ORDER) {
								// 不做处理 只是报错
							}
							break;
						}
					}
				}

			} catch (Exception e) {
				System.err.println("解析异常" + e);
			}

		*/}
}



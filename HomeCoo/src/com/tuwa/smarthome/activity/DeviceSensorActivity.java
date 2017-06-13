package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Item;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeData;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.MD5Security16;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceSensorActivity extends BaseActivity {
	 //activity绑定service1
	  private  SocketService devService ;
	  @Bind(R.id.tv_head_submit)  TextView tvSubmit;
	  @Bind(R.id.tv_head_back) TextView tvBack;
	  @Bind(R.id.tv_head_title) TextView tvtitle;
	  @Bind(R.id.gv_devsensor_list) GridView gvDevices;
	  
	  final char ON=1+'0';  //字符开
	  final char OFF=0+'0';  //字符关
	  private static char [] strStaArr=new char[4];   //字符数组代表多路开关状态
	  
	 private List<Device>  devlist=new ArrayList<Device>();
	 private List<Device>  devAllList=new ArrayList<Device>();
     private DeviceAdapter deviceAdpter=null;
     
 	private List<Theme> themeSensorList = new ArrayList<Theme>();
 	private List<ThemeDevice> themeDeviceList = new ArrayList<ThemeDevice>();
   
     private int aleThemeid;  //安防情景号
 	private int j=0;     //正在同步网关的情景关联设备指针

     /*辅线程动态刷新页面*/
     Handler handler=new Handler(){
	   	 @Override
	   	 public void handleMessage(Message msg){
	   		 switch(msg.what){
	   		 case 0x129:
	   			if(devlist!=null){
	   				deviceAdpter=new DeviceAdapter();
		   		    gvDevices.setAdapter( deviceAdpter);
	   			}
	   		 }
	   	 }
	   };
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_sensor);
		
        ButterKnife.bind(DeviceSensorActivity.this);//注解工具声明
        
     // Activity和service绑定
 	   Intent service = new Intent(DeviceSensorActivity.this,SocketService.class);
 	   bindService(service, conn, Context.BIND_AUTO_CREATE);
		
		tvtitle.setText("联动管理");
		
		initViews();
		initDatas();
	}
	
	//获取SocketService实例对象
	  ServiceConnection conn = new ServiceConnection() {  
	        @Override  
	        public void onServiceDisconnected(ComponentName name) {  
	              
	        }  
	          
	        @Override  
	        public void onServiceConnected(ComponentName name, IBinder service) {  
	            //返回一个SocketService对象  
	        	devService = ((SocketService.SocketBinder)service).getService();  
	            
	        	devService.callSocket(new SocketCallBack(){
	    			@Override 
	    			public void callBack(TranObject tranObject) {
	    				
	    				switch (tranObject.getTranType()) {
	    				case NETMSG:   
	    					int netstatue=(Integer) tranObject.getObject();
	    					if ((NetValue.NONET==netstatue)) {  //本地连接失败
	    						ToastUtils.showToast(DeviceSensorActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
	    						NetValue.netFlag=NetValue.OUTERNET;  //【调试】内网失败，自动切换为外网
//	    						tvbttomNetwork.setText("远程");
	    					}
	    					break;
	    				case DEVMSG:
	    					SocketPacket socketPacket=(SocketPacket) tranObject.getObject();
	    					Short devtype=socketPacket.getDevType();
//	    				    if (devtype==NetValue.DEV_LOCAL_PHONE) {     //手机认证类型反馈 
//	    				    	if (socketPacket.getData().equals("0")) {    //验证成功返回0
//	    					    	//通过验证，跳转到主界面
//	    				            
//	    				            SocketPacket devAllPacket=WebPacketUtil.getDevAllStatePacket(SystemValue.gatewayid);
//	    							socketService.sentPacket(devAllPacket);   //发送请求所有设备状态
//	    						    NetValue.netFlag=NetValue.INTRANET;  //保存当前的网络为内网
//
//	    						 }else {
//	    							 dismissLoadingDialog();
//	    							 showCustomToast("本地验证失败，请检查网关连接！");   //用户名对应的网关id错误
//	    						}
//	    					}
	    				default:
	    					break;
	    				}
	    			}
	    		});
	        }  
	    }; 
	
	@Override
	protected void onResume() {
		System.out.println("activity=========onresume");
		themeSensorList=new ThemeDao(DeviceSensorActivity.this)
		           .findThemeSensorListByGatewayNo(SystemValue.gatewayid);

		super.onResume();
	}
	

	
	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initDatas() {
		devAllList=new DevdtoDao(DeviceSensorActivity.this)
             .switchListBygwId(SystemValue.gatewayid,SystemValue.SENSOR);
		
		if(devAllList!=null){
			devlist=WebPacketUtil.deleteFansFromDeviceSensor(devAllList);
			
			//  异步进程更新界面
			Message msg=new Message();
			msg.what=0x129;
			handler.sendMessage(msg);
		}
	
		
	}  

    public class DeviceAdapter  extends BaseAdapter {
		
		@Override
		public int getCount() {
			return devlist.size();
		}

		@Override
		public Object getItem(int position) {
			return devlist.get(position);
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
			      view = View.inflate(DeviceSensorActivity.this,R.layout.item_devsensor_set,null);
			      holder = new ViewHolder(view);
			      view.setTag(holder);
			}
			
            showViewByDevtype(holder,position);  //根据设备类型显示状态
			
			switchViewOnClick(holder,position); //列表中开关按键点击事件监听
			
			return view;
		}

		class ViewHolder {
			@Bind(R.id.tv_set_devSite)  TextView tvDevSite;
			@Bind(R.id.tv_set_devtypeName)  TextView tvDevName;
			@Bind(R.id.im_setting)  ImageView imSetting;
//			@Bind(R.id.tg_sensor_switch)  ToggleButton tgBtnSwitch;
			
			
		    public ViewHolder(View view) {
		    	ButterKnife.bind(this,view);
		    }
		  }
		
		
		private void showViewByDevtype(ViewHolder holder, int position) {
		
			 Device devdto=devlist.get(position);
			 String devstate=devdto.getDeviceStateCmd();
			 if(devstate!=""){
				 strStaArr=devstate.toCharArray();
			 }
			 
		    initDeviceNameAndSite(holder.tvDevSite,holder.tvDevName,devdto);//初始化房间名称和位置
			 
		}

		 private void switchViewOnClick(final ViewHolder holder, final int position) {
				
				holder.imSetting.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					Device device=devlist.get(position);
					
					//生成对应的安防类情景
					Theme theme = new Theme();
					theme.setThemeName(device.getDeviceName());
					System.out.println("安防设备名称"+device.getDeviceName());
					theme.setDeviceNo(device.getDeviceNo());
					theme.setThemeState("01000000");  //底层报警上报0064即为触发报警状态8个字节
					theme.setThemeType(SystemValue.SCENE_TRIGGER);   //安防类情景
					theme.setGatewayNo(SystemValue.gatewayid);

					 // 安防类情景只根据GatewayId+deviceNo生成themeNo，这样安防情景名称可以随便改
					String strTheme = theme.getGatewayNo() + theme.getDeviceNo();
					
					
					try {
						String themeNo=MD5Security16.md5_16(strTheme);
						System.out.println("====md5===="+themeNo);
						
						theme.setThemeNo(themeNo);
					    new ThemeDao(DeviceSensorActivity.this).addOrUpdateTheme(theme);   //添加theme到本地数据库
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				 SystemValue.themeSet=theme;	
				 //跳转到情景设置页面	
				 Intent sceneSetIntent=new Intent(DeviceSensorActivity.this,SceneSetActivity.class);
				 sceneSetIntent.putExtra("themeNo",theme.getThemeNo());
				 sceneSetIntent.putExtra("themename",theme.getThemeName());
				 sceneSetIntent.putExtra("themeDevNo",theme.getDeviceNo());
				 sceneSetIntent.putExtra("themetype",theme.getThemeType());
				 sceneSetIntent.putExtra("themestate",theme.getThemeState());
				 startActivity(sceneSetIntent);
				//设置切换动画，从右边进入，左边退出
			     overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 	
					}
				});
				
		}
	}
    
	
	@OnClick(R.id.tv_head_back)
	public void back(){
		finish();
	}
	
	/***提示修改情景设置信息到网关***/
	@OnClick(R.id.tv_head_submit)
	public void syncScene2Gateway() {
		showLoadingDialog("正在同步安防情景到网关！");
		j=0;  //每当新情景，指针重置为0
		timerhandler.post(timerrunnable);
	}
	
	
	//连续执行情景线程同步到网关
	Handler timerhandler=new Handler();  
	Runnable timerrunnable=new Runnable() {  
	    @Override  
	    public void run() {  
	    	sysnSceneToGatewayLoop();
	    	try {
				Thread.sleep(1000); //每隔2秒执行一个情景状态
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }  
	};
	
	/**
	 * 同步安防情景及其联动设置到网关
	 */
	private void sysnSceneToGatewayLoop() {
		
        if(j<themeSensorList.size()){
        	 Theme theme=themeSensorList.get(j);
    		 ArrayList<Item> triggerList=new ArrayList<Item>();
    		 Item  trggerItem=new Item();
    		 trggerItem.setDeviceNo(theme.getDeviceNo());      //触发器item的id设置为theme_mac
    		 System.out.println("===填充的硬件识别号deviceNo"+theme.getDeviceNo()+"themename"+theme.getThemeName());
    		 trggerItem.setDeviceStateCmd(theme.getThemeState());
    		 trggerItem.setDataLen(2);   //安防触发器的data长度
    		 
    		 Device  devTrigger=new DevdtoDao(DeviceSensorActivity.this)
    		                .findDevByDeviceNoAndGatewayNo(theme.getDeviceNo(),SystemValue.gatewayid);
    		 trggerItem.setDeviceType(devTrigger.getDeviceTypeId());  //触发器类型
    		 System.out.println("安防情景触发器类型"+devTrigger.getDeviceTypeId());
    		 triggerList.add(trggerItem);
    		
    		themeDeviceList = new ThemeDeviceDao(DeviceSensorActivity.this)
    		              .findDevstateBythemeNo(theme.getThemeNo());
    		int itemSize=themeDeviceList.size();
    		 System.out.println("一个情景的themeNo为"+theme.getThemeNo()+"联动设备数"+itemSize);  //===临时修改theme小bug====
    		 ArrayList<Item> sceneList=new ArrayList<Item>();
    		 for (int i = 0; i < themeDeviceList.size(); i++) {
    				ThemeDevice themedevice=themeDeviceList.get(i);
    				Item  deviceitem=new Item();
    				String deviceNo= themedevice.getDeviceNo();
    				Device device=new DevdtoDao(DeviceSensorActivity.this)
    				       .findDevByDeviceNoAndGatewayNo(deviceNo, SystemValue.gatewayid);
    				
    				deviceitem.setDeviceNo(themedevice.getDeviceNo());
    				deviceitem.setDeviceStateCmd(themedevice.getDeviceStateCmd());
    				int datalen=themedevice.getDeviceStateCmd().length();
    				System.out.println("被触发的设备的datalen:"+datalen);
    				deviceitem.setDataLen(datalen);
    				deviceitem.setDeviceType(device.getDeviceTypeId());
    				System.out.println("被触发的设备的类型:"+device.getDeviceTypeId());
    				sceneList.add(deviceitem);
    		}
    		
    		ThemeData themeData=new ThemeData();
    		themeData.setDeviceNo(theme.getDeviceNo());
    		themeData.setThemeNo(theme.getThemeNo());
    		themeData.setThemeState(theme.getThemeState());
    		themeData.setThemeType(theme.getThemeType());
    		System.out.println("===情景名称封装到themedata==="+theme.getThemeName());
    		themeData.setThemeName(devTrigger.getDeviceName()); //情景名称随着安防设备名称变
    		themeData.setTriggerNum(1);
    		themeData.setDeviceNum(itemSize);    //联动的设备数量
    		themeData.setTriggerList(triggerList);
    		themeData.setDeviceList(sceneList);
    		
    		SocketPacket socketPacket=WebPacketUtil.sceneSet2Packet(themeData);
    		
    		 sentCmdByServerOrGateway(socketPacket);  //判断内外网并发送
    		
    	     timerhandler.post(timerrunnable);
			 j++;
			
	   }else {
			dismissLoadingDialog();
			SocketPacket socketPacket=WebPacketUtil.finnishThemeSetPacket();
			
			sentCmdByServerOrGateway(socketPacket); //判断发送到网关还是服务器
			
	   }
		
	}
	
	
	private void sentCmdByServerOrGateway(SocketPacket socketPacket) {
		switch (NetValue.netFlag) {
		case NetValue.OUTERNET: // 外网
			//将命令封装为字符串发送到服务器
			byte[] sentBytes=WebPacketUtil.packetToByteStream(socketPacket);
			
			sendCmdToServer(sentBytes,0);   //发送到服务器的命令串

			break;
		case NetValue.INTRANET: // 内网
		
			devService.sentPacket(socketPacket); // 发送请求认证报文到网关
			System.out.println("========内网发送");
			break;
		}
  
	}
	
	@Override  
    protected void onDestroy() {  
		unbindService(conn);
        super.onDestroy(); //注意先后  
    } 
	
}

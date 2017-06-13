package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;
import cn.jpush.android.api.JPushInterface;
import com.alibaba.fastjson.JSONObject;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.GateWayDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.entity.Mp3;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeData;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.MulticastService;
import com.tuwa.smarthome.network.MusicPlayService;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.MusicUtils;
import com.tuwa.smarthome.util.PreferencesUtils;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SceneQuickActivity extends BaseActivity {  
	private  SocketService socketService ;
	@Bind(R.id.tv_head_title)  TextView tvtitle;
	@Bind(R.id.tv_head_back)   TextView tvBack;
	@Bind(R.id.rg_sceneQuick)  RadioGroup rbSceneQuick;
	@Bind(R.id.tv_scene_quick1)  RadioButton rbSceneQuick1;
	@Bind(R.id.tv_scene_quick2)  RadioButton rbSceneQuick2;
	@Bind(R.id.tv_scene_quick3)  RadioButton rbSceneQuick3;
	@Bind(R.id.tv_scene_quick4)  RadioButton rbSceneQuick4;
	@Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
	
	private List<Theme>  themeList=new ArrayList<Theme>();
	private List<Theme>  themeQuickList=new ArrayList<Theme>();  //快捷情景列表
	private Theme themeQuick1,themeQuick2,themeQuick3,themeQuick4;
	private static List<Mp3> songs;// 歌曲集合
	//手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
	float x1 = 0;
	float x2 = 0;
	float y1 = 0;
	float y2 = 0;
	
	  /*辅线程动态刷新页面*/   
    @SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
	   	 @Override
	   	 public void handleMessage(Message msg){
	   		 switch(msg.what){
	   		 case 0x108:
	   			initDatas();
	   			System.out.println("刷新界面显示！");
	   			break;
	   		 }
	   	 }
	   };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_4scenemodel);
		ButterKnife.bind(SceneQuickActivity.this);// 注解工具声明
		
		// Activity和service绑定2
		Intent service = new Intent(SceneQuickActivity.this,SocketService.class);
		bindService(service,conn, Context.BIND_AUTO_CREATE);
		
		// 20160815  每个activity只能bind一个service    	七寸屏的IP广播放到homeactivity上启动  2016-09-08
		Intent multiservice=new Intent(SceneQuickActivity.this,MulticastService.class);
		this.startService(multiservice);   //20161003
		
		Gateway mGateway = new GateWayDao(SceneQuickActivity.this).getFirstGatewayForScreem();
		if (mGateway!=null) {
			SystemValue.gatewayid=mGateway.getGatewayNo();
			SystemValue.phonenum=mGateway.getGatewayNo();
			System.out.println("情景用户名为"+SystemValue.phonenum+"情景网关号为"+SystemValue.gatewayid);
		}
		
		initViews();
		initDatas();
				
		//极光初始化
//		JPushInterface.setDebugMode(true);
//		JPushInterface.init(this);
//		JPushInterface.setLatestNotificationNumber(getApplicationContext(),3);// 保留多少条通知数 
		
		//设置别名，JPush根据别名广播给别名用户
		System.out.println("别名：==="+SystemValue.gatewayid);
		setAliasAndTags();
		
		Intent startIntent = new Intent(this, SocketService.class);
		startService(startIntent);
		
		if(VerifyUtils.isEmpty(SystemValue.gatewayid)){
			ToastUtils.showToast(SceneQuickActivity.this, "请先绑定网关！", 2000);
		}
		
		//这里单独启动一个音乐播放的service 用于jpushreceive的时候调用
		if (SystemValue.musicPlayService == null) {
			SystemValue.musicPlayService =new MusicPlayService();
			SystemValue.musicPlayService.onCreate();
		}
	}

	@Override
	protected void initViews() {
		tvtitle.setText("快捷情景模式");
		tvBack.setVisibility(View.INVISIBLE);
		
		if (NetValue.netFlag==NetValue.INTRANET) {
			tvbttomNetwork.setText("本地");   //任务栏显示网络状态
		}else if (NetValue.netFlag==NetValue.OUTERNET) {
			tvbttomNetwork.setText("远程");    //任务栏显示网络状态
		} 
	}

	@Override
	protected void initDatas() {
		initRadioBtnChecked();  //恢复以前选中状态
		
		themeQuickList= new ThemeDao(SceneQuickActivity.this)
		                    .findThemeQuickListByGatewayNo(SystemValue.gatewayid);
		if(themeQuickList!=null){
			rbSceneQuick1.setText("快捷情景(待添加)");
			rbSceneQuick2.setText("快捷情景(待添加)");
			rbSceneQuick3.setText("快捷情景(待添加)");
			rbSceneQuick4.setText("快捷情景(待添加)");
			
			themeQuick1=null;
			themeQuick2=null;
			themeQuick3=null;
			themeQuick4=null;
			
			for (int i = 0; i < themeQuickList.size(); i++) {
				Theme theme=themeQuickList.get(i);
				String themeName=getSceneQuickName(theme);
				
				if(i==0){
					rbSceneQuick1.setText(themeName);
					themeQuick1=theme;
				}else if(i==1){
					rbSceneQuick2.setText(themeName);
					themeQuick2=theme;
				}else if(i==2){
					rbSceneQuick3.setText(themeName);
					themeQuick3=theme;
				}else if(i==3){
					rbSceneQuick4.setText(themeName);
					themeQuick4=theme;
				}
			}
		}
	}

	//恢复以前选中状态
	private void initRadioBtnChecked() {
		int checkId=PreferencesUtils.getInt(SceneQuickActivity.this, "checkId");
		switch (checkId) {
		case R.id.tv_scene_quick1:
			rbSceneQuick1.setChecked(true);
			break;
		case R.id.tv_scene_quick2:
			rbSceneQuick2.setChecked(true);
			break;
		case R.id.tv_scene_quick3:
			rbSceneQuick3.setChecked(true);
			break;
		case R.id.tv_scene_quick4:
			rbSceneQuick4.setChecked(true);
			break;

		default:
			break;
		}
	}

	/**
	 * 加载硬件情景的位置
	 * @param theme
	 * @return
	 */
	private String getSceneQuickName(Theme theme) {
		String thmeName="";
		int themeType=theme.getThemeType();
		if(themeType==SystemValue.SCENE_HARD){  //硬件情景显示硬件情景开关的位置
			UserSpaceDevice userSpace = new UserSpaceDevDao(SceneQuickActivity.this)
			    .findDeviceSpace(SystemValue.phonenum, theme.getDeviceNo());
			if (userSpace != null) {
				String spacename = WebPacketUtil.getSpaceName(userSpace
						.getSpaceNo()); // 根据phonespaceid获取spacename
				thmeName=spacename+"/"+theme.getThemeName();
			} else {
				Device devdto=new DevdtoDao(SceneQuickActivity.this).findDevByDeviceNoAndGatewayNo(theme.getDeviceNo(), SystemValue.gatewayid);
				String spacename = WebPacketUtil.getSpaceName(devdto.getSpaceNo()); // 根据phonespaceid获取spacename
				thmeName=spacename+"/"+theme.getThemeName();
			}
		}else{
			thmeName=theme.getThemeName();
		}
		return thmeName;
	}

	@OnClick(R.id.btn_scenequick_add) 
    public void sceneDefaultSet(){
		sceneQuickSetDialog();
    }
	
	/**
	 * 快捷情景设置对话框
	 */
	private void sceneQuickSetDialog() {
		//根据网关号从数据库加载相应设备
		List<Theme> allThemeList=new ArrayList<Theme>();
		allThemeList=new ThemeDao(SceneQuickActivity.this)
                       .themeListByGatewayNo(SystemValue.gatewayid);
		themeList=WebPacketUtil.findCustomThemeFromThemesAll(allThemeList);
		
		System.out.println("情景列表大小"+themeList.size());
		  
		GridView gvSceneQuick=new GridView(SceneQuickActivity.this);
		gvSceneQuick.setAdapter(new SceneQuickAdapter());
		
		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("快捷情景设置").setView(gvSceneQuick);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				   //  异步进程更新界面
	 		    Message msg=new Message();
	            msg.what=0x108;
	            handler.sendMessage(msg);

			}
		});
		builder.setNegativeButton("取消", null).show();
	}
	
	  //情景列表适配器
			public class SceneQuickAdapter  extends BaseAdapter {
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
					      view = View.inflate(SceneQuickActivity.this,R.layout.item_scene,null);
					      holder = new ViewHolder(view);
					      view.setTag(holder);
					    }
					
					holder.cbSceneQuick.setVisibility(View.VISIBLE);
					holder.tvScenename.setTag(position);
					holder.cbSceneQuick.setTag(position);
					
					Theme theme = themeList.get(position);
					theme.getThemeType();
					theme.getDeviceNo();
					String sceneQuickString=theme.getThemeQuick();
					
					String sceneName=getSceneQuickName(theme);
					holder.tvScenename.setText(sceneName);
					
					holder.imSetting.setVisibility(View.INVISIBLE);
					holder.tgSceneSwitch.setVisibility(View.INVISIBLE);
					
					holder.cbSceneQuick.setChecked(false);
					
					if(!VerifyUtils.isEmpty(sceneQuickString)){
						if(sceneQuickString.equals("1")){  //快捷情景
							holder.cbSceneQuick.setChecked(true);
						}
					}
					
					sceneViewOnClick(holder,theme); //列表中开关按键点击事件监听
					
					return view;
				}
	   
				class ViewHolder {    
					@Bind(R.id.tv_list_scenename)  TextView tvScenename;
					@Bind(R.id.im_setting)  ImageView imSetting;
					@Bind(R.id.tg_scene_switch)  ToggleButton tgSceneSwitch;
					@Bind(R.id.cb_scene_quick)  CheckBox cbSceneQuick;
					
				    public ViewHolder(View view) {
				    	ButterKnife.bind(this,view);
				    }
				  }
				
				
		 private void sceneViewOnClick(final ViewHolder holder,final Theme theme) {
			 holder.cbSceneQuick.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (holder.cbSceneQuick.isChecked()) {
						if(themeQuickList!=null){
							if(themeQuickList.size()>=4){
								holder.cbSceneQuick.setChecked(false);
								ToastUtils.showToast(SceneQuickActivity.this, "快捷情景最多为4条！", 2000);
							}else {
								theme.setThemeQuick("1");
								new ThemeDao(SceneQuickActivity.this).updateThemeQuickByThemeNo(theme);
							}
						}
					}else{
						theme.setThemeQuick("0");
						new ThemeDao(SceneQuickActivity.this).updateThemeQuickByThemeNo(theme);
					}
				
				}
			});
	    }
	 }

			
			 
	//获取SocketService实例对象
	  ServiceConnection conn = new ServiceConnection() {  
	        @Override  
	        public void onServiceDisconnected(ComponentName name) {  
	              
	        }  
	          
	        @Override  
	        public void onServiceConnected(ComponentName name, IBinder service) {  
	            //返回一个SocketService对象  
	            socketService = ((SocketService.SocketBinder)service).getService();  
	            
	        	socketService.callSocket(new SocketCallBack(){
	    			@Override 
	    			public void callBack(TranObject tranObject) {
	    				
	    				switch (tranObject.getTranType()) {
	    				case NETMSG:   
	    					int netstatue = (Integer) tranObject.getObject();
							if (NetValue.NONET == netstatue) { // 本地连接失败
								if(!NetValue.autoFlag){
									ToastUtils.showToast(SceneQuickActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;
	    				case DEVMSG:
	    					SocketPacket socketPacket=(SocketPacket) tranObject.getObject();
	    					Short devtype=socketPacket.getDevType();
	    				    if (devtype==NetValue.DEV_LOCAL_PHONE) {     //手机认证类型反馈 
	    				    	if (socketPacket.getData().equals("0")) {    //验证成功返回0
	    					    	//通过验证，跳转到主界面
	    				            
	    						 }else {
	    							 dismissLoadingDialog();
	    							 showCustomToast("本地验证失败，请检查网关连接！");   //用户名对应的网关id错误
	    						}
	    					}
	    				default:
	    					break;
	    				}
	    			}
	    		});
	        }  
	    }; 
	    
		
		/**
		 * 根据网络类型从内网或者外网发送
		 * @param socketPacket
		 */
		private void sentCmdByServerOrGateway(Theme theme) {
			//方案一：发送情景控制报文到手机
   			ThemeData themeData=new ThemeData();
    		themeData.setThemeNo(theme.getThemeNo());
    		themeData.setThemeState(theme.getThemeState());
    		SocketPacket socketPacket=WebPacketUtil.sceneControl2Packet(themeData);
   		
    		//TODO 这里获取快捷点击情景后台播放音乐 情景音乐后台播放
    		List<APPThemeMusic> list= new APPThemeMusicDao(SceneQuickActivity.this).GetAppThemeMusicListByThemeNo(theme.getThemeNo());
    		if (list.size()>0) {
    			String order=list.get(0).getStyle();
				String jpushmessage=MusicUtil.GetJpushmessag(list.get(0).getSongName(),order,list.get(0).getThemeNo());
    			SystemValue.THEME_MUSIC_THEME=1;
				SystemValue.theme_music_themeno=theme.getThemeNo();
//				CtrlMusic(jpushmessage);
				String jpushString=MusicUtil.MusicOrderToMusicOrder(list.get(0));
				Log.i("inside","传到musicutil "+jpushString);
				MusicUtil.CtrlMusic(jpushString,SceneQuickActivity.this);
			}
    		
			switch (NetValue.netFlag) {
			case NetValue.OUTERNET: // 外网
				//将命令封装为字符串发送到服务器
				byte[] sentBytes=WebPacketUtil.packetToByteStream(socketPacket);
				sendCmdToServer(sentBytes,0); // 发送到服务器的命令串

				break;
			case NetValue.INTRANET: // 内网
			
				socketService.sentPacket(socketPacket); // 发送请求认证报文到网关
				System.out.println("========内网发送");
				break;
			default:
				System.out.println("无网络连接！");
				break;
			}
	  
		}
		
		/*** 快捷情景 ***/
		@OnClick(R.id.tv_scene_quick1)
		public void sceneQuick1() {
			//方案一：发送情景控制报文到手机
			rbSceneQuick2.setChecked(false);
			rbSceneQuick3.setChecked(false);
			rbSceneQuick4.setChecked(false);
			if(themeQuick1!=null){
				sentCmdByServerOrGateway(themeQuick1);
			}else {
				System.out.println("选中的themeQuick1为空");
			}
			PreferencesUtils.putInt(SceneQuickActivity.this, "checkId", R.id.tv_scene_quick1);
		}
		
		/*** 快捷情景 ***/
		@OnClick(R.id.tv_scene_quick2)
		public void sceneQuick2() {
			rbSceneQuick1.setChecked(false);
			rbSceneQuick3.setChecked(false);
			rbSceneQuick4.setChecked(false);
			if(themeQuick2!=null){
				sentCmdByServerOrGateway(themeQuick2);
			}
			PreferencesUtils.putInt(SceneQuickActivity.this, "checkId", R.id.tv_scene_quick2);
		}
		
		/*** 快捷情景 ***/
		@OnClick(R.id.tv_scene_quick3)
		public void sceneQuick3() {
			rbSceneQuick1.setChecked(false);
			rbSceneQuick2.setChecked(false);
			rbSceneQuick4.setChecked(false);
			if(themeQuick3!=null){
				sentCmdByServerOrGateway(themeQuick3);
			}
			PreferencesUtils.putInt(SceneQuickActivity.this, "checkId", R.id.tv_scene_quick3);
		}
		
		/*** 快捷情景 ***/
		@OnClick(R.id.tv_scene_quick4)
		public void sceneQuick4() {
			rbSceneQuick1.setChecked(false);
			rbSceneQuick2.setChecked(false);
			rbSceneQuick3.setChecked(false);
			if(themeQuick4!=null){
				sentCmdByServerOrGateway(themeQuick4);
			}
			PreferencesUtils.putInt(SceneQuickActivity.this, "checkId", R.id.tv_scene_quick4);
		}
		
		

		/*****************左右滑动事件监听******************/
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			//继承了Activity的onTouchEvent方法，直接监听点击事件
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				//当手指按下的时候
				x1 = event.getX();
				y1 = event.getY();
			}
			if(event.getAction() == MotionEvent.ACTION_UP) {
				//当手指离开的时候
				x2 = event.getX();
				y2 = event.getY();
			    if(x1 - x2 > 50) { //向左滑动
			    	System.out.println("==《《《=向左滑动====");
			    	Intent intent=new Intent(SceneQuickActivity.this,CameraKindsActivity.class);
					startActivity(intent);		
					finish();
					overridePendingTransition(R.anim.out_to_left,
							R.anim.in_from_right);
				} else if(x2 - x1 > 50) {  //向右滑动
					System.out.println("===向右滑动==》》》==");
					Intent intent=new Intent(SceneQuickActivity.this,MusicMainActivity.class);
					startActivity(intent);		
					finish();
					overridePendingTransition(R.anim.out_to_right,
							R.anim.in_from_left);
				}
			}
			return super.onTouchEvent(event);
		}
		
		
	    /***空间***/
	    @OnCheckedChanged(R.id.rb_navi_space)
	    public void  spaceDeviceShow(){
	    	Intent intent=new Intent(SceneQuickActivity.this,SpaceDevicesActivity.class);
			startActivity(intent);		
			finish();
	    }
	    
		 /***q情景模式***/
		 @OnClick(R.id.rb_navi_scene)
		public void sceneMode(){
			Intent sceneIntent=new Intent(SceneQuickActivity.this,SceneModelActivity.class);
			startActivity(sceneIntent);		
			finish();
		}
	    
	    
	    /***网络切换***/
		@OnClick(R.id.tv_bottom_network)
		public void networkSwitchClick(){
			
			netWorkSwitch(socketService,tvbttomNetwork);  //切换网络状态
		}
		
		/***防区管理***/
		@OnCheckedChanged(R.id.rb_navi_alert)
		public void DefenceAreaClick(){
			Intent sceneIntent=new Intent(SceneQuickActivity.this,DefenceAreaActivity.class);
			startActivity(sceneIntent);		
			finish();
		}
	    /***系统设置***/
		 @OnCheckedChanged(R.id.rb_navi_set)
	    public void  systemSet(){
	    	Intent intent=new Intent(SceneQuickActivity.this,SetActivity.class);
			startActivity(intent);		
			finish();
	    }
		 
		
		@Override
		protected void onResume() {
			super.onResume();
			JPushInterface.onResume(this);
		}
		
		@Override
		protected void onPause() {
			
			super.onPause();
			JPushInterface.onPause(this);
		}
		
		@Override
		protected void onDestroy() {
			unbindService(conn);
			super.onDestroy(); // 注意先后
		}
		
		private void CtrlMusic(String musicOrder) {
			// 获取收到的 jpush 的数据 转换为
			Jpush jpush = JSONObject.parseObject(musicOrder, Jpush.class);
			try {
				MusicOrder order = JSONObject.parseObject( jpush.getObject().toString(), MusicOrder.class);
				Log.i("quickmusic", "SceneModelActivivty  收到JPush的推送消息   :"+order.toString());
				/**
				 * 根据pause判断是否是暂停控制 然后根据歌曲名称进行查询播放
				 */
				if (order.getOrder().equals(SystemValue.MUSIC_CTRL_PAUSE)){
					if (order.getBz()!=null || !order.getBz().equals("")) {
						//情景音乐中 order.bz 有值   用于解决暂停情景音乐 点击两次会播放音乐
						socketService.getmMediaPlayer().pause();
					}else{
						//单控音乐order.bz没有值
						socketService.pausePlay();
					}
				} else {
					songs = MusicUtils.getAllSongs(SceneQuickActivity.this);
					for (int i = 0; i < songs.size(); i++) {
						Mp3 mp3 = songs.get(i);
						if (order.getSongName().equals(mp3.getName())) {
							socketService.setCurrentListItme(i);
							socketService.setDuration(songs.get(i).getDuration());
							socketService.setSongs(songs);
							int musicorder = MusicUtil.JudgeMusicOrder(order);
							if (musicorder == SystemValue.MUSIC_CTRL_ORDER) {
								socketService.playMusic(mp3.getUrl());
							} else if (musicorder == SystemValue.MUSIC_STYLE_ORDER) {
								if (!socketService.isPlay()) { // 当前未在播放
									socketService.playMusic(mp3.getUrl());
								}
								SystemValue.MUSIC_STYLE = order.getOrder();
							} else if (musicorder == SystemValue.MUSIC_ERROR_ORDER) {
								// 不做处理 只是报错
							}
//							handler.post(updateThread);
							break;
						}
					}
				}

			} catch (Exception e) {
				System.err.println("解析异常" + e);
			}

		}

}

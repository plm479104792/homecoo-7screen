package com.tuwa.smarthome.global;


public  class  NetValue {
	//============服务器网络参数=================
//	 public static final String SERVER_URL="http://192.168.0.110:8080/smarthome";
	 public static final String SERVER_URL="http://120.26.220.55:8080/smarthome";
	 public static final String SERVER_IP="120.26.220.55";
	 
	 public static final String GETCODE_URL=SERVER_URL+"/appSendRegitserCode";        //发送验证码
	 public static final String VERIFY_CODE_URL=SERVER_URL+"/appPhoneCheckCode";          //校验验证码
	 public static final String USERREGISTER_URL=SERVER_URL+"/appUserRegister";  //注册用户
	 public static final String CANCEL_USER_URL=SERVER_URL+"/appCancelUser";  //注销用户
	 public static final String GET_REPWDCODE_URL=SERVER_URL+"/appSendRePwdCode";  //重置密码验证码';
	 public static final String UPDATE_REPWDCODE_URL=SERVER_URL+"/appUpdatePassword";  //重置密码
	 public static final String LOGIN_URL=SERVER_URL+"/appLogin";  //用户登录
	 
	 
	 //===========space相关URL===========/
	 public static final String SPACE_ADD_URL=SERVER_URL+"/appAddSpace";  //添加房间
	 public static final String SPACE_DELETE_URL=SERVER_URL+"/appDeleteSpace";  //删除房间
	 public static final String SPACE_TO_SERVER_URL=SERVER_URL+"/appSetSpaceList";  //同步spacelist到服务器
	 public static final String SPACE_FROM_SERVER_URL=SERVER_URL+"/appGetALLSpace";  //从服务器获取所有spacelist
	 
	 //===========device相关URL==========/
	 public static final String DEVICE_TO_SERVER_URL=SERVER_URL+"/appSyncDeviceInfo";  //同步spacelist到服务器
	 public static final String DEVICE_FROM_SERVER_URL=SERVER_URL+"/appGetDeviceInfo";  //从服务器获取所有spacelist
	 public static final String DEVICE_DELETE_URL=SERVER_URL+"/appDeleteDevice";    //从服务器删除device
	 public static final String DEVICE_CATEGORY_URL=SERVER_URL+"/appGetDeviceListByCategory";//根据设备大类加载设备列表
	 public static final String DEVICE_ALL_URL=SERVER_URL+"/appGetAllDevice";//获取所有设备最新状态
	 public static final String DEVICE_CONTROL_URL=SERVER_URL+"/appDeviceController";//控制设备报文串
	 
	 //===========theme相关URL==========/
	 public static final String THEME_TO_SERVER_URL=SERVER_URL+"/appSyncThemeInfo";  //同步spacelist到服务器
	 public static final String THEME_FROM_SERVER_URL=SERVER_URL+"/appGetThemeDevice";  //从服务器获取所有spacelist
	 public static final String THEMEPAKET_FROM_SERVER_URL=SERVER_URL+"/appGetAllTheme";  //从服务器获取所有theme报文
	 //===========gateway相关URL==========/
	 public static final String GATEWAY_TO_SERVER_URL=SERVER_URL+"/appSyncGatewayInfo";  //同步gatewaylist到服务器
	 public static final String GATEWAY_FROM_SERVER_URL=SERVER_URL+"/appGetGatewayInfo";  //从服务器获取所有gatewaylist
	 
	 
	//===========version校验版本信息==========/
	 public static final String CHECK_VERSION_URL=SERVER_URL+"/appCompareVersion";  //对比服务器的version
	 
	 
	//===========【Timer】==========/
	 public static final String SCHEDULE_ADD_URL=SERVER_URL+"/appAddSchedule";//添加定时
	 public static final String SCHEDULE_GET_URL=SERVER_URL+"/appGetSchedule";//请求定时列表
	 public static final String SCHEDULE_DELETE_URL=SERVER_URL+"/appDeleteScheduleByScheduleId";//删除定时
	 public static final String SCHEDULE_UPDATE_URL=SERVER_URL+"/appUpdateSchedule";//更新定时
	 
	 //==============【music】===================
	 public static final String MUSIC_SEND_MUSIC_TO_SERVER=SERVER_URL+"/appSendMusicList";//音乐列表同步到服务器上(七寸屏上的接口)
	 public static final String MUSIC_GET_MUSIC_FROM_SERVER=SERVER_URL+"/appGetMusic";//获取音乐列表(手机上的接口)
	 public static final String MUSIC_CTRL_MUSIC=SERVER_URL+"/appSendMusicOrder";//控制音乐
	 public static final String MUSIC_SET_THEME_MUSIC=SERVER_URL+"/setThemeMusic";//设置情景音乐
	 public static final String MUSIC_DELETE_THEME_MUSIC=SERVER_URL+"/DeleteThemeMusic";//删除情景音乐
	 public static final String MUSIC_GET_THEME_MUSIC=SERVER_URL+"/appGetThemeMusic";//获取情景音乐
	 public static final String MUSIC_GET_ALL_THEME_MUSIC=SERVER_URL+"/appGetAllThemeMusic";//手机获取所有的情景音乐
	 public static final String MUSIC_SET_ALL_THEME_MUSIC=SERVER_URL+"/appSetThemeMusic";//手机同步所有的情景音乐到服务器
	 public static final String MUSIC_SEND_THEMEMUSIC_TO_SERVER=SERVER_URL+"/appSendThemeMusicList"; //同步所有的情景音乐到服务器
	 public static final String MUSIC_CTRL_THEMEMUSIC=SERVER_URL+"/appSendThemeMusicOrder"; //发送情景音乐设置到七寸屏
	 public static final String MUSIC_VOLUME_SET=SERVER_URL+"/appSetVolume";  //设置音量数值
	 
	 
	//============网络返回值=================
	 
	 public static final String SUCCESS_MESSAGE="success";  //服务器操作成功
	 public static final String ERROR_MESSAGE="failed";  //服务器操作失败
	
	 
	//============内网网络参数=================
	 public static String LOCAL_IP ="192.168.0.0";  //本地主机IP
	 public static String WG_IP="192.168.2.101";
	 public static String IP ="192.168.2.101";      //手机ip
     public static int LOCAL_PORT =9091;  //本地主机端口

     public static boolean callbackflag=false;   //回调flage,搜索ip屏蔽网络报异常
     public static boolean ipsearchflag=false;   //是否正在搜索网关的标志
     
 	// ============IP相关的标识===================
 	public static  boolean IP_SCAN_FLAG = false; //扫描在线ip
 	public static  boolean IP_CONNECT_FLAG = false; //联通ip
	
	//============内外网标志=================
	public static  int netFlag=2;       //网络状态
	public static final int  NONET=0;       //无网络连接
	public static final int INTRANET=1;     //内网
	public static final int OUTERNET=2;     //外网
	

	public static boolean autoFlag=false;  //断网重连标志
	public static boolean sIsConneted=false;  //socket是否连接
	public static boolean inputflag=false;  //socket输入流开关
	public static boolean socketauthen=false;  //socket是否通过认证
    public static boolean isAcceptAlert=true;   //是否接受安防设备触发
	
	//==========用户登录验证结果=============
	public static final int IN_SUCCESS =1;     //内网验证成功
	public static final int IN_FAILURE =2;     //内网验证失败
	public static final int OUT_SUCCESS =3;    //外网验证成功
	public static final int OUT_FAILURE =4;    //外网验证失败
	
	
	
	//==========socketpacket报文封装=============
	public static Short localDataState=1;              //上报设备状态
	public static Short localDataControl=2;            //下发设备控制指令
	public static Short localInstallDev=6;              //开始安装设备
	public static Short localDATA_GET_INSTALL=7;        //获取所有设备的安装信息
	public static Short DATA_INSTALL=8;          //网关返回的dataType
	public static Short localFinishInstall=11;          //结束安装设备
	
	public static Short DATA_SCENE_CTRL=13;          //控制情景
	public static Short DATA_GET_SCENE=14;          //获取设置情景
	public static Short DATA_SET_SCENE=15;          //请求设置情景
	public static Short DATA_SCENE=16;          //网关同步情景设置信息给手机
	public static Short DATA_FINISH_SCENE=17;       //完成情景设置，提示网关保存
	public static Short DATA_DELETE_SCENE=18;       //完成情景设置，提示网关保存
	public static Short ACK_FINISH_SCENE=70;       //网关完成保存情景，应答报文
	
	public static Short localDevDatareq=27;            //请求所有设备
	public static Short localAuthDatareq=28;           //内网手机认证请求

	public static Short DATA_ACK_AUTH =67;       //localuser auth ack

	public static Short Data_UPDATE_PWD=102;             //请求修改密码
	public static Short UPDATE_PWD_SUCCESS=104;             //修改密码成功
	
	public static String DEVID_NULL="3030303030303030";    //默认的devid
	public static String DEVID_INFRA="3030303030303030";    //默认的红外设备id
	
	//==========设备类型封装=============
	public static Short DEV_LOCAL_PHONE=50;             //设备类型手机认证
	public static Short DEV_FAN=51;                     //设备类型风扇
	public static Short DEV_LAMP_LIGHT=5;                //调光灯
	public static Short DEV_INFRARE=105;                  //红外遥控设备
	
	public static Short SOCKET_PULSE=3;                //datatype为心跳包
	public static int PULSE_TIME=5000;                //socket心跳包间隔
	
	public static String header="AADD";
	public static String stamp="00000000";
//	public static String gatewayId="0000000000000000";
	public static String authdata="88888888";
	
	
}

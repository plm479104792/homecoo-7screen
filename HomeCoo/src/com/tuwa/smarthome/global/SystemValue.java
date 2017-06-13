package com.tuwa.smarthome.global;

import java.util.ArrayList;
import java.util.List;

import com.tuwa.smarthome.R;
import com.tuwa.smarthome.entity.DevWidetype;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.MenuSet;
import com.tuwa.smarthome.entity.Schedule;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeInfra;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.network.DatagramSocketClient;
import com.tuwa.smarthome.network.MusicPlayService;

public class SystemValue {
	/***************** 系统每次启动的初始化变量 *********************/
	public static int userid = 1; // 用户id
	public static User user;
	public static String gatewayid = "3230313641303230"; // 网关id

	public static String SCREEN_IP="192.168.0.107";
	public static String phonenum = "00AA0088"; // 注册验证的手机号
	
	public static Theme themeSet = null; //情景设置全局变量
	public static final int TIMEOUT=1000;  //网络超时时间为1S
	public static final int MSG_TIME=1000;  //Toast显示时间为1S
	
	public static boolean jpushInitFlag=false;  //极光推送初始化标志
	
	
	public static String city = "上海"; // 定位的城市
	
	// ============Login=================	
	public static String cameraPhone="18679451786";    //传递给摄像机的手机账号
	public static String cameraPwd="123123";      //传递给摄像机的密码
	
	// ============标志位=================	
	public static boolean deviceSysnFlag=false;  //设备同步标志位
	public static boolean sceneSysnFlag=false;  //情景同步标志位	
	
	// ============网关结果显示=================	
	public static boolean NETRESULT_SHOW_FLAG=false;  //网络结果显示标志位
	
	// ============Version版本类型=================
	public static final int VERSION_APP = 1; // app版本
	public static final int VERSION_DEVICE = 2; // 设备版本
	public static final int VERSION_SPACE = 3; // 空间版本
	public static final int VERSION_SCENE = 4; // 情景版本
	public static final int VERSION_MUSIC = 5; // 情景音乐版本
	public static final int VERSION_GATEWAY = 6; // 网关版本

	// ============功能对话框操作类型=================
	public static final int add = 2; // 增加
	public static final int update = 0; // 修改
	public static final int delete = 1; // 删除

	// ============HomeActivity产品类别图标=================
	public static final int SWITCH = 1; // 照明
	public static final int SENSOR = 2; // 传感类
	public static final int WINDOW = 3; // 门窗
	public static final int SCENE=4;   //情景面板
	public static final int SOCK = 5; // 插座
	public static final int weikong2 = 7; // 微控
	public static final int anfang = 11; // 安防模块
	public static final int yaokong = 12; // 遥控
	public static final int xiaoxi = 13; // 消息
	public static final int yingyue = 15; // 音乐

	// ============设备类型定义=================
	public static final int DEV_SWITCH_ONE = 1; // 一路开关
	public static final int DEV_SWITCH_two = 2; // 二路开关
	public static final int DEV_SWITCH_three = 3; // 三路开关
	public static final int DEV_SWITCH_four = 4; // 四路开关
	public static final int DEV_DIM_LIGHT = 5; // 调光开关
	public static final int DEV_CURTAIN_ONE = 6; // 窗帘
	public static final int DEV_SOCK_ONE = 8; // 插座
	public static final int DEV_WINDOW_ONE = 11; // 窗户
	public static final int DEV_FANS = 51; // 风扇

	public static final int DEV_TEMP_HUMI = 104; // 温湿度
	public static final int DEV_INFRA_CONTROL = 105; // 红外转发控制

	public static final int DEV_PM25 = 109; // PM25
	public static final int DEV_DOOR_LOCK = 110; // 门磁
	public static final int DEV_INFRA_DETECT = 113; // 红外入侵
	public static final int DEV_SENSOR_GAS = 115; // 燃气
	public static final int DEV_SENSOR_SMOKE = 118; // 烟感

	public static final int DEV_SCENE = 202; // 情景开关
	public static final int DEV_DOUBLE_CONTRL = 204; // 双控开关

	// ============情景类型宏定义=================
	public static final int SCENE_HARD = 1; // 硬件情景
	public static final int SCENE_DOUBLE = 2; // 双控情景
	public static final int SCENE_TRIGGER = 3; // 传感器情景
	public static final int SCENE_SOFT = 4; // 自定义情景

	// ============CameraKindActivity摄像机=================
	public static final int yuntai = 100; // 云台
	public static final int qiangji = 101; // 枪机
	public static final int duijiang = 102; // 对讲
	
	
	// ============情景联动设置中遥控的操作类型操作类型=================
	public static  int InfraSetType = 0; //遥控联动操作类型
	public static final int InfraAdd = 1; // 添加遥控
	public static final int InfraUpdate = 2; // 修改遥控
	public static  ThemeInfra formerInfra = null; // 修改前遥控对象
	public static  ThemeInfra formerAddInfra = null; // 添加遥控对象
	public static int infraDevType=0;   //遥控对象的设备类型
	public static String sceneSetThemeNo; // 全局情景设置情景号
	
	public static boolean themeClean=false;

	// ============定时设置系统变量=================
		public static String timerAddType = "0"; // 添加定时标志位              1:设备 2:情景
		public static String timerUpdateType = "0"; //更新定时标志位        1:设备 2:情景
		public static boolean timerSetFlag = false; // 1:设备 2:情景
		public static String TIMER_DEVICE ="1"; // 1:设备
		public static String TIMER_SCENE = "2"; // 2:情景
		public static String TIMER_MUSIC = "3"; // 3:音乐
		public static String TIMER_INFRA = "4"; // 4:红外
		
		public static Device sdevice;
		public static Theme stheme;
		public static String smusicName; // 定时音乐名称
		public static String sInfraName; // 定时红外名称
		public static String sInfraData; // 定时红外码
		public static Device sInfraDevice=null;  //硬件红外转发器
		public static Schedule schedule;
		public static int sAddrfreshType=2;   //区分红外大类标志位   1:红外   2:其它


	// ============HomeActivity产品类别列表初始化=================
	public static List<DevWidetype> getDevWideList() {
		List<DevWidetype> list = new ArrayList<DevWidetype>();
		DevWidetype devwide1 = new DevWidetype(R.drawable.ha_anfang, "监控", 11);
		DevWidetype devwide2 = new DevWidetype(R.drawable.ha_yaokong, "遥控", 12);
		DevWidetype devwide3 = new DevWidetype(R.drawable.ha_zhaoming, "照明", 1);
		DevWidetype devwide4 = new DevWidetype(R.drawable.ha_xiaoxi, "消息", 13);
		DevWidetype devwide5 = new DevWidetype(R.drawable.ha_menchuan, "门窗", 3);
		DevWidetype devwide6 = new DevWidetype(R.drawable.ha_chazuo, "插座", 5);
		DevWidetype devwide7 = new DevWidetype(R.drawable.ha_weikong, "微控", 2);
		DevWidetype devwide8 = new DevWidetype(R.drawable.ha_yingyue, "音乐", 15);

		list.add(devwide1);
		list.add(devwide2);
		list.add(devwide3);
		list.add(devwide4);
		list.add(devwide5);
		list.add(devwide6);
		list.add(devwide7);
		list.add(devwide8);
		return list;
	}

	// ============填充系统设置的菜单栏=================
	public static List<MenuSet> getMenuSetList() {
		List<MenuSet> list = new ArrayList<MenuSet>();
		MenuSet updateMenu = new MenuSet(R.drawable.update, "版本信息");
		MenuSet mLogoffUser= new MenuSet(R.drawable.deleteuser,"注销账号");
		list.add(updateMenu);
		list.add(mLogoffUser);
		return list;
	}

	/**
	 * 封装版本时间戳
	 * 
	 * @param versionType
	 * @return
	 */
	public static Version getVersion(int versionType) {
		Version version = new Version();
		version.setPhonenum(SystemValue.phonenum);
		version.setGatewayNo(SystemValue.gatewayid);
		version.setVersionType(versionType);
		long date = System.currentTimeMillis();// 获取当前时间
		System.out.println("===时间戳==="+date);
		version.setUpdateTime(date);
		return version;
	}

	/**
	 * 封装初始化版本
	 * 
	 * @param versionType
	 * @return
	 */
	public static Version getinitVersion(int versionType) {
		Version version = new Version();
		version.setPhonenum(SystemValue.phonenum);
		version.setGatewayNo(SystemValue.gatewayid);
		version.setVersionType(versionType);
		version.setUpdateTime(1);  //无版本信息，初始化为最小的时间
		return version;
	}

	public static List<Schedule> getTimetaskList() {
		List<Schedule> list = new ArrayList<Schedule>();
		// Schedule task1 = new Schedule(1,"1010000","", "09:18", "0",
		// "","18679451786","bcbc3706004b1200","","3030414130304444","100","客厅三路灯","","","");
		// Schedule task2 = new Schedule(2,"","2016年5月10日", "16:28", "1",
		// "","18679451786","bcbc3706004b1200","","3030414130304444","101","客厅三路灯","","","");
		// list.add(task1);
		// list.add(task2);
		return list;
	}

	// ============CameraKindActivity摄像机大类=================
	public static List<DevWidetype> getCameraKindList() {
		List<DevWidetype> list = new ArrayList<DevWidetype>();
		DevWidetype camerakind1 = new DevWidetype(R.drawable.ha_anfang, "摄像机",
				100);
		// DevWidetype camerakind2=new DevWidetype(R.drawable.ha_yaokong,
		// "室外枪机",101);
		DevWidetype camerakind3 = new DevWidetype(R.drawable.ha_zhaoming,
				"可视对讲", 102);

		list.add(camerakind1);
		// list.add(camerakind2);
		list.add(camerakind3);
		return list;
	}

	public static List<String> strips = new ArrayList<String>();

	/**
	 * 将所有可以ping通的ip地址添加到列表中
	 * 
	 * @param ip
	 * @return
	 */
	public static List<String> setIps(String ip) {
		boolean isflag = false;
		if (!ip.equals("")) {
			for (int i = 0; i < strips.size(); i++) {
				String IP = strips.get(i);
				if (ip.equals(IP)) {
					isflag = true;
					break;
				}
			}
			if (!isflag) {
				strips.add(ip);
			}
		}
		System.out.println("ping通的ip数量" + strips.size());
		return strips;
	}

	public static List<String> getIps() {
		return strips;
	}

	// ==========JPush实体类 messageType 定义==========
	public static final int DEVICE_STATE_UPDATE_JPUSH = 1; // 设备状态更新
	public static final int SECURITY_ALERT_JPUSH = 2; // 安防报警
	public static final int MUSIC_JPUSH = 3; // 音乐
	public static final int THEME_JPUSH = 4; // 情景
	public static final int THEME_MUSIC_JPUSH=5; //情景音乐
	

	/**
	 * 音乐控制模式
	 * */
	public static final String MUSIC_GET_MUSICLIST_INSERT="0"; //内网获取歌曲列表
	public static final String MUSIC_CTRL_PAUSE = "1"; // 暂停
	public static final String MUSIC_CTRL_PLAY = "2"; // 播放
	public static final String MUSIC_CTRL_LAST_SONG = "3"; // 上一首歌曲
	public static final String MUSIC_CTRL_NEXT_SONG = "4"; // 下一首歌曲
	public static final String MUSIC_STYLE_SINGER = "5"; // 单曲
	public static String MUSIC_STYLE = "6"; // 默认列表
	public static final String MUSIC_STYLE_LIST = "6"; // 列表
	public static final String MUSIC_STYLE_RANDOM = "7"; // 随机
	public static final String MUSIC_DELETE_INSERT="9";  //内网删除情景音乐
	public static final String MUSIC_UPDATE_INSERT="10"; //内网更新情景音乐
	public static final String MUSIC_PLAY_INSERT="11"; //内网播放情景音乐
	public static final String MUSIC_PAUSE_INSERT="12"; //内网暂停情景音乐
	public static final String MUSIC_VOLUME="15"; //音量获取(内网)
	public static final String MUSIC_VOLUME_CTRL="16"; //音量控制(内网)
	public static final String MUSIC_THEME_GET="17"; //内网APP获取情景音乐 联动音乐设置
	public static final String MUSIC_THEME_SEND="18"; //内网七寸屏发送情景音乐  联动音乐
	
	/**
	 * 音乐控制还是 风格 区分
	 * */
	public static final int MUSIC_CTRL_ORDER=0;		//控制类型
	public static final int MUSIC_STYLE_ORDER=1;	//风格类型
	public static final int MUSIC_ERROR_ORDER=3;	//错误类型
	public static String theme_music_themeno="";	//七寸屏 自己发送的情景音乐
	public static int THEME_MUSIC_THEME=0;			//表示七寸屏刚才自己点击了情景			0：初始值  1：七寸屏点击了情景
	
	public static String IP="";
	public static MusicPlayService musicPlayService=null;     //用于jpushreceive接收端调用   2016-09-27


	public static final String MESSAGE_MUSIC_ACTION = "com.tuwa.smarthome.permission.MISIC_JPUSH";
	
	/**
	 * @Description:根据多点广播  获取七寸屏上的局域网IP，赋予全局变量，用于内网music的socket连接
	 * 不直接用多点广播连接的原因是    多点广播不够稳定
	 * */
	public static String MUSIC_SCREEN_IP="";
	//当前音量数值
	public static String MUSIC_VOLUME_LAST="";
	
	/**
	 * Datagramsocket 实体类
	 * */
	public static DatagramSocketClient datagramClient=null;
	

}

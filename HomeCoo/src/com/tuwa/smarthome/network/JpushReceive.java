package com.tuwa.smarthome.network;

import java.util.Iterator;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tuwa.smarthome.activity.DeviceWeiKongActivity;
import com.tuwa.smarthome.activity.MusicMainActivity;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.entity.Mp3;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.MusicUtils;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;


public class JpushReceive extends BroadcastReceiver {

	private static List<Mp3> songs;// 歌曲集合
	private SocketService musicService ;
	private MediaPlayer  mMediaPlayer;  
	private MusicPlayService musicPlayService;
	@SuppressWarnings("unused")
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle=intent.getExtras();
		
		// 消息标题，对应 API 消息内容的 title 字段，Portal 推送消息界上不作展示
		if (bundle.containsKey(JPushInterface.EXTRA_TITLE)) {
			String titleString = bundle.getString(JPushInterface.EXTRA_TITLE);
		}
		// 附加字段,是个 JSON 字符串,对应 API 消息内容的 extras 字段.
		// 对应 Portal推送消息界面上的“可选设置”里的附加字段
		if (bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
			String extrasString = bundle.getString(JPushInterface.EXTRA_EXTRA);
		}
		// 内容类型，对应 API 消息内容的 content_type 字段
		if (bundle.containsKey(JPushInterface.EXTRA_CONTENT_TYPE)) {
			String typeString = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
		}
		// 唯一标识消息的 ID, 可用于上报统计等。
		if (bundle.containsKey(JPushInterface.EXTRA_MSG_ID)) {
			String msgIdString = bundle.getString(JPushInterface.EXTRA_MSG_ID);
		}
		/**
		 * 对action行为的判断
		 */
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			// SDK 向 JPush Server 注册所得到的注册 全局唯一的 ID ，可以通过此 ID 向对应的客户端发送消息和通知。
			String registrationIdString = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			
			String jpushMessage=bundle.getString(JPushInterface.EXTRA_MESSAGE);
			
			// 等师兄回来了  之后要去掉的
//			if(NetValue.netFlag==NetValue.OUTERNET){
			
				//发送广播，在socketService中接收
				 Intent jpushIntent = new Intent();  //Itent就是我们要发送的内容
				 jpushIntent.putExtra("jpushMessage",jpushMessage );  
				 jpushIntent.setAction("ACTION_JPUSH_MESSAGE");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
				 Jpush jpush=JSON.parseObject(jpushMessage,Jpush.class); 
				 
				Log.i("JPUSH","外网极光推送自定义"+jpushMessage);
				
				 if (jpush.getMesssageType() == SystemValue.MUSIC_JPUSH) {
					 MusicOrder order = JSON.parseObject( jpush.getObject().toString(), MusicOrder.class);
					 boolean b=MusicUtil.ThemeMusicJpush(order);
					if ( b ==true){
							if (SystemValue.THEME_MUSIC_THEME==1 && order.getBz().equals(SystemValue.theme_music_themeno)) {
								 //七寸屏 情景音乐控制不会跳转到音乐UI
								SystemValue.theme_music_themeno="";
								SystemValue.THEME_MUSIC_THEME=0;
							}else {
//								CtrlMusic(jpushMessage, context);
								MusicUtil.CtrlMusic(jpushMessage, context);
							}
					}else{
							Intent musicmsg = new Intent(
									MusicMainActivity.MESSAGE_RECEIVED_ACTION);
							musicmsg.putExtra("message", jpushMessage);
							Log.i("JPUSH",jpushMessage);
							/**
							 * 2016-6-03 新加
							 * */
							ActivityManager manager = (ActivityManager) context
									.getSystemService(Context.ACTIVITY_SERVICE);
							RunningTaskInfo info = manager.getRunningTasks(1).get(0);
							String shortClassName = info.topActivity
									.getShortClassName(); // 类名
							if (!shortClassName.equals(".activity.MusicMainActivity")) {
								Intent a = new Intent(context, MusicMainActivity.class);
								a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								a.putExtra("MusicJpushNew", true);  //新建MusicMainActivity，并播放
								a.putExtra("jpushMsgMusic", jpushMessage);
								context.startActivity(a);
//							context.sendBroadcast(musicmsg);      2016-06-18
							}
							context.sendBroadcast(musicmsg);  //极光接收，直接调用musicService播放
						}
//					}
			}else if (jpush.getMesssageType() == SystemValue.THEME_MUSIC_JPUSH) {
						//七寸屏这里需要处理
				try {
				
						List<APPThemeMusic> list=JSONArray.parseArray(jpush.getObject().toString(), APPThemeMusic.class);
						Iterator<APPThemeMusic> iterator=list.iterator();
						while (iterator.hasNext()) {
							APPThemeMusic appThemeMusic=iterator.next();
							int thememusicOrder=Integer.valueOf(appThemeMusic.getStyle());
							switch (thememusicOrder) {
							case 9:
								//情景联动音乐：删除
								new APPThemeMusicDao(context).DeleteAppThemeMusic(appThemeMusic.getThemeNo());
								break;
							case 1:
								//情景联动音乐设置：暂停
								new APPThemeMusicDao(context).UpdateOrAddThemeMusicByThemeNo(appThemeMusic);
								break;
							case 6:
								//情景联动音乐设置：播放
								new APPThemeMusicDao(context).UpdateOrAddThemeMusicByThemeNo(appThemeMusic);
								break;
							default:
								break;
							}
						}
						
				} catch (Exception e) {
					// TODO: handle exception
				}
						Log.i("tongbu",SystemValue.gatewayid);
						List<APPThemeMusic> listmusic=new APPThemeMusicDao(context).GetAppThemeMusicListByGatewayNo(SystemValue.gatewayid);
						Log.i("tongbu", listmusic.toString());
						
					}else {
						context.sendBroadcast(jpushIntent);   //发送广播
					}
//			}
			
			
			// 消息内容,对应 API 消息内容的 message 字段,对应 Portal 推送消息界面上的"自定义消息内容”字段
			if (bundle.containsKey(JPushInterface.EXTRA_MESSAGE)) {
				String messageString = bundle
						.getString(JPushInterface.EXTRA_MESSAGE);
			}
		} 
		else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
		//	System.out.println("收到了通知");
			// 在这里可以做些统计，或者做些其他工作

			// 通知的标题,对应 API 通知内容的 n_title 字段,对应 Portal 推送通知界面上的“通知标题”字段
			String notificationTileString = bundle
					.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			// 通知内容,对应 API 通知内容的 n_content 字段,对应 Portal 推送通知界面上的“通知内容”字段
			String alertString = bundle.getString(JPushInterface.EXTRA_ALERT);
			// 通知栏的Notification ID，可以用于清除Notification
			
			int notificationId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			System.out.println(notificationId+"接收到极光推送通知：标题："+notificationTileString+"内容"+alertString);
		} 
		else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			// 在这里可以自己写代码去定义用户点击后的行为
			JPushInterface.reportNotificationOpened(context,
					bundle.getString(JPushInterface.EXTRA_MSG_ID));// 用于上报用户的通知栏被打开
			
			System.out.println("===用户点击通知栏===");
			Intent a=new Intent(context,DeviceWeiKongActivity.class);
			a.putExtra("operator_type", "notification");
			a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(a);
			

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {// 网络断开，连接
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			// Toast.makeText(arg0, "网络连接" + connected,
			// Toast.LENGTH_SHORT).show();
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			// 接受富推送

			// 富文本页面 Javascript 回调API，获取参数参数 ”params“
			if (bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
				String params = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
			}

			// 富媒体通消息推送下载后的文件路径和文件名。
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_FILE_PATH)) {
				String filePathString = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
			}

			// 富媒体通知推送下载的HTML的文件路径,用于展现WebView。
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_HTML_PATH)) {
				String fileHtmlPath = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
			}

			// 富媒体通知推送下载的图片资源的文件名,多个文件名用 “，” 分开,路径为 fileHtmlPath
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_HTML_RES)) {
				String fileImageStr = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_HTML_RES);
				String[] fileNames = fileImageStr.split(",");
			}
		} else {
			Log.d("其它的action行为", "Unhandled intent - " + intent.getAction());
		}
	
		
	}
	
		/*// 打印所有的 intent extra 数据
		@SuppressWarnings("unused")
		private static String printBundle(Bundle bundle) {
			StringBuilder sb = new StringBuilder();
			for (String key : bundle.keySet()) {
				if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
					sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
				} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
					sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
				} else {
					sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
				}
			}
			return sb.toString();
		}*/
		
	/**
	 * 后台播放情景联动音乐     2016-10-05 封装到MusicUtil里面了
	 * */	
	private void CtrlMusic(String musicOrder,Context context) {
		musicPlayService=SystemValue.musicPlayService;
		if (musicPlayService==null) {
			SystemValue.musicPlayService =new MusicPlayService();
		    SystemValue.musicPlayService.onCreate();
		    musicPlayService=SystemValue.musicPlayService;
		}
		
			// 获取收到的 jpush 的数据 转换为
			Jpush jpush = JSON.parseObject(musicOrder, Jpush.class);
			try {
				MusicOrder order1 = JSON.parseObject( jpush.getObject().toString(), MusicOrder.class);
				MusicOrder order=MusicUtil.ThemeMusic(order1);
				/**
				 * 根据pause判断是否是暂停控制 然后根据歌曲名称进行查询播放
				 */
				if (order.getOrder().equals(SystemValue.MUSIC_CTRL_PAUSE)) {
					if (order.getBz()!=null && !order.getBz().equals("")) {
						//情景音乐中 order.bz 有值   用于解决暂停情景音乐 点击两次会播放音乐
						musicPlayService.getmMediaPlayer().pause();
					}else{
						//单控音乐order.bz没有值
						musicPlayService.pausePlay();
					}
				}
				else {
					songs = MusicUtils.getAllSongs(context);
					for (int i = 0; i < songs.size(); i++) {
						Mp3 mp3 = songs.get(i);
						if (order.getSongName().equals(mp3.getName())) {
							musicPlayService.setCurrentListItme(i);
							musicPlayService.setDuration(songs.get(i).getDuration());
							musicPlayService.setSongs(songs);
							int musicorder = MusicUtil.JudgeMusicOrder(order);
							if (musicorder == SystemValue.MUSIC_CTRL_ORDER) {
								musicPlayService.playMusic(mp3.getUrl());
							} else if (musicorder == SystemValue.MUSIC_STYLE_ORDER) {
								if (!musicPlayService.isPlay()) { // 当前未在播放
									musicPlayService.playMusic(mp3.getUrl());
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

		}
	
}
	

		


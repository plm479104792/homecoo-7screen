package com.tuwa.smarthome.util;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.ThemeMusic;
import com.tuwa.smarthome.entity.Volume;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;

/**
 * 音乐Jpush工具类
 * */
public class MusicJpush {
	private static ResultMessage msg=new ResultMessage();
	
	/**
	 * 推送自定义消息  
	 * */
	public static void JpushMusicOrder(MusicOrder musicOrder){
		JPushimpl jPushimpl=new JPushimpl();
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(SystemValue.gatewayid);
		jpush.setMesssageType(SystemValue.MUSIC_JPUSH);
		jpush.setObject(JSONObject.toJSONString(musicOrder));
		jPushimpl.sendPush(jpush);
	}
	
	/**
	 * musicOrder 转 jpush
	 * */
	public static Jpush ToJpush(MusicOrder order){
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(SystemValue.gatewayid);
		jpush.setMesssageType(SystemValue.MUSIC_JPUSH);
		jpush.setObject(JSONObject.toJSONString(order));
		return jpush;
	}
	
	/**
	 * 调用服务器 接口 让服务器JPush推送到七寸屏
	 * */
	public static ResultMessage SendServer(MusicOrder order){
		Jpush jpush=MusicJpush.ToJpush(order);
		RequestParams params=new RequestParams();
		params.addBodyParameter("jpushJson",JSONObject.toJSONString(jpush));
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST,NetValue.MUSIC_CTRL_MUSIC, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				msg.setMessageInfo("网络错误，请检查网络");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				msg=JSONObject.parseObject(arg0.result,ResultMessage.class);
				
			}
		});
		
		System.out.println(msg.toString());
		return msg;
		
	}
	
	/**
	 * @Description:设置情景音乐
	 * @param:ThemeMusic 情景音乐实体类
	 * @param:type 接口类型
	 * @return:null
	 * */
	public static void SendThemeMusicToServer(ThemeMusic themeMusic,String type){
		RequestParams params=new RequestParams();
		params.addBodyParameter("thememusic", JSONObject.toJSONString(themeMusic));
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, type, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				//不做处理
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				msg=JSONObject.parseObject(arg0.result,ResultMessage.class);
			}
		});
	
		
	}
	
	/**
	 * @Description:把情景音乐同步到server上
	 * @Date 2016-06-24
	 * @param List<APPthememusic>  
	 * @return null
	 * */
	public static void SendThemeMusicListToserver(List<APPThemeMusic> list){
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("appthemeMusicJson",
				JSONArray.toJSONString(list));
		HttpUtils httpUtils = new HttpUtils();
		
		httpUtils.send(HttpMethod.POST,
				NetValue.MUSIC_SEND_THEMEMUSIC_TO_SERVER, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						// 访问失败  不做处理
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						//
					}
				});

	}
	
	/**
	 * @Description:外网下，点击设置情景音乐  通过JPush推送 情景音乐设置到七寸屏  
	 * 把appthememusic放到jpush.object上  
	 * */
	public static void SendThemeMusicToJpush(APPThemeMusic appThemeMusic){
		
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(SystemValue.gatewayid);
		jpush.setMesssageType(SystemValue.THEME_MUSIC_JPUSH);
		jpush.setObject(JSONObject.toJSONString(appThemeMusic));
		jpush.setTime(0L);
		
		RequestParams params=new RequestParams();
		params.addBodyParameter("jpushJson", JSONObject.toJSONString(jpush));
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_CTRL_THEMEMUSIC, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				ResultMessage message=JSONObject.parseObject(arg0.result, ResultMessage.class);
				if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
				}
			}
		});
		
	}
	
	/**
	 * 七寸屏将音量数值同步到服务器上
	 * */
	public static void SendVolumeToServer(Volume volume){
		RequestParams params=new RequestParams();
		params.addBodyParameter("volumeJson",JSONObject.toJSONString(volume) );
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_VOLUME_SET, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				//不做处理
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//不做处理
			}
		});
		
	}
	
	
	/**
	 * 外网下，点击设置情景音乐  通过JPush推送 情景音乐设置到七寸屏  
	 * 同步情景联动音乐   手机app将内网设置的情景联动音乐通过Jpush退送到七寸屏，七寸屏保存。
	 * 把appthememusic放到jpush.object上  
	 * */
	public static void SendThemeMusicToJpush(List<APPThemeMusic> list){
		
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(SystemValue.gatewayid);
		jpush.setMesssageType(SystemValue.THEME_MUSIC_JPUSH);
		jpush.setObject(JSONArray.toJSONString(list));
		jpush.setTime(0L);
		
		RequestParams params=new RequestParams();
		params.addBodyParameter("jpushJson", JSONObject.toJSONString(jpush));
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_CTRL_THEMEMUSIC, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				ResultMessage message=JSONObject.parseObject(arg0.result, ResultMessage.class);
				if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
				}
			}
		});
		
	}
	
	
	
}

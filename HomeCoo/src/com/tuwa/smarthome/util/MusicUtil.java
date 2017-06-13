package com.tuwa.smarthome.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.util.Log;

import cn.jpush.android.data.j;
import cn.jpush.im.proto.ap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.entity.APPMusic;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.AppThemeMusicSocket;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.entity.Mp3;
import com.tuwa.smarthome.entity.Music;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.MusicSocket;
import com.tuwa.smarthome.entity.MusicSocketByte;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeMusic;
import com.tuwa.smarthome.entity.Volume;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.DatagramSocketClient;
import com.tuwa.smarthome.network.MusicPlayService;


/**
 * 自定义的音乐 工具类
 * */
public class MusicUtil {

	
	private static MusicPlayService musicPlayService;
	private static List<Mp3> song;// 歌曲集合
	
	/**
	 * @Description:musci 转 musicOrder
	 * @param:songName 歌曲名称
	 * @param:type 控制类型
	 * @param:style 循环模式
	 * @return:MusicOrder
	 * */
	public static MusicOrder ToMusicOrder(String songName, String type,
			String style) {
		MusicOrder musicOrder = new MusicOrder();
		musicOrder.setOrder(type);
		musicOrder.setSongName(songName);
		musicOrder.setWgid(SystemValue.gatewayid);
		musicOrder.setStyle(style);
		musicOrder.setBz("");
		return musicOrder;

	}
	
	
	/**
	 * @Description:appthememusic 转 thememusic 只用于情景音乐 为 暂停播放音乐  情况
	 * */
	public static ThemeMusic PauseThememusic(Theme theme,ThemeMusic thememusic){
	
		ThemeMusic themeMusic=new ThemeMusic();
		themeMusic.setBz("");
		themeMusic.setDeviceNo(theme.getDeviceNo());
		themeMusic.setDeviceState(theme.getThemeState());
		if (theme.getThemeState().length()<5) {
			String themestate=theme.getThemeState()+"0000";
			themeMusic.setDeviceState(themestate);
		}
		themeMusic.setGatewayNo(SystemValue.gatewayid);
//		themeMusic.setSongName("00");
		themeMusic.setSongName(thememusic.getSongName());
		themeMusic.setSpace("");
		themeMusic.setStyle(SystemValue.MUSIC_CTRL_PAUSE);
		themeMusic.setThemeName(theme.getThemeName());
		themeMusic.setThemeNo(theme.getThemeNo());
		
		return themeMusic;
		
	}
	
	/**
	 * @Description:设置情景音乐为暂停音乐
	 * */
	public static APPThemeMusic PauseAppthemeMusic(Theme theme,ThemeMusic themeMusic){
		APPThemeMusic appThemeMusic=new APPThemeMusic();
		appThemeMusic.setBz("");
		appThemeMusic.setDeviceNo(theme.getDeviceNo());
		appThemeMusic.setDeviceState(theme.getThemeState());
		if (theme.getThemeState().length()<5) {
			String themestate=theme.getThemeState()+"0000";
			appThemeMusic.setDeviceState(themestate);
		}
		appThemeMusic.setGatewayNo(SystemValue.gatewayid);
		appThemeMusic.setSongName(themeMusic.getSongName());
		appThemeMusic.setSpace("");
		appThemeMusic.setStyle(SystemValue.MUSIC_CTRL_PAUSE);
		appThemeMusic.setThemeName(theme.getThemeName());
		appThemeMusic.setThemeNo(theme.getThemeNo());
		
		return appThemeMusic;
		
	}

	
	
	

	/**
	 * @Description:MusicList 转 MusicThemeList
	 * @param:Musiclist
	 * @param:ThemeMusiclist
	 * @return:themeMusicList
	 * */
	public static List<ThemeMusic> ToThemeMusicList(List<Music> list) {

		Iterator<Music> iterator = list.iterator();
		List<ThemeMusic> themeMusicList = new ArrayList<ThemeMusic>();
		while (iterator.hasNext()) {
			Music music = iterator.next();
			ThemeMusic themeMusic = new ThemeMusic();
			themeMusic.setBz(music.getBz());
			themeMusic.setSpace(music.getSpace());
			themeMusic.setSongName(music.getSongName());
			// 默认情景模式下 音乐是列表循环 之后的再搞
			themeMusic.setStyle(SystemValue.MUSIC_STYLE_LIST);
			themeMusic.setThemeNo(null);
			themeMusic.setGatewayNo(SystemValue.gatewayid);
			themeMusicList.add(themeMusic);
		}
		return themeMusicList;

	}

	/**
	 * @Description:List<Music> 转 List<APPMusic>
	 * */
	public static List<APPMusic> GetAppMusicList(List<Music> musics) {
		Iterator<Music> iterator = musics.iterator();
		List<APPMusic> list = new ArrayList<APPMusic>();
		while (iterator.hasNext()) {
			APPMusic appMusic = new APPMusic();
			Music music = (Music) iterator.next();
			appMusic.setGatewayNo(music.getGatewayNo());
			appMusic.setSongName(music.getSongName());
			list.add(appMusic);
		}

		return list;
	}

	/**
	 * @Description:List<APPMusic> 转 list<ThemeMusic>
	 * */
	public static List<ThemeMusic> TothememusicList(List<APPMusic> appMusics) {
		List<ThemeMusic> list = new ArrayList<ThemeMusic>();
		Iterator<APPMusic> iterator = appMusics.iterator();
		while (iterator.hasNext()) {
			APPMusic appMusic = (APPMusic) iterator.next();
			ThemeMusic themeMusic = new ThemeMusic();
			themeMusic.setGatewayNo(SystemValue.gatewayid);
			themeMusic.setSongName(appMusic.getSongName());
			themeMusic.setStyle(SystemValue.MUSIC_STYLE_LIST);
			list.add(themeMusic);

		}

		return list;

	}

	/**
	 * @Description:thememusic theme 得到 appthememusic
	 * */
	public static APPThemeMusic GetAppThemeMusic(ThemeMusic themeMusic,
			Theme theme) {
		APPThemeMusic appThemeMusic = new APPThemeMusic();
		appThemeMusic.setBz("");
		appThemeMusic.setDeviceNo(theme.getDeviceNo());
		appThemeMusic.setDeviceState(theme.getThemeState());
		if (theme.getThemeState().length()<5) {
			String themestate=theme.getThemeState()+"0000";
			appThemeMusic.setDeviceState(themestate);
		}
		appThemeMusic.setGatewayNo(themeMusic.getGatewayNo());
		appThemeMusic.setSongName(themeMusic.getSongName());
		appThemeMusic.setSpace("");
//		appThemeMusic.setStyle(themeMusic.getStyle());
		appThemeMusic.setStyle(SystemValue.MUSIC_STYLE_SINGER);
		appThemeMusic.setThemeName(theme.getThemeName());
		appThemeMusic.setThemeNo(theme.getThemeNo());
		return appThemeMusic;

	}

	/**
	 * @Description:情景设置音乐
	 * */
	public static MusicOrder SetthememusicOnInside(APPThemeMusic appThemeMusic) {

		MusicOrder order = new MusicOrder();
		AppThemeMusicSocket musicSocket = MusicUtil
				.Getappthememusicsocket(appThemeMusic);
		String bz = JSONObject.toJSONString(musicSocket);
		order.setBz(bz);
		order.setOrder("10");
		order.setSongName(appThemeMusic.getSongName());
		order.setStyle(appThemeMusic.getStyle());
		order.setWgid(appThemeMusic.getGatewayNo());
		return order;

	}

	/**
	 * @Description:每次一连接到socket就发送一条命令 获取歌曲列表
	 * */
	public static MusicOrder GetMusicList() {
		MusicOrder musicOrder = new MusicOrder();
		musicOrder.setBz("");
		musicOrder.setOrder("0");
		musicOrder.setSongName("");
		musicOrder.setStyle("");
		musicOrder.setWgid(SystemValue.gatewayid);
		return musicOrder;

	}

	/**
	 * @Description:appthememusic 转 appthememusicsocket
	 * */
	public static AppThemeMusicSocket Getappthememusicsocket(
			APPThemeMusic appThemeMusic) {
		AppThemeMusicSocket musicSocket = new AppThemeMusicSocket();
		musicSocket.setBz(appThemeMusic.getBz());
		musicSocket.setDeviceNo(appThemeMusic.getDeviceNo());
		musicSocket.setDeviceState(appThemeMusic.getDeviceState());
		musicSocket.setGatewayNo(appThemeMusic.getGatewayNo());
		byte[] songName = appThemeMusic.getSongName().getBytes();
		musicSocket.setSongName(songName);
		musicSocket.setSpace(appThemeMusic.getSpace());
		musicSocket.setStyle(appThemeMusic.getStyle());
		byte[] themeName = appThemeMusic.getThemeName().getBytes();
		musicSocket.setThemeName(themeName);
		musicSocket.setThemeNo(appThemeMusic.getThemeNo());

		return musicSocket;

	}

	/**
	 * @Description:内网删除情景音乐
	 * */
	public static MusicOrder getMusicOrder(String themeid) {

		MusicOrder order = new MusicOrder();
		order.setOrder("9");
		order.setStyle("6");
		order.setBz(themeid);
		order.setSongName("");
		order.setWgid(SystemValue.gatewayid);
		return order;

	}

	/**
	 * 获取到的音乐 List<Mp3> 转 List<Music>
	 * */
	public static List<Music> ToMusicList(List<Mp3> songs) {
		List<Music> list = new ArrayList<Music>();
		Iterator<Mp3> iterator = songs.iterator();
		while (iterator.hasNext()) {
			UUID uuid = UUID.randomUUID();
			String Uuid = String.valueOf(uuid);
			Mp3 mp3 = iterator.next();
			Music music = new Music();
			music.setFamilyName("a");
			music.setSongName(mp3.getName());
			music.setSpace("一楼");
			music.setUuid(Uuid);
			music.setGatewayNo(SystemValue.gatewayid);
			list.add(music);
		}
		return list;
	}

	/**
	 * List<MP3> 转 List<Map<String,Object>>
	 * */
	public static List<Map<String, Object>> ToListMap(List<Mp3> songs) {
		Iterator<Mp3> iterator = songs.iterator();
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		while (iterator.hasNext()) {
			Mp3 mp3 = (Mp3) iterator.next();
			Map<String, Object> map = new HashMap<String, Object>();
			UUID uuid = UUID.randomUUID();
			map.put("uuid", uuid);
			map.put("id", mp3.getSqlId());
			map.put("wgid", SystemValue.gatewayid); // 这个wgid 用来辨别发送到哪一家
			map.put("familyName", "a"); // 这个familyName 后面看要不要
			map.put("songName", mp3.getName());
			if (mp3.getSingerName().equals("<unknown>")) {
				map.put("singerName", "----");
			} else {
				map.put("singerName", mp3.getName());
			}
			listItems.add(map);
		}

		return listItems;

	}

	/**
	 * @Description:判断收到JPush 推送消息 音乐控制类型 后期可能还需要修改
	 * @Date:2016-06-04
	 * @author xiaobai
	 * @return 0:控制类 1:风格类 3:参数错误
	 * */
	public static int JudgeMusicOrder(MusicOrder order) {
		String musicOrder = order.getOrder();
		if (musicOrder.equals(SystemValue.MUSIC_CTRL_LAST_SONG)
				|| musicOrder.equals(SystemValue.MUSIC_CTRL_NEXT_SONG)
				|| musicOrder.equals(SystemValue.MUSIC_CTRL_PLAY)) {
			return SystemValue.MUSIC_CTRL_ORDER;
		} else if (musicOrder.equals(SystemValue.MUSIC_STYLE_LIST)
				|| musicOrder.equals(SystemValue.MUSIC_STYLE_RANDOM)
				|| musicOrder.equals(SystemValue.MUSIC_STYLE_SINGER)) {
			return SystemValue.MUSIC_STYLE_ORDER;
		} else {
			return SystemValue.MUSIC_ERROR_ORDER;
		}
	}

	/**
	 * @Description:List<MP3> 转 musicsocketbytejson
	 * */
	public static String GetSendData(List<Mp3> songs) {
		List<MusicSocketByte> list = new ArrayList<MusicSocketByte>();
		MusicSocketByte byte1=new MusicSocketByte();
		Iterator<Mp3> iterator = songs.iterator();
		while (iterator.hasNext()) {
			Mp3 mp3 = iterator.next();
			MusicSocketByte socketByte = new MusicSocketByte();
			socketByte.setBz("");
			socketByte.setOrder("0");			//0：内外获取音乐列表
			byte[] songName = mp3.getName().getBytes();
			socketByte.setSongName(songName);
			socketByte.setStyle("");
			socketByte.setWgid(SystemValue.gatewayid);
			list.add(socketByte);
		}
		byte1.setBz("");
		byte1.setOrder("0");
		byte1.setStyle(JSONArray.toJSONString(list));
		byte1.setWgid(SystemValue.gatewayid);
		String respjson = JSONObject.toJSONString(byte1);
		return respjson;
	}
	
	/**
	 * 传输联动音乐至APP
	 * */
	public static String SendThemeMusicToApp(List<APPThemeMusic> list){
		List<AppThemeMusicSocket> MusicSocketByteList=new ArrayList<AppThemeMusicSocket>();
		Iterator<APPThemeMusic> iterator=list.iterator();
		MusicSocketByte byte1=new MusicSocketByte();
		while (iterator.hasNext()) {
			AppThemeMusicSocket musicSocket=new AppThemeMusicSocket();
			APPThemeMusic music=iterator.next();
			musicSocket.setBz(music.getBz());
			musicSocket.setDeviceNo(music.getDeviceNo());
			musicSocket.setDeviceState(music.getDeviceState());
			musicSocket.setGatewayNo(music.getGatewayNo());
			byte[] songName=music.getSongName().getBytes();
			musicSocket.setSongName(songName);
			musicSocket.setSpace(music.getSpace());
			musicSocket.setStyle(music.getStyle());
			byte[] themeName=music.getThemeName().getBytes();
			musicSocket.setThemeName(themeName);
			musicSocket.setThemeNo(music.getThemeNo());
			MusicSocketByteList.add(musicSocket);
		}
		
		byte1.setBz("");
		byte1.setOrder(SystemValue.MUSIC_THEME_SEND);
		byte1.setStyle(JSONArray.toJSONString(MusicSocketByteList));
		byte1.setWgid(SystemValue.gatewayid);
		String respjson=JSON.toJSONString(byte1);
		return respjson;
		
	}
	
	

	/**
	 * @Description:音量控制
	 * */
	public static String SendVolume(String volume){
		MusicSocketByte byte1=new MusicSocketByte();
		byte1.setBz("");
		byte1.setOrder(SystemValue.MUSIC_VOLUME);	//15:获取七寸屏内网音量数值
		byte1.setStyle(volume);
		byte1.setWgid(SystemValue.gatewayid);
		String respjson=JSONObject.toJSONString(byte1);
		return respjson;
	}
	
	/**
	 * @Description:Socket收到音乐的控制命令的处理
	 * @param MusicSocketByte
	 *            order 0:获取歌曲列表 1:暂停 2:播放 3:上一首 4:下一首 5:单曲循环 6:列表循环 7:随机循环
	 *            8:获取音乐列表
	 * */
	public static String HandlerMusic(MusicSocketByte musicSocketByte) {
		MusicOrder musicOrder = MusicUtil.ToMusicOrder(musicSocketByte);
		Jpush jpush = MusicUtil.ToJush(musicOrder);
		String jpushmessage = JSONObject.toJSONString(jpush);
		// String musicorderjson=JSONObject.toJSONString(musicOrder);
		return jpushmessage;
	}

	/**
	 * @Description:七寸屏上 情景音乐
	 * */
	public static String GetJpushmessag(String songName,String style,String themeNo){
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(SystemValue.gatewayid);
		jpush.setMesssageType(SystemValue.MUSIC_JPUSH);
		jpush.setTime(0L);
		
		MusicOrder order=new MusicOrder();
		order.setBz(themeNo);
		order.setOrder(SystemValue.MUSIC_CTRL_PLAY);
		if (style.equals("1")) {
			order.setOrder(SystemValue.MUSIC_CTRL_PAUSE);
		}
		order.setSongName(songName);
		order.setStyle(SystemValue.MUSIC_STYLE_LIST);
		order.setWgid(SystemValue.gatewayid);

		jpush.setObject(JSONObject.toJSON(order));
		String jpushmessage=JSONObject.toJSONString(jpush);
		
		return jpushmessage;
		
	}
	
	
	
	
	/**
	 * @Description:musicSocketbyte 转 MusicOder
	 * @param musicsocketbyte
	 * @return musicorder
	 * */
	public static MusicOrder ToMusicOrder(MusicSocketByte musicSocketByte) {
		MusicOrder order = new MusicOrder();
		order.setBz(musicSocketByte.getBz());
		order.setOrder(musicSocketByte.getOrder());
		if (!musicSocketByte.getOrder().equals("11")) {
			String songName = new String(musicSocketByte.getSongName());
			order.setSongName(songName);
		}
		order.setStyle(musicSocketByte.getStyle());
		order.setWgid(musicSocketByte.getWgid());
		return order;
	}

	/**
	 * @Description:musicorder 转 Jpush
	 * */
	public static Jpush ToJush(MusicOrder musicOrder) {
		Jpush jpush = new Jpush();
		jpush.setGatewayNo(musicOrder.getWgid());
		jpush.setMesssageType(SystemValue.MUSIC_JPUSH);
		jpush.setTime(0L);
		jpush.setObject(JSONObject.toJSONString(musicOrder));
		return jpush;

	}

	/**
	 * @Description：musicorder 转 appthememusic
	 * */
	public static APPThemeMusic GetAppThemeMusic(MusicOrder musicOrder) {
		APPThemeMusic appThemeMusic = new APPThemeMusic();
		String str = musicOrder.getBz();
		AppThemeMusicSocket musicSocket = JSONObject.parseObject(str,
				AppThemeMusicSocket.class);
		appThemeMusic = MusicUtil.Getappthememusic(musicSocket);

		return appThemeMusic;

	}
	
	/**
	 * @Description：musicorder 转 appthememusic   只应用与 暂停情景音乐
	 * */
	public static APPThemeMusic GetPauseAppThemeMusic(MusicOrder musicOrder) {
		APPThemeMusic appThemeMusic = new APPThemeMusic();
//		appThemeMusic.setBz("");
//		appThemeMusic.setGatewayNo(musicOrder.getWgid());
//		appThemeMusic.setSongName(musicOrder.getSongName());
//		appThemeMusic.setStyle(musicOrder.getStyle());
//		appThemeMusic.setThemeNo(musicOrder.getBz());
		
//		appThemeMusic=JSONObject.parseObject(musicOrder.getBz(),APPThemeMusic.class);
		AppThemeMusicSocket appThemeMusicSocket=JSONObject.parseObject(musicOrder.getBz(),AppThemeMusicSocket.class);
		appThemeMusic=MusicUtil.Getappthememusic(appThemeMusicSocket);

		return appThemeMusic;

	}
	

	/**
	 * @Description:appthememusicsocket 转 appthememusic
	 * */
	public static APPThemeMusic Getappthememusic(AppThemeMusicSocket musicSocket) {
		APPThemeMusic appThemeMusic = new APPThemeMusic();
		appThemeMusic.setBz(musicSocket.getBz());
		appThemeMusic.setDeviceNo(musicSocket.getDeviceNo());
		appThemeMusic.setDeviceState(musicSocket.getDeviceState());
		appThemeMusic.setGatewayNo(musicSocket.getGatewayNo());
		String songName = new String(musicSocket.getSongName());
		appThemeMusic.setSongName(songName);
		appThemeMusic.setSpace(musicSocket.getSpace());
		appThemeMusic.setStyle(musicSocket.getStyle());
		String themeName = new String(musicSocket.getThemeName());
		appThemeMusic.setThemeName(themeName);
		appThemeMusic.setThemeNo(musicSocket.getThemeNo());

		return appThemeMusic;

	}

	/**
	 * @Description:收到socket未解码的 incodemusiclist 转码为 MusicList
	 * @param String
	 * @return List<MusicSocket>
	 * */
	public static List<MusicSocket> ToMusicSocketList(String incodemusiclist) {
		List<MusicSocket> musicList = new ArrayList<MusicSocket>();
		musicList = JSONArray.parseArray(incodemusiclist, MusicSocket.class);
		return musicList;
	}

	/**
	 * @Description:List<Music> 转 List<Map<String, Object>>
	 * @return List<Map<String, Object>>
	 * */
	public static List<Map<String, Object>> TolistMap(List<Music> list) {
		List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
		Iterator<Music> iterator = list.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			Music music = iterator.next();
			map.put("songName", music.getSongName());
			listems.add(map);
		}
		return listems;

	}
	
	/**
	 * @Description:List<MusicSocket> 转 List<Music>
	 * @retuen List<Music>    
	 * */
	public static List<Music> ToMusicListBY(List<MusicSocket> list){
		List<Music> musiclist=new ArrayList<Music>();
		Iterator<MusicSocket> iterator=list.iterator();
		while (iterator.hasNext()) {
			MusicSocket musicSocket=iterator.next();
			Music music=new Music();
			music.setBz("");
			music.setFamilyName("");
			String songName=new String(musicSocket.getSongName());
			music.setSongName(songName);
			music.setSpace("");
			music.setUuid("");
			music.setGatewayNo(SystemValue.gatewayid);
			musiclist.add(music);
		}
		return musiclist;
	}

	

	/**
	 * @Description:MusicOrder 转 MusicSocketByteJson
	 * @return MusicSocketByteJson
	 * */
	public static String ToMusicOrderSocketJson(MusicOrder musicOrder) {

		MusicSocketByte socketByte = new MusicSocketByte();
		socketByte.setBz(musicOrder.getBz());
		socketByte.setOrder(musicOrder.getOrder());
		if (!musicOrder.getOrder().equals("0")) { // 0表示 获取歌曲列表
			byte[] songName = musicOrder.getSongName().getBytes();
			socketByte.setSongName(songName);
		}
		socketByte.setStyle(musicOrder.getStyle());
		socketByte.setWgid(SystemValue.gatewayid);
		String respjson = JSONObject.toJSONString(socketByte) + '\0';
		return respjson;

	}

	/**
	 * @Description:将List<Mp3> 转化为List<APPMusic> 并存入sqlite
	 * @param List<Mp3>
	 * @return List<APPMusic>
	 * */
	public static List<APPMusic> GetListAppMusicBYLsitMp3(List<Mp3> mp3s){
		List<APPMusic> list=new ArrayList<APPMusic>();
		Iterator<Mp3> iterator=mp3s.iterator();
		while (iterator.hasNext()) {
			APPMusic appMusic=new APPMusic();
			Mp3 mp3 = (Mp3) iterator.next();
			appMusic.setGatewayNo(SystemValue.gatewayid);
			appMusic.setSongName(mp3.getName());
			list.add(appMusic);
		}
		
		return list;
		
	}
	
	/**
	 * @Description:情景音乐设置 生成 themeMusic
	 * 
	 * */	
	public static ThemeMusic GetThemeMusic(Theme theme,ThemeMusic themeMusic){
		themeMusic.setThemeNo(theme.getThemeNo());
		themeMusic.setThemeName(theme.getThemeName());
		themeMusic.setDeviceNo(theme.getDeviceNo());
		themeMusic.setDeviceState(theme.getThemeState());
		if (theme.getThemeState().length()<5) {
			String themestate=theme.getThemeState()+"0000";
			themeMusic.setDeviceState(themestate);
		}
		themeMusic.setSpace("");
		themeMusic.setStyle("5");   //情景音乐都设置为单曲循环
		
		return themeMusic;
	}

	/**
	 * 获取设置的  情景音乐 在歌曲列表的下标
	 * */
	public static int GetMusicListIndex(String themeMusicName,List<ThemeMusic> list){
		int a=-1;
		for (int i = 0; i < list.size(); i++) {
			if (themeMusicName.equals(list.get(i).getSongName())) {
				a=i;
				break;
			}
		}
		return a;
	
	}
	
	/**
	 * 得到Volume对象
	 * */
	public static Volume GetVolume(String volumeL){
		Volume volume=new Volume();
		volume.setGatewayNo(SystemValue.gatewayid);
		volume.setVolume(volumeL);
		return volume;
	}
	
	/**
	 * Appthememusic 转 musicorder   用于内网联动音乐
	 * */
	public static String GteMusicOrder(APPThemeMusic appThemeMusic){
		MusicOrder musicOrder=new MusicOrder();
		musicOrder.setBz(appThemeMusic.getBz());
		musicOrder.setOrder("2");
		musicOrder.setSongName(appThemeMusic.getSongName());
		musicOrder.setStyle(appThemeMusic.getStyle());
		musicOrder.setWgid(appThemeMusic.getGatewayNo());
		if (appThemeMusic.getStyle().equals("1")) {
			musicOrder.setOrder("1");
		}
		
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(appThemeMusic.getGatewayNo());
		jpush.setMesssageType(3);
		jpush.setObject(JSONObject.toJSONString(musicOrder));
		jpush.setTime(0L);
		return JSONObject.toJSONString(jpush);
	}
	
	/**
	 * 判断是不是情景联动音乐推送的JPush  
	 * 情景联动音乐 返回true  单控音乐返回false
	 * */
	public static boolean ThemeMusicJpush(MusicOrder musicOrder){
		String order=musicOrder.getOrder();
		if (order.equals("20") || order.equals("21") || order=="21" || order=="20") {
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * Jpush收到的情景联动音乐 20/21要 转回来
	 * */
	public static MusicOrder ThemeMusic(MusicOrder musicOrder){
		String order=musicOrder.getOrder();
		if (order.equals("20") || order=="20") {
			musicOrder.setOrder("2");
		}else {
			musicOrder.setOrder("1");
		}
		return musicOrder;
		
	}
	
	/**
	 * 七寸屏启动Datagramsocket 发送自己的IP
	 * */
	public static void StartSendIp(){
		SystemValue.datagramClient=new DatagramSocketClient();
		SystemValue.datagramClient.onCreate();
	}
	
	
	/**
	 * 后台播放情景联动音乐
	 * */	
	public static  void CtrlMusic(String jpushString,Context context) {
		musicPlayService=SystemValue.musicPlayService;
		if (musicPlayService==null) {
			if (SystemValue.musicPlayService==null) {
				SystemValue.musicPlayService =new MusicPlayService();
				SystemValue.musicPlayService.onCreate();
			}
		    musicPlayService=SystemValue.musicPlayService;
		}
		
			// 获取收到的 jpush 的数据 转换为
			Jpush jpush = JSON.parseObject(jpushString, Jpush.class);
			try {
				MusicOrder order1 = JSON.parseObject( jpush.getObject().toString(), MusicOrder.class);
				Log.i("2016","控制播放："+order1.toString());
				MusicOrder order=MusicUtil.ThemeMusic(order1);
//				String songName=song.get(musicPlayService.getCurrentListItme()).getName();
				int index=musicPlayService.getCurrentListItme();
				String songName=null;
				if (index!=-1) {
					 songName=song.get(index).getName();
				}
				
				Log.i("2016","当前正在播放的歌曲 "+songName);
				/**
				 * 根据pause判断是否是暂停控制 然后根据歌曲名称进行查询播放
				 */
				if (order.getOrder().equals(SystemValue.MUSIC_CTRL_PAUSE)) {
//					if (order.getBz()!=null && !order.getBz().equals("")) {
//						//情景音乐中 order.bz 有值   用于解决暂停情景音乐 点击两次会播放音乐
//						musicPlayService.getmMediaPlayer().pause();
//					}else{
//						//单控音乐order.bz没有值
//						musicPlayService.pausePlay();
//					}
					if (order.getSongName().equals(songName) || order.getSongName()==songName) {
						musicPlayService.getmMediaPlayer().pause();
					}
					
				}else {
					Log.i("2016",musicPlayService.toString()+"   "+order.toString());
					song = MusicUtils.getAllSongs(context);
					for (int i = 0; i < song.size(); i++) {
						Mp3 mp3 = song.get(i);
						if (order.getSongName().equals(mp3.getName())) {
							musicPlayService.setCurrentListItme(i);
							musicPlayService.setDuration(song.get(i).getDuration());
							musicPlayService.setSongs(song);
							int musicorder = MusicUtil.JudgeMusicOrder(order);
							Log.i("2016",String.valueOf(musicorder));
							if (musicorder == SystemValue.MUSIC_CTRL_ORDER) {
								musicPlayService.playMusic(mp3.getUrl());
								SystemValue.MUSIC_STYLE=SystemValue.MUSIC_STYLE_SINGER;
							} else if (musicorder == SystemValue.MUSIC_STYLE_ORDER) {
								if (!musicPlayService.isPlay()) { // 当前未在播放
									musicPlayService.playMusic(mp3.getUrl());
								}
								SystemValue.MUSIC_STYLE=SystemValue.MUSIC_STYLE_SINGER;
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
	
	/**
	 * 用于socketservice 收到硬件情景音乐的 appthememusic转musicorder
	 * 其中musicorder 20：play   21:pause
	 * */
	public static String GetMusicOrderScenePanel(APPThemeMusic appThemeMusic){
		MusicOrder musicOrder=new MusicOrder();
		musicOrder.setBz(appThemeMusic.getThemeNo());
		musicOrder.setOrder("20");
		musicOrder.setSongName(appThemeMusic.getSongName());
		musicOrder.setStyle(appThemeMusic.getStyle());
		musicOrder.setWgid(appThemeMusic.getGatewayNo());
		if (appThemeMusic.getStyle().equals("1")) {
			musicOrder.setOrder("21");
		}
		
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(appThemeMusic.getGatewayNo());
		jpush.setMesssageType(3);
		jpush.setObject(JSONObject.toJSONString(musicOrder));
		jpush.setTime(0L);
		return JSON.toJSONString(jpush);
	}
	
	/**
	 * List<ThemeMusic> 转 List<AppThemeMusic>
	 * @param List<ThemeMusic>
	 * @return List<APPThemeMusic>
	 * */
	public static List<APPThemeMusic> ThemeMusicListToAppThemeMuiscList(List<ThemeMusic> list){
		List<APPThemeMusic> list2=new ArrayList<APPThemeMusic>();
		Iterator<ThemeMusic> iterator=list.iterator();
		while (iterator.hasNext()) {
			APPThemeMusic appThemeMusic=new APPThemeMusic();
			ThemeMusic themeMusic=iterator.next();
			appThemeMusic.setBz(themeMusic.getBz());
			appThemeMusic.setDeviceNo(themeMusic.getDeviceNo());
			appThemeMusic.setDeviceState(themeMusic.getDeviceState());
			appThemeMusic.setGatewayNo(themeMusic.getGatewayNo());
			appThemeMusic.setSongName(themeMusic.getSongName());
			appThemeMusic.setSpace(themeMusic.getSpace());
			appThemeMusic.setStyle(themeMusic.getStyle());
			appThemeMusic.setThemeName(themeMusic.getThemeName());
			appThemeMusic.setThemeNo(themeMusic.getThemeNo());
			list2.add(appThemeMusic);
		}
		return list2;
	}
	
	
	/**
	 * APPThemeMusic 转 ThemeMusic
	 * @param AppThemeMusic
	 * @return ThemeMusic
	 * */
	public static ThemeMusic AppThemeMusicToThemeMusic(APPThemeMusic appThemeMusic){
		ThemeMusic themeMusic=new ThemeMusic();
		themeMusic.setBz(appThemeMusic.getBz());
		themeMusic.setDeviceNo(appThemeMusic.getDeviceNo());
		themeMusic.setDeviceState(appThemeMusic.getDeviceState());
		themeMusic.setGatewayNo(appThemeMusic.getGatewayNo());
		themeMusic.setSongName(appThemeMusic.getSongName());
		themeMusic.setSpace(appThemeMusic.getSpace());
		themeMusic.setStyle(appThemeMusic.getStyle());
		themeMusic.setThemeName(appThemeMusic.getThemeName());
		themeMusic.setThemeNo(appThemeMusic.getThemeNo());
		
		return themeMusic;
		
	}
	
	/**
	 * 只应用于SceneModerActivity中  控制情景联动音乐播放
	 * 由于之前 MusicUtil 工具类中的播放情景联动音乐写法的问题 才出现这个类
	 * order 中的 2转为 20  1转为 21
	 * */
	public static String MusicOrderToMusicOrder(APPThemeMusic appThemeMusic){
		MusicOrder order=new MusicOrder();
		Jpush jpush=new Jpush();
		order.setBz(appThemeMusic.getThemeNo());
		order.setOrder(appThemeMusic.getStyle());
		if (appThemeMusic.getStyle().equals("5")) {
			order.setOrder("20");
		}else if (appThemeMusic.getStyle().equals("1")) {
			order.setOrder("21");
		}
		order.setSongName(appThemeMusic.getSongName());
		order.setStyle(appThemeMusic.getStyle());
		order.setWgid(appThemeMusic.getGatewayNo());
		
		jpush.setGatewayNo(appThemeMusic.getGatewayNo());
		jpush.setMesssageType(SystemValue.MUSIC_JPUSH);
		jpush.setTime(0L);
		jpush.setObject(JSONObject.toJSONString(order));
		
		String str=JSONObject.toJSONString(jpush);
		return str;
		
	}
	
	
	/**
	 * 每次七寸屏收到一个 order=23  的  表示确认七寸屏的IP 是否正确
	 * 七寸屏需要回复一个24
	 * **/
	public static String RespClient(){
		MusicSocketByte socketByte=new MusicSocketByte();
		socketByte.setOrder("24");   //回复一个socket给手机端
		socketByte.setWgid(SystemValue.gatewayid);
		String resp=JSON.toJSONString(socketByte);
		return resp;
		
	}
	
	
	
	
	
	
}

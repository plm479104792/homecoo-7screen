package com.tuwa.smarthome.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tuwa.smarthome.activity.MusicMainActivity;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Mp3;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.MusicSocketByte;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.MusicUtils;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MulticastService extends Service implements Runnable{
	
	private ServerSocket serverSocket = null;
	StringBuffer stringBuffer = new StringBuffer();
	private boolean socketStatus = false;
	private OutputStream outputStream = null;
	private String data;
	private InputStream inputStream;
	private List<Mp3> songs;// 歌曲集合
//	private MusicPlayService musicService;
	
	/**
	 * @Description:Ip协议为多点广播提供了一批特殊的IP地址，这些IP地址的范围是 224.0.0.0---239.255.255.255
	 * 启动一个服务不停的往这个IP地址段里面发生广播
	 * */
	private String multicasthost="224.0.0.1";
	private MulticastSocket multicastSocket;   //多点广播
	private Thread t;
	private InetAddress inetAddress=null;
	private volatile boolean isRuning=true;
	private String IP;
	private MusicSocketByte musicSocketByte=null;
	
	
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
//				editText_1.setText(msg.obj.toString());
				break;
			case 2:
//				editText_2.setText(msg.obj.toString());
				String aa=msg.obj.toString();
				Log.i("clientAPP","七寸屏Socket接收到的数据："+aa);
				try {
					
				
				musicSocketByte=JSON.parseObject(aa, MusicSocketByte.class);
				Log.i("inside","MulticastSocket接收的音乐Order"+musicSocketByte.toString());
				if (musicSocketByte.getOrder().equals("0")) {
					//获取音乐列表
					songs=MusicUtils.getAllSongs(MulticastService.this);
					String str=MusicUtil.GetSendData(songs);
//					System.out.println("songs"+songs.toString());
					send(str);
				}else if (musicSocketByte.getOrder().equals("9")) {
					Log.i("inside","删除情景音乐："+musicSocketByte.toString());
					List<APPThemeMusic> list=new APPThemeMusicDao(MulticastService.this).GetAppThemeMusicListByThemeNo(musicSocketByte.getWgid());
					Log.i("inside","删除情景音乐之前："+list.toString());
					new APPThemeMusicDao(MulticastService.this).DeleteAppThemeMusic(musicSocketByte.getBz());
					
					List<APPThemeMusic> list1=new APPThemeMusicDao(MulticastService.this).GetAppThemeMusicListByThemeNo(musicSocketByte.getWgid());
					Log.i("inside","删除情景音乐之后："+list1.toString());
				}else if (musicSocketByte.getOrder().equals("10")) {
//					Log.i("2016","更新情景音乐设置："+musicSocketByte.toString());
					//更新情景音乐
					MusicOrder musicOrder=MusicUtil.ToMusicOrder(musicSocketByte);
					APPThemeMusic appThemeMusic=MusicUtil.GetAppThemeMusic(musicOrder);
					//先判断是否已经情景音乐，没有 添加  有更新
//					List<APPThemeMusic> list=new APPThemeMusicDao(MulticastService.this).GetAppThemeMusicListByThemeNo(appThemeMusic.getThemeNo());
//					Log.i("2016","更新情景音乐之前的 ："+list.toString());
					new APPThemeMusicDao(MulticastService.this).UpdateOrAddThemeMusicByThemeNo(appThemeMusic);
//					if (list.size()>0) {
//						new APPThemeMusicDao(MulticastService.this).UpdateAppThemeMusci(appThemeMusic);
//					}else{
//						new APPThemeMusicDao(MulticastService.this).InsertAppThemeMusic(appThemeMusic);
//					}
//					List<APPThemeMusic> list1=new APPThemeMusicDao(MulticastService.this).GetAppThemeMusicListByThemeNo(appThemeMusic.getThemeNo());
//					Log.i("2016","更新情景音乐之后："+list1.toString());
					
				}else if (musicSocketByte.getOrder().equals("11")) {
					//TODO 播放情景音乐
					Log.i("2016","socket收到的  "+musicSocketByte.toString());
					MusicOrder musicOrder=MusicUtil.ToMusicOrder(musicSocketByte);
					List<APPThemeMusic> list= new APPThemeMusicDao(MulticastService.this).GetAppThemeMusicListByThemeNo(musicOrder.getBz());
					if (list.size()>0) {
						String songName=list.get(0).getSongName();
						musicOrder.setSongName(songName);
						musicOrder.setOrder("20");
						if (list.get(0).getStyle().equals("1") || list.get(0).getStyle()=="1") {
							musicOrder.setOrder("21");
							musicOrder.setBz(musicOrder.getSongName());
						}
						Log.i("2016","内网情景音乐播放："+list.toString());
						String jpushmessage=JSONObject.toJSONString(MusicUtil.ToJush(musicOrder));
//						CtrlMusic(jpushmessage);
						MusicUtil.CtrlMusic(jpushmessage, MulticastService.this);
						
					}
				}else if (musicSocketByte.getOrder().equals("12")) {
//					Log.i("2016", "本地暂停情景音乐“："+musicSocketByte.toString());
					//更新情节音乐暂停
					MusicOrder musicOrder=MusicUtil.ToMusicOrder(musicSocketByte);
//					Log.i("2016","musicOrder "+musicOrder.toString());
					APPThemeMusic appThemeMusic=MusicUtil.GetPauseAppThemeMusic(musicOrder);
//					Log.i("2016","appThemeMusic "+appThemeMusic.toString() );
//					List<APPThemeMusic> list=new APPThemeMusicDao(MulticastService.this).GetAppThemeMusicListByThemeNo(appThemeMusic.getThemeNo());
//					Log.i("2016","之前的暂停情景音乐："+list.toString());
//					if (list.size()>0) {1
//						new APPThemeMusicDao(MulticastService.this).UpdateAppThemeMusci(appThemeMusic);
//					}else{
//						new APPThemeMusicDao(MulticastService.this).InsertAppThemeMusic(appThemeMusic);
//					}
					new APPThemeMusicDao(MulticastService.this).UpdateOrAddThemeMusicByThemeNo(appThemeMusic);
					
//					List<APPThemeMusic> list1=new APPThemeMusicDao(MulticastService.this).GetAppThemeMusicListByThemeNo(appThemeMusic.getThemeNo());
//					Log.i("2016","之后的暂停情景音乐："+list1.toString());
					
				}else if (musicSocketByte.getOrder().equals(SystemValue.MUSIC_VOLUME)) {
					//音量数值获取
					AudioManager manager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
					String cc=String.valueOf(manager.getStreamVolume(AudioManager.STREAM_MUSIC));
					String sendData=MusicUtil.SendVolume(cc);
					send(sendData);
				}else if (musicSocketByte.getOrder().equals(SystemValue.MUSIC_VOLUME_CTRL)) {
					//音量控制
					AudioManager manager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
					manager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.valueOf(musicSocketByte.getStyle()), AudioManager.FLAG_PLAY_SOUND);
				}else if (musicSocketByte.getOrder().equals(SystemValue.MUSIC_THEME_GET)) {
					//APP 获取联动音乐  七寸屏发送联动音乐到APP
					List<APPThemeMusic> list=new APPThemeMusicDao(MulticastService.this).GetAppThemeMusicListByGatewayNo(musicSocketByte.getWgid());
					Log.i("2016",list.toString());
					if (list.size()>0) {
					String str=MusicUtil.SendThemeMusicToApp(list);
					Log.i("2016","七寸屏发送的情景联动音乐   "+str);
					send(str);
					}
				}else if (musicSocketByte.getOrder().equals("23")) {
					Log.i("clientAPP",musicSocketByte.toString());
					String UDPIP=MusicUtil.RespClient();
					send(UDPIP);
					
				}else{
					//音乐控制
					String jpushmessage=MusicUtil.HandlerMusic(musicSocketByte);
					Log.i("inside", jpushmessage.toString());
					Context context=MulticastService.this;
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
						a.putExtra("jpushMsgMusic", jpushmessage);
						context.startActivity(a);
					}else{
						Intent musicmsg = new Intent(
								MusicMainActivity.MESSAGE_RECEIVED_ACTION);
						musicmsg.putExtra("message", jpushmessage);
						context.sendBroadcast(musicmsg);  //极光接收，直接调用musicService播放
					}
				}
				} catch (Exception e) {
					System.err.println("本地音乐json解析异常"+e);
				}
				stringBuffer.setLength(0);
				
				break;
			}

		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		IP=getIp();
		SystemValue.IP=null;
		SystemValue.IP=IP;
		GetScreenIp();
		try {
			multicastSocket=new MulticastSocket(8003);
			inetAddress=InetAddress.getByName(multicasthost);
			multicastSocket.setSoTimeout(10000);
			multicastSocket.setTimeToLive(1);
			multicastSocket.joinGroup(inetAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		t=new Thread(this);
		t.start();
		receiveData();
	}
	
	/*
	 * 服务器端接收数据 需要注意以下一点： 服务器端应该是多线程的，因为一个服务器可能会有多个客户端连接在服务器上；
	 */
	public void receiveData() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				/* 指明服务器端的端口号 */
				try {
					serverSocket = new ServerSocket(8000);
					Log.i("inside","启动内网socket");
					socketStatus = true;
				} catch (IOException e) {
					e.printStackTrace();
				}

				while (true) {
					Socket socket = null;
					try {
						socket = serverSocket.accept();
						inputStream = socket.getInputStream();
						outputStream = socket.getOutputStream();
					} catch (IOException e) {
						e.printStackTrace();
					}

					new ServerThread(socket, inputStream).start();

				}
			}
		};
		thread.start();

	}
	
	class ServerThread extends Thread {

		private Socket socket;
		private InputStream inputStream;
		private StringBuffer stringBuffer = MulticastService.this.stringBuffer;

		public ServerThread(Socket socket, InputStream inputStream) {
			this.socket = socket;
			this.inputStream = inputStream;
		}

		@Override
		public void run() {
			int len;
			byte[] bytes = new byte[20];
			boolean isString = false;

			try {
				// 在这里需要明白一下什么时候其会等于 -1，其在输入流关闭时才会等于 -1，
				// 并不是数据读完了，再去读才会等于-1，数据读完了，最结果也就是读不到数据为0而已；
				while ((len = inputStream.read(bytes)) != -1) {
					for (int i = 0; i < len; i++) {
						if (bytes[i] != '\0') {
							stringBuffer.append((char) bytes[i]);
						} else {
							isString = true;
							break;
						}
					}
					if (isString) {
						Message message_2 = handler.obtainMessage();
						message_2.what = 2;
						message_2.obj = stringBuffer;
						Log.i("clientAPP","收到的socket信息     "+stringBuffer);
						handler.sendMessage(message_2);
						isString = false;
//						@SuppressWarnings("unused")
//						String aa = String.valueOf(stringBuffer);
					}
				}
				// 当这个异常发生时，说明客户端那边的连接已经断开
			} catch (IOException e) {
				e.printStackTrace();
//				try {
//					inputStream.close();
//					socket.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}

			}

		}
	}
	
	
	/**
	 * @Description:获取IP地址
	 * */
	private String getIp() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int i = wifiInfo.getIpAddress();
		String ipaddress = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
				+ ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
		SystemValue.SCREEN_IP=ipaddress;
//		Log.i("clientAPP", ipaddress);
		return ipaddress;

	}

	/**
	 * @Description:发送数据
	 * @date:2016-06-12
	 * */
	public void send(String str) {

		// data = editText_data.getText().toString();
//		data = Test.getMusicList();
		 data=null;
		 data=str;
		if (data == null) {
			Toast.makeText(MulticastService.this, "please input Sending Data",
					Toast.LENGTH_SHORT).show();
		} else {
			// 在后面加上 '\0' ,是为了在服务端方便我们去解析；
			data = data + '\0';
		}

		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				if (socketStatus) {
					try {
						outputStream.write(data.getBytes());
						outputStream.flush();
						Log.i("inside","发送到手机上："+data);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}
		};
		thread.start();
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}


	@SuppressWarnings("resource")
	@Override
	public void run() {
		DatagramPacket datagramPacket = null;
		byte[] data = IP.getBytes();
//		byte[] data = SocketIp().getBytes();
		datagramPacket = new DatagramPacket(data, data.length, inetAddress, 8003);
		while (true) {
			if (isRuning) {
				try {
					multicastSocket.send(datagramPacket);
					DatagramSocket datagramSocket=new DatagramSocket();
					datagramSocket.send(datagramPacket);
					Thread.sleep(60000);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 发送socketIp
	 * */
//	private String SocketIp(){
//		SocketConnect connect=new SocketConnect();
//		connect.setGatewayNo(SystemValue.gatewayid);
//		connect.setIpAddress(getIp());
//		String respjson=JSON.toJSONString(connect);
//		return respjson;
//	}
	
	
	/**
	 * 开启一个线程专门用于获取当前的IP地址
	 * */
	public void GetScreenIp(){

		Thread thread=new Thread(){
			@Override
			public void run() {
				super.run();
				
				while (true) {
					
				IP=getIp();
				SystemValue.IP=null;
				SystemValue.IP=IP;
				
				}
			}
		};thread.start();
	}
	
	
	
}

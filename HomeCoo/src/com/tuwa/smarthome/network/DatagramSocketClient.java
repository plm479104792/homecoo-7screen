package com.tuwa.smarthome.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import com.tuwa.smarthome.global.SystemValue;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
/**
 * DatgramSocket发送七寸屏的IP(UDP协议)
 * */
public class DatagramSocketClient extends Service{
	
	private DatagramSocket datagramSocket;
	private static boolean flag=true;
	private InetAddress inetAddress=null;
	private DatagramPacket datagramPacket;
	
	@SuppressLint("HandlerLeak")
	public Handler handler=new Handler(){
		
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 1:
				if (msg.obj.toString()==SystemValue.gatewayid || msg.obj.toString().equals(SystemValue.gatewayid)) {
					byte[] data=SystemValue.SCREEN_IP.getBytes();
					DatagramPacket pack;
					try {
						pack = new DatagramPacket(data, data.length,inetAddress,8004);
						datagramSocket.send(pack);
					} catch (SocketException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
			}
		}
		
	};
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			datagramSocket=new DatagramSocket(8004);
			inetAddress=InetAddress.getByName("255.255.255.255");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		StartDatagramSocketClient();
	}
	
	
	/**
	 * 启动service一直等待接收UDP广播
	 * */
	public void StartDatagramSocketClient(){
		Thread thread=new Thread(){
			@Override
			public void run() {
				super.run();
				Log.i("clientAPP","启动Client ");
				byte[] buf=new byte[1024];
				datagramPacket=new DatagramPacket(buf, buf.length);
				while (flag) {
					
					try {
						datagramSocket.receive(datagramPacket);
						String receiveData=new String(buf, 0, datagramPacket.getLength());
						Log.i("clientAPP","七寸屏收到的UDP广播 : "+receiveData);
//						String RDataHead=receiveData.substring(0, 1);
						String RData=receiveData.substring(1);
						if (RData==SystemValue.gatewayid || RData.equals(SystemValue.gatewayid)) {
							SendDatagramSocketIp();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};thread.start();
	}


	/**
	 * 当收到一个UDP广播的数据和自己的gatewayNo一致时,回复自己的IP地址
	 * */
	public void SendDatagramSocketIp(){
		Thread thread=new Thread(){
			@Override
			public void run() {
				super.run();
				String CData="C"+SystemValue.SCREEN_IP;
//				byte[] data=SystemValue.SCREEN_IP.getBytes();
				byte[] data=CData.getBytes();
				DatagramPacket pack;
				try {
					pack = new DatagramPacket(data, data.length,inetAddress,8004);
					datagramSocket.send(pack);
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.i("clientAPP","七寸屏发送自己的IP ："+CData);
				
			}
		};thread.start();
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
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
		return ipaddress;
	}

}

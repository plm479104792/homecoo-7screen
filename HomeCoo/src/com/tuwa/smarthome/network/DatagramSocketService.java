/*package com.tuwa.smarthome.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.alibaba.fastjson.JSON;
import com.tuwa.smarthome.entity.SocketConnect;
import com.tuwa.smarthome.global.SystemValue;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;


public class DatagramSocketService extends Service implements Runnable{
	
	
	
	private DatagramSocket datagramSocket=null;
	private Thread t;
	private String multicasthost="255.255.255.255";
	private InetAddress inetAddress=null;
	private volatile boolean isRuning=true;
	private String IP;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		IP=getIp();
		super.onCreate();
		try {
			datagramSocket=new DatagramSocket();
			inetAddress=InetAddress.getByName(multicasthost);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		t=new Thread(this);
		t.start();
		
	}
	
	@SuppressWarnings("resource")
	@Override
	public void run() {
		DatagramPacket datagramPacket = null;
		byte[] data = IP.getBytes();
//		byte[] data = SocketIp().getBytes();
		datagramPacket = new DatagramPacket(data, data.length, inetAddress, 8004);
		while (true) {
			if (isRuning) {
				try {
					datagramSocket.send(datagramPacket);
					Log.i("IP","DatagramSocketService"+SocketIp());
					datagramSocket.setSoTimeout(0);
					Thread.sleep(60000);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	*//**
	 * @Description:获取IP地址0
	 * *//*
	private String getIp() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int i = wifiInfo.getIpAddress();
		String ipaddress = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
				+ ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
		SystemValue.SCREEN_IP=ipaddress;
		return ipaddress;

	}
	
	
	*//**
	 * 发送socketIp
	 * *//*
	private String SocketIp(){
		SocketConnect connect=new SocketConnect();
		connect.setGatewayNo(SystemValue.gatewayid);
		connect.setIpAddress(getIp());
		String respjson=JSON.toJSONString(connect);
		return respjson;
	}
	
	

}
*/
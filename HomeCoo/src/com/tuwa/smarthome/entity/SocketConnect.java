package com.tuwa.smarthome.entity;

/**
 * 内网音乐 socket通信
 * */
public class SocketConnect {

	private String gatewayNo;
	private String IpAddress;
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public String getIpAddress() {
		return IpAddress;
	}
	public void setIpAddress(String ipAddress) {
		IpAddress = ipAddress;
	}
	@Override
	public String toString() {
		return "SocketConnect [gatewayNo=" + gatewayNo + ", IpAddress="
				+ IpAddress + "]";
	}
	
}

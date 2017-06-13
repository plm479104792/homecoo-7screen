package com.tuwa.smarthome.entity;

import java.util.Arrays;
/**
 * 内网音乐传输类
 * */
public class MusicSocketByte {
	
	private byte[] songName;
	private String wgid;		//网关id
	private String order;			//0:获取歌曲列表 1:暂停     2:播放    3:上一首     4:下一首    5:单曲循环  6:列表循环   7:随机循环  8:获取音乐列表 15：获取七寸屏内网音量数值   16：七寸屏返回音量数值
	private String style;			//备用
	private String bz;			//备用
	public byte[] getSongName() {
		return songName;
	}
	public void setSongName(byte[] songName) {
		this.songName = songName;
	}
	public String getWgid() {
		return wgid;
	}
	public void setWgid(String wgid) {
		this.wgid = wgid;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	@Override
	public String toString() {
		return "MusicSocketByte [songName=" + Arrays.toString(songName)
				+ ", wgid=" + wgid + ", order=" + order + ", style=" + style
				+ ", bz=" + bz + "]";
	}
	
}

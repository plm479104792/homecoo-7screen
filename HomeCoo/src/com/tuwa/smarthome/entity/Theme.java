package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName = "theme") 
public class Theme {
	@DatabaseField(columnName = "THEME_ID")
	private String themeId;
	@DatabaseField(columnName = "THEME_NO")
	private String themeNo;
	@DatabaseField(columnName = "THEME_NAME") 
	private String themeName;
	@DatabaseField(columnName = "DEVICE_NO")   //实体情景设备识别码
	private String deviceNo;
	@DatabaseField(columnName = "GATEWAY_NO") 
	private String gatewayNo;  
	@DatabaseField(columnName = "THEME_TYPE")    //情景类型 1：硬件四路 2：双控开关   3：安防情景 4：自定义情景
	private Integer themeType;
	@DatabaseField(columnName = "THEME_STATE") 
	private String themeState;
	@DatabaseField(columnName = "THEME_QUICK")  //快捷情景标志     1：快捷情景
	private String themeQuick;
	public String getThemeId() {
		return themeId;
	}
	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}
	public String getThemeNo() {
		return themeNo;
	}
	public void setThemeNo(String themeNo) {
		this.themeNo = themeNo;
	}
	public String getThemeName() {
		return themeName;
	}
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public Integer getThemeType() {
		return themeType;
	}
	public void setThemeType(Integer themeType) {
		this.themeType = themeType;
	}
	public String getThemeState() {
		return themeState;
	}
	public void setThemeState(String themeState) {
		this.themeState = themeState;
	}
	public String getThemeQuick() {
		return themeQuick;
	}
	public void setThemeQuick(String themeQuick) {
		this.themeQuick = themeQuick;
	}
	public Theme(String themeId, String themeNo, String themeName,
			String deviceNo, String gatewayNo, Integer themeType,
			String themeState, String themeQuick) {
		super();
		this.themeId = themeId;
		this.themeNo = themeNo;
		this.themeName = themeName;
		this.deviceNo = deviceNo;
		this.gatewayNo = gatewayNo;
		this.themeType = themeType;
		this.themeState = themeState;
		this.themeQuick = themeQuick;
	}
	public Theme() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Theme [themeId=" + themeId + ", themeNo=" + themeNo
				+ ", themeName=" + themeName + ", deviceNo=" + deviceNo
				+ ", gatewayNo=" + gatewayNo + ", themeType=" + themeType
				+ ", themeState=" + themeState + ", themeQuick=" + themeQuick
				+ "]";
	}
	
	

}

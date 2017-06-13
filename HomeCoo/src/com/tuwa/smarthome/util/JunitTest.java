package com.tuwa.smarthome.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tuwa.smarthome.dao.SpaceDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.SystemValue;

import android.test.AndroidTestCase;

public class JunitTest extends AndroidTestCase {
	public void checkVersion() {
		System.out.println("==单元测试===");

		Version version = new Version();
		version.setPhonenum("18679451786");
		version.setGatewayNo(SystemValue.gatewayid);
		version.setVersionType(SystemValue.VERSION_SPACE);

		boolean result = new VersionDao(null).isUseableVersion(version);
		System.out.println("==单元测试===" + result);
	}

	public void deleteTheme() {
		new ThemeDeviceDao(null)
				.deleteThemeDeviceAllByThemeNo("92881B5D3A449B65");
	}

	public void TestByteBuffer() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byteBuffer.putInt(1024);
		byteBuffer.put("134".getBytes());
		byte[] bytes = byteBuffer.array();

		for (byte b : bytes) {
			System.out.println(b);
		}
	}
	
	
	public void convertChinese2Byte(){
		String str=DataConvertUtil.chineseToString("door红外");
		System.out.println("===转换后的汉字为==="+str);
	
		
		
		byte[] bSpaceName=str.getBytes();
		String strb=new String(bSpaceName);
		
		String chinese=DataConvertUtil.stringToChinese(strb);
		System.out.println("===转换后的汉字为==="+chinese);
		
	}
	
	public void byte2int(){
//		byte[] head =new byte[]{0x05,0x19};
//		int i=DataConvertUtil.byte2int(head);
//		System.out.println("wendu"+i);
		
		String gatewayNo="AADDAAD8";
		byte[] bGateway=gatewayNo.getBytes();
		String str=DataConvertUtil.toHexUpString(bGateway);
		System.out.println("网关转码后为==="+str);
		
	}
	
//	public static int Length(String str)
//	{
//	    int len = 0;
//	    char[] arr = str.toCharArray();
//	    for (int i = 0; i < arr.length; i++)
//	    {
//	        //汉字
//	        if (Asc(arr[i].ToString) == -1)
//	            len += 2;
//	        else
//	            len += 1;
//	    }
//	 
//	    return len;
//	}
}

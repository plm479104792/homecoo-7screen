package com.homecoolink;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.homecoolink.R;
import com.homecoolink.activity.AlarmActivity;
import com.homecoolink.data.AlarmMask;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.data.SharedPreferencesManager;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.MusicManger;
import com.homecoolink.utils.T;
import com.p2p.core.P2PValue;

public class P2PConnect {
	public P2PConnect(Context context) {
		P2PConnect.mContext = context;
	}

	static String TAG = "p2p";
	public static final int P2P_STATE_NONE = 0;

	public static final int P2P_STATE_CALLING = 1;
	public static final int P2P_STATE_READY = 2;
	public static final int P2P_STATE_ALARM = 4;

	private static int current_state = P2P_STATE_NONE;
	private static String current_call_id = "0";
	private static int currentDeviceType;
	private static boolean isAlarming = false;
	private static boolean isPlaying = false;

	private static int mode = P2PValue.VideoMode.VIDEO_MODE_SD;
	private static int number = 1;

	static Context mContext;

	public static int getCurrent_state() {
		return current_state;
	}

	public static void setCurrent_state(int current_state) {
		P2PConnect.current_state = current_state;
		switch (current_state) {
		case P2P_STATE_NONE:
			Log.e(TAG, "P2P_STATE_NONE");
			break;
		case P2P_STATE_CALLING:
			Log.e(TAG, "P2P_STATE_CALLING");
			break;
		case P2P_STATE_READY:
			Log.e(TAG, "P2P_STATE_READY");
			break;
		}
	}

	public static int getMode() {
		return mode;
	}

	public static void setMode(int mode) {
		P2PConnect.mode = mode;
	}

	public static int getNumber() {
		return number;
	}

	public static void setNumber(int number) {
		P2PConnect.number = number;
	}

	public static String getCurrent_call_id() {

		return current_call_id;
	}

	public static void setCurrent_call_id(String current_call_id) {
		P2PConnect.current_call_id = current_call_id;
	}

	public static void setCurrentDeviceType(int type) {
		P2PConnect.currentDeviceType = type;
	}

	public static int getCurrentDeviceType() {
		return currentDeviceType;
	}

	public static boolean isPlaying() {
		return isPlaying;
	}

	public static void setPlaying(boolean isPlaying) {
		P2PConnect.isPlaying = isPlaying;
	}

	public static synchronized void vCalling(boolean isOutCall, int type) {
		// TODO Auto-generated method stub
		Log.e(TAG, "vCalling:" + current_call_id);
		P2PConnect.setCurrentDeviceType(type);
		if (!isOutCall && current_state == P2P_STATE_NONE) {
			P2PConnect.setCurrent_state(P2P_STATE_CALLING);

			Intent call = new Intent();
			call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			call.setClass(mContext, CallActivity.class);
			call.putExtra("callId", current_call_id);
			call.putExtra("type", Constants.P2P_TYPE.P2P_TYPE_CALL);
			mContext.startActivity(call);
		}
	}

	public static synchronized void vReject(String msg) {
		// TODO Auto-generated method stub
		Log.e(TAG, "vReject:" + msg);
		if (!msg.equals("")) {
			if (!msg.equals(MyApp.app.getResources().getString(
					R.string.pw_incrrect))) {
				T.showShort(mContext, msg);
			}
		}
		try {
			P2PConnect.setCurrent_state(P2P_STATE_NONE);

			P2PConnect.setMode(P2PValue.VideoMode.VIDEO_MODE_SD);
			P2PConnect.setNumber(1);

			MusicManger.getInstance().stop();
			MusicManger.getInstance().stopVibrate();

			Intent refreshContans = new Intent();
			refreshContans
					.setAction(Constants.Action.ACTION_REFRESH_NEARLY_TELL);
			MyApp.app.sendBroadcast(refreshContans);

			Intent reject = new Intent();
			reject.putExtra("msg", msg);
			reject.setAction(Constants.P2P.P2P_REJECT);
			mContext.sendBroadcast(reject);
		} catch (Exception e) {
			Log.e(TAG, "vReject:error");
		}
		Log.e(TAG, "vReject:end");
	}

	public static synchronized void vAccept() {
		// TODO Auto-generated method stub
		Log.e(TAG, "vAccept");
		MusicManger.getInstance().stop();
		MusicManger.getInstance().stopVibrate();

		Intent accept = new Intent();
		accept.setAction(Constants.P2P.P2P_ACCEPT);
		mContext.sendBroadcast(accept);
	}

	public static synchronized void vConnectReady() {
		// TODO Auto-generated method stub
		Log.e(TAG, "vConnectReady");
		if (current_state != P2P_STATE_READY) {
			P2PConnect.setCurrent_state(P2P_STATE_READY);
			Intent ready = new Intent();
			ready.setAction(Constants.P2P.P2P_READY);
			mContext.sendBroadcast(ready);
		}
	}

	
	public static synchronized void vAllarming(int id, int type,
			boolean isSupport, int group, int item) {
		// TODO Auto-generated method stub
		Log.e("my", "vAllarming:" + isAlarming + " " + id + " " + type);
		if (!isAlarming) {
			long time = SharedPreferencesManager.getInstance()
					.getIgnoreAlarmTime(mContext);
			int time_interval = SharedPreferencesManager.getInstance()
					.getAlarmTimeInterval(mContext);

			if (null == NpcCommon.mThreeNum || "".equals(NpcCommon.mThreeNum)) {
				return;
			}

			List<AlarmMask> list = DataManager.findAlarmMaskByActiveUser(
					mContext, NpcCommon.mThreeNum);
			for (AlarmMask alarmMask : list) {
				if (id == Integer.parseInt(alarmMask.deviceId)) {
					return;
				}
			}

			if ((System.currentTimeMillis() - time) < (1000 * time_interval)) {
				return;
			}
			// if(current_state==P2P_STATE_CALLING&&Integer.parseInt(current_call_id)==id){
			// return;
			// }
			// if(current_state==P2P_STATE_READY&&Integer.parseInt(current_call_id)==id){
			// return;
			// }

			Contact contact = FList.getInstance().isContact(String.valueOf(id));
			if (contact == null) {
			
			} else {				
				
				Intent alarm = new Intent();
				alarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				alarm.setClass(mContext, AlarmActivity.class);
				alarm.putExtra("alarm_id", id);
				alarm.putExtra("alarm_type", type);
				alarm.putExtra("isSupport", isSupport);
				alarm.putExtra("group", group);
				alarm.putExtra("item", item);
				mContext.startActivity(alarm);
				isAlarming = true;
			}

		}
	}

	public static synchronized void vEndAllarm() {
		// TODO Auto-generated method stub
		isAlarming = false;
	}

}

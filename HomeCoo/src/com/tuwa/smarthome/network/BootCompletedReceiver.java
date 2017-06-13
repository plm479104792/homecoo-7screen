package com.tuwa.smarthome.network;

import com.tuwa.smarthome.activity.HomeActivity;
import com.tuwa.smarthome.activity.SceneQuickActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {  

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {  
            System.out.println("检测到开机启动，去启动服务");
            Log.i("343", "检测到开机启动，去启动服务");
            Intent newIntent = new Intent(context, StartService.class);  
            context.startService(newIntent);  
            
            //七寸屏开机自启动
            Intent startIntent = new Intent(context,SceneQuickActivity.class); 
            startIntent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent); 
        }  
	}  
}  

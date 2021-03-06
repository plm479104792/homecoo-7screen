package com.tuwa.smarthome.activity;

import java.util.List;

import object.dbnewgo.client.MainActivity;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.adapter.DevWidetypeAdapter;
import com.tuwa.smarthome.entity.DevWidetype;
import com.tuwa.smarthome.global.SystemValue;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTouch;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class CameraKindsActivity extends BaseActivity {
	//SharedPreferences共享数据
	 SharedPreferences preferences;
	 SharedPreferences.Editor editor;
		 
	 @Bind(R.id.tv_head_submit)  TextView tvExit;
	 @Bind(R.id.tv_head_back) TextView tvBack;
	 @Bind(R.id.tv_head_title) TextView tvTitle;
	 @Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;

	  
	 @Bind(R.id.gv_camera_kind) GridView gvCameraKind;
	 @Bind(R.id.rg_tab_navi) RadioGroup rg_navi_tab;
	 
	 private List<DevWidetype>  cameraKindList;
	 
		//手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
		float x1 = 0;
		float x2 = 0;
		float y1 = 0;
		float y2 = 0;
	 
	 private Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
		    switch(msg.what){
		   		 case 0x122:
//					Intent in = new Intent(CameraKindsActivity.this, AddCameraActivity.class);
//				    editor.putBoolean("firstflag", true);
//				    editor.putString("userid",SystemValue.gatewayid); //共享userid
//		      		editor.commit(); 
//					startActivity(in);
				 break;
		   		 case 0x123:
		   			Intent duijangIntent=new Intent(CameraKindsActivity.this,MainActivity.class);
		        	duijangIntent.putExtra("userid",SystemValue.gatewayid);
		        	System.out.println("++===camerakinds==usrid===="+SystemValue.userid);
					startActivity(duijangIntent);
		   	     break;
		    }
			}
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_kinds);
		ButterKnife.bind(CameraKindsActivity.this);//注解工具声明
		
		// 获取只能被本应用程序读、写的SharedPreferences对象
		 preferences = getSharedPreferences("tuwa",Context.MODE_PRIVATE);
		 editor = preferences.edit();
		
		tvExit.setText("退出");
		tvBack.setVisibility(View.INVISIBLE);
		tvTitle.setText("摄像机");
		
		cameraKindList=SystemValue.getCameraKindList();
		gvCameraKind.setAdapter(new DevWidetypeAdapter(CameraKindsActivity.this,cameraKindList));
		
	}
	
	@OnItemClick(R.id.gv_camera_kind)
	public void devwideClick(int position){
		DevWidetype camerakind=cameraKindList.get(position);
		int devwideId=camerakind.getWidetypeId();
		switch (devwideId) {
		case SystemValue.yuntai:
			
//			Intent intent = new Intent();
//			intent.setClass(CameraKindsActivity.this, BridgeService.class);
//			startService(intent);
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
//						
//						Message msg = new Message();
//						msg.what=0x122;
//						mHandler.sendMessage(msg);
////						Thread.sleep(2000);
//					} catch (Exception e) {
//					}
//				}
//			}).start();
			
			
			Intent intent = new Intent();  //Itent就是我们要发送的内容
 		    intent.putExtra("username",SystemValue.cameraPhone);  
 		    intent.putExtra("passwd",SystemValue.cameraPwd);
 		    intent.setAction("com.homecoolink.loginFlag");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
 		    sendBroadcast(intent);   //发送广播
 		    System.out.println("发送摄像机登录广播..."); 
			
			break;
        case SystemValue.qiangji:
			
			break;
        case SystemValue.duijiang:
			
        	Intent duijangIntent=new Intent(CameraKindsActivity.this,MainActivity.class);
        	duijangIntent.putExtra("userid",SystemValue.gatewayid);
        	System.out.println("++===camerakinds==usrid===="+SystemValue.userid);
			startActivity(duijangIntent);
			break;
     
		default:
			break;
		}
	}
	
	
	/***返回***/
	@OnTouch(R.id.tv_head_back)
	public boolean back() {
		Intent intent=new Intent(CameraKindsActivity.this,HomeActivity.class);
		startActivity(intent);		
		finish();
		return false;
	}
	/***退出系统***/
    @OnClick(R.id.tv_head_submit)
    public void systemExit(){
    	initExitDialog();
    }
 
    
    /***空间***/
    @OnCheckedChanged(R.id.rb_navi_space)
    public void  spaceDeviceShow(){
    	Intent intent=new Intent(CameraKindsActivity.this,SpaceDevicesActivity.class);
		startActivity(intent);		
		finish();
    }
    
	@OnCheckedChanged(R.id.rb_navi_scene)
	public void naviSceneClick(){
		Intent sceneIntent=new Intent(CameraKindsActivity.this,SceneModelActivity.class);
 		startActivity(sceneIntent);		
 		finish();
	}

 	@OnClick(R.id.tv_bottom_network)
	public void networkSwitchClick(){
		
	}
    
    /***防区管理***/
 	@OnCheckedChanged(R.id.rb_navi_alert)
 	public void DefenceAreaClick(){
 		Intent sceneIntent=new Intent(CameraKindsActivity.this,DefenceAreaActivity.class);
 		startActivity(sceneIntent);		
 		finish();
 	}
 	
    /***系统设置***/
    @OnCheckedChanged(R.id.rb_navi_set)
    public void  systemSet(){
    	Intent intent=new Intent(CameraKindsActivity.this,SetActivity.class);
		startActivity(intent);		
		finish();
    }

	
	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initDatas() {
		// TODO Auto-generated method stub
	}

	/*****************左右滑动事件监听******************/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//继承了Activity的onTouchEvent方法，直接监听点击事件
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			//当手指按下的时候
			x1 = event.getX();
			y1 = event.getY();
		}
		if(event.getAction() == MotionEvent.ACTION_UP) {
			//当手指离开的时候
			x2 = event.getX();
			y2 = event.getY();
		    if(x1 - x2 > 50) { //向左滑动
		    	System.out.println("==《《《=向左滑动====");
		    	Intent intent=new Intent(CameraKindsActivity.this,HomeActivity.class);
				startActivity(intent);		
				finish();
				overridePendingTransition(R.anim.out_to_left,
						R.anim.in_from_right);
			} else if(x2 - x1 > 50) {  //向右滑动
				System.out.println("===向右滑动==》》》==");
				Intent intent=new Intent(CameraKindsActivity.this,SceneQuickActivity.class);
				startActivity(intent);		
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);
			}
		}
		return super.onTouchEvent(event);
	}
}

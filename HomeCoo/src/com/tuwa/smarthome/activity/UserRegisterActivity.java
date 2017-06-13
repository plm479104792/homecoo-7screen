package com.tuwa.smarthome.activity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.fragment.PhoneFragment;
import com.tuwa.smarthome.fragment.RegisterInfoFragment;
import com.tuwa.smarthome.fragment.VerifyFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

public class UserRegisterActivity extends BaseActivity {
	private static final String FRAGMENT_PHONE = "fragment_phone";
	private static final String FRAGMENT_VERIFY = "fragment_verify";
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_register);
		
		ButterKnife.bind(UserRegisterActivity.this);
		
		
		showPhoneFragment();  
		
	}
	
	/**
	 * 手机号注册页面
	 */
	public void showPhoneFragment(){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction bt = fm.beginTransaction();
		bt.replace(R.id.fl_register,new PhoneFragment(),FRAGMENT_PHONE);
		bt.commitAllowingStateLoss();  //销毁activity之前不保存数据
	}
	
	/**
	 *手机验证码页面
	 */
	public void showVerifyFragment(){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction bt = fm.beginTransaction();
		bt.replace(R.id.fl_register,new VerifyFragment(),FRAGMENT_VERIFY);
		bt.commitAllowingStateLoss();
	}
	
	/**
	 * 註冊用戶信息详情页
	 */
	public void showRegisterInfoFragment(){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction bt = fm.beginTransaction();
		bt.replace(R.id.fl_register,new RegisterInfoFragment());
		bt.commitAllowingStateLoss();
	}
	
	
	
	
//	@OnClick(R.id.tv_head_back)
	public void back(){
		finish();
		overridePendingTransition(R.anim.out_to_right,
			  	 R.anim.in_from_left);
		
	}
//    //提交按钮点击事件
//	@OnClick(R.id.tv_head_submit)
//	public void submit(){
//		 username = etUserName.getText().toString().trim();
//		 password = etPassWord.getText().toString().trim();
//		 comfirmpwd = etConfirmPwd.getText().toString().trim();
//		 phoneno = etPhoneNo.getText().toString().trim();
//		 email = etEmail.getText().toString().trim();
//		 realname = etRealName.getText().toString().trim();
//		 address = etAddress.getText().toString().trim();
//		if (username.length()<6||username.length()>18) {
//			showCustomToast("账号应为6至18位数字或字母");
//			etUserName.requestFocus();
//			
//		}else if (!VerifyUtils.matchAccount(username)){
//			showCustomToast("账号格式错误");
//			etUserName.requestFocus();
//			
//		}else if (!(new UserDao(this).isUseableLogincode(username))) {
//			showCustomToast("该账号已被注册，请输入新账号！");
//			etUserName.setText(null);
//			etUserName.requestFocus();
//		}else if (password.equals("")) {
//			showCustomToast("请填写密码");
//			
//		}else if (password.length() <6||password.length()>18) {
//			showCustomToast("密码格式错误");
//		}else if (comfirmpwd.equals("")) {
//			showCustomToast("请再次输入密码");
//			etConfirmPwd.requestFocus();
//			
//		}else if (!password.equals(comfirmpwd)) {
//			etConfirmPwd.setText(null);
//			etConfirmPwd.requestFocus();
//			showCustomToast("两次输入的密码不一致");
//		} else if (phoneno.equals("")) {
//			showCustomToast("请输入手机号码");
//			etPhoneNo.requestFocus();
//		}else if (!VerifyUtils.isMobileNO(phoneno)) {
//			showCustomToast("手机号码格式错误");
//			etPhoneNo.requestFocus();
//		}else if(email.equals("")){
//			showCustomToast("请输入邮箱地址");
//			etEmail.requestFocus();
//		}else if (!VerifyUtils.matchEmail(email)) {
//			showCustomToast("邮箱地址格式错误");
//			etEmail.requestFocus();
//		}else {
//			
//			User user=new User();
//		
//			//新的user
//			user.setLogincode(username);
//			user.setPassword(password);
//			user.setPhoneno(phoneno);
////			user.setEmail(email);
////			user.setRealname(realname);
////			user.setAddress(address);
//			
//			registerUserToServer(user);
//			
//		}	
//
//	}
//	
//	/**
//	 * 从外网注册用户
//	 * @param user
//	 */
//	private void registerUserToServer(final User user) {
//		  
////		    String methodName = "insertOrUpdateUser";   
////		  	String url = WebService.WEB_USER_SERVERURL;
////	    	HashMap<String,Object> properties=new HashMap<String,Object>();
////	    	
////	    	Gson gson = new Gson();
////		    String jsonUser = gson.toJson(user); 
////		    
////		    System.out.println("==jsonUser=="+jsonUser);
////	    	properties.put("user",jsonUser);
//		
//	    	//新user注册接口
//		    String methodName = "register";   
//		  	String url = WebService.WEB_USER_SERVERURL;
//	    	HashMap<String,Object> properties=new HashMap<String,Object>();
//	    	properties.put("logincode",user.getLogincode());
//	    	properties.put("password",user.getPassword());
//	    	properties.put("phone",user.getPhoneno());
//	    	 
//	    	WebService.callWebService(url, methodName, properties,new WebServiceCallBack() {  
//	            
//	          //WebService接口返回的数据回调到这个方法中  
//	          @Override  
//	          public void callBack(TranObject tranObject) { 
//	        	  switch (tranObject.getTranType()) {
//					case NETMSG:   
//						int netstatue=(Integer) tranObject.getObject();
//						if (NetValue.NONET==netstatue) {  //本地连接失败
//					
//							showCustomToast("远程登录失败，请检查网络！");
//						}
//	        	       break;
//					case  DEVMSG:
//						//根据外网返回结果
//						   outerNetLoginVerify(tranObject,user);   //外网用户校验
//						   
//						break;
//	        	  }
//	          }
//
//		
//	      }); 
//		
//	}
//	
//	private void outerNetLoginVerify(TranObject tranObject,User user) {
//		  SoapObject soapObject=(SoapObject) tranObject.getObject();
//		  String rstJson = soapObject.getProperty(0).toString();  //转换为jsonArray串
//		  
//		  System.out.println("=====register返回========"+rstJson);
//		try {
//			  //获取每一个JsonObject对象
//			    org.json.JSONObject myjObject =new JSONObject(rstJson);
//			    String result=myjObject.getString("success");
//			   
//			   if (result.equals("true")) {
//				   String message=myjObject.getString("message");
//				    int userid=myjObject.getInt("userid");
//				    User muser=new UserDao(UserRegisterActivity.this).getUser(username, password);
//				    if (muser==null) {  //服务器端注册，同步用户信息到本地数据库
//				    	user.setUserid(userid);
//				    	new UserDao(this).add(user);
//					}
//				    showCustomToast(message);
//				    finish();
//					overridePendingTransition(R.anim.out_to_right,
//							R.anim.in_from_left);
//					
//			   }else {
//				   String errors=myjObject.getString("errors");
//				   showCustomToast(errors); 
//			   } 
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	
	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initDatas() {
		// TODO Auto-generated method stub
		
	}
	
//	public void showLoadingDialog(String str){
//		showLoadingDialog(str);
//	}
//	
	

}

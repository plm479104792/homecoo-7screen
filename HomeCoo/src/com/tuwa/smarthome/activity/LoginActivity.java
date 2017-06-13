package com.tuwa.smarthome.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import cn.jpush.android.api.JPushInterface;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.dao.GateWayDao;
import com.tuwa.smarthome.dao.UserDao;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.MusicPlayService;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.MD5Security16;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import com.umeng.update.UmengUpdateAgent;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LoginActivity extends BaseActivity {   
	// activity绑定service
	private SocketService socketService;
	private String isMemory = "";// isMemory变量用来判断SharedPreferences有没有数据，包括上面的YES和NO
	static String YES = "yes";
	static String NO = "no";
	static String strname, strpwd;
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
  
	public int loginResult; // 用户登录结果
	public int netFlag = 0; // 0为内网，1是外网
	public int userid; // 用户id

	@Bind(R.id.bt_userRegister)
	Button mBtnRegister;
	@Bind(R.id.tv_login_exit)
	TextView tvLoginExit;
	@Bind(R.id.cb_remenmer_pwd)
	CheckBox cbRemenberPwd;
	private TextView btgetbackPwd;
	private User mpUser;

	private Context mContext;
	private Button mBtnLogin;
	private EditText mEtUserName, mEtUserPwd;
	private ImageView ivDrop;
	private PopupWindow mPopup;
	private ListView lvUserList; // 待选用户名列表控件
	private List<String> mUserList; // 待选用户列表

	private String username, password, pwdMd5;
	private Handler serviceHandler;  
	private String strRePasswd;
	

	/* 辅线程动态刷新页面 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x121:
				dismissLoadingDialog();
				String contentStr=(String) msg.obj;
				showLoadingDialog(contentStr);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mContext = this;
		ButterKnife.bind(LoginActivity.this);

		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getSharedPreferences("tuwa", Context.MODE_PRIVATE);
		editor = preferences.edit();

		// 友盟更新检测
		UmengUpdateAgent.update(this);

		// Activity和service绑定
		Intent service = new Intent(LoginActivity.this, SocketService.class);
		bindService(service, conn, Context.BIND_AUTO_CREATE);

		initViews();
		initDatas();


		isMemory = preferences.getString("isMemory", NO);
		// 进入界面时，这个if用来判断SharedPreferences里面name和password有没有数据，有的话则直接打在EditText上面
		if (isMemory.equals(YES)) {

			strname = preferences.getString("name", "");
			strpwd = preferences.getString("password", "");
			mEtUserName.setText(strname);
			mEtUserPwd.setText(strpwd);
			cbRemenberPwd.setChecked(true);
		}
		editor.putString(strname, mEtUserName.toString());
		editor.putString(strpwd, mEtUserPwd.toString());
		editor.putBoolean("SCENE_FLAG", false);  //清空情景设置标识
		editor.commit();

		setUserListsFromSql(); // 从数据库加载待选用户列表

//		// 获取位置服务
//		locationService = ((LocweatherApplication) getApplication()).locationService;
//		// 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
//		locationService.registerListener(mListener);
//		// 注册监听
//		int type = getIntent().getIntExtra("from", 0);
//		if (type == 0) {
//			locationService.setLocationOption(locationService
//					.getDefaultLocationClientOption());
//		} else if (type == 1) {
//			locationService.setLocationOption(locationService.getOption());
//		}
//		locationService.start();
		
//		//极光初始化    20161215
//		JPushInterface.setDebugMode(true);
//		JPushInterface.init(this);
//		JPushInterface.setLatestNotificationNumber(getApplicationContext(),3);// 保留多少条通知数  
		
//	    HandlerThread serviceThread = new HandlerThread("SentHandlerThread");
//	    serviceThread.start();//创建一个HandlerThread并启动它
//        serviceHandler=new Handler(serviceThread.getLooper());  //发送数据线程
		
//		//这里单独启动一个音乐播放的service 用于jpushreceive的时候调用
//		SystemValue.musicPlayService =new MusicPlayService();
//		SystemValue.musicPlayService.onCreate();
//		
		
	}

	// 从数据库加载待选用户列表
	private void setUserListsFromSql() {
		lvUserList = new ListView(this);

		mUserList = new ArrayList<String>();
		mUserList = new UserDao(LoginActivity.this).getUsernameAll();

		lvUserList.setAdapter(new DropAdapter());

		lvUserList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mEtUserName.setText(mUserList.get(position));
				mPopup.dismiss();
			}
		});
	}

	@Override
	protected void onStart() {
		System.out.println("onStart()");
		// TODO Auto-generated method stub
		super.onStart();
	}

	// 获取SocketService实例对象
	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 返回一个SocketService对象
			socketService = ((SocketService.SocketBinder) service).getService();
		}
	};

	@Override
	protected void initViews() {
		mEtUserName = (EditText) findViewById(R.id.et_username);
		mEtUserPwd = (EditText) findViewById(R.id.et_userpwd);
		mBtnLogin = (Button) findViewById(R.id.bt_userLogin);
		btgetbackPwd = (TextView) findViewById(R.id.bt_getback_pwd);

		ivDrop = (ImageView) findViewById(R.id.iv_drop);
		mBtnLogin.setOnClickListener(loginOnClickListener);
		btgetbackPwd.setOnClickListener(getbackPwdOnClickListener);

		ivDrop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDropDown();
			}
		});
	}

	@Override
	protected void initDatas() {

	}

	//用户注册
	@OnClick(R.id.bt_userRegister)
	public void userRegister() {

		Intent intent = new Intent(mContext, UserRegisterActivity.class);
		startActivity(intent);
	}
    //用户登录
	private OnClickListener loginOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			username = mEtUserName.getText().toString().trim();
			password = mEtUserPwd.getText().toString().trim();
			if (username.equals("")) {
				showCustomToast("请填写账号");
				mEtUserName.requestFocus();
			} else if (password.equals("")) {
				showCustomToast("请填写密码");
			} else if (!VerifyUtils.isMobileNO(username)) { // 校验作为手机号的用户名
				showCustomToast("账号格式错误");
				mEtUserName.requestFocus();
			} else if (mEtUserPwd.length() < 5) {
				showCustomToast("密码格式错误");
			} else {
				
				remenberNameAndPassword(); // 记住用户名和密码
                // 对密码进行加密
				try {
					pwdMd5 = MD5Security16.md5_16(password).toUpperCase();
					
					switchNetworkToLogin(username, pwdMd5);
					
					NetValue.callbackflag = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	};

	private OnClickListener getMsgOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Message msg = new Message();
			msg.what = 0x126; // 验证码输入框显示
			handler.sendMessage(msg);
		}
	};

	private OnClickListener getbackPwdOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			getMsgFromServerDialog("");
		}
	};

	/**
	 * 内外网登录切换
	 * 
	 * @param username
	 * @param password
	 */
	private void switchNetworkToLogin(String username, String password) {
		
		mpUser = new UserDao(LoginActivity.this).getUser(username, password);
		if (mpUser == null) { // 本地没有注册账号的信息
			outernetUserLogin(username, pwdMd5); // 此为新手机没有原来的账号信息，外网登录验证
		} else {
			String phonenum = mpUser.getPhonenum();
			SystemValue.phonenum = phonenum;
			SystemValue.user = mpUser; // 将user对象保存为全局变量
			
			String gatewayNo=mpUser.getGatewayNo();

			if (!VerifyUtils.isEmpty(gatewayNo)) {
				SystemValue.gatewayid = gatewayNo; // 用户已经绑定网关，赋值为全局变量
				Gateway mgateWay = new GateWayDao(LoginActivity.this).getGatewayByGatewayNo(gatewayNo); //获取该手机号下对应的第一个网关
				if (mgateWay == null) { // 没有绑定网关
					outernetUserLogin(username, pwdMd5); // 此为新手机没有原来的账号信息，外网登录验证
					System.out.println("===没找到用户对应的网关！===");
				} else {
					intranetUserLogin(mgateWay); // 获取到用户名对应的网关id,内网登录验证
					System.out.println("===找到了用户对应的网关！===");
				}
			}else {
				outernetUserLogin(username, pwdMd5); // 此为新手机没有原来的账号信息，外网登录验证
			}
		}
	}

	/**
	 * 内网用户连接登录
	 * 
	 * @param gatewayId
	 *            网关id
	 * @param mpassword
	 *            网关密码
	 */
	private void intranetUserLogin(Gateway mgateWay) {

		System.out.println("【login】当前用户的网关编号为==="+SystemValue.gatewayid);
		NetValue.LOCAL_IP = mgateWay.getGatewayIp();
		NetValue.authdata = mgateWay.getGatewayPwd();

		if (!NetValue.socketauthen) {
			// 网关id和ip存在，建立socket连接,发送新认证
			socketService.socketConnect(NetValue.LOCAL_IP);
		}
		
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x121;
		msg.obj="正在使用本地登录,请稍后...";
		handler.sendMessage(msg);
		

		socketService.callSocket(new SocketCallBack() {
			@Override
			public void callBack(TranObject tranObject) {
				    mBtnLogin.setClickable(true);   //恢复按钮点击

				switch (tranObject.getTranType()) {
				case NETMSG:
					
					int netstatue = (Integer) tranObject.getObject();
					if ((NetValue.NONET == netstatue)) { // 本地连接失败
						loginResult = NetValue.IN_FAILURE; // 保存登录方式为内网失败
						dismissLoadingDialog();
						
						outernetUserLogin(username, pwdMd5); // 内网登录失败后切换到外网登录
						System.out.println("========内网网络不通,自动切换到外网登录=====");
					}
					break;
				case DEVMSG:
					
					intranetLoginVerify(tranObject); // 内网用户登录校验
				default:
					break;
				}
			}

		});

	}

	/**
	 * 内网用户登录校验
	 * 
	 * @param tranObject
	 */
	private void intranetLoginVerify(TranObject tranObject) {
		SocketPacket socketPacket = (SocketPacket) tranObject.getObject();
        if(socketPacket.getDataType()==NetValue.DATA_ACK_AUTH){
			if (socketPacket.getData().equals("0")) { // 验证成功返回0
				dismissLoadingDialog();
				NetValue.netFlag = NetValue.INTRANET; // 保存当前的网络为内网
	
				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			} else {
				dismissLoadingDialog();
				System.out.println("网关返回"+"datatype"+socketPacket.getDataType()+"data:"+socketPacket.getData());
				ToastUtils.showToast(LoginActivity.this, "本地验证失败，请检查用户名和密码！", 1000);
			}
	   }
	}
	


	/**
	 * 用户外网连接登录
	 * 
	 * @param name
	 *            用户名
	 * @param pwd
	 *            密码
	 */
	private void outernetUserLogin(final String phonenum, String pwd) {

		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x121;
		msg.obj="正在切换远程登录,请稍后...";
		handler.sendMessage(msg);
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phonenum);
		params.addBodyParameter("password", pwd);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.LOGIN_URL, params,
			new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
				     mBtnLogin.setClickable(true);
					
					 loginResult=NetValue.OUT_FAILURE; //保存登录方式为内网失败
					 dismissLoadingDialog();
					 ToastUtils.showToast(LoginActivity.this,"远程登录失败，请检查网络！",SystemValue.MSG_TIME);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// 根据返回值校验登录是否成功
					Gson gson=new Gson();
					ResultMessage message=gson.fromJson(arg0.result, ResultMessage.class);
					
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
	                    Gson guser=new Gson();
	                    System.out.println("========="+message.getObject());
						try {
							User muser=guser.fromJson((String)message.getObject(),User.class);
						
							new UserDao(LoginActivity.this).insertOrUpdateUser(muser); //添加新用户
						
							NetValue.netFlag =NetValue.OUTERNET;
							SystemValue.phonenum=phonenum;   //用户验证通过，手机号赋为全局变量
							if (mpUser!=null) {//本地数据库存在用户信息
								 String gatewayid=mpUser.getGatewayNo(); //网关是在本地添加的，所以本地有网关的信息
								 if(!VerifyUtils.isEmpty(gatewayid)){
									 SystemValue.gatewayid=gatewayid;
									//请求所有设备的最新状态
									if(SystemValue.deviceSysnFlag==false){
									    //手机端从网关认证通过，向网关请求所有设备状态
									}
								 }else{
//									 ToastUtils.showToast(LoginActivity.this,"请添加网关！",1000);
									 System.out.println(">>>该用户在服务器端未绑定网关！===");
								 }
							}
							
							dismissLoadingDialog();//finish,LoginActivity前一定要关闭Dilog
							Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
							startActivity(intent);
							finish();
						} catch (JsonSyntaxException e) {
							System.out.println("json解析错误！");
							e.printStackTrace();
						}
						
					}else {
						loginResult = NetValue.OUT_FAILURE; // 外网登录失败
						dismissLoadingDialog();
						ToastUtils.showToast(LoginActivity.this, message.getMessageInfo(), 1000);
					}
				}
			});
	}

	
	// 获取短信验证对话框
	public void getMsgFromServerDialog(String phone) {

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_dialog_identify,
				(ViewGroup) findViewById(R.id.dialog));
		final EditText etPhone = (EditText) layout
				.findViewById(R.id.et_phoneno);
		final TextView tvGetMsg = (TextView) layout
				.findViewById(R.id.tv_getMsg);
		final EditText etIdentifyCode = (EditText) layout
				.findViewById(R.id.et_identify_code);
		final TableRow tr_identify = (TableRow) layout
				.findViewById(R.id.tr_identify_code);
		etPhone.setText(phone);
		tvGetMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phoneno = etPhone.getText().toString().trim();

				if (phoneno.equals("")) {
					etPhone.requestFocus();
					showCustomToast("手机号码不能为空");
				} else if (!VerifyUtils.isMobileNO(phoneno)) {
					showCustomToast("手机号码格式错误");
					etPhone.requestFocus();
				} else { // 空间列表和用户关联表中都没有此设备

					// 填写手机号发送到服务器
//					requestMsgFromServer(phoneno, tr_identify);
					getRePasswordCodeFromServer(phoneno);   //获取验证码
				}

			}
		});

		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("短信验证").setView(layout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 点击确定对话框不消失
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String phoneno = etPhone.getText().toString().trim();
				String identifycode = etIdentifyCode.getText().toString()
						.trim();
				// 发送验证码到服务器
				getVertifyResultFromServer(phoneno,identifycode);

				try { // 点击确定对话框消失
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).show();
	}
	
	/**
	 * 请求服务器发送重置密码的验证码到对应的手机
	 * @param phonenum
	 */
	public void getRePasswordCodeFromServer(String phonenum) {
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phonenum);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.GET_REPWDCODE_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 访问网络失败
						ToastUtils.showToast(LoginActivity.this, "访问服务器失败，请检查网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						System.out.println("服务器返回" + arg0.result);

						Gson gson = new Gson();
						try {
							ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
							if (message != null) {
								if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
									ToastUtils.showToast(LoginActivity.this, message.getMessageInfo(), 1000);
								} else {
									ToastUtils.showToast(LoginActivity.this, message.getMessageInfo(), 1000);
								}
							}
						} catch (Exception e) {
							System.out.println("json解析异常");
						}
					}
				});
	}

	
	/**
	 * 从服务器获取校验验证码结果
	 * @param phone
	 */
	public void getVertifyResultFromServer(final String phonenum,String verifycode) {
		
		RequestParams params=new RequestParams();
		params.addBodyParameter("phonenum",phonenum);
		params.addBodyParameter("identifyCode",verifycode);
		params.addBodyParameter("smsCodeType","2");  //重置验证码的类型为2

		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.POST, NetValue.VERIFY_CODE_URL,params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissLoadingDialog();
				System.out.println("===访问服务器失败返回的值为==="+arg1);
				showCustomToast(getResources().getString(R.string.error_network));
			
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				dismissLoadingDialog();
				Gson gson=new Gson();
				ResultMessage message=gson.fromJson(arg0.result, ResultMessage.class);
			   if (message!=null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						ToastUtils.showToast(LoginActivity.this, message.getMessageInfo(), 1000);
						// 重置密码对话框
						resetPasswordToServerDialog(phonenum);
					}else {
						ToastUtils.showToast(LoginActivity.this, message.getMessageInfo(), 1000); // 本对话框一直显示
						getMsgFromServerDialog(phonenum);
						
					}
			   }
			}
		});
	}
	

	// 重置密码对话框
	public void resetPasswordToServerDialog(final String phone) {

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_dialog_resetpasswd,
				(ViewGroup) findViewById(R.id.dialog));
		final EditText etLoginCode = (EditText) layout.findViewById(R.id.et_logincode);
		final EditText etPassword = (EditText) layout.findViewById(R.id.et_passwd);
		final EditText etRePassword = (EditText) layout.findViewById(R.id.et_repasswd);
		System.out.println("=====当前用户名为======="+phone);
		etLoginCode.setText(phone);
		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("密码重置").setView(layout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 点击确定对话框不消失
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String phonenum = etLoginCode.getText().toString().trim();
				String password = etPassword.getText().toString().trim();
				String comfirmpwd = etRePassword.getText().toString().trim();

				if (!VerifyUtils.isMobileNO(phonenum)) {
					showCustomToast("账号格式错误");
					etLoginCode.requestFocus();
				} else if (password.length() < 5) {
					showCustomToast("密码格式错误");
					etPassword.requestFocus();
				} else if (!password.equals(comfirmpwd)) {
					etRePassword.setText(null);
					etRePassword.requestFocus();
					showCustomToast("两次输入的密码不一致");
				} else {
					try {// 点击确定对话框消失
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 重置密码到服务器
					resetPasswordToServer(phonenum,password, dialog);
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).show();
	}

	// 重置用户名和密码到服务器
	private void resetPasswordToServer(final String phonenum,String passwd, final DialogInterface dialog) {
		
		 // 对密码进行加密
		
		try {  
			strRePasswd = MD5Security16.md5_16(passwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RequestParams params=new RequestParams();
		params.addBodyParameter("phonenum",phonenum);
		params.addBodyParameter("newPassword",strRePasswd);
		
		HttpUtils  utils=new HttpUtils();
		
		utils.send(HttpMethod.POST, NetValue.UPDATE_REPWDCODE_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 访问网络失败
						showCustomToast(getResources().getString(R.string.error_network));
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						System.out.println("服务器返回" + arg0.result);
						
						Gson gson=new Gson();
						ResultMessage message=gson.fromJson(arg0.result, ResultMessage.class);
					   if (message!=null) {
							if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
								ToastUtils.showToast(LoginActivity.this, message.getMessageInfo(), 1000);
								//重置密码成功后，修改本地密码
								User muser=new User();
								muser.setPhonenum(phonenum);
								muser.setPassword(strRePasswd);
								new UserDao(LoginActivity.this).updateUserByPhonenum(muser);

								try {// 验证码正确，点击确定对话框消失
									Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
									field.setAccessible(true);
									field.set(dialog, true);
								} catch (Exception e) {
									e.printStackTrace();
								}

							}else {
								ToastUtils.showToast(LoginActivity.this, message.getMessageInfo(), 1000); // 本对话框一直显示
							}
					   }
						
					}
				});
		
	}

	// remenber方法用于判断是否记住密码，checkBox1选中时，提取出EditText里面的内容，放到SharedPreferences里面的name和password中
	public void remenberNameAndPassword() {
		if (cbRemenberPwd.isChecked()) {
			if (preferences == null) {
				preferences = getSharedPreferences("tuwa", MODE_PRIVATE);
			}
			Editor edit = preferences.edit();
			edit.putString("name", mEtUserName.getText().toString());
			edit.putString("password", mEtUserPwd.getText().toString());
			edit.putString("isMemory", YES);
			edit.commit();
		} else if (!cbRemenberPwd.isChecked()) {
			if (preferences == null) {
				preferences = getSharedPreferences("tuwa", MODE_PRIVATE);
			}
			Editor edit = preferences.edit();
			edit.putString("isMemory", NO);
			edit.commit();
		}
	}

	// 用户名下拉选择框
	protected void showDropDown() {
		if (mPopup == null) {
			mPopup = new PopupWindow(lvUserList, mEtUserName.getWidth(), 200,
					true);
			mPopup.setBackgroundDrawable(new ColorDrawable());
		}

		mPopup.showAsDropDown(mEtUserName);
	}

	class DropAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mUserList.size();
		}

		@Override
		public String getItem(int position) {
			return mUserList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_userdrop, null);
				holder = new ViewHolder();
				holder.tvContent = (TextView) convertView
						.findViewById(R.id.tv_content);
				holder.ivDelete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvContent.setText(getItem(position));
			// holder.ivDelete.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// mUserListList.remove(position);
			// DropAdapterAdapter.this.notifyDataSetChanged();
			// }
			// });

			return convertView;
		}
	}

	static class ViewHolder {
		public TextView tvContent;
		public ImageView ivDelete;
	}

	// 退出点击事件
	@OnClick(R.id.tv_login_exit)
	public void exit() {
		initExitDialog();
	}

	@Override
	protected void onDestroy() {
		unbindService(conn);
		super.onDestroy(); // 注意先后
	}
	
//	/*****
//	 * @see copy funtion to you project
//	 *      定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
//	 * 
//	 */
//	private BDLocationListener mListener = new BDLocationListener() {
//
//		public void onReceiveLocation(BDLocation location) {
//			// System.out.println("mListener>>>>>>>======");
//			// TODO Auto-generated method stub
//			if (null != location
//					&& location.getLocType() != BDLocation.TypeServerError) {
//				StringBuffer sb = new StringBuffer(256);
//				sb.append("time : ");
//				/**
//				 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//				 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//				 */
//				sb.append(location.getTime()); // 当前时间年月日 时分秒
//				sb.append("\nerror code : ");
//				sb.append(location.getLocType());
//				sb.append("\nlatitude : ");
//				sb.append(location.getLatitude());
//				sb.append("\nlontitude : ");
//				sb.append(location.getLongitude());
//				sb.append("\nradius : ");
//				sb.append(location.getRadius());
//				sb.append("\nCountryCode : ");
//				sb.append(location.getCountryCode());
//				sb.append("\nCountry : ");
//				sb.append(location.getCountry());
//				sb.append("\ncitycode : ");
//				sb.append(location.getCityCode());
//				sb.append("\ncity : ");
//				sb.append(location.getCity()); // 当前所在城市
//				sb.append("\nDistrict : ");
//				sb.append(location.getDistrict());
//				sb.append("\nStreet : ");
//				sb.append(location.getStreet());
//				sb.append("\naddr : ");
//				sb.append(location.getAddrStr());
//				sb.append("\nDescribe: ");
//				sb.append(location.getLocationDescribe());
//				sb.append("\nDirection(not all devices have value): ");
//				sb.append(location.getDirection());
//				sb.append("\nPoi: ");
//				if (location.getPoiList() != null
//						&& !location.getPoiList().isEmpty()) {
//					for (int i = 0; i < location.getPoiList().size(); i++) {
//						Poi poi = (Poi) location.getPoiList().get(i);
//						sb.append(poi.getName() + ";");
//					}
//				}
//				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//					sb.append("\nspeed : ");
//					sb.append(location.getSpeed());// 单位：km/h
//					sb.append("\nsatellite : ");
//					sb.append(location.getSatelliteNumber());
//					sb.append("\nheight : ");
//					sb.append(location.getAltitude());// 单位：米
//					sb.append("\ndescribe : ");
//					sb.append("gps定位成功");
//				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//					// 运营商信息
//					sb.append("\noperationers : ");
//					sb.append(location.getOperators());
//					sb.append("\ndescribe : ");
//					sb.append("网络定位成功");
//				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//					sb.append("\ndescribe : ");
//					sb.append("离线定位成功，离线定位结果也是有效的");
//				} else if (location.getLocType() == BDLocation.TypeServerError) {
//					sb.append("\ndescribe : ");
//					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//					sb.append("\ndescribe : ");
//					sb.append("网络不同导致定位失败，请检查网络是否通畅");
//				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//					sb.append("\ndescribe : ");
//					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//				}
//				// logMsg(sb.toString());
//				locationService.stop();
//
//				System.out.println("=========" + location.getCity());
//
//				if (location.getCity() != null) {
//					city = location.getCity().substring(0,
//							location.getCity().length() - 1);
//					System.out.println("=======city===" + city);
//				}
//			}
//		}
//
//	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		JPushInterface.onPause(this);
	}
	

}

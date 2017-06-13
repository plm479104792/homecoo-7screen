package com.tuwa.smarthome.fragment;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.UserRegisterActivity;
import com.tuwa.smarthome.dao.UserDao;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.DataConvertUtil;
import com.tuwa.smarthome.util.MD5Security16;
import com.tuwa.smarthome.util.VerifyUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @类名 HomeFragment
 * @创建者 ppa
 * @创建时间 2016-3-29
 * @描述 TODO
 */
public class RegisterInfoFragment extends BaseFragment implements
		OnClickListener {

	private String username, password, comfirmpwd, phoneno, email, realname,
			address;
	private EditText etUserName, etPassWord, etConfirmPwd, etEmail, etAddress,
			etRealName;
	private TextView tvSubmit, tvBack, tvTitle;
	private User mUser;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_register, null);
		tvBack = (TextView) view.findViewById(R.id.tv_head_back);
		tvSubmit = (TextView) view.findViewById(R.id.tv_head_submit);

		etUserName = (EditText) view.findViewById(R.id.et_username);
		etPassWord = (EditText) view.findViewById(R.id.et_password);
		etConfirmPwd = (EditText) view.findViewById(R.id.et_confirmpwd);
	
		etEmail = (EditText) view.findViewById(R.id.et_email);
		etRealName = (EditText) view.findViewById(R.id.et_realname);
		etAddress = (EditText) view.findViewById(R.id.et_address);

		tvTitle = (TextView) view.findViewById(R.id.tv_head_title);
		tvTitle.setText("注册信息");

		return view;
	}

	@Override
	public void initData() {
		etUserName.setText(SystemValue.phonenum); // 用户名默认填充手机号

		tvBack.setOnClickListener(this);
		tvSubmit.setOnClickListener(this); // 提交

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_head_back:
			UserRegisterActivity activity = (UserRegisterActivity) mActivity;
			activity.showVerifyFragment();
			break;

		case R.id.tv_head_submit:

			submitInfoToServer();

			break;
		default:
			break;
		}

	}

	/**
	 * 校验并提交注册信息到服务器
	 */
	public void submitInfoToServer() {
		username = etUserName.getText().toString().trim();
		password = etPassWord.getText().toString().trim();
		comfirmpwd = etConfirmPwd.getText().toString().trim();
	
		email = etEmail.getText().toString().trim();
		realname = etRealName.getText().toString().trim();
		address = etAddress.getText().toString().trim();
		if (username.length() < 6 || username.length() > 18) {
			showCustomToast("账号应为6至18位数字或字母");
			etUserName.requestFocus();
		} else if (!VerifyUtils.matchAccount(username)) {
			showCustomToast("账号格式错误");
			etUserName.requestFocus();
		} else if (password.equals("")) {
			showCustomToast("请填写密码");

		} else if (password.length() < 6 || password.length() > 18) {
			showCustomToast("密码格式错误");
		} else if (comfirmpwd.equals("")) {
			showCustomToast("请再次输入密码");
			etConfirmPwd.requestFocus();

		} else if (!password.equals(comfirmpwd)) {
			etConfirmPwd.setText(null);
			etConfirmPwd.requestFocus();
			showCustomToast("两次输入的密码不一致");
	
		} else if (email.equals("")) {
			showCustomToast("请输入邮箱地址");
			etEmail.requestFocus();
		} else if (!VerifyUtils.matchEmail(email)) {
			showCustomToast("邮箱地址格式错误");
			etEmail.requestFocus();
		} else {
			
			String pwdMd5 ="";   //先加密后再传到服务器
			try {
				pwdMd5=MD5Security16.md5_16(password);
				System.out.println("====md5===="+pwdMd5);
			} catch (Exception e) {
				e.printStackTrace();
			}

			User user = new User();
			user.setPhonenum(SystemValue.phonenum);
			user.setPassword(pwdMd5);
			user.setGatewayNo("");
			user.setRealname(realname);
			user.setEmail(email);
			user.setAddress(address);

			mUser = user;
			registerUserToServer(user);

		}

	}

	/**
	 * 发送用户信息到服务器
	 * 
	 * @param user
	 */
	private void registerUserToServer(User user) {
		Gson gson = new Gson();
		String juser = gson.toJson(user);

		RequestParams params = new RequestParams();
		params.addBodyParameter("userJson", juser);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.USERREGISTER_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						showCustomToast(mActivity.getResources().getString(R.string.error_network));
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
						// 备注：result success/fail ;展示 message

						if (message != null) {
							if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
//							
								new UserDao(mActivity).insertOrUpdateUser(mUser);

								UserRegisterActivity activity1 = (UserRegisterActivity) mActivity;
								activity1.back();
							} else {
								showCustomToast(message.getMessageInfo());
							}
						}

					}
				});

	}

}

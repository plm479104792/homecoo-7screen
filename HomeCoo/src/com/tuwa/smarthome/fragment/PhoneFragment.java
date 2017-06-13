package com.tuwa.smarthome.fragment;

import java.security.PublicKey;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.SimpleListDialog;
import com.tuwa.smarthome.SimpleListDialog.onSimpleListItemClickListener;
import com.tuwa.smarthome.activity.UserRegisterActivity;
import com.tuwa.smarthome.adapter.SimpleListDialogAdapter;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.TextUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import com.tuwa.smarthome.view.HandyTextView;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
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
public class PhoneFragment extends BaseFragment implements OnClickListener,
		TextWatcher, onSimpleListItemClickListener {

	private static final String DEFAULT_PHONE = "+8618679451786";
	private TextView tvTitle, tvBack, tvSubmit;
	private HandyTextView mHtvAreaCode;
	private EditText mEtPhone;

	private SimpleListDialog mSimpleListDialog;
	private String[] mCountryCodes;
	private String mAreaCode = "+86";
	private boolean mIsChange = true;
	private HandyTextView mHtvNotice;
	private HandyTextView mHtvNote;
	private String mPhoneNum;
	private UserRegisterActivity activity;
	private boolean loadflag;
	private boolean resultlflag;

	@Override
	public View initView() {
		activity = (UserRegisterActivity) mActivity;
		View view = View.inflate(mActivity, R.layout.fragment_phone, null);
		tvTitle = (TextView) view.findViewById(R.id.tv_head_title);
		tvBack = (TextView) view.findViewById(R.id.tv_head_back);
		tvSubmit = (TextView) view.findViewById(R.id.tv_head_submit);
		tvTitle.setText("注册账号");
		tvBack.setText("返回");
		tvSubmit.setText("下一步");

		mHtvAreaCode = (HandyTextView) view
				.findViewById(R.id.reg_phone_htv_areacode);
		mEtPhone = (EditText) view.findViewById(R.id.reg_phone_et_phone);
		mHtvNotice = (HandyTextView) view
				.findViewById(R.id.reg_phone_htv_notice);
		mHtvNote = (HandyTextView) view.findViewById(R.id.reg_phone_htv_note);
		TextUtils.addHyperlinks(mHtvNote, 17, 21, this); // 用户账号下划线强调

		return view;
	}

	@Override
	public void initData() {
		tvBack.setOnClickListener(this);
		tvSubmit.setOnClickListener(this);
		mHtvAreaCode.setOnClickListener(this);
		mEtPhone.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_head_back:

			activity.back();
			break;

		case R.id.tv_head_submit:

			submit();

			break;
		case R.id.reg_phone_htv_areacode:
			mCountryCodes = mActivity.getResources().getStringArray(
					R.array.country_codes);
			mSimpleListDialog = new SimpleListDialog(mActivity);
			mSimpleListDialog.setTitle("选择国家区号");
			mSimpleListDialog.setTitleLineVisibility(View.GONE);
			mSimpleListDialog.setAdapter(new SimpleListDialogAdapter(mActivity,
					mCountryCodes));
			mSimpleListDialog.setOnSimpleListItemClickListener(this);
			mSimpleListDialog.show();
			break;

		}

	}

	/**
	 * 提交手机号校验
	 */
	public void submit() {
		mPhoneNum = mEtPhone.getText().toString().trim();

		if (mPhoneNum.equals("")) {
			showCustomToast("请输入手机号码");
			mEtPhone.requestFocus();
		} else if (!VerifyUtils.isMobileNO(mPhoneNum)) {
			showCustomToast("手机号码格式错误");
			mEtPhone.requestFocus();
		} else {
			loadflag = false;
			VerifyPhoneNoTask task = new VerifyPhoneNoTask();
			task.execute(mPhoneNum);
		}
	}

	/**
	 * 从服务器校验手机号是否已被注册
	 */
	class VerifyPhoneNoTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!loadflag) {
				showLoadingDialog("正在验证手机号，请稍后...");
				loadflag = true;
			}

		}

		@Override
		protected Boolean doInBackground(String... params) {

			getVertifyCodeFromServer(mPhoneNum);

			return true;
		}

		// @Override
		// protected void onPostExecute(Boolean result) {
		// super.onPostExecute(result);
		// dismissLoadingDialog();
		// if (result) {
		// SystemValue.phoneno=mPhoneNum;
		// mIsChange = false;
		// activity.showVerifyFragment();
		// } else {
		// showCustomToast("手机号码不可用或已被注册");
		// }
		// }

	}

	@Override
	public void onItemClick(int position) {
		String text = TextUtils.getCountryCodeBracketsInfo(
				mCountryCodes[position], mAreaCode);
		mAreaCode = text;
		mHtvAreaCode.setText(text);

	}

	/**
	 * 发送手机号给服务器发送验证码到对应的手机号
	 * 
	 * @param phone
	 */
	public void getVertifyCodeFromServer(String phoneno) {
		resultlflag = false;

		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phoneno);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.GETCODE_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dismissLoadingDialog();
						SystemValue.phonenum = mPhoneNum;
						mIsChange = false;
						// 访问网络失败
						showCustomToast(getResources().getString(
								R.string.error_network));
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						System.out.println("服务器返回" + arg0.result);

						Gson gson = new Gson();
						try {
							ResultMessage message = gson.fromJson(arg0.result,
									ResultMessage.class);
							if (message != null) {
								if (message.getResult().equals(
										NetValue.SUCCESS_MESSAGE)) {
									resultlflag = true;
									dismissLoadingDialog();
									SystemValue.phonenum = mPhoneNum;
									mIsChange = false;
									activity.showVerifyFragment();
								} else {
									showCustomToast(message.getMessageInfo());
								}
							}
						} catch (Exception e) {
							System.out.println("json解析异常");
						}
					}
				});
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mIsChange = true;
		if (s.toString().length() > 0) {
			mHtvNotice.setVisibility(View.VISIBLE);
			char[] chars = s.toString().toCharArray();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < chars.length; i++) {
				if (i % 4 == 2) {
					buffer.append(chars[i] + "  ");
				} else {
					buffer.append(chars[i]);
				}
			}
			mHtvNotice.setText(buffer.toString());
		} else {
			mHtvNotice.setVisibility(View.GONE);
		}

	}

}

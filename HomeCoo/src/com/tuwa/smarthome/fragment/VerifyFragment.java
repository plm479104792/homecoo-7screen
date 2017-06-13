package com.tuwa.smarthome.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseDialog;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.UserRegisterActivity;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.TextUtils;
import com.tuwa.smarthome.view.HandyTextView;




/**
 * @类名    HomeFragment
 * @创建者   ppa
 * @创建时间 2016-3-29
 * @描述   TODO
 */
public class VerifyFragment extends BaseFragment implements OnClickListener 
                     ,TextWatcher{
	private TextView tvTitle,tvBack,tvSubmit;
	
	private HandyTextView mHtvPhoneNumber;
	private EditText mEtVerifyCode;
	private Button mBtnResend;
	private HandyTextView mHtvNoCode;
	private BaseDialog mBaseDialog;

	private static final String PROMPT = "验证码已经发送到* ";
	private static final String DEFAULT_VALIDATE_CODE = "123123";

	private boolean mIsChange = true;
	private String mVerifyCode;

	private int mReSendTime = 60;

	private boolean verifyFlag;   //服务器返回的验证结果

	
	@Override
	public View initView() {
		handler.sendEmptyMessage(0);
		View view = View.inflate(mActivity, R.layout.fragment_verify, null);
		tvTitle = (TextView) view.findViewById(R.id.tv_head_title);
		tvBack = (TextView) view.findViewById(R.id.tv_head_back);
		tvSubmit = (TextView) view.findViewById(R.id.tv_head_submit);
		
		tvTitle.setText("注册账号");
		tvBack.setText("返回");
		tvSubmit.setText("下一步");
		
		mHtvPhoneNumber = (HandyTextView) view.findViewById(R.id.reg_verify_htv_phonenumber);
		mHtvPhoneNumber.setText(PROMPT + SystemValue.phonenum);
		mEtVerifyCode = (EditText) view.findViewById(R.id.reg_verify_et_verifycode);
		mBtnResend = (Button) view.findViewById(R.id.reg_verify_btn_resend);
		mBtnResend.setEnabled(false);
		mBtnResend.setText("重发(60)");
		mHtvNoCode = (HandyTextView) view.findViewById(R.id.reg_verify_htv_nocode);
		TextUtils.addUnderlineText(mActivity, mHtvNoCode, 0, mHtvNoCode
				.getText().toString().length());
		return view;
	}

	@Override
	public void initData() {
//		handler.sendEmptyMessage(0); //进入页面开始倒计时
		tvBack.setOnClickListener(this);
		tvSubmit.setOnClickListener(this);
		
		mBtnResend.setOnClickListener(this);
		mHtvNoCode.setOnClickListener(this);
		mEtVerifyCode.addTextChangedListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_head_back:
			UserRegisterActivity activity1 = (UserRegisterActivity) mActivity;
			activity1.showPhoneFragment();
			break;
			
		case R.id.tv_head_submit:
			if (validate()) {
				verifyTask = new VerifyCodeTask();
				verifyTask.execute(mVerifyCode);
			}
			
			break;
			
		case R.id.reg_verify_btn_resend:
			handler.sendEmptyMessage(0);
			break;
		}
		
	}
	
	public boolean validate() {
		if (mEtVerifyCode.equals(null)) {
			showCustomToast("请输入验证码");
			mEtVerifyCode.requestFocus();
			return false;
		}
		mVerifyCode = mEtVerifyCode.getText().toString().trim();
		return true;
	}
	
	class VerifyCodeTask extends AsyncTask<String, Void, Boolean>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingDialog("正在验证,请稍后...");
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
				
			getVertifyResultFromServer(params[0]);
			
			return true;
		}
		
//		@Override
//		protected void onPostExecute(Boolean result) {
//			super.onPostExecute(result);
////			dismissLoadingDialog();
////			if (result) {
////				mIsChange = false;
////				UserRegisterActivity activity = (UserRegisterActivity) mActivity;
////				activity.showRegisterInfoFragment();
////			} else {
////				mBaseDialog = BaseDialog.getDialog(mActivity, "提示", "验证码错误",
////						"确认", new DialogInterface.OnClickListener() {
////
////							@Override
////							public void onClick(DialogInterface dialog,
////									int which) {
////								mEtVerifyCode.requestFocus();
////								dialog.dismiss();
////							}
////
////						});
////				mBaseDialog.show();
////			}
//		}
		
	}
	
	/**
	 * 从服务器获取校验验证码结果
	 * @param phone
	 */
	public void getVertifyResultFromServer(String verifycode) {
		verifyFlag = false;
		
		RequestParams params=new RequestParams("utf-8");
		params.addBodyParameter("phonenum",SystemValue.phonenum);
		params.addBodyParameter("identifyCode",verifycode);
		params.addBodyParameter("smsCodeType","1");

		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.POST, NetValue.VERIFY_CODE_URL,params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissLoadingDialog();
				System.out.println("===访问服务器失败返回的值为==="+arg1);
				showCustomToast(mActivity.getResources().getString(R.string.error_network));
			
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				dismissLoadingDialog();
				Gson gson=new Gson();
				ResultMessage message=gson.fromJson(arg0.result, ResultMessage.class);
			   if (message!=null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						mIsChange = false;
						UserRegisterActivity activity = (UserRegisterActivity) mActivity;
						activity.showRegisterInfoFragment();
					}else {

						mBaseDialog = BaseDialog.getDialog(mActivity, "提示", "验证码错误",
								"确认", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mEtVerifyCode.requestFocus();
										dialog.dismiss();
									}

								});
						mBaseDialog.show();
					}
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
	}
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mReSendTime > 1) {
				mReSendTime--;
				mBtnResend.setEnabled(false);
				mBtnResend.setText("重发(" + mReSendTime + ")");
				handler.sendEmptyMessageDelayed(0, 1000);
			} else {
				mReSendTime = 60;
				mBtnResend.setEnabled(true);
				mBtnResend.setText("重    发");
				
				getVertifyResultFromServer(mVerifyCode); //重新从服务器请求验证码
				
			}
		}
	};

	private VerifyCodeTask verifyTask;

}

package com.tuwa.smarthome.fragment;

import com.tuwa.smarthome.FlippingLoadingDialog;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.UserRegisterActivity;
import com.tuwa.smarthome.view.HandyTextView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * @类名    BaseFragment
 * @创建者   ppa
 * @创建时间 2016-3-21
 * @描述   TODO
 */
public abstract class BaseFragment extends Fragment {

	public UserRegisterActivity mActivity;
	protected FlippingLoadingDialog mLoadingDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mActivity = (UserRegisterActivity) getActivity();
    	mLoadingDialog = new FlippingLoadingDialog(mActivity, "请求提交中");
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return initView();
	}
	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	initData();
    }
   
	
	public abstract View  initView();
	
	public abstract void initData();
	
	
	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(mActivity).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(mActivity);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}
	
	protected void showLoadingDialog(String text) {
		if (text != null) {
			mLoadingDialog.setText(text);
		}
		mLoadingDialog.show();
	}
	
	protected void dismissLoadingDialog() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}
	
//	protected void initExitDialog() {
//		mBackDialog = BaseDialog.getDialog(mActivity, "提示",
//				"确认要退出HomeCoo系统吗？", "确认", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						
//					}
//				}, "取消", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//					}
//				});
//		mBackDialog.show();
//	}
	
}

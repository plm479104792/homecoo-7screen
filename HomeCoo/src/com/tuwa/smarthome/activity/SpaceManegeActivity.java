package com.tuwa.smarthome.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.homecoolink.global.MyApp;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.BaseDialog;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.R.id;
import com.tuwa.smarthome.dao.SpaceDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.MD5Security16;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class SpaceManegeActivity extends BaseActivity implements OnClickListener {
	private BaseDialog mBackDialog;
	protected Context mContext;
	private SpaceAdapter spaceAdpter = null;
	private TextView tvAddSpace,tvSpaceToServer,tvSpaceToLocal;
	private int j = 0; // 正在执行同步的空间指针

	private List<Space> spacetypeList = new ArrayList<Space>();
	private List<Space> localSpaceList = new ArrayList<Space>();
	private Drawable drawable,drawLeftServer,drawLeftLocal;
	private int versionResult;   //房间版本信息标识
	private Version pVersion;

	@Bind(R.id.tv_head_submit)
	TextView tvSubmit;
	@Bind(R.id.tv_head_back)
	TextView tvBack;
	@Bind(R.id.tv_head_title)
	TextView tvtitle;
	@Bind(R.id.gv_spacelist)
	GridView gvSpace;

	/* 辅线程动态刷新页面 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x129:
				if(spacetypeList!=null){  //list不为null后填充适配器
					spaceAdpter = new SpaceAdapter();
					gvSpace.setAdapter(spaceAdpter);
				}
			 break;
			case 0x100:  
				if(versionResult==0){
					tvSpaceToServer.setCompoundDrawables(drawLeftServer,null,null,null);
					tvSpaceToLocal.setCompoundDrawables(drawLeftLocal,null,null,null);
				}else if(versionResult==1){
					//提示从服务器同步到本地，刷新右边显示NEW！
					tvSpaceToLocal.setCompoundDrawables(null,null,drawable,null);
				}else if(versionResult==2){
					//提示同步到服务器，刷新右边显示NEW！
					tvSpaceToServer.setCompoundDrawables(null,null,drawable,null);
				}
			 break;
//			case 0x101:  
//				//提示从服务器同步到本地，刷新右边显示NEW！
//				tvSpaceToLocal.setCompoundDrawables(null,null,drawable,null);
//			 break;
//			case 0x102:
//				//提示同步到服务器，刷新右边显示NEW！
//				tvSpaceToServer.setCompoundDrawables(null,null,drawable,null);
//			 break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_space_manege);
		
		ButterKnife.bind(SpaceManegeActivity.this);// 注解工具声明

		tvSubmit.setVisibility(View.INVISIBLE);
		tvtitle.setText("空间管理");

		initViews();
		
		sysnSpaceFromServer();
		
//		initDatas();  //连不上服务器从本地加载      
      
	}
	
	/**
	 * 从服务器对比版本信息
	 */
	private void checkVersionFromServer() {
		String phonenum=SystemValue.phonenum;
	    int versionType=SystemValue.VERSION_SPACE;
	    pVersion = new VersionDao(SpaceManegeActivity.this)
		        .getVersionByPhonenumAndVersionType(phonenum,versionType);
		if(pVersion==null){
			pVersion = new Version();
			pVersion.setPhonenum(SystemValue.phonenum);
			pVersion.setGatewayNo(SystemValue.gatewayid);
			pVersion.setVersionType(versionType);
			long date=System.currentTimeMillis();//获取当前时间  
			pVersion.setUpdateTime(date);		
		}
	
			Gson gversion = new Gson();
			String strVersion=gversion.toJson(pVersion);
		
			RequestParams params = new RequestParams();
			params.addBodyParameter("versionJson",strVersion);

			HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
			utils.send(HttpMethod.POST, NetValue.CHECK_VERSION_URL, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							ToastUtils.showToast(SpaceManegeActivity.this, "访问服务器失败，请检查网络连接",SystemValue.MSG_TIME);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Gson gson = new Gson();
							ResultMessage message = gson.fromJson(arg0.result,
									ResultMessage.class);
							if (message != null) {
								if (message.getResult().equals(
										NetValue.SUCCESS_MESSAGE)) {
									System.out.println("检查版本"+ message.getMessageInfo());
									System.out.println("匹配结果"+ message.getObject());
									
									versionResult = Integer.valueOf((String)message.getObject());
									// 辅助线程更新页面
									Message msg = new Message();
									msg.what = 0x100;   //服务器和手机版本相同
									handler.sendMessage(msg);
									
								} else {
									showCustomToast(message.getMessageInfo());
								}
							}
						}
					});
	}
	


	// 空间适配器
	public class SpaceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return spacetypeList.size();
		}

		@Override
		public Object getItem(int position) {
			return spacetypeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if (view != null) {
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(SpaceManegeActivity.this,
						R.layout.item_space, null);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}

			Space space = spacetypeList.get(position);
			holder.tvSpacename.setText(space.getSpaceName());
			holder.imSetting.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					roomSetDialog(position); // 设置功能对话框
				}
			});

			return view;
		}

		class ViewHolder {
			@Bind(R.id.tv_list_spacename)
			TextView tvSpacename;
			@Bind(R.id.im_setting)
			ImageView imSetting;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
		}
	}

	/** 删除对话框 **/
	protected void showDeleteDialog(final int position) {
		final Space deleteSpace = spacetypeList.get(position);
		final String spacename = deleteSpace.getSpaceName();
		mBackDialog = BaseDialog.getDialog(SpaceManegeActivity.this, "提示",
				"确认要删除房间" + spacename + "吗？", "确认",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						deleteSpaceToServer(deleteSpace,position);  //先从服务器删除，返回success,删除本地数据库

						mBackDialog.dismiss();

					}
				}, "取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		mBackDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mBackDialog.show();
	}

	/**
	 * 从服务器删除空间
	 * 
	 * @param space
	 */
	private void deleteSpaceToServer(final Space space, final int position) {
		String spaceNo=space.getSpaceNo();
		String phonenum=space.getPhoneNum();
		Gson gson = new Gson();
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("SpaceNo",spaceNo); 
		params.addBodyParameter("phoneNum",phonenum);
		
		System.out.println("===删除空间==="+spaceNo+phonenum);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST,NetValue.SPACE_DELETE_URL, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ToastUtils.showToast(SpaceManegeActivity.this, "访问服务器失败，请检查网络连接",SystemValue.MSG_TIME);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Gson gson = new Gson();
				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
				if (message != null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						
						 spacetypeList.remove(position);
						 spaceAdpter.notifyDataSetInvalidated();
						 showCustomToast("删除空间" +space.getSpaceName()+"成功");
						 //辅助线程更新页面
						 Message msg=new Message();
						 msg.what=0x129;
						 handler.sendMessage(msg);
						 //从数据库中删除空间
						 new SpaceDao(SpaceManegeActivity.this).deleteSpaceBySpaceNo(space);
	
					} else {
						showCustomToast(message.getMessageInfo());
					}
				}
			}
		});

	}

	/**
	 * 房间设置对话框
	 */
	private void roomSetDialog(final int position) {
		final String items[] = {"删除" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("空间设置"); // 设置标题
		// builder.setIcon(R.drawable.set_tool);//设置图标，图片id即可
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0:
					showDeleteDialog(position);
					break;
				}
			}
		});
		builder.create().show();
	}

	/************ 添加空间 **************/
	private void addSpaceDialog() {

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_dialog_tvandet,
				(ViewGroup) findViewById(R.id.dialog));
		final TextView tvSpaceName = (TextView) layout
				.findViewById(R.id.tv_dialog_name);
		tvSpaceName.setText("空间名称:");
		final EditText etSpaceName = (EditText) layout
				.findViewById(R.id.et_dialog_content);
		etSpaceName.setHint("请输入空间名称");
		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("添加空间").setView(layout);
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

				String mWgid = SystemValue.gatewayid;
				String spacename = etSpaceName.getText().toString();
				String strSpace = SystemValue.phonenum + mWgid + spacename; // 手机号+网关号+房间名，生产唯一phonespaceid
				String phoneSpaceId="";
				try {
					 phoneSpaceId=MD5Security16.md5_16(strSpace);
				} catch (Exception e) {
					e.printStackTrace();
				}	
				
				System.out.println("===转码后生成的phoneSpaceId为===" + phoneSpaceId);

				if (mWgid.equals(null)) {
					showCustomToast("请激活网关后，再添加空间！"); // 【注意】添加空间需激活网关
				} else if (spacename.equals("")) {
					etSpaceName.requestFocus();
					showCustomToast("请输入新空间名称");
				} else if (spacename.length() > 10) {
					showCustomToast("空间名称长度应小于5");
					etSpaceName.requestFocus();
				} else if (!(new SpaceDao(SpaceManegeActivity.this)
						.isUseableSpaceByWgid(phoneSpaceId))) {
					showCustomToast("该用户对应空间名称已存在！");
				} else {

					Space space = new Space();
					space.setSpaceNo(phoneSpaceId);
					space.setSpaceName(spacename);
					space.setGatewayNo(mWgid);
					space.setPhoneNum(SystemValue.phonenum);

					if (new SpaceDao(SpaceManegeActivity.this)
							.isUseableSpace(phoneSpaceId)) {
					
					
				    //	20160428无需通过外网直接将space加入到数据库
					    spacetypeList.add(space); // 添加到本地space缓存表
						showCustomToast("恭喜您添加新空间" + spacename + "成功");
						// 辅助线程更新页面
						Message msg = new Message();
						msg.what = 0x129;
						handler.sendMessage(msg);
						new SpaceDao(SpaceManegeActivity.this).add(space); // 新添space到数据库
						
				    	addNewSpaceToServer(space);  //添加房间信息到服务器
						
					}else {
						showCustomToast("该房间已经存在！");
					}

					try {// 点击确定对话框消失
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// 辅助线程更新页面
				Message msg = new Message();
				msg.what = 0x129;
				handler.sendMessage(msg);
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
	 * 添加空间到服务器
	 * 
	 * @param space
	 */
	private void addNewSpaceToServer(final Space space) {
		Gson gson = new Gson();
		String spaceJson = gson.toJson(space);
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("spaceJson", spaceJson);   //同步版本信息到服务器

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST,NetValue.SPACE_ADD_URL, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				initDatas();
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Gson gson = new Gson();
				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
				System.out.println("添加空间到服务器返回"+message.getResult());
				if (message != null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
//						spacetypeList.add(space); // 添加到本地space缓存表
//						showCustomToast("恭喜您添加新空间" +space.getSpaceName() + "成功");
						// 辅助线程更新页面
//						Message msg = new Message();
//						msg.what = 0x129;
//						handler.sendMessage(msg);
//						new SpaceDao(SpaceManegeActivity.this).add(space); // 新添space到数据库
						
					
						
					} else {
						showCustomToast(message.getMessageInfo());
					}
				}
			}
		});

	}

//	/************ 添加空间 **************/
//	private void showUpdateDialog(final int position) {
//
//		LayoutInflater inflater = getLayoutInflater();
//		View layout = inflater.inflate(R.layout.include_dialog_tvandet,
//				(ViewGroup) findViewById(R.id.dialog));
//		final TextView tvSpaceName = (TextView) layout
//				.findViewById(R.id.tv_dialog_name);
//		tvSpaceName.setText("空间名称:");
//		final EditText etSpaceName = (EditText) layout
//				.findViewById(R.id.et_dialog_content);
//		etSpaceName.setHint("请输入空间名称");
//		// 对话框的外部结构
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("修改空间").setView(layout);
//		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				// 点击确定对话框不消失
//				try {
//					Field field = dialog.getClass().getSuperclass()
//							.getDeclaredField("mShowing");
//					field.setAccessible(true);
//					field.set(dialog, false);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				String spacename = etSpaceName.getText().toString();
//				if (spacename.equals("")) {
//					etSpaceName.requestFocus();
//					showCustomToast("请输入新空间名称");
//				} else if (spacename.length() > 10) {
//					showCustomToast("空间名称长度应小于5");
//					etSpaceName.requestFocus();
//				} else if (!(new SpaceDao(SpaceManegeActivity.this)
//						.isUseableSpaceByWgid(spacename))) {
//					showCustomToast("该用户对应空间名称已存在！");
//				} else { // 空间列表和用户关联表中都没有此设备
//					String mWgid = SystemValue.gatewayid;
//					Space spaceType = spacetypeList.get(position);
//					spaceType.setSpaceName(spacename);
//
//					// spacetypeList.get(position).setSpacetypnename(spacename);
//					// //添加到本地space缓存表
//					// //辅助线程更新页面
//					// Message msg=new Message();
//					// msg.what=0x129;
//					// handler.sendMessage(msg);
//					// showCustomToast("恭喜您更新空间"
//					// +spaceType.getSpacetypnename()+"成功");
//					// new
//					// SpaceTypeDao(SpaceManegeActivity.this).update(spaceType);
//					// //根据spaceId,更新本地spacetype 数据库
//
//					// 更新空间到外网服务器
//					updateSpaceToServer(spaceType, position);
//
//					try {// 点击确定对话框消失
//						Field field = dialog.getClass().getSuperclass()
//								.getDeclaredField("mShowing");
//						field.setAccessible(true);
//						field.set(dialog, true);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				// 辅助线程更新页面
//				Message msg = new Message();
//				msg.what = 0x129;
//				handler.sendMessage(msg);
//			}
//
//		});
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//
//				try {
//					Field field = dialog.getClass().getSuperclass()
//							.getDeclaredField("mShowing");
//					field.setAccessible(true);
//					field.set(dialog, true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).show();
//	}

//	/**
//	 * 更新空间到服务器
//	 * 
//	 * @param spaceType
//	 */
//	private void updateSpaceToServer(final Space spaceType, final int position) {
//
//		String methodName = "saveOrUpdateSpaceType"; // 根据设备id更新设备状态
//		String url = WebService.WEB_SERVER_URL;
//		HashMap<String, Object> properties = new HashMap<String, Object>();
//
//		Gson gson = new Gson();
//		String jsonSpace = gson.toJson(spaceType);
//		properties.put("spacetype", jsonSpace);
//
//		WebService.callWebService(url, methodName, properties,
//				new WebServiceCallBack() {
//
//					// WebService接口返回的数据回调到这个方法中
//					@Override
//					public void callBack(TranObject tranObject) {
//						switch (tranObject.getTranType()) {
//						case NETMSG:
//							int netstatue = (Integer) tranObject.getObject();
//							if (NetValue.NONET == netstatue) {
//								showCustomToast("远程连接失败，请检查网络！");
//							}
//							break;
//						case DEVMSG:
//							SoapObject soapObject = (SoapObject) tranObject
//									.getObject();
//							String rstJson = soapObject.getProperty(0)
//									.toString(); // 转换为jsonArray串
//							System.out.println("=====返回=====" + rstJson);
//							if (rstJson.equals("success")) {
//								showCustomToast("恭喜您更新空间"
//										+ spaceType.getSpaceName() + "成功");
//								spacetypeList.get(position).setSpaceName(
//										spaceType.getSpaceName()); // 添加到本地space缓存表
//								// 辅助线程更新页面
//								Message msg = new Message();
//								msg.what = 0x129;
//								handler.sendMessage(msg);
//								new SpaceDao(SpaceManegeActivity.this)
//										.update(spaceType);
//							} else {
//								showCustomToast("从服务器更新空间失败！");
//							}
//							break;
//						}
//					}
//				});
//	}

	/**
	 * 同步本地网关
	 */
	// private void sysSpaceToServer() {
	// System.out.println("==空间同步，当前网关为==" + SystemValue.gatewayid);
	// // 根据userid从数据库加载网关
	// localSpaceList = new SpaceTypeDao(SpaceManegeActivity.this)
	// .getSpaceByGatewayId(SystemValue.gatewayid);
	// j = 0; // 每当新情景，指针重置为0
	// showLoadingDialog("正在同步网关信息...");
	//
	// timerhandler.post(timerrunnable);
	// }

//	Handler timerhandler = new Handler();
//	Runnable timerrunnable = new Runnable() {
//		@Override
//		public void run() {
//
//			themeExecuteLoop();
//			try {
//				Thread.sleep(1000); // 线程休眠1秒
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	};

//	/****** 循环发送情景表中的关联设备状态 *******/
//	private void themeExecuteLoop() {
//
//		if (j < localSpaceList.size()) {
//			Space space = localSpaceList.get(j);
//			addNewSpaceToServer(space);
//			j++;
//
//			timerhandler.post(timerrunnable);
//
//		} else {
//			/* 外网根据设备大类号加载网关信息 */
//			dismissLoadingDialog();
//		}
//	}


	@OnClick(R.id.tv_head_back)
	public void back() {
		finish();
	}

	@Override
	protected void initViews() {
		tvAddSpace = (TextView) findViewById(R.id.tv_addspace);
		tvSpaceToServer=(TextView) findViewById(R.id.tv_spacetoserver);
		tvSpaceToLocal=(TextView) findViewById(R.id.tv_spacetolocal);
		
		//动态更新NEW图标
		drawable = getResources().getDrawable(R.drawable.icon_new);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		drawLeftServer = getResources().getDrawable(R.drawable.leftmenu_toserver_sel);
		drawLeftServer.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		drawLeftLocal = getResources().getDrawable(R.drawable.leftmenu_downlocal_sel);
		drawLeftLocal.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		
		tvSpaceToServer.setCompoundDrawables(drawLeftServer,null,null,null);
		tvSpaceToLocal.setCompoundDrawables(drawLeftLocal,null,null,null);
		
		tvAddSpace.setOnClickListener(addSpaceOnClickListener);
        tvSpaceToServer.setOnClickListener(this);
        tvSpaceToLocal.setOnClickListener(this);
	}

	@Override
	protected void initDatas() {
		// 根据网关号从数据库加载空间列表
		String gatewayId = SystemValue.gatewayid;
		System.out.println("==spaceManege==gatewyaid==" + gatewayId);
		spacetypeList = new SpaceDao(SpaceManegeActivity.this)
				.getSpaceByPhonenum(SystemValue.phonenum);

		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);
	}

	private OnClickListener addSpaceOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			addSpaceDialog();
			System.out.println("====当前的userid======" + SystemValue.userid);
		}
	};



	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case id.tv_spacetoserver:
			if(versionResult==2){
				sysnSpaceToServer();
			}else{
				ToastUtils.showToast(SpaceManegeActivity.this,"服务器已经是最新数据！", 1000);
			}
			break;
		case id.tv_spacetolocal:
			if(versionResult==1){
				sysnSpaceFromServer();
			}else{
				ToastUtils.showToast(SpaceManegeActivity.this,"本地已经是最新版本！", 1000);
			}
			break;
		}
		
	}
   
	/**
	 * 从服务器同步所有的空间列表信息
	 */
	private void sysnSpaceFromServer() {
		System.out.println("手机号"+SystemValue.phonenum);
		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", SystemValue.phonenum);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.SPACE_FROM_SERVER_URL, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				initDatas();  //连不上服务器从本地加载
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Gson gson = new Gson();
				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
				if (message != null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						
						System.out.println("获取的房间列表的信息"+message.getObject());
						String strSpacelist=(String) message.getObject();
						
						if(!strSpacelist.equals("[]")){
							//更新数据库中的设备表
							spacetypeList=WebPacketUtil.parseSpacelistFromServer(strSpacelist);
							// 辅助线程更新页面
							Message msg = new Message();
							msg.what = 0x129;
							handler.sendMessage(msg);
						}
						
					} else {
						showCustomToast(message.getMessageInfo());
					}
				}
			}
		});
		
	}

	/**
	 * 同步房间列表信息到服务器
	 */
	private void sysnSpaceToServer() {		
		Gson gVersion = new Gson();
		String jVersion = gVersion.toJson(pVersion);
		
		Gson gson = new Gson();
		String jSpaceList = gson.toJson(spacetypeList);

		RequestParams params = new RequestParams();
		params.addBodyParameter("spaceJsonList", jSpaceList);
		params.addBodyParameter("versionJson", jVersion);   //同步版本信息到服务器

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.SPACE_TO_SERVER_URL, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				ToastUtils.showToast(SpaceManegeActivity.this, "访问服务器失败，请检查网络连接",SystemValue.MSG_TIME);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Gson gson = new Gson();
				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
				if (message != null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						//同步服务器成功，将这条记录插入到本地数据库version表中
						new VersionDao(SpaceManegeActivity.this).addorUpdateVerson(pVersion);
						showCustomToast(message.getMessageInfo());
						
						// 辅助线程更新页面
						versionResult=0;
						Message uiMsg = new Message();
						uiMsg.what = 0x100;
						handler.sendMessage(uiMsg);
					
					} else {
						showCustomToast(message.getMessageInfo());
					}
				}
			}
		});
	}
}

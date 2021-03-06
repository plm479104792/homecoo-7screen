package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.BaseDialog;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.SceneSetActivity.DevicesAllAdapter.ViewHolder;
import com.tuwa.smarthome.adapter.SpaceDeviceViewAdapter;
import com.tuwa.smarthome.dao.APPMusicDao;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.ThemeInfraDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.APPMusic;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Item;
import com.tuwa.smarthome.entity.Music;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeData;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.ThemeInfra;
import com.tuwa.smarthome.entity.ThemeMusic;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.DataConvertUtil;
import com.tuwa.smarthome.util.MusicJpush;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;
import et.song.remotestar.ActivityMain;

interface AdpterOnItemClick1 {
	void onApterClick(int which, int postion);
}

public class SceneSetActivity extends BaseActivity implements
		AdpterOnItemClick1, OnCheckedChangeListener {
	private SocketService socketService;
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;

	private TextView tvBack, tvTitle, tvSubmit;
	@SuppressWarnings("unused")
	private TextView tvRefreshThemestate;
	private RadioGroup rb_tab;
	private RadioButton rb_yaokong;
	private LayoutInflater inflater;
	private ViewGroup main;
	private List<ThemeMusic> musiclist = new ArrayList<ThemeMusic>();
	private MusicListAdapter musicListAdapter;
	private InfraTaskAdapter infraTaskAdapter;
	private List<ThemeInfra> themeInfralist = new ArrayList<ThemeInfra>();

	private View[] spaceViews; // 动态生成的view数组
	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private GridView[] mgvDevices;
	private List<Device> gvlistAll = new ArrayList<Device>(); // 在列表中显示的list
	private List<Device> devlist = new ArrayList<Device>();
	private int viewsNum;
	private int selectIndex;
	private boolean sceneSaveFlag = true;

	// 滑动缓存加载Listview
	private final int LOAD_STATE_IDLE = 0; // 没有在加载，并且list中还有数据没加载
	private final int LOAD_STATE_LOADING = 1; // 正在加载
	private final int LOAD_STATE_FINISH = 2; // list中数据全部加载完毕
	private int loadState = LOAD_STATE_IDLE; // 记录加载状态
	private int LIST_COUNT; // list中的总数目
	private static int EACH_COUNT = 10; // 每页加载的数目

	private String themeNo, themeDeviceNo; // 上级传感器对应的themeno
	private Theme ptheme; // 定义全局情景
	@SuppressWarnings("unused")
	private static String wgid = SystemValue.gatewayid;

	private List<ThemeDevice> themeLinkList = new ArrayList<ThemeDevice>();
	private DevicesAllAdapter deviceAdpter = new DevicesAllAdapter();
	final char ON = 1 + '0'; // 字符开
	final char OFF = 0 + '0'; // 字符关
	final char WinON = 5 + '0'; // 窗帘暂停
	final char WinPK = 6 + '0'; // 窗帘暂停
	final char WinOFF = 7 + '0'; // 窗帘暂停
	public String sLightVal; // 可调关的亮度
	private static char[] strStaArr = new char[4]; // 字符数组代表多路开关状态

	private InfraSetReceiver infraReceiver; // 红外设置的广播接收器
	private BaseDialog mBackDialog;
	// 同步情景到网关
	private List<Theme> themeList = new ArrayList<Theme>();
	private List<ThemeDevice> themeDeviceList = new ArrayList<ThemeDevice>();
	private int j = 0; // 正在同步网关的情景关联设备指针

	private static List<Music> mArrayList = new ArrayList<Music>();
	public static List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
	private static String themeMusic_songName;
	private static int MusicPosition = -1;
	private static boolean b = true;
	private static ThemeMusic Staticthememusic=null;
	private static APPThemeMusic Staticappthememusic=null;
	/* 辅线程动态刷新页面 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x129:
				LIST_COUNT = gvlistAll.size(); // 实例化所有设备的总数

				deviceAdpter = new DevicesAllAdapter();
				deviceAdpter.onListener(SceneSetActivity.this);
				mgvDevices[selectIndex].setAdapter(deviceAdpter);
				break;
			case 0x008:
				@SuppressWarnings("unchecked")
				ArrayList<Device> result = ((ArrayList<Device>) msg.obj);

				devlist.addAll(result);
				deviceAdpter.notifyDataSetChanged();
				break;
			case 0x010:
				showLoadingDialog("正在同步情景到网关！");
				break;
			case 0x130:
				musicListAdapter = new MusicListAdapter(SceneSetActivity.this,
						musiclist);
				mgvDevices[selectIndex].setAdapter(musicListAdapter);
				break;
			case 0x131: // 红外任务列表

				infraTaskAdapter = new InfraTaskAdapter();
				mgvDevices[selectIndex].setAdapter(infraTaskAdapter);
				break;
			case 1:
				/**
				 * 这里要做一个把收到的音乐列表 转码 之后存到sqlite上去 方便thememusic 调用
				 * */
				List<APPMusic> list = MusicUtil.GetAppMusicList(mArrayList);
				new APPMusicDao(SceneSetActivity.this).InsertAppMusic(list);

				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.bind(SceneSetActivity.this);// 注解工具声明

		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getSharedPreferences("tuwa", Context.MODE_PRIVATE);
		editor = preferences.edit();

		// Activity和service绑定
		Intent service = new Intent(SceneSetActivity.this, SocketService.class);
		bindService(service, conn, Context.BIND_AUTO_CREATE);

		inflater = getLayoutInflater();
		main = (ViewGroup) inflater.inflate(R.layout.activity_scene_set, null);

		initViews();
		initDatas();

		Intent intent = getIntent();
		themeNo = intent.getStringExtra("themeNo");
		themeDeviceNo = intent.getStringExtra("themeDevNo");
		String themename = intent.getStringExtra("themename");
		int themetype = intent.getIntExtra("themetype", 0);

		String themestate = intent.getStringExtra("themestate");

		ptheme = new Theme();
		ptheme.setThemeNo(themeNo);
		ptheme.setDeviceNo(themeDeviceNo);
		ptheme.setThemeType(themetype);
		ptheme.setThemeState(themestate);

		tvTitle.setText(themename + "联动配置");

		// 每个情景themeid,缓存加载一次对应的themeLink
		initMonitorSceneDevstate(); // 加载themeid下已经设置的themeLink
		viewsNum = 7;
		// 初始化SceneViewPages
		initSpaceDeviceView(inflater, viewsNum);

		setContentView(main);

		// 注册红外广播接收器
		infraReceiver = new InfraSetReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("send");
		filter.addAction("ACTION_SCENE_INFRA_SET");
		registerReceiver(infraReceiver, filter);
		showRemoteControlFragment(); // 初始化显示第一个页面
		MusicPosition = -1;
		
		if (NetValue.netFlag == NetValue.OUTERNET) {
			findmusicByWgid(!b);
			// 外网获取情景音乐
			getThemeMusic();
		} else if (NetValue.netFlag == NetValue.INTRANET) {
			// 内网获取情景音乐
			Log.i("inside","该情景的THEMEnO："+themeNo);
			getThemeMusicInside(themeNo);
		}

	}

	/*********** 当前情景下设备已经设定的状态 *****************/
	private void initMonitorSceneDevstate() {
		themeLinkList = new ThemeDeviceDao(SceneSetActivity.this)
				.findDevstateBythemeNo(themeNo);
	}

	/**************** 初始化SpaceDeviceViewPages ************************/
	private void initSpaceDeviceView(LayoutInflater inflater, int viewsnum) {
		pageViews = new ArrayList<View>();
		spaceViews = new View[viewsNum];
		mgvDevices = new GridView[6]; // switch,sock,window,weikong,yinyue,infra,

		spaceViews[0] = inflater.inflate(R.layout.item_link_scene, null);
		spaceViews[1] = inflater.inflate(R.layout.item_link_yaokong, null); // 红外
		mgvDevices[5] = (GridView) spaceViews[1]
				.findViewById(R.id.gv_infratask_list);
		spaceViews[2] = inflater.inflate(R.layout.item_space_devices, null); // 照明
		mgvDevices[0] = (GridView) spaceViews[2]
				.findViewById(R.id.gv_space_devices);
		spaceViews[3] = inflater.inflate(R.layout.item_space_devices, null); // 插座
		mgvDevices[1] = (GridView) spaceViews[3]
				.findViewById(R.id.gv_space_devices);
		spaceViews[4] = inflater.inflate(R.layout.item_space_devices, null); // 门窗
		mgvDevices[2] = (GridView) spaceViews[4]
				.findViewById(R.id.gv_space_devices);
		spaceViews[5] = inflater.inflate(R.layout.item_space_devices, null); // 微控
		mgvDevices[3] = (GridView) spaceViews[5]
				.findViewById(R.id.gv_space_devices);
		spaceViews[6] = inflater.inflate(R.layout.item_space_devices, null); // 音乐
		mgvDevices[4] = (GridView) spaceViews[6]
				.findViewById(R.id.gv_space_devices);
		for (int i = 0; i < viewsnum; i++) {
			pageViews.add(spaceViews[i]);
		}

		viewPager.setAdapter(new SpaceDeviceViewAdapter(pageViews));

	}

	/**
	 * 根据网关id加载音乐列表
	 */
	private void findmusicByWgid(final boolean b) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("gatewayNo", SystemValue.gatewayid);
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_GET_MUSIC_FROM_SERVER,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						showCustomToast(getResources().getString(
								R.string.error_network));
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ResultMessage msg = JSONObject.parseObject(arg0.result,
								ResultMessage.class);
						List<Music> list = JSONArray.parseArray(
								(String) msg.getObject(), Music.class);
						musiclist.clear();
						musiclist = MusicUtil.ToThemeMusicList(list);
						if (b) {
							Message msg1 = new Message();
							msg1.what = 0x130;
							handler.sendMessage(msg1);
						}
					}
				});

	}

	/**
	 * @Description:内网获取歌曲列表
	 * */
	public void findmusicBygatewayNo() {
		List<APPMusic> list = new APPMusicDao(SceneSetActivity.this)
				.GetAppMusicListByGatewayNo(SystemValue.gatewayid);
		musiclist = MusicUtil.TothememusicList(list);
		Message msg1 = new Message();
		msg1.what = 0x130;
		handler.sendMessage(msg1);
	}

	/*************** devlist数据适配器 *******************/
	public class DevicesAllAdapter extends BaseAdapter {
		private AdpterOnItemClick1 myAdpterOnclick;

		public void onListener(AdpterOnItemClick1 listener) {
			this.myAdpterOnclick = listener;
		}

		@Override
		public int getCount() {
			return devlist.size();
		}

		@Override
		public Object getItem(int position) {
			return devlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return -1;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if (view != null) {
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(SceneSetActivity.this,
						R.layout.item_scene_dev_set, null);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}
			Device devdto = devlist.get(position);

			initDeviceNameAndSite(holder.tvDevSite, holder.tvDevName, devdto);// 初始化房间名称和位置

			// 点击事件的按钮做标记
			holder.tgBtn1.setTag(position);
			holder.tgBtn2.setTag(position);
			holder.tgBtn3.setTag(position);
			holder.tgBtn4.setTag(position);
			holder.sbLight.setTag(position);
			holder.imBtnOn.setTag(position);
			holder.imBtnPause.setTag(position);
			holder.imBtnOff.setTag(position);
			holder.checkBox.setTag(position);

			final int fpostion = position;
			final ViewHolder fHolder = holder;

			// checkbox是否选中
			if (isChecked(devdto, position)) {
				holder.checkBox.setChecked(true);
			} else {
				holder.checkBox.setChecked(false);
			}
			// 设备按钮状态显示
			showViewByDevtype(holder, devdto); // 根据设备类型显示状态

			holder.tgBtn1.setOnClickListener(new ClickEvent() {
				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						if (!fHolder.tgBtn1.isChecked()) { // 当前状态为开，点击的时候发送off
							cmdControl(fHolder, position, 1, OFF);
						} else {
							cmdControl(fHolder, position, 1, ON);
						}
					} else {
						System.out.println("===myAdpterOnclick为空====");
					}

				}
			});

			holder.tgBtn2.setOnClickListener(new ClickEvent() {

				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						if (!fHolder.tgBtn2.isChecked()) {
							cmdControl(fHolder, position, 2, OFF);
						} else {
							cmdControl(fHolder, position, 2, ON);
						}
					}
				}
			});

			holder.tgBtn3.setOnClickListener(new ClickEvent() {

				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						if (!fHolder.tgBtn3.isChecked()) {
							cmdControl(fHolder, position, 3, OFF);
						} else {
							cmdControl(fHolder, position, 3, ON);
						}
					}
				}
			});

			holder.tgBtn4.setOnClickListener(new ClickEvent() {

				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						if (!fHolder.tgBtn4.isChecked()) {
							cmdControl(fHolder, position, 4, OFF);
						} else {
							cmdControl(fHolder, position, 4, ON);
						}
					}
				}
			});

			holder.imBtnOn.setOnClickListener(new OnClickListener() { // 窗帘on

						@Override
						public void onClick(View v) {
							if (myAdpterOnclick != null) {
								int which = v.getId();
								myAdpterOnclick.onApterClick(which, fpostion);

								cmdControl(fHolder, position, 6, WinON);
								fHolder.imBtnOn
										.setBackgroundResource(R.drawable.on1);
								fHolder.imBtnPause
										.setBackgroundResource(R.drawable.pause0);
								fHolder.imBtnOff
										.setBackgroundResource(R.drawable.off0);
							}
						}
					});
			holder.imBtnPause.setOnClickListener(new OnClickListener() { // 窗帘pk

						@Override
						public void onClick(View v) {
							if (myAdpterOnclick != null) {
								int which = v.getId();
								myAdpterOnclick.onApterClick(which, fpostion);

								cmdControl(fHolder, position, 6, WinPK);
								fHolder.imBtnOn
										.setBackgroundResource(R.drawable.on0);
								fHolder.imBtnPause
										.setBackgroundResource(R.drawable.pause1);
								fHolder.imBtnOff
										.setBackgroundResource(R.drawable.off0);
							}
						}
					});
			holder.imBtnOff.setOnClickListener(new OnClickListener() { // 窗帘off

						@Override
						public void onClick(View v) {
							if (myAdpterOnclick != null) {
								int which = v.getId();
								myAdpterOnclick.onApterClick(which, fpostion);

								cmdControl(fHolder, position, 6, WinOFF);
								fHolder.imBtnOn
										.setBackgroundResource(R.drawable.on0);
								fHolder.imBtnPause
										.setBackgroundResource(R.drawable.pause0);
								fHolder.imBtnOff
										.setBackgroundResource(R.drawable.off1);
							}
						}
					});

			holder.checkBox.setOnClickListener(new ClickEvent() {

				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						sceneSaveFlag = false;

						Device device = devlist.get(position);
						device.getDeviceStateCmd();
						ThemeDevice themeState = new ThemeDevice();
						themeState.setDeviceNo(device.getDeviceNo());
						themeState.setThemeNo(themeNo);
						themeState.setDeviceStateCmd(device.getDeviceStateCmd());

						if (fHolder.checkBox.isChecked()) {
							// 更新本地缓存的themestate
							updateThemestateBythemeStateCache(device,
									themeState);

							System.out
									.println("=======checkbox执行了选中事件=========="
											+ device.getDeviceNo() + position);
							// 本地themestate添加或更新设备状态（1）
							new ThemeDeviceDao(SceneSetActivity.this)
									.addOrUpdate(themeState);

						} else {
							// 从本地缓存中themestate删除设备状态（2）
							deleteThemestateBythemeStateCache(device,
									themeState);

							new ThemeDeviceDao(SceneSetActivity.this)
									.deleteThemeDeviceByThemeNo(themeState);
						}
					}
				}
			});

			holder.sbLight
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() { // 调光灯
						int mprogress = 0;

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							mprogress = progress;
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) { // 滑动条停止时触发事件
							sLightVal = String.valueOf(mprogress);

							devlist.get(fpostion).setDeviceStateCmd(sLightVal);
							cmdControl(fHolder, position, 5, ON);
							;
						}
					});

			return view;
		}

		class ViewHolder {
			@Bind(R.id.tv_switch_devSite)
			TextView tvDevSite;
			@Bind(R.id.tv_switch_devtypeName)
			TextView tvDevName;
			@Bind(R.id.chbox_scene_set)
			CheckBox checkBox;
			@Bind(R.id.tg_btn1)
			ToggleButton tgBtn1;
			@Bind(R.id.tg_btn2)
			ToggleButton tgBtn2;
			@Bind(R.id.tg_btn3)
			ToggleButton tgBtn3;
			@Bind(R.id.tg_btn4)
			ToggleButton tgBtn4;
			@Bind(R.id.sb_switch_light)
			SeekBar sbLight; // 调光灯
			@Bind(R.id.im_btnOn)
			ImageView imBtnOn;
			@Bind(R.id.im_btnPause)
			ImageView imBtnPause;
			@Bind(R.id.im_btnOff)
			ImageView imBtnOff;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
		}
	}

	/**************** 音乐adapter ************************/

	public class MusicListAdapter extends BaseAdapter {
		private int[] colors = new int[] { 0x30BCDFE3, 0x30E1EFEF };
		private List<ThemeMusic> musicList;
		private LayoutInflater mInflater;

		public MusicListAdapter(Context context, List<ThemeMusic> vector) {
			this.musicList = vector;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return musicList.size();
		}

		@Override
		public Object getItem(int position) {
			return musicList.get(position);
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
				view = mInflater.inflate(R.layout.include_music, parent, false);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}

			holder.tvmusicname.setText((CharSequence) musicList.get(position)
					.getSongName());
			holder.tvmusicname.setTextSize(25);
			int colorPos = position % colors.length;
			view.setBackgroundColor(colors[colorPos]);
			if (MusicPosition == position) {
				view.setBackgroundColor(getResources().getColor(
						R.color.listSelector));
			}
			return view;
		}

		class ViewHolder {
			@Bind(R.id.tv_music_name)
			TextView tvmusicname;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
		}

	}

	/**
	 * 情景红外任务适配器
	 * 
	 * @author WSN-520
	 * 
	 */
	public class InfraTaskAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return themeInfralist.size();
		}

		@Override
		public Object getItem(int position) {
			return themeInfralist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(final int position, View view, ViewGroup parent) {

			view = View.inflate(SceneSetActivity.this,
					R.layout.item_themeinfra, null);
			TextView tvInfraName = (TextView) view
					.findViewById(R.id.tv_list_themeinfra);
			ImageView tvInfraSet = (ImageView) view
					.findViewById(R.id.im_themeinfra_set);

			ThemeInfra themeInfra = themeInfralist.get(position);

			String deviceNo = themeInfra.getDeviceNo();

			String devSite = initInfraDeviceSite(deviceNo) + "/";

			tvInfraName.setText(devSite + themeInfra.getInfraControlName());

			tvInfraSet.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					infraManagerDialog(position);
				}
			});

			return view;
		}

	}

	/**
	 * 
	 * @param position
	 */
	private void infraManagerDialog(final int position) {

		String[] items = new String[2];
		items[0] = "遥控设置";
		items[1] = "遥控删除";

		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("遥控管理"); // 设置标题

		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0:
					SystemValue.InfraSetType = SystemValue.InfraUpdate;
					SystemValue.formerInfra = themeInfralist.get(position);
					Intent yaokongIntent = new Intent(SceneSetActivity.this,
							ActivityMain.class);
					startActivity(yaokongIntent);

					break;
				case 1:
					showDeleteDialog(position);
					break;
				default:
					break;
				}
			}
		});
		builder.create().show();
	}

	public String initInfraDeviceSite(String devdtoNo) {
		String spacename = "";
		Device devdto = new DevdtoDao(SceneSetActivity.this)
				.findDevByDeviceNoAndGatewayNo(devdtoNo, SystemValue.gatewayid);
		if (devdto != null) {
			UserSpaceDevice userSpace = new UserSpaceDevDao(
					SceneSetActivity.this).findDeviceSpace(
					SystemValue.phonenum, devdto.getDeviceNo());
			if (userSpace != null) {
				spacename = WebPacketUtil.getSpaceName(userSpace.getSpaceNo()); // 根据phonespaceid获取spacename
			} else {
				spacename = WebPacketUtil.getSpaceName(devdto.getSpaceNo()); // 根据phonespaceid获取spacename
			}
		}
		return spacename;
	}

	/** 删除对话框 **/
	protected void showDeleteDialog(final int position) {
		final ThemeInfra themeInfra = themeInfralist.get(position);

		mBackDialog = BaseDialog.getDialog(SceneSetActivity.this, "提示",
				"确认要删除遥控" + themeInfra.getInfraControlName() + "吗？", "确认",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						new ThemeInfraDao(SceneSetActivity.this)
								.deleteThemeInfra(themeInfra);
						new ThemeDeviceDao(SceneSetActivity.this)
								.deleteThemeInfra(themeInfra);

						themeInfralist = new ThemeInfraDao(
								SceneSetActivity.this)
								.getThemeInfraByThemeNo(themeNo);
						Message msg = new Message();
						msg.what = 0x131;
						handler.sendMessage(msg);
						dialog.cancel();
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
	 * 判断情景中否在存在某设备及设定的状态
	 * 
	 * @param position
	 * @return
	 */
	private boolean isChecked(Device devdto, int position) {
		// DevDTO devdto=devlist.get(position);
		String devid = devdto.getDeviceNo();
		if (themeLinkList != null) {
			for (int i = 0; i < themeLinkList.size(); i++) {
				String themedevid = themeLinkList.get(i).getDeviceNo();
				if (themedevid.equals(devid)) {
					String devState = themeLinkList.get(i).getDeviceStateCmd();
					devlist.get(position).setDeviceStateCmd(devState); // 更新显示devlist中显示为已经设置的状态
					// devdto.setDevState(devState); //更新显示devlist中显示为已经设置的状态
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 根据设备类型显示按钮
	 * 
	 * @param holder
	 * @param devtype
	 */
	private void showViewByDevtype(ViewHolder holder, Device devdto) {

		int devtype = devdto.getDeviceTypeId();

		// 设备状态串转换为状态字符数组
		String mdevState = devdto.getDeviceStateCmd();
		strStaArr = mdevState.toCharArray();
		switch (devtype) {
		case 1:
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (strStaArr[0] == ON) {
				holder.tgBtn1.setChecked(true);
			} else {
				holder.tgBtn1.setChecked(false);
			}
			break;
		case 8: // 插座
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (strStaArr[0] == ON) {
				holder.tgBtn1.setChecked(true);
			} else {
				holder.tgBtn1.setChecked(false);
			}
			break;
		case 110: // 门磁报警器
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (mdevState.equals("11000000") || mdevState.equals("11")
					|| mdevState.equals("01000000")) { // 入网，布防，报警
				holder.tgBtn1.setChecked(true);
			} else { // 显示布防状态为11
				holder.tgBtn1.setChecked(false);
			}
			break;
		case 113: // 红外报警器
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (mdevState.equals("11000000") || mdevState.equals("11")
					|| mdevState.equals("01000000")) { // 入网，布防，报警
				holder.tgBtn1.setChecked(true);
			} else { // 显示布防状态为11
				holder.tgBtn1.setChecked(false);
			}
			break;
		case 115: // 燃气报警器
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (mdevState.equals("11000000") || mdevState.equals("11")
					|| mdevState.equals("01000000")) { // 入网，布防，报警
				holder.tgBtn1.setChecked(true);
			} else { // 显示布防状态为11
				holder.tgBtn1.setChecked(false);
			}
			break;
		case 118: // 烟感报警器
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (mdevState.equals("11000000") || mdevState.equals("11")
					|| mdevState.equals("01000000")) { // 入网，布防，报警
				holder.tgBtn1.setChecked(true);
			} else { // 显示布防状态为11
				holder.tgBtn1.setChecked(false);
			}
			break;

		case 51: // 风扇 51
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (strStaArr[0] == ON) {
				holder.tgBtn1.setChecked(true);
			} else {
				holder.tgBtn1.setChecked(false);
			}
			break;
		case 2:
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.VISIBLE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (strStaArr[0] == ON) {
				holder.tgBtn1.setChecked(true);
			} else {
				holder.tgBtn1.setChecked(false);
			}
			if (strStaArr[1] == ON) {
				holder.tgBtn2.setChecked(true);
			} else {
				holder.tgBtn2.setChecked(false);
			}
			break;
		case 3:
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.VISIBLE);
			holder.tgBtn3.setVisibility(View.VISIBLE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);

			holder.sbLight.setVisibility(View.GONE);
			if (strStaArr[0] == ON) {
				holder.tgBtn1.setChecked(true);
			} else {
				holder.tgBtn1.setChecked(false);
			}
			if (strStaArr[1] == ON) {
				holder.tgBtn2.setChecked(true);
			} else {
				holder.tgBtn2.setChecked(false);
			}
			if (strStaArr[2] == ON) {
				holder.tgBtn3.setChecked(true);
			} else {
				holder.tgBtn3.setChecked(false);
			}
			break;
		case 4:
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.VISIBLE);
			holder.tgBtn3.setVisibility(View.VISIBLE);
			holder.tgBtn4.setVisibility(View.VISIBLE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (strStaArr[0] == ON) {
				holder.tgBtn1.setChecked(true);
			} else {
				holder.tgBtn1.setChecked(false);
			}
			if (strStaArr[1] == ON) {
				holder.tgBtn2.setChecked(true);
			} else {
				holder.tgBtn2.setChecked(false);
			}
			if (strStaArr[2] == ON) {
				holder.tgBtn3.setChecked(true);
			} else {
				holder.tgBtn3.setChecked(false);
			}
			if (strStaArr[3] == ON) {
				holder.tgBtn4.setChecked(true);
			} else {
				holder.tgBtn4.setChecked(false);
			}
			break;
		case 5:
			holder.sbLight.setVisibility(View.VISIBLE);
			holder.tgBtn1.setVisibility(View.GONE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			
			holder.sbLight.setMax(9);
			int dLight = 0;
			if (mdevState.endsWith("a")) {
				dLight = 9;
			} else {
				dLight = Integer.valueOf(mdevState);
			}
			holder.sbLight.setProgress(dLight);

			break;
		case 6: // 窗帘
			holder.imBtnOn.setVisibility(View.VISIBLE);
			holder.imBtnPause.setVisibility(View.VISIBLE);
			holder.imBtnOff.setVisibility(View.VISIBLE);
			if (mdevState.equals("10")) {

				holder.imBtnOn.setBackgroundResource(R.drawable.on1);
				holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
				holder.imBtnOff.setBackgroundResource(R.drawable.off0);
			} else if (mdevState.equals("01")) {
				holder.imBtnOn.setBackgroundResource(R.drawable.on0);
				holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
				holder.imBtnOff.setBackgroundResource(R.drawable.off1);
			} else {
				holder.imBtnOn.setBackgroundResource(R.drawable.on0);
				holder.imBtnPause.setBackgroundResource(R.drawable.pause1);
				holder.imBtnOff.setBackgroundResource(R.drawable.off0);
			}
			break;
		case 11: // 窗户
			holder.imBtnOn.setVisibility(View.VISIBLE);
			holder.imBtnPause.setVisibility(View.VISIBLE);
			holder.imBtnOff.setVisibility(View.VISIBLE);
			if (mdevState.equals("10")) {
				holder.imBtnOn.setBackgroundResource(R.drawable.on1);
				holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
				holder.imBtnOff.setBackgroundResource(R.drawable.off0);
			} else if (mdevState.equals("01")) {
				holder.imBtnOn.setBackgroundResource(R.drawable.on0);
				holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
				holder.imBtnOff.setBackgroundResource(R.drawable.off1);
			} else {
				holder.imBtnOn.setBackgroundResource(R.drawable.on0);
				holder.imBtnPause.setBackgroundResource(R.drawable.pause1);
				holder.imBtnOff.setBackgroundResource(R.drawable.off0);
			}
			break;
		}

	}

	/**
	 * 发送命令函数
	 * 
	 * @param position
	 *            当前点击的开关在devlist中位置
	 * @param switchid
	 *            多路开关中当前点击的位置
	 * @param ch
	 *            相应的开关命令
	 */
	public void cmdControl(ViewHolder holder, int position, int switchid,
			char ch) {
		sceneSaveFlag = false; // 情景设置有改动，同步网关标志位为false

		holder.checkBox.setChecked(true);

		Device device = devlist.get(position);

		String sqlCmd = WebPacketUtil.convertCmdToSql(device, switchid, ch); // devstate转码到本地

		device.setDeviceStateCmd(sqlCmd); // 注意更改后的设备状态先加载到本地
		addOrUpdateThemestateToLocal(themeNo, device);

		// ===更新version_scene 时间戳===
		Version version = SystemValue.getVersion(SystemValue.VERSION_SCENE);
		new VersionDao(null).addorUpdateVerson(version);

	}

	/**
	 * 更新本地themestate
	 * 
	 * @param themeid
	 * @param device
	 * @param cmd
	 */
	private void addOrUpdateThemestateToLocal(String themeid, Device device) {

		ThemeDevice themeDevice = new ThemeDevice();
		themeDevice.setDeviceNo(device.getDeviceNo());
		themeDevice.setThemeNo(themeNo);
		themeDevice.setThemeDeviceNo(themeDeviceNo);
		themeDevice.setGatewayNo(SystemValue.gatewayid);
		themeDevice.setDeviceStateCmd(device.getDeviceStateCmd()); // devstate转换到本地themestate

		// 更新本地缓存的themestate
		updateThemestateBythemeStateCache(device, themeDevice);

		// 本地themestate添加或更新设备状态（1）
		new ThemeDeviceDao(SceneSetActivity.this).addOrUpdate(themeDevice);
	}

	// 更新本地的themestate
	private void updateThemestateBythemeStateCache(Device device,
			ThemeDevice themeState) {
		boolean themeflag = false;
		for (int i = 0; i < themeLinkList.size(); i++) {
			ThemeDevice state = themeLinkList.get(i);
			if (state.getDeviceNo().equals(device.getDeviceNo())) {
				state.setDeviceStateCmd(device.getDeviceStateCmd());
				themeflag = true; // 情景列表中存在设备
				break;
			}
		}
		if (!themeflag) {
			themeLinkList.add(themeState);
		}
	}

	// 删除本地的themestate
	private void deleteThemestateBythemeStateCache(Device device,
			ThemeDevice themeState) {
		for (int i = 0; i < themeLinkList.size(); i++) {
			ThemeDevice state = themeLinkList.get(i);
			if (state.getDeviceNo().equals(device.getDeviceNo())) {
				themeLinkList.remove(i);
				break;
			}
		}
	}

	@Override
	protected void initViews() {
		viewPager = (ViewPager) main.findViewById(R.id.vp_scene_vpager);
		tvBack = (TextView) main.findViewById(R.id.tv_head_back);
		tvTitle = (TextView) main.findViewById(R.id.tv_head_title);
		tvSubmit = (TextView) main.findViewById(R.id.tv_head_submit);
		tvSubmit.setVisibility(View.VISIBLE);
		tvSubmit.setOnTouchListener(sysnThemeClickListener);

		rb_tab = (RadioGroup) main.findViewById(R.id.rg_tab);
		rb_yaokong = (RadioButton) main.findViewById(R.id.rb_scene_yaokong);

		tvRefreshThemestate = (TextView) main
				.findViewById(R.id.tv_refresh_themestate);
	}

	@Override
	protected void initDatas() {
		tvBack.setOnTouchListener(BackOnTouchListener);
		rb_tab.setOnCheckedChangeListener(this);

		// tvRefreshThemestate
		// .setOnClickListener(RefreshThemestateOnClickListener);

	}

	@SuppressWarnings("unused")
	private OnClickListener BackOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			unregisterReceiverSafe(infraReceiver);
			if (sceneSaveFlag == true) {
				finish();
			} else {
				ToastUtils.showToast(SceneSetActivity.this, "请先点击完成，保存情景设置！",
						2000);
			}
		}
	};

	private OnTouchListener BackOnTouchListener = new OnTouchListener() {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			unregisterReceiverSafe(infraReceiver);
			if (sceneSaveFlag == true) {
				finish();
			} else {
				ToastUtils.showToast(SceneSetActivity.this, "请先点击完成，保存情景设置！",
						2000);
			}
			return false;
		}
	};

	private OnTouchListener sysnThemeClickListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			sceneSaveFlag = true;

			ToastUtils.showToast(SceneSetActivity.this, " 正在同步情景到网关！", 2000);

			j = 0; // 每当新情景，指针重置为0
			themeList.clear();
			themeList.add(SystemValue.themeSet);
			timerhandler.post(timerrunnable);
			
			if (NetValue.netFlag == NetValue.OUTERNET) {
				List<APPThemeMusic> list = new APPThemeMusicDao(
						SceneSetActivity.this)
						.GetAppThemeMusicListByGatewayNo(SystemValue.gatewayid);
				if (list.size() > 0) {

					MusicJpush.SendThemeMusicListToserver(list);

				}

			}
			return false;
		}
	};

	// 联动情景设置
	private void showSceneFragment() {
		viewPager.setCurrentItem(0);
	}

	// 联动遥控设置
	private void showRemoteControlFragment() {
		rb_yaokong.setChecked(true);

		editor.putString("OPERATION_TYPE", "SCENE_INFRA_SET"); // 情景设置页面进入，操作类型为红外码设置
		editor.commit();

		viewPager.setCurrentItem(1);
		selectIndex = 5;

		TextView tvAddInfra = (TextView) spaceViews[1]
				.findViewById(R.id.tv_add_infratask);

		themeInfralist = new ThemeInfraDao(SceneSetActivity.this)
				.getThemeInfraByThemeNo(themeNo);
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x131;
		handler.sendMessage(msg);

		tvAddInfra.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SystemValue.InfraSetType = SystemValue.InfraAdd;
				Intent yaokongIntent = new Intent(SceneSetActivity.this,
						ActivityMain.class);
				startActivity(yaokongIntent);
			}
		});
	}

	// 联动开关设置
	private void showLightFragment() {
		viewPager.setCurrentItem(2);
		selectIndex = 0;

		// =====清空当前list中缓存=====
		gvlistAll.clear();
		devlist.clear();
		loadState = LOAD_STATE_IDLE;

		// if (NetValue.netFlag==NetValue.INTRANET) {
		// //根据网关号从数据库加载相应设备
		gvlistAll = new DevdtoDao(SceneSetActivity.this).switchListBygwId(
				SystemValue.gatewayid, SystemValue.SWITCH);
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

		mgvDevices[0].setOnScrollListener(new MyOnScrollListener());
	}

	// 联动插座设置
	private void showSocketFragment() {
		viewPager.setCurrentItem(3);
		selectIndex = 1;

		// =====清空当前list中缓存=====
		gvlistAll.clear();
		devlist.clear();
		loadState = LOAD_STATE_IDLE;

		// 根据网关号从数据库加载相应设备
		gvlistAll = new DevdtoDao(SceneSetActivity.this).switchListBygwId(
				SystemValue.gatewayid, SystemValue.SOCK);
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

		// gridView滑动监听
		mgvDevices[1].setOnScrollListener(new MyOnScrollListener());
	}

	// 联动门窗设置
	private void showWindowFragment() {
		viewPager.setCurrentItem(4);
		selectIndex = 2;

		// =====清空当前list中缓存=====
		gvlistAll.clear();
		devlist.clear();
		loadState = LOAD_STATE_IDLE;

		// 根据网关号从数据库加载相应设备
		gvlistAll = new DevdtoDao(SceneSetActivity.this).switchListBygwId(
				SystemValue.gatewayid, SystemValue.WINDOW);
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

		mgvDevices[2].setOnScrollListener(new MyOnScrollListener());
	}

	// 联动微控设置
	private void showMicroControlFragment() {
		viewPager.setCurrentItem(5);
		selectIndex = 3;

		// =====清空当前list中缓存=====
		gvlistAll.clear();
		devlist.clear();
		loadState = LOAD_STATE_IDLE;

		// 根据网关号从数据库加载相应设备
		List<Device> allDevList = new ArrayList<Device>();
		allDevList = new DevdtoDao(SceneSetActivity.this).switchListBygwId(
				SystemValue.gatewayid, SystemValue.SENSOR);
		gvlistAll = WebPacketUtil.DeleteSensorFromDevicesAll(allDevList);

		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

		// gridView滑动监听
		mgvDevices[3].setOnScrollListener(new MyOnScrollListener());
	}

	//TODO  联动音乐设置
	private void showMusicFragment() {
		viewPager.setCurrentItem(7);
		selectIndex = 4; // 音乐是第5个页面，view编号为4
		loadState = LOAD_STATE_IDLE;
		if (NetValue.netFlag == NetValue.OUTERNET) {
			getThemeMusic();
			findmusicByWgid(b);
			if (themeMusic_songName != null) {
				OuternetDialog();
			} else {
				ToastUtils.showToast(SceneSetActivity.this, "未设置音乐！", 2000);
			}

		} else if (NetValue.netFlag == NetValue.INTRANET) {
			// 内网下获取音乐
			findmusicBygatewayNo();
			getThemeMusicInside(themeNo);
			if (themeMusic_songName != null) {
				InteranetDialog();
			} else {
				ToastUtils.showToast(SceneSetActivity.this, "未设置音乐！", 2000);
			}
		}
		Message mesg = new Message();
		mesg.what = 0x130;
		handler.sendMessage(mesg);

		// 设置情景音乐
		if (NetValue.netFlag == NetValue.OUTERNET) {
			mgvDevices[4].setOnItemClickListener(SetThemeMusicListener);
		} else if (NetValue.netFlag == NetValue.INTRANET) {
			mgvDevices[4].setOnItemClickListener(SetInsideThemeMusicListener);
		}

	}

	@Override
	public void onApterClick(int which, int postion) {

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_scene_qingjing:
			showSceneFragment();
			break;
		case R.id.rb_scene_yaokong:
			showRemoteControlFragment();
			break;
		case R.id.rb_scene_zhaoming:
			showLightFragment();
			break;
		case R.id.rb_scene_menchuang:
			showWindowFragment();
			break;
		case R.id.rb_scene_chazuo:
			showSocketFragment();
			break;
		case R.id.rb_scene_weikong:
			showMicroControlFragment();
			break;
		case R.id.rb_scene_yinyue:
			showMusicFragment();
			break;
		}

	}

	/**
	 * 点击 设置情景音乐
	 * */
	private OnItemClickListener SetThemeMusicListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			OutsideThemeMusicChoiceDialog(arg2);
		}
	};

	/**
	 * @Description:内网设置情景音乐
	 * */
	private OnItemClickListener SetInsideThemeMusicListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			InsideThemeMusicChoiceDialog(arg2);
		}
	};

	/**
	 * 外网获取设置的情急 音乐对象
	 * */
	private void getThemeMusic() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("themeNo", themeNo);
		params.addBodyParameter("gatewayNo", SystemValue.gatewayid);
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_GET_THEME_MUSIC, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 访问网络失败
						showCustomToast(getResources().getString(
								R.string.error_network));
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ResultMessage msg = JSONObject.parseObject(arg0.result,
								ResultMessage.class);
						if (msg.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
							ThemeMusic themeMusic = JSONObject.parseObject(
									(String) msg.getObject(), ThemeMusic.class);
							Staticthememusic=themeMusic;
							themeMusic_songName = themeMusic.getSongName();
							MusicPosition = MusicUtil.GetMusicListIndex(
									themeMusic_songName, musiclist);
						} else {
							themeMusic_songName = null;
						}
					}
				});
	}

	/**
	 * @Description:内网获取情景音乐
	 * @Date 2016-06-20
	 * */
	public void getThemeMusicInside(String str) {
		List<APPThemeMusic> list = new APPThemeMusicDao(SceneSetActivity.this)
				.GetAppThemeMusicListByThemeNo(str);
		Log.i("inside","得到的情景音乐:"+list.toString());
		if (list.size() > 0) {
			themeMusic_songName = list.get(0).getSongName();
//			if (list.get(0).getStyle().equals("1")) {
////				themeMusic_songName = "00";
//				themeMusic_songName=list.get(0).getSongName();
//			}
			Staticappthememusic=list.get(0);
			MusicPosition = MusicUtil.GetMusicListIndex(themeMusic_songName,
					musiclist);
		} else {
			themeMusic_songName = null;
		}
	}

	private final class MyOnScrollListener implements OnScrollListener {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			final int totalCount = firstVisibleItem + visibleItemCount; // firstVisibleItem当前页的第一项位置
																		// totalCount
																		// 当前页的最后一项位置
			if (totalCount == totalItemCount) { // 当前这一页加载完成，等待加载下一页
				if (loadState == LOAD_STATE_IDLE) {
					loadState = LOAD_STATE_LOADING;

					new Thread() {
						public void run() {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							int count = deviceAdpter.getCount();
							int dataIndex = 0; // 要加载的数据的index
							List<Device> result = new ArrayList<Device>();

							if (gvlistAll.size() > 0) {
								for (dataIndex = count; dataIndex < Math.min(
										count + EACH_COUNT, LIST_COUNT); dataIndex++) {
									Device devdto = gvlistAll.get(dataIndex);
									result.add(devdto);
								}
							}

							if (dataIndex == LIST_COUNT) {
								loadState = LOAD_STATE_FINISH;
							} else {
								loadState = LOAD_STATE_IDLE; // list未加载完，待续
							}

							if (dataIndex <= LIST_COUNT) {
								Message msg = new Message();
								msg.what = 0x008;
								msg.obj = result;
								handler.sendMessage(msg);
							}
						};
					}.start();
				}
			}
		}
	}

	/**
	 * 接收红外转发器的红外码
	 * 
	 * @author WSN-520
	 * 
	 */
	private class InfraSetReceiver extends BroadcastReceiver { // 收到广播后关闭AddCameraActivity

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String action = arg1.getAction();
			if (action.equals("INFRA_DEVID")) { // 接收红外设备id
				String devid = arg1.getStringExtra("devid");
				NetValue.DEVID_INFRA = devid;
				System.out.println("===接收到的红外设备id是==" + NetValue.DEVID_INFRA);
			} else if (action.equals("ACTION_SCENE_INFRA_SET")) { // 接收设置红外码
				sceneSaveFlag = false; // 红外设置改动，标志位为false
				byte[] infraredbuf = arg1.getByteArrayExtra("infrared");
				String str = DataConvertUtil.toHexUpString(infraredbuf);
				System.out.println("接收到的红外码===" + str);
				DataConvertUtil.rprintHexString(infraredbuf);
				int deviceType = arg1.getIntExtra("INFRA_TYPE", 0); // 遥控器控制的设备类型
				String deviceName = WebPacketUtil
						.getInfraDevtypeNameByDevtype(deviceType);
				String model = arg1.getStringExtra("INFRA_MODEL"); // 用户设置的模式
				String infraControlName = deviceName + model;
				System.out.println("接收到的空调模式===" + infraControlName);

				// 数据库更新待定 根据 DeviceNo(红外转发器)+deviceType(空调/风扇)
				ThemeInfra themeInfra = new ThemeInfra();
				themeInfra.setDeviceNo(NetValue.DEVID_INFRA);
				themeInfra.setDeviceStateCmd(str);
				themeInfra.setInfraControlName(infraControlName);
				themeInfra.setInfraTypeId(deviceType);
				themeInfra.setThemeNo(themeNo);
				themeInfra.setGatewayNo(SystemValue.gatewayid);

				try {
					if (SystemValue.InfraSetType == SystemValue.InfraAdd) {
						if (SystemValue.infraDevType != deviceType) { // 不是同一种设备类型就添加红外任务
							new ThemeInfraDao(SceneSetActivity.this)
									.addThemeInfra(themeInfra,
											SystemValue.themeSet);
							SystemValue.formerAddInfra = themeInfra; // 先更新后替换
							SystemValue.infraDevType = deviceType;
						} else {
							new ThemeInfraDao(SceneSetActivity.this)
									.updateThemeInfra(
											SystemValue.formerAddInfra,
											themeInfra);
							SystemValue.formerAddInfra = themeInfra; // 先更新后替换
						}
					} else if (SystemValue.InfraSetType == SystemValue.InfraUpdate) {
						new ThemeInfraDao(SceneSetActivity.this)
								.updateThemeInfra(SystemValue.formerInfra,
										themeInfra);
						SystemValue.formerInfra = themeInfra; // 先更新后替换
						System.out.println("SystemValue.InfraSetType"
								+ SystemValue.InfraUpdate);
					}
				} catch (Exception e) {
					System.out.println("红外情景联动设置抛出异常...");
					e.printStackTrace();
				}

				themeInfralist = new ThemeInfraDao(SceneSetActivity.this)
						.getThemeInfraByThemeNo(themeNo);
				// 异步进程更新界面
				Message msg = new Message();
				msg.what = 0x131;
				handler.sendMessage(msg);
			}
		}

	}

	// 取消动态注册的广播
	private void unregisterReceiverSafe(BroadcastReceiver receiver) {
		try {
			unregisterReceiver(receiver);
		} catch (IllegalArgumentException e) {
			// ignore
		}
	}

	/**
	 * 同步情景及其联动设置到网关
	 */
	private void sysnSceneToGatewayLoop() {
		// 设备安装信息不需同步到网关2016.04.16

		if (j < themeList.size()) {
			Theme theme = themeList.get(j);

			Item trggerItem = setTrggerItemByThemetype(theme); // 根据触发器类型，填充TriggerItem

			ArrayList<Item> triggerList = new ArrayList<Item>();
			// Item trggerItem=new Item();
			// trggerItem.setDeviceNo(theme.getDeviceNo());
			// //触发器item的id设置为theme_mac
			// System.out.println("===填充的硬件识别号deviceNo"+theme.getDeviceNo());

			// trggerItem.setDeviceStateCmd(theme.getThemeState());
			// trggerItem.setDataLen(4); //触发器的data长度
			// trggerItem.setDeviceType(202); //填充默认的情景类型202
			triggerList.add(trggerItem);

			themeDeviceList = new ThemeDeviceDao(SceneSetActivity.this)
					.findDevstateBythemeNo(theme.getThemeNo());
			int itemSize = themeDeviceList.size();
			ArrayList<Item> sceneList = new ArrayList<Item>();
			for (int i = 0; i < themeDeviceList.size(); i++) {
				ThemeDevice themedevice = themeDeviceList.get(i);
				Item deviceitem = new Item();
				String deviceNo = themedevice.getDeviceNo();
				Device device = new DevdtoDao(SceneSetActivity.this)
						.findDevByDeviceNoAndGatewayNo(deviceNo,
								SystemValue.gatewayid);

				if (device != null) {
					deviceitem.setDeviceNo(themedevice.getDeviceNo());
					deviceitem.setDeviceStateCmd(themedevice
							.getDeviceStateCmd());

					int datalen = themedevice.getDeviceStateCmd().length();
					if (device.getDeviceTypeId() == SystemValue.DEV_INFRA_CONTROL) {
						datalen = datalen / 2;
					}
					deviceitem.setDataLen(datalen);
					deviceitem.setDeviceType(device.getDeviceTypeId());
					sceneList.add(deviceitem);
				} else {
				}
			}

			ThemeData themeData = new ThemeData();
			themeData.setDeviceNo(theme.getDeviceNo()); // 【1】DeviceNo
			themeData.setThemeNo(theme.getThemeNo()); // 【2】ThemeNo
			themeData.setThemeState(theme.getThemeState()); // 【3】ThemeState
			themeData.setThemeType(theme.getThemeType());
			themeData.setThemeName(theme.getThemeName());
			if(theme.getThemeType()==4){  //20160904
				themeData.setTriggerNum(0);
			}else{
				themeData.setTriggerNum(1);
			}
			themeData.setDeviceNum(itemSize); // 联动的设备数量
			themeData.setTriggerList(triggerList);
			themeData.setDeviceList(sceneList);

			SocketPacket socketPacket = WebPacketUtil
					.sceneSet2Packet(themeData);
			sentCmdByServerOrGateway(socketPacket);

			timerhandler.post(timerrunnable);
			j++;
		} else {
			
			new Handler().postDelayed(new Runnable() {
				public void run() {

					SocketPacket socketPacket = WebPacketUtil
							.finnishThemeSetPacket();
					sentCmdByServerOrGateway(socketPacket); // 判断发送到网关还是服务器
				}
			}, 1000);
			
		}
	}

	
	/**
	 * 判断触发器是硬件还是传感器设备并填充对应情景
	 * 
	 * @param theme
	 */
	private Item setTrggerItemByThemetype(Theme theme) {
		Item trggerItem = new Item();
		String triDeviceNo = theme.getDeviceNo();
		trggerItem.setDeviceNo(triDeviceNo); // 触发器item的id设置为theme_mac

		Device device = new DevdtoDao(SceneSetActivity.this)
				.findDevByDeviceNoAndGatewayNo(triDeviceNo,
						SystemValue.gatewayid);

		int themeType = theme.getThemeType();
		if (themeType == SystemValue.SCENE_TRIGGER) {    //触发器情景
			trggerItem.setDeviceStateCmd("01000000");
			trggerItem.setDataLen(8); // 触发器的data长度
			trggerItem.setDeviceType(device.getDeviceTypeId());

		} else if (themeType == SystemValue.SCENE_HARD
				|| themeType == SystemValue.SCENE_SOFT) {  //
			trggerItem.setDeviceStateCmd(theme.getThemeState());
			trggerItem.setDataLen(4); // 触发器的data长度
			trggerItem.setDeviceType(202); // 填充默认的情景类型202
		}
		return trggerItem;
	}

	
	
	private void sentCmdByServerOrGateway(final SocketPacket socketPacket) {
		switch (NetValue.netFlag) {
		case NetValue.OUTERNET: // 外网
			// 将命令封装为字符串发送到服务器
			byte[] sentBytes = WebPacketUtil.packetToByteStream(socketPacket);
			sendCmdToServer(sentBytes, 0); // 发送到服务器的命令串
			break;
			
		case NetValue.INTRANET: // 内网
			socketService.socketClose();
			SystemValue.deviceSysnFlag=true;    //设置情景重连socket，无需再请求设备的最新状态
			socketService.socketConnect(NetValue.LOCAL_IP);
			
			new Handler().postDelayed(new Runnable() {
				public void run() {
					socketService.sentPacket(socketPacket); // 发送情景设置报文
				}
			}, 500);
			break;
		}
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

			socketService.callSocket(new SocketCallBack() {
				@Override
				public void callBack(TranObject tranObject) {

					switch (tranObject.getTranType()) {
					case NETMSG:
						int netstatue = (Integer) tranObject.getObject();
						if ((NetValue.NONET == netstatue)) { // 本地连接失败
							showCustomToast("本地连接失败,请检查网关是否连接本地网络！");

							NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
						}
						break;
					case DEVMSG:
						SocketPacket socketPacket = (SocketPacket) tranObject
								.getObject();
						Short datatype = socketPacket.getDataType();
						
						if (datatype == NetValue.ACK_FINISH_SCENE) {
						
							ToastUtils.showToast(SceneSetActivity.this,"设置情景成功！", 2000);
						}
					default:
						break;
					}
				}
			});
		}
	};

	// 连续执行情景线程同步到网关
	Handler timerhandler = new Handler();
	Runnable timerrunnable = new Runnable() {
		@Override
		public void run() {
			sysnSceneToGatewayLoop();

		}
	};

	@Override
	protected void onDestroy() {
		unbindService(conn);
		super.onDestroy(); // 注意先后
	}

	private void OuternetDialog() {

//		if (themeMusic_songName.equals("00")) {
//			themeMusic_songName = "暂停音乐";
//		}
		if (Staticthememusic.getStyle().equals(SystemValue.MUSIC_CTRL_PAUSE)|| Staticthememusic.getStyle()=="1") {
			themeMusic_songName="暂停  "+Staticthememusic.getSongName();
		}
		new AlertDialog.Builder(this)
				.setTitle("当前联动音乐设置")
				.setSingleChoiceItems(new String[] {"当前设置:"+themeMusic_songName, "删除联动音乐", "联动音乐暂停" },
						0, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								if (arg1 == 1) {
									// 删除本地sqlite 2016-06-24
									new APPThemeMusicDao(SceneSetActivity.this)
											.DeleteAppThemeMusic(themeNo);
									// 服务器上也要删除
									DeleteThemeMusic(themeNo,
											SystemValue.gatewayid);
									themeMusic_songName = null;
									MusicPosition = -1;
									Message mesg1 = new Message();
									mesg1.what = 0x130;
									handler.sendMessage(mesg1);

								} else if(arg1==2) {
									// 情景音乐设置保存到本地
									APPThemeMusic appThemeMusic = MusicUtil
											.PauseAppthemeMusic(SystemValue.themeSet,Staticthememusic);
									new APPThemeMusicDao(SceneSetActivity.this)
											.UpdateAppThemeMusci(appThemeMusic);
									// 情景音乐设置保存到服务器
									ThemeMusic themeMusic = MusicUtil.PauseThememusic(SystemValue.themeSet,Staticthememusic);
									Pausethememusic(themeMusic);
								
//									 情景音乐通过服务器 JPush 推送到七寸屏上 该情景音乐为 暂停音乐
//									MusicJpush.SendThemeMusicToJpush(appThemeMusic);  
								}

							}
						}).setNegativeButton("设置完成", null).show();

	}

	private void InteranetDialog() {
//		if (themeMusic_songName.equals("00")) {
//			themeMusic_songName = "暂停音乐";
//		}
		if (Staticappthememusic.getStyle().equals(SystemValue.MUSIC_CTRL_PAUSE)) {
			themeMusic_songName="暂停  "+Staticappthememusic.getSongName();
		}
		new AlertDialog.Builder(this)
				.setTitle("当前联动音乐设置")
				.setSingleChoiceItems(new String[] { "当前设置:"+themeMusic_songName,"删除联动音乐", "联动音乐暂停" },
						0, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								if (arg1 == 1) {
									// 删除本地sqlite 2016-06-24
									new APPThemeMusicDao(SceneSetActivity.this).DeleteAppThemeMusic(themeNo);
									DeleteThemeMusic(themeNo, SystemValue.gatewayid);
									themeMusic_songName = null;
									MusicPosition = -1;
									Message mesg1 = new Message();
									mesg1.what = 0x130;
									handler.sendMessage(mesg1);
								} else if(arg1 == 2){
									// 情景音乐设置保存到本地
									ThemeMusic music=MusicUtil.AppThemeMusicToThemeMusic(Staticappthememusic);
									APPThemeMusic appThemeMusic = MusicUtil.PauseAppthemeMusic(SystemValue.themeSet,music);
									new APPThemeMusicDao(SceneSetActivity.this).UpdateAppThemeMusci(appThemeMusic);
								}
							}
						}).setNegativeButton("设置完成", null).show();

	}

	private void DeleteThemeMusic(String themeid, String gatewayid) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("themeNo", themeid);
		params.addBodyParameter("gatewayNo", gatewayid);
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_DELETE_THEME_MUSIC,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 访问网络失败
						showCustomToast(getResources().getString(
								R.string.error_network));
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ResultMessage msg = JSONObject.parseObject(arg0.result,
								ResultMessage.class);
						if (msg.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
							ToastUtils.showToast(SceneSetActivity.this,
									"取消设置成功!", 2000);
						} else {
							ToastUtils.showToast(SceneSetActivity.this,
									"取消设置失败!请检查网络", 2000);
						}

					}
				});
	}

	private void Pausethememusic(ThemeMusic themeMusic) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("thememusic",
				JSONObject.toJSONString(themeMusic));
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_SET_THEME_MUSIC, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 访问网络失败
						showCustomToast(getResources().getString(
								R.string.error_network));
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ResultMessage msg = JSONObject.parseObject(arg0.result,
								ResultMessage.class);
						if (msg.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
							ToastUtils.showToast(SceneSetActivity.this,
									"设置成功!", 2000);
						} else {
							ToastUtils.showToast(SceneSetActivity.this,
									"设置失败!请检查网络", 2000);
						}
					}
				});

	}

	private void OutsideThemeMusicChoiceDialog(final int posistion) {
		final String items[] = { "播放音乐", "暂停音乐", "删除音乐" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("联动音乐设置");
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
				case 0:
					ThemeMusic themeMusic = MusicUtil.GetThemeMusic(SystemValue.themeSet, musiclist.get(posistion));
					MusicJpush.SendThemeMusicToServer(themeMusic,NetValue.MUSIC_SET_THEME_MUSIC);
					
					//保存到本地
					APPThemeMusic appThemeMusic = MusicUtil.GetAppThemeMusic(themeMusic, SystemValue.themeSet);
					List<APPThemeMusic> list = new APPThemeMusicDao(SceneSetActivity.this).GetAppThemeMusicListByThemeNo(SystemValue.themeSet
									.getThemeNo());
					if (list.size() > 0) {
						new APPThemeMusicDao(SceneSetActivity.this).UpdateAppThemeMusci(appThemeMusic);
					} else {
						new APPThemeMusicDao(SceneSetActivity.this).InsertAppThemeMusic(appThemeMusic);
					}

					// 刷新音乐列表，让被点击的歌曲颜色变换
					MusicPosition = posistion;
					Message mesg = new Message();
					mesg.what = 0x130;
					handler.sendMessage(mesg);
					break;
				case 1:
					ThemeMusic themeMusicPause = MusicUtil.GetThemeMusic(SystemValue.themeSet, musiclist.get(posistion));
					
					ThemeMusic pausethemeMusic = MusicUtil.PauseThememusic(SystemValue.themeSet,themeMusicPause);
					Pausethememusic(pausethemeMusic);
					
					APPThemeMusic pauseappThemeMusic = MusicUtil.PauseAppthemeMusic(SystemValue.themeSet,pausethemeMusic);
					List<APPThemeMusic> list1=new APPThemeMusicDao(SceneSetActivity.this).GetAppThemeMusicListByThemeNo(themeNo);
					if (list1.size()>0) {
						new APPThemeMusicDao(SceneSetActivity.this).UpdateAppThemeMusci(pauseappThemeMusic);
					}else{
						new APPThemeMusicDao(SceneSetActivity.this).InsertAppThemeMusic(pauseappThemeMusic);
					}
					break;
				case 2:
					// 删除本地sqlite 2016-06-24
					new APPThemeMusicDao(SceneSetActivity.this).DeleteAppThemeMusic(themeNo);
					// 服务器上也要删除
					DeleteThemeMusic(themeNo, SystemValue.gatewayid);
					
//					// 通过服务器 JPush 推送删除情景音乐到七寸屏上
//					APPThemeMusic deleteappThemeMusic = new APPThemeMusic();
//					deleteappThemeMusic.setThemeNo(themeNo);
//					MusicJpush.SendThemeMusicToJpush(deleteappThemeMusic);
					MusicPosition = -1;
					Message mesg1 = new Message();
					mesg1.what = 0x130;
					handler.sendMessage(mesg1);
					break;
				default:
					break;
				}
				arg0.cancel();
			}
		});
		builder.create().show();
	}

	private void InsideThemeMusicChoiceDialog(final int position) {

		final String items[] = { "播放音乐", "暂停音乐", "删除音乐" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("联动音乐设置");
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
				case 0:
					APPThemeMusic appThemeMusic = MusicUtil.GetAppThemeMusic(musiclist.get(position), SystemValue.themeSet);
					List<APPThemeMusic> list = new APPThemeMusicDao(SceneSetActivity.this).GetAppThemeMusicListByThemeNo(SystemValue.themeSet
									.getThemeNo());
					if (list.size() > 0) {
						new APPThemeMusicDao(SceneSetActivity.this).UpdateAppThemeMusci(appThemeMusic);
					} else {
						new APPThemeMusicDao(SceneSetActivity.this).InsertAppThemeMusic(appThemeMusic);
					}
					
					// MusicOrder order =
					// MusicUtil.SetthememusicOnInside(appThemeMusic);
					// sendSocket(order);
					// 刷新音乐列表，让被点击的歌曲颜色变换
					MusicPosition = position;
					Message mesg = new Message();
					mesg.what = 0x130;
					handler.sendMessage(mesg);
					break;
				case 1:
					APPThemeMusic pauseappThemeMusic = MusicUtil.PauseAppthemeMusic(SystemValue.themeSet,musiclist.get(position));
					List<APPThemeMusic> list1=new APPThemeMusicDao(SceneSetActivity.this).GetAppThemeMusicListByThemeNo(themeNo);
					if (list1.size()>0) {
						new APPThemeMusicDao(SceneSetActivity.this).UpdateAppThemeMusci(pauseappThemeMusic);
					}else{
						new APPThemeMusicDao(SceneSetActivity.this).InsertAppThemeMusic(pauseappThemeMusic);
					}
					MusicPosition = -1;
					break;
				case 2:
					// 删除本地sqlite 2016-06-24
					new APPThemeMusicDao(SceneSetActivity.this)
							.DeleteAppThemeMusic(themeNo);
					// 通过socket发送删除情景音乐到七寸屏n
					themeMusic_songName = null;
					// MusicOrder deleteorder =
					// MusicUtil.getMusicOrder(themeNo);
					// sendSocket(deleteorder);
					MusicPosition = -1;
					Message mesg1 = new Message();
					mesg1.what = 0x130;
					handler.sendMessage(mesg1);
					break;

				default:
					break;
				}

			}
		});
		builder.create().show();
	}
	
}

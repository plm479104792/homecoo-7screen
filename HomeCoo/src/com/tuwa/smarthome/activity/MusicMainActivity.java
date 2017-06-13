package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.DeviceSwitchActivity.SwitchsAdapter.Holder;
import com.tuwa.smarthome.dao.APPMusicDao;
import com.tuwa.smarthome.entity.APPMusic;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.entity.Mp3;
import com.tuwa.smarthome.entity.Music;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.Volume;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.MusicPlayService;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketBinder;
import com.tuwa.smarthome.util.MusicJpush;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.MusicUtils;
import com.tuwa.smarthome.util.ToastUtils;

public class MusicMainActivity extends Activity {
	
//	private SocketService musicService;
	private MusicPlayService musicService=null;

	// 手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
	float x1 = 0;
	float x2 = 0;
	float y1 = 0;
	float y2 = 0;

	@Bind(R.id.tv_head_submit)
	TextView tvExit;
	@Bind(R.id.tv_head_back)
	TextView tvBack;
	@Bind(R.id.tv_head_title)
	TextView tvTitle;
	@Bind(R.id.tv_bottom_network)
	TextView tvbttomNetwork;

	private ListView listView;
	private TextView btn_allSongs;
	private SimpleAdapter adapter;
	boolean isReturePlaylist;
	private int type = -1; // 这个是做什么的
	private static List<Mp3> songs;// 歌曲集合
//	private List<String> al_playlist;// 播放列表集合
//	private MusiService musicService;
	public static final int PLAYLIST = 1;// 适配器加载的数据是歌曲列表 /*这个是做什么的没有看懂 */
	public static final int SONGS_LIST = 2;// 适配器加载的数据是歌曲列表
	public static final int ALL_SINGERS_LIST = 3;// 适配器加载的数据是歌手列表
	public static final int ALL_ALBUMS_LIST = 4;// 适配器加载的数据是专辑列表
//	private LocweatherApplication application;
	public static boolean isForeground;
	public static String wgid = SystemValue.gatewayid; // 之后要给成给的SystemValue.gatewayid
	private ImageButton mFrontImageButton, mPauseImageButton, mNextImageButton;
	private TextView tv_curcentTime, tv_allTime;
	private SeekBar seekBar1;// 播放进度条
	private static int MusicPosition=-1;
	private static AudioManager manager;
//	public static MusicMainActivity instance;

	private musicMessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.tuwa.smarthome.permission.JPUSH_MESSAGE";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private static Intent service;
	private boolean musicJpushFlag;

	Runnable updateThread = new Runnable() {
		public void run() {
			// 获得歌曲的长度并设置成播放进度条的最大值
			seekBar1.setMax(musicService.getDuration());
			// 获得歌曲现在播放位置并设置成播放进度条的值
			seekBar1.setProgress(musicService.getCurrent());
			// tv_singerName.setText(musicService.getSingerName());
			tv_curcentTime.setText(formatTime(musicService.getCurrent()));
			tv_allTime.setText(formatTime(musicService.getDuration()));
			// 每次延迟100毫秒再启动线程
			if (musicService.isPlay()) {
				// 获得歌曲的长度并设置成播放进度条的最大值
				seekBar1.setMax(musicService.getDuration());
				// 获得歌曲现在播放位置并设置成播放进度条的值
				seekBar1.setProgress(musicService.getCurrent());
				// tv_singerName.setText(musicService.getSingerName());
				tv_curcentTime.setText(formatTime(musicService.getCurrent()));
				tv_allTime.setText(formatTime(musicService.getDuration()));
				// 每次延迟100毫秒再启动线程
			} else {
				
			}
			handler.postDelayed(updateThread, 100);
		}
	};
	
	
	/* 辅线程动态刷新页面 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x129:
				if (musicService!=null) {
					if (musicService.isPlay() ) {
						mPauseImageButton.setBackgroundResource(R.drawable.play001);
					}
					if (musicService.getCurrent()!=0) {
						handler.post(updateThread);
					}
				}else{
					Log.i("343", "musicService为空！");
				}
				break;
			case 001:
				if (musicJpushFlag) {
					String musicJpush = getIntent().getStringExtra("jpushMsgMusic");
					CtrlMusic(musicJpush);
				}
				break;
			}
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_musicpage);
		ButterKnife.bind(MusicMainActivity.this);
		registerMessageReceiver();
		tvExit.setText("退出");
//		tvTitle.setText("音乐");
		tvTitle.setText(SystemValue.SCREEN_IP);
		initViews();
		setListener();
		songs = MusicUtils.getAllSongs(MusicMainActivity.this); // 得到媒体库中的全部歌曲
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		listItems.clear();
		if (songs != null) {
			listItems = MusicUtil.ToListMap(songs);
		}
//		
//		if (musicService==null) {
//			SystemValue.musicPlayService =new MusicPlayService();
//		    SystemValue.musicPlayService.onCreate();
		musicService=SystemValue.musicPlayService;
//		}
		
		initListener();
		adapter = new SimpleAdapter(MusicMainActivity.this, listItems,
				R.layout.item4music_main_activity, new String[] { "songName" },
				new int[] { R.id.tv_songName });
		type = SONGS_LIST;
		listView.setAdapter(adapter);
	    musicJpushFlag = getIntent().getBooleanExtra("MusicJpushNew",false);
	    
		VolumeHandler();
		Message message=new Message();
		message.what=001;
		handler.sendMessage(message);
		
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);
		
	}

	private void setListener() {

		// 暂停OR开始
		mPauseImageButton.setOnClickListener(PlayOrPauseListener);
		// 下一首
		mFrontImageButton.setOnClickListener(NextSongListener);
		// 上一首
		mNextImageButton.setOnClickListener(FrontSongListener);
		// //随机
		// random.setsetOnClickListener(new ImageButton.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// songs = MusicUtils.getAllSongs(MusicMainActivity.this);
		// musicService.RandomPlayMusic(songs.get(0).getUrl());
		// }
		// });
		//
		// 单曲
		// single.etsetOnClickListener(new ImageButton.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// songs = MusicUtils.getAllSongs(MusicMainActivity.this);
		// musicService.singlePlay(songs.get(0).getUrl());
		// }
		// });

		// 从移动后的位置开始播放
		seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// fromUser判断是用户改变的滑块的值
				if (musicService != null) {
					if (fromUser == true) {
						musicService.movePlay(progress);
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	public void initListener() {
		if (songs!=null) {
			btn_allSongs.setOnClickListener(SyncMusicToServerListener);
			tvTitle.setOnClickListener(SyncMusicToServerListener);
		}
		listView.setOnItemClickListener(PlayMusicListener);
	}

	/**
	 * @Descripton:将歌曲列表同步到服务器上
	 * @author xiaobai
	 * @param:List<Mp3>
	 * @param:gatewayNo
	 * */
	private OnClickListener SyncMusicToServerListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			
			VolumeHandler();
			//往本地sqlite上添加音乐
			List<APPMusic> list=MusicUtil.GetListAppMusicBYLsitMp3(songs);
			new APPMusicDao(MusicMainActivity.this).InsertAppMusic(list);
			
			songs = MusicUtils.getAllSongs(MusicMainActivity.this); // 得到媒体库中的全部歌曲
			List<Music> musicList = MusicUtil.ToMusicList(songs);
			String musicJson = JSONArray.toJSONString(musicList);
			RequestParams params = new RequestParams();
			params.addBodyParameter("musicJson", musicJson);
			params.addBodyParameter("gatewayNo", SystemValue.gatewayid);
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.send(HttpMethod.POST,
					NetValue.MUSIC_SEND_MUSIC_TO_SERVER, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							ResultMessage msg = JSONObject.parseObject(
									arg0.result, ResultMessage.class);
							if (msg != null) {
								if (msg.getResult().equals(
										NetValue.SUCCESS_MESSAGE)) {
									ToastUtils.showToast(
											MusicMainActivity.this,
											msg.getMessageInfo(), 1000);
								} else {
									ToastUtils.showToast(
											MusicMainActivity.this,
											msg.getMessageInfo(), 1000);
								}
							}
						}
					});
		}
	};

	/**
	 * @Description:点击歌曲列表，播发音乐
	 * */
	private OnItemClickListener PlayMusicListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			switch (type) {
			case SONGS_LIST:
				if (musicService==null) {
//					SystemValue.musicPlayService =new MusicPlayService();
//				    SystemValue.musicPlayService.onCreate();
				    musicService=SystemValue.musicPlayService;
				}
				musicService.setCurrentListItme(position);
				musicService.setSongs(songs);
				musicService.playMusic(songs.get(position).getUrl());
				mPauseImageButton.setBackgroundResource(R.drawable.pause001);
//				System.out.println("点击歌曲" + musicService.getCurrentListItme()
//						+ " ==  " + musicService.getSongs() + " === "
//						+ musicService.getDuration());
				// 启动
				handler.post(updateThread);
				MusicPosition=position;
				mPauseImageButton.setBackgroundResource(R.drawable.play001);
				SystemValue.MUSIC_STYLE=SystemValue.MUSIC_STYLE_LIST;
				break;
			}
		}
	};

	/**
	 * @Description:暂停或者播放音乐
	 * */
	private OnClickListener PlayOrPauseListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (musicService != null) {
				musicService.pausePlay();
				if (musicService.isPlay()) {
					mPauseImageButton.setBackgroundResource(R.drawable.play001);
				} else {
					mPauseImageButton.setBackgroundResource(R.drawable.pause001);
				}
			}else{
				musicService.pausePlay();
				if (musicService.isPlay()) {
					mPauseImageButton.setBackgroundResource(R.drawable.play001);
				} else {
					mPauseImageButton.setBackgroundResource(R.drawable.pause001);
					System.out.println("当前播放进度 : "+musicService.getCurrent());
				}
			}

		}
	};

	/**
	 * @Descritpion:下一曲
	 * */
	private OnClickListener NextSongListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (musicService != null) {
				musicService.pausePlay();
				musicService.nextMusic();
			}
//			mPauseImageButton.setBackgroundResource(R.drawable.pause001);
			mPauseImageButton.setBackgroundResource(R.drawable.play001);
		}
	};

	/**
	 * @Descritpion:上一首
	 * */
	private OnClickListener FrontSongListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (musicService != null) {
				musicService.pausePlay();
				musicService.frontMusic();
			}
//			mPauseImageButton.setBackgroundResource(R.drawable.pause001);
			mPauseImageButton.setBackgroundResource(R.drawable.play001);
		}
	};

	// 所有歌曲列表显示
	@Override
	protected void onResume() {
		super.onResume();
//		initListener();
		// 这里要重新刷新列表，因为跳到列表歌曲界面时可能把这个列表删了，
		// 所有再跳回来当然要刷新，另外新建列表再回来肯定要刷新的
		if (isReturePlaylist) {
			List<Map<String, Object>> listItems = MusicUtil.ToListMap(songs);
			adapter = new SimpleAdapter(MusicMainActivity.this, listItems,
					R.layout.item4music_main_activity, new String[] { "id",
							"songName", "singerName" }, new int[] { R.id.tv_id,
							R.id.tv_songName, R.id.tv_singerName });
			listView.setAdapter(adapter);
			isReturePlaylist = false;
			handler.post(updateThread);
		}
		isForeground = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		isForeground = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMessageReceiver);
	}

	/**
	 * @Description:动态注册广播
	 * */
	public void registerMessageReceiver() {
		mMessageReceiver = new musicMessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class musicMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);// JPush推送过来的消息
				System.out.println("message" + messge.toString());//
				CtrlMusic(messge);
			}
		}
	}

	/**
	 * @Description:音乐控制 根据收到的推送消息 播放 暂停 下一首 上一首 控制
	 * @author xiaobai
	 * @Date:2016-06-03 21:21:45
	 * */
	private void CtrlMusic(String musicOrder) {
		if (musicService==null) {
			if (SystemValue.musicPlayService==null) {
				SystemValue.musicPlayService =new MusicPlayService();
				SystemValue.musicPlayService.onCreate();
			}
			musicService=SystemValue.musicPlayService;
		}
		// 获取收到的 jpush 的数据 转换为
		Jpush jpush = JSON.parseObject(musicOrder, Jpush.class);
		try {
			MusicOrder order = JSON.parseObject( jpush.getObject().toString(), MusicOrder.class);
			Log.i("Multicast","MusicMainActivivty  收到JPush的推送消息	:"+order.toString());
			/**
			 * 根据pause判断是否是暂停控制 然后根据歌曲名称进行查询播放
			 */
			if (order.getOrder().equals(SystemValue.MUSIC_CTRL_PAUSE)) {
				if (order.getBz()!=null && !order.getBz().equals("")) {
					//情景音乐中 order.bz 有值   用于解决暂停情景音乐 点击两次会播放音乐
					musicService.getmMediaPlayer().pause();
				}else{
					//单控音乐order.bz没有值
					musicService.pausePlay();
				}
			}else if (order.getOrder().equals(SystemValue.MUSIC_VOLUME)) {
				manager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.valueOf(order.getSongName()), AudioManager.FLAG_PLAY_SOUND);
				Volume volume=MusicUtil.GetVolume(order.getSongName());
				MusicJpush.SendVolumeToServer(volume);
			} else {
//				songs = MusicUtils.getAllSongs(MusicMainActivity.this);
				for (int i = 0; i < songs.size(); i++) {
					Mp3 mp3 = songs.get(i);
					if (order.getSongName().equals(mp3.getName())) {
						musicService.setCurrentListItme(i);
						musicService.setDuration(songs.get(i).getDuration());
						musicService.setSongs(songs);
						int musicorder = MusicUtil.JudgeMusicOrder(order);
						if (musicorder == SystemValue.MUSIC_CTRL_ORDER) {
							musicService.playMusic(mp3.getUrl());
						} else if (musicorder == SystemValue.MUSIC_STYLE_ORDER) {
							if (!musicService.isPlay()) { // 当前未在播放
								musicService.playMusic(mp3.getUrl());
							}
							SystemValue.MUSIC_STYLE = order.getOrder();
						} else if (musicorder == SystemValue.MUSIC_ERROR_ORDER) {
							// 不做处理 只是报错
						}
						handler.post(updateThread);
						break;
					}
				}
			}
		} catch (Exception e) {
			System.err.println("解析异常" + e);
		}

	}

	/**
	 * 音量控制 将变化的音量数值同步到服务器上
	 * */
	private void VolumeHandler(){
		manager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
		String cc=String.valueOf(manager.getStreamVolume(AudioManager.STREAM_MUSIC));
		if (!cc.equals(SystemValue.MUSIC_VOLUME_LAST) || SystemValue.MUSIC_VOLUME_LAST!="") {
			SystemValue.MUSIC_VOLUME_LAST=cc;
			Volume volume=MusicUtil.GetVolume(cc);
//			ToastUtils.showToast(MusicMainActivity.this,"`````````````````````````"+cc, 3000);
			MusicJpush.SendVolumeToServer(volume);
		}
	}
	
	
	/**
	 * 格式化时间，将其变成00:00的形式
	 */
	public String formatTime(int time) {
		int secondSum = time / 1000;
		int minute = secondSum / 60;
		int second = secondSum % 60;

		String result = "";
		if (minute < 10)
			result = "0";
		result = result + minute + ":";
		if (second < 10)
			result = result + "0";
		result = result + second;

		return result;
	}

	/*** 退出系统 ***/
	@OnClick(R.id.tv_head_submit)
	public void systemExit() {
		// initExitDialog();
	}

	/*** 返回 ***/
	@OnClick(R.id.tv_head_back)
	public void back() {
		Intent intent = new Intent(MusicMainActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/*** 空间 ***/
	@OnCheckedChanged(R.id.rb_navi_space)
	public void spaceDeviceShow() {
		Intent intent = new Intent(MusicMainActivity.this,
				SpaceDevicesActivity.class);
		startActivity(intent);
		finish();
	}

	/*** 情景模式 ***/
	@OnCheckedChanged(R.id.rb_navi_scene)
	public void sceneMode() {
		Intent sceneIntent = new Intent(MusicMainActivity.this,
				SceneModelActivity.class);
		startActivity(sceneIntent);
		finish();
	}

	// /*** 网络切换 ***/
	// @OnClick(R.id.tv_bottom_network)
	// public void networkSwitchClick() {
	// netWorkSwitch(musicService, tvbttomNetwork);
	// }

	/*** 防区管理 ***/
	@OnCheckedChanged(R.id.rb_navi_alert)
	public void defenceArea() {
		Intent intent = new Intent(MusicMainActivity.this,
				DefenceAreaActivity.class);
		startActivity(intent);
		finish();
	}

	/*** 系统设置 ***/
	@OnCheckedChanged(R.id.rb_navi_set)
	public void systemSet() {
		Intent intent = new Intent(MusicMainActivity.this, SetActivity.class);
		startActivity(intent);
		finish();
	}

	protected void initViews() {
		musicService=SystemValue.musicPlayService;

		if (NetValue.netFlag == NetValue.INTRANET) {
			tvbttomNetwork.setText("本地"); // 任务栏显示网络状态
		} else if (NetValue.netFlag == NetValue.OUTERNET) {
			tvbttomNetwork.setText("远程"); // 任务栏显示网络状态
		}
		
		listView = (ListView) this.findViewById(R.id.listview); // 主页面上的列表
		btn_allSongs = (TextView) this.findViewById(R.id.btn_allSongs); // 主页面上的歌曲按钮
		mFrontImageButton = (ImageButton) findViewById(R.id.LastImageButton);
		mPauseImageButton = (ImageButton) findViewById(R.id.PauseImageButton1);
		mNextImageButton = (ImageButton) findViewById(R.id.NextImageButton);
		tv_curcentTime = (TextView) findViewById(R.id.tv_curcentTime);
		tv_allTime = (TextView) findViewById(R.id.tv_allTime);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		
	}

	protected void initEvents() {
	}

	/***************** 左右滑动事件监听 ******************/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 继承了Activity的onTouchEvent方法，直接监听点击事件
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 当手指按下的时候
			x1 = event.getX();
			y1 = event.getY();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// 当手指离开的时候
			x2 = event.getX();
			y2 = event.getY();
			if (x1 - x2 > 50) { // 向左滑动				System.out.println("==《《《=向左滑动====");
				Intent intent = new Intent(MusicMainActivity.this,SceneQuickActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.out_to_left,
						R.anim.in_from_right);
			} else if (x2 - x1 > 50) { // 向右滑动
				System.out.println("===向右滑动==》》》==");
				Intent intent = new Intent(MusicMainActivity.this,HomeActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	
}

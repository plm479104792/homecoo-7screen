package com.tuwa.smarthome.network;

import java.io.IOException;
import java.util.List;

import com.tuwa.smarthome.entity.Mp3;
import com.tuwa.smarthome.global.SystemValue;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.util.Log;

/**
 * 单独的音乐播放service   2016-09-27   
 * */
public class MusicPlayService extends Service {
	
	//音乐Service集成
	private MediaPlayer  mMediaPlayer;  
    private int currentTime = 0;//歌曲播放进度
	private int currentListItme = -1;//当前播放第几首歌
	private List<Mp3> songs;//要播放的歌曲集合
	private Object duration;
	private static String a;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		  if (mMediaPlayer == null) {
  			mMediaPlayer = new MediaPlayer();
  		}
	}
	
	//音乐集成
			/**
			 *得到当前播放进度 
			 */
			public int getCurrent() {
				if (mMediaPlayer.isPlaying()) {
					return mMediaPlayer.getCurrentPosition();
				} else {
					return currentTime;
				}
			}
			
			/**
			 *	跳到输入的进度 
			 */
			public void movePlay(int progress) {
				mMediaPlayer.seekTo(progress);
				currentTime = progress;
			}
			/**
			 * 随机播放
			 * */
			public void RandomPlayMusic(String path){
				System.out.println("randomplaymusic	:	"+path);
				try {
			//		mMediaPlayer.reset();
					mMediaPlayer.setDataSource(path);
					mMediaPlayer.prepare();
					mMediaPlayer.start();
					mMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
						@Override
						public void onCompletion(MediaPlayer mp) {
							RandomNext();
						}
						
					});
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			public void RandomNext(){
				if(++currentListItme >=songs.size()){
					currentListItme=0;
				}
				Math.random();
				int b=(int) (1+Math.random()*songs.size());
				currentListItme=b-1;
				System.out.println("randomNext"+currentListItme);
				try {
					playMusic(songs.get(currentListItme).getUrl());
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}
			
			/*
			 * 单曲循环播放
			 * */
			public void singlePlay(String path){
				if(a==null){
					a=path;
				}
				System.out.println("singlePlay");
				try {
			//		mMediaPlayer.reset();
					mMediaPlayer.setDataSource(path);
					mMediaPlayer.prepare();
					mMediaPlayer.start();
					mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							singlePlay(a);
						}
					});
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
			/**
			 *	根据歌曲存储路径播放歌曲 n
			 */
			public void playMusic(String path) {
				try {
					/* 重置MediaPlayer */
					if (path!=null) {
						a=path;
					}
					mMediaPlayer.reset();
					/* 设置要播放的文件的路径 */
					mMediaPlayer.setDataSource(path);
					// mMediaPlayer = MediaPlayer.create(this,
					// R.drawable.bbb);播放资源文件中的歌曲
					/* 准备播放 */
					mMediaPlayer.prepare();
					/* 开始播放 */
					mMediaPlayer.start();
					mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer arg0) {
							// 播放完成一首之后进行下一首
//							nextMusic();
							/**
							 * @Description:音乐播放风格
							 * @Date:2016-06-04
							 * @author xiaobai
							 * */
							if (SystemValue.MUSIC_STYLE.equals(SystemValue.MUSIC_STYLE_LIST)) {
								nextMusic();
							}else if (SystemValue.MUSIC_STYLE.equals(SystemValue.MUSIC_STYLE_RANDOM)) {
								RandomNext();
							}else if (SystemValue.MUSIC_STYLE.equals(SystemValue.MUSIC_STYLE_SINGER)) {
								playMusic(a);
							}else{
								nextMusic();
							}
						}
					});
				} catch (IOException e) {
				}
			}
			
			/* 下一首 */
			public void nextMusic() {
				if (++currentListItme >= songs.size()) {
					currentListItme = 0;
				}
				playMusic(songs.get(currentListItme).getUrl());
			}

			/* 上一首 */
			public void frontMusic() {
				Log.v("itme", currentListItme + "hree");
				if (--currentListItme < 0) {
					currentListItme = songs.size() - 1;
				}
				playMusic(songs.get(currentListItme).getUrl());
			}
			
			/**
			 *	歌曲是否真在播放 
			 */
			public boolean isPlay() {
				return mMediaPlayer.isPlaying();
			}
			
			/**
			 *	暂停或开始播放歌曲 
			 */
			public void pausePlay() {
				if (mMediaPlayer.isPlaying()) {
					currentTime = mMediaPlayer.getCurrentPosition();
					mMediaPlayer.pause();
				} else {
					mMediaPlayer.start();
				}
			}
			


			public String getSongName(){
				return songs.get(currentListItme).getName();
			}
			
			public String getSingerName(){
				return songs.get(currentListItme).getSingerName();
			}
			
			public MediaPlayer getmMediaPlayer() {
				return mMediaPlayer;
			}

			public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
				this.mMediaPlayer = mMediaPlayer;
			}

			public int getCurrentListItme() {
				return currentListItme;
			}

			public void setCurrentListItme(int currentListItme) {
				this.currentListItme = currentListItme;
			}

			public int getDuration() {
				return mMediaPlayer.getDuration();
			}
			public void setDuration(int i){
				this.duration = duration;
			}

			public List<Mp3> getSongs() {
				return songs;
			}

			public void setSongs(List<Mp3> songs) {
				this.songs = songs;
			}
	
	
}

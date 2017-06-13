package com.tuwa.smarthome.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.tuwa.smarthome.database.DatabaseHelper;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.JpushReceive;

public class APPThemeMusicDao {
	
	private Context context;
	private Dao<APPThemeMusic,Integer> appThemeMusicDao;
	private DatabaseHelper helper;
	@SuppressWarnings("unchecked")
	public APPThemeMusicDao(Context context) {
		super();
		this.context = context;
		try {
			helper=DatabaseHelper.getHelper(context);
			appThemeMusicDao=helper.getDao(APPThemeMusic.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description: 添加情景音乐
	 * */
	public void InsertAppThemeMusic(APPThemeMusic appThemeMusic){
		try {
			appThemeMusicDao.create(appThemeMusic);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @Description:根据themeid 删除情景音乐
	 * @param themeid
	 * */
	public void DeleteAppThemeMusic(String themeid){
		try {
			DeleteBuilder<APPThemeMusic, Integer> deleteBuilder=appThemeMusicDao.deleteBuilder();
			deleteBuilder.where().eq("THEMENO",themeid);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description:根据themeid 修改情景音乐
	 * */
	public void UpdateAppThemeMusci(APPThemeMusic appThemeMusic){
		
		try {
			UpdateBuilder<APPThemeMusic, Integer> updateBuilder=appThemeMusicDao.updateBuilder();
			updateBuilder.where().eq("THEMENO", appThemeMusic.getThemeNo());
			updateBuilder.updateColumnValue("SONGNAME", appThemeMusic.getSongName());
			updateBuilder.updateColumnValue("STYLE", appThemeMusic.getStyle());
			updateBuilder.updateColumnValue("DEVICESTATE", appThemeMusic.getDeviceState());
			updateBuilder.updateColumnValue("THEMENAME", appThemeMusic.getThemeName());
			updateBuilder.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @Description:获取情景音乐
	 * @param themeid
	 * */
	public List<APPThemeMusic> GetAppThemeMusicListByThemeNo(String themeid){
		List<APPThemeMusic> list=new ArrayList<APPThemeMusic>();
		try {
			list=appThemeMusicDao.queryBuilder().where().eq("THEMENO", themeid).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * @Description：根据网关id 获取所有的情节音乐
	 * @param gatewayid
	 * **/
	public List<APPThemeMusic> GetAppThemeMusicListByGatewayNo(String gatewayNo){
		List<APPThemeMusic> list=new ArrayList<APPThemeMusic>();
		try {
			list=appThemeMusicDao.queryBuilder().where().eq("GATEWAYNO", gatewayNo).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	/**
	 * 根据deviceNo获取情景音乐   用于联动音乐 
	 * update 2016-09-04
	 * */
	public APPThemeMusic GetAppThemeMusicByDeviceNo(String deviceNo){
		List<APPThemeMusic> list=new ArrayList<APPThemeMusic>();
		try {
			list=appThemeMusicDao.queryBuilder().where().eq("DEVICENO",deviceNo).and()
								.eq("GATEWAYNO",SystemValue.gatewayid).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (list.size()>0) {
			return list.get(0);
		}else {
			return null;
		}
		
	}
	
	/**
	 * 根据deviceNo 和 devicestate 获取硬件面板上情景音乐
	 * */
	public APPThemeMusic GetAppThemeMusicByDeviceNoAndDeviceState(String deviceNo,String deviceState){
		List<APPThemeMusic> list=new ArrayList<APPThemeMusic>();
		try {
			list=appThemeMusicDao.queryBuilder().where()
					.eq("DEVICENO",deviceNo).and()
					.eq("DEVICESTATE",deviceState).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (list.size()>0) {
			return list.get(0);
		}else {
			return null;
		}	}
	
	/**
	 * 
	 * 先判断该情景是否已经有联动音乐，有更新，没有删除
	 * */
	public void UpdateOrAddThemeMusicByThemeNo(APPThemeMusic appThemeMusic){
		List<APPThemeMusic> list=GetAppThemeMusicListByThemeNo(appThemeMusic.getThemeNo());
		if (list.size()>0) {
			UpdateAppThemeMusci(appThemeMusic);
		}else {
			InsertAppThemeMusic(appThemeMusic);
		}
	}
	
	
	/**
	 * @Description:根据网关id 获取所有的情景音乐
	 * @param gatewayid
	 * */
	public List<APPThemeMusic> GetAppthemeMusicListByGatewayNo(){
		List<APPThemeMusic> list=new ArrayList<APPThemeMusic>();
		try {
			list=appThemeMusicDao.queryBuilder().where().eq("GATEWAYNO",SystemValue.gatewayid).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	

	/**
	 * 更新联动音乐列表
	 * param:List<AppThemeMusic>
	 * */
	public void UpdateOrSaveAppthemeMusic(List<APPThemeMusic> list){
		try {
			Iterator<APPThemeMusic> iterator=list.iterator();
			while (iterator.hasNext()) {
				APPThemeMusic appThemeMusic = (APPThemeMusic) iterator.next();
				boolean b=ExistedAppthemeMusic(appThemeMusic.getThemeNo());
				if (b) {
					InsertAppThemeMusic(appThemeMusic);
				}else{
					UpdateAppThemeMusci(appThemeMusic);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断是否有该联动音乐了
	 * return boolean
	 * */
	public boolean ExistedAppthemeMusic(String themeid){
		try {
			List<APPThemeMusic> list=appThemeMusicDao.queryBuilder().where().eq("THEMENO", themeid).query();
			return list.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
}

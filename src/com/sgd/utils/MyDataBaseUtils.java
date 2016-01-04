package com.sgd.utils;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sgd.baseDao.cityInfoProvider;


public class MyDataBaseUtils {
	/** Double CheckLock 实现单例模式
	 *  DLC实现双重判断，第一次避免不必要的同步，第二次避免多次创建
	 *  但由于在使用new创建对象时类初始化成员字段与将对象指向内存空间这
	 *  两步是无序的，因而可能存在DCL实效
	 *  在1.5JDK版本后添加volatile修饰单例对象
	 *  */
	private static volatile MyDataBaseUtils instance = null;
	private MyDataBaseUtils() {
	}
	public static MyDataBaseUtils getInstance(){
		if(instance == null){
			synchronized (MyUtils.class) {
				if(instance == null){
					instance = new MyDataBaseUtils();
				}
			}
		}
		return instance;
	}
	/** 将城市信息添加到数据库中 */
	public void addCityInfo(Context context,String cityname){
		ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("cityname", cityname);
		if(!isCityExistInDB(cr,cityname)){
			//如果数据库中不存在当前城市名，则将城市名添加到数据库中
			cr.insert(cityInfoProvider.CONTENT_URI, values);
		}
	}
	/** 查询所有数据库中的城市信息，并返回游标 */
	public Cursor getAllCityInfo(Context context){
		ContentResolver cr = context.getContentResolver();
		String[] result = {"_id","cityname"};
		Cursor cursor = cr.query(cityInfoProvider.CONTENT_URI,result, null, null, null);
		return cursor;
	}
	/** 查询数据库中是否已经存在该城市 ，返回false代表不存在 */
	private boolean isCityExistInDB(ContentResolver cr,String cityname){
		String[] result = {"_id"};
		Cursor cursor = cr.query(cityInfoProvider.CONTENT_URI, result,
				" cityname = ? " ,new String[]{cityname}, null);
		return cursor.moveToFirst();
	}
	/** 根据城市名删除数据库中的数据 */
	public int deleCityByName(Context context,String cityname){
		ContentResolver cr = context.getContentResolver();
		return cr.delete(cityInfoProvider.CONTENT_URI, " cityname = ? " ,new String[]{cityname});
	}
}
















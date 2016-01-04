package com.sgd.baseDao;

import com.sgd.weatherreportdemo.IConstans;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class cityDaoOpenHelper extends SQLiteOpenHelper implements IConstans{
	//创建表的语句
	private String create_database =" CREATE TABLE "
				+ DATABASE_TABLE 
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ " cityname VARCHAR(30) )" ;
	
	public cityDaoOpenHelper(Context context) {
		//第一次进入时新建数据库
		super(context, DATABASE_NAME, null, DATABASE_VISION);
	}			
	public cityDaoOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建表
		db.execSQL(create_database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//删除旧表
		db.execSQL(" DROP TABLE IF EXISTS "+ DATABASE_TABLE);
		//创建新表
		onCreate(db);
	}
	
}



















package com.sgd.baseDao;

import com.sgd.weatherreportdemo.IConstans;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class cityInfoProvider extends ContentProvider implements IConstans{
	public static final Uri CONTENT_URI = Uri.parse(CITY_PROVIDER_URI);
	private static final UriMatcher uriMatcher;
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.sgd.baseDao.cityinfoprovider", "elements", ALLROWS);
		uriMatcher.addURI("com.sgd.baseDao.cityinfoprovider", "elements/#", SINGLE_ROW);
	}
	
	private cityDaoOpenHelper myOpenHelper;
	private static final String KEY_ID = "_id";
	@Override
	public boolean onCreate() {
		//打开数据库
		myOpenHelper = new cityDaoOpenHelper(getContext());
		return true;
	}
	/** 查询 数据 
	 * */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		//打开数据库
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DATABASE_TABLE);
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(KEY_ID+" = "+rowID);
			break;
		default:
			break;
		}
		Cursor cursor = queryBuilder.query(db, projection, selection, 
				selectionArgs, null, null, sortOrder);
		return cursor;
	}

	/**  插入数据 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//打开数据库
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		//想要通过传入一个空Content Value对象的方式向数据库添加一个空行
		//必须使用nullColumnHack参数来指定可以设置为null的列名
		String nullColumnHack = null;
		long id = db.insert(DATABASE_TABLE, nullColumnHack, values);
		//构造并返回新插入航的Uri
		if(id > -1){
			Uri insertedID = ContentUris.withAppendedId(CONTENT_URI, id);
			//通知所有观察者，数据集已经改变
			getContext().getContentResolver().notifyChange(insertedID, null);
			return insertedID;
		}		
		return null;
	}
	/** 删除数据 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		//打开数据库
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DATABASE_TABLE);
		switch (uriMatcher.match(uri)) {
		//如果是行id，限定删除的行为指定的行
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID+" = "+rowID 
					+ (!TextUtils.isEmpty(selection) ? " AND ( "+selection +" ) " :"");
			break;
		default:
			break;
		}
		//想要返回删除项的数量，必须指定一条where语句。删除所有的行并返回一个值，同时传入"1"
		if(selection == null){
			selection = "1";
		}
		int deleteCount = db.delete(DATABASE_TABLE, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}
	/** 更新数据库 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		//打开数据库
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DATABASE_TABLE);
		switch (uriMatcher.match(uri)) {
		//如果是行id，限定删除的行为指定的行
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID+" = "+rowID 
					+ (!TextUtils.isEmpty(selection) ? " AND ( "+selection +" ) " :"");
			break;
		default:
			break;
		}
		int updateCount = db.update(DATABASE_TABLE, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}
}






















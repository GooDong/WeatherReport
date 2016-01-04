package com.sgd.app;

import org.xutils.x;

import android.app.Application;

public class MyApp extends Application{
	@Override
	public void onCreate() {
		//初始化
		x.Ext.init(this);
		//记录日志
		//x.Ext.setDebug(true);
	}
	
}

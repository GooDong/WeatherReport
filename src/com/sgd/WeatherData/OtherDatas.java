package com.sgd.WeatherData;

import java.util.HashMap;

public class OtherDatas {
	
	public String airAqiDetail;//PM2.5数
	
	public String aqi;//空气指数
	
	public String sd;//湿度
	public String wind_direction;//风向
	public String wind_power;//风力
	
	public String so2;//二氧化硫1小时平均
	public String pm10;//颗粒物（粒径小于等于10μm）1小时平均
	public String o3_8h;//臭氧8小时平均
	
	
	
	public String latitude;//维度
	public String longitude;//经度
	public String c15;//海拔
	public String c16;//雷达号
	
	
	public HashMap<String,String> datas;
	
	public  HashMap<String,String> getDataMap(){
		datas = new HashMap<String,String>();
		this.datas.put("湿度", sd);
		this.datas.put("风向/风力",wind_direction+"/"+wind_power);
		this.datas.put("维度", latitude);
		this.datas.put("经度", longitude);
		this.datas.put("海拔", c15);
		this.datas.put("雷达号", c16);
		this.datas.put("二氧化硫", so2);
		this.datas.put("10μm颗粒", pm10);
		this.datas.put("臭氧平均", o3_8h);
		
		return datas;
	}
	
}

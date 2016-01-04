package com.sgd.utils;

import java.util.ArrayList;

import org.json.JSONObject;

import com.sgd.WeatherData.OtherDatas;
import com.sgd.WeatherData.WeatherDataOfDays;
import com.sgd.WeatherData.WeatherDataOfNow;
import com.sgd.weatherreportdemo.IConstans;

public class WeatherDataUtils implements IConstans{
	/** 静态内部类实现单例模式
	 * 只有在第一次调用getInstance时才会初始化Holder静态类
	 * 因而instance只会被初始化一次  
	 * */
	private WeatherDataUtils() {
	}
	public static WeatherDataUtils getInstance(){
		return Holder.instance;
	}
	/** 静态内部内 */
	private static class Holder{
		private static final WeatherDataUtils instance = new WeatherDataUtils();
	}
	/**
	* 从第一级JSONObject中取出当前、今天、明天、后天的天气数据
	* */
	public ArrayList<WeatherDataOfDays> getWeatherDataList(JSONObject dataBody,boolean isDay){
		 ArrayList<WeatherDataOfDays> weatherDataListTemp = new ArrayList<WeatherDataOfDays>();
		 for (int i = 1; i < DAYS.length; i++) {
			 //依照数组，取出今天、明天、后天……的天气数据
			 JSONObject weatherDay = dataBody.optJSONObject(DAYS[i]);
			 WeatherDataOfDays weatherDataTemp = new WeatherDataOfDays();
			 
			 weatherDataTemp.temperatureDay = weatherDay.optString("day_air_temperature");//最高温度
			 weatherDataTemp.temperatureNight = weatherDay.optString("night_air_temperature");//最低温度
			 weatherDataTemp.sun_begin_end = weatherDay.optString("sun_begin_end");//日出日落时间
			 weatherDataTemp.day = weatherDay.optString("day");//日期
			 if(isDay){
				 weatherDataTemp.weatherIconPath = weatherDay.optString("day_weather_pic");//天气图标的路径
			 }else{
				 weatherDataTemp.weatherIconPath = weatherDay.optString("night_weather_pic");//天气图标的路径
			 }
			
			 
			 weatherDataListTemp.add(weatherDataTemp);
		}
		 return weatherDataListTemp;
	}
	/**
	 * 取出所有天數的溫度數據
	 * */
	public float[][] getWeatherTempeture(JSONObject dataBody){
		 float[][] daysTemperature = new float[2][NUM_WEATHER_DAYS];
		 daysTemperature[0][0] = 0;
		 daysTemperature[1][0] = 0;
		 for (int i = 1; i < DAYS.length; i++) {
			 //依照数组，取出今天、明天、后天……温度数据
			 JSONObject weatherDay = dataBody.optJSONObject(DAYS[i]);
			 
			 daysTemperature[0][i] = Float.parseFloat(weatherDay.optString("day_air_temperature")) ;
			 daysTemperature[1][i] =  Float.parseFloat(weatherDay.optString("night_air_temperature"));
		}
		 return daysTemperature;
	}
	/**
	* 取出实时的天气数据
	* */
	public WeatherDataOfNow getWeatherDataOfNow(JSONObject dataBody) {
		WeatherDataOfNow dataTmep = new WeatherDataOfNow();
		
		JSONObject weatherDay = dataBody.optJSONObject("now");
		
		dataTmep.temperature = weatherDay.optString("temperature");//实时温度
		dataTmep.temperature_time = weatherDay.optString("temperature_time");//获取温度的时间
		dataTmep.weather = weatherDay.optString("weather");//天气状况
		
		dataTmep.weatherIcon = weatherDay.optString("weather_pic");//天气图标的路径
		JSONObject aqiDetail = weatherDay.optJSONObject("aqiDetail");
		dataTmep.airAqi = aqiDetail.optString("quality");//空气质量
		
		
		return dataTmep;
	}
	/**
	 * 取出其他的杂项数据
	 * */
	public OtherDatas getOtherDatas(JSONObject dataBody){
		OtherDatas dataTmep = new OtherDatas();
		
		JSONObject weatherDay = dataBody.optJSONObject("now");
		
		dataTmep.sd = weatherDay.optString("sd");//湿度
		dataTmep.wind_direction = weatherDay.optString("wind_direction");//风向
		dataTmep.wind_power = weatherDay.optString("wind_power");//风力
		dataTmep.aqi = weatherDay.optString("aqi");//空气指数
		
		JSONObject aqiDetail = weatherDay.optJSONObject("aqiDetail");
		
		dataTmep.airAqiDetail = aqiDetail.optString("pm2_5");//颗粒物(PM2.5)
		dataTmep.so2 = aqiDetail.optString("so2");//二氧化硫1小时平均
		dataTmep.pm10 = aqiDetail.optString("pm10");//颗粒物（粒径小于等于10μm）1小时平均
		dataTmep.o3_8h = aqiDetail.optString("o3_8h");//臭氧8小时平均
		
		
		JSONObject weatherData = dataBody.optJSONObject("cityInfo");//城市信息
			
		dataTmep.latitude = weatherData.optString("latitude");//经度
		dataTmep.longitude = weatherData.optString("longitude");//维度
		dataTmep.c15 = weatherData.optString("c15");//海拔
		dataTmep.c16 = weatherData.optString("c16");//雷达号
		return dataTmep;
	}
	
	/**
	 * 得到當前是否是白天
	 * */
	public boolean isDay (JSONObject dataBody){
		int[] times = getTime(dataBody);
		return times[2] > times[1] ?false : true;
	}
	/**
	 * 返回日出日落时间以及当前的时间
	 * */
	public int[] getTime(JSONObject dataBody){
		int[] times = new int[3]; 
		JSONObject weatherDay = dataBody.optJSONObject("f1");
		String sun_begin_end = weatherDay.optString("sun_begin_end");
		
		String sunDown = sun_begin_end.substring(sun_begin_end.lastIndexOf("|")+1).replace(":","");
		String sunRise = sun_begin_end.substring(0,sun_begin_end.lastIndexOf("|")).replace(":","");
		String nowTimeStr = MyUtils.getInstance().getTime();
		times[0] = Integer.parseInt(sunRise+"00");
		times[1] = Integer.parseInt(sunDown+"00");
		times[2] = Integer.parseInt(nowTimeStr.substring(8));
		return times;
	}
	/**
	 * 得到网络数据中的今天的日期
	 * */
	public String getCurrentDate (JSONObject dataBody){
		JSONObject weatherDay = dataBody.optJSONObject("f1");
		String day = weatherDay.optString("day");
		return day;
	}

}














package com.sgd.WeatherData;


public class WeatherDataOfDays {
	public String cityName;//城市名
	public String day;//当前日期
	public String temperatureDay;//白天温度
	public String temperatureNight;//晚上温度
	public String weatherIconPath;//天气图标
	public String sun_begin_end;//日出日落的时间
	
	public String toString(){
		return "白天温度:"+this.temperatureDay;
	}

}

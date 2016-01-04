package com.sgd.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgd.WeatherData.WeatherDataOfDays;
import com.sgd.utils.DownImageLruCacheUtils;
import com.sgd.utils.DownImageLruCacheUtils.OnImgLoadDownLisenter;
import com.sgd.weatherreportdemo.IConstans;
import com.sgd.weatherreportdemo.R;

public class WeatherDayViewAdapter extends BaseAdapter implements IConstans{
	ArrayList<WeatherDataOfDays>  weatherDataList;
	Context mContext;
	Handler handler;
	DownImageLruCacheUtils downImgUtils;
	public WeatherDayViewAdapter(ArrayList<WeatherDataOfDays>  weatherDataList,
			Context mContext,Handler handler,DownImageLruCacheUtils downImgUtils) {
		this.weatherDataList = weatherDataList;
		this.mContext= mContext;
		this.handler = handler;
		this.downImgUtils = downImgUtils;
	}
	@Override
	public int getCount() {
		return weatherDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return weatherDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HandlerView handlerView;
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.weather_days_item, null);
			handlerView = new HandlerView(convertView);
			convertView.setTag(handlerView);
		}else{
			handlerView = (HandlerView) convertView.getTag();
		}
		WeatherDataOfDays tempDay = weatherDataList.get(position);
		handlerView.date.setText(tempDay.day.substring(4,6)+"/"+tempDay.day.substring(6,8));
		handlerView.temperature.setText(tempDay.temperatureDay+"℃/"+tempDay.temperatureNight+"℃");
		Bitmap weatherIcon = downImgUtils.downLoadImgFromIntent(tempDay.weatherIconPath, new OnImgLoadDownLisenter() {
			@Override
			public void onImgLoadDownLisenter(Bitmap bitmap) {
				handler.sendEmptyMessage(REFREASH_DAYS_WEATHRE_ICON);
			}
		});
		if(weatherIcon != null){
			handlerView.weatherIcon.setImageBitmap(weatherIcon);
		}
		return convertView;
	}
	class HandlerView{
		TextView date;
		TextView temperature;
		ImageView weatherIcon;
		public HandlerView(View layout) {
			date = (TextView) layout.findViewById(R.id.txt_date);
			temperature = (TextView) layout.findViewById(R.id.txt_temperature);
			weatherIcon = (ImageView) layout.findViewById(R.id.img_weather);
		}
		
	}
	
}

















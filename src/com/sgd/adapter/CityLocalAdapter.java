package com.sgd.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgd.weatherreportdemo.IConstans;
import com.sgd.weatherreportdemo.R;

public class CityLocalAdapter extends BaseAdapter implements IConstans{
	Handler handler;
	Context context;
	String nowCity;
	public CityLocalAdapter(Context context,String nowCity) {
		this.context = context;
		this.nowCity = nowCity;
	}
	@Override
	public int getCount() {
		return mainCities.length;
	}

	@Override
	public Object getItem(int position) {
		return mainCities[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String cityName = mainCities[position];
		HandlerView handlerView;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.city_info_grid_item,null);
			handlerView = new HandlerView(convertView);
			convertView.setTag(handlerView);
		}else{
			handlerView = (HandlerView) convertView.getTag();
		}
		handlerView.cityName.setText(cityName);
		if(cityName.equals("定位") || cityName.equals(nowCity)){
			handlerView.cityName.setTextColor(Color.argb(0x88, 00, 00, 00));
			handlerView.sign.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	class HandlerView{
		TextView cityName;
		ImageView sign;
		public HandlerView(View view) {
			cityName = (TextView) view.findViewById(R.id.txt_city_name_now);
			sign = (ImageView) view.findViewById(R.id.img_city_sign_now);
		}
	} 
}
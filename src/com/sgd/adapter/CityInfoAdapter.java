package com.sgd.adapter;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgd.weatherreportdemo.R;

public class CityInfoAdapter extends BaseAdapter{
	List<String> cities;
	Context context;
	public CityInfoAdapter(Context context,List<String> cities) {
		this.cities = cities;
		this.context = context;
	}
	@Override
	public int getCount() {
		return cities.size();
	}

	@Override
	public Object getItem(int position) {
		return cities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HandlerView handlerView;
		SharedPreferences sp = context.getSharedPreferences("CurrentCity", Context.MODE_PRIVATE);
		String cityNameForNow = sp.getString("cityNameForNow", "深圳");
		if(convertView == null){
			convertView = View.inflate(context, R.layout.city_info_item,null);
			handlerView = new HandlerView(convertView);
			convertView.setTag(handlerView);
		}else{
			handlerView = (HandlerView) convertView.getTag();
		}
		handlerView.cityName.setText(cities.get(position));
		if(cities.get(position).equals(cityNameForNow)){
			handlerView.sign.setVisibility(View.VISIBLE);
			handlerView.currntSign.setVisibility(View.VISIBLE);
		}else{
			handlerView.sign.setVisibility(View.INVISIBLE);
			handlerView.currntSign.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
	class HandlerView{
		TextView cityName;
		ImageView sign;
		TextView currntSign;
		public HandlerView(View view) {
			cityName = (TextView) view.findViewById(R.id.txt_city_name_item);
			sign = (ImageView) view.findViewById(R.id.img_sign_item);
			currntSign = (TextView) view.findViewById(R.id.txt_city_sign_item);
		}
	} 
}
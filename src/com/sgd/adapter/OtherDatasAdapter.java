package com.sgd.adapter;

import java.util.HashMap;

import com.sgd.weatherreportdemo.IConstans;
import com.sgd.weatherreportdemo.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OtherDatasAdapter extends BaseAdapter implements IConstans{
	HashMap<String, String> otherDatas;
	Context context;
	public OtherDatasAdapter(HashMap<String, String> otherDatas,Context context) {
		this.otherDatas= otherDatas;
		this.context = context;
	}
	@Override
	public int getCount() {
		return otherDatas.values().size();
	}
	@Override
	public Object getItem(int position) {
		return otherDatas.values().toArray()[position];
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HandlerView handlerView;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.weather_otherdatas_item, null);
			handlerView = new HandlerView(convertView);
			convertView.setTag(handlerView);
		}else{
			handlerView = (HandlerView) convertView.getTag();
		}
		handlerView.dataName.setText(OTHER_DATAS[position]);
		handlerView.dataDetail.setText(otherDatas.get(OTHER_DATAS[position]));
		return convertView;
	}
	class HandlerView {
		TextView dataName,dataDetail;
		public HandlerView(View layout) {
			dataName = (TextView) layout.findViewById(R.id.txt_oterdatas_name);
			dataDetail = (TextView) layout.findViewById(R.id.txt_otherdatas_data);
			Animation animation = AnimationUtils.loadAnimation(context, R.anim.datas_show_anim);
			dataDetail.setAnimation(animation );
		}
	}
	public void setOtherDatas(HashMap<String, String> otherDatas) {
		this.otherDatas = otherDatas;
	}
	
}















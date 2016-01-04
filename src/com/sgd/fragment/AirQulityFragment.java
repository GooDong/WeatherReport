package com.sgd.fragment;

import com.sgd.view.AirDataShowView;
import com.sgd.weatherreportdemo.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AirQulityFragment extends Fragment{
	View view;
	LinearLayout linAirQulity;
	TextView textTitle,txtSendTime;
	int data;
	String pm2_5;
	String sendTime = "";
	AirDataShowView airShowView;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.weather_air_qulity_fragment, null);
		
		textTitle = (TextView) view.findViewById(R.id.txt_air_name);
		textTitle.setText("空气质量");
		txtSendTime = (TextView) view.findViewById(R.id.txt_air_send_time);
		txtSendTime.setText("中国环境监测总站于 "+sendTime+" 发布");
		
		airShowView = new AirDataShowView(getActivity(),null);
		setSurfaceViewBkg(airShowView);
		
		linAirQulity = (LinearLayout) view.findViewById(R.id.lin_air_qulity_fragment);
		linAirQulity.addView(airShowView);
		return view;
	}

	private void setSurfaceViewBkg(AirDataShowView airShowView) {
		//将surface的背景颜色改变为将要绘制的背景颜色
		//防止在滑动过程中surfaceView背景显露，与Activity的背景不一致
		airShowView.setBackgroundColor(Color.rgb(0x33, 0x55, 0x88));
		airShowView.setZOrderOnTop(true);
		airShowView.getHolder().setFormat(PixelFormat.TRANSPARENT);
	}
	public void setData(int data) {
		this.data = data;
	}

	public void setPm2_5(String pm2_5) {
		this.pm2_5 = pm2_5;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public  void refreshUI(){
		airShowView.resetDistance(data, pm2_5);
		airShowView.startToDraw();
		txtSendTime.setText("中国环境监测总站于 "+sendTime+" 发布");
	}
	@Override
	public void onResume() {
		//线程恢复的时候重新启动绘制线程
		super.onResume();
	}
	@Override
	public void onPause() {
		//碎片暂停的时候终止绘制线程
		airShowView.endDrawing();
		super.onPause();
	}
}






















package com.sgd.fragment;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.sgd.base.BaseFragment;
import com.sgd.view.AirDataShowView;
import com.sgd.weatherreportdemo.R;
public class AirQulityFragment extends BaseFragment{
	TextView textTitle,txtSendTime;
	int data;
	String pm2_5;
	String sendTime = "";
	@Override
	public int getContentView() {
		return R.layout.weather_air_qulity_fragment;
	}

	@Override
	public void initView() {
		textTitle = (TextView) view.findViewById(R.id.txt_air_name);
		txtSendTime = (TextView) view.findViewById(R.id.txt_air_send_time);
		linearLayout = (LinearLayout) view.findViewById(R.id.lin_air_qulity_fragment);
		
		surfaceView = new AirDataShowView(getActivity(), null);
		this.setSurfaceViewBkg(surfaceView);
		linearLayout.addView(surfaceView);
	}

	@Override
	public void initTxtInfo() {
		textTitle.setText("空气质量");
		txtSendTime.setText("中国环境监测总站于 "+sendTime+" 发布");
	}

	@Override
	public void refreshView() {
		((AirDataShowView)surfaceView).initDistance(data, pm2_5);
		surfaceView.startToDraw();
		initTxtInfo();
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public void setData(int data) {
		this.data = data;
	}
	public void setPm2_5(String pm2_5) {
		this.pm2_5 = pm2_5;
	}
}

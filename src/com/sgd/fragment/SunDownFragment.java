package com.sgd.fragment;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.sgd.base.BaseFragment;
import com.sgd.view.SunDownView;
import com.sgd.weatherreportdemo.R;

public class SunDownFragment extends BaseFragment{
	
	TextView textTitle;
	int[] times;
	
	@Override
	public int getContentView() {
		return R.layout.sun_rise_down_fragment;
	}

	@Override
	public void initView() {
		textTitle = (TextView) view.findViewById(R.id.txt_sun_name);
		linearLayout = (LinearLayout) view.findViewById(R.id.lin_sun_rise_down_fragment);
		
		surfaceView = new SunDownView(getActivity(), null);
		this.setSurfaceViewBkg(surfaceView);
		linearLayout.addView(surfaceView);
	}

	@Override
	public void initTxtInfo() {
		textTitle.setText("日出日落");
	}

	@Override
	public void refreshView() {
		((SunDownView)surfaceView).initDatas(times);
		surfaceView.startToDraw();
		initTxtInfo();
	}
	/** 更新SunDownView上的时间 */
	public void setTimes(int[] times) {
		this.times = times;
	}
}














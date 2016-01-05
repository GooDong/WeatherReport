package com.sgd.fragment;


import android.widget.LinearLayout;

import com.sgd.base.BaseFragment;
import com.sgd.view.TemperatureChangeLineView;
import com.sgd.weatherreportdemo.R;

public class TemperatureChangeLineFragment extends BaseFragment{
	float[][] index;
	int linHeight;
	@Override
	public int getContentView() {
		return R.layout.temperature_change_line_fragment;
	}
	@Override
	public void initView() {
		linearLayout = (LinearLayout)view.findViewById(R.id.lin_temperature_line);
		surfaceView = new TemperatureChangeLineView(getActivity(), null);
		this.setSurfaceViewBkg(surfaceView);
		linearLayout.addView(surfaceView);
		super.initView();
	}
	@Override
	public void refreshView() {
		((TemperatureChangeLineView)surfaceView).setDatasAndHeight(index, linHeight);
		surfaceView.startToDraw();
	}

	public void setLinHeight(int linHeight) {
		this.linHeight = linHeight;
	}

	public void setIndex(float[][] index) {
		this.index = index;
	}
}



















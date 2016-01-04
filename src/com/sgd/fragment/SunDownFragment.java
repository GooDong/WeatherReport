package com.sgd.fragment;

import com.sgd.view.SunDownView;
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

public class SunDownFragment extends Fragment{
	View view;
	LinearLayout linSunDown;
	TextView textTitle;
	int[] times;
	SunDownView sunDownView;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.sun_rise_down_fragment, null);
		textTitle = (TextView) view.findViewById(R.id.txt_sun_name);
		textTitle.setText("日出日落");
		
		sunDownView = new SunDownView(getActivity());
		setSurfaceViewBkg(sunDownView);
		
		linSunDown = (LinearLayout) view.findViewById(R.id.lin_sun_rise_down_fragment);
		linSunDown.addView(sunDownView);
		return view;
	}
	
	private void setSurfaceViewBkg(SunDownView sunDownView) {
		//将surface的背景颜色改变为将要绘制的背景颜色
		//防止在滑动过程中surfaceView背景显露，与Activity的背景不一致
		sunDownView.setBackgroundColor(Color.rgb(0x33, 0x55, 0x88));
		//我们在创建了一个SurfaceView之后，可以调用它的成员函数setZOrderMediaOverlay、
		//setZOrderOnTop或者setWindowType来修改该SurfaceView的窗口类型，
		//也就是修改该SurfaceView的成员变量mWindowType的值。
		sunDownView.setZOrderOnTop(true);
		sunDownView.getHolder().setFormat(PixelFormat.TRANSPARENT);
	}
	/** 更新SunDownView上的时间 */
	public void setTimes(int[] times) {
		this.times = times;
	}
	/** 在view中重新设置参数并启动单线程进行更新 */
	public void refreshView(){
		sunDownView.resetTimes(times);
		sunDownView.startToDraw();
	}
	@Override
	public void onResume() {
		//启动绘制线程
		//sunDownView.startToDraw();
		super.onResume();
	}
	@Override
	public void onPause() {
		//终止绘制线程
		sunDownView.endDrawing();
		super.onPause();
	}
}





























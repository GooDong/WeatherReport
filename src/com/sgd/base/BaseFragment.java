package com.sgd.base;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class BaseFragment extends Fragment implements ViewFragmentInterface{
	public View view;
	public LinearLayout linearLayout;
	public BaseSurfaceView surfaceView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(getContentView(), null);
		initView();
		initTxtInfo();
		return view;
	};
	@Override
	public int getContentView() {
		return 0;
	}
	@Override
	public void initView() {
		
	}
	@Override
	public void initTxtInfo() {
		
	}
	@Override
	public void refreshView() {
		
	}
	@Override
	public void onPause() {
		//碎片暂停的时候终止绘制线程
		surfaceView.endDrawing();
		super.onPause();
	}
	
	public void setSurfaceViewBkg(BaseSurfaceView airShowView) {
		//将surface的背景颜色改变为将要绘制的背景颜色
		//防止在滑动过程中surfaceView背景显露，与Activity的背景不一致
		airShowView.setBackgroundColor(Color.rgb(0x33, 0x55, 0x88));
		airShowView.setZOrderOnTop(true);
		airShowView.getHolder().setFormat(PixelFormat.TRANSPARENT);
	}
}















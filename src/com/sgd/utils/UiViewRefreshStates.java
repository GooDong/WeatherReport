package com.sgd.utils;

import android.util.Log;

public class UiViewRefreshStates {
	private UiViewRefreshStates() {
	}
	public static UiViewRefreshStates getInstance(){
		return UiViewRefreshStatesHolder.instance;
	};
	private static class UiViewRefreshStatesHolder{
		public static final UiViewRefreshStates instance = new UiViewRefreshStates();
	}
	private int currentIndex = 0;
	private float totalHeight = 0;
	private static boolean[] status = new boolean[4];
	
	public boolean isRefreshDatasLin() {
		return baseJudge(10,30,0);
	}
	
	public boolean isOtherDatas() {
		return baseJudge(710,730,1);
	}

	public boolean isRefreshAir() {
		return baseJudge(1710,1730,2);
	}

	public boolean isRefreshSun() {
		return baseJudge(2810,2830,3);
	}

	public void setCurrentIndex(float currentIndex) {
		//将当前高度缩放为4704的比例缩放，屏幕基础尺寸位：小米3，1080*1920
		this.currentIndex = (int) (4704 * (currentIndex / totalHeight));
		Log.i("-----","this.currentIndex "+ this.currentIndex);
	}
	
	public float getTotalHeight() {
		return totalHeight;
	}
	public void setTotalHeight(int totalHeight) {
		this.totalHeight = totalHeight;
	}
	/** 根据当前的位置以及视图的刷新状态返回是否可以刷新
	 * @return true 可以刷新
	 *  */
	private boolean baseJudge(int minIndex,int maxIndex,int index){
		if(currentIndex >= minIndex && currentIndex <= maxIndex && !status[index]){
			reverse(index);
			return true;
		}
		return false;
	}
	/** 翻转刷新状态，第一次刷新过后，直到另一个视图将视图状态翻转否则当前视图不会再次刷新   */
	private void reverse(int index) {
		for (int i = 0; i < status.length; i++) {
			status[i] = ( i == index ? true : false);
		}
	}
}

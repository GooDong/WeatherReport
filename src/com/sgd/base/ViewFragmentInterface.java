package com.sgd.base;

public interface ViewFragmentInterface {
	/** 初始化视图 */
	public void initView();
	/** 初始化文本提示信息 */
	public void initTxtInfo();
	/** 刷新视图 */
	public void refreshView();
	/** 获取当前的布局文件 */
	public int getContentView();
}

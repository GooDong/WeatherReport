package com.sgd.base;

public interface DrawViewInterface {
	/** 视图对象实现自己的绘制线程并通过createDrawThread返回一个新的线程 */
	public abstract Thread createDrawThread();
	/** 视图对象初始化画笔参数 */
	public abstract void initPaint();
	/** 视图对象初始化自己的数据   */
	public abstract void initDatas();
}

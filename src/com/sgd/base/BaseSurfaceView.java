package com.sgd.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class  BaseSurfaceView extends SurfaceView 
implements SurfaceHolder.Callback,DrawViewInterface{
	//获取上下文
	public Context mContext;
	//获取SurfaceHolder
	public SurfaceHolder holder;
	//单线程线程池
	public ExecutorService exec;
	//画笔对象
	public Paint paint;
	
	public BaseSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		holder = getHolder();
		holder.addCallback(this);
		this.initPaint();
	}
	
	/** 开始绘制 */
	public void startToDraw(){
		if(exec == null || exec.isShutdown()){
			//如果线程池是空的则新建一个线程池
			exec = Executors.newSingleThreadExecutor();
		}
		Thread thread = this.createDrawThread();
		exec.execute(thread);
	}
	
	/** 终止线程 */
	public void endDrawing(){
		if(exec != null && !exec.isShutdown()){
			exec.shutdownNow();
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public Thread createDrawThread() {
		return null;
	}

	@Override
	public void initPaint() {
		
	}

	@Override
	public void initDatas() {
		
	}
}
















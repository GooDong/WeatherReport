package com.sgd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.sgd.base.BaseSurfaceView;
import com.sgd.utils.MyUtils;

public class SunDownView extends BaseSurfaceView{
	//定义一个字体大小的倡廉
	final static int TEXT_SIZE = 30;
	//获得屏幕的宽度
	float screenWidth,screenHeight,radius;
	//定义边距
	int toTop,toLeft = 100;
	//定义当前的时间进度，代表从日出到日落途中的位置（百分比）
	float progress;
	//定义三个时间
	String sunStartTime,sunEndTime,timeNow;
	
	public SunDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public Thread createDrawThread() {
		return new drawThread();
	}

	@Override
	public void initPaint() {
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize(TEXT_SIZE);
	}
	
	public void initDatas(int[] times) {
		screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
		radius = (screenWidth - 2*toLeft)/2;
		toTop = toLeft;
		//转换为时间的格式
		sunStartTime = MyUtils.getInstance().getTimeStr(times[0]);
		sunEndTime = MyUtils.getInstance().getTimeStr(times[1]);
		if((times[2] - times[0]) >= 0 && (times[2] - times[1]) <=0 ){
			progress = (times[2] - (float)times[0])/(times[1] - (float)times[0]) * 100; 
		}else{
			progress = 0;
		}
	}
	
	public void drawData(Canvas canvas,int progressNow){
		
		//保存为单一层（如果不保存，则源图层将设定为上一白色图层）
		int canvasCount = canvas.saveLayer(0, 0, screenWidth, screenHeight, paint, Canvas.ALL_SAVE_FLAG);
		
		//绘制目标图
		RectF ovel = new RectF(toLeft,toTop,screenWidth - toLeft,screenWidth - toLeft);
		canvas.drawArc(ovel,180, 180, true, paint);
		
		//设置混合模式
		paint.setColor(Color.GRAY);
		
		//SRC_ATOP，在原图像和目标图像相交的地方绘制源（后绘制的）图像，不相交的地方绘制目标图像
		paint.setXfermode( new  PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		
		//设置源图
		float addDistance = (2*radius) * ((float)progressNow/100);
		canvas.drawRect(toLeft, toTop,toLeft+addDistance, screenWidth - toLeft, paint);
		
		//解除混合模式	
		paint.setXfermode(null);
		canvas.restoreToCount(canvasCount);
		
		//绘制白色的地平线
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(1);
		canvas.drawLine(0, radius+toLeft, screenWidth, radius+toTop, paint);
		canvas.drawText(sunStartTime, toLeft-20, toTop + radius +50, paint);
		canvas.drawText(sunEndTime, screenWidth - sunEndTime.length()*TEXT_SIZE - 80, toTop + radius +50, paint);
		paint.setStrokeWidth(3);
		
		//绘制模拟的太阳位置
		//得到太阳位置相对圆心旋转的角度
		double dexI = Math.acos((radius-addDistance)/(radius));
		float dexR = (float) (dexI/3.14159265758 * 180);
		canvas.rotate(dexR, screenWidth/2, radius+toTop);
		paint.setColor(Color.YELLOW);
		canvas.drawCircle(toLeft,radius+toLeft , 20 , paint);
		paint.setColor(Color.RED);
		canvas.drawCircle(toLeft,radius+toLeft , 15 , paint);
		
		paint.setColor(Color.WHITE);
	}

	private class drawThread extends Thread{
		@Override
		public void run() {
				try {
					//如果时间已经超出日落时间，则不再绘制太阳的运动轨迹
					if(progress <= 0 || progress >= 100){
						Canvas canvas = holder.lockCanvas();
						drawNight(canvas);
						holder.unlockCanvasAndPost(canvas);
						paint.setColor(Color.WHITE);
						return;
					}
					//当时间处于太阳升起后且在太阳落下前，则绘制太阳运动的动画
					int progressNow = 0;
					while( progressNow < progress){
						Canvas canvas = holder.lockCanvas();
						//铺设背景
						canvas.drawColor(Color.rgb(0x33, 0x55, 0x88));
						drawData(canvas,progressNow);
						holder.unlockCanvasAndPost(canvas);
						progressNow += 2;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	/**
	 * 绘制夜晚（日落之后）的图案
	 * */
	private void drawNight(Canvas canvas) {
		canvas.drawColor(Color.rgb(0x33, 0x55, 0x88));
		RectF ovel = new RectF(toLeft,toTop,screenWidth - toLeft,screenWidth - toLeft);
		paint.setColor(Color.GRAY);
		canvas.drawArc(ovel,180, 180, true, paint);
		for (int i = 1; i < 8; i++) {
			paint.setColor(Color.WHITE);
			canvas.rotate((float) 22.5,screenWidth/2, radius+toTop);
			canvas.drawLine(toLeft, radius+toLeft, screenWidth/2, radius+toLeft, paint);
		}
		//绘制图像
		paint.setColor(Color.YELLOW);
		canvas.drawCircle(screenWidth/2,radius+toLeft , 20 , paint);
		paint.setColor(Color.RED);
		canvas.drawCircle(screenWidth/2,radius+toLeft , 15 , paint);
	}
	
}

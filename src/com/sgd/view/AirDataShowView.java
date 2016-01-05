package com.sgd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Paint.Align;
import android.util.AttributeSet;

import com.sgd.base.BaseSurfaceView;

public class AirDataShowView extends BaseSurfaceView{
	//空气质量参数
	int data;
	
	//PM2.5的指数
	String PM2_5;
	
	//定义视图相对父类容器的位置数据
	float TO_LEFT ;
	float TO_RIGHT ;
	float TOP;
	float BOTTOM;
	
	//定义视图中指针的长度
	float lineLength;
	
	//视图的中心位置坐标
	float center_x;
	float center_y;
	
	public AirDataShowView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public Thread createDrawThread() {
		return new drawThread();
	}

	/** 初始化画笔参数  */
	@Override
	public void initPaint() {
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(20);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize(50);
		paint.setTextAlign(Align.CENTER);
	}

	@Override
	public void initDatas() {
		initDistance(data,PM2_5);
	}
	
	/** 初始化视图相对于父类容器的相对位置 */
	public void initDistance(int dataIn,String pm_2_5_in) {
	    int	leftDistance = mContext.getResources().getDisplayMetrics().widthPixels/2;
		TO_LEFT = leftDistance - 175;
		TO_RIGHT = leftDistance + 175;
		TOP = 60;
		BOTTOM = 410;
		lineLength = (BOTTOM - TOP + 100)/2;
		center_x = (TO_LEFT + TO_RIGHT)/2;
		center_y = (TOP + BOTTOM)/2;
		this.data = dataIn;
		this.PM2_5 = pm_2_5_in;
	}
	
	public void drawData(Canvas canvas,int dataNow){
		//绘制进度的底层
		RectF oval = new RectF(TO_LEFT,TOP,TO_RIGHT,BOTTOM);
		paint.setStrokeWidth(20);
		paint.setShader(new LinearGradient(TO_LEFT, BOTTOM, TO_RIGHT, BOTTOM, 
				Color.WHITE,Color.rgb(0x11, 0x33, 0xff), Shader.TileMode.CLAMP));
		canvas.drawArc(oval, 135, 270, false, paint);
		//绘制进度
		paint.setShader(null);
		paint.setColor(Color.RED);
		canvas.drawArc(oval, 135, dataNow, false, paint);
		paint.setColor(Color.WHITE);
		
		//绘制最外层的线条
		RectF oval2 = new RectF(TO_LEFT-50,TOP-50,TO_RIGHT+50 ,BOTTOM+50);
		paint.setStrokeWidth(4);
		canvas.drawArc(oval2, 135, 270, false, paint);
		
		//绘制文本
		canvas.drawText(""+dataNow,center_x,center_y + 120,paint);
		paint.setTextSize(30);
		paint.setStrokeWidth(2);
		canvas.drawText("PM2.5 : "+PM2_5,center_x,center_y + 160,paint);
		paint.setTextSize(50);

		//旋转画布，绘制指针
		paint.setStrokeWidth(4);
		canvas.rotate(dataNow+45,center_x,center_y);
		canvas.drawLine(center_x, center_y, center_x, center_y +lineLength, paint);
		
		//绘制实心圆心
		paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(center_x, center_y, 16, paint);
		paint.setColor(Color.rgb(0x33, 0x55, 0x88));
		canvas.drawCircle(center_x, center_y, 10, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
	}

	private class drawThread extends Thread{
		@Override
		public void run() {
			int dataTemp = 0;
				try {
					while (dataTemp < data) {
						dataTemp += 1;
						Canvas canvas = holder.lockCanvas();
						canvas.drawColor(Color.rgb(0x33, 0x55, 0x88));
						drawData(canvas, dataTemp);
						holder.unlockCanvasAndPost(canvas);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	
}

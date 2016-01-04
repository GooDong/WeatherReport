package com.sgd.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sgd.utils.MyUtils;
import com.sgd.weatherreportdemo.IConstans;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class WeatherDataLineView extends View implements IConstans{
	//每天最高温度的保存坐标点
	List<Float> points_x_h;
	List<Float> points_y_h;
	//每天最低温度的保存坐标点
	List<Float> points_x_l;
	List<Float> points_y_l;
	//温度数据
	float[][] index ;
	//父容器的高度
	int linHeight;
	//最值温度的值和坐标
	float max;
	float min;
	int indexOfMax;
	int indexOfMin;
	//画笔
	Paint paint;
	//温度曲线的路径
	Path[] path = new Path[2];
	HashMap<String,Object> mixNums = new HashMap<String,Object>();
	float screenWidthPeace,screenWidth;
	
	public static WeatherDataLineView createNewWeatherDataLineView(
			Context context,float[][] indexIn,int linHeight){
		return new WeatherDataLineView(context,indexIn,linHeight);
	}
	
	public WeatherDataLineView(Context context,float[][] indexIn,int linHeight) {
		super(context);
		this.index = MyUtils.getInstance().adapterData(linHeight, indexIn);
		this.linHeight = linHeight;
		mixNums = MyUtils.getInstance().getMixNums(linHeight, indexIn);
		//得到最值
		max = (float) mixNums.get("max");
		min = (float) mixNums.get("min");
		indexOfMax = (int) mixNums.get("indexOfMax");
		indexOfMin = (int) mixNums.get("indexOfMin");
		//得到屏幕宽度
		screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		//分割屏幕的宽度
		screenWidthPeace = screenWidth/10;
		initPaint();
		//初始化路径
		initPathH();
		initPathL();
	}
	/**
	 * 初始化绘制路径
	 * */
	private void initPathH(){
		//每天的最高温度
		points_x_h = new ArrayList<Float>();
		points_y_h = new ArrayList<Float>();
		for (int i = 0; i < NUM_WEATHER_DAYS; i++) {
			points_x_h.add(screenWidthPeace * (i*2 - 1));
			points_y_h.add(index[0][i]);
		}
	}
	private void initPathL(){
		//每天的最低温度
		points_x_l = new ArrayList<Float>();
		points_y_l = new ArrayList<Float>();
		for (int i = 0; i < NUM_WEATHER_DAYS; i++) {
			points_x_l.add(screenWidthPeace * (i*2 -1));
			points_y_l.add(index[1][i]);
		}
	}
	/**
	 * 初始化画笔参数
	 * */
	private void initPaint() {
		paint = new Paint();
		//线型画笔
		paint.setStyle(Paint.Style.STROKE);
		//白色
		paint.setColor(Color.WHITE);
		//宽度为3
		paint.setStrokeWidth(3);
		//抗锯齿
		paint.setAntiAlias(true);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//根据当前的温度点计算所有出路径中的点
		List<Cubic> calculate_x_h = calculate(points_x_h);
		List<Cubic> calculate_y_h = calculate(points_y_h);
		List<Cubic> calculate_x_l = calculate(points_x_l);
		List<Cubic> calculate_y_l = calculate(points_y_l);
		//初始化路径
		path[0] = new Path();
		path[1] = new Path();
		path[0].moveTo(calculate_x_h.get(0).eval(0), calculate_y_h.get(0).eval(0));
		path[1].moveTo(calculate_x_l.get(0).eval(0), calculate_y_l.get(0).eval(0));
		for (int i = 0; i < calculate_x_h.size(); i++) {
			for (int j = 1; j <= STEPS; j++) {
				float u = j / (float) STEPS;
				path[0].lineTo(calculate_x_h.get(i).eval(u), calculate_y_h.get(i)
						.eval(u));
				path[1].lineTo(calculate_x_l.get(i).eval(u), calculate_y_l.get(i)
						.eval(u));
			}
		}
		//绘制路径
		canvas.drawPath(path[0], paint);
		canvas.drawPath(path[1], paint);
		canvas.save();
		
		//当天的数据上绘制实心圆点
		paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(screenWidthPeace, index[0][1], 5, paint);
		canvas.drawCircle(screenWidthPeace, index[1][1], 5, paint);
			
		//在当天的气温上绘制一条虚线
		Path pathTemp = new Path();
		pathTemp.moveTo(screenWidthPeace , index[0][1]);
		pathTemp.lineTo(screenWidthPeace , linHeight);
		paint.setStyle(Paint.Style.STROKE);
		paint.setPathEffect(new DashPathEffect(new float[]{5,5,5,5}, 0));
		canvas.drawPath(pathTemp, paint);
		
		//重新设置画笔绘制最值温度
		paint.setTextSize(30);
		paint.setStrokeWidth(2);
		paint.setPathEffect(null);
		
		//在相应位置处绘制最高温度和最低温度,局部位置微调
		canvas.drawText(max+"℃",(indexOfMax*2 - 1)*screenWidthPeace , index[0][indexOfMax]-15, paint);
		canvas.save();
		canvas.drawText(min+"℃",(indexOfMin*2 - 1)*screenWidthPeace , index[1][indexOfMin]+35, paint);
		canvas.save();
		
	}
	/**
	 * 计算曲线.
	 * @param x
	 * @return
	 */
	private List<Cubic> calculate(List<Float> x) {
		int n = x.size() - 1;
		float[] gamma = new float[n + 1];
		float[] delta = new float[n + 1];
		float[] D = new float[n + 1];
		int i;
		/*
		 * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
		 * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
		 * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])]
		 * 
		 * 通过使用行操作来变换高阶三角矩阵，
		 * by using row operations to convert the matrix to upper triangular and
		 * 然后回代。
		 * then back sustitution（通过使用行变换矩阵转换为上三角sustitution然后回来）.
		 *  The D[i] are the derivatives/派生物 at the knots/节点.
		 */

		gamma[0] = 1.0f / 2.0f;
		for (i = 1; i < n; i++) {
			gamma[i] = 1 / (4 - gamma[i - 1]);
		}
		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
		for (i = 1; i < n; i++) {
			delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
					* gamma[i];
		}
		delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for (i = n - 1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		/* now compute the coefficients of the cubics 现在计算三次曲线的系数*/
		List<Cubic> cubics = new LinkedList<Cubic>();
		for (i = 0; i < n; i++) {
			Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
					- 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
					+ D[i + 1]);
			cubics.add(c);
		}
		return cubics;
	}
	class Cubic {
		 float a,b,c,d;         

		  public Cubic(float a, float b, float c, float d){
		    this.a = a;
		    this.b = b;
		    this.c = c;
		    this.d = d;
		  }
		  
		  /** evaluate cubic */
		  public float eval(float u) {
		    return (((d*u) + c)*u + b)*u + a;
		  }
	}
}














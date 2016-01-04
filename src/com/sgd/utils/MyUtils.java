package com.sgd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;




import com.sgd.weatherreportdemo.IConstans;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MyUtils implements IConstans{
	/** Double CheckLock 实现单例模式 */
	private static MyUtils instance = null;
	private MyUtils() {
	}
	public static MyUtils getInstance(){
		if(instance == null){
			synchronized (MyUtils.class) {
				if(instance == null){
					instance = new MyUtils();
				}
			}
		}
		return instance;
	}
	/**
	 * 返回当前时间的字符串
	 * */
	@SuppressLint("SimpleDateFormat")
	public String getTime(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 传入初始路径，HashMap集合，返回一个可用的url
	 * @param path String 路径的开头；
	 * @param params HashMap<String, String> 条件-值的集合
	 * */
	public String getURL(String path,HashMap<String, String> params){
		StringBuilder url = new StringBuilder();
		url.append(path + "?");
		//获取HashMap中的所有元素，利用set集合实现遍历
		Set<String> keySet = params.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()){
			String key = it.next();
			//将查询条件添加到Url中
			url.append(key +"="+params.get(key) +"&");
		}
		//去除最后一个&符号
		return url.toString().substring(0, url.length()-1);
	}
	/**
	 * 传入地址，下载图片
	 * */
	public Bitmap downLoadImg(String path){
		Bitmap img = null;
		try {
			URL url = new URL(path);
			InputStream is = url.openStream();
			img = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}
	/**
	 * 传入视图的高度，将数组中的数适按照比例放大
	 * */
	public float[][] adapterData(int layoutHeight,float[][] indexs){
		float max = -100;
		float min = 100;
		//得到所有温度中的最大值（一定出现在最高气温的数组中）和最小值（一定出现在最低气温的数组中），
		for (int j = 0; j < NUM_WEATHER_DAYS; j++) {
			if(indexs[0][j] > max){
				max = indexs[0][j];
			}
			if(indexs[1][j] < min){
				min = indexs[1][j];
			}
		}
		//得到最值的之差，以及对应差值的单位高度
		float perheight = layoutHeight/(max - min);
		//将所有数组按照差值比例放大
		float[][] temp = new  float[2][NUM_WEATHER_DAYS];
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < NUM_WEATHER_DAYS; j++) {
				//因为数据最终绘制到视图中时，方向为从上到下，因而需要进行翻转，微调图形的位置
				temp[i][j] = (float) (layoutHeight - ((indexs[i][j] - min) * perheight*0.6)-60);
			}
		}
		return temp;
	}
	/**
	 * 得到数组中的最值及其坐标
	 * */
	public HashMap<String,Object> getMixNums(int layoutHeight,float[][] indexs){
		float max = -100;
		float min = 100;
		int indexOfMax = 1;
		int indexOfMin = 1;
		//得到所有温度中的最大值（一定出现在最高气温的数组中）和最小值（一定出现在最低气温的数组中），
		//并且只从今天和未来四天中取值
		for (int j = 1; j < NUM_WEATHER_DAYS -1 ; j++) {
			if(indexs[0][j] > max){
				max = indexs[0][j];
				indexOfMax = j;
			}
			if(indexs[1][j] < min){
				min = indexs[1][j];
				indexOfMin = j;
			}
		}
		HashMap<String,Object> mixNums = new HashMap<String,Object>();
		mixNums.put("indexOfMax", indexOfMax);
		mixNums.put("indexOfMin", indexOfMin);
		mixNums.put("max", max);
		mixNums.put("min", min);
		return mixNums;
	}
	/**
	 * 打开一个文件，将文件中的内容作为字符串返回
	 * */
	public String getFileContent(File file){
		String result = "";
		 try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = fis.read(buf)) != -1){
				result += new String(buf,0,len);
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return result;
	}
	public String  getTimeStr(int time){
		String timeS = String.valueOf(time);
		if(timeS.length() < 6){
			timeS = "0"+timeS;
		}
		String timeTemp = timeS.substring(0, 2) + ":";
		timeS = timeTemp + timeS.substring(2, 4);
		return timeS;
	}

}
























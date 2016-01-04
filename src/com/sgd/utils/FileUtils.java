package com.sgd.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
/**
 * 文件工具类，提供本地内存/SD卡保存、提取、删除图片的方法
 * */
public class FileUtils {
	String path;//文件路径
	public FileUtils(Context contextIn) {
		//检测手机是否有SD卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/cache/";
		}else{
			path = contextIn.getCacheDir()+"/cache/";
		}
		//如果文件夹不存在，则新创建一个该路径下的文件夹
		if(!isFolderExists(path)){
			new File(path).mkdirs();
		}
	}
	/**
	 * 保存图片到文件夹中
	 * */
	public void saveBitmap(String name,Bitmap bitmap){
		try {
			File file = new File(path,name);
			FileOutputStream fos = new  FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 从文件夹中取出图片
	 * */
	public Bitmap getBitmap(String name){
		if(isFileExists(name)){
			return BitmapFactory.decodeFile(path+""+name);
		}
		return null;
	}
	/**
	 * 删除所有内存文件
	 * */
	public void deleAllBitmap(){
		File[] files = (new File(path)).listFiles();
		if(files != null){
			for (File file : files) {
				file.delete();
			}
		}
	}
	/**
	 * 判断是否是文件
	 * */
	public boolean isFileExists(String name){
		File file = new File(path,name);
		if(file.isFile() && file.exists()){
			return true;
		}
		return false;
	}
	/**
	 * 判断是否是文件夹
	 * */
	public boolean isFolderExists(String path){
		File file = new File(path);
		if(file.isDirectory()&& file.exists()){
			return true;
		}
		return false;
	}
}


























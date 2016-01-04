package com.sgd.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
/**
 * 提供三级下载机制：本地、缓存、网络
 * 首先尝试从缓存中读取文件，其次尝试从缓存中读取文件，最后尝试从网络下载文件
 * */
public class DownImageLruCacheUtils {
	
	LruCache<String,Bitmap> lruCache;
	FileUtils fileUtils;
	ExecutorService ThreadPool;//线程池	
	public DownImageLruCacheUtils(Context contextIn) {
		//初始化文件工具类
		fileUtils = new FileUtils(contextIn);
		//分配缓存的大小为运行时最大内存的八分之一，32M(?)
		lruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 8)){
			@Override
			protected int sizeOf(String name, Bitmap bitmap) {
				//返回图片的大小
				return bitmap.getByteCount();
			}
		};
	}
	/**
	 * 从网络中下载图片
	 * */
	public Bitmap downLoadImgFromIntent(final String url,final OnImgLoadDownLisenter onImgLoadDownLisenter){
		//将网址中所有的，非0~9，a~z,A~Z,的字符全部替换为空
		final String name =url.replaceAll("[^\\w]", "");
		//尝试从缓存中获取图片
		if(getBitmapFromMemory(name) != null){
			return getBitmapFromMemory(name);
		}
		//尝试从内存中获取图片
		if(getBitmapFromSDCard(name) != null){
			return getBitmapFromSDCard(name);
		}
		//如果缓存和内存中都没有图片资源则从网络上下载
		getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					//从网络中下载图片
					URL path = new URL(url);
					InputStream is  = path.openStream();
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					is.close();
					//保存图片到缓存、内存中
					addBitmapToMemory(name, bitmap);
					addBitmapToSDCard(name, bitmap);
					//调用下载完成的回调接口
					onImgLoadDownLisenter.onImgLoadDownLisenter(bitmap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return null;
	}
	
	/**
	 * 将图片添加到缓存中
	 * */
	public void addBitmapToMemory(String name,Bitmap bitmap){
		lruCache.put(name, bitmap);
	}
	/**
	 * 从缓存中取出图片
	 * */
	public Bitmap getBitmapFromMemory(String name){
		return lruCache.get(name);
	}
	/**
	 * 将图片添加到内存中
	 * */
	public void addBitmapToSDCard(String name,Bitmap bitmap){
		fileUtils.saveBitmap(name, bitmap);
	}
	/**
	 * 从内存中取出图片
	 * */
	public Bitmap getBitmapFromSDCard(String name){
		Bitmap bitmap = fileUtils.getBitmap(name);
		if(bitmap != null ){
			addBitmapToMemory(name, bitmap);
		}
		return bitmap;
	}
	/**
	 * 返回一个线程池
	 * */
	public ExecutorService getThreadPool(){
		//添加同步锁，Executors中所有的静态方法都将处于同步装填
		synchronized (Executors.class) {
			if (ThreadPool == null) {
				//开启3个线程，当某个线程结束之后，该线程将被复用
				ThreadPool = Executors.newFixedThreadPool(3);
			}
			return ThreadPool;
		}
		
	}
	/**
	 * 下载完成之后的回调接口
	 * */
	public interface OnImgLoadDownLisenter{
		public void onImgLoadDownLisenter(Bitmap bitmap);
	}
}





























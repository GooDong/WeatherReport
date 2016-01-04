package com.sgd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.json.JSONObject;

import com.sgd.weatherreportdemo.IConstans;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class ReadAndWriteJasonFileUtil implements IConstans{
	/** Double CheckLock 实现单例模式 */
	private static ReadAndWriteJasonFileUtil instance = null;
	private ReadAndWriteJasonFileUtil() {
	}
	public static ReadAndWriteJasonFileUtil getInstance(){
		if(instance == null){
			synchronized (MyUtils.class) {
				if(instance == null){
					instance = new ReadAndWriteJasonFileUtil();
				}
			}
		}
		return instance;
	}
	public boolean readJasonFromLocal (Context context,Handler handler,String cityNameForNow){
		try {
			String path;
			//检测手机是否有SD卡
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/weatherdata/";
			}else{
				path = context.getCacheDir()+"/weatherdata/";
			}
			//将城市天气信息以城市名命名
			path = path+cityNameForNow+".txt";
			if(!(new File(path).exists())){
				//如果当前没有任何旧的数据则返回false
				return false;
			}
			FileInputStream is = new FileInputStream(path);
			byte[] buf = new byte[1024];
			int len = 0;
			String result = "";
			while((len = is.read(buf)) != -1){
				result += new String(buf,0,len);
			}
			is.close();
			//新建一个JsonObject文件
			JSONObject json = new JSONObject(result);
			//发送消息，表示已经读取成功
			Message msg = handler.obtainMessage();
			msg.obj = json;
			msg.what = LOAD_FROM_LOCAL;
			handler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	public void writeIntoLocalFile(JSONObject jsonFile,Context context,String cityNameForNow) {
		String path;
		//检测手机是否有SD卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/weatherdata/";
		}else{
			path = context.getCacheDir()+"/weatherdata/";
		}
		File folder = new File(path);
		if(!folder.exists()){
			//如果文件夹不存在则新建一个文件夹
			folder.mkdirs();
		}
		try {
			File file = new File(path,cityNameForNow+".txt");
			if(file.exists()){
				//如果有旧的文件，则将旧的文件删除
				file.delete();
			}
			//创建一个新文件
			file.createNewFile();
			String content = jsonFile.toString();
			FileOutputStream fos = new FileOutputStream(file);
			//向文件中写入数据
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

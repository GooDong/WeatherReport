package com.sgd.weatherreportdemo;

import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.sgd.utils.MyUtils;
/**
 * 异步任务，从向API请求数据
 * */
public class OpenApiAsyncTask extends AsyncTask<HashMap<String,String>, Void, JSONObject> 
implements IConstans{
	Handler handler;
	boolean loadingFromLocl;
	/**
	 * 新建一个向API请求数据的异步任务
	 * */
	public OpenApiAsyncTask(Handler handler,boolean loadingFromLocl) {
		this.handler = handler;
		this.loadingFromLocl = loadingFromLocl;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	@Override
	protected void onPostExecute(JSONObject jsonBody) {
		super.onPostExecute(jsonBody);
		if(jsonBody == null){
			handler.sendEmptyMessage(ALREADY_GET_RESPONSE);
			return;
		}
		Message msg = handler.obtainMessage();
		msg.obj = jsonBody;
		msg.what = ALREADY_GET_RESPONSE;
		handler.sendMessage(msg);
	}
	@Override
	protected JSONObject doInBackground(@SuppressWarnings("unchecked") HashMap<String, String>... params) {
		BasicHttpParams paramsh = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(paramsh, 4*1000);
		HttpConnectionParams.setSoTimeout(paramsh, 4*1000);	
		HttpClient client = new DefaultHttpClient(paramsh);
			String url = MyUtils.getInstance().getURL(PATH, params[0]);
			HttpGet get = new HttpGet(url);
			try {
				HttpResponse response = client.execute(get);
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					//创建一个新的JSON对象
					JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
					//返回结果参数，易源返回标志，0为成功，其他为失败。
					int showapi_res_code = json.optInt("showapi_res_code");
					if(loadingFromLocl){
						//从文件中读取数据与从网络读取数据在使用资源时会冲突，因而如果从本地文件读取成功则将异步任务迟滞3秒
						//以保证资源的安全
						Thread.sleep(ASYNC_TASK_SLEEP_TIME);
					}
					if(showapi_res_code != 0){
						return null;
					}else{
						JSONObject showapi_res_body = json.optJSONObject("showapi_res_body");
						return showapi_res_body;
					}
				}
				return null;
			}catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

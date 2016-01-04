package com.sgd.fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sgd.utils.MyDataBaseUtils;
import com.sgd.utils.MyUtils;
import com.sgd.weatherreportdemo.IConstans;
import com.sgd.weatherreportdemo.MainActivity;
import com.sgd.weatherreportdemo.R;

public class SearchCityFragment extends Fragment implements IConstans{
	//正在执行搜索的标志
	static boolean  isSearching; 
	//碎片页面的视图
	View searchCityLayout;
	//布局文件
	LinearLayout searchLinLayout;
	//用于搜索的信息
	String searchContent = "";
	//搜索成功后保存的城市的名字
	String cityname = "";
	//执行搜索任务的线程池
	ExecutorService exec ;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//单线程线程池，如果并非首次启动，则手动停止之前的搜索
		if(exec != null && !exec.isShutdown()){
			exec.shutdown();
		}
		//创建新的线程池
		exec = Executors.newSingleThreadExecutor();
		//设置正在搜索标志为false
		isSearching = false;
		//LinLayout布局
		searchCityLayout = inflater.inflate(R.layout.search_city_fragment, null);
		//找到布局文件
		searchLinLayout = (LinearLayout) searchCityLayout
				.findViewById(R.id.lin_layout_search_city);
		//开始搜索城市信息
		searchCityInfo();
		x.view().inject(getActivity());
		return searchCityLayout;
	}
	private void searchCityInfo() {
		if(searchContent.equals("")){
			showContent(EMPTY_CONTENT);
			return;
		}
		if(!isSearching){
			showContent(SEARCHING);
			//设置正在执行搜索的标志为true
			isSearching = true;
			//开始执行搜索
			startSearching();
		}
	}
	private void startSearching() {
		//开始执行搜索
		exec.execute(new searchThread());
	}
	/** 显示当前的任务状态或者提示信息  */
	private void showContent(String content) {
		searchLinLayout.removeAllViews();
		TextView searchingTxt = new TextView(getActivity());
		if(!cityname.equals("")){
			searchingTxt.setTextSize(30);
			searchingTxt.setTextColor(Color.BLUE);
			searchingTxt.setClickable(true);
			searchingTxt.setOnClickListener(findCityInfoListener);
		}else{
			//否则信息居中显示,且文本不能被点击
			searchingTxt.setTextColor(Color.BLACK);
			searchingTxt.setGravity(Gravity.CENTER_HORIZONTAL);
			searchingTxt.setClickable(false);
		}
		searchingTxt.setText(content);
		searchLinLayout.addView(searchingTxt);
	}
	private OnClickListener findCityInfoListener = new OnClickListener() {
		@Override
		public void onClick(View cityInfoTxt) {
			//获得城市信息，点击之后：
			//一、将城市信息写入数据库
			//二、跳转到天气主页面
			addCityInfoToDatabase();
			tripToWeatherActivity();
		}

		private void addCityInfoToDatabase() {
			MyDataBaseUtils.getInstance().addCityInfo(getActivity(), cityname);
		}

		private void tripToWeatherActivity() {
			//跳转到主页面
			Intent intent = new Intent(getActivity(),MainActivity.class);
			intent.putExtra("cityName",cityname);
			getActivity().setResult(Activity.RESULT_OK);
			//启动Intent
			startActivity(intent);
			//关闭当前的Activity
			getActivity().finish();
		}
	};
	/** 如果查询成功，则将查询结果显示出来 */
	private void showCityInfoList(String cityInfo) {
		try {
			//解析查询结果中的信息
			JSONObject cityInfoBody = new JSONObject(cityInfo);
			JSONObject showapi_res_body = cityInfoBody.optJSONObject("showapi_res_body");
			JSONArray data = (JSONArray) showapi_res_body.opt("list");
			JSONObject detail = (JSONObject) data.opt(0);
			//得到省份信息和城市信息
			String pv = detail.optString( "prov") +"---"+detail.optString( "distric");
			//保存城市信息
			cityname = detail.optString( "distric");
			//将城市信息显示到页面上
			showContent(pv);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/** 设置搜索城市的名字 */
	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
	}
	class searchThread extends Thread{
		@Override
		public void run() {
			//使用单线程池执行搜索任务
			//传入搜索字段开始执行
			RequestParams params = new RequestParams("http://route.showapi.com/9-3");
			params.addQueryStringParameter("showapi_appid", "13088");
			params.addQueryStringParameter("showapi_sign", "329224e46550451a8863dce6f2ce1adf");
			params.addQueryStringParameter("showapi_timestamp",MyUtils.getInstance().getTime());
			params.addQueryStringParameter("area",searchContent);
			x.http().get(params, new CommonCallback<String>() {
				@Override
				public void onCancelled(CancelledException arg0) {
					isSearching = false; 
				}

				@Override
				public void onError(Throwable arg0, boolean arg1) {
					isSearching = false; 
					//搜索出错则提示信息
					showContent(NOT_FIND);
				}

				@Override
				public void onFinished() {
					isSearching = false;
				}

				@Override
				public void onSuccess(String cityInfo) {
					isSearching = false; 
					//搜索完毕将城市信息提示出来
					showCityInfoList(cityInfo);
				}
			});
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		isSearching = false;
	}
	@Override
	public void onPause() {
		super.onPause();
		cityname = "";
	}
/*	class AddInfoToDBThread extends Thread{
		@Override
		public void run() {
			
			super.run();
		}
	}*/
}



























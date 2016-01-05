package com.sgd.weatherreportdemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sgd.WeatherData.OtherDatas;
import com.sgd.WeatherData.WeatherDataOfDays;
import com.sgd.WeatherData.WeatherDataOfNow;
import com.sgd.adapter.OtherDatasAdapter;
import com.sgd.adapter.WeatherDayViewAdapter;
import com.sgd.fragment.AirQulityFragment;
import com.sgd.fragment.SunDownFragment;
import com.sgd.fragment.TemperatureChangeLineFragment;
import com.sgd.utils.DownImageLruCacheUtils;
import com.sgd.utils.MyUtils;
import com.sgd.utils.ReadAndWriteJasonFileUtil;
import com.sgd.utils.WeatherDataUtils;
import com.sgd.utils.DownImageLruCacheUtils.OnImgLoadDownLisenter;
import com.sgd.view.PullToRefreshView;
import com.sgd.view.PullToRefreshView.OnFooterRefreshListener;
import com.sgd.view.PullToRefreshView.OnHeaderRefreshListener;
import com.sgd.weatherreportdemo.R;

/**
 * 主頁面主要用來初始化/更新數據
 * */
public class MainActivity extends Activity 
implements IConstans ,OnClickListener,
OnHeaderRefreshListener, OnFooterRefreshListener{
	//定义当前的城市（用于测试）
	String cityNameForNow;
	//一个温度数据的文件
	JSONObject jsonFile;
	//保存实时天气图片
	Bitmap weatherImgNow;
	//标识了是否能够从本地文件读取数据
	boolean loadingFromLocal;
	// 用于保存和取出系统数据
	SharedPreferences preferences;
	// 日出时间、日落时间、当前时间，用于绘制日出日落演示图
	int[] times = {0600,1800,1200};
	// 柵欄任務，執行固定數量的線程
	private static CyclicBarrier barrier;
	
	// 線程執行器，配合一个栅栏任务，执行固定数量的线程任务，
	// 直到所有的线程都执行完毕的时候才进入下一步
	private static ExecutorService exec;
	
	// 各天的溫度數據
	float[][] daysTemperature = new float[2][NUM_WEATHER_DAYS];

	// 控件
	TextView cityName, temperature, weatherAndAir, weatherSendTime;
	LinearLayout linWeatherChangeLine , linAirQulity;
	ImageView weatherImg;
	GridView weatherDaysView,otherDatasView;
	ImageView checkAllInfo;
	
	// 存儲了查詢的條件
	HashMap<String, String> params;

	// 實時天氣數據
	WeatherDataOfNow weatherNow = new WeatherDataOfNow();

	// 各天的天氣數據
	ArrayList<WeatherDataOfDays> weatherDataList;
	
	//其他天气数据
	OtherDatas otherTemDatas;
	HashMap<String,String> datas;
	
	// 工具類
	DownImageLruCacheUtils downImgUtil;
	WeatherDayViewAdapter adapter;
	OpenApiAsyncTask weatherCheck;
	
	//碎片
	AirQulityFragment airQulityFragment;
	SunDownFragment sunDownFragment;
	TemperatureChangeLineFragment temperatureLineFragment;
	//下拉刷新组件
	PullToRefreshView mPullToRefreshView;
	boolean isFreshing = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//隐藏导航栏
		getActionBar().hide();
		initView();
		startLoadInfo();
	}
	/**从布局中找到所有控件 */
	private void initView() {
		cityName = (TextView) findViewById(R.id.txt_city_name);
		temperature = (TextView) findViewById(R.id.txt_temperature);
		weatherAndAir = (TextView) findViewById(R.id.txt_weather_air);
		weatherSendTime = (TextView) findViewById(R.id.txt_weather_sendtime);
		weatherImg = (ImageView) findViewById(R.id.img_weather);
		checkAllInfo = (ImageView) findViewById(R.id.img_check_all_info);
		checkAllInfo.setOnClickListener(this);
		linWeatherChangeLine = (LinearLayout) findViewById(R.id.lin_weather_change_line);
		weatherDaysView = (GridView) findViewById(R.id.gridview_weather_days);
		otherDatasView = (GridView) findViewById(R.id.gridview_weather_otherdatas);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refreshview);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setLastUpdateTime(getSharedPreferences("FreshDate", MODE_PRIVATE)
				.getString("lastFreshDate","2015-12-24 00:00:00"));
		downImgUtil = new DownImageLruCacheUtils(MainActivity.this);
		exec = Executors.newCachedThreadPool();
		barrier = new CyclicBarrier(BARRIER_TASK_NUMS, new Runnable() {
			@Override
			public void run() {
				//使用栅栏任务执行线程的时候
				//当固定数量的线程全部运行完毕之后将会加载展览任务的线程
				handler.sendEmptyMessage(REFREASH_WEATHER_DATA);
			}
		});
		initCurrentCity();
		//初始化一些视图的，将它们置零并添加到主页面上
		initshowViews();
	}
	private void initCurrentCity() {
		SharedPreferences sp = getSharedPreferences("CurrentCity", MODE_PRIVATE);
		cityNameForNow = sp.getString("cityNameForNow", "深圳");
	}
	@SuppressWarnings("unchecked")
	private void startLoadInfo() {
		// 尝试从本地中寻找温度数据，如果失败则返回false，在异步任务执行的时候将不再休眠，
		// 否则异步任务休眠一定时间防止资源冲突
		if(!isFreshing){
			loadingFromLocal = ReadAndWriteJasonFileUtil.getInstance().readJasonFromLocal(this, handler, cityNameForNow);
		}
		// 执行异步任务
		weatherCheck = new OpenApiAsyncTask(handler,loadingFromLocal);
		initParams();
		weatherCheck.execute(params);
	}

	/** 初始化访问API的参数 */
	private void initParams() {
		params = new HashMap<String, String>();
		params.put("showapi_appid", "13088");// Api接口识别id
		params.put("showapi_sign", "329224e46550451a8863dce6f2ce1adf");// API接口识别标志
		params.put("showapi_timestamp", MyUtils.getInstance().getTime());// 查询时间
		params.put("needMoreDay", "1");// 查询时间
		params.put("area", cityNameForNow);// 地区
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {
			case LOAD_FROM_LOCAL:
				jsonFile = (JSONObject) msg.obj;
				getInfoFromJson(jsonFile);
				break;
			case ALREADY_GET_RESPONSE:
				//从网络刷新数据后，将刷新状态设为false
				isFreshing = false;
				// 開啟異步任務，當網絡響應時發送消息
				jsonFile = (JSONObject) msg.obj;
				if(jsonFile == null){
					Toast.makeText(MainActivity.this,"网络异常", Toast.LENGTH_SHORT).show();
					return;
				}
				//开启多线程任务，从资源文件中取出所有的温度数据
				getInfoFromJson(jsonFile);
				//将网络下载的数据更新进本地文件当中
				ReadAndWriteJasonFileUtil.getInstance().writeIntoLocalFile(jsonFile,
						MainActivity.this, cityNameForNow);
				break;
			case REFREASH_WEATHER_DATA:
				//设置当前天气数据
				setWeatherNowData();
				//设置近期温度数据
				setWeatherDayData();
				//设置其他数据
				setOtherDatas();
				//绘制温度变化图
				setWeatherDataLine();
				//绘制空气质量图
				setAirQulityView();
				//绘制日日落图
				setSunDownDow();
				break;
			case REFREASH_DAYS_WEATHRE_ICON:
				// 適配器在加載數據的時候將有可能從網絡下載圖片，當執行網絡下載完成時發送下載完成的消息
				adapter.notifyDataSetChanged();
				break;
			case REFREASH_NOW_WEATHRE_ICON:
				// 在加載天氣圖片的時候可能可能從網絡下載圖片，當執行網絡下載完成時發送下載完成的消息
				Bitmap temp = (Bitmap) msg.obj;
				weatherImg.setImageBitmap(temp);
				break;
			case LOAD_ANOTHER_CITY:
				//加载另外一个城市的信息
				startLoadInfo();
				break;
			case REFRESH_BY_NOW:
				//下拉刷新数据
				isFreshing = true;
				startLoadInfo();
				break;
			}
		}

	};
	/** 设置其他的天气参数 */
	private void setOtherDatas() {
		//填充其他天气数据的ListView
		OtherDatasAdapter datasAdapter = new OtherDatasAdapter(datas, MainActivity.this);
		otherDatasView.setAdapter(datasAdapter);
	}
	/**  初始化空气质量展示图、日出日落图 */
	private void initshowViews() {
		//初始化空气质量展示图
		airQulityFragment = new AirQulityFragment();
		airQulityFragment.setData(1);
		airQulityFragment.setPm2_5("0");
		airQulityFragment.setSendTime("00:00");
		//初始化日出日落示意图
		sunDownFragment = new SunDownFragment();
		sunDownFragment.setTimes(times);
		//初始化温度曲线变化图
		int linHeight = linWeatherChangeLine.getHeight();
		float[][] index = {{50,50,50,50,50,50,50},{50,50,50,50,50,50,50}};
		temperatureLineFragment = new TemperatureChangeLineFragment();
		temperatureLineFragment.setLinHeight(linHeight);
		temperatureLineFragment.setIndex(index);
		//替换碎片
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.lin_air_qulity, airQulityFragment);
		ft.replace(R.id.lin_sun_start_end, sunDownFragment);
		ft.replace(R.id.lin_weather_change_line, temperatureLineFragment);
		ft.commit();
	}
	/** 绘制/刷新空气质量图 */
	private void setAirQulityView() {
		//刷新数据
		airQulityFragment.setData(Integer.parseInt(otherTemDatas.aqi));
		airQulityFragment.setPm2_5(otherTemDatas.airAqiDetail);
		airQulityFragment.setSendTime(weatherNow.temperature_time);
		//重绘展示图
		airQulityFragment.refreshView();
	}
	/** 绘制/刷新日出日落时间图  */
	private void setSunDownDow() {
		sunDownFragment.setTimes(times);
		sunDownFragment.refreshView();
	}
	/** 绘制/刷新温度曲线图*/
	private void setWeatherDataLine() {
		int linHeight = linWeatherChangeLine.getHeight();
		temperatureLineFragment.setLinHeight(linHeight);
		temperatureLineFragment.setIndex(daysTemperature);
		temperatureLineFragment.refreshView();
	}
	/**  設置各天的溫度數據 */
	private void setWeatherDayData() {
		adapter = new WeatherDayViewAdapter(weatherDataList, MainActivity.this,
				handler, downImgUtil);
		weatherDaysView.setAdapter(adapter);
	}

	/** 设置当前温度数据 */ 
	private void setWeatherNowData() {
		cityName.setText(cityNameForNow);
		temperature.setText(weatherNow.temperature + "℃");
		weatherAndAir.setText(weatherNow.weather + " | 空气" + weatherNow.airAqi);
		weatherSendTime.setText("中国天气网于" + weatherNow.temperature_time + "发布");
		
		//使用图片下载工具
		weatherImgNow = downImgUtil.downLoadImgFromIntent(
				weatherNow.weatherIcon, new OnImgLoadDownLisenter() {
					@Override
					public void onImgLoadDownLisenter(Bitmap bitmap) {
						//尝试从网络下载图片
						Message msg = handler.obtainMessage();
						msg.obj = bitmap;
						msg.what = REFREASH_NOW_WEATHRE_ICON;
						handler.sendMessage(msg);
					}
				});
		if (weatherImgNow != null) {
			weatherImg.setImageBitmap(weatherImgNow);
		}
	}
	/** 读取昨天的温度 */
	private void getLastDayData(final JSONObject dataBody) {
		//先从系统数据中读取昨天的温度数据
		preferences = getSharedPreferences("yesterdayTem", MODE_PRIVATE);
		daysTemperature[0][0] = preferences.getFloat("high", 0);
		daysTemperature[1][0] = preferences.getFloat("low", 0);
		String saveDate = preferences.getString("saveDate","111111");
		//如果在SP中存储的不是今天的数据，则将数据更新(包括数据的日期)
		String nowDate = WeatherDataUtils.getInstance().getCurrentDate(dataBody);
		if(!nowDate.equals(saveDate)){
			Editor edit = preferences.edit();
			edit.putFloat("high", daysTemperature[0][1]);
			edit.putFloat("low", daysTemperature[1][1]);
			edit.putString("saveDate",nowDate);
			edit.commit();
		}
	}
	/**通过栅栏任务分多个线程执行资源加载任务 */
	private void getInfoFromJson(final JSONObject dataBody){
		exec.execute(new Runnable() {
			boolean isDay = WeatherDataUtils.getInstance().isDay(dataBody);
			@Override
			public void run() {
				weatherDataList = WeatherDataUtils.getInstance().getWeatherDataList(dataBody,
						isDay);
				try {
					//当前线程执行完毕之后会等待，直到其他线程也执行完毕
					barrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		exec.execute(new Runnable() {
			@Override
			public void run() {

				daysTemperature = WeatherDataUtils.getInstance()
						.getWeatherTempeture(dataBody);
				try {
					barrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
				getLastDayData(dataBody);
			}
		});
		exec.execute(new Runnable() {
			@Override
			public void run() {
				weatherNow = WeatherDataUtils.getInstance().getWeatherDataOfNow(dataBody);
				try {
					barrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		exec.execute(new Runnable() {
			@Override
			public void run() {
				otherTemDatas = WeatherDataUtils.getInstance().getOtherDatas(dataBody);
				datas = otherTemDatas.getDataMap();
				//获取日出日落时间和当前的时间
				times = WeatherDataUtils.getInstance().getTime(dataBody);
				try {
					barrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.img_check_all_info:
				tripToLocalCity();
			break;
		}
	}

	private void tripToLocalCity() {
		Intent intent = new Intent(MainActivity.this,LocalCityActivity.class);
		startActivityForResult(intent, MAIN_TO_LOACL);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MAIN_TO_LOACL){
				//从城市信息页面返回时，刷新数据
				handler.sendEmptyMessage(REFREASH_WEATHER_DATA);
		}
	}
	private void ResumeFromIntent(Intent intent) {
		if(intent != null){
			String cityName = intent.getStringExtra("cityName");
			if(cityName != null && !cityNameForNow.equals(cityName)){
				//如果传送回来的城市信息并非当前城市信息则重新查询数据
				cityNameForNow = cityName;
				handler.sendEmptyMessage(LOAD_ANOTHER_CITY);
			}
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// 此时MainActivity的启动模式为SingleTask，在onResum中无法拿到Intent中的数据
		// 这能在onNewIntent中拿取，并刷新数据
		ResumeFromIntent(intent);
		super.onNewIntent(intent);
		
	}
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		handler.sendEmptyMessage(REFRESH_BY_NOW);
		mPullToRefreshView.postDelayed(new Runnable() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = format.format(System.currentTimeMillis());
				mPullToRefreshView.onHeaderRefreshComplete(date);
				SharedPreferences sp = getSharedPreferences("FreshDate", MODE_PRIVATE);
				sp.edit().putString("lastFreshDate", date).commit();
				mPullToRefreshView.setLastUpdateTime(date);
			}
		}, 3000);
	}
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.onFooterRefreshComplete();
	}
	
}































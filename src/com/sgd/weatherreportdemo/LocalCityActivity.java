package com.sgd.weatherreportdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.sgd.adapter.CityInfoAdapter;
import com.sgd.utils.MyDataBaseUtils;
import com.sgd.weatherreportdemo.R;

public class LocalCityActivity extends Activity 
implements android.view.View.OnClickListener, OnItemClickListener, OnItemLongClickListener{
	SQLiteDatabase dataBase;
	List<String> cities;
	ListView cityList;
	ImageView addCity;
	ActionBar actionBar;
	CityInfoAdapter cityInfoAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_city);
		init();
		select();
		cityInfoAdapter = new CityInfoAdapter(LocalCityActivity.this, cities);
		cityList.setAdapter(cityInfoAdapter);
		cityList.setOnItemClickListener(this);
		cityList.setOnItemLongClickListener(this);
	}
	/**
	 * 完成控件的初始化
	 * */
	private void init() {
		cityList = (ListView) findViewById(R.id.listView_all_city);
		addCity = (ImageView) findViewById(R.id.img_add_city);
		addCity.setOnClickListener(this);
		actionBar = getActionBar();
		//设置是否将应用程序图标转换为可点击的图标，并在图标上添加一个向左的箭头
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

	}
	private void select() {
		//从数据库中取出数据
		Cursor cursor = MyDataBaseUtils.getInstance().getAllCityInfo(LocalCityActivity.this);
		cities = new ArrayList<String>();
		//将查询到的数据添加到列表中
		while(cursor.moveToNext()){
			String city = cursor.getString(cursor.getColumnIndexOrThrow("cityname"));
			cities.add(city);
		}
		Log.i("---",cities.toString());
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
			break;
			}
		return true;
	}
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.img_add_city:
			intent = new Intent(LocalCityActivity.this,AddCityActivity.class);
			startActivity(intent);
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		tripToMainActivity(position);
	}
	private void tripToMainActivity(int position) {
		//跳转到主页面
		String cityName = cities.get(position);
		Intent intent = new Intent(LocalCityActivity.this,MainActivity.class);
		intent.putExtra("cityName", cityName);
		startActivity(intent);
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("位置管理");
		builder.setMessage("当前选中的城市为："+cities.get(position));
		builder.setPositiveButton("设为当前城市", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences sp = getSharedPreferences("CurrentCity", MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("cityNameForNow",cities.get(position));
				editor.commit();
				cityInfoAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("从列表中删除",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int deleNums = -1;
				deleNums = MyDataBaseUtils.getInstance().deleCityByName(LocalCityActivity.this,
						cities.get(position));
				if(deleNums == 1 ){
					Toast.makeText(LocalCityActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(LocalCityActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
				}
				cities.remove(position);
				cityInfoAdapter.notifyDataSetChanged();
			}
		});
		builder.create().show();
		return true;
	}

}























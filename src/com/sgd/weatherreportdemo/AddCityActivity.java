package com.sgd.weatherreportdemo;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

import com.sgd.fragment.AddCityFrament;
import com.sgd.fragment.SearchCityFragment;
import com.sgd.weatherreportdemo.R;

public class AddCityActivity extends Activity implements IConstans{
	AddCityFrament addCityFragment;
	SearchCityFragment searchCityFragment;
	SearchView searchCityView;
	String  searchContent = null;
	int currentFragmentIndex = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_city);
		initView();
	}
	private void initView() {
		addCityFragment = new AddCityFrament();
		searchCityFragment = new SearchCityFragment();
		setCurrentFrament(ADD_CITY_FRAGMENT);
		//设置导航栏的状态，不显示图标，则增加向左的返回箭头
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	private void setCurrentFrament(int index) {
		if(index == currentFragmentIndex){
			//如果当前的界面与目标界面是一样的，则返回
			return;
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if(index == ADD_CITY_FRAGMENT){
			//切换到添加界面
			ft.replace(R.id.framelayout_addcity_fragment, addCityFragment);
		}else{
			//切换到搜索界面
			ft.replace(R.id.framelayout_addcity_fragment, searchCityFragment);
		}
		ft.commit();
		//标记当前的Fragment
		currentFragmentIndex = index;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_city, menu);
		View view = menu.findItem(R.id.search_city_byname).getActionView();
		setSearchView(view);
		return super.onCreateOptionsMenu(menu);
	}
	private void setSearchView(View view) {
		searchCityView = (SearchView) view.findViewById(R.id.search_city_byname);
		//设置搜索框不使用自动缩小图标
		searchCityView.setIconifiedByDefault(false);
		//设置显示搜索按钮
		searchCityView.setSubmitButtonEnabled(true);
		//设置搜索框内显示的提示文本
		searchCityView.setQueryHint("搜索国内城市");
		//设置监听事件
		searchCityView.setOnQueryTextListener(searchInfoListener);
	}
	private OnQueryTextListener searchInfoListener = new OnQueryTextListener() {
		//当用户点击按钮时激活此方法
		@Override
		public boolean onQueryTextSubmit(String query) {
			if(query.equals("")){
				Toast.makeText(AddCityActivity.this, "搜索内容为空", Toast.LENGTH_SHORT).show();
			}
			changeFragment(query);
			return false;
		}
		//当用户输入时激活此方法
		@Override
		public boolean onQueryTextChange(String newText) {
			changeFragment(newText);
			return false;
		}

	};
	public void changeFragment(String content) {
		if(content.equals("")){
			setCurrentFrament(ADD_CITY_FRAGMENT);
		}else{
			//提交之后，若内容不为空则切换到搜索页面
			searchCityFragment.setSearchContent(content);
			setCurrentFrament(SEARCH_CITY_FRAGMENT);
			//区别是从快速定位输入还是从搜索框中输入
			//如果是前者，则将输入内容显示到搜索框中，并提交
			if(searchCityView.getQuery().toString().equals("") ){
				searchCityView.setQuery(content, true);
			}
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if(item.isCheckable()){
			item.setChecked(true);
		}
		switch (item.getItemId()) {
			case android.R.id.home:
				//调用返回上一层
				onBackPressed();
				break;
		}
		return true;
	}
}

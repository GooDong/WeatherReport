package com.sgd.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.sgd.adapter.CityLocalAdapter;
import com.sgd.weatherreportdemo.AddCityActivity;
import com.sgd.weatherreportdemo.IConstans;
import com.sgd.weatherreportdemo.R;

@SuppressLint("InflateParams")
public class AddCityFrament extends Fragment implements IConstans{
	AddCityActivity addCityActivity;
	GridView citiesGrid;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		addCityActivity = (AddCityActivity) getActivity();
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.addcity_fragment, null);
		citiesGrid = (GridView) view.findViewById(R.id.gridview_cities);
		SharedPreferences sp = getActivity()
				.getSharedPreferences("CurrentCity", Context.MODE_PRIVATE);
		String currentCity = sp.getString("cityNameForNow","深圳"); 
		CityLocalAdapter adapter = new CityLocalAdapter(getActivity(),currentCity);
		citiesGrid.setAdapter(adapter);
		citiesGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String chosedCity = mainCities[position];
				addCityActivity.changeFragment(chosedCity);
			}
		});
		return view;
	}
}















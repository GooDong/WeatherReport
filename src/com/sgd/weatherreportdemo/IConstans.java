package com.sgd.weatherreportdemo;

public interface IConstans {
	//提供多少天的天气数据：NUM_WEATHER_DAYS
	final static int NUM_WEATHER_DAYS = 7;
	//定义加载哪些时间的天气数据
	final static String[] DAYS = {"","f1","f2","f3","f4","f5","f6"};
	
	//加载数据时候开辟的加载线程的数目
	final static int BARRIER_TASK_NUMS = 4;
	//定义发送消息的类型
	final static int ALREADY_GET_RESPONSE = 0;//连接API获取数据已经得到响应，并已经得到了HttpResponse
	final static int REFREASH_WEATHER_DATA = 1;//刷新天气数据
	final static int REFREASH_NOW_WEATHRE_ICON = 2;//刷新天气图标
	final static int REFREASH_DAYS_WEATHRE_ICON = 3;//刷新实时天气图标
	final static int LOAD_FROM_LOCAL = 4;//从本地加载文件成功
	final static int LOAD_ANOTHER_CITY = 5;//从本地加载文件成功
	final static int REFRESH_BY_NOW = 6;//下拉刷新消息

	//定义Api的网址
	final static String PATH = "http://route.showapi.com/9-2";
	//定义常用的其他杂项数据名称
	final static String[] OTHER_DATAS = {"湿度","风向/风力","维度","经度","海拔","雷达号","二氧化硫","10μm颗粒","臭氧平均"};
	//主要城市
	final static String[] mainCities = {
		"定位","北京","上海","广州","深圳","珠海",
		"佛山","南京","苏州","杭州","济南","青岛",
		"郑州","石家庄","福州","厦门","武汉","长沙",
		"成都","重庆","太原","沈阳","南宁","西安"};
	//绘制温度变化曲线的阶数
	final static int STEPS = 12;
	
	//定义异步加载任务的休眠时间
	final static int ASYNC_TASK_SLEEP_TIME = 2000;
	
	//定义数据库的常量
	final static String DATABASE_NAME = "cityDataBase";//数据库名
	final static String DATABASE_TABLE = "city";//表名
	final static int DATABASE_VISION = 1;//表名
	
	//定义ContentProvider的Uri
	final static String CITY_PROVIDER_URI = "content://com.sgd.baseDao.cityinfoprovider/elements";
	final static int ALLROWS = 1;
	final static int SINGLE_ROW = 2;
	
	//定义添加城市时，添加和搜索Fragment之间切换的参数
	final static int ADD_CITY_FRAGMENT = 1;
	final static int SEARCH_CITY_FRAGMENT = 2;
	
	//返回城市搜索信息的相关提示
	final static  String SEARCHING = "搜索中……………";
	final static  String EMPTY_CONTENT = "搜索内容不能为空";
	final static  String NOT_FIND = "未找到匹配城市信息";
	
	//页面跳转requestCode
	final static int MAIN_TO_LOACL = 0x002;
}




























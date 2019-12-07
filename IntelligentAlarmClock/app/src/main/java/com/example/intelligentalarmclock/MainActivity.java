package com.example.intelligentalarmclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.intelligentalarmclock.db.County;
import com.example.intelligentalarmclock.db.SelectedInfo;
import com.example.intelligentalarmclock.gson.CaiyunDailyWeatherContent;
import com.example.intelligentalarmclock.gson.CaiyunWeatherContent;
import com.example.intelligentalarmclock.gson.DailySkycon;
import com.example.intelligentalarmclock.gson.DailyTemperature;
import com.example.intelligentalarmclock.gson.Skycon;
import com.example.intelligentalarmclock.gson.Temperature;
import com.example.intelligentalarmclock.util.HttpUtil;
import com.example.intelligentalarmclock.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alarmclass.AlarmActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import serviceclass.AlarmJobIntentService;
import serviceclass.ReceiveNotifyService;

public class MainActivity extends AppCompatActivity {

    private ScreenBroadcastListener listener;

    HorizontalAdapter horizontalAdapter;
    private List<HourlyInfo> hourInfoList=new ArrayList<HourlyInfo>();

    private RecyclerView horizontalRecyclerView; //水平滑动控件
    private ImageView bingPicImg;        //背景图片
    public DrawerLayout drawerLayout;     //左滑布局
    private TextView countyTitle;         //选中的城市名称
    private Button switchButton;          //切换城市按键
    private Button alarmButton;           //闹钟按键
    private LinearLayout forecastLayout;  //预报区域
    private ScrollView scrollView;        //滑动区域
    private TextView todayInfo;           //当天天气情况
    private TextView todayTemp;           //当天气温
    private TextView speedText;           //风速
    private TextView description;         //天气总描述
    private TextView sunrise;             //日出
    private TextView sunset;              //日落
    private TextView visibility;          //能见度
    private TextView pm25;                //pm25
    private TextView aqi;                 //aqi
    public SwipeRefreshLayout swipeRefreshLayout; //下拉刷新布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        LogInfo.d("MainActivity onCreate start"+Thread.currentThread().getId());
        preInit();
        //checkRequestPermission();

        //若当前在响铃，则关闭响铃
        int ringingAlarmID=0;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            ringingAlarmID= AlarmJobIntentService.getRingingAlarmID();
            if (0!=ringingAlarmID){
                LogInfo.d("the bell is running");
                stopRing(ringingAlarmID);
                AlarmJobIntentService.resetRingingAlarmID();
            }else {
                LogInfo.d("the bell is not running ");
            }
        }else{
            ringingAlarmID= ReceiveNotifyService.getRingingAlarmID();
            if (0!=ringingAlarmID){
                LogInfo.d("the bell is running");
                stopRing(ringingAlarmID);
                ReceiveNotifyService.resetRingingAlarmID();
            }else{
                LogInfo.d("the bell is not running ");
            }
        }

        //水平滑动控件信息处理
        LogInfo.d("horizontalAdapter start");
        horizontalAdapter =new HorizontalAdapter(hourInfoList);
        horizontalRecyclerView.setAdapter(horizontalAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        horizontalRecyclerView.setLayoutManager(linearLayoutManager);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //加载界面图片，若缓存没有，则去网上获取
        String bingPic = prefs.getString("bing_pic",null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPig();
        }
        //加载小时级天气信息，若缓存没有，则去网上获取
        getHourlyWeather(prefs);
        //加载天级天气信息，若缓存没有，则去网上获取
        getDailyWeather(prefs);

        //下拉刷新逻辑
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogInfo.d("swipeRefreshLayout start.Thread="+Thread.currentThread().getId());
                List<SelectedInfo> selectedInfoList= LitePal.findAll(SelectedInfo.class);
                requestWeather(selectedInfoList.get(0).getWeatherID());
                requestDailyWeather(selectedInfoList.get(0).getWeatherID());
                loadBingPig();
            }
        });

        //切换到闹钟页面
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("打开闹钟页面.Thread="+Thread.currentThread().getId());
                Intent intent=new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });

        //打开滑动菜单
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("openDrawer start.Thread="+Thread.currentThread().getId());
                drawerLayout.openDrawer(GravityCompat.START);//GravityCompat.START表示侧滑菜单打开的方向
                LogInfo.d("openDrawer end" );
            }
        });

        //监听DrawerLayout的打开或关闭事件
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                 LogInfo.d("onDrawerClosed start");
                 ChooseAreaFragment chooseAreaFragment=(ChooseAreaFragment)getSupportFragmentManager().findFragmentById(R.id.choose_area_fragment);
                 chooseAreaFragment.queryProvinces();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        final KeepManage keepManage=KeepManage.getInstance(MainActivity.this);
        listener=new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                LogInfo.d("onScreenOn start.Thread="+Thread.currentThread().getId());
                keepManage.finishActivity();
            }

            @Override
            public void onScreenOff() {
                LogInfo.d("onScreenOff start.Thread"+Thread.currentThread().getId());
                keepManage.startActivity();
            }

            @Override
            public void onScreenUserPresent() {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        drawerLayout.closeDrawer(GravityCompat.START);
        LogInfo.d("MainActivity onStart start. ThreadID="+Thread.currentThread().getId());
    }

    /**
     * 根据天气id请求城市小时级天气信息
     */
    public void requestWeather (final String weatherId) {
        LogInfo.d("coolWeather", "requestWeather start. threadID="+Thread.currentThread().getId());

        String hourWeatherUrl = "https://api.caiyunapp.com/v2/kcrfFCZQeHy7Dde0/" + weatherId + "/hourly?lang=zh_CN&hourlysteps=24";
        HttpUtil.sendOkHttpRequest(hourWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogInfo.d( "requestWeather onFailure.thread="+Thread.currentThread().getId());
                //切线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogInfo.d("runOnUiThread start. threadID"+Thread.currentThread().getId());
                        swipeRefreshLayout.setRefreshing(false);
                        LogInfo.d("swipeRefreshLayout.setRefreshing(false) ok");
                        Toast.makeText(MainActivity.this, "获取天气信息失败,请检查网络连接",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogInfo.d("coolWeather","requestWeather onResponse start. threadID="+Thread.currentThread().getId());
                final String responseText = response.body().string();

                final CaiyunWeatherContent weather = Utility.handleWeatherResponse(responseText);

                LogInfo.d("切到UI线程");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            Log.d("coolWeather","WeatherActivity handleWeatherResponse ok. threadID="+Thread.currentThread().getId());
                            //将取得的weather信息存入缓存中
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    MainActivity.this).edit();
                            editor.putString("weather",responseText );
                            editor.apply();

                            //改变数据库中当前选择的城市的信息
                            List<SelectedInfo> selectedInfoList=LitePal.findAll(SelectedInfo.class);
                            if(0==selectedInfoList.size()){
                                LogInfo.d("SelectedInfo in dataBase is null");
                                County selectedCounty=new County();
                                selectedCounty.setCountyName("北京市市辖区");
                                selectedCounty.setWeatherId("116.395645,39.929986");
                            }else {
                                LogInfo.d("SelectedInfo in dataBase is not null");
                                County selectedCounty=ChooseAreaFragment.getSelectedCounty();
                                changeSelectedInfo(selectedCounty);
                                showWeatherInfo(weather);
                            }
                        }else {
                            LogInfo.d("requestWeather fail. threadId="+Thread.currentThread().getId());
                            Toast.makeText(MainActivity.this,"获取天气信息失败，请检查网络连接",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示CaiyunWeatherContent实体类中的数据
     */
    private void showWeatherInfo(CaiyunWeatherContent weather){
        LogInfo.d("coolWeather","showWeatherInfo start. threadID="+Thread.currentThread().getId());

        List<Temperature> tepList = weather.temperatureList;
        List<Skycon> skyconsList= weather.skyconList;
        String weatherInfo = weather.description;

        if(null!=getSelectedInfo()){
            LogInfo.d("getSelectedInfo="+getSelectedInfo().getCountyName());
            countyTitle.setText(getSelectedInfo().getCountyName());
        }else{
            LogInfo.d("getSelectedInfo is null");
        }
        //即使温度、天气情况
        todayInfo.setText(convertToChinese(skyconsList.get(0).value));
        description.setText(weatherInfo);
        String currentTemp=convertTempToInteger(tepList.get(0).value);
        todayTemp.setText(currentTemp+"°");
        //能见度、pm25、aqi
        visibility.setText(weather.visibility.get(0).value);
        pm25.setText(weather.pm25List.get(0).value);
        aqi.setText(weather.aqiList.get(0).value);
        //24小时预报
        hourInfoList.clear();
        for (int i=0;i<skyconsList.size();i++){
            HourlyInfo hourlyInfo=new HourlyInfo();
            String converString=getTime(weather.skyconList.get(i).detetime);
            hourlyInfo.setTime(converString);
            converString=convertToChinese(weather.skyconList.get(i).value);
            hourlyInfo.setWeatherInfo(converString);
            String temp=convertTempToInteger(weather.temperatureList.get(i).value);
            hourlyInfo.setTemp(temp+"°");
            hourInfoList.add(hourlyInfo);
        }
        horizontalAdapter.refreshAdapterList(hourInfoList);
        horizontalAdapter.notifyDataSetChanged();
    }

    /**
     *更改数据库中当前选中城市的数据
     */
    private void changeSelectedInfo(County selectedCounty){
        LogInfo.d("changeSelectedInfo start.Thread="+Thread.currentThread().getId());
        SelectedInfo selectedInfo=new SelectedInfo();
        selectedInfo.setCountyName(selectedCounty.getCountyName());
        selectedInfo.setWeatherID(selectedCounty.getWeatherId());
        selectedInfo.updateAll();
    }

    /**
     *获取数据库中当前选中城市的数据
     */
    public SelectedInfo getSelectedInfo(){
        List<SelectedInfo> selectedInfoList= LitePal.findAll(SelectedInfo.class);
        if (null!=selectedInfoList){
            return selectedInfoList.get(0);
        }else{
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        LogInfo.d("onDestroy start.Thread="+Thread.currentThread().getId());
        super.onDestroy();
        listener.unregisterListener();
    }

    /**
     * 根据天气id请求城市Daily级天气信息
     */
    public void requestDailyWeather (final String weatherId) {
        LogInfo.d("coolWeather", "requestDailyWeather start. threadID="+Thread.currentThread().getId());

        String dailyWeatherUrl ="https://api.caiyunapp.com/v2/kcrfFCZQeHy7Dde0/"+weatherId+"/daily.json?lang=zh_CN&dailysteps=7";
        HttpUtil.sendOkHttpRequest(dailyWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogInfo.d( "requestDailyWeather onFailure. threadID="+Thread.currentThread().getId());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogInfo.d("coolWeather","requestDailyWeather onResponse start. threadID="+Thread.currentThread().getId());
                final String responseText = response.body().string();

                final CaiyunDailyWeatherContent weather = Utility.handleDailyWeatherResponse(responseText);

                //将取得的weather信息存入缓存中
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                        MainActivity.this).edit();
                editor.putString("dailyWeather",responseText );
                editor.apply();

                LogInfo.d("切到UI线程");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            LogInfo.d("coolWeather","WeatherActivity handleDailyWeatherResponse ok. threadID="+Thread.currentThread().getId());
                            //将取得的weather信息存入缓存中
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    MainActivity.this).edit();
                            editor.putString("dailyWeather",responseText );
                            editor.apply();
                            showDailyWeatherInfo(weather);
                        }else {
                            LogInfo.d("handleDailyWeatherResponse UI wrong");
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 处理并在未来5天预报模块处展示CaiyunDailyWeatherContent实体类中的数据
     */
    private void showDailyWeatherInfo(CaiyunDailyWeatherContent caiyunDailyWeatherContent){
        LogInfo.d("showDailyWeatherInfo start.ThreadID="+Thread.currentThread().getId());

        List<DailyTemperature> tepList = caiyunDailyWeatherContent.dailyTemperatureList;
        List<DailySkycon> skyconsList= caiyunDailyWeatherContent.dailySkyconList;

        //5天预报
        forecastLayout.removeAllViews();
        for (int i=0; i<skyconsList.size();i++){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false );
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(skyconsList.get(i).dete);
            String info = convertToChinese(skyconsList.get(i).value);
            infoText.setText(info);
            String temp=convertTempToInteger(tepList.get(i).max);
            maxText.setText(temp);
            temp=convertTempToInteger(tepList.get(i).min);
            minText.setText(temp);
            forecastLayout.addView(view);
        }
        forecastLayout.setVisibility(View.VISIBLE);
        //日出日落
        sunrise.setText(caiyunDailyWeatherContent.dailyAstroList.get(0).getSunriseTime());
        sunset.setText(caiyunDailyWeatherContent.dailyAstroList.get(0).getSunsetTime());
    }

    //加载必应每日一图
    private void loadBingPig(){
        LogInfo.d("loadBingPig start.threadID="+Thread.currentThread().getId());
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogInfo.d("loadBingPig wrong.thread="+Thread.currentThread().getId());
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogInfo.d("loadBingPig onResponse start.thread="+Thread.currentThread().getId());
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    private String convertToChinese(String weatherInfo){
        LogInfo.d("convertToChinese start");
        String info="未知";
        if (-1!=weatherInfo.indexOf("CLEAR")){
            LogInfo.d("CLEAR");
            info="晴";
        }else if (-1!=weatherInfo.indexOf("PARTLY")){
            LogInfo.d("PARTLY CLEAR");
            info="多云";
        }else if (-1!=weatherInfo.indexOf("CLOUDY")){
            LogInfo.d("CLOUDY");
            info="阴";
        }else if (-1!=weatherInfo.indexOf("RAIN")){
            LogInfo.d("RAIN");
            info="雨";
        }
        else if (-1!=weatherInfo.indexOf("WIND"))
        {
            LogInfo.d("WIND");
            info="大风";
        }else if (-1!=weatherInfo.indexOf("SNOW")){
            LogInfo.d("SNOW");
            info="雪";
        }else if (-1!=weatherInfo.indexOf("HAZE")){
            LogInfo.d("HAZE");
            info="雾霾";
        }
        return info;
    }

    private void preInit(){
        horizontalRecyclerView = (RecyclerView)findViewById(R.id.horizontal_recyclerView);
        bingPicImg = findViewById(R.id.bing_pic_img);
        drawerLayout = findViewById(R.id.drawer_layout);
        countyTitle = findViewById(R.id.current_county);
        switchButton = findViewById(R.id.switch_county);
        alarmButton = findViewById(R.id.color);
        forecastLayout = findViewById(R.id.forecast_list);
        scrollView = findViewById(R.id.main_content) ;
        todayInfo = findViewById(R.id.today_info);
        todayTemp = findViewById(R.id.today_temp);
        speedText = findViewById(R.id.speed);
        description = findViewById(R.id.description);
        sunrise = findViewById(R.id.sun_up);
        sunset = findViewById(R.id.sun_down);
        visibility = findViewById(R.id.visibility);
        pm25 = findViewById(R.id.pm);
        aqi = findViewById(R.id.aqi);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//活动的布局会显示在状态栏上面
            getWindow().setStatusBarColor(Color.TRANSPARENT);//状态栏设置透明
        }
    }

    //检查所需权限
    private void checkRequestPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED){
            LogInfo.d("SET_ALARM Permission Denied");
            requestPermissions(new String[]{Manifest.permission.SET_ALARM},1);
        }else{
            LogInfo.d("SET_ALARM Permission Exists");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED){
            LogInfo.d("WAKE_LOCK Permission Denied");
            requestPermissions(new String[]{Manifest.permission.WAKE_LOCK},1);
        }else{
            LogInfo.d("WAKE_LOCK Permission Exists");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED){
            LogInfo.d("RECEIVE_BOOT_COMPLETED Permission Denied");
            requestPermissions(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},1);
        }else{
            LogInfo.d("RECEIVE_BOOT_COMPLETED Permission Exists");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED){
            LogInfo.d("VIBRATE Permission Denied");
            requestPermissions(new String[]{Manifest.permission.VIBRATE},1);
        }else{
            LogInfo.d("VIBRATE Permission Exists");
        }
    }

    private String getTime(String time){
        LogInfo.d("getTime start");
        int index=time.indexOf(' ');
        String hourTime=null;
        if (-1!=index){
            hourTime=time.substring(index);
        }else {
            LogInfo.d("wrong","indexOf wrong");
        }
        return hourTime;
    }

    private String convertTempToInteger(String tem){
        int current=(int) Math.round(Double.valueOf(tem));
        return String.valueOf(current);
    }

    //若正在响铃，则停止响铃
    public void stopRing(int alarmID){
        LogInfo.d("stopRing start");
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(alarmID);
    }


    /**
     * 加载天级天气信息，若缓存没有，则去网上获取
     */
    private void getDailyWeather(SharedPreferences prefs){
        LogInfo.d("getDailyWeather start");
        if (prefs.getString("weather", null ) == null){
            //无缓存时去网站服务器查询天气,默认为北京市
            LogInfo.d("coolWeather","MainActivity request weather data from server" );
            forecastLayout.setVisibility(View.INVISIBLE);
            ChooseAreaFragment.setSelectedCounty("北京市市辖区","116.395645,39.929986");
            Log.d("coolWeather","requestWeather");
            requestDailyWeather("116.395645,39.929986");
        }else {
            LogInfo.d("coolWeather","MainActivity PreferenceManager.getDefaultSharedPreferences is not null" );
            SelectedInfo selectedInfo=getSelectedInfo();
            if (selectedInfo!=null){
                final CaiyunDailyWeatherContent dailyWeatherContent=Utility.handleDailyWeatherResponse((prefs.getString("dailyWeather", null )));
                showDailyWeatherInfo(dailyWeatherContent);
            }else{
                Log.d("coolWeather","selected is null" );
            }
        }
    }

    /**
     * 加载天气信息，若缓存没有，则去网上获取
     */
    private void getHourlyWeather(SharedPreferences prefs){
        LogInfo.d("getHourlyWeather start");
        if (prefs.getString("weather", null ) == null){
            LogInfo.d("coolWeather","MainActivity PreferenceManager.getDefaultSharedPreferences is null" );
            //无缓存时去网站服务器查询天气,默认为北京市
            LogInfo.d("coolWeather","MainActivity request weather data from server" );

            forecastLayout.setVisibility(View.INVISIBLE);
            ChooseAreaFragment.setSelectedCounty("北京市市辖区","116.395645,39.929986");
            Log.d("coolWeather","requestWeather");
            requestWeather("116.395645,39.929986");
        }else {
            LogInfo.d("coolWeather","MainActivity PreferenceManager.getDefaultSharedPreferences is not null" );
            SelectedInfo selectedInfo=getSelectedInfo();
            if (selectedInfo!=null){
                final CaiyunWeatherContent weather = Utility.handleWeatherResponse((prefs.getString("weather", null )));
                showWeatherInfo(weather);
            }else{
                Log.d("coolWeather","selected is null" );
            }
        }
    }
}

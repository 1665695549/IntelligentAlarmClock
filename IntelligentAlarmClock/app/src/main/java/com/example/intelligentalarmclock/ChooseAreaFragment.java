package com.example.intelligentalarmclock;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.intelligentalarmclock.db.City;
import com.example.intelligentalarmclock.db.County;
import com.example.intelligentalarmclock.db.Province;
import com.example.intelligentalarmclock.db.SelectedInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *说明：这个是切换城市的左滑子页面，主要内含一个ListView
 */
public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titilText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dataList=new ArrayList<String>();

    /**
     * 省列表
     */
    private  List<Province> m_provinceList;
    /**
     * 市列表
     */
    private List<City> m_cityList;
    /**
     * 县列表
     */
    private List<County> m_countyList;
    /**
     *选中的省
     */
    private Province m_selectedProvince;
    /**
     * 选中的市
     */
    private City m_selectedCity;

    /**
     * 选中的县
     */
    public static County m_selectedCounty;

    /**
     * 当前选中的级别
     */
    private int m_currentLevel;
    /**
     *加载控件，为ListView设置适配器
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_arae, container,false);
        LogInfo.d("coolWeather","ChooseAreaFragment onCreateView. threadID="+Thread.currentThread().getId());

        List<SelectedInfo> selectedInfoList=LitePal.findAll(SelectedInfo.class);
        if (0==selectedInfoList.size()){
            LogInfo.d("there are selectInfo");
            m_selectedCounty=new County();
            m_selectedCounty.setCountyName("请选择城市");
        }else{
            LogInfo.d("there are not selectInfo");
            m_selectedCounty=new County();
            m_selectedCounty.setCountyName(selectedInfoList.get(0).getCountyName());
        }

        titilText =  view.findViewById(R.id.title_text);
        backButton =  view.findViewById(R.id.back_button);
        listView =  view.findViewById(R.id.list_view);

        adapter = new  ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        //listView.setVisibility(View.VISIBLE);
        return view;
    }

    /**
     *为Butto和ListView设置点击事件
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogInfo.d("ChooseAreaFragment onActivityCreated.ThreadID="+Thread.currentThread().getId() );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (m_currentLevel == LEVEL_PROVINCE){
                    m_selectedProvince = m_provinceList.get(position);
                    queryCities();
                }else if (m_currentLevel == LEVEL_CITY){
                    m_selectedCity = m_cityList.get(position);
                    queryCounties();
                }else if (m_currentLevel == LEVEL_COUNTY){
                    m_selectedCounty = m_countyList.get(position);
                    String weatherId = m_selectedCounty.getWeatherId();
                    MainActivity mainActivity=(MainActivity)getActivity();
                    mainActivity.drawerLayout.closeDrawers();//收起切换城市列表
                    mainActivity.swipeRefreshLayout.setRefreshing(true);//显示刷新图标
                    mainActivity.requestWeather(weatherId);//根据经纬度请求hourly天气信息
                    mainActivity.requestDailyWeather(weatherId);//根据经纬度请求daily天气信息
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (m_currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (m_currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去assets里的json数据查询
     */
     public void queryProvinces(){
        titilText.setText("中国");
        LogInfo.d( " ChooseAreaFragment queryProvinces start.Thread="+Thread.currentThread().getId());
         m_provinceList = LitePal.findAll(Province.class);
        if (m_provinceList.size() > 0){
            Log.d("coolWeather", "ChooseAreaFragment queryProvinces from LitePal");
            dataList.clear();
            for (Province province: m_provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            m_currentLevel = LEVEL_PROVINCE;
        }else {
            Log.d("coolWeather", "ChooseAreaFragment queryProvinces from province.json");
            String address = "assets/"+"province.json";
            queryFromJSONFile(address, "province");
        }
    }

    /**
     *查询选中省内所有的市，优先从数据库查询，如果没有查询到JSON文件中查询
     */
    private void queryCities(){
        LogInfo.d("ChooseAreaFragment queryCities start.ThreadID="+Thread.currentThread().getId());
        titilText.setText(m_selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        m_cityList = LitePal.where("provinceId=?",String.valueOf(m_selectedProvince.getProvinceCode())).find(City.class);
        if (m_cityList.size() > 0){
            LogInfo.d("coolWeather","* queryCities from LitePal start" );
            dataList.clear();
            for (City city: m_cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            m_currentLevel = LEVEL_CITY;
        }else {
            LogInfo.d("coolWeather","* queryCities from JSONFile start" );
            String address =  "assets/"+"city.json";
            queryFromJSONFile(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到JSON文件上查询
     */

    private void queryCounties(){
        LogInfo.d("coolWeather","ChooseAreaFragment queryCounties  start.ThreadID="+Thread.currentThread().getId() );
        titilText.setText(m_selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        m_countyList = LitePal.where("cityId=? and provinceId=?",String.valueOf(m_selectedCity.getCityCode()),
                String.valueOf(m_selectedProvince.getProvinceCode())).find(County.class);
        if (m_countyList.size() > 0){
            LogInfo.d("coolWeather","*ChooseAreaFragment queryCounties from LitePal start" );
            dataList.clear();
            for (County county: m_countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            m_currentLevel = LEVEL_COUNTY;
        }else {
            LogInfo.d("coolWeather","ChooseAreaFragment queryCounties from JSON file start" );
            int provinceID = m_selectedProvince.getProvinceCode();
            String address =  "assets/"+"countyOf"+ String.valueOf(provinceID)+".json";
            queryFromJSONFile(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从JSON文件上查询省市县数据
     */
    private void queryFromJSONFile(String address, final String type){
        LogInfo.d("coolWeather","ChooseAreaFragment queryFromJSONFile start.ThreadID="+Thread.currentThread().getId());
        showProgressDialog();
        try {
            LogInfo.d("queryFrom " + address);
            InputStream is = ChooseAreaFragment.this.getClass().getClassLoader().
                    getResourceAsStream(address);
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader  = new BufferedReader(streamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
            reader.close();
            streamReader.close();
            is.close();

           LogInfo.d("get the data and save to the database" );
            line = stringBuilder.toString();
            try{
                if ("province".equals(type)){
                    LogInfo.d("coolWeather","type=province");
                    JSONObject allProvince = new JSONObject(line);
                    line = allProvince.getString("province");
                    JSONArray provinces = new JSONArray(line);
                    for (int i=0; i<provinces.length(); i++){
                        allProvince = provinces.getJSONObject(i);
                        Province province = new Province();
                        province.setProvinceName(allProvince.getString("name"));
                        province.setProvinceCode(allProvince.getInt("id"));
                        province.save();//将全部的省级数据存储到数据库
                    }
                }else if ("city".equals(type)){
                    LogInfo.d("coolWeather","type=city");
                    int provinceID = m_selectedProvince.getProvinceCode();
                    JSONObject allCities = new JSONObject(line);
                    line = allCities.getString("city");
                    JSONArray cities = new JSONArray(line);
                    for (int i=0; i<cities.length(); i++){
                        allCities = cities.getJSONObject(i);
                        if ( provinceID == allCities.getInt("provinceId")){
                            City city = new City();
                            city.setCityCode(allCities.getInt("id"));
                            city.setCityName(allCities.getString("name"));
                            city.setProviceId(provinceID);
                            city.save();//将某个省里的全部市的数据存储到数据库
                        }
                    }
                }else if ("county".equals(type)){
                    LogInfo.d("coolWeather","type=county");
                    int cityID = m_selectedCity.getCityCode();
                    JSONObject allCounties = new JSONObject(line);
                    JSONArray counties = allCounties.getJSONArray("county");
                    for (int i=0; i<counties.length(); i++){
                        allCounties = counties.getJSONObject(i);
                        if ( cityID == allCounties.getInt("cityId") ){
                            County county = new County();
                            county.setWeatherId(allCounties.getString("id"));
                            county.setCountyName(allCounties.getString("name"));
                            county.setCityId(m_selectedCity.getCityCode());
                            county.setProvinceId(m_selectedProvince.getProvinceCode());
                            county.save();//将某个省下的某个市下的某个城市的数据存储到数据库
                        }
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
                LogInfo.d( "%%%%%%%%%%%%%%%%%% province JSONException fail");
            }

            LogInfo.d("*show the select list" );
            //回到主界面上操作
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogInfo.d("runOnUiThread start.ThreadID="+Thread.currentThread().getId());
                    closeProgressDialog();
                    if ("province".equals(type)){
                        queryProvinces();
                    }else if ("city".equals(type)){
                        queryCities();
                    }else if ("county".equals(type)){
                        queryCounties();
                    }
                }
            });
        }catch (IOException e){
            e.printStackTrace();
            Log.d("coolWeather", "%%%%%%%%%%%%%%%%%% province IOException fail");
        }
    }


    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    public static County getSelectedCounty(){
        LogInfo.d("getSelectedCounty start");
        return m_selectedCounty;
    }

    public static void setSelectedCounty(String countyName, String weatherId){
        m_selectedCounty.setCountyName(countyName);
        m_selectedCounty.setWeatherId(weatherId);
    }
}

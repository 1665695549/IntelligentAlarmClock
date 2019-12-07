package com.example.intelligentalarmclock.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.intelligentalarmclock.LogInfo;
import com.example.intelligentalarmclock.db.City;
import com.example.intelligentalarmclock.db.County;
import com.example.intelligentalarmclock.db.Province;
import com.example.intelligentalarmclock.gson.CaiyunDailyWeatherContent;
import com.example.intelligentalarmclock.gson.CaiyunWeatherContent;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    /**
     * 将返回的JSON数据解析成CaiyunWeatherContent实体类
     */
    public static CaiyunWeatherContent handleWeatherResponse(String response){
        LogInfo.d("coolWeather","Utility handleWeatherResponse start");
        try{
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("result").getJSONObject("hourly");
            String weatherContent = jsonObject.toString();
            // 从JSON结构中取需要的数据到自定义类中
            return new Gson().fromJson(weatherContent,CaiyunWeatherContent.class );
        }catch (Exception e){
            LogInfo.d("handleWeatherResponse wrong");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将返回的daily级JSON数据解析成CaiyunWeatherContent实体类
     */
    public static CaiyunDailyWeatherContent handleDailyWeatherResponse(String response){
        LogInfo.d("coolWeather","Utility handleDailyWeatherResponse start");
        try{
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("result");
            jsonObject = jsonObject.getJSONObject("daily");
            String dailyWeatherContent = jsonObject.toString();
            // 从JSON结构中取需要的数据到自定义类
            return new Gson().fromJson(dailyWeatherContent,CaiyunDailyWeatherContent.class );
        }catch (Exception e){
            LogInfo.d("somthing wrong");
            e.printStackTrace();
        }
        return null;
    }
}



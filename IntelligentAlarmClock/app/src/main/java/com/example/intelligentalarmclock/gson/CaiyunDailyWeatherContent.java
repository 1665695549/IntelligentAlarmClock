package com.example.intelligentalarmclock.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CaiyunDailyWeatherContent {

    @SerializedName("status")
    public String status;

    @SerializedName("temperature")
    public List<DailyTemperature> dailyTemperatureList;

    @SerializedName("skycon")
    public List<DailySkycon> dailySkyconList;

    @SerializedName("astro")
    public List<DailyAstro> dailyAstroList;

}

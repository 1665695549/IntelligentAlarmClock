package com.example.intelligentalarmclock.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CaiyunWeatherContent {

    @SerializedName("status")
    public String status;

    @SerializedName("description")
    public String description;

    @SerializedName("skycon")
    public List<Skycon> skyconList;

    @SerializedName("temperature")
    public List<Temperature> temperatureList;

    @SerializedName("visibility")
    public List<Visibility> visibility ;

    @SerializedName("pm25")
    public List<Pm25> pm25List ;

    @SerializedName("aqi")
    public List<Aqi> aqiList ;
}

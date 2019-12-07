package com.example.intelligentalarmclock;

public class HourlyInfo {
    private String time;
    private String weatherInfo;
    private String temp;

    public String getTime(){
        return this.time;
    }
    public void  setTime(String time){
        this.time=time;
    }
    public String getWeatherInfo(){
        return weatherInfo;
    }
    public void setWeatherInfo(String weatherInfo){
        this.weatherInfo=weatherInfo;
    }
    public String getTemp(){
        return temp;
    }
    public void setTemp(String temp){
        this.temp=temp;
    }
}

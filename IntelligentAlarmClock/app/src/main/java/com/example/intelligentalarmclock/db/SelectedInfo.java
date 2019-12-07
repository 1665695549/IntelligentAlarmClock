package com.example.intelligentalarmclock.db;

import org.litepal.crud.LitePalSupport;

public class SelectedInfo extends LitePalSupport {
    private int id;
    private String countyName;
    private String weatherID;

    public String getCountyName(){
        return this.countyName;
    }

    public void setCountyName(String countyName){
        this.countyName=countyName;
    }

    public String getWeatherID(){
        return this.weatherID;
    }

    public void setWeatherID(String weatherID){
        this.weatherID=weatherID;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id=id;
    }

}

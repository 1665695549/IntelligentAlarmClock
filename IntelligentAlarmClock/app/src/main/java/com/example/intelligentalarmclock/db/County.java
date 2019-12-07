package com.example.intelligentalarmclock.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {
    private int id;
    private  int provinceId;
    private String countyName;
    private String weatherId;
    private int cityId;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getCountyName(){
        return this.countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId(){
        return this.weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCityId(){ return this.cityId; }

    public void setProvinceId(int provinceId){this.provinceId = provinceId;}

    public int getProvinceId(){
        return this.provinceId;
    }
}

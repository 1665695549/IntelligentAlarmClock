package com.example.intelligentalarmclock.db;

import org.litepal.crud.LitePalSupport;

/**
 *存储创建的闹钟列表
 */
public class Alarm extends LitePalSupport {
    private int id;
    private boolean vality;
    private int alarmID;
    private String APm;
    private int hour;
    private String minute;//用int型,00不好存，所以用了string
    private String repeate;
    private String title;
    private String condition;
    private String weatherID;
    private String countyName;
    private long timeInMillis;

    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return  this.id;
    }
    public void setAlarmID(int alarmID){
        this.alarmID=alarmID;
    }
    public int getAlarmID(){
        return  this.alarmID;
    }
    public void setAPm(String APm){
        this.APm=APm;
    }
    public String getAPm(){
        return this.APm;
    }
    public void setHour(int hour){
        this.hour=hour;
    }
    public int getHour(){
        return  this.hour;
    }
    public void setMinute(String minute){
        this.minute=minute;
    }
    public String getMinute(){
        return this.minute;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setRepeate(String repeate){
        this.repeate=repeate;
    }
    public String getRepeate(){
        return this.repeate;
    }
    public void setCondition(String condition){
        this.condition=condition;
    }
    public String getCondition(){
        return this.condition;
    }
    public void setWeatherID(String weatherID){
        this.weatherID=weatherID;
    }
    public String getWeatherID(){
        return this.weatherID;
    }
    public void setCountyName(String countyName){
        this.countyName=countyName;
    }
    public String getCountyName(){
        return this.countyName;
    }

    public void setVality(boolean vality){
        this.vality=vality;
    }
    public boolean getVality(){
        return this.vality;
    }

    public void setTimeInMillis(long timeInMillis){
        this.timeInMillis=timeInMillis;
    }
    public long getTimeInMillis(){
        return this.timeInMillis;
    }
}

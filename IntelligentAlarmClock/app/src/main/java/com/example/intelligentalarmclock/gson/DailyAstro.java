package com.example.intelligentalarmclock.gson;

import com.google.gson.annotations.SerializedName;

public class DailyAstro {
    @SerializedName("sunset")
    public  Sunset sunset;

    @SerializedName("sunrise")
    public Sunrise sunrise;

    public String getSunriseTime(){
        return sunrise.time;
    }

    public String getSunsetTime(){
        return sunset.time;
    }

    //内置类
    class Sunset {
        @SerializedName("time")
        public String time;
    }
    class Sunrise {
        @SerializedName("time")
        public String time;
    }
}

package com.example.intelligentalarmclock.gson;

import com.google.gson.annotations.SerializedName;

public class DailyTemperature {
    @SerializedName("datetime")
    public String datetime;

    @SerializedName("max")
    public String max;

    @SerializedName("min")
    public String min;
}

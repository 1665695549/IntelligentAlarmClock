package com.example.intelligentalarmclock.gson;

import com.google.gson.annotations.SerializedName;

public class Temperature {
    @SerializedName("value")
    public String value;

    @SerializedName("datetime")
    public String datetime;
}

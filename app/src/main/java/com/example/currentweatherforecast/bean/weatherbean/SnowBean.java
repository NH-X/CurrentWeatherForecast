package com.example.currentweatherforecast.bean.weatherbean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SnowBean implements Serializable {
    /**
     * 1h : 0.84
     */

    @SerializedName("1h")
    public double _$1h;

    public static SnowBean objectFromData(String str) {

        return new Gson().fromJson(str, SnowBean.class);
    }
}

package com.example.currentweatherforecast.bean.weatherbean;

import com.google.gson.Gson;

import java.io.Serializable;

public class CoordBean implements Serializable {
    /**
     * lon : 10.99
     * lat : 44.34
     */

    public double lon;          //经度
    public double lat;          //纬度
    public String timeone;
    public String timeone_offset;

    public static CoordBean objectFromData(String str) {

        return new Gson().fromJson(str, CoordBean.class);
    }
}

package com.example.currentweatherforcecast.bean.weatherbean;

import com.google.gson.Gson;

import java.io.Serializable;

public class WeatherBean implements Serializable {
    /**
     * id : 802
     * main : Clouds
     * description : scattered clouds
     * icon : 03d
     */

    public int id;
    public String main;
    public String description;
    public String icon="01d";

    public static WeatherBean objectFromData(String str) {

        return new Gson().fromJson(str, WeatherBean.class);
    }
}
package com.example.currentweatherforecast.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.bean.citybean.CityInfo;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.example.currentweatherforecast.task.CurrentCityRequest;

import java.util.ArrayList;
import java.util.List;

public class CityRepository {
    private static final String TAG="CityRepository";

    private static CityRepository instance;
    private CoordBean coord;
    private List<CityInfo> cityDataSet=new ArrayList<>();
    private MutableLiveData<List<CityInfo>> mCity=new MutableLiveData<>();
    private MutableLiveData<String> cityName=new MutableLiveData<>("Earth");
    private static CurrentCityRequest cityRequest;

    public static CityRepository getInstance(CoordBean coord){
        if(instance==null){
            instance=new CityRepository();
        }
        instance.coord=coord;
        return instance;
    }

    /**
     * 向远程服务器发送请求，获取当前所在城市
     */
    public void startRequest(MutableLiveData<Resource<List<CityInfo>>> mRequestSchedule){
        mRequestSchedule.setValue(new Resource<>(
                null,
                RequestProcessType.request_executing,
                "等待中"
        ));
        cityRequest=new CurrentCityRequest(mRequestSchedule,cityName);
        cityRequest.setCityInfo(mCity).run(coord);

        Log.d(TAG, "startRequest: run");
    }

    public MutableLiveData<List<CityInfo>> getCityInfo(){
        MutableLiveData<List<CityInfo>> dataSet=new MutableLiveData<>();
        dataSet.setValue(cityDataSet);

        return dataSet;
    }

    public MutableLiveData<String> getCityName(){
        return cityName;
    }
}

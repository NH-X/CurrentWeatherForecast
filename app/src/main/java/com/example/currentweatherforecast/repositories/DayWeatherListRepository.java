package com.example.currentweatherforecast.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo.DailyBean;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.example.currentweatherforecast.task.DayWeatherListRequest;

import java.util.List;

public class DayWeatherListRepository {
    private static final String TAG="DayWeatherListRepository";

    private static DayWeatherListRepository instance;
    private CoordBean coord;
    private WeatherDayInfo dayWeatherDataSet =new WeatherDayInfo();
    private MutableLiveData<List<DailyBean>> dailyWeatherDataSet=new MutableLiveData<>();

    private static DayWeatherListRequest dayWeatherListRequest;

    public static DayWeatherListRepository getInstance(CoordBean coord) {
        if (null == instance) {
            instance = new DayWeatherListRepository();
        }
        instance.coord=coord;

        return instance;
    }

    public void setCoordBean(CoordBean coord){
        this.coord=coord;
    }

    /**
     * 向远程服务器发送请求，获取当前天气和24小时天气。
     */
    public void startRequest(MutableLiveData<Resource<WeatherDayInfo>> mRequestSchedule){
        mRequestSchedule.setValue(new Resource<>(
                null,
                RequestProcessType.request_executing,
                "等待中"
        ));
        dayWeatherListRequest=new DayWeatherListRequest(mRequestSchedule);
        dayWeatherListRequest.setWeatherDailyBean(dailyWeatherDataSet).run(coord);
        Log.d(TAG, "startRequest: run");
    }

    public MutableLiveData<WeatherDayInfo> getDayWeatherList(){
        MutableLiveData<WeatherDayInfo> dataList=new MutableLiveData<>();
        dataList.setValue(dayWeatherDataSet);

        return dataList;
    }

    public MutableLiveData<List<DailyBean>> getDailyWeatherList() {
        Log.d(TAG, "getDailyWeatherList: run "+(
                dailyWeatherDataSet == null?"mWeatherDailyBean is null" :
                        dailyWeatherDataSet.getValue() == null ? "mWeatherDailyBean value is null" :
                                dailyWeatherDataSet.getValue().size()));
        return dailyWeatherDataSet;
    }
}

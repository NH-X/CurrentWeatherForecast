package com.example.currentweatherforcecast.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforcecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforcecast.bean.weatherbean.WeatherDayInfo;
import com.example.currentweatherforcecast.bean.weatherbean.WeatherDayInfo.DailyBean;
import com.example.currentweatherforcecast.respost.RequestProcessType;
import com.example.currentweatherforcecast.respost.Resource;
import com.example.currentweatherforcecast.task.DayWeatherListRequestTask;
import com.example.currentweatherforcecast.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class DayWeatherListRepository {
    private static final String TAG="DayWeatherListRepository";

    private static DayWeatherListRepository instance;
    private CoordBean coord;
    private MainViewModel viewModel;
    private WeatherDayInfo dayWeatherDataSet =new WeatherDayInfo();
    private MutableLiveData<List<DailyBean>> dailyWeatherDataSet=new MutableLiveData<>();

    private static DayWeatherListRequestTask dayWLRequestTask;

    public static DayWeatherListRepository getInstance(
            CoordBean coord,
            MainViewModel viewModel) {
        if (null == instance) {
            instance = new DayWeatherListRepository();
        }
        instance.coord=coord;
        instance.viewModel=viewModel;

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
        dayWLRequestTask =new DayWeatherListRequestTask(dayWeatherDataSet,mRequestSchedule);
        dayWLRequestTask.setWeatherDailyBean(dailyWeatherDataSet);
        Log.d(TAG, "startRequest: run");
        dayWLRequestTask.execute(coord);
    }

    public void setWeatherDayInfo(WeatherDayInfo weatherDayInfo){
        this.dayWeatherDataSet =weatherDayInfo;
        this.viewModel.setWeatherDay(weatherDayInfo);
        Log.d(TAG, "setWeatherDayInfo: dayWeatherDataSet is null?"+(dayWeatherDataSet ==null));
        Log.d(TAG, "setWeatherDayInfo: dailyBean is null?"+(dayWeatherDataSet.daily==null));
    }

    public MutableLiveData<WeatherDayInfo> getDayWeatherList(){
        MutableLiveData<WeatherDayInfo> dataList=new MutableLiveData<>();
        dataList.setValue(dayWeatherDataSet);

        return dataList;
    }

    public MutableLiveData<List<DailyBean>> getDailyWeatherList() {
        return dailyWeatherDataSet;
    }

    public List<DailyBean> refreshDailyWeather(){
        return dayWeatherDataSet.daily;
    }
}

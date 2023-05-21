package com.example.currentweatherforecast.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.CurrentBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.HourlyBean;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.example.currentweatherforecast.task.HourWeatherListRequestTask;
import com.example.currentweatherforecast.viewmodel.MainViewModel;

import java.util.List;

public class HourWeatherRepository {
    private static final String TAG="HourWeatherRepository";

    private static HourWeatherRepository instance;
    private CoordBean coord;
    private WeatherHourInfo hourWeatherDataSet =new WeatherHourInfo();
    private MutableLiveData<List<HourlyBean>> hourlyWeatherDataSet=new MutableLiveData<>();
    private MutableLiveData<CurrentBean> currentWeatherDataSet=new MutableLiveData<>();

    private static HourWeatherListRequestTask hourWLRequestTask;

    public static HourWeatherRepository getInstance(CoordBean coord) {
        if (null == instance) {
            instance = new HourWeatherRepository();
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
    public void startRequest(MutableLiveData<Resource<WeatherHourInfo>> mRequestSchedule){
        mRequestSchedule.setValue(new Resource<>(
                null,
                RequestProcessType.request_executing,
                "等待中"
        ));
        hourWLRequestTask=new HourWeatherListRequestTask(mRequestSchedule);
        hourWLRequestTask
                .setHourInfo(hourWeatherDataSet)
                .setHourlyBean(hourlyWeatherDataSet)
                .setCurrentBean(currentWeatherDataSet);
        Log.d(TAG, "startRequest: run");
        hourWLRequestTask.execute(coord);
    }

//    public void setWeatherHourInfo(WeatherHourInfo weatherHourInfo){
//        this.hourWeatherDataSet =weatherHourInfo;
//        this.viewModel.setWeatherHour(weatherHourInfo);
//        Log.d(TAG, "setWeatherHourInfo: weatherHourInfo is null? "+(weatherHourInfo==null));
//        Log.d(TAG, "setWeatherHourInfo: currentBean is null? "+(weatherHourInfo==null?true:(weatherHourInfo.current==null)));
//    }

    public MutableLiveData<WeatherHourInfo> getHourWeather() {
        MutableLiveData<WeatherHourInfo> dataList=new MutableLiveData<>();
        dataList.setValue(hourWeatherDataSet);

        return dataList;
    }

    public MutableLiveData<CurrentBean> getCurrentWeather(){
        return currentWeatherDataSet;
    }

    public MutableLiveData<List<HourlyBean>> getHourBean(){
        return hourlyWeatherDataSet;
    }
}
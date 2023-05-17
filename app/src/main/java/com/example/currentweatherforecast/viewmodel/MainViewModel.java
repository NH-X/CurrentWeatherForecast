package com.example.currentweatherforecast.viewmodel;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo.DailyBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.CurrentBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.HourlyBean;
import com.example.currentweatherforecast.repositories.DayWeatherListRepository;
import com.example.currentweatherforecast.repositories.HourWeatherRepository;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;

import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String TAG="MainViewModel";

    private DayWeatherListRepository dayWeatherRepo;
    private HourWeatherRepository hourWeatherRepo;
    private CoordBean mCityCoord;
    private MutableLiveData<WeatherDayInfo> mWeatherDayInfo;
    private MutableLiveData<WeatherHourInfo> mWeatherHourInfo;
    private MutableLiveData<List<DailyBean>> mWeatherDailyBean;
    private MutableLiveData<List<HourlyBean>> mWeatherHourlyBean;
    private MutableLiveData<CurrentBean> currentWeather;
    private MutableLiveData<Resource<WeatherHourInfo>> hourWeatherResource=new MutableLiveData<>();
    private MutableLiveData<Resource<WeatherDayInfo>> dayWeatherRequest = new MutableLiveData<>(
            new Resource<>(
                    null,
                    RequestProcessType.request_executing,
                    "")
    );

    public void init(CoordBean coord){
        mCityCoord=coord;
        dayWeatherRepo = DayWeatherListRepository.getInstance(mCityCoord,this);
        hourWeatherRepo= HourWeatherRepository.getInstance(mCityCoord,this);

        mWeatherHourlyBean=hourWeatherRepo.getHourBean();
        mWeatherHourInfo=hourWeatherRepo.getHourWeather();
        currentWeather=hourWeatherRepo.getCurrentWeather();

        mWeatherDayInfo = dayWeatherRepo.getDayWeatherList();
        mWeatherDailyBean= dayWeatherRepo.getDailyWeatherList();

        Log.d(TAG, "init: currentBean is null ?"+(currentWeather.getValue()==null));
    }

    public LiveData<List<HourlyBean>> getWeatherHourlyList(){
        return mWeatherHourlyBean;
    }

    public LiveData<CurrentBean> getCurrentWeather(){
        return currentWeather;
    }

    public LiveData<WeatherDayInfo> getWeatherDayList(){
        return mWeatherDayInfo;
    }

    public LiveData<List<DailyBean>> getWeatherDailyList(){
        return mWeatherDailyBean;
    }

    public LiveData<WeatherHourInfo> getWeatherHour(){
        return mWeatherHourInfo;
    }

    public LiveData<Resource<WeatherDayInfo>> getDayResourceSchedule(){
        return dayWeatherRequest;
    }

    public LiveData<Resource<WeatherHourInfo>> getHourResourceSchedule(){
        return hourWeatherResource;
    }

    public void setWeatherHour(WeatherHourInfo weatherHourInfo){
        mWeatherHourlyBean.postValue(weatherHourInfo.hourly);
        mWeatherHourInfo.postValue(weatherHourInfo);
        currentWeather.postValue(weatherHourInfo.current);
    }

    public void setWeatherDay(WeatherDayInfo weatherDayInfo){
        mWeatherDayInfo.postValue(weatherDayInfo);
        mWeatherDailyBean.postValue(weatherDayInfo.daily);
    }

    public void onResume(){
        // 立即执行一次请求
        requestHandler.post(requestRunnable);
    }

    public void onPause(){
        // 停止请求的定时执行
        requestHandler.removeCallbacks(requestRunnable);
    }

    private Integer delayTime=60*1000;
    private Handler requestHandler = new Handler();
    private Runnable requestRunnable = new Runnable() {
        @Override
        public void run() {
            //获取当天当前天气和后48小时天气
            hourWeatherRepo.startRequest(hourWeatherResource);
            mWeatherHourlyBean.postValue(hourWeatherRepo.refreshHourlyWeather());
            mWeatherHourInfo.postValue(hourWeatherRepo.refreshHourInfoWeather());
            currentWeather.postValue(hourWeatherRepo.refreshCurrentWeather());

            //获取当天起到后8天天气
            dayWeatherRepo.startRequest(dayWeatherRequest);
            mWeatherDailyBean.postValue(dayWeatherRepo.refreshDailyWeather());

            // 延迟5分钟后再次执行请求
            requestHandler.postDelayed(requestRunnable, delayTime);
        }
    };
}

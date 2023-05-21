package com.example.currentweatherforecast.viewmodel;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.currentweatherforecast.bean.citybean.CityInfo;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo.DailyBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.CurrentBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.HourlyBean;
import com.example.currentweatherforecast.repositories.CityRepository;
import com.example.currentweatherforecast.repositories.DayWeatherListRepository;
import com.example.currentweatherforecast.repositories.HourWeatherRepository;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;

import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String TAG="MainViewModel";

    private DayWeatherListRepository dayWeatherRepo;
    private HourWeatherRepository hourWeatherRepo;
    private CityRepository cityRepo;

    private CoordBean mCityCoord;
    private MutableLiveData<WeatherDayInfo> mWeatherDayInfo;
    private MutableLiveData<WeatherHourInfo> mWeatherHourInfo;
    private MutableLiveData<List<CityInfo>> mCityInfo;
    private MutableLiveData<List<DailyBean>> mWeatherDailyBean;
    private MutableLiveData<List<HourlyBean>> mWeatherHourlyBean;
    private MutableLiveData<CurrentBean> mCurrentWeather;
    private MutableLiveData<String> mCityName;
    private MutableLiveData<Resource<WeatherHourInfo>> hourWeatherResource=new MutableLiveData<>();
    private MutableLiveData<Resource<WeatherDayInfo>> dayWeatherRequest = new MutableLiveData<>(
            new Resource<>(
                    null,
                    RequestProcessType.request_executing,
                    "")
    );
    private MutableLiveData<Resource<List<CityInfo>>> cityRequest=new MutableLiveData<>();

    public void init(CoordBean coord){
        mCityCoord=coord;
        dayWeatherRepo = DayWeatherListRepository.getInstance(mCityCoord);
        hourWeatherRepo= HourWeatherRepository.getInstance(mCityCoord);
        cityRepo=CityRepository.getInstance(mCityCoord);

        mWeatherHourlyBean=hourWeatherRepo.getHourBean();
        mWeatherHourInfo=hourWeatherRepo.getHourWeather();
        mCurrentWeather =hourWeatherRepo.getCurrentWeather();

        mWeatherDayInfo = dayWeatherRepo.getDayWeatherList();
        mWeatherDailyBean= dayWeatherRepo.getDailyWeatherList();

        mCityInfo=cityRepo.getCityInfo();
        mCityName=cityRepo.getCityName();

        Log.d(TAG, "init: currentBean is null ?"+(mCurrentWeather.getValue()==null));
    }

    public LiveData<List<HourlyBean>> getWeatherHourlyList(){
        return mWeatherHourlyBean;
    }

    public LiveData<CurrentBean> getmCurrentWeather(){
        return mCurrentWeather;
    }

    public LiveData<String> getCurrentCityName(){
        return mCityName;
    }

    /**
     * 这三个方法预留，当前用不到，但是后期扩展业务可能会用到
     */
    /*
    public LiveData<WeatherDayInfo> getWeatherDayList(){
        return mWeatherDayInfo;
    }

    public LiveData<WeatherHourInfo> getWeatherHour(){
        return mWeatherHourInfo;
    }

    public LiveData<List<CityInfo>> getCurrentCity(){
        return mCityInfo;
    }
    */

    public LiveData<List<DailyBean>>  getWeatherDailyList(){
        Log.d(TAG, "getWeatherDailyList: run "+(
                mWeatherDailyBean == null?"mWeatherDailyBean is null" :
                        mWeatherDailyBean.getValue() == null ? "mWeatherDailyBean value is null" :
                                mWeatherDailyBean.getValue().size()));
        return mWeatherDailyBean;
    }

    public LiveData<Resource<WeatherDayInfo>> getDayResourceSchedule(){
        return dayWeatherRequest;
    }

    public LiveData<Resource<WeatherHourInfo>> getHourResourceSchedule(){
        return hourWeatherResource;
    }

    public LiveData<Resource<List<CityInfo>>> getCityResourceSchedule(){
        return cityRequest;
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
            //获取当前所在城市
            cityRepo.startRequest(cityRequest);

            //获取当天当前天气和后48小时天气
            hourWeatherRepo.startRequest(hourWeatherResource);

            //获取当天起到后8天天气
            dayWeatherRepo.startRequest(dayWeatherRequest);

            // 延迟5分钟后再次执行请求
            requestHandler.postDelayed(requestRunnable, delayTime);
        }
    };
}

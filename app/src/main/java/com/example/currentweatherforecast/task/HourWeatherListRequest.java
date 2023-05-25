package com.example.currentweatherforecast.task;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.BuildConfig;
import com.example.currentweatherforecast.bean.RequestUrl;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HourWeatherListRequest {
    private static final String TAG="HourWeatherListRequestTask";

    private MutableLiveData<Resource<WeatherHourInfo>> mRequestSchedule;
    private MutableLiveData<List<WeatherHourInfo.HourlyBean>> mHourlyBean;
    private MutableLiveData<WeatherHourInfo.CurrentBean> mCurrentBean;

    public HourWeatherListRequest(
            MutableLiveData<Resource<WeatherHourInfo>> requestSchedule) {
        this.mRequestSchedule=requestSchedule;
    }

    public void run(CoordBean coordinate){
        final WeatherHourInfo[] hourWeatherData = {null};

        String requestUrl = RequestUrl.WeatherHourURL
                .replace("{lat}", String.valueOf(coordinate.lat))
                .replace("{lon}", String.valueOf(coordinate.lon))
                .replace("{apiKey}", BuildConfig.MY_WEATHER_API_KEY)
                .replace("{lang}", "en");
        Log.d(TAG, "doInBackground: requestUrl="+requestUrl);
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(requestUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mRequestSchedule.postValue(new Resource<>(
                        hourWeatherData[0],
                        RequestProcessType.request_error_city_not_found,
                        e.getMessage()
                ));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if(response.isSuccessful()){
                    String cityHourWeatherResponse=null;
                    try{
                        cityHourWeatherResponse=response.body().string();
                    } catch (IOException e) {
                        mRequestSchedule.postValue(new Resource<>(
                                hourWeatherData[0],
                                RequestProcessType.request_error_network_connection_failed,
                                "网络异常"
                        ));
                        Log.d(TAG, "doInBackground: 网络异常"+e.getMessage());
                    }
                    Gson gson=new Gson();
                    hourWeatherData[0] =gson.fromJson(cityHourWeatherResponse,WeatherHourInfo.class);
                    if(hourWeatherData[0]!=null){
                        if(mHourlyBean!=null){
                            mHourlyBean.postValue(hourWeatherData[0].hourly);
                        }
                        if(mCurrentBean!=null){
                            mCurrentBean.postValue(hourWeatherData[0].current);
                        }
                        mRequestSchedule.postValue(new Resource<>(
                                hourWeatherData[0],
                                RequestProcessType.request_success,
                                "成功"
                        ));
                    }
                    else{
                        mRequestSchedule.postValue(new Resource<>(
                                hourWeatherData[0],
                                RequestProcessType.request_error_city_not_found,
                                "未找到该城市的天气信息"
                        ));
                    }
                }
            }
        });
    }

    public void setSchedule(MutableLiveData<Resource<WeatherHourInfo>> requestSchedule){
        this.mRequestSchedule=requestSchedule;
    }

    public HourWeatherListRequest setHourlyBean(MutableLiveData<List<WeatherHourInfo.HourlyBean>> mHourlyBean){
        this.mHourlyBean=mHourlyBean;
        return this;
    }

    public HourWeatherListRequest setCurrentBean(MutableLiveData<WeatherHourInfo.CurrentBean> mCurrentBean){
        this.mCurrentBean=mCurrentBean;
        return this;
    }
}

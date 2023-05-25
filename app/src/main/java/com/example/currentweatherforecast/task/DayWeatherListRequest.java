package com.example.currentweatherforecast.task;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.BuildConfig;
import com.example.currentweatherforecast.bean.RequestUrl;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo.DailyBean;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DayWeatherListRequest {
    private static final String TAG="DayWeatherListRequestTask";

    private MutableLiveData<Resource<WeatherDayInfo>> mRequestSchedule;
    private MutableLiveData<List<DailyBean>> mDailyBean;

    public DayWeatherListRequest(
            MutableLiveData<Resource<WeatherDayInfo>> requestSchedule){
        this.mRequestSchedule=requestSchedule;
    }

    public void run(CoordBean coordinate){
        final WeatherDayInfo[] dayWeatherData = {null};

        String requestUrl= RequestUrl.WeatherDayUrl
                .replace("{lat}",String.valueOf(coordinate.lat))
                .replace("{lon}",String.valueOf(coordinate.lon))
                .replace("{apiKey}", BuildConfig.MY_WEATHER_API_KEY)
                .replace("{lang}","en");
        Log.d(TAG, "doInBackground: requestUrl="+requestUrl);

        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(requestUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mRequestSchedule.postValue(new Resource<>(
                        dayWeatherData[0],
                        RequestProcessType.request_error_city_not_found,
                        e.getMessage()
                ));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                if(response.isSuccessful()){
                    String dayWeatherResponse =null;
                    try{
                        dayWeatherResponse =response.body().string();
                    }catch (IOException e) {
                        mRequestSchedule.postValue(new Resource<>(
                                null,
                                RequestProcessType.request_error_network_connection_failed,
                                "网络异常"
                        ));
                        Log.d(TAG, "doInBackground: 网络异常"+e.getMessage());
                    }
                    Gson gson=new Gson();
                    dayWeatherData[0] =gson.fromJson(dayWeatherResponse,WeatherDayInfo.class);
                    mRequestSchedule.postValue(new Resource<>(
                            dayWeatherData[0],
                            RequestProcessType.request_success,
                            "成功"));
                    if(dayWeatherData!=null){
                        if(mDailyBean!=null && !Objects.equals(mDailyBean.getValue(),dayWeatherData[0].daily)){
                            mDailyBean.postValue( dayWeatherData[0].daily);
                            Log.d(TAG, "doInBackground: mDailyBean is null? "+(mDailyBean==null));
                        }
                    }
                }
            }
        });



    }

    public DayWeatherListRequest setSchedule(MutableLiveData<Resource<WeatherDayInfo>> requestSchedule){
        this.mRequestSchedule=requestSchedule;
        return this;
    }

    public DayWeatherListRequest setWeatherDailyBean(MutableLiveData<List<DailyBean>> mDailyBean){
        this.mDailyBean=mDailyBean;
        return this;
    }
}

package com.example.currentweatherforecast.task;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.BuildConfig;
import com.example.currentweatherforecast.bean.RequestUrl;
import com.example.currentweatherforecast.bean.citybean.CityInfo;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrentCityRequest {
    private static final String TAG="CurrentCityRequest";

    private MutableLiveData<Resource<List<CityInfo>>> mRequestSchedule;
    private MutableLiveData<String> mCityName;
    private MutableLiveData<List<CityInfo>> cityInfo;

    public CurrentCityRequest(
            MutableLiveData<Resource<List<CityInfo>>> requestSchedule,
            MutableLiveData<String> cityName
    ){
        this.mRequestSchedule=requestSchedule;
        this.mCityName=cityName;
    }

    public void run(CoordBean coordinate){
        final List<CityInfo>[] mCityInfo = new List[]{null};

        String requestUrl= RequestUrl.cityInfoUrl
                .replace("{lat}",String.valueOf(coordinate.lat))
                .replace("{lon}",String.valueOf(coordinate.lon))
                .replace("{limit}",String.valueOf(1))
                .replace("{apiKey}", BuildConfig.MY_WEATHER_API_KEY)
                .replace("{lang}","en");
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(requestUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mRequestSchedule.postValue(new Resource<>(
                        mCityInfo[0],
                        RequestProcessType.request_error_city_not_found,
                        e.getMessage()
                ));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                if(response.isSuccessful()){
                    String cityResponse= null;
                    try {
                        cityResponse = response.body().string();
                    } catch (IOException e) {
                        mRequestSchedule.postValue(new Resource<>(
                                mCityInfo[0],
                                RequestProcessType.request_error_network_connection_failed,
                                "网络异常"
                        ));
                        Log.d(TAG, "doInBackground: 网络异常"+e.getMessage());
                    }
                    Gson gson=new Gson();
                    Type cityInfoListType = new TypeToken<List<CityInfo>>() {}.getType();
                    mCityInfo[0] = gson.fromJson(cityResponse, cityInfoListType);
                    if(mCityInfo[0] !=null){
                        if(mCityInfo[0].size()==0){
                            mCityName.postValue("ocean");
                        }
                        else if(mCityInfo[0].get(0).name!=null) {
                            Log.d(TAG, "doInBackground: mCityInfo size is "+ mCityInfo[0].size());
                            mCityName.postValue(mCityInfo[0].get(0).name);
                            Log.d(TAG, "doInBackground: cityName is: "+ mCityInfo[0].get(0).name);
                            Log.d(TAG, "doInBackground: mCityName is null? " + (mCityName == null));
                        }
                        if(cityInfo!=null){
                            cityInfo.postValue(mCityInfo[0]);
                        }
                        cityInfo.postValue(mCityInfo[0]);
                        mRequestSchedule.postValue(new Resource<>(
                                mCityInfo[0],
                                RequestProcessType.request_success,
                                "成功"
                        ));
                    }
                    else{
                        mRequestSchedule.postValue(new Resource<>(
                                mCityInfo[0],
                                RequestProcessType.request_error_city_not_found,
                                "无法获取该城市的名称"
                        ));
                    }
                }
            }
        });
    }

    public CurrentCityRequest setCityInfo(MutableLiveData<List<CityInfo>> cityInfo){
        this.cityInfo=cityInfo;
        return this;
    }
}

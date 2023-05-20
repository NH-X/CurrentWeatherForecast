package com.example.currentweatherforecast.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.BuildConfig;
import com.example.currentweatherforecast.bean.RequestUrl;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.HourlyBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.CurrentBean;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HourWeatherListRequestTask extends AsyncTask<CoordBean,Void,Void> {
    private static final String TAG="HourWeatherListRequestTask";

    private MutableLiveData<Resource<WeatherHourInfo>> mRequestSchedule;
    private WeatherHourInfo hourWeatherData;
    private MutableLiveData<List<HourlyBean>> mHourlyBean;
    private MutableLiveData<CurrentBean> mCurrentBean;

    public HourWeatherListRequestTask(
            MutableLiveData<Resource<WeatherHourInfo>> requestSchedule) {
        this.mRequestSchedule=requestSchedule;
    }

    @Override
    protected Void doInBackground(CoordBean... coordBeans) {
        CoordBean coordinate=coordBeans[0];
        WeatherHourInfo weatherHourInfo=null;
        BufferedReader reader=null;
        InputStream inputStream=null;
        HttpURLConnection connection=null;

        try {
            String requestUrl = RequestUrl.WeatherHourURL
                    .replace("{lat}", String.valueOf(coordinate.lat))
                    .replace("{lon}", String.valueOf(coordinate.lon))
                    .replace("{apiKey}", BuildConfig.MY_WEATHER_API_KEY)
                    .replace("{lang}", "zh_cn");
            Log.d(TAG, "doInBackground: requestUrl="+requestUrl);
            URL url=new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            Log.d(TAG, "doInBackground: response="+response);
            Gson gson = new Gson();
            weatherHourInfo = gson.fromJson(response.toString(), WeatherHourInfo.class);
            mRequestSchedule.postValue(new Resource<>(
                    weatherHourInfo,
                    RequestProcessType.request_success,
                    "成功"
            ));
            hourWeatherData=weatherHourInfo;
            if(weatherHourInfo!=null){
                if(mHourlyBean!=null){
                    mHourlyBean.postValue(weatherHourInfo.hourly);
                    Log.d(TAG, "doInBackground: mHourlyBean is null? "+(mHourlyBean==null));
                }
                if(mCurrentBean!=null){
                    mCurrentBean.postValue(weatherHourInfo.current);
                    Log.d(TAG, "doInBackground: mCurrentBean is null? "+(mCurrentBean==null));
                }
            }
        } catch (MalformedURLException e) {
            mRequestSchedule.postValue(new Resource<>(
                    null,
                    RequestProcessType.request_error_city_not_found,
                    "未找到该城市的天气信息"
            ));
        } catch (IOException e) {
            mRequestSchedule.postValue(new Resource<>(
                    null,
                    RequestProcessType.request_error_network_connection_failed,
                    "网络异常"
            ));
        }
        finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    mRequestSchedule.postValue(new Resource<>(
                            null,
                            RequestProcessType.request_error_data_processing_failed,
                            "数据处理异常"
                    ));
                }
            }
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    mRequestSchedule.postValue(new Resource<>(
                            null,
                            RequestProcessType.request_error_data_processing_failed,
                            "数据处理异常"
                    ));
                }
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
        Log.d(TAG, "doInBackground: currentBean is null? "+(weatherHourInfo==null?true:weatherHourInfo.current==null));
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        Log.d(TAG, "onPostExecute: run");
    }

    public void setSchedule(MutableLiveData<Resource<WeatherHourInfo>> requestSchedule){
        this.mRequestSchedule=requestSchedule;
    }

    public HourWeatherListRequestTask setHourInfo(WeatherHourInfo weatherHourInfo){
        this.hourWeatherData=weatherHourInfo;
        return this;
    }

    public HourWeatherListRequestTask setHourlyBean(MutableLiveData<List<HourlyBean>> mHourlyBean){
        this.mHourlyBean=mHourlyBean;
        return this;
    }

    public HourWeatherListRequestTask setCurrentBean(MutableLiveData<CurrentBean> mCurrentBean){
        this.mCurrentBean=mCurrentBean;
        return this;
    }
}

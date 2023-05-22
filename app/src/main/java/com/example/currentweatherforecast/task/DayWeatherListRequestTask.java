package com.example.currentweatherforecast.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.BuildConfig;
import com.example.currentweatherforecast.bean.RequestUrl;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo.DailyBean;
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
import java.util.Objects;

public class DayWeatherListRequestTask extends AsyncTask<CoordBean,Void, Void> {
    private static final String TAG="DayWeatherListRequestTask";

    private MutableLiveData<Resource<WeatherDayInfo>> mRequestSchedule;
    private WeatherDayInfo dayWeatherData;
    private MutableLiveData<List<DailyBean>> mDailyBean;

    public DayWeatherListRequestTask(
            WeatherDayInfo dayWeatherData
            ,MutableLiveData<Resource<WeatherDayInfo>> requestSchedule){
        this.dayWeatherData =dayWeatherData;
        this.mRequestSchedule=requestSchedule;
    }

    @Override
    protected Void doInBackground(CoordBean... coordBeans) {
        CoordBean coordinate=coordBeans[0];
        WeatherDayInfo weatherDayInfo=null;
        BufferedReader reader=null;
        InputStream inputStream=null;
        HttpURLConnection connection=null;

        try{
            String requestUrl= RequestUrl.WeatherDayUrl
                    .replace("{lat}",String.valueOf(coordinate.lat))
                    .replace("{lon}",String.valueOf(coordinate.lon))
                    .replace("{apiKey}", BuildConfig.MY_WEATHER_API_KEY)
                    .replace("{lang}","en");
            Log.d(TAG, "doInBackground: requestUrl="+requestUrl);
            URL url=new URL(requestUrl);
            connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            inputStream=connection.getInputStream();
            reader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response=new StringBuilder();
            String line;
            while((line= reader.readLine())!=null){
                response.append(line);
            }
            Log.d(TAG, "doInBackground: response="+response);
            Gson gson=new Gson();
            weatherDayInfo=gson.fromJson(response.toString(),WeatherDayInfo.class);
            mRequestSchedule.postValue(new Resource<>(
                    weatherDayInfo,
                    RequestProcessType.request_success,
                    "成功"));
            dayWeatherData=weatherDayInfo;
            if(weatherDayInfo!=null) {
                if (mDailyBean != null && !Objects.equals(mDailyBean.getValue(), weatherDayInfo.daily)) {
                    mDailyBean.postValue( weatherDayInfo.daily);
                    Log.d(TAG, "doInBackground: mDailyBean is null? "+(mDailyBean==null));
                }
            }
        } catch (MalformedURLException e) {
            mRequestSchedule.postValue(new Resource<>(
                    null,
                    RequestProcessType.request_error_city_not_found,
                    "未找到该城市的天气信息"));
        } catch (IOException e) {
            mRequestSchedule.postValue(new Resource<>(
                    null,
                    RequestProcessType.request_error_network_connection_failed,
                    "网络异常"));
        }
        finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    mRequestSchedule.postValue(new Resource<>(
                            null,
                            RequestProcessType.request_error_data_processing_failed,
                            "数据处理异常"));
                }
            }
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    mRequestSchedule.postValue(new Resource<>(
                            null,
                            RequestProcessType.request_error_data_processing_failed,
                            "数据处理异常"));
                }
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
        Log.d(TAG, "doInBackground: weatherDayInfo is null? "+(weatherDayInfo==null));
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        Log.d(TAG, "onPostExecute: run");
    }

    public void setSchedule(MutableLiveData<Resource<WeatherDayInfo>> requestSchedule){
        this.mRequestSchedule=requestSchedule;
    }

    public void setWeatherDailyBean(MutableLiveData<List<DailyBean>> mDailyBean){
        this.mDailyBean=mDailyBean;
    }
}

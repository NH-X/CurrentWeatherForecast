package com.example.currentweatherforecast.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.currentweatherforecast.BuildConfig;
import com.example.currentweatherforecast.bean.RequestUrl;
import com.example.currentweatherforecast.bean.citybean.CityInfo;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class CurrentCityRequestTask extends AsyncTask<CoordBean,Void,Void> {
    private static final String TAG="CurrentCityRequestTask";

    private MutableLiveData<Resource<List<CityInfo>>> mRequestSchedule;
    private MutableLiveData<String> mCityName;
    private MutableLiveData<List<CityInfo>> cityInfo;

    public CurrentCityRequestTask(
            MutableLiveData<Resource<List<CityInfo>>> requestSchedule,
            MutableLiveData<String> cityName){
        this.mRequestSchedule=requestSchedule;
        this.mCityName=cityName;
    }

    @Override
    protected Void doInBackground(CoordBean... coordBeans) {
        CoordBean coordinate=coordBeans[0];
        List<CityInfo> mCityInfo=null;
        BufferedReader reader=null;
        InputStream inputStream=null;
        HttpURLConnection connection=null;

        try{
            String requestUrl= RequestUrl.cityInfoUrl
                    .replace("{lat}",String.valueOf(coordinate.lat))
                    .replace("{lon}",String.valueOf(coordinate.lon))
                    .replace("{limit}",String.valueOf(1))
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
            Type cityInfoListType = new TypeToken<List<CityInfo>>() {}.getType();
            mCityInfo = gson.fromJson(response.toString(), cityInfoListType);
            if(mCityInfo!=null){
                if(mCityInfo.size()==0){
                    mCityName.postValue("ocean");
                }
                else if(mCityInfo.get(0).name!=null) {
                    Log.d(TAG, "doInBackground: mCityInfo size is "+mCityInfo.size());
                    mCityName.postValue(mCityInfo.get(0).name);
                    Log.d(TAG, "doInBackground: cityName is: "+mCityInfo.get(0).name);
                    Log.d(TAG, "doInBackground: mCityName is null? " + (mCityName == null));
                }
                if(cityInfo!=null){
                    cityInfo.postValue(mCityInfo);
                }
                cityInfo.postValue(mCityInfo);
                mRequestSchedule.postValue(new Resource<>(
                        mCityInfo,
                        RequestProcessType.request_success,
                        "成功"
                ));
            }
            else{
                mRequestSchedule.postValue(new Resource<>(
                        mCityInfo,
                        RequestProcessType.request_error_city_not_found,
                        "无法获取该城市的名称"
                ));
            }
        } catch (MalformedURLException e) {
            mRequestSchedule.postValue(new Resource<>(
                    null,
                    RequestProcessType.request_error_city_not_found,
                    "未找到该城市的天气信息"));
            Log.d(TAG, "doInBackground: 未找到该城市的天气信息");
        }catch (FileNotFoundException e){
            Log.d(TAG, "doInBackground: 资源未找到");
        }catch (ProtocolException e){
            Log.d(TAG, "doInBackground: 请求方式出错");
        }catch (SecurityException e){
            Log.d(TAG, "doInBackground: 组织访问");
        }
        catch (IOException e) {
            mRequestSchedule.postValue(new Resource<>(
                    null,
                    RequestProcessType.request_error_network_connection_failed,
                    "网络异常"
            ));
            Log.d(TAG, "doInBackground: 网络异常"+e.getMessage());
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
                    Log.d(TAG, "doInBackground: 数据处理异常");
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
                    Log.d(TAG, "doInBackground: 数据处理异常");
                }
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
        Log.d(TAG, "doInBackground: cityInfo is null? "+(mCityInfo==null));
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        Log.d(TAG, "onPostExecute: run");
    }

    public CurrentCityRequestTask setCityInfo(MutableLiveData<List<CityInfo>> cityInfo){
        this.cityInfo=cityInfo;
        return this;
    }
}

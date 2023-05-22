package com.example.currentweatherforecast;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currentweatherforecast.adapter.WeatherDayListAdapter;
import com.example.currentweatherforecast.adapter.WeatherHourListAdapter;
import com.example.currentweatherforecast.bean.citybean.CityInfo;
import com.example.currentweatherforecast.bean.weatherbean.CoordBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherDayInfo.DailyBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.CurrentBean;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.HourlyBean;
import com.example.currentweatherforecast.respost.RequestProcessType;
import com.example.currentweatherforecast.respost.Resource;
import com.example.currentweatherforecast.util.DateUtil;
import com.example.currentweatherforecast.util.PermissionUtil;
import com.example.currentweatherforecast.viewmodel.MainViewModel;
import com.example.currentweatherforecast.widget.SpacesItemDecoration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private static final String TAG="MainActivity";

    private AdView adView;

    private LocationManager locationManager;

    private MainViewModel mMainViewModel;
    private MainApplication myApp;
    private WeatherDayListAdapter mWeatherDayListAdapter;
    private WeatherHourListAdapter mWeatherHourListAdapter;
    private SpacesItemDecoration decorationDayList;                //item之间的间距
    private SpacesItemDecoration decorationHourList;
    private CoordBean coord;                                        //经纬度

    private Toolbar tl_head;
    private TextView tv_city;
    private TextView tv_date;
    private ImageView icon_weather;
    private LinearLayout ll_snow;
    private LinearLayout ll_current_weather;
    private TextView tv_temperature;
    private TextView tv_description;
    private TextView tv_visibility;
    private TextView tv_atmospheric_pressure;
    private TextView tv_wind_speed;
    private TextView tv_humidity;
    private ImageView icon_snow;
    private TextView tv_snowfall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("E800B0825E3D8B69F8A3E7154C2F4945"))
                        .build());
        //ADF79BFD97AFC7E720A536FDCF9C9B73
        //This is my deviceIds: E800B0825E3D8B69F8A3E7154C2F4945
        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        adView = findViewById(R.id.adView);

        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

        myApp=MainApplication.getInstance();
        shared=getSharedPreferences("coordinates",MODE_PRIVATE);
        coord=new CoordBean();
        getCoordinates();                       //获取上次保存的经纬度信息
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 检查网络定位是否可用
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            // 请求网络定位
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 200)) {
                Log.d(TAG, "onCreate: Network is close");
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        60*1000,
                        0,
                        this);
            }
        } else {
            // 网络定位不可用，您可以在这里处理相应逻辑
            Log.d(TAG, "onCreate: Network is close");
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //打开了gps定位服务
            Log.d(TAG, "onCreate: GPS is open");
            if(PermissionUtil.checkPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION,200)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        60*1000,
                        0,
                        this
                );
            }
            getLocation();
        }
        else{
            //没有gps定位服务
            Log.d(TAG, "onCreate: GPS is close");
        }
        mMainViewModel= new ViewModelProvider(this).get(MainViewModel.class);
        mMainViewModel.init(coord);

        if(!mMainViewModel.getCityResourceSchedule().hasObservers()) {
            mMainViewModel.getCityResourceSchedule().observe(this, new Observer<Resource<List<CityInfo>>>() {
                @Override
                public void onChanged(Resource<List<CityInfo>> cityInfoResource) {
                    if (cityInfoResource.getType() == RequestProcessType.request_error) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                    } else if (cityInfoResource.getType() == RequestProcessType.request_error_data_processing_failed) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_data_processing_failed), Toast.LENGTH_SHORT).show();
                    } else if (cityInfoResource.getType() == RequestProcessType.request_error_city_not_found) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_city_not_found), Toast.LENGTH_SHORT).show();
                    } else if (cityInfoResource.getType() == RequestProcessType.request_error_network_connection_failed) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_network_connection_failed), Toast.LENGTH_SHORT).show();
                    } else if (cityInfoResource.getType() == RequestProcessType.request_success) {
                        if(!mMainViewModel.getCurrentCityName().hasObservers()) {
                            mMainViewModel.getCurrentCityName().observe(MainActivity.this, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    tv_city.setText(s);
                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onChanged: cityName is " + s);

                                    mWeatherDayListAdapter.setCity(s);
                                    mWeatherDayListAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            });
        }
        if(!mMainViewModel.getHourResourceSchedule().hasObservers()) {
            mMainViewModel.getHourResourceSchedule().observe(this, new Observer<Resource<WeatherHourInfo>>() {
                @Override
                public void onChanged(Resource<WeatherHourInfo> weatherHourInfoResource) {
                    if (weatherHourInfoResource.getType() == RequestProcessType.request_error) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                    } else if (weatherHourInfoResource.getType() == RequestProcessType.request_error_data_processing_failed) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_data_processing_failed), Toast.LENGTH_SHORT).show();
                    } else if (weatherHourInfoResource.getType() == RequestProcessType.request_error_city_not_found) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_city_not_found), Toast.LENGTH_SHORT).show();
                    } else if (weatherHourInfoResource.getType() == RequestProcessType.request_error_network_connection_failed) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_network_connection_failed), Toast.LENGTH_SHORT).show();
                    } else if (weatherHourInfoResource.getType() == RequestProcessType.request_success) {
                        if(!mMainViewModel.getWeatherHourlyList().hasObservers()) {
                            mMainViewModel.getWeatherHourlyList().observe(MainActivity.this, new Observer<List<HourlyBean>>() {
                                @Override
                                public void onChanged(List<HourlyBean> weatherHourInfo) {
                                    Log.d(TAG, "onChanged: getWeatherHourlyList is run " + (weatherHourInfo == null ? 0 : weatherHourInfo.size()));
                                    mWeatherHourListAdapter.setHourlyBeanList(weatherHourInfo);
                                    mWeatherHourListAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        if(!mMainViewModel.getmCurrentWeather().hasObservers()) {
                            mMainViewModel.getmCurrentWeather().observe(MainActivity.this, new Observer<CurrentBean>() {
                                @Override
                                public void onChanged(CurrentBean currentBean) {
                                    ll_current_weather.setVisibility(View.GONE);
                                    if (currentBean != null) {
                                        ll_current_weather.setVisibility(View.VISIBLE);
                                        Log.d(TAG, "onChanged: currentBean=" + (currentBean == null));
                                        tv_date.setText(DateUtil.getNowDateTime());
                                        icon_weather.setImageResource(myApp.getWeatherIcon(currentBean.weather.get(0).icon));
                                        tv_temperature.setText(currentBean.temp + "℃");
                                        tv_description.setText(currentBean.weather.get(0).description);
                                        tv_visibility.setText(
                                                String.format("%.3fkm", currentBean.visibility * 1.0 / 1000)
                                        );
                                        tv_atmospheric_pressure.setText(
                                                String.format("%dp", currentBean.pressure)
                                        );
                                        tv_wind_speed.setText(
                                                String.format("%.1fm/s", currentBean.windSpeed)
                                        );
                                        tv_humidity.setText(
                                                String.format("%d%%", currentBean.humidity)
                                        );
                                        if (currentBean.weather.get(0).main.equals("Rain")) {
                                            ll_snow.setVisibility(View.VISIBLE);
                                            icon_snow.setImageResource(R.mipmap.rain);
                                            tv_snowfall.setText(
                                                    String.format("%.2fmm/h", currentBean.rain._$1h)
                                            );
                                        } else if (currentBean.weather.get(0).main.equals("Snow")) {
                                            ll_snow.setVisibility(View.VISIBLE);
                                            icon_snow.setImageResource(R.mipmap.snow);
                                            tv_snowfall.setText(
                                                    String.format("%.2fmm/h", currentBean.snow._$1h)
                                            );
                                        } else {
                                            ll_snow.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        if(!mMainViewModel.getDayResourceSchedule().hasObservers()) {
            mMainViewModel.getDayResourceSchedule().observe(this, new Observer<Resource<WeatherDayInfo>>() {
                @Override
                public void onChanged(Resource<WeatherDayInfo> weatherDayInfoResource) {
                    Log.d(TAG, "onChanged: getDayResourceSchedule is run");
                    if (weatherDayInfoResource.getType() == RequestProcessType.request_error) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                    } else if (weatherDayInfoResource.getType() == RequestProcessType.request_error_data_processing_failed) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_data_processing_failed), Toast.LENGTH_SHORT).show();
                    } else if (weatherDayInfoResource.getType() == RequestProcessType.request_error_city_not_found) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_city_not_found), Toast.LENGTH_SHORT).show();
                    } else if (weatherDayInfoResource.getType() == RequestProcessType.request_error_network_connection_failed) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_network_connection_failed), Toast.LENGTH_SHORT).show();
                    } else if (weatherDayInfoResource.getType() == RequestProcessType.request_success) {
                        //Toast.makeText(MainActivity.this, weatherDayInfoResource.getData().daily.size()+"", Toast.LENGTH_SHORT).show();
                        if (!mMainViewModel.getWeatherDailyList().hasObservers()) {
                            mMainViewModel.getWeatherDailyList().observe(MainActivity.this, new Observer<List<DailyBean>>() {
                                @Override
                                public void onChanged(List<DailyBean> weatherDailyBean) {
                                    Log.d(TAG, "onChanged: getWeatherDailyList is run");
                                    if (weatherDailyBean != null) {
                                        Toast.makeText(MainActivity.this, weatherDailyBean.size() + "", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onChanged: weatherDailyBean size is " + weatherDailyBean.size());
                                        mWeatherDayListAdapter.setDailyBeanList(weatherDailyBean);
                                        mWeatherDayListAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }

        initFindView();
        initHourRecycleView();
        initDayRecycleView();
    }

    private void initFindView(){
        tl_head=findViewById(R.id.tl_head);
        setSupportActionBar(tl_head);
        tv_city=findViewById(R.id.tv_city);
        tv_date=findViewById(R.id.tv_date);
        ll_current_weather=findViewById(R.id.ll_current_weather);
        icon_weather=findViewById(R.id.icon_weather);
        ll_snow=findViewById(R.id.ll_snow);
        tv_temperature=findViewById(R.id.tv_temperature);
        tv_description=findViewById(R.id.tv_description);
        tv_visibility=findViewById(R.id.tv_visibility);
        tv_atmospheric_pressure=findViewById(R.id.tv_atmospheric_pressure);
        tv_wind_speed=findViewById(R.id.tv_wind_speed);
        tv_humidity=findViewById(R.id.tv_humidity);
        icon_snow=findViewById(R.id.icon_snow);
        tv_snowfall=findViewById(R.id.tv_snowfall);
    }

    private void initHourRecycleView() {
        RecyclerView rv_weathers = findViewById(R.id.rv_weathers);
        mWeatherHourListAdapter = new WeatherHourListAdapter(
                this,
                new ArrayList<>());
        rv_weathers.setAdapter(mWeatherHourListAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rv_weathers.setLayoutManager(layoutManager);
        if (decorationHourList != null) {
            rv_weathers.removeItemDecoration(decorationHourList);
        }
        decorationHourList = new SpacesItemDecoration(50);
        rv_weathers.addItemDecoration(decorationHourList);
    }

    private void initDayRecycleView() {
        RecyclerView rv_weather_list = findViewById(R.id.rv_weather_list);
        mWeatherDayListAdapter = new WeatherDayListAdapter(
                this,
                mMainViewModel.getWeatherDailyList().getValue());
        mWeatherDayListAdapter.setCity("");
        rv_weather_list.setAdapter(mWeatherDayListAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        );
        rv_weather_list.setLayoutManager(layoutManager);
        if (decorationDayList != null) {
            rv_weather_list.removeItemDecoration(decorationDayList);
        }
        decorationDayList = new SpacesItemDecoration(25);
        rv_weather_list.addItemDecoration(decorationDayList);
    }


    SharedPreferences shared;

    //从共享数据中获取上次的经纬度
    private void getCoordinates(){
        coord.lon=shared.getFloat("lon",0.0f);
        coord.lat=shared.getFloat("lat",0.0f);
        Log.d(TAG, String.format("getCoordinates: lon is %f lat is %f",coord.lon,coord.lat));
    }

    //往共享数据存储当前的经纬度
    private void setCoordinates(){
        SharedPreferences.Editor editor=shared.edit();
        editor.putFloat("lon", (float) coord.lon);
        editor.putFloat("lat",(float) coord.lat);
        editor.commit();
        Log.d(TAG, String.format("setCoordinates: lon is %f lat is %f",coord.lon,coord.lat));
    }

    private void getLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers=locationManager.getProviders(true);
        for (String provider:providers){
            Log.d(TAG, "getLocation: "+provider);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude(); // 获取纬度
        double longitude = location.getLongitude(); // 获取经度
        // TODO: 处理获取到的经纬度信息
        coord.lon=longitude;
        coord.lat=latitude;
        Log.d(TAG, String.format("onLocationChanged: lat is %f, lon is %f",latitude,longitude));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: provider = " + provider + ", status = " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
        mMainViewModel.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        mMainViewModel.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Before calling onDestroy()");
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
        Log.d(TAG, "onDestroy: being");
        setCoordinates();           //保存当前的经纬度
        locationManager.removeUpdates(this);
        mMainViewModel=null;
        Log.d(TAG, "onDestroy: end");
    }

    /** Override the default implementation when the user presses the back key. */
    @Override
    @SuppressWarnings("MissingSuperCall")
    public void onBackPressed() {
        // Move the task containing the MainActivity to the back of the activity stack, instead of
        // destroying it. Therefore, MainActivity will be shown when the user switches back to the app.
        moveTaskToBack(true);
    }
}
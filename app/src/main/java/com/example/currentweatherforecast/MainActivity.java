package com.example.currentweatherforecast;


import android.content.Context;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";

    private AdView adView;

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
        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        adView = findViewById(R.id.adView);

        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

        myApp=MainApplication.getInstance();
        coord=new CoordBean();
        getLocation();
        coord.lat=20.03;
        coord.lon=110.32;
        mMainViewModel= new ViewModelProvider(this).get(MainViewModel.class);
        mMainViewModel.init(coord);
        mMainViewModel.getHourResourceSchedule().observe(this, new Observer<Resource<WeatherHourInfo>>() {
            @Override
            public void onChanged(Resource<WeatherHourInfo> weatherHourInfoResource) {
                if(weatherHourInfoResource.getType()== RequestProcessType.request_error){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                }
                else if(weatherHourInfoResource.getType()== RequestProcessType.request_error_data_processing_failed){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_data_processing_failed), Toast.LENGTH_SHORT).show();
                }
                else if(weatherHourInfoResource.getType()== RequestProcessType.request_error_city_not_found){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_city_not_found), Toast.LENGTH_SHORT).show();
                }
                else if(weatherHourInfoResource.getType()== RequestProcessType.request_error_network_connection_failed){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_network_connection_failed), Toast.LENGTH_SHORT).show();
                }
                else if(weatherHourInfoResource.getType()==RequestProcessType.request_success){
                    mMainViewModel.getWeatherHourlyList().observe(MainActivity.this, new Observer<List<HourlyBean>>() {
                        @Override
                        public void onChanged(List<HourlyBean> weatherHourInfo) {
                            Log.d(TAG, "onChanged: getWeatherHourlyList is run "+(weatherHourInfo==null?0:weatherHourInfo.size()));
                            mWeatherHourListAdapter.setHourlyBeanList(weatherHourInfo);
                            mWeatherHourListAdapter.notifyDataSetChanged();
                        }
                    });
                    mMainViewModel.getCurrentWeather().observe(MainActivity.this, new Observer<CurrentBean>() {
                        @Override
                        public void onChanged(CurrentBean currentBean) {
                            tv_city.setText(String.format(
                                    "%.2f+%.2f", coord.lon, coord.lat
                            ));
                            if (currentBean != null) {
                                Log.d(TAG,"onChanged: currentBean=" + (currentBean == null));
                                tv_date.setText(DateUtil.getNowDateTime());
                                icon_weather.setImageResource(myApp.getWeatherIcon(currentBean.weather.get(0).icon));
                                tv_temperature.setText(currentBean.temp + "℃");
                                tv_description.setText(currentBean.weather.get(0).description);
                                tv_visibility.setText(
                                        String.format("%.3fkm", currentBean.visibility * 1.0 / 1000)
                                );
                                tv_atmospheric_pressure.setText(
                                        String.format("%dp" , currentBean.pressure)
                                );
                                tv_wind_speed.setText(
                                        String.format("%.1fm/s" , currentBean.windSpeed)
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
        });
        mMainViewModel.getDayResourceSchedule().observe(this, new Observer<Resource<WeatherDayInfo>>() {
            @Override
            public void onChanged(Resource<WeatherDayInfo> weatherDayInfoResource) {
                if(weatherDayInfoResource.getType()== RequestProcessType.request_error){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error), Toast.LENGTH_SHORT).show();
                }
                else if(weatherDayInfoResource.getType()== RequestProcessType.request_error_data_processing_failed){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_data_processing_failed), Toast.LENGTH_SHORT).show();
                }
                else if(weatherDayInfoResource.getType()== RequestProcessType.request_error_city_not_found){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_city_not_found), Toast.LENGTH_SHORT).show();
                }
                else if(weatherDayInfoResource.getType()== RequestProcessType.request_error_network_connection_failed){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.request_error_network_connection_failed), Toast.LENGTH_SHORT).show();
                }
                else if(weatherDayInfoResource.getType()==RequestProcessType.request_success){
                    //Toast.makeText(MainActivity.this, weatherDayInfoResource.getData().daily.size()+"", Toast.LENGTH_SHORT).show();
                    mMainViewModel.getWeatherDailyList().observe(MainActivity.this, new Observer<List<DailyBean>>() {
                        @Override
                        public void onChanged(List<DailyBean> weatherDailyBean) {
                            if(weatherDailyBean!=null) {
                                Toast.makeText(MainActivity.this, weatherDailyBean.size() + "", Toast.LENGTH_SHORT).show();
                                mWeatherDayListAdapter.setCity(coord.lon + "+" + coord.lat);
                                mWeatherDayListAdapter.setDailyBeanList(weatherDailyBean);
                                mWeatherDayListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });

        initFindView();
        initHourRecycleView();
        initDayRecycleView();
    }

    private void initFindView(){
        tl_head=findViewById(R.id.tl_head);
        setSupportActionBar(tl_head);
        tv_city=findViewById(R.id.tv_city);
        tv_date=findViewById(R.id.tv_date);
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
        decorationDayList = new SpacesItemDecoration(50);
        rv_weather_list.addItemDecoration(decorationDayList);
    }

    private void getLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(PermissionUtil.checkPermission(this,LocationManager.GPS_PROVIDER,0)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude(); // 获取纬度
            double longitude = location.getLongitude(); // 获取经度
            // TODO: 处理获取到的经纬度信息
            coord.lon=longitude;
            coord.lat=latitude;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };


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
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
        mMainViewModel=null;
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
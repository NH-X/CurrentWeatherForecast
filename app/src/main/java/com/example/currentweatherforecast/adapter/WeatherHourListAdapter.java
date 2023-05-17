package com.example.currentweatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.currentweatherforecast.MainApplication;
import com.example.currentweatherforecast.R;
import com.example.currentweatherforecast.bean.weatherbean.WeatherHourInfo.HourlyBean;
import com.example.currentweatherforecast.util.DateUtil;

import java.util.List;

public class WeatherHourListAdapter extends Adapter<ViewHolder> {
    private static final String TAG="WeatherHourListAdapter";
    private MainApplication myApp;
    private Context context;
    private List<HourlyBean> hourlyBeanList;

    public WeatherHourListAdapter(Context context,List<HourlyBean> hourlyBeanList){
        myApp=MainApplication.getInstance();
        this.context=context;
        this.hourlyBeanList=hourlyBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_hour_weatherlist,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyBean info=hourlyBeanList.get(position);
        ItemHolder itemHolder= (ItemHolder) holder;
        itemHolder.tv_time.setText(DateUtil.getTime(info.dt));
        itemHolder.icon_weather.setImageResource(myApp.getWeatherIcon(info.weather.get(0).icon));
        itemHolder.tv_temperature.setText(info.temp+"℃");
    }

    //只需要获取前24
    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: hourlyBeanList size is "+hourlyBeanList.size());
        return hourlyBeanList==null?0:hourlyBeanList.size()/2;
    }

    public void setHourlyBeanList(List<HourlyBean> hourlyBeanList) {
        this.hourlyBeanList = hourlyBeanList;
    }

    private class ItemHolder extends ViewHolder{
        public TextView tv_time;
        public ImageView icon_weather;
        public TextView tv_temperature;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tv_time=itemView.findViewById(R.id.tv_time);
            icon_weather=itemView.findViewById(R.id.icon_weather);
            tv_temperature=itemView.findViewById(R.id.tv_temperature);
        }
    }
}

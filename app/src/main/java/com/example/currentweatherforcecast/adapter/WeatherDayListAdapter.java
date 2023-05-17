package com.example.currentweatherforcecast.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currentweatherforcecast.MainApplication;
import com.example.currentweatherforcecast.bean.weatherbean.WeatherDayInfo;
import com.example.currentweatherforcecast.bean.weatherbean.WeatherDayInfo.DailyBean;
import com.example.currentweatherforcecast.R;
import com.example.currentweatherforcecast.util.DateUtil;
import com.example.currentweatherforcecast.widget.RecyclerExtras;

import java.util.List;

public class WeatherDayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MainApplication myApp;
    private Context context;
    private List<DailyBean> weatherList;
    private String city;

    public WeatherDayListAdapter(Context context, List<DailyBean> weatherList){
        this.context=context;
        this.weatherList=weatherList;
        this.myApp=MainApplication.getInstance();
    }

    //创建列表项的视图持有者
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_day_weatherlist,parent,false);
        return new ItemHolder(v);
    }

    //绑定列表项视图持有者
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemHolder itemHolder= (ItemHolder) holder;
        itemHolder.tv_city.setText(city);
        itemHolder.tv_date.setText(DateUtil.getDate(weatherList.get(position).dt));
        itemHolder.icon_weather.setImageResource(myApp.getWeatherIcon(weatherList.get(position).weather.get(0).icon));
        itemHolder.position=position;
        itemHolder.ll_item.setTag(itemHolder);
    }

    @Override
    public int getItemCount() {
        return weatherList==null?0:weatherList.size();
    }

    public void setCity(String city){
        this.city=city;
    }

    public void setDailyBeanList(List<DailyBean> weatherDailyBeanInfo) {
        this.weatherList = weatherDailyBeanInfo;
    }

    private class ItemHolder extends RecyclerView.ViewHolder{
        public LinearLayout ll_item;
        public TextView tv_city;
        public TextView tv_date;
        public ImageView icon_weather;

        public int position;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ll_item=itemView.findViewById(R.id.ll_item);
            tv_city=itemView.findViewById(R.id.tv_city);
            tv_date=itemView.findViewById(R.id.tv_date);
            icon_weather=itemView.findViewById(R.id.icon_weather);
        }
    }
}

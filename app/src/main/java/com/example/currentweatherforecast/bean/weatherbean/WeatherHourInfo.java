package com.example.currentweatherforecast.bean.weatherbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WeatherHourInfo implements Serializable {

    @SerializedName("lat")
    public Double lat;
    @SerializedName("lon")
    public Double lon;
    @SerializedName("timezone")
    public String timezone;
    @SerializedName("timezone_offset")
    public Integer timezoneOffset;
    @SerializedName("current")
    public CurrentBean current;
    @SerializedName("rain")
    public RainBean rain;
    @SerializedName("snow")
    public SnowBean snow;
    @SerializedName("minutely")
    public List<MinutelyBean> minutely;
    @SerializedName("hourly")
    public List<HourlyBean> hourly;

    @NoArgsConstructor
    @Data
    public static class CurrentBean {
        @SerializedName("dt")
        public Integer dt;
        @SerializedName("sunrise")
        public Integer sunrise;
        @SerializedName("sunset")
        public Integer sunset;
        @SerializedName("temp")
        public Double temp;
        @SerializedName("feels_like")
        public Double feelsLike;
        @SerializedName("pressure")
        public Integer pressure;
        @SerializedName("humidity")
        public Integer humidity;
        @SerializedName("dew_point")
        public Double dewPoint;
        @SerializedName("uvi")
        public Double uvi;
        @SerializedName("clouds")
        public Integer clouds;
        @SerializedName("visibility")
        public Integer visibility;
        @SerializedName("rain")
        public RainBean rain;
        @SerializedName("snow")
        public SnowBean snow;
        @SerializedName("wind_speed")
        public Double windSpeed;
        @SerializedName("wind_deg")
        public Integer windDeg;
        @SerializedName("wind_gust")
        public Double windGust;
        @SerializedName("weather")
        public List<WeatherBean> weather;
    }

    @NoArgsConstructor
    @Data
    public static class MinutelyBean {
        @SerializedName("dt")
        public Integer dt;
        @SerializedName("precipitation")
        public Double precipitation;
    }

    @NoArgsConstructor
    @Data
    public static class HourlyBean {
        @SerializedName("dt")
        public Integer dt;
        @SerializedName("temp")
        public Double temp;
        @SerializedName("feels_like")
        public Double feelsLike;
        @SerializedName("pressure")
        public Integer pressure;
        @SerializedName("humidity")
        public Integer humidity;
        @SerializedName("dew_point")
        public Double dewPoint;
        @SerializedName("uvi")
        public Double uvi;
        @SerializedName("clouds")
        public Integer clouds;
        @SerializedName("visibility")
        public Integer visibility;
        @SerializedName("wind_speed")
        public Double windSpeed;
        @SerializedName("wind_deg")
        public Integer windDeg;
        @SerializedName("wind_gust")
        public Double windGust;
        @SerializedName("weather")
        public List<WeatherBean> weather;
        @SerializedName("pop")
        public Double pop;
        @SerializedName("rain")
        public RainBean rain;
    }
}
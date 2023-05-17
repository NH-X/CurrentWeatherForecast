package com.example.currentweatherforecast.bean.weatherbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WeatherDayInfo implements Serializable {
    @SerializedName("lat")
    public Double lat;
    @SerializedName("lon")
    public Double lon;
    @SerializedName("timezone")
    public String timezone;
    @SerializedName("timezone_offset")
    public Integer timezoneOffset;
    @SerializedName("daily")
    public List<DailyBean> daily;

    @NoArgsConstructor
    @Data
    public static class DailyBean {
        @SerializedName("dt")
        public Integer dt;
        @SerializedName("sunrise")
        public Integer sunrise;
        @SerializedName("sunset")
        public Integer sunset;
        @SerializedName("moonrise")
        public Integer moonrise;
        @SerializedName("moonset")
        public Integer moonset;
        @SerializedName("moon_phase")
        public Double moonPhase;
        @SerializedName("temp")
        public TempBean temp;
        @SerializedName("feels_like")
        public FeelsLikeBean feelsLike;
        @SerializedName("pressure")
        public Integer pressure;
        @SerializedName("humidity")
        public Integer humidity;
        @SerializedName("dew_point")
        public Double dewPoint;
        @SerializedName("wind_speed")
        public Double windSpeed;
        @SerializedName("wind_deg")
        public Integer windDeg;
        @SerializedName("wind_gust")
        public Double windGust;
        @SerializedName("weather")
        public List<WeatherBean> weather;
        @SerializedName("clouds")
        public Integer clouds;
        @SerializedName("pop")
        public Double pop;
        @SerializedName("uvi")
        public Double uvi;

        @NoArgsConstructor
        @Data
        public static class TempBean {
            @SerializedName("day")
            public Double day;
            @SerializedName("min")
            public Double min;
            @SerializedName("max")
            public Double max;
            @SerializedName("night")
            public Double night;
            @SerializedName("eve")
            public Double eve;
            @SerializedName("morn")
            public Double morn;
        }

        @NoArgsConstructor
        @Data
        public static class FeelsLikeBean {
            @SerializedName("day")
            public Double day;
            @SerializedName("night")
            public Double night;
            @SerializedName("eve")
            public Double eve;
            @SerializedName("morn")
            public Double morn;
        }
    }
}
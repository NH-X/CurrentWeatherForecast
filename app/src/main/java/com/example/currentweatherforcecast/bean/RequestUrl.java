package com.example.currentweatherforcecast.bean;

public class RequestUrl {
    public static String WeatherUrl="https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={exclude}&units=metric&appid={apiKey}&lang={lang}";
    public static String WeatherHourURL="https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude=daily&units=metric&appid={apiKey}&lang={lang}";
    public static String WeatherDayUrl="https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude=minutely,hourly&units=metric&appid={apiKey}&lang={lang}";

    public static String cityInfoUrl="http://api.geonames.org/search?username=ahai&country={country}&type=json";
}

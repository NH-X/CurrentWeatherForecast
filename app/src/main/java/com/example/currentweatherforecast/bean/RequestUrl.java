package com.example.currentweatherforecast.bean;

public class RequestUrl {
    public static String WeatherUrl="https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={exclude}&units=metric&appid={apiKey}&lang={lang}";
    public static String WeatherHourURL="https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude=daily&units=metric&appid={apiKey}&lang={lang}";
    public static String WeatherDayUrl="https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude=minutely,hourly&units=metric&appid={apiKey}&lang={lang}";

    public static String cityInfoUrl="https://api.openweathermap.org/geo/1.0/reverse?lat={lat}&lon={lon}&limit={limit}&appid={apiKey}&lang={lang}";
}

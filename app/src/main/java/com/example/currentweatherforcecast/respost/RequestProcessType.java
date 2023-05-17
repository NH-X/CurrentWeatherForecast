package com.example.currentweatherforcecast.respost;

public enum RequestProcessType {
    request_executing,                          //正在执行
    request_error,                              //发送请求失败
    request_error_network_connection_failed,    //网络异常
    request_error_city_not_found,               //未找到该城市的天气信息
    request_error_data_processing_failed,       //数据处理异常
    request_success                             //成功
}

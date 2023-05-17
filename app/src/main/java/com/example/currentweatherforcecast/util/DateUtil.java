package com.example.currentweatherforcecast.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * @return yyyy-MM-dd
     */
    public static String getNowDate(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }

    /**
     * @return yyyy-MM-dd HH-mm
     */
    public static String getNowDateTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date());
    }

    public static String getCustomizeNowDateTime(String str){
        return new SimpleDateFormat(str).format(new Date());
    }

    /**
     * @param time
     * @return yyyy-MM-dd
     */
    public static String getDate(long time){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time*1000));
    }

    /**
     * @param time
     * @return yyyy-MM-dd HH-mm
     */
    public static String getDateTime(long time){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date(time*1000));
    }

    /**
     * @param time
     * @return HH-mm
     */
    public static String getTime(long time){
        SimpleDateFormat format=new SimpleDateFormat("HH:mm");
        return format.format(new Date(time*1000));
    }
}

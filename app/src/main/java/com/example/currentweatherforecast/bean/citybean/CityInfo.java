package com.example.currentweatherforecast.bean.citybean;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CityInfo {

    @SerializedName("name")
    public String name;
    @SerializedName("local_names")
    public LocalNamesBean localNames;
    @SerializedName("lat")
    public Double lat;
    @SerializedName("lon")
    public Double lon;
    @SerializedName("country")
    public String country;
    @SerializedName("state")
    public String state;

    @NoArgsConstructor
    @Data
    public static class LocalNamesBean {
        @SerializedName("fi")
        public String fi;
        @SerializedName("fa")
        public String fa;
        @SerializedName("bo")
        public String bo;
        @SerializedName("nl")
        public String nl;
        @SerializedName("fr")
        public String fr;
        @SerializedName("mg")
        public String mg;
        @SerializedName("be")
        public String be;
        @SerializedName("la")
        public String la;
        @SerializedName("tl")
        public String tl;
        @SerializedName("feature_name")
        public String featureName;
        @SerializedName("nn")
        public String nn;
        @SerializedName("ug")
        public String ug;
        @SerializedName("ru")
        public String ru;
        @SerializedName("ja")
        public String ja;
        @SerializedName("bg")
        public String bg;
        @SerializedName("za")
        public String za;
        @SerializedName("en")
        public String en;
        @SerializedName("ki")
        public String ki;
        @SerializedName("eo")
        public String eo;
        @SerializedName("eu")
        public String eu;
        @SerializedName("id")
        public String id;
        @SerializedName("ascii")
        public String ascii;
        @SerializedName("hy")
        public String hy;
        @SerializedName("ar")
        public String ar;
        @SerializedName("cs")
        public String cs;
        @SerializedName("hi")
        public String hi;
        @SerializedName("my")
        public String my;
        @SerializedName("kn")
        public String kn;
        @SerializedName("ku")
        public String ku;
        @SerializedName("hr")
        public String hr;
        @SerializedName("hu")
        public String hu;
        @SerializedName("pt")
        public String pt;
        @SerializedName("es")
        public String es;
        @SerializedName("ca")
        public String ca;
        @SerializedName("el")
        public String el;
        @SerializedName("ur")
        public String ur;
        @SerializedName("pl")
        public String pl;
        @SerializedName("it")
        public String it;
        @SerializedName("os")
        public String os;
        @SerializedName("br")
        public String br;
        @SerializedName("ta")
        public String ta;
        @SerializedName("no")
        public String no;
        @SerializedName("tr")
        public String tr;
        @SerializedName("lv")
        public String lv;
        @SerializedName("uk")
        public String uk;
        @SerializedName("vi")
        public String vi;
        @SerializedName("et")
        public String et;
        @SerializedName("zh")
        public String zh;
        @SerializedName("sv")
        public String sv;
        @SerializedName("cy")
        public String cy;
        @SerializedName("sw")
        public String sw;
        @SerializedName("da")
        public String da;
        @SerializedName("lt")
        public String lt;
        @SerializedName("af")
        public String af;
        @SerializedName("sh")
        public String sh;
        @SerializedName("de")
        public String de;
        @SerializedName("sr")
        public String sr;
        @SerializedName("ko")
        public String ko;
        @SerializedName("qu")
        public String qu;
    }
}
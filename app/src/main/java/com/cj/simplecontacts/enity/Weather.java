package com.cj.simplecontacts.enity;

import java.util.List;

/**
 * Created by chenjun on 17-8-23.
 * 包括7-10天预报、实况天气、每小时天气、灾害预警、生活指数、空气质量，一次获取足量数据
 */

public class Weather {
    public Aqi aqi;//AQI
    public Basic basic;
    public List<DialyForecast> daily_forecast;
    public List<HourlyForcast> hourly_forecast;
    public Now now;
    public String status;//状态码
    public Suggestion suggestion;

    public Aqi getAqi() {
        return aqi;
    }

    public Basic getBasic() {
        return basic;
    }

    public Now getNow() {
        return now;
    }

    public String getStatus() {
        return status;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public List<DialyForecast> getDaily_forecast() {
        return daily_forecast;
    }

    public List<HourlyForcast> getHourly_forecast() {
        return hourly_forecast;
    }
}

/**************************************/

/****
 *在接口返回的数据中，status字段是表明数据的状态，目前有以下几种状态：
 * ok	数据正常
 * invalid key	错误的key
 * unknown city	未知或错误城市/地区
 * no data for this location	该城市/地区没有你所请求的数据
 * no more requests	超过访问次数
 * param invalid	参数错误
 * too fast	超过限定的QPM
 * anr	无响应或超时
 * permission denied	无访问权限，如免费key强制获取付费数据或获取未购买的付费数据
 *
 *
 */

package com.cj.simplecontacts.enity;

/**
 * Created by chenjun on 17-8-23.
 * 空气质量实况和预报
 *
 */

public class Aqi {
    public City city;
    public class City
    {
        public String aqi;//AQI
        private String co;//CO
        private String no2;//NO2
        private String o3;//O3
        private String pm10;//PM10
        public String pm25;//PM2.5
        private String qlty;//空气质量
        private String so2;//SO2

        public String getAqi() {
            return aqi;
        }

        public String getCo() {
            return co;
        }

        public String getNo2() {
            return no2;
        }

        public String getO3() {
            return o3;
        }

        public String getPm10() {
            return pm10;
        }

        public String getPm25() {
            return pm25;
        }

        public String getQlty() {
            return qlty;
        }

        public String getSo2() {
            return so2;
        }
    }

    public City getCity() {
        return city;
    }
}
/**********************************/

/**
 * 最长7天全国3181个城市空气质量预报数据
 * "aqi": {
         "city": {
             "aqi": "60",
             "co": "0",
             "no2": "14",
             "o3": "95",
             "pm10": "67",
             "pm25": "15",
             "qlty": "良",  //共六个级别，分别：优，良，轻度污染，中度污染，重度污染，严重污染
             "so2": "10"
             }
         },
 */

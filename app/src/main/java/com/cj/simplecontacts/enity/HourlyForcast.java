package com.cj.simplecontacts.enity;

/**
 * Created by chenjun on 17-8-23.
 * 未来每小时预报
 */

public class HourlyForcast {
    public String date;
    public String hum;
    public String pop;
    public String pres;
    public String tmp;
    public Wind wind;
    public class Wind
    {
        public String deg;
        public String dir;
        public String sc;
        public String spd;

        public String getDeg() {
            return deg;
        }

        public String getDir() {
            return dir;
        }

        public String getSc() {
            return sc;
        }

        public String getSpd() {
            return spd;
        }
    }

    public Cond cond;
    public class Cond
    {
        public String code;//天气状况代码
        public String txt;//数据详情

        public String getCode() {
            return code;
        }

        public String getTxt() {
            return txt;
        }
    }

    public String getDate() {
        return date;
    }

    public String getHum() {
        return hum;
    }

    public String getPop() {
        return pop;
    }

    public String getPres() {
        return pres;
    }

    public String getTmp() {
        return tmp;
    }

    public Wind getWind() {
        return wind;
    }

    public Cond getCond() {
        return cond;
    }
}
/*************************************/

/**
 * 未来1-10天每一小时天气预报数据
 *  "hourly_forecast": [
 {
 "cond": {
     "code": "100",
     "txt": "晴"
     },
 "date": "2016-08-30 12:00",
 "hum": "47",
 "pop": "0",
 "pres": "1006",
 "tmp": "29",
 "wind": {
     "deg": "335",
     "dir": "西北风",
     "sc": "4-5",
     "spd": "36"
     }
 }
 ],
 */
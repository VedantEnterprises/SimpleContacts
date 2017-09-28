package com.cj.simplecontacts.enity;

/**
 * Created by chenjun on 17-8-23.
 * 实况天气
 */

public class Now {
    public Cond cond;//天气状况
    public class Cond
    {
        public String code;
        public String txt;

        public String getCode() {
            return code;
        }

        public String getTxt() {
            return txt;
        }
    }
    public String fl;//体感温度
    public String hum;//相对湿度
    public String pcpn;//降水量
    public String pres;//气压
    public String tmp;//温度
    public String vis;//能见度
    public Wind wind;
    public class Wind
    {
        public String deg;//风向（360度）
        public String dir;//风向
        public String sc;//风力等级
        public String spd;//风速

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

    public Cond getCond() {
        return cond;
    }

    public String getFl() {
        return fl;
    }

    public String getHum() {
        return hum;
    }

    public String getPcpn() {
        return pcpn;
    }

    public String getPres() {
        return pres;
    }

    public String getTmp() {
        return tmp;
    }

    public String getVis() {
        return vis;
    }

    public Wind getWind() {
        return wind;
    }
}

/********************************/
/*
实况天气即为当前时间的天气状况、温度、风向风力等等
包括多种气象指数的实况天气，每小时更新
 *"now": {
                "cond": {
                    "code": "100",
                    "txt": "晴"
                },
                "fl": "28",
                "hum": "41",
                "pcpn": "0",
                "pres": "1005",
                "tmp": "26",
                "vis": "10",
                "wind": {
                    "deg": "330",
                    "dir": "西北风",
                    "sc": "6-7",
                    "spd": "34"
                }
            },
 */

package com.cj.simplecontacts.enity;

/**
 * Created by chenjun on 17-8-23.
 * 天气预报
 */

public class DialyForecast {
    public Astro astro;//天文指数
    public class Astro
    {
        public String mr;//月升时间
        public String ms;//月落时间
        public String sr;//日出时间
        public String ss;//日落时间

        public String getMr() {
            return mr;
        }

        public String getMs() {
            return ms;
        }

        public String getSr() {
            return sr;
        }

        public String getSs() {
            return ss;
        }
    }
    public Cond cond;//天气状况
    public class Cond
    {
        public String code_d;//白天天气状况代码
        public String code_n;//夜间天气状况代码
        public String txt_d;//白天天气状况描述
        public String txt_n;//夜间天气状况描述

        public String getCode_d() {
            return code_d;
        }

        public String getCode_n() {
            return code_n;
        }

        public String getTxt_d() {
            return txt_d;
        }

        public String getTxt_n() {
            return txt_n;
        }
    }
    public String date;//日期
    public String hum;//相对湿度
    public String pcpn;//降水量
    public String pop;//降水概率
    public String pres;//气压
    public Tmp tmp;//温度
    public class Tmp
    {
        public String max;//最高温度
        public String min;//最低温度

        public String getMax() {
            return max;
        }

        public String getMin() {
            return min;
        }
    }
    public String vis;//能见度
    public Wind wind;//风力情况
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

    public Astro getAstro() {
        return astro;
    }

    public Cond getCond() {
        return cond;
    }

    public String getDate() {
        return date;
    }

    public String getHum() {
        return hum;
    }

    public String getPcpn() {
        return pcpn;
    }

    public String getPop() {
        return pop;
    }

    public String getPres() {
        return pres;
    }

    public Tmp getTmp() {
        return tmp;
    }

    public String getVis() {
        return vis;
    }

    public Wind getWind() {
        return wind;
    }
}

/**********************************/

/**
 * "daily_forecast": [
         {
         "astro": {
                 "mr": "03:09",
                 "ms": "17:06",
                 "sr": "05:28",
                 "ss": "18:29"
                 },
         "cond": {
             "code_d": "100",
             "code_n": "100",
             "txt_d": "晴",
             "txt_n": "晴"
             },
         "date": "2016-08-30",
         "hum": "45",
         "pcpn": "0.0",
         "pop": "8",
         "pres": "1005",
         "tmp": {
             "max": "29",
             "min": "22"
             },
         "vis": "10",
         "wind": {
             "deg": "339",
             "dir": "北风",
             "sc": "4-5",
             "spd": "24"
             }
         }
         ],
 */

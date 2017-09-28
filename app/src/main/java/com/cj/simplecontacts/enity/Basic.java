package com.cj.simplecontacts.enity;

/**
 * Created by chenjun on 17-8-23.
 * 基本信息
 */

public class Basic {
    public String city;
    private String cnty;//国家
    public String id;//城市ID
    public String lat;//纬度
    public String lon;//经度
    public String prov;

    public Update update;//更新时间
    public class Update
    {
        public String loc;//当地时间
        public String utc;//UTC时间

        public String getLoc() {
            return loc;
        }

        public String getUtc() {
            return utc;
        }
    }

    public String getCity() {
        return city;
    }

    public String getCnty() {
        return cnty;
    }

    public String getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getProv() {
        return prov;
    }

    public Update getUpdate() {
        return update;
    }
}

/***************************/
/**
 *  "basic": {
             "city": "青岛",
             "cnty": "中国",
             "id": "CN101120201",
             "lat": "36.088000",
             "lon": "120.343000",
             "prov": "山东"  //城市所属省份（仅限国内城市）
             "update": {
                     "loc": "2016-08-30 11:52",
                     "utc": "2016-08-30 03:52"
                     }
             },
 */
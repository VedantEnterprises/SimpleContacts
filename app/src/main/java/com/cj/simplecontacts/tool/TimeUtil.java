package com.cj.simplecontacts.tool;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenjun on 17-7-27.
 */

public class TimeUtil {

    public static  String timeCompare(long time){
        //格式化时间
        SimpleDateFormat CurrentTime= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat CurrentTime1= new SimpleDateFormat("HH:mm");
        SimpleDateFormat CurrentTime2= new SimpleDateFormat("MM/dd HH:mm");

        String data = "";
        try {
            Date beginTime=new Date(time);
            Date endTime=new Date();
            //判断是否大于两天
            long surplus = (endTime.getTime() - beginTime.getTime())/(24*60*60*1000);
            if(surplus < 1) {
                data = CurrentTime1.format(beginTime);
            }else  if(surplus == 1){
                data = "昨天 "+CurrentTime1.format(beginTime);
            }else  if(surplus == 2){
                data = "前天 "+CurrentTime1.format(beginTime);
            }else if(surplus < 365){
                data = CurrentTime2.format(beginTime);
            }else{
                data = CurrentTime.format(beginTime);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
}

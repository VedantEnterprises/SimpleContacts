package com.cj.simplecontacts.tool;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenjun on 17-7-27.
 */

public class TimeUtil {

    public static  String timeCompare(long time){
        //格式化时间
        SimpleDateFormat yMDTime= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat hMTime= new SimpleDateFormat("HH:mm");
        SimpleDateFormat mDTime= new SimpleDateFormat("MM/dd HH:mm");


        Calendar calendar = Calendar.getInstance();
        String data = "";
        try {
            Date date=new Date(time);
            Date currentDate=new Date();

            calendar.setTime(currentDate);

            calendar.set(Calendar.HOUR_OF_DAY,24);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);

            //判断是否大于两天
            long surplus = (calendar.getTimeInMillis() - date.getTime())/(24*60*60*1000);
         //   Log.d("test","surplus = "+surplus);
            if(surplus < 1) {
                data = hMTime.format(date);
            }else  if(surplus < 2){
                data = "昨天 "+hMTime.format(date);
            }else  if(surplus < 3){
                data = "前天 "+hMTime.format(date);
            }else if(surplus < 365){
                data = mDTime.format(date);
            }else{
                data = yMDTime.format(date);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
}

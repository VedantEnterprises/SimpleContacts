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
        SimpleDateFormat CurrentTime= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat CurrentTime1= new SimpleDateFormat("HH:mm");
        SimpleDateFormat CurrentTime2= new SimpleDateFormat("MM/dd HH:mm");


        Calendar calendar2 = Calendar.getInstance();
        String data = "";
        try {
            Date date=new Date(time);
            Date currentDate=new Date();

            calendar2.setTime(currentDate);

            calendar2.set(Calendar.HOUR_OF_DAY,24);
            calendar2.set(Calendar.MINUTE,0);
            calendar2.set(Calendar.SECOND,0);

            //判断是否大于两天
            long surplus = (calendar2.getTimeInMillis() - date.getTime())/(24*60*60*1000);
            Log.d("test","surplus = "+surplus);
            if(surplus < 1) {
                data = CurrentTime1.format(date);
            }else  if(surplus < 2){
                data = "昨天 "+CurrentTime1.format(date);
            }else  if(surplus < 3){
                data = "前天 "+CurrentTime1.format(date);
            }else if(surplus < 365){
                data = CurrentTime2.format(date);
            }else{
                data = CurrentTime.format(date);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
}

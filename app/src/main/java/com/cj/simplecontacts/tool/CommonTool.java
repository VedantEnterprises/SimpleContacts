package com.cj.simplecontacts.tool;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by chenjun on 17-8-17.
 */

public class CommonTool {
    private CommonTool(){}
    private  static CommonTool commonTool;
    private static int  heightPixels;
    private static int  widthPixels;

    public static CommonTool getCommonTool(Activity activity){
        if(commonTool == null){
            commonTool = new CommonTool();
            Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            heightPixels = displayMetrics.heightPixels;
            widthPixels = displayMetrics.widthPixels;
        }
        return commonTool;
    }

    public int getHeightPixels(){
        return heightPixels;
    }

}

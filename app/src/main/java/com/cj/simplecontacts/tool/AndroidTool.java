package com.cj.simplecontacts.tool;

import android.os.Build;

/**
 * Created by chenjun on 17-7-5.
 */

public class AndroidTool {
    /**
     * 判断当前运行的系统是不是6.0之前
     * @return
     */
    public static boolean isPreM() {
        return Build.VERSION.SDK_INT < 23;
    }
}

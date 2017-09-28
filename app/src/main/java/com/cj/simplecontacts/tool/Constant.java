package com.cj.simplecontacts.tool;

import android.Manifest;

/**
 * Created by chenjun on 17-7-6.
 */

public class Constant {
    public static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    public static final String CONTACT_ASSISTANT = "联系人助手";
    public static final String CONTACT_GROUP = "我的分组";

    public static final String CONTACTS_FRAGMENT = "contactsFragment";
    public static final String DIAL_FRAGMENT = "dialFragment";
    public static final String MESSAGE_FRAGMENT = "messageFragment";
    public static final String LIFE_ASSISTANT_FRAGMENT = "lifeAssistantFragment";

    public static final String CHINA_UNICOM = "中国联通";
    public static final String CHINA_UNICOM_NUM_1 = "10010";
    public static final String CHINA_UNICOM_NUM_2 = "10012";
    public static final String CHINA_MOBILE = "中国移动";
    public static final String CHINA_MOBILE_NUM_1 = "10658098";
    public static final String CHINA_MOBILE_NUM_2 = "10086";
    public static final String CHINA_MOBILE_NUM_3 = "10086100";
    public static final String CMB = "招商银行";
    public static final String CMB_NUM_1 = "1065795555";
    public static final String WEI_SAI = "微赛体育";
    public static final String WEI_SAI_NUM_1 = "10690757102661";
    public static final String MO_BIKE = "摩拜科技";
    public static final String MO_BIKE_NUM_1 = "1069066692255";
    public static final String CSDN = "CSDN";
    public static final String CSDN_NUM_1 = "10690411523852260";
    public static final String AI_KANG = "爱康国宾";
    public static final String AI_KANG_NUM_1 = "106900323012";

    public static final String[] PERMISSIONS_ARRAY = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
            };
}

package com.cj.simplecontacts.tool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;

import com.cj.simplecontacts.BaseApplication;
import com.cj.simplecontacts.enity.NumAttribution;
import com.cj.simplecontacts.enity.Number;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenjun on 17-7-24.
 */

public class NumberUtil {
    // 用于匹配手机号码
    private final static String REGEX_MOBILEPHONE = "^0?1[34578]\\d{9}$";
    // 用于匹配固定电话号码
    private final static String REGEX_FIXEDPHONE = "^(010|02\\d|0[3-9]\\d{2})?\\d{6,8}$";
    // 用于获取固定电话中的区号
    private final static String REGEX_ZIPCODE = "^(010|02\\d|0[3-9]\\d{2})\\d{6,8}$";
    private static Pattern PATTERN_MOBILEPHONE;
    private static Pattern PATTERN_FIXEDPHONE;
    private static Pattern PATTERN_ZIPCODE;

    static {
        PATTERN_FIXEDPHONE = Pattern.compile(REGEX_FIXEDPHONE);
        PATTERN_MOBILEPHONE = Pattern.compile(REGEX_MOBILEPHONE);
        PATTERN_ZIPCODE = Pattern.compile(REGEX_ZIPCODE);
    }

    public static enum PhoneType {
        /**
         * 手机
         */
        CELLPHONE,

        /**
         * 固定电话
         */
        FIXEDPHONE,

        /**
         * 非法格式号码
         */
        INVALIDPHONE
    }

    public static class Numbers {
        private PhoneType type;
        /**
         * 如果是手机号码，则该字段存储的是手机号码 前七位；如果是固定电话，则该字段存储的是区号
         */
        private String code;
        private String number;

        public Numbers(PhoneType _type, String _code, String _number) {
            this.type = _type;
            this.code = _code;
            this.number = _number;
        }

        public PhoneType getType() {
            return type;
        }

        public String getCode() {
            return code;
        }

        public String getNumber() {
            return number;
        }

        public String toString() {
            return String.format("[number:%s, type:%s, code:%s]", number, type.name(), code);
        }
    }

    /**
     * 判断是否为手机号码
     *
     * @param number
     *            手机号码
     * @return
     */
    public static boolean isCellPhone(String number) {
        Matcher match = PATTERN_MOBILEPHONE.matcher(number);
        return match.matches();
    }

    /**
     * 判断是否为固定电话号码
     *
     * @param number
     *            固定电话号码
     * @return
     */
    public static boolean isFixedPhone(String number) {
        Matcher match = PATTERN_FIXEDPHONE.matcher(number);
        return match.matches();
    }

    /**
     * 获取固定号码号码中的区号
     *
     * @param strNumber
     * @return
     */
    public static String getZipFromHomephone(String strNumber) {
        Matcher matcher = PATTERN_ZIPCODE.matcher(strNumber);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    /**
     * 检查号码类型，并获取号码前缀，手机获取前7位，固话获取区号
     *
     * @param _number
     * @return
     */
    public static Numbers checkNumber(String _number) {
        String number = _number;
        Numbers rtNum = null;

        if (number != null && number.length() > 0) {
            if (isCellPhone(number)) {
                // 如果手机号码以0开始，则去掉0
                if (number.charAt(0) == '0') {
                    number = number.substring(1);
                }
                rtNum = new Numbers(PhoneType.CELLPHONE, number.substring(0, 7), _number);
            } else if (isFixedPhone(number)) {
                // 获取区号
                String zipCode = getZipFromHomephone(number);
                rtNum = new Numbers(PhoneType.FIXEDPHONE, zipCode, _number);
            } else {
                rtNum = new Numbers(PhoneType.INVALIDPHONE, null, _number);
            }
        }

        return rtNum;
    }


    /**
     *从本地数据文件  查询归属地  assets目录下文件
     * @param
     * @return
     */
    public static String getAttInfo(Context context, NumberUtil.PhoneType type, String code) {
        String attribution = "未知归属地";
        //Log.d(TAG,"readAssetsFile  num="+num);
        InputStream is = null;
        try {
            is = context.getAssets().open("Mobile.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] attInfo = line.split(",");

                String seven = attInfo[1];
                seven = seven.substring(1, seven.length() - 1).trim();//去掉""手机号前7位

                String province = attInfo[2];
                province = province.substring(1, province.length() - 1).trim();//省份

                String city = attInfo[3];
                city = city.substring(1, city.length() - 1).trim();//城市

                String operator = attInfo[4];
                operator = operator.substring(1, operator.length() - 1).trim().replace("中国", "");//运营商


                String areaCode = attInfo[5];
                areaCode = areaCode.substring(1, areaCode.length() - 1).trim();//去掉""区号


                if (type == NumberUtil.PhoneType.CELLPHONE) {

                    if (code.equals(seven)) {
                        if (province.equals(city)) {
                            attribution = city + " " + operator;
                        } else {
                            attribution = province + "-" + city + " " + operator;
                        }

                        break;
                    } else {
                        continue;
                    }

                } else if (type == NumberUtil.PhoneType.FIXEDPHONE) {

                    if (code.equals(areaCode)) {
                        if (province.equals(city)) {
                            attribution = city;
                        } else {
                            attribution = province + "-" + city;
                        }

                        break;
                    } else {
                        continue;
                    }

                }
            }

        } catch (IOException e) {

        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return attribution;
    }

    /**
     * 判断是否是双卡
     * @param context
     * @return
     */

    public static boolean isMultiSim(Context context) {
        boolean result = false;
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (telecomManager != null) {
            List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
            result = phoneAccountHandleList.size() >= 2;
        }
        return result;
    }

    /**
     * 拨打电话
     * @param context
     * @param id
     * @param telNum
     */
    public static void call(Context context, int id, String telNum){
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        if(telecomManager != null){
            List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + telNum));
            intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandleList.get(id));
            context.startActivity(intent);
        }
    }
//  用指定sim卡拨号

//    添加了一个携带值
//    phoneAccountHandleList.get(id)
//    id为0即为卡1 ，1即为卡二  希望对你有帮助
// 所有的选卡外呼都是在TelephonyManager和TelecomManager这两个类里面找，
// 这是Android 原生的，从5.1版本开始原生就支持双卡拨电话了自己多看看api
}

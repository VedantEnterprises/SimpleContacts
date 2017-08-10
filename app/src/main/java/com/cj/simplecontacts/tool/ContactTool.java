package com.cj.simplecontacts.tool;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Created by chenjun on 17-8-10.
 */

public class ContactTool {
    private final static String TAG = "ContactTool";

    public static Cursor getSmsCursor(ContentResolver resolver){
        // Log.d(TAG,"getSmsCursor()");
        String sortString = Telephony.Sms.DEFAULT_SORT_ORDER;//sort by  [A-Z]
        Uri uri = Uri.parse("content://sms");
        String[] projection = {
                Telephony.Sms._ID,
                Telephony.Sms.THREAD_ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.PERSON,
                Telephony.Sms.DATE,
                Telephony.Sms.READ,
                Telephony.Sms.SUBSCRIPTION_ID,
                Telephony.Sms.STATUS,
                Telephony.Sms.TYPE,
                Telephony.Sms.BODY};
        Cursor c = resolver.query(uri, projection, null, null, sortString);
        return c;
    }
    public static Cursor getSmsCursorByThreadID(ContentResolver resolver,int threadID){
        // Log.d(TAG,"getSmsCursorByThreadID()");
        String sortString = Telephony.Sms.DEFAULT_SORT_ORDER;//sort by  [A-Z]

        Uri uri = Uri.parse("content://sms");
        String[] projection = {
                Telephony.Sms._ID,
                Telephony.Sms.THREAD_ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.PERSON,
                Telephony.Sms.DATE,
                Telephony.Sms.READ,
                Telephony.Sms.SUBSCRIPTION_ID,
                Telephony.Sms.STATUS,
                Telephony.Sms.TYPE,
                Telephony.Sms.BODY};
        Cursor c = resolver.query(uri, projection, Telephony.Sms.THREAD_ID+" = ?", new String[]{threadID+""}, sortString);
        return c;
    }
}

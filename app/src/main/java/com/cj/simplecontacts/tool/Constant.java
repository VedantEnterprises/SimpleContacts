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

    public static final String[] PERMISSIONS_ARRAY = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG};
}

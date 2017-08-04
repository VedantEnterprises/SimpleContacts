package com.cj.simplecontacts.tool;

import com.cj.simplecontacts.enity.Message;

import java.util.Comparator;

/**
 * Created by chenjun on 17-8-4.
 */

public class SmsComparator implements Comparator<Message> {
    @Override
    public int compare(Message m1, Message m2) {
        long l = m2.getDate() - m1.getDate();
        if(l>=0){
            return 1;
        }
        return -1;
    }
}

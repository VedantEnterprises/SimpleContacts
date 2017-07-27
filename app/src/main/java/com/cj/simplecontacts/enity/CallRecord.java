package com.cj.simplecontacts.enity;

/**
 * Created by chenjun on 17-7-24.
 * 通话记录
 */

public class CallRecord {
    private String id;
    private String name;
    private String accountId;
    private String number;
    private int type;
    private int duration;
    private long date;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public long getDate() {
        return date;
    }

    public String getNumber() {

        return number;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

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
    private String location;
    private int type;
    private int duration;
    private long date;
    private String numAttr;

    private boolean isChecked;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumAttr() {
        return numAttr;
    }

    public void setNumAttr(String numAttr) {
        this.numAttr = numAttr;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (null == this.number ? 0 : this.number.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if (obj instanceof CallRecord) {
            CallRecord c = (CallRecord) obj;
            if(this.number != null){
                return this.number.equals(c.getNumber());
            }
        }
        return false;
    }
}

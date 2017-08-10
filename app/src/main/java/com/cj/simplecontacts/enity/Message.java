package com.cj.simplecontacts.enity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by chenjun on 17-8-2.
 */

public class Message implements Parcelable{
    private int _id;
    private int threadID;//对话id
    private String address;//手机号码
    private String person;//发件人，如果发件人在通讯录中则为具体姓名，陌生人为null 　　
    private long date;
    private int readStatus;//是否阅读  1已读
    private int status;//a TP-Status value or -1 if it status hasn't been received
    private int type;//类型 1是**到的，2是发出的
    private int subID;//2为卡1  3为卡2
    private String body;//内容
    private boolean isChecked;
    private ArrayList<Message> list = new ArrayList<>();

    public int get_id() {
        return _id;
    }

    public int getThreadID() {
        return threadID;
    }

    public String getAddress() {
        return address;
    }

    public String getPerson() {
        return person;
    }

    public long getDate() {
        return date;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setThreadID(int threadID) {
        this.threadID = threadID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getSubID() {
        return subID;
    }

    public void setSubID(int subID) {
        this.subID = subID;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<Message> getList() {
        return list;
    }

    public void setList(ArrayList<Message> list) {
        this.list = list;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.threadID;
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
        if (obj instanceof Message) {
            Message m = (Message) obj;
            return this.threadID == m.getThreadID();

        }
        return false;
    }

    public Message(){

    }

    protected Message(Parcel in) {
        threadID = in.readInt();
        address = in.readString();
        person = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            Message message = new Message(in);
            return message;
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(threadID);
        dest.writeString(address);
        dest.writeString(person);
    }
}

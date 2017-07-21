package com.cj.simplecontacts.enity;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by chenjun on 17-7-5.
 */

public class Contact {
    private boolean isContact;
    private String name;
    private String phonebook_label;
    private String pinying;
    private String contactID;
    private String contactAccountID;
    private boolean isChecked = false;
    private ArrayList<Number> numbers = new ArrayList<>();//one contact have more than one phone number

    public boolean isContact() {
        return isContact;
    }

    public void setContact(boolean contact) {
        isContact = contact;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhonebookLabel() {
        return phonebook_label;
    }
    public void setPhonebookLabel(String phonebookLabel) {
        this.phonebook_label = phonebookLabel;
    }

    public void setPinying(String pinying) {
        this.pinying = pinying;
    }
    public String getPinying() {
        return pinying;
    }

    public String getContactID() {
        return contactID;
    }
    public void setContactID(String contactID) {
        this.contactID = contactID;
    }


    public String getContactAccountID() {
        return contactAccountID;
    }
    public void setContactAccountID(String contactAcoountID) {
        this.contactAccountID = contactAcoountID;
    }


    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<Number> getNumbers() {
        return numbers;
    }
    public void setNumbers(ArrayList<Number> numbers) {
        this.numbers = numbers;
    }


    /**
     * 获取关键字在名字pinying中下标集合
     * eg: chen_jun  search  ch  index is 0,search cj  index is 0 and 1
     */
    public ArrayList<Integer> getKeyIndexList(String str){

        ArrayList<Integer> lsit = new ArrayList<>();
        if(TextUtils.isEmpty(str)){
            return lsit;
        }
        char ch = str.charAt(0);
      //  Log.d("test","getKeyIndexList ch="+(int)ch+"\n");
        if((ch >= 'a' && ch <='z')|| (ch >= 'A' && ch <='Z')){
            //if search by letter
           // Log.d("test","getKeyIndexList letter \n");
            int lastIndex = -1;
            String temp = str.toLowerCase();
            for(int i=0;i<temp.length();i++){
                char c = temp.charAt(i);

                //Log.d(tag,"getKeyIndexList c="+c+"\n");
                int index = pinying.indexOf(c,lastIndex+1);
                // Log.d(tag,"getKeyIndexList index="+index+"\n");
                if(index < 0){
                    lsit.clear();
                    return lsit;
                }else{
                    lastIndex = index;
                    String substring = pinying.substring(0, index + 1);
                    String[] split = substring.split("_");
                    if(!lsit.contains(split.length)){
                        lsit.add(split.length);
                    }
                }
            }
        }else{
            //if search by hanzi
            int lastIndex = -1;
            for(int i=0;i<str.length();i++){
                char c = str.charAt(i);
                //Log.d("test","getKeyIndexList c="+c+"\n");
                int index = name.indexOf(c,lastIndex+1);
               // Log.d("test","getKeyIndexList index="+index+"\n");
                if(index < 0){
                    lsit.clear();
                    return lsit;
                }else{
                    lastIndex = index;
                    lsit.add(index+1);
                }
            }
        }
        return lsit;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (null == this.contactID ? 0 : this.contactID.hashCode());
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
        if (obj instanceof Contact) {
            Contact c = (Contact) obj;
            if(this.contactID != null){
                return this.contactID.equals(c.getContactID());
            }
        }
        return false;
    }
}

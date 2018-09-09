package com.ceri.android.ucounter.ui;

import android.util.Log;

public class CounterInfo {
    private int mValue;
    private String mName;
    private int mId;
    private int next;

    public CounterInfo(){
        mValue = 0;
        mName = "N/A";
        mId = -2;
        next = -1;
    }

    // Get
    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getValue(){
        return mValue;
    }

    public int getNext(){
        return next;
    }

    public void setValue(int value){
        mValue = value;
    }

    public void setValue(String sValue) {
        try{
            mValue = Integer.parseInt(sValue);
        } catch (NumberFormatException e){
            Log.e(CounterInfo.class.getSimpleName(), "NumberFormatExeception: " + e);
        }
    }

    public void setName(String name){
        if(!name.isEmpty()) {mName = name;}
    }

    public void setId(int id){
        mId = id;
    }

    public void setNext(int next){
        this.next = next;
    }
}

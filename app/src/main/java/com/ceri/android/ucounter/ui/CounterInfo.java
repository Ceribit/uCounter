package com.ceri.android.ucounter.ui;

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
    // Set
    public void setValue(int value){
        mValue = value;
    }

    public void setName(String name){
        mName = name;
    }

    public void setId(int id){
        mId = id;
    }

    public void setNext(int next){
        this.next = next;
    }
}

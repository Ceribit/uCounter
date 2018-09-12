package com.ceri.android.ucounter.ui.presenters;

import android.util.Log;

import com.ceri.android.ucounter.data.CounterDataController;
import com.ceri.android.ucounter.ui.CounterItemContract;

public class CounterDialogPresenter implements CounterItemContract.Presenter {
    /** Tag value representing the name of the class */
    private static String TAG = CounterDialogPresenter.class.getSimpleName();

    private CounterDataController mDataController;

    public CounterDialogPresenter(){
    }

    public void attach(Object context) {
        mDataController = new CounterDataController();
        mDataController.setContext(context);
    }

    public Boolean insertCounter(String name, int initialValue){
        Log.e(TAG, "Name: " + name);
        return mDataController.append(name, initialValue);
    }

    public Boolean deleteCounter(int id){
        return mDataController.delete(id);
    }

}

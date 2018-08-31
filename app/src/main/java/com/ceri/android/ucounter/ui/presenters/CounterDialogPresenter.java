package com.ceri.android.ucounter.ui.presenters;

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
        return mDataController.appendCounter(name, initialValue);
    }

    public Boolean deleteCounter(int id){
        return mDataController.deleteCounter(id);
    }

}
package com.ceribit.android.ucounter.ui.presenters;

import android.util.Log;

import com.ceribit.android.ucounter.data.CounterDataController;
import com.ceribit.android.ucounter.ui.CounterItemContract;

public class CounterEditorPresenter implements CounterItemContract.Presenter {
    /** Tag value representing the name of the class */
    private static String TAG = CounterEditorPresenter.class.getSimpleName();

    private CounterDataController mDataController;

    public CounterEditorPresenter(){
    }

    public void attach(Object context) {
        mDataController = new CounterDataController();
        mDataController.setContext(context);
    }

    public Boolean insertCounter(String name, int initialValue){
        return mDataController.append(name, initialValue);
    }

    public Boolean deleteCounter(int id){
        return mDataController.delete(id);
    }

}

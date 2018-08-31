package com.ceri.android.ucounter.ui.presenters;

import android.support.annotation.Nullable;

import com.ceri.android.ucounter.data.CounterDataController;
import com.ceri.android.ucounter.ui.CounterItemContract;

import java.util.ArrayList;

public class CounterItemPresenter<V> implements CounterItemContract.Presenter {

    /** Tag value representing the name of the class */
    private static String TAG = CounterItemPresenter.class.getSimpleName();

    private CounterDataController mDataController;

    CounterItemContract.View mView;

    public CounterItemPresenter(){
        mDataController = new CounterDataController();
    }

    public void attach(V view) {
    }

    public Boolean updateCounter(int id, String name, int oldValue, int change){
        return mDataController.updateCounter(id, name, oldValue, change);
    }

    @Nullable
    public static ArrayList<Integer> getPositionList(Object view){
        return CounterDataController.getPositionList(view);
    }

    public void bind(CounterItemContract.View view){
        mView = view;
        mDataController.setContext(view.getViewContext());
    }

    public void unbind(){
        mView = null;
    }

}

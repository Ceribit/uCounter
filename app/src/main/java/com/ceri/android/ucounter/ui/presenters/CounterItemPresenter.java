package com.ceri.android.ucounter.ui.presenters;

import android.support.annotation.Nullable;

import com.ceri.android.ucounter.data.CounterDataController;
import com.ceri.android.ucounter.ui.CounterInfo;
import com.ceri.android.ucounter.ui.CounterItemContract;

import java.util.ArrayList;

public class CounterItemPresenter implements CounterItemContract.Presenter {

    // Tag used for debugging
    private static String TAG = CounterItemPresenter.class.getSimpleName();

    // DataController
    private CounterDataController mDataController;

    CounterItemContract.View mView;

    public CounterItemPresenter(){
        mDataController = new CounterDataController();
    }


    public Boolean updateCounter(int id, String name, int oldValue, int change){
        return mDataController.updateCounter(id, name, oldValue, change);
    }

    public Boolean updateCounter(CounterInfo counterInfo){
        return mDataController.updateCounter(
                counterInfo.getId(),
                counterInfo.getName(),
                counterInfo.getValue(),
                0);
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

    public CounterInfo getCounterInfo(int id){
        return mDataController.getCounterData(id);
    }

}

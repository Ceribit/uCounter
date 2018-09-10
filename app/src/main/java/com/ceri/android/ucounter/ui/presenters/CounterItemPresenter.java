package com.ceri.android.ucounter.ui.presenters;

import android.nfc.Tag;
import android.support.annotation.Nullable;

import com.ceri.android.ucounter.data.CounterDataController;
import com.ceri.android.ucounter.ui.CounterInfo;
import com.ceri.android.ucounter.ui.CounterItemContract;

import java.util.ArrayList;
import java.util.List;

public class CounterItemPresenter implements CounterItemContract.Presenter {

    /** Tag used for debugging */
    private static String TAG = CounterItemPresenter.class.getSimpleName();

    /** Model Component of MVP architecture */
    private CounterDataController mDataController;

    /** View component of MVP architecture */
    CounterItemContract.View mView;

    /** Constructor */
    public CounterItemPresenter(){
        mDataController = new CounterDataController();
    }


    /** Updates database given multiple counter fields */
    public Boolean updateCounter(int id, String name, int oldValue, int change){
        return mDataController.updateCounter(id, name, oldValue, change);
    }


    /** Updates database given a CounterInfo object*/
    public Boolean updateCounter(CounterInfo counterInfo){
        return mDataController.updateCounter(
                counterInfo.getId(),
                counterInfo.getName(),
                counterInfo.getValue(),
                0);
    }


    /** Retrieves array list of counter IDs */
    @Nullable
    public static ArrayList<Integer> getPositionList(Object view){
        return CounterDataController.getPositionList(view);
    }

    /** Retrieves context from View to be used in Model */
    public void bind(CounterItemContract.View view){
        mView = view;
        mDataController.setContext(view.getViewContext());
    }


    /** Unbinds connected view */
    public void unbind(){
        mView = null;
        mDataController.setContext(null);
    }


    /** Returns CounterInfo from database given its id */
    public CounterInfo getCounterInfo(int id){
        return mDataController.getCounterData(id);
    }

    /** Returns list of CounterInfos from database given a list of positions */
    public List<CounterInfo> getCounterInfoList(List<Integer> ids){
        ArrayList<CounterInfo> counterInfoList = new ArrayList<>();
        for(int i = 1; i < ids.size(); i++){
            counterInfoList.add(mDataController.getCounterData(ids.get(i)));
        }
        return counterInfoList;
    }
}

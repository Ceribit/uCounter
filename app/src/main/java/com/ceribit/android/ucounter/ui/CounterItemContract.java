package com.ceribit.android.ucounter.ui;

import android.app.Activity;
import android.database.Cursor;
import android.view.View;

public interface CounterItemContract {

    /**
     * This holds the main view of the program
     *      The view interface has the role of managing all the views and interacting with the
     *      presenter in order to update
     * */
    interface View{
        Object getViewContext();
    }

    /**
     * This holds the main presenter of the program
     * */
    interface Presenter<V>{
        //void attach(V view);
        //public void updateCounter(Cursor cursor);
    }

}

package com.ceri.android.ucounter.ui.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ceri.android.ucounter.R;
import com.ceri.android.ucounter.ui.CounterInfo;
import com.ceri.android.ucounter.ui.CounterItemContract;
import com.ceri.android.ucounter.ui.presenters.CounterItemPresenter;
import com.ceri.android.ucounter.data.db.CounterContract;

public class CounterSlidePageFragment extends Fragment implements CounterItemContract.View{

    /**
     * Views found in the xml file
     */
    private Cursor mCursor;
    private TextView mTitleText;
    private TextView mValueText;
    private FloatingActionButton mAddButton;
    private FloatingActionButton mSubtractionButton;
    private CounterItemPresenter mPresenter;
    private ContentResolver mContentResolver;
    private int mId;
    private int mValue;
    private String mName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Get views of the corresponding item
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.counter_page_item, container, false);
        mTitleText = rootView.findViewById(R.id.fragment_name);
        mValueText = rootView.findViewById(R.id.fragment_value);



        // Add model and presenter to this view
        mPresenter = new CounterItemPresenter();
        mPresenter.bind(this);

        // Get cursor of the current queue and set the initial values of the counter being shown
        setCursor();
        setDisplay();

        // Set Button clicks
        mAddButton = rootView.findViewById(R.id.counter_page_add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setButtonClick(1);
            }
        });
        mSubtractionButton = rootView.findViewById(R.id.counter_page_subtraction_button);
        mSubtractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonClick(-1);
            }
        });

        // mAddButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
        // .getColor(android.R.color.holo_blue_bright));
        mAddButton.setImageResource(R.drawable.fab_add_icon);
        mAddButton.setColorFilter(getResources().getColor(android.R.color.white));
        mAddButton.setRippleColor(getResources().getColor(android.R.color.white));
        mSubtractionButton.setImageResource(R.drawable.fab_minus_icon);
        mSubtractionButton.setColorFilter(getResources().getColor(android.R.color.white));
        return rootView;
    }

    // Returns the context of the fragment
    @Override
    public Object getViewContext() {
        return getContext();
    }

    // Gets the data from the argument 'position' passed in when creating the fragment
    public void setCursor(){
        // Get location of data in table
        if(getArguments() !=  null) {
            int itemId = getArguments().getInt("id", 0);

            CounterInfo counterInfo = mPresenter.getCounterInfo(itemId);

            // Get values from database
            if(counterInfo.getId() != -1) {
                mId = counterInfo.getId();
                mName = "Name: " + counterInfo.getName() +
                        "\nID: " + counterInfo.getId() +
                        "\nValue: " + counterInfo.getValue() +
                        "\nNext ID: " + counterInfo.getNext();

                mValue = counterInfo.getValue();
            }
        }
    }

    // Sets up the initial display of the fragment
    public void setDisplay() {
        mTitleText.setText(mName);
        mValueText.setText(String.valueOf(mValue));
    }

    // Updates the counter number displayed in the center
    public void updateValue(int n){
        mValueText.setText(String.valueOf(n));
    }

    // Updates the value of the displayed number
    public void setButtonClick(int step){
        boolean hasChanged = mPresenter.updateCounter(mId, mName , mValue, step);
        if (hasChanged){
            updateValue(mValue+step);
            mValue += step;
        } else{
            Toast.makeText(getContext(), "Not Updated", Toast.LENGTH_SHORT).show();
        }
    }

    public int getItemId(){
        return mId;
    }
}

package com.ceri.android.ucounter.ui.activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.ceri.android.ucounter.R;
import com.ceri.android.ucounter.data.db.CounterContract;
import com.ceri.android.ucounter.ui.CounterInfo;
import com.ceri.android.ucounter.ui.CounterItemContract;
import com.ceri.android.ucounter.ui.presenters.CounterItemPresenter;


public class CounterEditorActivity extends AppCompatActivity implements
        CounterItemContract.View {

    /** Presenter which communicates with the View and the Model to retrieve data*/
    private CounterItemPresenter mPresenter;

    /** The name of the associated counter */
    private EditText nameEditText;

    /** The value of the associated counter */
    private EditText valueEditText;
    /** The Id for which this editor is for */
    private int mId;

    /** The list of details for the current counter */
    private CounterInfo mCounterInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.counter_editor);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mId = intent.getIntExtra("counterId", 1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        mPresenter = new CounterItemPresenter();
        mPresenter.bind(this);

        nameEditText = findViewById(R.id.editor_item_name_input);
        valueEditText = findViewById(R.id.editor_item_value_input);
        mCounterInfo = mPresenter.getCounterInfo(mId);

        nameEditText.setText(mCounterInfo.getName());
        valueEditText.setText(String.valueOf(mCounterInfo.getValue()));
    }

    @Override
    public void finish() {
        mCounterInfo.setName(nameEditText.getText().toString());
        mCounterInfo.setValue(Integer.parseInt(valueEditText.getText().toString()));
        mPresenter.updateCounter(mCounterInfo);
        super.finish();
        overridePendingTransition(
                R.anim.animation_left_to_center,
                R.anim.animation_center_to_right
        );
    }

    @Override
    public Object getViewContext() {
        return getBaseContext();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}

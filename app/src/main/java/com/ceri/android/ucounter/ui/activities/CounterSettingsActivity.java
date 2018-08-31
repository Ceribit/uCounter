package com.ceri.android.ucounter.ui.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ceri.android.ucounter.R;

import static java.security.AccessController.getContext;


// TODO: Implement Counter-Specific Settings Page
public class CounterSettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.counter_settings);
        ListView listView = getListView();
        getLayoutInflater().inflate(R.layout.settings_toolbar, null);

        listView.setDivider(getResources().getDrawable(R.drawable.line_horizontal));
        listView.setDivider(null);

        //TODO: Add Action Bar to Settings Page
    }
}

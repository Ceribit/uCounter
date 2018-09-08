package com.ceri.android.ucounter.ui.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import com.ceri.android.ucounter.R;

// TODO: Implement General Settings Page
public class GeneralSettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_preferences);
    }
}

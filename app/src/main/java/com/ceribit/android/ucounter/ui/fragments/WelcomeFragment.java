package com.ceribit.android.ucounter.ui.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ceribit.android.ucounter.R;

public class WelcomeFragment extends android.support.v17.leanback.app.OnboardingSupportFragment {

    public static final String COMPLETED_ONBOARDING = "completed_onboarding";
    private ImageView mContentView;

    private static final int[] pageTitles = {
            R.string.onboarding_title_welcome,
            R.string.onboarding_title_instructions
    };

    private static final int[] pageDescriptions = {
            R.string.onboarding_description_welcome,
            R.string.onboarding_description_instructions
    };

    /** Number of pages in the introduction screen */
    private static final int NUM_PAGES = 2;

    @Override
    protected void onFinishFragment() {
        super.onFinishFragment();
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        sharedPreferencesEditor.putBoolean(COMPLETED_ONBOARDING, true).apply();
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }


    @Nullable
    @Override
    protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }


    @Nullable
    @Override
    protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
        View bgView = new View(getActivity());
        bgView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        return bgView;
    }

    @Override
    public int onProvideTheme() {
        return R.style.Theme_Leanback_Onboarding;
    }

    @Override
    protected int getPageCount() {
        return NUM_PAGES;
    }

    @Override
    protected CharSequence getPageTitle(int pageIndex) {
        return getString(pageTitles[pageIndex]);
    }

    @Override
    protected CharSequence getPageDescription(int pageIndex) {
        return getString(pageDescriptions[pageIndex]);
    }






}

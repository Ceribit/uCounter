package com.ceri.android.ucounter.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ceri.android.ucounter.R;
import com.ceri.android.ucounter.data.CounterDataController;
import com.ceri.android.ucounter.data.db.CounterContract;
import com.ceri.android.ucounter.data.db.CounterDbHelper;
import com.ceri.android.ucounter.ui.CounterItemContract;
import com.ceri.android.ucounter.ui.dialogs.AddNewCounterDialog;
import com.ceri.android.ucounter.ui.dialogs.DeleteCounterDialog;
import com.ceri.android.ucounter.ui.fragments.CounterSlidePageFragment;
import com.ceri.android.ucounter.ui.presenters.CounterItemPresenter;

import java.util.ArrayList;

public class CounterActivity extends AppCompatActivity implements CounterItemContract.View {

    /** Drawer layout provides control over the Drawer View item */
    private DrawerLayout mDrawerLayout;

    /** Handles animation and allows swiping to access previous and next wizard steps. */
    private ViewPager mPager;

    /** The pager adapter, which provides the pages to the view pager widget. */
    private PagerAdapter mPagerAdapter;

    /** Datebase Variables*/
    private CounterDbHelper mDbHelper;

    /** Manages multiple fragments used throughout the program */
    private FragmentManager mFragmentManager;

    /**
     * Overridden onCreate Function
     * @param savedInstanceState Takes the current state of the instance
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_page_main);

        // Set up database
        mDbHelper = new CounterDbHelper(this);

        // Set up Fragment Manager
        mFragmentManager = getSupportFragmentManager();

        //Set Up Navigation Panel
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        return setDrawerListener(item);
                    }
                }
        );

        // Instantiate a ViewPager and PagerAdapter
        mPager = (ViewPager) findViewById(R.id.main_pager);
        mPagerAdapter = new CounterSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // TODO: Set Empty View
        View emptyView = findViewById(R.id.counter_empty_view);
        emptyView.setVisibility(View.GONE);

        //Set Up Toolbar and associated images
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        /* TODO: Set addDrawerListener */


    }

    @Override
    public Object getViewContext() {
        return this;
    }

    /**************************
     * Menu Options
     **************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu , menu);
        return true;
    }



    /**************************
     * Screen Sliding Adapter
     **************************/
    private class CounterSlidePagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Integer> positionList;

        CounterSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            positionList = CounterItemPresenter.getPositionList(this);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("id", positionList.get(position));
            CounterSlidePageFragment pageFragment = new CounterSlidePageFragment();
            pageFragment.setArguments(bundle);
            return pageFragment;
        }

        @Override
        public int getCount() {
            return getRowCount();
        }
    }

    @Override
    public void onBackPressed() {
        if(mPager.getCurrentItem() == 0){
            super.onBackPressed();
        } else{
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**************************
    * onClick Utility
     **************************/
    /**
     * Performs certain drawer actions based on
     * @param item [Takes in item selected by the user]
     * @return
     */
    public Boolean setDrawerListener(MenuItem item){
        // Set item as selected to have a highlight
        item.setChecked(true);
        // Close drawer when item is selected
        mDrawerLayout.closeDrawers();
        // TODO: Update UI based on item selected
        int menuId = item.getItemId();
        String toastMessage = "Nothing clicked";
        switch (menuId){
            case R.id.add_counter:
                // Add new counter
                AddNewCounterDialog addFragment = new AddNewCounterDialog();
                addFragment.show(mFragmentManager, "Add a counter");
                toastMessage = "Add counter clicked!";
                break;
            case R.id.add_group:
                // TODO: Add new counter group
                toastMessage = "Add group clicked!";
                break;
            case R.id.drawer_settings:
                Intent intent = new Intent(this, GeneralSettingsActivity.class);
                startActivity(intent);
                toastMessage = "Settings clicked!";
                break;
            case R.id.drawer_delete_all:
                getContentResolver().delete(CounterContract.CounterEntry.CONTENT_URI, null, null);
        }
        Toast.makeText(getBaseContext(), toastMessage,
                Toast.LENGTH_SHORT).show();
        return true;
    }


    /**
     * Performs certain actions based on
     * @param item [Takes in item selected by the user]
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_delete_data:
                // TODO: Add delete functionality to the menu
                DeleteCounterDialog deleteFragment = new DeleteCounterDialog();
                int pos = mPager.getCurrentItem();
                Log.e("CounterActivity", "Position you gave to your sons was " + pos);
                deleteFragment.setId(pos);
                deleteFragment.show(mFragmentManager, "Delete a counter");

                Toast.makeText(this, "Delete button clicked.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_counter_settings:
                Intent intent = new Intent(this,CounterSettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    /**************************
     * Database Utility
     **************************/
    public int getRowCount() {
        Bundle bundle = getContentResolver().call(CounterContract.CounterEntry.CONTENT_URI, "getRowCount",
                null, null);
        return bundle.getInt("numRows");
    }

    public Boolean isEmpty(){
        return getRowCount() == 0;
    }

    public void notifyChange(){
        mPagerAdapter.notifyDataSetChanged();
    }
}

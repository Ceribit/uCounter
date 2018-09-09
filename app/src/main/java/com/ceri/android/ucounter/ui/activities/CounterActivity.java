package com.ceri.android.ucounter.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ceri.android.ucounter.R;
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
    private CounterSlidePagerAdapter mPagerAdapter;

    /** Datebase Variables*/
    private CounterDbHelper mDbHelper;

    /** Manages multiple fragments used throughout the program */
    private FragmentManager mFragmentManager;

    /** Log Tag for Debugging */
    private static String TAG = CounterActivity.class.getSimpleName();

    /** Empty View */
    private TextView mEmptyView;

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
        mPager.setOffscreenPageLimit(3);
        // Set Empty View

        //Set Up Toolbar and associated images
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up bar
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorText));
        actionBar.setHomeAsUpIndicator(drawable);

        /** TODO: Set addDrawerListener */


    }

    /** Returns activity context for the model */
    @Override
    public Object getViewContext() {
        return this;
    }

    /**
     * @param menu is the associated menu item selected
     * @return Action based on selected menu option and whether it was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_delete);
        drawable.setColorFilter(getResources().getColor(R.color.colorText), PorterDuff.Mode.SRC_IN);
        MenuItem menuItem = menu.findItem(R.id.action_delete_data);
        menuItem.setIcon(drawable);

        return true;
    }



    /**************************
     * Screen Sliding Adapter
     **************************/
    private class CounterSlidePagerAdapter extends FragmentStatePagerAdapter {

        /**
         * A list of ids for each counter where the array index represents the counter's associated
         * page
          */
        ArrayList<Integer> positionList;

        /** Instantiates the position list */
        CounterSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            positionList = CounterItemPresenter.getPositionList(getBaseContext());
            Log.i(TAG, "Position list Array = " + positionList.toString());
        }

        /** Returns the counter fragment of a given position */
        @Override
        public Fragment getItem(int position) {
            if (position > positionList.size() - 1){
                CounterSlidePageFragment pageFragment = new CounterSlidePageFragment();
                return pageFragment;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("id", positionList.get(position+1));
            CounterSlidePageFragment pageFragment = new CounterSlidePageFragment();
            pageFragment.setArguments(bundle);
            return pageFragment;
        }

        /** Refreshes Position List and makes fragments refresh*/
        @Override
        public int getItemPosition(@NonNull Object item) {
            positionList = CounterItemPresenter.getPositionList(getBaseContext());
            return POSITION_NONE;
        }

        /** Returns the number of rows */
        @Override
        public int getCount() {
            mEmptyView = findViewById(R.id.counter_empty_view);

            positionList = CounterItemPresenter.getPositionList(getBaseContext());
            int positionSize = positionList.size();
            if(positionSize < 2){
                mEmptyView.setVisibility(View.VISIBLE);
            } else{
                mEmptyView.setVisibility(View.INVISIBLE);
            }
            return positionSize -1 ; // -1 is done to exclude the default value in the front of the
                                     // list
        }

        /** Returns the id at the associated position */
        private int getIdAtPosition(int position){
            return positionList.get(position+1);
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


    ///**************************
    //* OnClick Utility for Drawers/Menus
    //**************************/
    /**
     * Performs certain drawer actions based on
     * @param item [Takes in item selected by the user]
     * @return
     */
    public Boolean setDrawerListener(MenuItem item){
        // Highlights selected item
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
                toastMessage = "Add group clicked! (But nothing happened!) ";
                break;
            case R.id.drawer_settings:
                Intent intent = new Intent(this, GeneralSettingsActivity.class);
                startActivity(intent);
                toastMessage = "Settings clicked!";
                break;
            case R.id.drawer_delete_all:
                getContentResolver().delete(CounterContract.CounterEntry.CONTENT_URI, null, null);
                SharedPreferences.Editor preferencesEditor = getSharedPreferences("com.ceri.android.counterprefs", MODE_PRIVATE).edit();
                preferencesEditor.putInt("tailId", -1);
                preferencesEditor.apply();
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

            // Returns the user back to the menu
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            // Opens up Delete Notification to delete the current page item
            case R.id.action_delete_data:
                int pagePosition = mPager.getCurrentItem();
                Log.e(TAG, "onOptionsItemSelected : Delete_Data ID Provided = "
                        + mPagerAdapter.getIdAtPosition(mPager.getCurrentItem()));

                DeleteCounterDialog deleteFragment = new DeleteCounterDialog();

                // Sets ID and provides the current page number to the delete dialog
                deleteFragment.setId(
                        mPagerAdapter.getIdAtPosition(pagePosition),
                        pagePosition, mPagerAdapter.
                                getCount()
                );

                // Show fragment to the screen
                deleteFragment.show(mFragmentManager, "Delete a counter");
                Toast.makeText(this, "Delete button clicked.", Toast.LENGTH_SHORT).show();
                break;

            // Opens up the settings page for the current page item
            case R.id.action_counter_settings:
                // Create intent to open up the settings screen
                Intent intent = new Intent(this, CounterEditorActivity.class);

                // Store current page information
                intent.putExtra(
                        "counterId",
                        mPagerAdapter.getIdAtPosition(mPager.getCurrentItem())
                );

                // /Start Activity
                startActivity(intent);
                overridePendingTransition(
                        R.anim.animation_right_to_center,
                        R.anim.animation_center_to_left);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // *************************
    // * Notification Utility
    // *************************
    /** Notifies the system that the adapter has changed and changes position of pager */
    public void notifyChange(Integer newPosition){
        mPagerAdapter.notifyDataSetChanged();

        // If not specified, don't move
        if(newPosition != null) {
            if (newPosition != -1) {
                mPager.setCurrentItem(newPosition);
            } else {
                mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
            }
        }
    }

    /** Every time the program resumes, update the fragments */
    @Override
    protected void onPostResume() {
        notifyChange(null);
        super.onPostResume();
    }

}

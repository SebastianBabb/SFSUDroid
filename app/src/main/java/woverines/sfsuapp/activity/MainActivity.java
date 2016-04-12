package woverines.sfsuapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import woverines.sfsuapp.R;
import woverines.sfsuapp.fragment.CampusMapFragment;
import woverines.sfsuapp.fragment.Gallery;
import woverines.sfsuapp.fragment.HomePageFragment;
import woverines.sfsuapp.fragment.ResourcesFragment;
import woverines.sfsuapp.fragment.ShuttleScheduleFragment;
import woverines.sfsuapp.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //HIDES TABs
//        tabLayout.setVisibility(View.GONE);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home_page) {
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.campus_map) {
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.shuttle) {
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.resources) {
            mViewPager.setCurrentItem(3);
        } else if (id == R.id.schedule_planner) {
            onAddClassAction();
        } else if (id == R.id.staff_directory) {
            onGoToStaffDirectoryAction();
        } else if (id == R.id.settings) {
            onOptionsItemSelected();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    //TODO extract
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a CampusMapFragment (defined as a static inner class below).\
            if(position == 0)
                return HomePageFragment.newInstance(position + 1, "HOME PAGE HERE");
            else if(position == 1)
                return CampusMapFragment.newInstance(position + 1, "CAMPUS MAP HERE");
            else if(position == 2)
                return ShuttleScheduleFragment.newInstance(position + 1, "SHUTTLE SCHEDULE HERE");
            else if(position == 3)
                return ResourcesFragment.newInstance(position + 1, "RESOURCES HERE - Helpful links");
            else
                return HomePageFragment.newInstance(position + 1, "Home default");
        }

        @Override
        public int getCount() {
            //TODO
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Map";
                case 2:
                    return "Shuttle";
                case 3:
                    return "Links";

            }
            return null;
        }
    }

    public void onOptionsItemSelected() {
        Intent goToSettings = new Intent(this, SettingsActivity.class);

        startActivity(goToSettings);

    }

    private void onAddClassAction() {
        Intent goToSchedule = new Intent(this, SchedulePlanner.class);

        startActivity(goToSchedule);
    }

    private void onGoToStaffDirectoryAction() {
        Intent goToStaffDirectory = new Intent(this, StaffDirectory.class);

        startActivity(goToStaffDirectory);
    }
}

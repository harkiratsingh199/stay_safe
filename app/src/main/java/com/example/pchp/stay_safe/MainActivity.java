package com.example.pchp.stay_safe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    ArrayList<Fragment> alFrag = new ArrayList<>();
    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalClass.db = openOrCreateDatabase("CONTACTSDATABASE",MODE_PRIVATE,null);
        GlobalClass.db.execSQL("CREATE TABLE IF NOT EXISTS contacts(id integer primary key," +
                "                       contactname varchar,contactno number)");
        alFrag.add(new SecondFragment());
        alFrag.add(new FirstFragment());
        alFrag.add(new ContentFragment());
        alFrag.add(new BlankFragment());

//        alFrag.add(new Third());


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {

            // This method will trigger on item Click of navigation menu
            @Override

            public boolean onNavigationItemSelected(MenuItem menuItem)
            {

                menuItem.setChecked(true);
                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId())
                {


                        //Replacing the main content with ContentFragment Which is our Inbox View;
                        case R.id.dotnet:
                            tabLayout.getTabAt(0).select();
                            return true;

                        // For rest of the options we just show a toast on click

                        case R.id.android:
                            tabLayout.getTabAt(1).select();
                            return true;
                        case R.id.contacts:
                            tabLayout.getTabAt(2).select();
                            return true;
                        case R.id.php:
                            tabLayout.getTabAt(3).select();
                            return true;
//                        case R.id.corejava:
//                            tabLayout.getTabAt(4).select();
//                            return true;

                        default:
                            Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                            return true;
                    }


            }

        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer)
        {

            @Override
            public void onDrawerClosed(View drawerView)
            {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Home");
        tabLayout.getTabAt(1).setText("Contacts");
        tabLayout.getTabAt(2).setText("View Locations");
        tabLayout.getTabAt(3).setText("About Us");
//        tabLayout.getTabAt(4).setText("Core Java");

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager)
                {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab)
                    {
                        super.onTabSelected(tab);
                        MenuItem mi =
                                navigationView.getMenu().getItem(tab.getPosition());
                        mi.setChecked(true);
                        int position = tab.getPosition();
                        viewPager.setCurrentItem(position);
                    }
                });


        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }


    class MyPagerAdapter extends FragmentPagerAdapter
    {

        MyPagerAdapter()
        {
            super(getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position)
        {
            return alFrag.get(position);
        }

        @Override
        public int getCount()
        {
            return alFrag.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout)
        {
           SharedPreferences pref1=getSharedPreferences("pref1", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=pref1.edit();
            editor.clear();
            editor.commit();


            GlobalClass.alLocations.clear();
            GlobalClass.alContacts.clear();

            MainActivity.this.finish();


            Intent it=new Intent(getBaseContext(),login.class);
            startActivity(it);

        }




        return super.onOptionsItemSelected(item);
    }
}

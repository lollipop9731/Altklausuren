package com.example.loren.altklausurenneu;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.loren.altklausurenneu.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ModulChooseFragment.FragmentInteractionListener {
    private static final int NOTCHOOSEN = -99;
    public static final String MEINEPROTOKOLLE = "Meine Protokolle";
    private int selectedList;
    private User user;

    //todo cleeeeeean!!!!!!!!!!

    //annotation to allow not every value for saving selected item
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LIST_EXPANDABLE, LISTVIEW})
    public @interface ListType {
    }

    public static final int LIST_EXPANDABLE = 0;
    public static final int LISTVIEW = 1;


//todo clear this interface chaos
    //todo delete all files on create -> thumbnails and full photos


    FabSpeedDial fabSpeedDial;
    FirebaseAuth mAuth;

    @Override
    public void onFragmentInteraction(String title) {
        //change title when fragment calls
        getSupportActionBar().setTitle(title);

    }

    private static final String TAG = "MainActivity";


    NavigationDrawerListAdapter navigationDrawerListAdapter;

    private FirebaseMethods firebaseMethods;

    //Expandable List in Navigation Drawer
    private ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    //Array List for Navigation Drawer
    String[] navdrawerheader;
    ListView navList;

    Context context;
    private int counter = 0;
    private DrawerLayout drawer;

    private int currentSelectedItem = -99;


    //constants for saving the selected state of navigation drawer
    private static final String CURRENTITEM = "currentkey";
    private static final String CURRENTLIST = "currentlist";
    private boolean expandable;


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the current position of navigation drawer and if on expandable list or not
        setSelectedItem(savedInstanceState.getInt(CURRENTLIST), savedInstanceState.getInt(CURRENTITEM));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = getApplicationContext();

        //open Meine Protokolle on start
        openMainFragement(MEINEPROTOKOLLE);


        init();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Log.d(TAG, "onCreate: Started.");

        //Views
        fabSpeedDial = (FabSpeedDial) findViewById(R.id.fabidnew);
        expandableListView = (ExpandableListView) findViewById(R.id.expandListNav);
        //preparing list Data
        prepareList();
        expandableListAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);


        //Adapter for List in Navigation Drawer
        navdrawerheader = new String[3];
        navdrawerheader[0] = "Meine Protokolle";
        navdrawerheader[1] = "Kalender";
        navdrawerheader[2] = "Einstellungen";
        navList = (ListView) findViewById(R.id.list_nav);


        navigationDrawerListAdapter = new NavigationDrawerListAdapter(MainActivity.this, navdrawerheader);
        if (getSelectedList() == 0) {
            navigationDrawerListAdapter.setPosition(NOTCHOOSEN);
            expandableListAdapter.setPosition(getSelectedItem());
        }
        //custom Position on start
        navigationDrawerListAdapter.setPosition(0);
        setSelectedItem(LISTVIEW, 0);

        navList.setAdapter(navigationDrawerListAdapter);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (getSelectedList() == 0) {
                    //expandable list was chosen -> must expand
                    expandableListView.expandGroup(0);
                } else {
                    expandableListView.collapseGroup(0);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

                if (getSelectedList() == 0) {
                    //expandable list was chosen -> set false position
                    navigationDrawerListAdapter.setPosition(NOTCHOOSEN);

                    navList.setAdapter(navigationDrawerListAdapter);
                    //set correct position to expandable list
                    //expandable list selected
                    expandableListAdapter.setPosition(currentSelectedItem);
                    expandableListView.setAdapter(expandableListAdapter);
                } else {
                    //normal list view chosen
                    navigationDrawerListAdapter.setPosition(getSelectedItem());
                    navList.setAdapter(navigationDrawerListAdapter);
                    expandableListAdapter.setPosition(NOTCHOOSEN);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {


            }
        });

        //handle click ofs expandable List with modules
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView textView = v.findViewById(R.id.lblListItem);
                //get the name of the selected Module
                String module = (String) textView.getText();
                openMainFragement(module);

                //save current site -> save if at expandable list
                setSelectedItem(LIST_EXPANDABLE, childPosition);
                //todo add click listener for adding moduls
                Log.d(TAG, "Child Count: " + expandableListView.getChildCount() + " Position: " + childPosition);
                if (childPosition == (expandableListView.getChildCount() - 2)) {
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment1 = ModulChooseFragment.newInstance();
                    fragmentTransaction.replace(R.id.fragment_container, fragment1);
                    fragmentTransaction.commit();
                }


                drawer.closeDrawer(GravityCompat.START);
                return true;

            }
        });


        //handle clicks of custom navigation Drawer Menu
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            android.app.Fragment fragment;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = new ProtocolFragment();
                Fragment fragment1 = ModulChooseFragment.newInstance();

                FragmentManager support = getSupportFragmentManager();
                FragmentTransaction fragmentTransactionsupport = support.beginTransaction();


                switch (position) {
                    case 0:


                        openMainFragement(MEINEPROTOKOLLE);
                        break;

                    case 1:
                        //my protocols pass userid -> so will be handled not as a modul in Main

                        Toast.makeText(getApplicationContext(), "USeer: " + position, Toast.LENGTH_SHORT).show();

                        break;

                    case 2:


                        break;


                }

                //save the current site, set to 0 if item from list is selected not from expandable list
                setSelectedItem(LISTVIEW, position);


                drawer.closeDrawer(GravityCompat.START);
            }
        });


    }

    /**
     * Prepares List for custom Navigation Menu
     */
    private void prepareList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<String, List<String>>();

        //adding header data
        listDataHeader.add("Modul auswählen");

        firebaseMethods = new FirebaseMethods(context);

        //Adding child Data
        final List<String> module = new ArrayList<>();

        //get all moduls from current user
        Query querygetModule = firebaseMethods.selectAllModulsOfCurrentUser();
        querygetModule.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                module.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //add every modul to Modulelist für expandable list view
                    String modul = snapshot.getKey();
                    module.add(modul);
                }

                //an letzte Stelle Eintrag mit Modul hinzufügen
                module.add("Modul hinzufügen...");
                listDataChild.put(listDataHeader.get(0), module);

                //set Adapter for expandable List
                expandableListAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
                expandableListView.setAdapter(expandableListAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void setSelectedItem(@ListType int list, int position) {
        this.selectedList = list;
        this.currentSelectedItem = position;
    }

    /**
     * returns the selected list ( expandable or listview)
     * 0 -> expandable
     * 1 -> normal list
     */
    private int getSelectedList() {
        return selectedList;
    }

    private int getSelectedItem() {
        return this.currentSelectedItem;
    }


    private void openMainFragement(String module) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        android.app.Fragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Module", module);
        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save position and curent list
        outState.putInt(CURRENTITEM, getSelectedItem());
        outState.putInt(CURRENTLIST, getSelectedList());


    }


    @Override
    public void onBackPressed() {
        counter++;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (counter > 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Nochmaliges Drücken beendet die App.", Toast.LENGTH_SHORT).show();
            }


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
        //settings
        if (id == R.id.action_settings) {

            return true;
        }
        if (id == R.id.action_signout) {
            mAuth.signOut();
            showSnackbar("Erfolgreich abgemeldet.", this.findViewById(android.R.id.content));
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_select_module) {
            // Handle the camera action
        } else if (id == R.id.nav_calendar) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_protocols) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Easy Method to show snackbar
     *
     * @param message to be shown
     */
    public void showSnackbar(String message, View view) {
        Snackbar.make(view.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * Sets up the activity
     */
    public void init() {

        Utils.getDatabase();
        mAuth = FirebaseAuth.getInstance();


    }


}

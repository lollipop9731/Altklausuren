package com.example.loren.altklausurenneu;

import android.app.Activity;

import android.app.ExpandableListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.loren.altklausurenneu.Utils.SampleCamera;
import com.example.loren.altklausurenneu.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.OnClick;
import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


//todo clear this interface chaos
    //todo delete all files on create -> thumbnails and full photos

    ListView listViewExam;
    FabSpeedDial fabSpeedDial;
    FirebaseAuth mAuth;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();


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
        //set Adapter for expandable List
        expandableListAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(expandableListAdapter);

        //Adapter for List in Navigation Drawer
        navdrawerheader = new String[3];
        navdrawerheader[0] = "Meine Protokolle";
        navdrawerheader[1] = "Kalender";
        navdrawerheader[2] = "Einstellungen";
        navList = (ListView) findViewById(R.id.list_nav);


        navigationDrawerListAdapter = new NavigationDrawerListAdapter(MainActivity.this, navdrawerheader);
        navList.setAdapter(navigationDrawerListAdapter);

        //handle click ofs expandable List with modules
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView textView = v.findViewById(R.id.lblListItem);
                //get the name of the selected Module
                String module = (String) textView.getText();
                openMainFragement(module);


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


                switch (position) {
                    case 0:

                        fragment = new MainFragment();
                        if (fragment != null) {
                            fragmentTransaction.replace(R.id.fragment_container, fragment);
                            fragmentTransaction.commit();

                        }


                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Position: " + position, Toast.LENGTH_SHORT).show();


                }


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

        //Adding child Data
        List<String> module = new ArrayList<>();
        module.add("Mobile Systeme");
        module.add("Mathe 12");
        module.add("Entrepreneurship");
        module.add("ERP-Systeme");
        module.add("Betriebssysteme");

        listDataChild.put(listDataHeader.get(0), module);
    }

    private void openMainFragement(String module){
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

       android.app.Fragment fragment = new MainFragment();
       Bundle bundle = new Bundle();
       bundle.putString("Module",module);
       fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();


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

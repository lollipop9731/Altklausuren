package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listViewExam;
    FabSpeedDial fabSpeedDial;

    private static final  String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Log.d(TAG, "onCreate: Started.");

        //Views

        fabSpeedDial = (FabSpeedDial)findViewById(R.id.fabidnew);

        //Listener for clicks of FAB
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            //handle clicks on miniFab here
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //Action here
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });



        //Exams for example
        Exam exam1 = new Exam("Mathematische Grundlagen 1", "SS 18 Probeklausur");
        Exam exam2 = new Exam("Mathematische Grundlagen 2", "SS 17 1. Zeitraum");
        Exam exam3 = new Exam("Mathematische Grundlagen 1", "WS 17/18 2. Zeitraum");
        Exam exam4 = new Exam("Mathematische Grundlagen 2", "SS 18 Gedächtnisprotokoll");

        // ArrayList to keep Exams
        final ArrayList<Exam> exams = new ArrayList<>();
        exams.add(exam1);
        exams.add(exam2);
        exams.add(exam3);
        exams.add(exam4);

        // Array Adapter for Custom ListView
        ExamListAdapter arrayAdapter = new ExamListAdapter(this,    exams );
        listViewExam = (ListView)findViewById(R.id.list_exams);
        listViewExam.setAdapter(arrayAdapter);



        listViewExam.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = exams.get(position).getName();
                Intent intent = new Intent(MainActivity.this,WebViewclass.class);
                startActivity(intent);
            }
        });








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
        //settings
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,Login.class);
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


}

package com.example.loren.altklausurenneu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listViewExam;
    FabSpeedDial fabSpeedDial;

    private static final  String TAG = "MainActivity";
    //code for ReadFile
    private static final int READ_REQUEST_CODE = 42;
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private DatabaseReference mDatabase;
    ExamListAdapter arrayAdapter;
    private ArrayList<Exam> exams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get current user
        mAuth = FirebaseAuth.getInstance();

        //get CloudStorage
        firebaseStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = firebaseStorage.getReference();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference("exams");




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
               switch(menuItem.getItemId()){
                   case R.id.menu_upload:
                       FileSearch();
                       break;
                   case R.id.menu_uploadtip:
                        writeNewExam("3","Mathematische Grundlagen 1","SS 2017","Gedächtnisprotokoll");
                        DialogNewExam();
                       break;
               }

                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });


        listViewExam = (ListView) findViewById(R.id.list_exams);


        mDatabase.addValueEventListener(getDataValueEvent());



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


    /**
     * Gets all Exams and set them to ListView with adapter
     * @return
     */
    private ValueEventListener getDataValueEvent(){
        //listen for changes of data and update UI
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // ArrayList to keep Exams
                exams = new ArrayList<Exam>();
                if(dataSnapshot.exists()){
                    //get all Exams
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Exam exam;
                        exam = snapshot.getValue(Exam.class);
                        Log.d(TAG, "User: " + exam.getName());
                        //add to list
                        exams.add(exam);

                    }
                }

                // Array Adapter for Custom ListView
                arrayAdapter = new ExamListAdapter(MainActivity.this, exams);
                listViewExam.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //getting exam failed -> log
                Log.d(TAG, "loadExam failed", databaseError.toException());
            }
        };

        return valueEventListener;

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

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
        if(id==R.id.action_signout){
            mAuth.signOut();
            showSnackbar("Erfolgreich abgemeldet.");
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

    /**
     * Easy Method to show snackbar
     * @param message to be shown
     */
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Upload byte[] to Firebase Cloud Storage
     * @param bytes file in bytes[[]
     * @param filename path of the file
     */
    private void uploadLocalFileFromPhone(byte[] bytes, final String filename){


        StorageReference klausurReference = storageRef.child(filename);
        uploadTask = klausurReference.putBytes(bytes);


        //Register if upload fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //handle failed uploads
                showSnackbar("Upload fehlgeschlagen");
                Log.d(TAG,"Upload fehgeschlagen, Path: "+filename);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //handle successful uploads
                showSnackbar("Upload erfolgreich!");
                Log.d(TAG,"Upload erfolgreich: " + filename);
            }
        });


    }

    /**
     * Opens the file explorer to choose the file to upload
     */
    private void FileSearch(){

        //choose a file
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        //Filter that only openable files are shown
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {"image/jpeg","application/pdf"};

        //sets the type first to all
        intent.setType("*/*");
        //apply new mimyTypes API ab 19, maybe other solution
        //todo new soltion here to support api <19
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);

        startActivityForResult(intent,READ_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //check if request code is correct
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //return intent contains a URI
            Uri uri = null;
            if(data !=null){
                uri = data.getData();
                //get path from selected file
                String filename = uri.getLastPathSegment();
                Log.d(TAG,"Uri: " + uri.toString());

                //convert to byte[] and upload to cloud storage
                try{

                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    try{
                        byte[] bytes = getBytes(inputStream);
                        Log.d(TAG,"Erfolgreich zu Bytes gewandelt.");
                        uploadLocalFileFromPhone(bytes,filename);

                    }catch (IOException e){
                        //do nothing
                    }
                }catch (FileNotFoundException e){
                    Log.d(TAG," File not found, cannot convert to BYTE []");
                    showSnackbar("File not found");
                }


            }
        }
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public void writeNewExam(String examid, String name, String semester, String category) {
        Exam exam = new Exam(name, semester, category);
        mDatabase.child(examid).setValue(exam)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "erfolgreich beschrieben.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Upload fehlgeschlagen:" + e.getMessage());
            }
        });


    }

    public void DialogNewExam(){
        //todo Dialog schöner machen
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogview = layoutInflater.inflate(R.layout.dialogfragment_newexam,null);

        final EditText name = (EditText)dialogview.findViewById(R.id.dialog_exam_name);
        final EditText category = (EditText)dialogview.findViewById(R.id.dialog_exam_category);
        final EditText semester = (EditText)dialogview.findViewById(R.id.dialog_exam_semester);
        final EditText id = (EditText)dialogview.findViewById(R.id.dialog_exam_id);


        dialog.setButton(Dialog.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setButton(Dialog.BUTTON_POSITIVE, "Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!name.getText().toString().equals("")&&!category.getText().toString().equals("")&&!semester.getText().toString().equals("")&&!id.getText().toString().equals("")){
                    writeNewExam(id.getText().toString(),name.getText().toString(),semester.getText().toString(),category.getText().toString());
                }else{
                    //todo Error setzen für EditText
                    showSnackbar("Alle Felder ausfüllen");
                }



            }
        });

        dialog.setCancelable(true);

        dialog.setView(dialogview);
        dialog.show();

    }
}

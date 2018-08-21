package com.example.loren.altklausurenneu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
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
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listViewExam;
    FabSpeedDial fabSpeedDial;

    private static final  String TAG = "MainActivity";
    private static final String DATABASE_CATEGORY = "category";
    private static final String DATABASE_SEMESTER = "semester";
    private static final String DATABASE_NAME = "name";
    private static final String DOWNLOAD_URL_BUNDLE = "downloadurl";

    //code for ReadFile
    private static final int READ_REQUEST_CODE = 42;
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private DatabaseReference mDatabase;
    private static FirebaseDatabase database;
    ExamListAdapter arrayAdapter;
    private ArrayList<Exam> exams;
    private FirebaseMethods firebaseMethods;
    private Exam exam;

    //boolean to keep track of dialog status
    private Boolean dialog_shown;
    private byte[] bytes;
    private String filename;
    private Uri fileData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();



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
               switch(menuItem.getItemId()){
                   case R.id.menu_upload:
                       exam.setUploaded(false);
                       FileSearch();
                       break;
                   case R.id.menu_uploadtip:
                       showDialog();
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



        //Listview handle clicks
        listViewExam.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       Exam downloadurl = exams.get(position);

                        Log.d(TAG,"DownloadURL: "+ downloadurl.getFilepath());
                        firebaseMethods.getFileFromDatabase(exams.get(position).getFilepath());
                        Log.d(TAG,"Filename: "+exam.getFilepath());


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
                        Log.d(TAG, "User: " + exam.getFilepath());
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


        final StorageReference klausurReference = storageRef.child("pdf/"+filename);
        uploadTask = klausurReference.putBytes(bytes);


        //Register if upload fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //handle failed uploads
                showSnackbar("Upload fehlgeschlagen");
                Log.d(TAG,"Upload fehgeschlagen, Path: "+filename);

            }//todo set progress of upload to snackbar
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //handle successful uploads
                showSnackbar("Upload erfolgreich!");
                Log.d(TAG,"Upload erfolgreich: " + filename);
                //set filepath to current exam
                klausurReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        exam.setFilepath(klausurReference.getDownloadUrl().toString());
                        Log.d(TAG,"Download URL: "+klausurReference.getDownloadUrl().toString());
                    }
                });


                //exam uploaded -> ready to upload to databas
               if(dialog_shown){
                   firebaseMethods.uploadNewExam(exam);
                   Log.d(TAG,"Dialog gezeigt");
                   dialog_shown=false;
               }




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

    /**
     *
     * @return bytes of chosen file
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     *
     * @return filename of local file
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Result from FileSearch
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //check if request code is correct
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //return intent contains a URI
            Uri uri = null;
            if(data !=null){
                uri = data.getData();
                setFileData(uri);
                showDialog();







            }else{
                Log.d(TAG,"File not found: ");
            }
        }
    }

    private void setBytesAndFile(byte[] bytes, String filename) {
        this.bytes = bytes;
        this.filename = filename;
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

    public void showDialog(){
        final NewExamDialog examDialog = DialogFactory.makeExamDialog(R.string.dialog_title,
                R.string.dialog_button, R.array.category, new NewExamDialog.ButtonDialogAction() {

                    @Override
                    public void onDialogClicked(String category, String semester) {
                        exam.setCategory(category);
                        exam.setSemester(semester);

                        dialog_shown = true;
                        Log.d(TAG,"Dialog wurde gezeigt.");

                        firebaseMethods.uploadFileToStorage(getFileData());
                        firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                            @Override
                            public void onUploadSuccess(String filepath) {
                                Log.d(TAG,"Upload erfolgreich");
                                exam.setFilepath(filepath);
                                firebaseMethods.uploadNewExam(exam);
                            }
                        });





                    }
                });

        examDialog.show(getFragmentManager(),NewExamDialog.TAG);


    }

    /**
     * Sets up the activity
     */
    public void init(){
        dialog_shown = false;



        exam = new Exam();
        getDatabase();

        firebaseMethods = new FirebaseMethods(getApplicationContext());

        //get current user
        mAuth = FirebaseAuth.getInstance();
        if(mAuth!=null){
            String user= mAuth.getCurrentUser().getUid();
            exam.setUserid(user);
        }

        //get CloudStorage
        firebaseStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = firebaseStorage.getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference("exams");



    }




    public static FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            // ...
        }

        return database;

    }


    public void setFileData(Uri fileData) {
        this.fileData = fileData;
    }

    public Uri getFileData() {
        return this.fileData;
    }
}

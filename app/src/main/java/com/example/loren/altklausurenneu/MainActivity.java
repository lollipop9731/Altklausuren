package com.example.loren.altklausurenneu;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;




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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FirebaseMethods.FireBaseMethodsInter {


//todo clear this interface chaos
    //todo delete all files on create -> thumbnails and full photos

    ListView listViewExam;
    FabSpeedDial fabSpeedDial;

    private static final String TAG = "MainActivity";
    private static final String DATABASE_CATEGORY = "category";
    private static final String DATABASE_SEMESTER = "semester";
    private static final String DATABASE_NAME = "name";
    private static final String DOWNLOAD_URL_BUNDLE = "downloadurl";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_PHOTO_PIX = 2;
    private static final String BUNDLE_DOWNLOAD_URL = "url";

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

    Context context;
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    private Exam exam;
    private Snackbar snackbar;
    private View rootview;


    private Uri fileData;
    private String mCurrentPhotoPath;





    //checks if File is downloaded
    private Boolean downloaded;

    FirebaseMethods.FireBaseMethodsInter fireBaseMethodsInter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        rootview = findViewById(android.R.id.content);

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
        fabSpeedDial = (FabSpeedDial) findViewById(R.id.fabidnew);

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
                switch (menuItem.getItemId()) {
                    //upload file from phone
                    case R.id.menu_upload:

                        FileSearch();
                        break;

                    //take photo
                    case R.id.menu_uploadtip:
                       Intent intent = new Intent(MainActivity.this,PhotoViewer.class);
                       startActivity(intent);
                       // TakePictureIntent();

                        break;
                }

                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });


        listViewExam = (ListView) findViewById(R.id.list_exams);

        //declare firebase method, set interface
        firebaseMethods = new FirebaseMethods(getApplicationContext());
        fireBaseMethodsInter = this;
        firebaseMethods.setMethodsInter(fireBaseMethodsInter);


        mDatabase.addValueEventListener(getDataValueEvent());


        //Listview handle clicks
        listViewExam.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Exam current = exams.get(position);
                        Intent intent = new Intent(MainActivity.this, PdfViewer.class);
                        Log.d(TAG,"Current Download: "+ current.getDownloadurl());
                        intent.putExtra(BUNDLE_DOWNLOAD_URL,current.getDownloadurl());
                        //todo open intent with glide, if not pdf but jpg
                        startActivity(intent);




                    }
                });


    }


    /**
     * Gets all Exams and set them to ListView with adapter
     *
     * @return
     */
    private ValueEventListener getDataValueEvent() {
        //listen for changes of data and update UI
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // ArrayList to keep Exams
                exams = new ArrayList<Exam>();
                if (dataSnapshot.exists()) {
                    //get all Exams
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Exam exam;
                        exam = snapshot.getValue(Exam.class);
                        Log.d(TAG, "User: " + exam.getDownloadurl());

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
    public void onUploadSuccess(String filepath,String download) {

    }

    @Override
    public void onDownloadSuccess(Boolean downloaded) {
        Log.d(TAG,"interface fired");
        if(downloaded){
            snackbar.dismiss();
            Log.d(TAG,"File successfully downloaded");
        }else{
            Log.d(TAG,"File could not be downloaded.");
            showSnackbar("Datei konnte nicht heruntergeladen werden.",rootview);
        }

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


    /**
     * Opens the file explorer to choose the file to upload
     */
    private void FileSearch() {

        //choose a file
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        //Filter that only openable files are shown
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {"image/jpeg", "application/pdf"};

        //sets the type first to all
        intent.setType("*/*");

        //todo new soltion here to support api <19
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, READ_REQUEST_CODE);


    }


    /**
     * Result from FileSearch
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //check if request code is correct
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //return intent contains a URI
            Uri uri = null;
            if (data != null) {
                uri = data.getData();

                //get mime Type of selected file -> Jpeg or pdf
                ContentResolver cR = this.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String type = mime.getExtensionFromMimeType(cR.getType(uri));
                Log.d(TAG, "Mime Typ: " + type);

                exam.setFilepath("." + type);
                setFileData(uri);
                showDialog();

            } else {
                Log.d(TAG, "File not found: ");
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {


                Log.d(TAG,"Photo file:" +mCurrentPhotoPath);
                File file = new File(mCurrentPhotoPath);
                Uri uri = Uri.fromFile(file);

            // new try :
                Intent intent = new Intent(getApplicationContext(),PhotoViewer.class);
                intent.setData(uri);
                startActivity(intent);



                Log.d(TAG,"Current Uri: "+uri.toString());

                //todo maybe uncomment!!
              /*  setFileData(uri);
                exam.setFilepath(".jpg");
                showDialog();*/

        }
    }


    public void showDialog()    {

        if(exam.getFilepath().contains(".pdf")){
            final NewExamDialog examDialog = DialogFactory.makePDFExamDialog(R.string.dialog_title,
                    R.string.dialog_button, R.array.category, new NewExamDialog.ButtonDialogAction() {

                        @Override
                        public void onDialogClicked(String category, final String semester) {
                            exam.setCategory(category);
                            exam.setSemester(semester);



                            Log.d(TAG, "Dialog wurde gezeigt.");


                            //upload chosen file to storage
                            firebaseMethods.uploadFileToStorageNEW(getFileData(),exam.getFilepath());

                            showProgressSnackbar("Datei wird hochgeladen...");


                            firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                                @Override
                                public void onUploadSuccess(String filepath, String downloadurl) {
                                    if(filepath.equals("Fail")){
                                        snackbar.dismiss();
                                        showSnackbar("Upload fehlgeschlagen",rootview);
                                    }else{
                                        Log.d(TAG, "Upload erfolgreich");
                                        //set Filepath and Download URL if upload was successful, get from FireBaseMethods
                                        exam.setFilepath(filepath);
                                        exam.setDownloadurl(downloadurl);
                                        Log.d(TAG, "Download url set: "+ exam.getDownloadurl());

                                        //if upload was successfull write new Databaseentry
                                        firebaseMethods.uploadNewExam(exam);
                                        snackbar.dismiss();
                                    }

                                }

                                //todo delete and use other method
                                @Override
                                public void onDownloadSuccess(Boolean downloaded) {

                                }
                            });


                        }
                    });

            examDialog.show(getFragmentManager(), NewExamDialog.TAG);
        }
        if(exam.getFilepath().contains(".jpg")){
            final NewExamDialog examDialog = DialogFactory.makeJPEGExamDialog(R.string.dialog_title,
                    R.string.dialog_button, R.array.category, new NewExamDialog.ButtonDialogAction() {

                        @Override
                        public void onDialogClicked(String category, final String semester) {
                            exam.setCategory(category);
                            exam.setSemester(semester);


                            Log.d(TAG, "Dialog wurde gezeigt.");


                            //upload chosen file to storage
                            firebaseMethods.uploadFileToStorageNEW(getFileData(), exam.getFilepath());

                            showProgressSnackbar("Datei wird hochgeladen...");


                            firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                                @Override
                                public void onUploadSuccess(String filepath,String download) {
                                    //set Filepath and Download URL if upload was successful, get from FireBaseMethods
                                    exam.setFilepath(filepath);
                                    exam.setDownloadurl(download);
                                    Log.d(TAG, "Download url set: "+ exam.getDownloadurl());

                                    //if upload was successfull write new Databaseentry
                                    firebaseMethods.uploadNewExam(exam);
                                    snackbar.dismiss();
                                }

                                //todo delete and use other method
                                @Override
                                public void onDownloadSuccess(Boolean downloaded) {

                                }
                            });


                        }
                    });

            examDialog.show(getFragmentManager(), NewExamDialog.TAG);
        }



    }

    /**
     * Sets up the activity
     */
    public void init() {

        downloaded = false;


        exam = new Exam();
        getDatabase();

        firebaseMethods = new FirebaseMethods(getApplicationContext());

        //get current user
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            String user = mAuth.getCurrentUser().getUid();
            exam.setUserid(user);
        }

        //get CloudStorage
        firebaseStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = firebaseStorage.getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference("exams");


    }

    /**
     * shows Snackbar with progress turning circle
     * @param text Message to be displayed in Snackbar
     */
    public void showProgressSnackbar(String text) {
        snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(getApplicationContext());
        contentLay.addView(item, 0);
        snackbar.show();
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

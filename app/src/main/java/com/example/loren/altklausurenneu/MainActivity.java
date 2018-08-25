package com.example.loren.altklausurenneu;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.loren.altklausurenneu.Utils.ExpandListModel;
import com.example.loren.altklausurenneu.Utils.ExpandableListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FirebaseMethods.FireBaseMethodsInter {




    ListView listViewExam;
    FabSpeedDial fabSpeedDial;

    private static final String TAG = "MainActivity";
    private static final String DATABASE_CATEGORY = "category";
    private static final String DATABASE_SEMESTER = "semester";
    private static final String DATABASE_NAME = "name";
    private static final String DOWNLOAD_URL_BUNDLE = "downloadurl";
    static final int REQUEST_TAKE_PHOTO = 1;

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

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    private Exam exam;
    private Snackbar snackbar;
    private View rootview;


    private Uri fileData;
    private String mCurrentPhotoPath;

    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandListModel> listDataHeader;
    HashMap<ExpandListModel, List<String>> listDataChild;



    //checks if File is downloaded
    private Boolean downloaded;

    FirebaseMethods.FireBaseMethodsInter fireBaseMethodsInter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
                    case R.id.menu_upload:

                        FileSearch();
                        break;
                    case R.id.menu_uploadtip:
                        TakePictureIntent();
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

                        Log.d(TAG, "Filepath: " + current.getFilepath());

                        //set interface again, wasnt called properly
                        firebaseMethods.setMethodsInter(fireBaseMethodsInter);
                        showProgressSnackbar("Datei wird heruntergeladen...");

                        firebaseMethods.getFileFromDatabase(current.getFilepath());


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
    public void onUploadSuccess(String filepath) {

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
                Log.d(TAG,"Current Uri: "+uri.toString());

                setFileData(uri);
                exam.setFilepath(".jpg");
                showDialog();

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
                            firebaseMethods.uploadFileToStorage(getFileData(), exam.getFilepath());

                            showProgressSnackbar("Datei wird hochgeladen...");


                            firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                                @Override
                                public void onUploadSuccess(String filepath) {
                                    if(filepath.equals("Fail")){
                                        snackbar.dismiss();
                                        showSnackbar("Upload fehlgeschlagen",rootview);
                                    }else{
                                        Log.d(TAG, "Upload erfolgreich");
                                        exam.setFilepath(filepath);

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
                            firebaseMethods.uploadFileToStorage(getFileData(), exam.getFilepath());

                            showProgressSnackbar("Datei wird hochgeladen...");


                            firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                                @Override
                                public void onUploadSuccess(String filepath) {
                                    Log.d(TAG, "Upload erfolgreich");
                                    exam.setFilepath(filepath);

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

    private File createImageFile() throws IOException{

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void TakePictureIntent(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG,"File for image could not be created: "+ex.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d(TAG,"File for image successfully created.");
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID,
                        photoFile);
                //get uri for photo for content provider
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


}

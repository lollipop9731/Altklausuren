package com.example.loren.altklausurenneu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loren.altklausurenneu.Utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;

import static android.app.Activity.RESULT_OK;


public class MainFragment extends android.app.Fragment implements FirebaseMethods.FireBaseMethodsInter {

    ListView listViewExam;

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
    NavigationDrawerListAdapter navigationDrawerListAdapter;
    private ArrayList<Exam> exams;
    private FirebaseMethods firebaseMethods;
    private String mCurrentPhotoPath;

    private Exam exam;
    private Snackbar snackbar;
    private View rootview;

    FabSpeedDial fabSpeedDial;


    private Uri fileData;
    private String module;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    FirebaseMethods.FireBaseMethodsInter fireBaseMethodsInter;
    private Context context;

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    public void setFileData(Uri fileData) {
        this.fileData = fileData;
    }

    public Uri getFileData() {
        return this.fileData;
    }


    /**
     * Result from FileSearch
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //check if request code is correct
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            //return intent contains a URI
            Uri uri = null;
            if (data != null) {
                uri = data.getData();

                //get mime Type of selected file -> Jpeg or pdf
                ContentResolver cR = getActivity().getContentResolver();
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


            Log.d(TAG, "Photo file:" + mCurrentPhotoPath);
            File file = new File(mCurrentPhotoPath);
            Uri uri = Uri.fromFile(file);

            // new try :
            Intent intent = new Intent(getActivity(), CameraViewer.class);
            intent.setData(uri);
            startActivity(intent);


            Log.d(TAG, "Current Uri: " + uri.toString());


        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_main, container, false);


        context = getActivity();

        rootview = getActivity().findViewById(android.R.id.content);
        //Views
        fabSpeedDial = (FabSpeedDial) view.findViewById(R.id.fabidnew);
        init();

        //get module from bundle, which is send from MainActivity
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            setModule(bundle.getString("Module"));


        }

        //set the action bar title with the current module
        if (getModule() != null) {

            ((MainActivity) getActivity()).setActionBarTitle(getModule());
        }


        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.context_edit:
                //todo method for edit name
                return true;
            case R.id.context_delete:
                Exam exame_delete = exams.get(info.position);
                deleteExam(exame_delete.getDownloadurl());

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * @param downloadurl URL of exam to be deleted
     */
    private void deleteExam(final String downloadurl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                .setTitle("Endgültig löschen")
                .setMessage("Soll das Element gelöscht werden? Dies kann nicht rückgängig gemacht werden.")
                .setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                    //Element löschen
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Query query = firebaseMethods.selectExamByChild("downloadurl", downloadurl);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    //iterate through all children and delete the wanted
                                    snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showSnackbar("Erfolgreich gelöscht", rootview);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        listViewExam = (ListView) view.findViewById(R.id.list_exams);

        //declare firebase method, set interface
        firebaseMethods = new FirebaseMethods(getActivity());
        fireBaseMethodsInter = this;

        firebaseMethods.setMethodsInter(fireBaseMethodsInter);


        if (getModule().equals(MainActivity.USERID)) {
            //own protocolls
            Query query = firebaseMethods.selectAllExamsFromUser(mAuth.getUid());
            query.addValueEventListener(getDataValueEvent());
            //proide context menu for edit
            registerForContextMenu(listViewExam);


        } else {
            Query query = firebaseMethods.selectExamByChild("name", getModule());
            query.addValueEventListener(getDataValueEvent());
        }


        // mDatabase.addValueEventListener(getDataValueEvent());
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
                        Intent intent = new Intent(getActivity(), CameraViewer.class);

                        startActivity(intent);


                        break;
                }

                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });


        //Listview handle clicks
        listViewExam.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Exam current = exams.get(position);
                        Intent intent = new Intent(getActivity(), PdfViewer.class);
                        Log.d(TAG, "Current Download: " + current.getDownloadurl());
                        intent.putExtra(BUNDLE_DOWNLOAD_URL, current.getDownloadurl());
                        //todo open intent with glide, if not pdf but jpg
                        startActivity(intent);


                    }
                });


    }

    /**
     * Sets up the activity
     */
    public void init() {


        //delete all temp pictures if some are left
        File dir = new File(getActivity().getExternalFilesDir("images/temp").toString());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                Boolean deleted = new File(dir, children[i]).delete();
                Log.d(TAG, "Files on created deleted: " + deleted);
            }
        }

        //todo handle persistence
        // Utils.getDatabase();
        exam = new Exam();


        firebaseMethods = new FirebaseMethods(getActivity());

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
                        Log.d(TAG, "com.example.loren.altklausurenneu.User: " + exam.getDownloadurl());

                        //add to list
                        exams.add(exam);

                    }
                }

                // Array Adapter for Custom ListView
                arrayAdapter = new ExamListAdapter(context, exams);
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
    public void onUploadSuccess(String filepath, String downloadurl) {

    }

    @Override
    public void onDownloadSuccess(Boolean downloaded) {
        Log.d(TAG, "interface fired");
        if (downloaded) {
            snackbar.dismiss();
            Log.d(TAG, "File successfully downloaded");
        } else {
            Log.d(TAG, "File could not be downloaded.");
            showSnackbar("Datei konnte nicht heruntergeladen werden.", rootview);
        }
    }

    /**
     * Easy Method to show snackbar
     *
     * @param message to be shown
     */
    public void showSnackbar(String message, View view) {
        Snackbar.make(view.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    public void showDialog() {

        if (exam.getFilepath().contains(".pdf")) {
            final NewExamDialog examDialog = DialogFactory.makePDFExamDialog(getModule(),
                    R.string.dialog_button, R.array.category, new NewExamDialog.ButtonDialogAction() {

                        @Override
                        public void onDialogClicked(String category, final String semester) {
                            exam.setCategory(category);
                            exam.setSemester(semester);


                            Log.d(TAG, "Dialog wurde gezeigt.");


                            //upload chosen file to storage
                            firebaseMethods.uploadFileToStorageNEW(getFileData(), exam.getFilepath());

                            showProgressSnackbar("Datei wird hochgeladen...", rootview);


                            firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                                @Override
                                public void onUploadSuccess(String filepath, String downloadurl) {
                                    if (filepath.equals("Fail")) {
                                        snackbar.dismiss();
                                        showSnackbar("Upload fehlgeschlagen", rootview);
                                    } else {
                                        Log.d(TAG, "Upload erfolgreich");
                                        //set Filepath and Download URL if upload was successful, get from FireBaseMethods
                                        exam.setFilepath(filepath);
                                        exam.setDownloadurl(downloadurl);
                                        exam.setName(getModule());
                                        Log.d(TAG, "Download url set: " + exam.getDownloadurl());

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
        //todo dont need jpg anymore
        if (exam.getFilepath().contains(".jpg")) {
            final NewExamDialog examDialog = DialogFactory.makeJPEGExamDialog(getModule(),
                    R.string.dialog_button, R.array.category, new NewExamDialog.ButtonDialogAction() {

                        @Override
                        public void onDialogClicked(String category, final String semester) {
                            exam.setCategory(category);
                            exam.setSemester(semester);


                            Log.d(TAG, "Dialog wurde gezeigt.");


                            //upload chosen file to storage
                            firebaseMethods.uploadFileToStorageNEW(getFileData(), exam.getFilepath());

                            showProgressSnackbar("Datei wird hochgeladen...", rootview);


                            firebaseMethods.setMethodsInter(new FirebaseMethods.FireBaseMethodsInter() {
                                @Override
                                public void onUploadSuccess(String filepath, String download) {
                                    //set Filepath and Download URL if upload was successful, get from FireBaseMethods
                                    exam.setFilepath(filepath);
                                    exam.setDownloadurl(download);
                                    Log.d(TAG, "Download url set: " + exam.getDownloadurl());

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
     * @param child    the child to be updated (name -> for name)
     * @param newvalue the new String value
     * @param oldvalue old value that has to be updated
     */
    public void updateName(final String child, final String newvalue, String oldvalue) {
        Query querymath = firebaseMethods.selectExamByChild(child, oldvalue);
        querymath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //iterate through all children
                    snapshot.getRef().child(child).setValue(newvalue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Updating name failed: " + databaseError.getMessage());
            }
        });
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
     * shows Snackbar with progress turning circle
     *
     * @param text Message to be displayed in Snackbar
     */
    public void showProgressSnackbar(String text, View rootview) {
        snackbar = Snackbar.make(rootview.findViewById(android.R.id.content), text, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(getActivity());
        contentLay.addView(item, 0);
        snackbar.show();
    }
}

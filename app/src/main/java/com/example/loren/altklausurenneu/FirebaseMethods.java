package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

    public class FirebaseMethods  {

        private Context context;
        private File localFile;
        StorageReference sRef;

        public FirebaseMethods(Context context) {
            this.context = context;
        }

        private static final String DATABASE_CATEGORY = "category";
    private static final String DATABASE_SEMESTER = "semester";
    private static final String DATABASE_NAME = "name";
    private static final String DATABASE_FILEPATH = "filepath";
    private static final String DATABASE_UID = "user_id";
    private static final String TAG = "FirebaseDatase";
    private UploadTask uploadTask;


    private FireBaseMethodsInter methodsInter;
    private getCurrentProgress getCurrentProgressInter;

    public void setGetCurrentProgressInter(getCurrentProgress getCurrentProgressInter) {
            this.getCurrentProgressInter = getCurrentProgressInter;
        }

        private static FirebaseDatabase database;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;


    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private StorageReference Reference;
    private DatabaseReference databaseReference = getDatabase().getReference("exams");

    public void setMethodsInter(FireBaseMethodsInter methodsInter) {
        this.methodsInter = methodsInter;
    }



    private static FirebaseDatabase getDatabase(){
        if ( database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            // ...
        }

        return database;
    }

    //todo delete filepath -> we only need name to get pdf

        /**
         * Writes new DB Entry with the details about the new exam
         * @param exam Exam to be uploaded
         */
    public void uploadNewExam(Exam exam){


        //todo get real name
        exam.setName("Mathe 12");


        Map<String, String> examneu = new HashMap<>();
        examneu.put(DATABASE_CATEGORY,exam.getCategory());
        examneu.put(DATABASE_NAME,exam.getName());
        examneu.put(DATABASE_SEMESTER,exam.getSemester());
        examneu.put(DATABASE_FILEPATH,exam.getFilepath());
        examneu.put(DATABASE_UID,exam.getUserid());

        databaseReference.push().setValue(examneu).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Erfolgreich hochgeladen");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Upload fehlgeschlagen:" + e.getMessage());
            }
        });



    }

    //todo progress listener for upload in snackbar
    public void uploadFileToStorage(Uri data,String type){


    //todo change name not always current time Millis, must be unique




        if(type.contains(".pdf")) {
            sRef = storageReference.child("pdf/" + System.currentTimeMillis() + ".pdf");
            Log.d(TAG,"Current Mime Type: PDF");
        }else{
            if(type.contains(".jpg")){
                sRef = storageReference.child("jpg/" + System.currentTimeMillis() + ".jpg");
                Log.d(TAG,"Current Mime Type: JPEG");
            }else{Log.d(TAG,"Unknow Mime Type");
                return;
            }
        }
        uploadTask = sRef.putFile(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //todo maybe use Metadata here for file -> better solution then mine
                Log.d(TAG,"Upload über neuen Task erfolgreich:  "+sRef.getName());
                if(methodsInter !=null){
                    //if upload of file was successful, pass the name of the uploaded file to Interface
                    methodsInter.onUploadSuccess(sRef.getName());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Upload war nicht erfolgreich");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //Interface to send Progress to Main
                getCurrentProgressInter.onProgress(progress);



            }
        });




    }

    public void getFileFromDatabase(final String path){



        //open PDF
        if(path.contains(".pdf")){
            Reference = storageReference.child("pdf/"+path);
            Log.d(TAG,"Getting PDF-File");
        }else{
            //open JPEG
            if(path.contains(".jpg")){
                Reference = storageReference.child("jpg/"+path);
                Log.d(TAG,"Getting JPEG-File");
            }else{
                Log.d(TAG,"File no jpg or pdf. Returns ");
                return;
            }
        }





        try{
            localFile = File.createTempFile("tempexam", ".pdf");
        }catch (IOException e){
            Log.d(TAG,e.getMessage());
            return;
        }


        Reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d(TAG,"Local temp file has been created.");

                Intent intent = new Intent(Intent.ACTION_VIEW);

                //set flags to give temporarily permission for external app to read my files
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //generate Uri -> why to string?
               Uri uri = FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID,localFile);

                //set type for pdf
                if(path.contains(".pdf")){
                    intent.setDataAndType(uri,"application/pdf");
                    Log.d(TAG,"Intent Type pdf chosen");
                }

                //set type for jpeg
                if(path.contains(".jpg")){
                    intent.setDataAndType(uri,"image/jpeg");
                    Log.d(TAG,"Intent Type JPEG chosen");
                }


                // validate that the device can open your File!
                PackageManager pm = context.getPackageManager();
                if (intent.resolveActivity(pm) != null) {
                    Log.d(TAG,"Application is there");
                    context.startActivity(intent);
                }else{
                    Log.d(TAG,"No Application to open file.");
                }




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public interface FireBaseMethodsInter{
            void onUploadSuccess(String filepath);
    }

    public interface getCurrentProgress{
            void onProgress(double progress);
    }





}
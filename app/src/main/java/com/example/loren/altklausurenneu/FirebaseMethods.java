package com.example.loren.altklausurenneu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;


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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

    public class FirebaseMethods {

        private Context context;
        private File localFile;

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


    private static FirebaseDatabase database;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;


    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
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

    public void uploadNewExam(Exam exam){



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

    //todo upload jpeg
    public void uploadFileToStorage(Uri data){



        final StorageReference sRef = storageReference.child("pdf/" + System.currentTimeMillis() + ".pdf");
        sRef.putFile(data).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                //continue with task to get download url
                return sRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Upload über neuen Task erfolgreich: "+sRef.getName());


                    if(methodsInter !=null){
                        methodsInter.onUploadSuccess(sRef.getName());
                    }
                }
            }
        });








                /*.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.d(TAG,"Upload erfolgreich: "+sRef.getName());


                        if(methodsInter !=null){
                            methodsInter.onUploadSuccess(storageReference.getDownloadUrl().toString());
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG,"Upload nicht erfolgreich");
                    }
                });*/

    }

    public void getFileFromDatabase(String path){
        StorageReference Reference = storageReference.child("pdf/"+path);


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

                //set type
                intent.setDataAndType(uri,"application/pdf");

                // validate that the device can open your File!
                PackageManager pm = context.getPackageManager();
                if (intent.resolveActivity(pm) != null) {
                    Log.d(TAG,"Application is there");
                    context.startActivity(intent);
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





}

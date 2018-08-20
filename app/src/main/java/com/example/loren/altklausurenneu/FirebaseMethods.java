package com.example.loren.altklausurenneu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMethods {

    private static final String DATABASE_CATEGORY = "category";
    private static final String DATABASE_SEMESTER = "semester";
    private static final String DATABASE_NAME = "name";
    private static final String DATABASE_FILEPATH = "filepath";
    private static final String DATABASE_UID = "user_id";
    private static final String TAG = "FirebaseDatase";



    private static FirebaseDatabase database;

    private FirebaseStorage firebaseStorage;

    private StorageReference storageReference;
    private DatabaseReference databaseReference = getDatabase().getReference("exams");


    public static FirebaseDatabase getDatabase(){
        if ( database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            // ...
        }

        return database;
    }

    public void uploadNewExam(String name,String semester, String category, String UID, String filepath){
        Exam exam = new Exam(name,semester,category,UID,filepath);

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


}

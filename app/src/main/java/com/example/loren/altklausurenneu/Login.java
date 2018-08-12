package com.example.loren.altklausurenneu;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText email, password;
    Button sign_in_btn, sign_out_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //UI Reference
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pw);
        sign_in_btn = (Button) findViewById(R.id.signin);
        sign_out_btn = (Button) findViewById(R.id.signout);


        mAuth = FirebaseAuth.getInstance();

        //Checks the loginstate
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in
                    Log.d(TAG, "signed in " + user.getUid());
                    toastMessage("Eingeloggt mit: " + user.getEmail());
                } else {
                    //User is signed out
                    Log.d(TAG, "Signed out");
                    toastMessage("Erfolgreich ausgeloggt");
                }
            }
        };

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailstring = email.getText().toString();
                String pwstring = password.getText().toString();

                //dont proceed with empty field
                if(!emailstring.equals("")&&!pwstring.equals("")){
                    mAuth.signInWithEmailAndPassword(emailstring,pwstring);
                }else{
                    toastMessage("Bitte beide Felder ausfüllen.");
                }
            }
        });

        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
    }




    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String toast){
        Toast.makeText(getApplicationContext(), toast,Toast.LENGTH_SHORT).show();
    }

}
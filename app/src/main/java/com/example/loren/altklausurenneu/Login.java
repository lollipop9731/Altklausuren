package com.example.loren.altklausurenneu;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String mEmailLink;
    private TextView mStatusText;

    private EditText emailfield, password;
    Button sign_in_btn, sign_out_btn,sendlink;
    String mPendingEmail;


    private static final String KEY_PENDING_EMAIL = "key_pending_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();





        //UI Reference
        emailfield = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pw);
        sign_in_btn = (Button) findViewById(R.id.signin);
        sendlink = (Button)findViewById(R.id.sendlink_btn);
        mStatusText = (TextView)findViewById(R.id.status);

        // Check if the Intent that started the Activity contains an email sign-in link.
         checkIntent(getIntent());

        // Restore the "pending" email address -> if there is one
        if (savedInstanceState != null) {
            mPendingEmail = savedInstanceState.getString(KEY_PENDING_EMAIL, null);
            emailfield.setText(mPendingEmail);
        }







        //Test signin only with emailfield without password
        sendlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onSendLinkClicked();
            }
        });

        //sign in clicked
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackbar("Klogin");
                onSignInClicked();


            }
        });






    }




    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null) {
            mStatusText.setText(mAuth.getCurrentUser().getEmail());
        }

        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PENDING_EMAIL, mPendingEmail);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * Checks if emailfield is not empty
     */
    private void onSendLinkClicked() {

        String emailstring = emailfield.getText().toString();
        if (TextUtils.isEmpty(emailstring)) {
            emailfield.setError("Bitte Feld ausfüllen.");
            return;
        }

        sendSignInLink(emailstring);
    }

    private void sendSignInLink(final String email) {
        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(
                        getPackageName(),
                        false, /* install if not available? */
                        null   /* minimum app version */)
                .setHandleCodeInApp(true)
                .setUrl("http://www.example.com/altklausuren")
                .build();



       // hideKeyboard(mEmailField);
        // showProgressDialog();

        mAuth.sendSignInLinkToEmail(email, settings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       // hideProgressDialog();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Link sent");

                            mPendingEmail = email;
                            showSnackbar("Link verschickt, bitte überprüfe deinen Posteingang!");
                        } else {
                            Exception e = task.getException();
                            Log.w(TAG, "Could not send link", e);
                            showSnackbar("Link konnte nicht verschickt werden.");

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                emailfield.setError("Invalid emailfield address.");
                            }
                        }
                    }
                });
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void onSignInClicked() {
        String email = emailfield.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailfield.setError("Bitte Feld ausfüllen.");
            return;
        }

        signInWithEmailLink(email, mEmailLink);
    }

    /**
     * Check to see if the Intent has an email link, and if so set up the UI accordingly.
     * This can be called from either onCreate or onNewIntent, depending on how the Activity
     * was launched.
     */
    private void checkIntent(@Nullable Intent intent) {
        if (intentHasEmailLink(intent)) {
            mEmailLink = intent.getData().toString();

            mStatusText.setText(R.string.status_link_found);
            sendlink.setEnabled(false);
            //sign_in_btn.setEnabled(true);
        } else {
            mStatusText.setText(R.string.status_email_not_sent);
            sendlink.setEnabled(true);
            //sign_in_btn.setEnabled(false);
        }
    }

    /**
     * Determine if the given Intent contains an email sign-in link.
     */
    private boolean intentHasEmailLink(@Nullable Intent intent) {
        if (intent != null && intent.getData() != null) {
            String intentData = intent.getData().toString();
            if (mAuth.isSignInWithEmailLink(intentData)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sign in using an email address and a link, the link is passed to the Activity
     * from the dynamic link contained in the email.
     */
    private void signInWithEmailLink(String email, String link) {
        Log.d(TAG, "signInWithLink:" + link);

        //hideKeyboard(mEmailField);
        //showProgressDialog();

        mAuth.signInWithEmailLink(email, link)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //hideProgressDialog();
                        mPendingEmail = null;

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailLink:success");
                            Toast.makeText(getApplicationContext(),"Du bist drrrrinnn",Toast.LENGTH_LONG).show();


                            emailfield.setText(null);
                           // updateUI(task.getResult().getUser());
                        } else {
                            Log.w(TAG, "signInWithEmailLink:failure", task.getException());
                            //updateUI(null);

                            Toast.makeText(getApplicationContext(),"Verkackt",Toast.LENGTH_LONG).show();

                            if (task.getException() instanceof FirebaseAuthActionCodeException) {
                                showSnackbar("Invalid or expired sign-in link.");
                            }
                        }
                    }
                });
    }







}
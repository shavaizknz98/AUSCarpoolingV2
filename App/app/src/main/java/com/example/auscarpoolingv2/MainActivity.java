package com.example.auscarpoolingv2;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "DebugMain";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgress;
    private TextView mForgotPassword;

    private EditText mEmail, mPassword;
    private Button btnSignIn;
    private Button btnSignUp;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = (EditText) findViewById(R.id.newEmailText);
        mPassword = (EditText) findViewById(R.id.passwordText);
        btnSignIn = (Button) findViewById(R.id.signInButton);
        btnSignUp = (Button) findViewById(R.id.signupButton);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        mForgotPassword = (TextView) findViewById(R.id.forgotPassword);
        mForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mProgress.show();
                mProgress.setMessage("Checking Account...");
                mProgress.setCancelable(false);
                if (mEmail.getText().toString().trim().isEmpty()){
                    makeToast("Use a valid Email");
                }
                else{
                    mAuth.sendPasswordResetEmail(mEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                makeToast("Password reset link sent on email address");
                            } else {
                                makeToast("Could not reset password, please use a valid email address");
                            }
                        }
                    });}
                mProgress.dismiss();
            }});

        mProgress.show();
        mProgress.setMessage("Checking Account...");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //go to register page if user is null?
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                    makeToast("Signed in with " + user.getEmail());
                    //if user is signed in then go to main menu directly
                    Intent gotoUserMainMenu = new Intent(MainActivity.this, UserMainPageActivity.class);
                    gotoUserMainMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mProgress.dismiss();
                    startActivity(gotoUserMainMenu);
                }
                mProgress.dismiss();
            }


        };

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                mProgress.setMessage("Signing In");
                mProgress.show();
                if (email != "" && pass != "") {
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mProgress.dismiss();
                                //go to main menu page if successful sign in
                                Intent gotoUserPage = new Intent(MainActivity.this, UserMainPageActivity.class);
                                gotoUserPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(gotoUserPage);
                            } else {
                                makeToast("Could not sign in user");
                                mProgress.dismiss();
                            }
                        }
                    });
                } else {
                    makeToast("Please enter a valid email and password!");
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(MainActivity.this, SignUpActivity.class);
                signupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signupIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //make a toast
    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
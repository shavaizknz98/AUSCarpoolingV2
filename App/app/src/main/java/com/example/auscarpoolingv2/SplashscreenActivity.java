package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashscreenActivity extends Activity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        userAutoLogin();

    }

    private void userAutoLogin(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //go to register page if user is null?
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    //if user is signed in then go to main menu directly
                    Intent gotoUserMainMenu = new Intent(SplashscreenActivity.this, UserMainPageActivity.class);
                    gotoUserMainMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(gotoUserMainMenu);
                    finish();
                }else{
                    Intent gotoSignIn = new Intent(SplashscreenActivity.this, MainActivity.class);
                    gotoSignIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(gotoSignIn);
                    finish();
                }
            }


        };
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
}

package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashscreenActivity extends Activity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private int backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        mAuth = FirebaseAuth.getInstance();
        backButtonCount =0;
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
                    Intent gotoUserMainMenu = new Intent(getApplicationContext(), UserMainPageActivity.class);
                    gotoUserMainMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(gotoUserMainMenu);
                    finish();
                }else{
                    Intent gotoSignIn = new Intent(getApplicationContext(), MainActivity.class);
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

    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}

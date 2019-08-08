package com.example.auscarpoolingv2;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "DebugMain";
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private TextView mForgotPassword;

    private EditText mEmail, mPassword;
    private Button btnSignIn;
    private Button btnSignUp;
    private CheckBox checkBox;
    private int backButtonCount;

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
        backButtonCount =0;
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
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
                hideSoftKeyBoard();


                mProgress.setMessage("Signing In");
                mProgress.show();
                if (mEmail.getText().toString().trim().isEmpty() || mPassword.getText().toString().trim().isEmpty()){
                    makeToast("Please enter a valid email and password!");
                    mProgress.dismiss();
                    return;
                }else{
                    String email = mEmail.getText().toString().trim();
                    String pass = mPassword.getText().toString().trim();
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


    //make a toast
    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
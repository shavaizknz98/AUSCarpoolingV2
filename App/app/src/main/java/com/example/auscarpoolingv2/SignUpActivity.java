package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUpActivity";
    private Button btnSignUpUser;
    private EditText mEmail, mPassword, mPhone, mName;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorAccent));
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mEmail = (EditText) findViewById(R.id.signUpEmail);
        mPassword = (EditText) findViewById(R.id.signUpPassword);
        mName = (EditText) findViewById(R.id.signUpName);
        mPhone = (EditText) findViewById(R.id.signUpPhone);
        btnSignUpUser = (Button) findViewById(R.id.signUpUser);

        mProgress = new ProgressDialog(this);

        btnSignUpUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = mEmail.getText().toString().trim();
        String pass = mPassword.getText().toString().trim();
        final String name = mName.getText().toString().trim();
        final String phone = mPhone.getText().toString().trim();

        if(email != "" || pass != "" || name != "" || phone != "") {

            mProgress.setMessage("Signing up");
            mProgress.setCancelable(false);
            mProgress.show();


            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("phone").setValue(phone);
                        current_user_db.child("toAUS").setValue(false);
                        current_user_db.child("genderpref").setValue("a");
                        current_user_db.child("providing").setValue(false);
                        current_user_db.child("date").setValue("1970-01-01");//year-month-day
                        current_user_db.child("time").setValue("00-00");//hour-min
                        current_user_db.child("latitude").setValue(0.0);
                        current_user_db.child("longitude").setValue(0.0);
                        current_user_db.child("rating").setValue(0.0);
                        current_user_db.child("timesRated").setValue(0);

                        mProgress.dismiss();

                        Intent backtoMainActivity = new Intent(SignUpActivity.this, MainActivity.class);
                        backtoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(backtoMainActivity);

                    }
                }
            });
        }
    }

}

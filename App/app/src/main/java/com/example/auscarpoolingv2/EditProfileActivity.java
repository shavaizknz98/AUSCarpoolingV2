package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {
    public static final String TAG = "EditProfileActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private Button btnChangeDetails;
    private EditText mEmail, mOldPassword, mNewPassword, mPhone, mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorAccent));
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress = new ProgressDialog(this);
        mEmail = (EditText) findViewById(R.id.newEmailText);
        mOldPassword= (EditText)findViewById(R.id.oldPassText);
        mNewPassword = (EditText) findViewById(R.id.newPasswordText);
        mPhone = (EditText) findViewById(R.id.newPhoneNumberText);
        mName = (EditText) findViewById(R.id.newNameText);
        btnChangeDetails = (Button) findViewById(R.id.changeDetailsBtn);

        btnChangeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Updating user information");
                mProgress.show();
                changeDetails();
                mProgress.dismiss();
            }
        });


    }
    public void changeDetails(){
        final FirebaseUser user = mAuth.getCurrentUser();
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabase.child(user_id);
        String email = mEmail.toString().trim();
        final String password = mOldPassword.toString().trim();

        if(!email.isEmpty() && !password.isEmpty()) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), mOldPassword.toString().trim());
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updateEmail(mEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        makeToast("Email address updated");
                                    }
                                    else{
                                        makeToast("Email could not be updated, try again later");
                                    }
                                }
                            });
                }
            });
        }

        String newPassword = mNewPassword.toString().trim();
        String oldPassword = mOldPassword.toString().trim();
        if(!newPassword.isEmpty() && !email.isEmpty()){
            Log.d(TAG, "new pw not empty");
            AuthCredential credential = EmailAuthProvider.getCredential(/*user.getEmail()*/email, newPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(password)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        makeToast("Password updated");
                                        mAuth.signOut();
                                        Intent signinagain = new Intent(EditProfileActivity.this, MainActivity.class);
                                        signinagain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(signinagain);
                                        makeToast("Please log in again");

                                    }
                                    else{
                                        makeToast("Password could not be updated, try again later");
                                        Log.d(TAG, "Failed to update password");
                                    }
                                }
                            });
                }
            });

        } else {
            makeToast("Please enter email, old and new password to update email");
        }
        String newphonenum = mPhone.getText().toString().trim();
        if (newphonenum.length()!=0){
            current_user_db.child("phone").setValue(newphonenum).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        makeToast("Phone number updated");
                    }
                    else{
                        makeToast("Phone number could not be changed, try again later");
                    }
                }
            });
        }

        String newname = mName.getText().toString().trim();
        if(newname.length()!=0){
            current_user_db.child("name").setValue(newname).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        makeToast("Name updated");
                    }
                    else{
                        makeToast("Name could not be updated, try again later");
                    }
                }
            });
        }


    }

    public void makeToast(String message) {
        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();

    }
}

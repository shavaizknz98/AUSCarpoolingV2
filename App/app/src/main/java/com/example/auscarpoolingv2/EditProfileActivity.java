package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private Button btnChangeUserEmail;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorAccent));
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        btnChangeUserEmail = (Button) findViewById(R.id.btnChangeEmail);

        btnChangeUserEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserEmail();
            }
        });


    }

    private void changeUserEmail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        final EditText emailText = new EditText(EditProfileActivity.this);
        emailText.setHint("Old email address");
        final EditText passwordText = new EditText(EditProfileActivity.this);
        passwordText.setHint("Password");
        final EditText updatedEmailText = new EditText(EditProfileActivity.this);
        updatedEmailText.setHint("Enter updated email");
        LinearLayout layout = new LinearLayout(EditProfileActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(emailText);
        layout.addView(passwordText);
        layout.addView(updatedEmailText);
        AlertDialog dialog = new AlertDialog.Builder(EditProfileActivity.this)
                .setTitle("Enter in details to update your email address")
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldEmailAcdress = String.valueOf(emailText.getText().toString().trim());
                        String newEmailAddress = String.valueOf(updatedEmailText.getText().toString().trim());
                        String password = String.valueOf(passwordText.getText().toString().trim());
                        if(oldEmailAcdress.isEmpty()){
                            Toast.makeText(EditProfileActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }else if (password.isEmpty()){
                            Toast.makeText(EditProfileActivity.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }else if(newEmailAddress.isEmpty() || newEmailAddress.equals(oldEmailAcdress)){
                            Toast.makeText(EditProfileActivity.this, "Please enter a valid new email address", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }else{
                            final FirebaseUser user = mAuth.getCurrentUser();
                            AuthCredential credential = EmailAuthProvider.getCredential(oldEmailAcdress, password);
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    user.updateEmail(newEmailAddress)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(EditProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();

                                                    }
                                                    else{
                                                        Toast.makeText(EditProfileActivity.this, "Email could not be updated, please check credentials", Toast.LENGTH_SHORT).show(); }
                                                    alertDialog.dismiss();

                                                }
                                            });
                                }
                            });
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

}

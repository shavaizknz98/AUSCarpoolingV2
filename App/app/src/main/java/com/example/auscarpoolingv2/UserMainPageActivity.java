package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserMainPageActivity extends AppCompatActivity {

    public static final String TAG = "UserMainPage";

    private Button btnFindRide, btnProvideRide, btnEditProfile, btnHelp, btnSignOut, btnRateDriver;
    private TextView welcomeUser;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private Button btnStopProviding;
    String current_user, fullname;
    String userID;
    private TextView ratingText;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);
        getSupportActionBar().setTitle("Main Page");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorAccent));
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        current_user = mAuth.getCurrentUser().getUid();
        FirebaseUser fUser = mAuth.getCurrentUser();
        userID = fUser.getUid();

        mProgress = new ProgressDialog(this);

        btnEditProfile = (Button) findViewById(R.id.editProfileButton);
        btnFindRide = (Button) findViewById(R.id.findRideButton);
        btnProvideRide = (Button) findViewById(R.id.provideRideButton);
        ratingText = (TextView) findViewById(R.id.ratingText);
        btnRateDriver = (Button) findViewById(R.id.rateDriverBtn);
        btnHelp = (Button) findViewById(R.id.helpButton);
        btnSignOut = (Button) findViewById(R.id.signOutMPButton);
        btnStopProviding = (Button) findViewById(R.id.stopProvidingBtn);
        welcomeUser = (TextView) findViewById(R.id.welcomeBackText);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgress.setMessage("Retrieving User Information");
                mProgress.show();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Users user = new Users();
                    user.setName(ds.child(userID).getValue(Users.class).getName());
                    user.setPhonenum(ds.child(userID).getValue(Users.class).getPhonenum());
                    user.setRating(ds.child(userID).getValue(Users.class).getRating());
                    Double myRating = Math.floor(user.getRating() * 100)/100;
                    welcomeUser.setText("Welcome back, " + user.getName() + "! \nYour rating is: " + myRating);
                    mProgress.dismiss();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnRateDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRateDriver = new Intent(UserMainPageActivity.this, RateDriverActivity.class);
                startActivity(toRateDriver);
            }
        });
        btnFindRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFindRide = new Intent(UserMainPageActivity.this, FindRideActivity.class);
                startActivity(toFindRide);
            }
        });
        btnStopProviding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference current_user_db = mDatabase.child("Users").child(user_id);
                current_user_db.child("providing").setValue(false);
                Toast.makeText(UserMainPageActivity.this, "Not Providing a Ride Anymore", Toast.LENGTH_SHORT).show();
            }
        });
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHelpPage = new Intent(UserMainPageActivity.this, HelpActivity.class);
                toHelpPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toHelpPage);
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Signing out");
                mProgress.show();
                mAuth.signOut();
                mProgress.dismiss();
                //on sign out go back to main activity
                Intent backtoMainActivity = new Intent(UserMainPageActivity.this, MainActivity.class);
                backtoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backtoMainActivity);
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEditProfile = new Intent(UserMainPageActivity.this, EditProfileActivity.class);
                toEditProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toEditProfile);
            }
        });

        btnProvideRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProvideRide = new Intent(UserMainPageActivity.this, ProvideRideActivity.class);
                //allow user to go back
                startActivity(toProvideRide);
            }
        });

    }


    //make a toast
    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}

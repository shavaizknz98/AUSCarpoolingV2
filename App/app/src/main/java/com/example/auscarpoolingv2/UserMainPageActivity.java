package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.auscarpoolingv2.BuildConfig;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.auscarpoolingv2.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.auscarpoolingv2.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;



public class UserMainPageActivity extends AppCompatActivity {

    public static final String TAG = "UserMainPage";

    private Button btnFindRide, btnProvideRide, btnEditProfile , btnHelp, btnSignOut, btnRateDriver;
    private TextView welcomeUser;
    private TextView verNumTextView;
    private int backButtonCount =0;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private ProvidingNotification providingNotification;
    private Button btnStopProviding;
    String current_user, fullname;
    String userID;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private boolean mLocationPermissionGranted = false;



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
        providingNotification = new ProvidingNotification(UserMainPageActivity.this);
        FirebaseUser fUser = mAuth.getCurrentUser();
        userID = fUser.getUid();

        mProgress = new ProgressDialog(this);

        btnFindRide = (Button) findViewById(R.id.findRideButton);
        btnEditProfile = (Button) findViewById(R.id.editProfileButton);
        btnProvideRide = (Button) findViewById(R.id.provideRideButton);
        btnRateDriver = (Button) findViewById(R.id.rateDriverBtn);
        btnHelp = (Button) findViewById(R.id.helpButton);
        btnSignOut = (Button) findViewById(R.id.signOutMPButton);
        btnStopProviding = (Button) findViewById(R.id.stopProvidingBtn);
        welcomeUser = (TextView) findViewById(R.id.welcomeBackText);
        verNumTextView = (TextView) findViewById(R.id.versionTextView);
        mProgress.setMessage("Just a moment, Setting up the App for you");
        mProgress.setCancelable(false);
        mProgress.show();

        btnRateDriver.setVisibility(View.GONE);
        btnFindRide.setVisibility(View.GONE);

        btnHelp.setVisibility(View.GONE);
        btnProvideRide.setVisibility(View.GONE);
        btnEditProfile.setVisibility(View.GONE);
        btnSignOut.setVisibility(View.GONE);
        btnStopProviding.setVisibility(View.GONE);
        verNumTextView.setVisibility(View.GONE);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgress.setMessage("Retrieving User Information");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Users user = new Users();
                    user.setName(ds.child(userID).getValue(Users.class).getName());
                    user.setPhonenum(ds.child(userID).getValue(Users.class).getPhonenum());
                    user.setRating(ds.child(userID).getValue(Users.class).getRating());
                    user.setProviding(ds.child(userID).getValue(Users.class).isProviding());
                    Double myRating = Math.floor(user.getRating() * 100)/100;
                    if(user.isProviding()){
                        btnStopProviding.setVisibility(View.VISIBLE);
                        providingNotification.showNotification();

                    }else{
                        btnStopProviding.setVisibility(View.GONE);
                    }
                    welcomeUser.setText("Welcome back, " + user.getName() + "! \nYour rating is: " + myRating);

                }
                mProgress.dismiss();

                btnRateDriver.setVisibility(View.VISIBLE);
                btnFindRide.setVisibility(View.VISIBLE);
                btnEditProfile.setVisibility(View.VISIBLE);
                btnHelp.setVisibility(View.VISIBLE);
                btnProvideRide.setVisibility(View.VISIBLE);
                btnSignOut.setVisibility(View.VISIBLE);
                verNumTextView.setText(BuildConfig.VERSION_NAME);
                verNumTextView.setVisibility(View.VISIBLE);
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
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int unicode = 0x1F60A;
                makeToast("This feature will be added soon " + ("\ud83d\ude01"));
                Intent toEditProfile = new Intent(UserMainPageActivity.this, EditProfileActivity.class);
                startActivity(toEditProfile);
            }
        });
        btnStopProviding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference current_user_db = mDatabase.child("Users").child(user_id);
                current_user_db.child("providing").setValue(false);
                Toast.makeText(UserMainPageActivity.this, "Not Providing a Ride Anymore", Toast.LENGTH_SHORT).show();
                providingNotification.deleteNotificationChannel();
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
                finish();
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

        getLocationPermission();

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions( UserMainPageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS:
                if (mLocationPermissionGranted) {
                }else{
                    getLocationPermission();
                }
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

    //make a toast
    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}

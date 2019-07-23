package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class findRideList extends AppCompatActivity {
    private TextView resultText;
    private String result = "";
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride_list);
        getSupportActionBar().setTitle("Drivers Found");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorAccent));
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Finding Riders");
        mProgress.show();
        resultText = (TextView) findViewById(R.id.resultText);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    boolean providingRide = ds.child("providing").getValue(boolean.class);
                    String rideDate = ds.child("date").getValue(String.class);
                    String rideGender = ds.child("genderpref").getValue(String.class);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date listDate = null;
                    Date choosenDate = null;
                    String dateString = format.format(new Date());
                    try {
                        listDate = format.parse(rideDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        choosenDate = format.parse(FindRideActivity.datestr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (providingRide == true && listDate.equals(choosenDate) && rideGender.equals(FindRideActivity.genderPref)) {
                        String name = ds.child("name").getValue(String.class);
                        String phoneNumber = ds.child("phone").getValue(String.class);
                        String rideTime = ds.child("time").getValue(String.class);
                        result += name + "\n\tContact: " + phoneNumber + "\n\tTime And Date: " + rideTime + " " + rideDate
                                + "\n\n";

                        mProgress.dismiss();
                        resultText.setText(result);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
}

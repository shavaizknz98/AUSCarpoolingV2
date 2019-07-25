package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Layout;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class findRideList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    private TextView resultText;
    private String result = "";
    private ProgressDialog mProgress;
    private boolean foundDrivers = false;

    private ArrayList<RideList> rideList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride_list);
        getSupportActionBar().setTitle("Drivers Found");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorAccent));
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Finding Riders");
        mProgress.setCancelable(false);
        mProgress.show();

        rideList = new ArrayList<RideList>();

        //---------------SETUP OF LIST----------------------------------------------
        //resultText = (TextView) findViewById(R.id.resultText);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String address; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Geocoder geocoder;
            List<Address> addresses = null;
            String name;
            String phoneNumber;
            String rideTime;
            String rideDate;
            String rideGender;
            SimpleDateFormat format;
            Date listDate;
            Date choosenDate;
            String dateString;
            Double latitude;
            Double longitude;
            boolean providingRide;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    providingRide = ds.child("providing").getValue(boolean.class);
                    rideDate = ds.child("date").getValue(String.class);
                    rideGender = ds.child("genderpref").getValue(String.class);
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    listDate = null;
                    choosenDate = null;
                    dateString = format.format(new Date());
                    latitude = ds.child("latitude").getValue(Double.class);
                    longitude = ds.child("longitude").getValue(Double.class);

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

                        name = ds.child("name").getValue(String.class);
                        phoneNumber = ds.child("phone").getValue(String.class);
                        rideTime = ds.child("time").getValue(String.class);

                        geocoder = new Geocoder(findRideList.this,Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        address = addresses.get(0).getAddressLine(0);
                        result += name + "\n\tContact: " + phoneNumber + "\n\tTime And Date: " + rideTime + ", " + rideDate
                                + "\n\n" + address;
                        Log.d("HELLOHELLO", result);
                        foundDrivers = true;
                        Log.d("HELLOHELLO", "ADDING_>" + name );
                        rideList.add(new RideList(name, phoneNumber, rideDate, rideTime, address));

                        //resultText.setText(result);
                        //Linkify.addLinks(resultText,Linkify.ALL);
                    }
                }
                if(!foundDrivers){
                    resultText.setText("No Drivers Found");
                    Log.d("HELLOHELLO", "No riders!");
                    Toast.makeText(findRideList.this, "No drivers found!", Toast.LENGTH_SHORT).show();
                }
                mProgress.dismiss();
                //----------------------END OF SETUP-----------------------------------------------

                //do recyclerview here
                recyclerView = (RecyclerView) findViewById(R.id.ridelist_recycler_view);
                layoutManager = new LinearLayoutManager(findRideList.this);
                recyclerView.setHasFixedSize(true);

                recyclerView.setLayoutManager(layoutManager);
                mAdapter = new RideListAdapter(rideList);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                Log.d("HELLOHELLO", "SIZE*** =" + rideList.size());
                Toast.makeText(findRideList.this, "Set up lists", Toast.LENGTH_SHORT).show();

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });

    }
}

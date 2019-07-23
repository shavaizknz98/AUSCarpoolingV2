package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.auscarpoolingv2.Constants.ERROR_DIALOG_REQUEST;
import static com.example.auscarpoolingv2.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.auscarpoolingv2.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MapsActivity_getUserLocation extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    public static final float DEFAULT_ZOOM = 15f;
    private EditText mSearchText;
    private boolean mLocationPermissionGranted = false;

    private FusedLocationProviderClient mFusedLocationProvider;
    private GoogleMap mMap;

    private ImageView chooseloc;

    private LatLng finalloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_get_user_location);
        mSearchText = (EditText) findViewById(R.id.input_search);

        chooseloc = (ImageView) findViewById(R.id.ic_choosethisloc);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getLocationPermission();

    }



    private void geoLocate(){
        Log.d("Geolocate","Geolocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity_getUserLocation.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        } catch (IOException e) {
            Log.e("geolocate",e.getMessage());
        }
        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, address.toString());
            mMap.clear();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            finalloc = new LatLng(address.getLatitude(), address.getLongitude());
        }
        else{
            Log.d(TAG, "Nothing found");
            Toast.makeText(this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void init() {
        Log.d("Init","Init:Initialising");
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //Execute geolocate
                    geoLocate();
                }
                return false;
            }
        });

        chooseloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //return latlong to previous activity
                Intent intent = new Intent();
                double [] locArr = {finalloc.latitude, finalloc.longitude};
                intent.putExtra("Location", locArr);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //init map
                    initMap();
                }
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: finding location");

        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProvider.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "getDeviceLocation: Found location");
                            Location currentLocation = (Location) task.getResult();
                            LatLng loc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            finalloc = loc;
                            moveCamera(loc, DEFAULT_ZOOM, "Selected location");
                        } else {

                            Log.d(TAG, "getDeviceLocation: Could not find location");
                            Toast.makeText(MapsActivity_getUserLocation.this, "Cannot find device location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (java.lang.NullPointerException n) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            Intent backtoUserMainPage = new Intent(MapsActivity_getUserLocation.this, UserMainPageActivity.class);
            backtoUserMainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(backtoUserMainPage);
        }
        catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Exception occurred: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Moving camera to lat:" + latLng.latitude + " long:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Map ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map ready");

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);

            init();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions m = new MarkerOptions();
                m.position(latLng);
                mMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(m);
                finalloc = latLng;
            }
        });

    }


}

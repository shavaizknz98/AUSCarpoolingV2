package com.example.auscarpoolingv2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static com.example.auscarpoolingv2.Constants.ERROR_DIALOG_REQUEST;

public class ProvideRideActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String TAG = "ProvideRideActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RadioGroup genderPrefRG, locationRG;
    private RadioButton anygenderRB, maleRB, femaleRB, toAUSRB, fromAUSRB;
    private Button gotoMapBtn;
    private Button confirmRide;

    private TextView txtDate, txtTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private int mDay = -1, mMonth = -1, mYear = -1;
    private int mHour = -1, mMinute = -1;
    private String datestr;
    private String timestr;

    private String genderPref = "a";
    private boolean toAUS = false;

    private LatLng userloc;

    private ProgressDialog mProgress;

    public static final int REQUEST_LOCATION_CODE = 987;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_ride);
        getSupportActionBar().setTitle("Provide A Ride");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorAccent));
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mProgress = new ProgressDialog(this);


        genderPrefRG = (RadioGroup) findViewById(R.id.genderRadioGroup);
        locationRG = (RadioGroup) findViewById(R.id.locationRadioGroup);

        txtTime = (TextView) findViewById(R.id.timeTextView);
        txtDate = (TextView) findViewById(R.id.dateTextView);

        gotoMapBtn = (Button) findViewById(R.id.chooselocButton);
        confirmRide = (Button) findViewById(R.id.buttonFindDrivers);

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        genderPrefRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.noPrefRadioButton) {
                    genderPref = "a";   //any
                    Log.d(TAG, "Any gender");
                } else if (checkedId == R.id.femaleRadioButton) {
                    genderPref = "f";   //female
                    Log.d(TAG, "Female only");
                } else {
                    genderPref = "m";   //male
                    Log.d(TAG, "Male only");
                }
            }
        });

        locationRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.toAUSRadioButton) {
                    toAUS = true;
                    Log.d(TAG, "To AUS");
                } else {
                    toAUS = false;
                    Log.d(TAG, "From AUS");
                }
            }
        });

        gotoMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServicesOK()) {
                    //need to return coordinates
                    Intent gotoMapActivity = new Intent(ProvideRideActivity.this, MapsActivity_getUserLocation.class);
                    //gotoMapActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(gotoMapActivity);     //this will change
                    startActivityForResult(gotoMapActivity, REQUEST_LOCATION_CODE);
                }
            }
        });

        confirmRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress dialog here
                mProgress.setMessage("Confirming");
                mProgress.show();
                if (userloc == null
                        || mMinute == -1 || mHour == -1
                        || mDay == -1 || mMonth == -1 || mYear == -1) {
                    mProgress.dismiss();
                    makeToast("Please input all required fields!");
                } else {
                    mDatabase.child("toAUS").setValue(toAUS);
                    mDatabase.child("genderpref").setValue(genderPref);
                    mDatabase.child("providing").setValue(true);
                    mDatabase.child("date").setValue(mYear + "-" + mMonth + "-" + mDay);//year-month-day
                    mDatabase.child("time").setValue(mHour + "-" + mMinute);//hour-min
                    mDatabase.child("latitude").setValue(userloc.latitude);
                    mDatabase.child("longitude").setValue(userloc.longitude);

                    mProgress.dismiss();
                    Intent backtoMainMenu = new Intent(ProvideRideActivity.this, UserMainPageActivity.class);
                    backtoMainMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(backtoMainMenu);
                    makeToast("Confirmed! Riders will contact you shortly.");
                }
            }
        });

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: Checking if services can be used");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ProvideRideActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            //can make requests
            Log.d(TAG, "isServicesOK: Can make requests");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: fixable error");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ProvideRideActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Log.d(TAG, "isServicesOK: can't make requests");
            makeToast("You can't make location requests, please try again later");
        }
        return false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    double[] locdata = data.getDoubleArrayExtra("Location");
                    userloc = new LatLng(locdata[0], locdata[1]);
                    Log.d("onActivityResult", "Lat:" + userloc.latitude + " Long:" + userloc.longitude);
                    makeToast("Lat:" + userloc.latitude + " Long:" + userloc.longitude);
                } else {
                    Log.d("onActivityResult", "Activity cancelled");
                    makeToast("Activity cancelled");
                }
                break;
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)
        );
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day_of_month) {
        month += 1;
        mDay = day_of_month;
        mMonth = month;
        mYear = year;
        datestr = day_of_month + "/" + month + "/" + year;
        txtDate.setText(datestr);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mMinute = minute;
        mHour = hourOfDay;
        String h = "", m = "";
        if (hourOfDay < 10) h = "0";
        h += hourOfDay;
        if (minute < 10) m = "0";
        m += minute;
        timestr = h + ":" + m;
        txtTime.setText(timestr);
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}

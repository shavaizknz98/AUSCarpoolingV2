package com.example.auscarpoolingv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RateDriverActivity extends AppCompatActivity {
    private Button rateBtn;
    private EditText driverNumber;
    private EditText driverRating;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_driver);
        getSupportActionBar().setTitle("Provide A Ride");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorAccent));
        mProgress = new ProgressDialog(RateDriverActivity.this);
        mProgress.setMessage("Looking for rider");
        rateBtn = (Button) findViewById(R.id.rateButton);
        driverNumber = (EditText) findViewById(R.id.driverPhone);
        mAuth = FirebaseAuth.getInstance();
        driverRating = (EditText) findViewById(R.id.driverRating);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference usersRef = rootRef.child("Users");
                final String enteredNumber = driverNumber.getText().toString().trim();
                final Double choosenRating = Double.valueOf(driverRating.getText().toString().trim());
                Toast.makeText(RateDriverActivity.this, enteredNumber.toString(), Toast.LENGTH_SHORT).show();
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            DatabaseReference current_user_db = mDatabase.child(ds.getKey());
                            String retrievedNumber = ds.child("phone").getValue(String.class);

                            FirebaseUser fUser = mAuth.getCurrentUser();
                            String user_id = fUser.getUid();
                            if (retrievedNumber.equals(enteredNumber) && !retrievedNumber.equals(ds.child(user_id).child("phone").getValue())) {
                                int timesRated = ds.child("timesRated").getValue(int.class);
                                timesRated++;
                                current_user_db.child("rating").setValue((ds.child("rating").getValue(Double.class) + choosenRating) / timesRated);
                                current_user_db.child("timesRated").setValue(timesRated);
                                mProgress.dismiss();
                                Intent sendBacktoMain = new Intent(RateDriverActivity.this, UserMainPageActivity.class);
                                startActivity(sendBacktoMain);
                            } else {
                                Toast.makeText(RateDriverActivity.this, "User Not Found, Or you are trying to rate yourself :)", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });
    }
}

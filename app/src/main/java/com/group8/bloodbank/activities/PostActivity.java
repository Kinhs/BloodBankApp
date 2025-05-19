package com.group8.bloodbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group8.bloodbank.R;
import com.group8.bloodbank.models.User;

import java.util.Calendar;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText text1, text2;
    Spinner spinner1, spinner2;
    Button btnpost;

    FirebaseDatabase fdb;
    DatabaseReference db_ref;
    FirebaseAuth mAuth;

    Calendar cal;
    String uid;
    String Time, Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        progressBar = findViewById(R.id.progress_bar);
        text1 = findViewById(R.id.getMobile);
        text2 = findViewById(R.id.getLocation);
        spinner1 = findViewById(R.id.SpinnerBlood);
        spinner2 = findViewById(R.id.SpinnerDivision);
        btnpost = findViewById(R.id.postbtn);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Post Blood Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        String ampm = cal.get(Calendar.AM_PM) == Calendar.PM ? "PM" : "AM";

        Time = (hour < 10 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min + " " + ampm;
        Date = day + "/" + month + "/" + year;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser cur_user = mAuth.getCurrentUser();

        if (cur_user == null) {
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
            finish();
            return;
        }

        uid = cur_user.getUid();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("posts");

        // Fetch user data and pre-populate fields
        fetchUserData();

        btnpost.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (text1.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter your contact number!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (text2.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter your location!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else {
                savePost();
            }
        });
    }

    private void fetchUserData() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference userRef = fdb.getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Pre-populate EditText fields
                        if (user.getContact() != null) {
                            text1.setText(user.getContact());
                        }
                        if (user.getAddress() != null) {
                            text2.setText(user.getAddress());
                        }

                        // Pre-populate Spinners
                        setSpinnerSelection(spinner1, user.getBloodGroup());
                        setSpinnerSelection(spinner2, user.getDivision());
                    }
                } else {
                    Toast.makeText(PostActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PostActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                Log.e("PostActivity", "Database error: " + databaseError.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, int position) {
        if (position >= 0 && position < spinner.getCount()) {
            spinner.setSelection(position);
        }
    }

    private void savePost() {
        DatabaseReference userRef = fdb.getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        DatabaseReference postRef = db_ref.child(uid);
                        postRef.child("Name").setValue(user.getName());
                        postRef.child("Contact").setValue(text1.getText().toString());
                        postRef.child("Address").setValue(text2.getText().toString());
                        postRef.child("Division").setValue(spinner2.getSelectedItem().toString());
                        postRef.child("BloodGroup").setValue(spinner1.getSelectedItem().toString());
                        postRef.child("Time").setValue(Time);
                        postRef.child("Date").setValue(Date);
                        Toast.makeText(PostActivity.this, "Your post has been created successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PostActivity.this, Dashboard.class));
                        finish();
                    }
                } else {
                    Toast.makeText(PostActivity.this, "Database error occurred.", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PostActivity.this, "Failed to save post.", Toast.LENGTH_LONG).show();
                Log.e("PostActivity", "Database error: " + databaseError.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
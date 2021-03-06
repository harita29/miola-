package com.example.android.sfwhf1;
//By Bedrya Balema 10/20 2017

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CrunchActivity extends AppCompatActivity {
    private EditText cduration;
    private TextView ccalBurnt;
    private ImageButton cSubmit;
    private ImageButton back;
    private int bmr;
    private Calendar date = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
    private String currentDate = dateFormat.format(date.getTime());
    private static DecimalFormat df2 = new DecimalFormat(".##");


    private String uID;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crunch);
        setTitle("Crunches");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cduration = (EditText)findViewById(R.id.c_duration);
        ccalBurnt = (TextView)findViewById(R.id.c_calView);
        cSubmit = (ImageButton)findViewById(R.id.c_submit);
        back = (ImageButton)findViewById(R.id.back);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uID = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uID);

        cSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cSave();
                //startActivity(new Intent(CrunchActivity.this, WorkOutTypeActivity.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CrunchActivity.this, WorkOutTypeActivity.class));
            }
        });
    }
    //MET for cruches is 6 per IEEE
    public double calorieBurnt(double duration){

        //Calorie Burnt =(MBR/24) * MET * T

        return (bmr/24)*(6)*(duration/60);
    }

    private void cSave() {
        final String dur = cduration.getText().toString().trim();

        if(!TextUtils.isEmpty(dur)){
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.child("Daily Vital").child(currentDate).child("BMR").getValue().toString().equals("0")){
                        bmr = Integer.parseInt(dataSnapshot.child("Daily Vital").child(currentDate).child("BMR").getValue().toString());
                        double wBurnt = calorieBurnt(Double.parseDouble(dur));
                        ccalBurnt.setText("Calorie Burnt: " + df2.format(wBurnt));

                        mDatabase.child("Workout").child(currentDate).child("crunch").child("Time").setValue(dur);
                        mDatabase.child("Workout").child(currentDate).child("Crunch Calorie Burnt").setValue(df2.format(wBurnt));
                        //startActivity(new Intent(CrunchActivity.this, WorkOutTypeActivity.class));
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(CrunchActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Please Fill Out Your Daily Vital First!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(CrunchActivity.this, Main.class));
                                    }
                                });
                        alertDialog.show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
package com.example.bacnetcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


import java.io.Console;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //Firebase Database
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference keyRef;

    //Declare Views
    TextView tvCurrentSetpoint;
    TextView tvroomTemp;
    TextView tvBacnetId;
    EditText etNewSetpoint;
    Button btnUpdateSetpoint;

    String value1;
    String value2;
    String value3;
    String value4;
    String value5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReferenceFromUrl("https://bacnetcontroller.firebaseio.com/");

        tvCurrentSetpoint = findViewById(R.id.tvCurrentSetpoint);
        tvroomTemp = findViewById(R.id.tvroomTemp);
        tvBacnetId = findViewById(R.id.tvBacnetId);
        etNewSetpoint = findViewById(R.id.etNewSetpoint);
        btnUpdateSetpoint = findViewById(R.id.btnUpdateSetpoint);


        getContents();
    }

    public void getContents() {
        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw
        databaseReference.addValueEventListener(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries
            @Override
            public void onDataChange (DataSnapshot dataSnapshot){
                //foodList.clear(); //Clear foodlist before adding items again
                //Get ID From: https://stackoverflow.com/questions/43975734/get-parent-firebase-android
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                     value1 = ds.child("roomSetpoint").getValue().toString();
                     value2 = ds.child("roomTemp").getValue().toString();
                     value3 = ds.child("deviceBacnetId").getValue().toString();

                    tvCurrentSetpoint.setText(value1 + " °C");
                    tvroomTemp.setText(value2 + " °C");
                    tvBacnetId.setText(value3);

                }
            }

            @Override
            public void onCancelled (DatabaseError databaseError){

            }
        });
    }

    public void changeSetpoint(View view){
        String sp = etNewSetpoint.getText().toString();
        Map<String,String> firebaseMap = new HashMap<>();
        firebaseMap.put("roomSetpoint",sp);
        firebaseMap.put("roomTemp",value2);
        firebaseMap.put("deviceBacnetId",value3);

        databaseReference.child("values").setValue(firebaseMap);

        Toast.makeText(getApplicationContext(),"Setpoint Changed to: " + sp + " °C",Toast.LENGTH_SHORT).show();


    }

}

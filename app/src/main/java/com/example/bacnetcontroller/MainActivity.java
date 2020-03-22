package com.example.bacnetcontroller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import java.io.Console;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //Firebase Database
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference keyRef;

    //Time
    Time now;
    int currentHour;
    int currentMinute;
    SimpleDateFormat sdf;
    Calendar cal;


    //Declare Views
    TextView tvCurrentSetpoint;
    TextView tvroomTemp;
    TextView tvBacnetId;
    TextView tvFanSpeed;
    TextView tvCurrentTime;
    TextView tvCurrentDate;

    //EditText
    EditText etNewSetpoint;

    //Buttons
    Button btnUpdateSetpoint;
    Button btnLowerSp;
    Button btnHigherSp;

    ToggleButton toggle;

    ImageView imgOccupancyImage;


    String value1;
    String value2;
    String value3;
    String value4;
    String value5;

    String onOffString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         cal = Calendar.getInstance();
         currentHour = cal.get(Calendar.HOUR_OF_DAY);
         currentMinute = cal.get(Calendar.MINUTE);

        Date date = Calendar.getInstance().getTime();
        //
        // Display a date in day, month, year format
        //
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(date);




        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReferenceFromUrl("https://bacnetcontroller.firebaseio.com/");

        tvCurrentSetpoint = findViewById(R.id.tvCurrentSetpoint);
        tvroomTemp = findViewById(R.id.tvroomTemp);
        tvBacnetId = findViewById(R.id.tvBacnetId);
        tvFanSpeed = findViewById(R.id.tvFanSpeed);


        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvCurrentTime.setText(currentHour + ":" + currentMinute);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvCurrentDate.setText(today+ "");

        btnLowerSp = findViewById(R.id.btnLowerSp);
        btnHigherSp = findViewById(R.id.btnHigherSp);

        toggle = (ToggleButton) findViewById(R.id.btnOnOff);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    value4 = "1";

                } else {
                    // The toggle is disabled
                    value4 = "0";

                }
            }
        });

        btnLowerSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseSetpoint();
                sendData(btnLowerSp);
            }
        });

        btnHigherSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseSetpoint();
                sendData(btnHigherSp);
            }
        });


        imgOccupancyImage = findViewById(R.id.imgOccupancyImage);



        getContents();
    }

    public void getContents() {
        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw
        databaseReference.addValueEventListener(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries
            @Override
            public void onDataChange (DataSnapshot dataSnapshot){
                cal = Calendar.getInstance();
                currentHour = cal.get(Calendar.HOUR_OF_DAY);
                currentMinute = cal.get(Calendar.MINUTE);
                tvCurrentTime.setText(currentHour + ":" + currentMinute);

                //Get ID From: https://stackoverflow.com/questions/43975734/get-parent-firebase-android
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                     value1 = ds.child("roomSetpoint").getValue().toString();
                     value2 = ds.child("roomTemp").getValue().toString();

                     String idAsString = ds.child("deviceBacnetId").getValue().toString();
                     Double id = Double.parseDouble(idAsString);
                     int idInt = id.intValue();
                     value3 = Integer.toString(idInt);

                     value4 = ds.child("onOffCommand").getValue().toString();
                     value5 = ds.child("fanSpeed").getValue().toString();

                    tvCurrentSetpoint.setText(value1 + " °C");
                    tvroomTemp.setText(value2 + " °C");
                    tvBacnetId.setText(value3);
                    if(value4.equals("1")){
                        toggle.setChecked(true);
                        imgOccupancyImage.setVisibility(View.VISIBLE);
                    }
                    else{
                        toggle.setChecked(false);
                        imgOccupancyImage.setVisibility(View.INVISIBLE);
                    }

                    tvFanSpeed.setText(value5);

                }
            }

            @Override
            public void onCancelled (DatabaseError databaseError){

            }
        });
    }

    public void sendData(View view){
        cal = Calendar.getInstance();
        currentHour = cal.get(Calendar.HOUR_OF_DAY);
        currentMinute = cal.get(Calendar.MINUTE);
        tvCurrentTime.setText(currentHour + ":" + currentMinute);

        Map<String,String> firebaseMap = new HashMap<>();
        firebaseMap.put("roomSetpoint",value1);
        firebaseMap.put("roomTemp",value2);
        firebaseMap.put("deviceBacnetId",value3);
        firebaseMap.put("onOffCommand",value4);
        firebaseMap.put("fanSpeed",value5);

        databaseReference.child("values").setValue(firebaseMap);

    }

    //Duplicated so does not need a View Object to run
    public void sendData(){
        currentHour = cal.get(Calendar.HOUR_OF_DAY);
        currentMinute = cal.get(Calendar.MINUTE);
        tvCurrentTime.setText(currentHour + ":" + currentMinute);

        Map<String,String> firebaseMap = new HashMap<>();
        firebaseMap.put("roomSetpoint",value1);
        firebaseMap.put("roomTemp",value2);
        firebaseMap.put("deviceBacnetId",value3);
        firebaseMap.put("onOffCommand",value4);
        firebaseMap.put("fanSpeed",value5);

        databaseReference.child("values").setValue(firebaseMap);


    }

    public void decreaseSetpoint(){
        Double setpoint;
        setpoint = Double.parseDouble(value1) - 0.5;
        value1 = setpoint.toString();
    }

    public void increaseSetpoint(){
        Double setpoint;
        setpoint = Double.parseDouble(value1) + 0.5;
        value1 = setpoint.toString();
    }

    public void changeFanSpeed(View view){
        final String[] fanSpeeds = {"1","2","3"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Fan Speed");
        builder.setItems(fanSpeeds, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                value5 = fanSpeeds[which];
                Toast.makeText(getApplicationContext(),value5 + " Selected",Toast.LENGTH_SHORT).show();
                sendData();
            }
        });
        builder.show();

    }

}

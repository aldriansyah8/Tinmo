package com.example.tinmo;

import static android.icu.text.ListFormatter.Type.OR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static com.example.tinmo.SigninActivity.mAuth;
import static com.example.tinmo.SigninActivity.myRef;
import static com.example.tinmo.SigninActivity.newuser;
import static com.example.tinmo.SigninActivity.namaDepan;
import static com.example.tinmo.SigninActivity.formatDate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected static TextView txtKapasitas, txtHum, txtTemp, txtLux1, txtLux2, txtLux3, txtLux0;
    protected static Button btnCahayaOn, btnCahayaOff, btnSemprotOn, btnSemprotOff;
    protected static ImageView color0, color1, color2;
    private Button btnLogout;
    private Float hum, temp, vol, lux0, lux1, lux2, lux3;
    protected static Integer cahaya, sprayer, led;
    private long epoch;
    protected static FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtKapasitas = findViewById(R.id.readTankCapacity);
        txtHum = findViewById(R.id.readHumidity);
        txtTemp = findViewById(R.id.readTemperature);
        txtLux0 = findViewById(R.id.readLux1);
        txtLux1 = findViewById(R.id.readLux2);
        txtLux2 = findViewById(R.id.readLux3);
        txtLux3 = findViewById(R.id.readLux4);
        btnCahayaOn = findViewById(R.id.btnCahayaOn);
        btnCahayaOff = findViewById(R.id.btnCahayaOff);
        btnSemprotOn = findViewById(R.id.btnPenemprotanOn);
        btnSemprotOff = findViewById(R.id.btnPeneyemprotanOff);
        color0 = findViewById(R.id.ledPurple);
        color1 = findViewById(R.id.ledBlue);
        color2 = findViewById(R.id.ledYellow);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAccount();
            }
        });

        btnCahayaOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("lightState");
                myRef.setValue(1);
            }
        });

        btnCahayaOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("lightState");
                myRef.setValue(0);
            }
        });

        btnSemprotOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("sprayer_state");
                myRef.setValue(1);
            }
        });

        btnSemprotOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("sprayer_state");
                myRef.setValue(0);
            }
        });

        readData();
        lightState();
        sprayerState();
        ledState();
    }

    private void logoutAccount() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, SigninActivity.class);
        startActivity(intent);

        Toast.makeText(getApplicationContext(), "Log out berhasil", Toast.LENGTH_LONG).show();

    }

    private void readData(){
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vol = snapshot.child("sensor_tankCapacity").getValue(Float.class);
                temp = snapshot.child("sensor_temperature").getValue(Float.class);
                hum = snapshot.child("sensor_humidity").getValue(Float.class);
                lux0 = snapshot.child("sensor_lux0").getValue(Float.class);
                lux1 = snapshot.child("sensor_lux1").getValue(Float.class);
                lux2 = snapshot.child("sensor_lux2").getValue(Float.class);
                lux3 = snapshot.child("sensor_lux3").getValue(Float.class);

                txtKapasitas.setText(String.valueOf(vol));
                txtTemp.setText(String.valueOf(temp));
                txtHum.setText(String.valueOf(hum));
                txtLux0.setText(String.valueOf(lux0));
                txtLux1.setText(String.valueOf(lux1));
                txtLux2.setText(String.valueOf(lux2));
                txtLux3.setText(String.valueOf(lux3));

                if (lux0 < 100 || lux1 < 100 || lux2 < 100 || lux3 < 100){
                    Toast.makeText(MainActivity.this,
                            "Hama terdeteksi, kipas nyala", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Hama tidak terdeteksi, kipas mati", Toast.LENGTH_LONG).show();
                }

                if (vol <= 30){
                    Toast.makeText(MainActivity.this,
                            "Ketersediaan perstisida tersisa 30%, segera isi ulang", Toast.LENGTH_LONG).show();
                } else if (vol >= 80){
                    Toast.makeText(MainActivity.this,
                            "Ketersediaan perstisida tersisa 80%", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lightState(){
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cahaya = snapshot.child("lightState").getValue(Integer.class);

                if (cahaya == 1){
                    btnCahayaOn.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                    btnCahayaOn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    btnCahayaOff.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    btnCahayaOff.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                } else {
                    btnCahayaOn.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    btnCahayaOn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                    btnCahayaOff.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                    btnCahayaOff.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sprayerState(){
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sprayer = snapshot.child("sprayer_state").getValue(Integer.class);

                if (sprayer == 1){
                    btnSemprotOn.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                    btnSemprotOn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    btnSemprotOff.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    btnSemprotOff.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                } else {
                    btnSemprotOn.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    btnSemprotOn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                    btnSemprotOff.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                    btnSemprotOff.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ledState(){
        DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                led = snapshot.child("light_color").getValue(Integer.class);

                color0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("light_color").setValue(0);
                    }
                });

                color1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("light_color").setValue(1);
                    }
                });

                color2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child("light_color").setValue(2);
                    }
                });

                if (led == 0){
                    color0.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.purple));
                    color1.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.deepBlue));
                    color2.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.deepYellow));
                } if (led == 1){
                    color0.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.deepPurple));
                    color1.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.blue));
                    color2.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.deepYellow));
                } else if (led == 2) {
                    color0.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.deepPurple));
                    color1.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.deepBlue));
                    color2.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.yellow));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
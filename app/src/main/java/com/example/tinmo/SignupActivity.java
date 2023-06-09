package com.example.tinmo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.tinmo.SigninActivity.mAuth;
import static com.example.tinmo.SigninActivity.database;
import static com.example.tinmo.SigninActivity.myRef;
import static com.example.tinmo.SigninActivity.newuser;
import static com.example.tinmo.SigninActivity.namaDepan;
import static com.example.tinmo.SigninActivity.formatDate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    //deklarasi variable dan tipe data
    private EditText txtNamaDepan, txtNamaBelakang, txtNomor, txtEmail;
    private TextInputEditText txtPassword;
    private Button btnDaftar;
    protected static String email, password, namaDepan, namaBelakang, nomor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //variable autentikasi firebase
        mAuth = FirebaseAuth.getInstance();

        //menghubungkan variable java dengan variable desain
        txtNamaDepan = findViewById(R.id.txtNamaDepan);
        txtNamaBelakang = findViewById(R.id.txtNamaBelakang);
        txtNomor = findViewById(R.id.txtNomor);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnDaftar = findViewById(R.id.btnDaftar);

        //klik tombol daftar
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //menjalankan function registrasi
                registerNewUser();
            }
        });
    }

    private void registerNewUser(){
        //mengambil nilai dari variabel tersebut
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        namaDepan = txtNamaDepan.getText().toString();
        namaBelakang = txtNamaBelakang.getText().toString();
        nomor = txtNomor.getText().toString();

        //mengambil 5 huruf pertama dari variabel email
        String newuser = email.substring(0,5);

        //koneksi firebase
        database = FirebaseDatabase.getInstance();

        //input data ke firebase
        myRef = database.getReference("user").child(newuser).child("nama_depan");
        myRef.setValue(namaDepan);

        myRef = database.getReference("user").child(newuser).child("nama_belakang");
        myRef.setValue(namaBelakang);

        myRef = database.getReference("user").child(newuser).child("nomor_hp");
        myRef.setValue(nomor);

        myRef = database.getReference("user").child(newuser).child("email");
        myRef.setValue(email);

        myRef = database.getReference("user").child(newuser).child("password");
        myRef.setValue(password);

        //verifikasi email dan password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter youremail!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        //autentikasi email dan password
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();


                            // if the user created intent to login activity
                            Intent intent
                                    = new Intent(SignupActivity.this,
                                    SigninActivity.class);
                            startActivity(intent);
                        }
                        else {

                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();

                        }
                    }
                });
    }
}
package com.example.hackathonnitk.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hackathonnitk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signinup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email,password;
    private Button loginbt,createuser;

    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signinup);
        setTitle("Login");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.emailtext);
        password=findViewById(R.id.passwordtext);
        loginbt=findViewById(R.id.loginbt);
        createuser=findViewById(R.id.signcreate);
        loginbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailstr=email.getText().toString();
                String passwordstr=password.getText().toString();
                if(!emailstr.isEmpty()&&!passwordstr.isEmpty()){
                    loginsignin(emailstr,passwordstr);

                }
                else {
                    Toast.makeText(Signinup.this,"Feel Details",Toast.LENGTH_LONG).show();
                }
            }
        });
        createuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailstr=email.getText().toString();
                String passwordstr=password.getText().toString();
                if(!emailstr.isEmpty()&&!passwordstr.isEmpty()){
                    createsignitfinal(emailstr,passwordstr);

                }
                else {
                    Toast.makeText(Signinup.this,"Feel Details",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createsignitfinal(String email,String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Signinup.this,"Welcome ",Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(Signinup.this, DisplayImageActivity.class));
                            finish();

                        } else {
                            Toast.makeText(Signinup.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void loginsignin(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Signinup.this,"Welcome Back",Toast.LENGTH_LONG).show();

                            startActivity(new Intent(Signinup.this,DisplayImageActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Signinup.this, "Type Correct password or email",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}

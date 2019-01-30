package com.example.tartlabs.facebooklearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText reg_email;
    private EditText reg_pass;
    private EditText reg_confirm_pass;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_email = findViewById(R.id.reg_email);
        reg_pass = findViewById(R.id.reg_pass);
        reg_confirm_pass = findViewById(R.id.reg_confirm_pass);
        reg_btn = findViewById(R.id.reg_btn);
        reg_login_btn = findViewById(R.id.reg_login_btn);
        progressBar = findViewById(R.id.reg_progress);
        firebaseAuth = FirebaseAuth.getInstance();
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = reg_email.getText().toString();
                String passsword = reg_pass.getText().toString();
                String cPassword = reg_confirm_pass.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(passsword) && !TextUtils.isEmpty(cPassword)) {
                    if (passsword.equals(cPassword)) {
                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuth.createUserWithEmailAndPassword(email, passsword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Account(task);
                                } else {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "ConfromPasswrd and Password Not Match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Please Enter The All Felid", Toast.LENGTH_SHORT).show();
                }

            }
        });
        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoginPage();
            }
        });
    }

    private void setLoginPage() {
        Intent in = new Intent(this, LoginActivity.class);
        startActivity(in);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            setFeed();
        }
    }

    private void setFeed() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
        finish();
    }

    private void Account(Task<AuthResult> task) {
        if (task.getResult() != null) {
            Map<String, String> user = new HashMap<>();
            DatabaseReference userDB = ref.child("user").child(task.getResult().getUser().getUid());
            user.put("email", task.getResult().getUser().getEmail());
            user.put("user_id", task.getResult().getUser().getUid());
            userDB.setValue(user);
            Intent in = new Intent(this, SetUpActivity.class);
            startActivity(in);
            finish();
        }
    }
}

package com.example.tartlabs.facebooklearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText reg_email;
    private EditText reg_confirm_pass;
    private Button login_btn;
    private Button login_reg_btn;
    private FirebaseAuth mAuth;
    private ProgressBar login_progress;
    private LoginButton loginButton;
    private FirebaseAuth auth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        reg_email = findViewById(R.id.reg_email);
        reg_confirm_pass = findViewById(R.id.reg_confirm_pass);
        login_btn = findViewById(R.id.login_btn);
        login_reg_btn = findViewById(R.id.login_reg_btn);
        login_progress = findViewById(R.id.login_progress);
        loginButton = findViewById(R.id.buttonFacebookLogin);
        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbSignIn();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        login_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentRegister();
            }
        });
    }

    private void fbSignIn() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleAccessTOken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void handleAccessTOken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(authResult -> {
            /*Toast.makeText(LoginActivity.this, authResult.getUser().getEmail(), Toast.LENGTH_SHORT).show();*/
            sentMain();
        });
    }

    private void login() {

        String email = reg_email.getText().toString();
        String password = reg_confirm_pass.getText().toString();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            login_progress.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    login_progress.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        sentMain();
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Please Enter The Email And Password", Toast.LENGTH_SHORT).show();
        }
    }


    private void sentMain() {
        Intent in = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(in);
        finish();
    }

    private void sentRegister() {
        Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(in);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

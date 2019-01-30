package com.example.tartlabs.facebooklearn;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.tartlabs.facebooklearn.fragment.ChatUserFragment;
import com.example.tartlabs.facebooklearn.fragment.FeedFragment;
import com.example.tartlabs.facebooklearn.fragment.FeedFragmentPaginate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mauth;
    private FloatingActionButton add_post_btn;
    private BottomNavigationView mainBottomNav;
    private FeedFragment feedFragment;
    private ChatUserFragment chatUserFragment;
    private FeedFragmentPaginate feedFragmentPaginate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //printHashKey(this);
        toolbar = findViewById(R.id.main_toolbar);
        add_post_btn = findViewById(R.id.add_post_btn);
        mainBottomNav = findViewById(R.id.mainBottomNav);
        feedFragment = new FeedFragment();
        chatUserFragment = new ChatUserFragment();
        feedFragmentPaginate = new FeedFragmentPaginate();
        // replacemnetFragment(feedFragment);

        setSupportActionBar(toolbar);
        toolbar.setTitle("FaceBook");
        mauth = FirebaseAuth.getInstance();
        add_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, CreatePostMutipleImage.class);
                startActivity(in);
            }
        });

        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_home:
                        replacemnetFragment(feedFragmentPaginate);
                        return true;
                    case R.id.bottom_action_notif:
                        replacemnetFragment(chatUserFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            setSignIn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout_btn: {
                signOut();
                return true;
            }
            case R.id.action_settings_btn: {
                sentAccount();
                return true;
            }
            default:
                return false;
        }
    }

    private void sentAccount() {
        Intent in = new Intent(MainActivity.this, SetUpActivity.class);
        startActivity(in);
    }

    private void signOut() {
        mauth.signOut();
        setSignIn();
    }

    private void setSignIn() {
        Intent in = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(in);
        finish();
    }

    public void replacemnetFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    public static void printHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }
}

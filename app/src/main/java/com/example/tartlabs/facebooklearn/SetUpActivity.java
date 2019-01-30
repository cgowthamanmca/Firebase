package com.example.tartlabs.facebooklearn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private Toolbar setupToolbar;
    private CircleImageView setup_image;
    private Uri mainImageURI;
    private Uri imageUri;
    private EditText setup_name;
    private Button setup_btn;
    public static final int IMAGE = 500;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private ProgressBar setup_progress;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setupToolbar = findViewById(R.id.setupToolbar);
        setup_btn = findViewById(R.id.setup_btn);
        setup_name = findViewById(R.id.setup_name);
        setup_image = findViewById(R.id.setup_image);
        setup_progress = findViewById(R.id.setup_progress);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        if (auth.getCurrentUser().getDisplayName() != null) {
            setup_name.setText(auth.getCurrentUser().getDisplayName());
        }

        if (auth.getCurrentUser().getPhotoUrl() != null) {
            Picasso.get().load(auth.getCurrentUser().getPhotoUrl()).into(setup_image);

        }
        setSupportActionBar(setupToolbar);
        setupToolbar.setTitle("Account");
        setup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(SetUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(SetUpActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }
        });

        setup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = setup_name.getText().toString();
                String userId = auth.getCurrentUser().getUid();
                if (!TextUtils.isEmpty(name) && mainImageURI != null) {
                    setup_progress.setVisibility(View.VISIBLE);
                    StorageReference imagePath = storageReference.child("profile_image").child(userId + ".jpg");
                    imagePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            setup_progress.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                getUrlAsync("profile_image");

                            } else {
                                Toast.makeText(SetUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void BringImagePicker() {
        Intent in = new Intent();
        in.setType("image/*");
        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(in, "Choose Image"), IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE: {
                if (data != null) {
                    mainImageURI = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mainImageURI);
                        setup_image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void saveUserInformation() {
        String name = setup_name.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter Thw Name", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name).setPhotoUri(imageUri).build();
            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        setFeed();
                        Toast.makeText(getApplicationContext(), "SuccessFuylly Added" + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setFeed() {
        Map<String, String> user = new HashMap<>();
        DatabaseReference userDB = ref.child("user").child(auth.getCurrentUser().getUid());
        user.put("name",auth.getCurrentUser().getDisplayName());
        user.put("email", auth.getCurrentUser().getEmail());
        user.put("user_id", auth.getCurrentUser().getUid());
        user.put("profile_image", auth.getCurrentUser().getPhotoUrl().toString());
        user.put("name", auth.getCurrentUser().getDisplayName());
        userDB.setValue(user);
        Intent in = new Intent(SetUpActivity.this, MainActivity.class);
        startActivity(in);
    }

    private void getUrlAsync(String date) {
        // Points to the root reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("/" + date + "/" + auth.getCurrentUser().getUid() + ".jpg");
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                imageUri = downloadUrl;
                saveUserInformation();
            }
        });
    }
}


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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePost extends AppCompatActivity {

    private ImageView new_post_image;
    private EditText new_post_desc;
    private Button post_btn;
    private Toolbar new_post_toolbar;
    public static final int IMAGE = 500;
    private Uri mainImageURI;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private ProgressBar new_post_progress;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private Uri imageUri;
    private String uploadId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        new_post_image = findViewById(R.id.new_post_image);
        new_post_desc = findViewById(R.id.new_post_desc);
        post_btn = findViewById(R.id.post_btn);
        new_post_toolbar = findViewById(R.id.new_post_toolbar);
        new_post_progress = findViewById(R.id.new_post_progress);
        setSupportActionBar(new_post_toolbar);
        new_post_toolbar.setTitle("Post");
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        new_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(CreatePost.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(CreatePost.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(CreatePost.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = new_post_desc.getText().toString();
                if (!TextUtils.isEmpty(description) && mainImageURI != null) {
                    new_post_progress.setVisibility(View.VISIBLE);
                    uploadId = UUID.randomUUID().toString();

                    //Single Image Upload
                    StorageReference imagePath = storageReference.child("post_image").child(uploadId + ".jpg");
                    imagePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            new_post_progress.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                getUrlAsync("post_image");
                            } else {
                                Toast.makeText(CreatePost.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
                        new_post_image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void getUrlAsync(String date) {
        // Points to the root reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("/" + date + "/" + uploadId + ".jpg");
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                imageUri = downloadUrl;
                savePostInformation();
            }
        });
    }

    private void savePostInformation() {
        Map<String, String> map = new HashMap<>();
        Map<String, String> user = new HashMap<>();
        map.put("description", new_post_desc.getText().toString());
        map.put("image", imageUri.toString());

        user.put("name", auth.getCurrentUser().getDisplayName());
        user.put("email", auth.getCurrentUser().getEmail());
        user.put("profile_image", auth.getCurrentUser().getPhotoUrl().toString());
        user.put("user_id", auth.getCurrentUser().getUid());
        /*ref.child("feed").child("post").child(uploadId).setValue(map);
        ref.child("feed").child("post").child(uploadId).child("user").setValue(user);*/
        DatabaseReference da = ref.child("feed").child("post").push();
        da.setValue(map);
        da.child("user").setValue(user);
        finish();

    }
}

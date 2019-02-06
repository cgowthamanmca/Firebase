package com.example.tartlabs.facebooklearn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tartlabs.facebooklearn.model.Media;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePostMutipleImage extends AppCompatActivity {
    private MediaGridView new_post_image;
    private EditText new_post_desc;
    private Button post_btn;
    private Toolbar new_post_toolbar;
    public static final int IMAGE = 500;
    private Uri mainImageURI;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private ProgressBar new_post_progress;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private String uploadId;
    int SELECT_PICTURES = 1;
    private ArrayList<Media> mArrayUri = new ArrayList<>();
    private ArrayList<String> imageUrl = new ArrayList<>();


    Media media;
    Uri imageUri;
    int up = 0;
    int k = 0;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_mutiple_image);
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

                    if (ContextCompat.checkSelfPermission(CreatePostMutipleImage.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(CreatePostMutipleImage.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(CreatePostMutipleImage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }
        });

        post_btn.setOnClickListener(v -> {
            String description = new_post_desc.getText().toString();
            if (!TextUtils.isEmpty(description) && media != null) {
                new_post_progress.setVisibility(View.VISIBLE);
                while (up < mArrayUri.size()) {
                    uploadId = UUID.randomUUID().toString();
                    StorageReference imagePath = storageReference.child("post_image").child(uploadId + ".jpg");
                    imagePath.putFile(mArrayUri.get(k).getMediaUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUrl.add(uri.toString());
                                        mArrayUri.get(i).setUrl(uri.toString());
                                        if (mArrayUri.size() == imageUrl.size()) {
                                            savePostInformation();
                                        }
                                        i++;
                                    }
                                });
                            } else {
                                Toast.makeText(CreatePostMutipleImage.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                    up++;
                    k++;
                }
            }
        });
    }

    private void BringImagePicker() {
        Intent in = new Intent();
        in.setType("image/*");
        in.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(in, "Choose Image"), SELECT_PICTURES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURES) {
            if (resultCode == MainActivity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    Log.i("count", String.valueOf(count));
                    int currentItem = 0;
                    while (currentItem < count) {
                        imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        Log.i("uri", imageUri.toString());
                        currentItem = currentItem + 1;
                        media = new Media();
                        media.setType("image");
                        media.setUrl(imageUri.toString());
                        media.setMediaUri(imageUri);
                        mArrayUri.add(media);
                    }
                    Log.i("listsize", String.valueOf(mArrayUri.size()));
                } else if (data.getData() != null) {

                }
                new_post_image.setImages(mArrayUri);
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
                for (int i = 0; i < mArrayUri.size(); i++) {
                    mArrayUri.get(i).setUrl(downloadUrl.toString());
                }
                imageUrl.add(downloadUrl.toString());
                savePostInformation();
            }

        });
    }

    private void savePostInformation() {
        Map<String, Object> map = new HashMap<>();
        map.put("description", new_post_desc.getText().toString());
        map.put("image", imageUri.toString());
        map.put("user_id", auth.getCurrentUser().getUid());

        // User
        HashMap<String, Object> userObj = new HashMap<>();
        userObj.put("name", auth.getCurrentUser().getDisplayName());
        userObj.put("email", auth.getCurrentUser().getEmail());
        userObj.put("profile_image", auth.getCurrentUser().getPhotoUrl().toString());
        userObj.put("user_id", auth.getCurrentUser().getUid());

        ArrayList<HashMap<String, Object>> mediaArray = new ArrayList<>();
        for (int i = 0; i < mArrayUri.size(); i++) {
            HashMap<String, Object> media = new HashMap<>();
            media.put("url", mArrayUri.get(i).getUrl());
            media.put("type", mArrayUri.get(i).getType());
            mediaArray.add(media);
        }

        ArrayList<HashMap<String, Object>> imagePayload = new ArrayList<>();
        for (int i = 0; i < mediaArray.size(); i++) {
            imagePayload.add(i, mediaArray.get(i));
        }

        DatabaseReference post = ref.child("post").push();
        map.put("image",imagePayload);
        map.put("user",userObj);
        post.setValue(map);
        new_post_progress.setVisibility(View.GONE);
        finish();
    }

    /* private void savePostInformation() {
     *//*  // Description
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("description", new_post_desc.getText().toString());

        // User
        HashMap<String,Object> userObj = new HashMap<>();
        userObj.put("name",auth.getCurrentUser().getDisplayName());
        userObj.put("email",auth.getCurrentUser().getEmail());
        userObj.put("profile_image",auth.getCurrentUser().getPhotoUrl().toString());
        userObj.put("user_id",auth.getCurrentUser().getUid());

        payload.put("user",userObj);

        // Media
        ArrayList<HashMap<String,Object>> mediaArray = new ArrayList<>();
        for(int i = 0; i< mArrayUri.size();i++){
            HashMap<String,Object> media = new HashMap<>();
            media.put("url",mArrayUri.get(i).getMediaUrl());
            media.put("type",mArrayUri.get(i).getMediaType());
            mediaArray.add(media);
        }

        ArrayList<HashMap<String,Object>> imagePayload = new ArrayList<>();
        for (int i = 0; i < mediaArray.size(); i++) {
            imagePayload.add(i, mediaArray.get(i));
        }

        //payload.put("image",imagePayload);

        DatabaseReference da = ref.child("feed").child("post").push();
        da.setValue(payload);
        finish();*//*

        Map<String, String> map = new HashMap<>();
        Map<String, String> user = new HashMap<>();
        map.put("description", new_post_desc.getText().toString());
        //map.put("image", imageUri.toString());

        ArrayList<HashMap<String,Object>> mediaArray = new ArrayList<>();

        for(int i = 0; i< mArrayUri.size(); i++){
            HashMap<String,Object> media = new HashMap<>();
            media.put("url",mArrayUri.get(i).getMediaUrl());
            media.put("type",mArrayUri.get(i).getMediaType());
            mediaArray.add(media);
        }

        ArrayList<HashMap<String,Object>> imagePayload = new ArrayList<>();
        for (int i = 0; i < mediaArray.size(); i++) {
            imagePayload.add(i, mediaArray.get(i));
        }

        user.put("name", auth.getCurrentUser().getDisplayName());
        user.put("email", auth.getCurrentUser().getEmail());
        user.put("profile_image", auth.getCurrentUser().getPhotoUrl().toString());
        user.put("user_id", auth.getCurrentUser().getUid());
        ref.child("feed").child("post").child(uploadId).setValue(map);
        ref.child("feed").child("post").child(uploadId).child("user").setValue(user);
        DatabaseReference da = ref.child("feed").child("post").push();
        da.setValue(map);
        da.child("user").setValue(user);
        da.child("image").setValue(imagePayload);
        finish();

    }*/

}

package com.example.tartlabs.facebooklearn.fbHelper;

import android.support.annotation.NonNull;

import com.example.tartlabs.facebooklearn.model.Post;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostHelper {
    // Get latest posts
    public static Task<ArrayList<Post>> getLatestPosts(int count) {
        final TaskCompletionSource<ArrayList<Post>> tcs = new TaskCompletionSource<>();
        final ArrayList<Post> posts = new ArrayList<>();

        Query ref = FirebaseDatabase.getInstance().getReference()
                .child("post")
                .orderByKey()
                .limitToLast(count);

        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post post = snapshot.getValue(Post.class);
                        if (post != null) {
                            post.setId(snapshot.getKey());
                            posts.add(post);
                        }
                    }
                }
                tcs.setResult(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });

        return tcs.getTask();
    }

    // Get old posts
    public static Task<ArrayList<Post>> getOldPosts(final String beforeKey, int count) {
        final TaskCompletionSource<ArrayList<Post>> tcs = new TaskCompletionSource<>();
        final ArrayList<Post> posts = new ArrayList<>();

        Query ref = FirebaseDatabase.getInstance().getReference()
                .child("post")
                .orderByKey()
                .endAt(beforeKey)
                .limitToLast(count + 1);

        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post post = snapshot.getValue(Post.class);
                        if (post != null) {
                            post.setId(snapshot.getKey());
                            if (!beforeKey.equals(post.getId())) {
                                posts.add(post);
                            }
                        }
                    }
                }
                tcs.setResult(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });
        return tcs.getTask();
    }
}

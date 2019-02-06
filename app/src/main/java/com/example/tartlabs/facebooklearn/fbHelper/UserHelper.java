package com.example.tartlabs.facebooklearn.fbHelper;

import android.support.annotation.NonNull;

import com.example.tartlabs.facebooklearn.model.Feed;
import com.example.tartlabs.facebooklearn.model.Snap;
import com.example.tartlabs.facebooklearn.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserHelper {

    public static Task<ArrayList<String>> getLatestFeedPosts(int count) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

        final TaskCompletionSource<ArrayList<String>> tcs = new TaskCompletionSource<>();
        final HashMap<String, Object> feeds = new HashMap<>();
        ArrayList<String> feedArray = new ArrayList<>();
        Query ref = FirebaseDatabase.getInstance().getReference()
                .child("user").child(currentUserId)
                .child("feed")
                .orderByKey()
                .limitToLast(count);

       /* When you use the keepSynced() method, you're telling Firebase to download and cache all the data from databaseRef. I hope databaseRef isn't the root Reference of your Database because if it is, you're downloading your entire database and this is not a good practice.

        You should use the keepSynced() to cache nodes that are really necessary for your app to work offline.

                You will be probably wondering what's the difference with setPersistanceEnabled(true). Well, setPersistanceEnabled(true) only caches data when there is a Listener attached to that node (when the data has been read at least once).

        On the other side, keepSynced(true) caches everything from that node, even if there is no listener attached.*/

        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*if (dataSnapshot.getValue() != null) {
                    Feed feed = dataSnapshot.getValue(Feed.class);
                    feeds.putAll(feed.getFeed());
                    for (Map.Entry<String, Object> entry : feeds.entrySet()) {
                        feedArray.add(entry.getKey());
                    }
                }*/
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    feedArray.add(ds.getKey());
                }
                tcs.setResult(feedArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });

        return tcs.getTask();
    }


    public static Task<ArrayList<String>> getOldFeedPosts(final String beforeKey, int count) {
        final TaskCompletionSource<ArrayList<String>> tcs = new TaskCompletionSource<>();
        final HashMap<String, Object> feeds = new HashMap<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        ArrayList<String> feedArray = new ArrayList<>();

        Query ref = FirebaseDatabase.getInstance().getReference()
                .child("post")
                .child(currentUserId)
                .child("feed")
                .orderByKey()
                .endAt(beforeKey)
                .limitToLast(count + 1);

        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue() != null) {
                        User user = dataSnapshot.getValue(User.class);
                        feeds.putAll(user.getFeed());
                        for (Map.Entry<String, Object> entry : feeds.entrySet()) {
                            if (!beforeKey.equals(entry.getKey())) {
                                feedArray.add(entry.getKey());
                            }
                        }
                    }
                }
                tcs.setResult(feedArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });
        return tcs.getTask();
    }

}

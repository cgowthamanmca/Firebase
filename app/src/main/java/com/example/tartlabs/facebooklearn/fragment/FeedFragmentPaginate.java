package com.example.tartlabs.facebooklearn.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.tartlabs.facebooklearn.Consts;
import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.adapter.feedpaginate.FeedPaaginateAdapter;
import com.example.tartlabs.facebooklearn.model.Post;
import com.example.tartlabs.facebooklearn.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragmentPaginate extends Fragment {


    private RecyclerView rvFeed;
    private FeedPaaginateAdapter adapter;
    private DatabaseReference databaseReference;
    private DatabaseReference post;
    private DatabaseReference userRef;
    private List<Post> postList = new ArrayList<>();
    private ProgressBar reg_progress;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth;
    private Map<String, Boolean> likesList = new HashMap<>();
    private Map<String, Object> feed = new HashMap<>();
    private ArrayList<Post> list = new ArrayList<>();
    private ArrayList<User> userArrayList = new ArrayList<>();


    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition;
    private boolean mIsLoading = false;
    private int mPostsPerPage = 10;
    private String lastKey;


    public FeedFragmentPaginate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_fragment_paginate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFeed = view.findViewById(R.id.rvFeed);
        reg_progress = view.findViewById(R.id.reg_progress);
        reg_progress.setVisibility(View.VISIBLE);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvFeed.setLayoutManager(mLayoutManager);
        rvFeed.setHasFixedSize(true);
        adapter = new FeedPaaginateAdapter();
        adapter.setPostList(list);
        //adapter.setListener(this);
        rvFeed.setAdapter(adapter);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        post = FirebaseDatabase.getInstance().getReference().child("post");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (dataSnapshot.getKey().equals(auth.getUid())) {
                    feed.putAll(user.getFeed());
                    getUsers(null);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (dataSnapshot.getKey().equals(auth.getUid())) {
                    feed.putAll(user.getFeed());
                    getUsers(null);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rvFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItemPosition + mPostsPerPage)) {
                    getUsers(adapter.getLastItemId());
                    mIsLoading = true;
                }
            }
        });
    }

    private void getPostList() {
        post.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        reg_progress.setVisibility(View.GONE);
                        list.clear();
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Post post;
                            post = dsp.getValue(Post.class);
                            post.setId(dsp.getKey());
                            if (feed.containsKey(post.getId())) {
                                list.add(post);
                            }
                        }
                        adapter.setPostList(list);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    private void getUsers(String nodeId) {
        Query query;
        reg_progress.setVisibility(View.GONE);
        if (nodeId == null)
            query = FirebaseDatabase.getInstance().getReference()
                    .child(Consts.FIREBASE_DATABASE_LOCATION_USERS)
                    .orderByKey()
                    .limitToFirst(mPostsPerPage);
        else
            query = FirebaseDatabase.getInstance().getReference()
                    .child(Consts.FIREBASE_DATABASE_LOCATION_USERS)
                    .orderByKey()
                    .startAt(nodeId)
                    .limitToFirst(mPostsPerPage);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post;
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    lastKey = userSnapshot.getKey();
                    post = userSnapshot.getValue(Post.class);
                    post.setId(userSnapshot.getKey());
                    posts.add(post);
                }
                adapter.addAll(posts);
                mIsLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }
}


package com.example.tartlabs.facebooklearn.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.adapter.FeedFragmentAdapter;
import com.example.tartlabs.facebooklearn.model.Post;
import com.example.tartlabs.facebooklearn.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements FeedFragmentAdapter.feedListener {

    private RecyclerView rvFeed;
    private FeedFragmentAdapter adapter;
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


    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFeed = view.findViewById(R.id.rvFeed);
        reg_progress = view.findViewById(R.id.reg_progress);
        reg_progress.setVisibility(View.VISIBLE);
        rvFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFeed.setHasFixedSize(true);
        adapter = new FeedFragmentAdapter();
        adapter.setPostList(list);
        adapter.setListener(this);
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
                    getPostList();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (dataSnapshot.getKey().equals(auth.getUid())) {
                    feed.putAll(user.getFeed());
                    getPostList();
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
       /* databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                reg_progress.setVisibility(View.GONE);
                Post post = dataSnapshot.getValue(Post.class);
                if (auth.getCurrentUser() != null) {
                    if (post.getLikes() != null) {
                        if (post.getLikes().containsKey(auth.getCurrentUser().getUid())) {
                            post.setLike(true);
                        } else {
                            post.setLike(false);
                        }
                    } else {
                        post.setLike(false);
                    }
                    post.setId(dataSnapshot.getKey());
                    postList.add(post);
                    adapter.setPostList(postList);
                    rvFeed.setAdapter(adapter);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);

                if (post.getLikes() != null) {
                    if (post.getLikes().containsKey(auth.getCurrentUser().getUid())) {
                        post.setLike(true);
                    } else {
                        post.setLike(false);
                    }
                } else {
                    post.setLike(false);
                }
                post.setId(dataSnapshot.getKey());

                for (int i = 0; i < postList.size(); i++) {
                    if (dataSnapshot.getKey().equals(postList.get(i).getId())) {
                        postList.set(i, post);
                        adapter.notifyItemChanged(i);
                    }
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
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });*/
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

    @Override
    public void Like(Post post, int pos) {
        if (post.getLike()) {
            DatabaseReference da = ref.child(auth.getCurrentUser().getUid()).child("post").child(post.getId());
            da.child("likes").child(auth.getCurrentUser().getUid()).removeValue();
            adapter.unLike(pos);
        } else {
            DatabaseReference da = ref.child(auth.getCurrentUser().getUid()).child("post").child(post.getId());
            da.child("likes").child(auth.getCurrentUser().getUid()).setValue(true);
            adapter.setLike(pos);
        }
    }

}

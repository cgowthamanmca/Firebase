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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tartlabs.facebooklearn.PaginationScrollListener;
import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.adapter.feed.PaginatedFeedAdapter;
import com.example.tartlabs.facebooklearn.fbHelper.PostHelper;
import com.example.tartlabs.facebooklearn.model.Feed;
import com.example.tartlabs.facebooklearn.model.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaginatedFeedFragment extends Fragment {

    private RecyclerView rvFeedList;
    private TextView tvNoData;
    private LinearLayout llProgress;

    private ArrayList<Post> postList = new ArrayList<>();
    private Post topPost = new Post();
    private Post bottomPost = new Post();
    private PaginatedFeedAdapter adapter;
    private boolean isLastPage, isLoading;
    private Query newFeedTrackerQuery;
    private List<Feed> feedList = new ArrayList<>();

    public PaginatedFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paginated_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        setRecyclerView();
        loadLatestPosts();
    }

    private void initViews(View view) {
        rvFeedList = view.findViewById(R.id.rvFeedList);
        tvNoData = view.findViewById(R.id.tvNoData);
        llProgress = view.findViewById(R.id.llProgress);
    }

    private void setRecyclerView() {
        adapter = new PaginatedFeedAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        rvFeedList.setLayoutManager(llm);
        rvFeedList.setAdapter(adapter);
        rvFeedList.addOnScrollListener(new PaginationScrollListener(llm) {
            @Override
            public void loadMoreItems() {
                isLoading = true;
                loadOldPosts();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void loadLatestPosts() {
        showProgress();
        PostHelper.getLatestPosts(10)
                .addOnSuccessListener(this::onLatestMessagesSuccess);
    }

    private void onLatestMessagesSuccess(ArrayList<Post> posts) {
        hideProgress();
        if (posts != null && !posts.isEmpty()) {
            setPosts();

            postList.clear();
            postList.addAll(posts);

            if (adapter != null) {
                if (postList.size() > 0) {
                    topPost = postList.get(0);
                    bottomPost = postList.get(postList.size() - 1);
                }

                Collections.reverse(postList);

                if (posts.isEmpty()) {
                    setNoPosts();
                } else {
                    setPosts();
                    adapter.setPostList(postList);
                    adapter.notifyDataSetChanged();
                    if (postList.size() >= 10) {
                        adapter.addLoadingFooter();
                    }
                    rvFeedList.scrollToPosition(0);
                }
            }
        } else {
            setNoPosts();
        }
        startTrackingNewFeed();
    }

    private void startTrackingNewFeed() {
        if (newFeedTrackerQuery != null) {
            newFeedTrackerQuery.removeEventListener(feedChildEventListener);
        }
        newFeedTrackerQuery = FirebaseDatabase.getInstance()
                .getReference()
                .child("post")
                .orderByKey()
                .startAt(bottomPost.getId());
        newFeedTrackerQuery.addChildEventListener(feedChildEventListener);
    }

    private ChildEventListener feedChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            Post post = dataSnapshot.getValue(Post.class);
            if (post != null) {
                post.setId(dataSnapshot.getKey());

                if (postList.isEmpty()) {
                    setPosts();
                }
                if (!postList.contains(post)) {
                    postList.add(0, post);
                    if (adapter != null) {
                        if (adapter.getItemCount() == 0) {
                            // Check count if no data set in adapter previously
                            // means no updates an user enters first update
                            adapter.setPostList(postList);
                        }
                        adapter.notifyItemInserted(0);
                        rvFeedList.scrollToPosition(0);
                    }
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                Post todo = dataSnapshot.getValue(Post.class);
                if (todo != null) {
                    todo.setId(dataSnapshot.getKey());
                    if (adapter != null) {
                        if (adapter.getPostList() != null && !adapter.getPostList().isEmpty()) {
                            adapter.getPostList().remove(todo);
                            //adapter.notifyItemRemoved(index);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void loadOldPosts() {
        hideProgress();
        PostHelper.getOldPosts(topPost.getId(), 10)
                .addOnSuccessListener(this::onOldMessagesSuccess);
    }

    private void onOldMessagesSuccess(ArrayList<Post> posts) {
        if (!posts.isEmpty()) {
            // Reverse
            Collections.reverse(posts);
            topPost = posts.get(posts.size() - 1);
            if (adapter != null) {
                adapter.removeLoadingFooter();
                isLoading = false;
                adapter.addAll(posts);
                adapter.addLoadingFooter();
            }
        } else {
            if (adapter != null) {
                adapter.removeLoadingFooter();
                isLoading = false;
                isLastPage = true;
            }
        }
    }

    private void setPosts() {
        tvNoData.setVisibility(GONE);
        rvFeedList.setVisibility(VISIBLE);
    }

    private void setNoPosts() {
        tvNoData.setVisibility(VISIBLE);
        rvFeedList.setVisibility(GONE);
    }

    private void showProgress() {
        llProgress.setVisibility(VISIBLE);
    }

    private void hideProgress() {
        llProgress.setVisibility(GONE);
    }
}

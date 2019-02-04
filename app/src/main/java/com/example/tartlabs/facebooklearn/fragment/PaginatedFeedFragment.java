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
import com.example.tartlabs.facebooklearn.model.Post;

import java.util.ArrayList;
import java.util.Collections;

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

    public PaginatedFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
    }

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

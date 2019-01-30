package com.example.tartlabs.facebooklearn.adapter.feedpaginate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.adapter.FeedFragmentAdapter;
import com.example.tartlabs.facebooklearn.model.Post;

import java.util.List;

public class FeedPaaginateAdapter extends RecyclerView.Adapter<PaginateFeed> {

    private List<Post> postList;
    private FeedFragmentAdapter.feedListener listener;

    public void setListener(FeedFragmentAdapter.feedListener listener) {
        this.listener = listener;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }


    @NonNull
    @Override
    public PaginateFeed onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.blog_list_item, viewGroup, false);
        return new PaginateFeed(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PaginateFeed paginateFeed, int i) {
        paginateFeed.setFeed(postList.get(i));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void addAll(List<Post> newUsers) {
        int initialSize = postList.size();
        postList.addAll(newUsers);
        notifyItemRangeInserted(initialSize, newUsers.size());
    }

    public String getLastItemId() {
        return postList.get(postList.size() - 1).getId();
    }
}

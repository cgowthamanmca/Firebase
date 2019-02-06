package com.example.tartlabs.facebooklearn.adapter.feedpaginate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.adapter.FeedFragmentAdapter;
import com.example.tartlabs.facebooklearn.adapter.feed.viewHolder.PaginateFeedVH;
import com.example.tartlabs.facebooklearn.model.Post;

import java.util.List;

public class FeedPaaginateAdapter extends RecyclerView.Adapter<PaginateFeedVH> {

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
    public PaginateFeedVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.blog_list_item, viewGroup, false);
        //return new PaginateFeedVH(view, listener);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PaginateFeedVH paginateFeedVH, int i) {
        paginateFeedVH.setFeed(postList.get(i));
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

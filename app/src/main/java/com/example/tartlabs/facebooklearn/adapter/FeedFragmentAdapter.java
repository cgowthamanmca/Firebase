package com.example.tartlabs.facebooklearn.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.model.Post;

import java.util.List;

public class FeedFragmentAdapter extends RecyclerView.Adapter<feedVh> {

    private List<Post> postList;
    private feedListener listener;

    public void setListener(feedListener listener) {
        this.listener = listener;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public feedVh onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.blog_list_item, parent, false);
        return new feedVh(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull feedVh feedVh, int i) {
        feedVh.setFeed(postList.get(i));
    }

    @Override
    public int getItemCount() {
        if (postList != null) {
            return postList.size();
        } else {
            return 0;
        }

    }

    public interface feedListener {
        public void Like(Post post, int pos);
    }

    public void setLike(int pos) {
        postList.get(pos).setLike(true);
        notifyItemChanged(pos);
    }

    public void unLike(int pos) {
        postList.get(pos).setLike(false);
        notifyItemChanged(pos);
    }

}

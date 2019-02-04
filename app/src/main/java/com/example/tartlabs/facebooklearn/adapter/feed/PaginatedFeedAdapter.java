package com.example.tartlabs.facebooklearn.adapter.feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.adapter.feed.viewHolder.LoadMoreVH;
import com.example.tartlabs.facebooklearn.adapter.feed.viewHolder.PaginateFeedVH;
import com.example.tartlabs.facebooklearn.model.Post;

import java.util.List;

public class PaginatedFeedAdapter extends RecyclerView.Adapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOAD_MORE = 1;

    private List<Post> postList;
    private PaginatedFeedItemClickListener listener;
    private boolean isLoadingAdded;

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public void setListener(PaginatedFeedItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        switch (viewType) {
            case TYPE_ITEM:
                View item = inflater.inflate(R.layout.blog_list_item, parent, false);
                return new PaginateFeedVH(item, listener);
            case TYPE_LOAD_MORE:
                View loadMore = inflater.inflate(R.layout.load_more, parent, false);
                return new LoadMoreVH(loadMore);
            default:
                throw new RuntimeException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case TYPE_ITEM:
                Post post = postList.get(position);
                PaginateFeedVH paginateFeedVH = (PaginateFeedVH) viewHolder;
                paginateFeedVH.setFeed(post);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == postList.size() - 1 && isLoadingAdded) {
            return TYPE_LOAD_MORE;
        } else {
            // In future different item may come
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    /*Pagination helpers*/
    public void add(Post post) {
        postList.add(post);
        notifyItemInserted(postList.size() - 1);
    }

    public void addAll(List<Post> postList) {
        for (Post post : postList) {
            add(post);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Post());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        removePost();
    }

    private void removePost() {
        int position = postList.size() - 1;
        Post item = getItem(position);

        if (item != null) {
            postList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Post getItem(int position) {
        if (!postList.isEmpty()) {
            return postList.get(position);
        } else {
            return null;
        }
    }

    public interface PaginatedFeedItemClickListener {

    }
}

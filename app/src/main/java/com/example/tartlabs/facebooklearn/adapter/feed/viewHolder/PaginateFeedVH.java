package com.example.tartlabs.facebooklearn.adapter.feed.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tartlabs.facebooklearn.MediaGridView;
import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.adapter.FeedFragmentAdapter;
import com.example.tartlabs.facebooklearn.adapter.feed.PaginatedFeedAdapter;
import com.example.tartlabs.facebooklearn.model.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class PaginateFeedVH extends RecyclerView.ViewHolder {

    private ImageView blog_user_image;
    private TextView blog_user_name;
    private TextView blog_date;
    private MediaGridView blog_image;
    private TextView blog_desc;
    private TextView blog_like_count;
    private TextView blog_comment_count;
    private ImageView blog_like_btn;

    private PaginatedFeedAdapter.PaginatedFeedItemClickListener listener;
    private Post post;

    public PaginateFeedVH(@NonNull View itemView, final PaginatedFeedAdapter.PaginatedFeedItemClickListener listener) {
        super(itemView);

        this.listener = listener;

        initViews(itemView);
    }

    private void initViews(View itemView) {
        blog_user_image = itemView.findViewById(R.id.blog_user_image);
        blog_user_name = itemView.findViewById(R.id.blog_user_name);
        blog_date = itemView.findViewById(R.id.blog_date);
        blog_image = itemView.findViewById(R.id.blog_image);
        blog_desc = itemView.findViewById(R.id.blog_desc);
        blog_like_count = itemView.findViewById(R.id.blog_like_count);
        blog_comment_count = itemView.findViewById(R.id.blog_comment_count);
        blog_like_btn = itemView.findViewById(R.id.blog_like_btn);
    }

    public void setFeed(Post post) {
        this.post = post;

        blog_user_name.setText(post.getUser().getName());
        blog_desc.setText(post.getDescription());
        Picasso.get().load(post.getUser().getProfile_image()).into(blog_user_image);
        //Picasso.get().load(post.getImage()).into(blog_image);
        blog_image.setImages(post.getImage());
        blog_like_count.setText("" + post.getLike_count());
        if (post.getLike() != null) {
            if (post.getLike()) {
                Picasso.get().load(R.mipmap.action_like_accent).into(blog_like_btn);
            } else {
                Picasso.get().load(R.mipmap.action_like_gray).into(blog_like_btn);
            }
        }
    }
}

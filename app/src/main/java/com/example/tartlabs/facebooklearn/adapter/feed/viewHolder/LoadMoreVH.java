package com.example.tartlabs.facebooklearn.adapter.feed.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tartlabs.facebooklearn.R;

public class LoadMoreVH extends RecyclerView.ViewHolder {
    private LinearLayout llLoader;

    public LoadMoreVH(@NonNull View itemView) {
        super(itemView);
        llLoader = itemView.findViewById(R.id.llLoader);

        llLoader.setVisibility(View.VISIBLE);
    }
}

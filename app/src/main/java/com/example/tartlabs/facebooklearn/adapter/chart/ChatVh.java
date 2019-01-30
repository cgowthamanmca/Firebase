package com.example.tartlabs.facebooklearn.adapter.chart;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.model.User;
import com.squareup.picasso.Picasso;

public class ChatVh extends RecyclerView.ViewHolder {

    ImageView ivUserImage;
    TextView tvChatUserName;

    public ChatVh(@NonNull View itemView, ChatUserAdapter.onChatClickListerner listener) {
        super(itemView);
        tvChatUserName = itemView.findViewById(R.id.tvChatUserName);
        ivUserImage = itemView.findViewById(R.id.ivUserImage);

    }

    public void setUser(User user) {
        if (user != null) {
            tvChatUserName.setText(user.getName());
            if (user.getProfile_image() != null) {
                Picasso.get().load(user.getProfile_image()).into(ivUserImage);
            }

        }
    }
}

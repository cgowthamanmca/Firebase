package com.example.tartlabs.facebooklearn.adapter.chart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.model.User;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatVh> {

    private onChatClickListerner listener;
    private List<User> userList;

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setListener(onChatClickListerner listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatVh onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_user_item, parent, false);
        return new ChatVh(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVh chatVh, int i) {
        chatVh.setUser(userList.get(i));
    }

    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
        } else {
            return 0;
        }
    }

    public interface onChatClickListerner {
        public void onChatUser(User user);
    }
}

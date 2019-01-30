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
import android.widget.ProgressBar;

import com.example.tartlabs.facebooklearn.R;
import com.example.tartlabs.facebooklearn.adapter.chart.ChatUserAdapter;
import com.example.tartlabs.facebooklearn.model.ChatUser;
import com.example.tartlabs.facebooklearn.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserFragment extends Fragment implements ChatUserAdapter.onChatClickListerner{

    RecyclerView recyclerView;
    private ProgressBar reg_progress;

    private DatabaseReference databaseReference;
    private ChatUserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseAuth auth;

    public ChatUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvChatUser);
        reg_progress = view.findViewById(R.id.reg_progress);
        reg_progress.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new ChatUserAdapter();
        adapter.setListener(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                reg_progress.setVisibility(View.GONE);
                User user = dataSnapshot.child("user").getValue(User.class);
                if (!auth.getCurrentUser().getUid().equals(user.getUser_id())) {
                    userList.add(user);
                    adapter.setUserList(userList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        userList.clear();
    }

    @Override
    public void onChatUser(User user) {

    }
}

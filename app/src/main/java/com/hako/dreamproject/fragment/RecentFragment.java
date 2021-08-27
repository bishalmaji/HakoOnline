package com.hako.dreamproject.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hako.dreamproject.R;
import com.hako.dreamproject.adapters.chat.GroupChatAdapter;
import com.hako.dreamproject.chatActivity;
import com.hako.dreamproject.model.chatRoom;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.UsableFunctions;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;


public class RecentFragment extends Fragment {
    // Root View
    private View root;

    // RecyclerView
    private RecyclerView rvGroupChats;

    // Firebase
    FirebaseFirestore db;
    ImageView empty_chat_imgs;
    FirestoreRecyclerAdapter<chatRoom,RecentChatHolder> adapter;

    public RecentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_chat, container, false);

        String userUniqueId = AppController.getInstance().getUser_unique_id();
        if (userUniqueId ==null){
            userUniqueId =AppController.getInstance().sharedPref.getString("userUniqueId","12345");
        }
        initViews();
        if (UsableFunctions.checkLoggedInOrNot()) {
            getChatRoomsFromFirebase(userUniqueId);
        }
        return root;
    }

    private void initViews() {
        //firebase
        db = FirebaseFirestore.getInstance();

        // RecyclerView
        empty_chat_imgs = root.findViewById(R.id.empty_chat_img);
        try {
            Glide.with(this).load(R.drawable.message_screen).into(empty_chat_imgs);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }

        rvGroupChats = root.findViewById(R.id.rv_chat_groupChat);
    }

    private void getChatRoomsFromFirebase(String myId) {
        Query query = db.collection("USERS").document(myId)
                .collection("chatRooms")
                .whereEqualTo("play","y").orderBy("chatRoomId", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<chatRoom> options=new FirestoreRecyclerOptions.Builder<chatRoom>()
                .setQuery(query,chatRoom.class).build();
        adapter= new FirestoreRecyclerAdapter<chatRoom, RecentChatHolder>(options) {
            @NonNull
            @Override
            public RecentChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card, parent, false);
                return new RecentChatHolder(holder);
            }

            @Override
            protected void onBindViewHolder(@NonNull RecentChatHolder holder, int position, @NonNull chatRoom model) {
                Glide.with(getActivity()).load(model.getFriendProfile()).circleCrop().into(holder.ivGroupChatImage);
                holder.tvChatTitle.setText(model.getFriendName());
                holder.itemView.setOnClickListener( view -> {
                    Intent intent = new Intent(getActivity(), chatActivity.class);
                    intent.putExtra("chatRoomId", model.getChatRoomId());
                    intent.putExtra("myScore", model.getMyScore());
                    intent.putExtra("freindScore", model.getFriendScore());
                    intent.putExtra("freindProfile", model.getFriendProfile());
                    intent.putExtra("freindName", model.getFriendName());
                    intent.putExtra("reciverId", model.getFriendId());
                    intent.putExtra("firstMsg",model.isFirstMsg());
                    startActivity(intent);
                });
                ClickShrinkEffectKt.applyClickShrink(holder.itemView);
            }
        };
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        rvGroupChats.setAdapter(new AlphaInAnimationAdapter(adapter));
        adapter.startListening();
        rvGroupChats.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rvGroupChats.setHasFixedSize(true);
        rvGroupChats.setItemAnimator(null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }
    static class RecentChatHolder extends RecyclerView.ViewHolder {
        ImageView ivGroupChatImage,ivStatus;
        TextView tvChatTitle;

        public RecentChatHolder(@NonNull View itemView) {
            super(itemView);
            ivGroupChatImage = itemView.findViewById(R.id.iv_charCard_groupChatImage);
            tvChatTitle = itemView.findViewById(R.id.tv_chatCard_chatTitle);
            ivStatus=itemView.findViewById(R.id.iv_stat);
        }

    }
}

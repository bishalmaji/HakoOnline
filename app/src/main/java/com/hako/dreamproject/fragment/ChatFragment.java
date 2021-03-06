package com.hako.dreamproject.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hako.dreamproject.R;
import com.hako.dreamproject.chatActivity;
import com.hako.dreamproject.model.chatRoom;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.UsableFunctions;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class ChatFragment extends Fragment {

    // Root View
    private View root;

    // RecyclerView
    private RecyclerView rvGroupChats;

    // Firebase
    FirebaseFirestore db;
    ImageView empty_chat_imgs;
    // String
    String TAG_CHAT_FRAGMENT = "chatFragment";
    FirestoreRecyclerAdapter<chatRoom,ChatViewHolder> adapter;
    String userUniqueId;

    //for online status
    ArrayList<String > statarray;
    int i=0;
    boolean stat;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_chat, container, false);
        statarray=new ArrayList<>();
        userUniqueId = AppController.getInstance().sharedPref.getString("suserUniqueId","useruid");

        initViews();
        if (UsableFunctions.checkLoggedInOrNot()) {
            getChatRoomsFromFirebase(userUniqueId);
        }

        return root;
    }

    private void initViews() {
        db = FirebaseFirestore.getInstance();
        empty_chat_imgs = root.findViewById(R.id.empty_chat_img);
        rvGroupChats = root.findViewById(R.id.rv_chat_groupChat);
        try {
            Glide.with(this).load(R.drawable.message_screen).into(empty_chat_imgs);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }


    }

    private void getChatRoomsFromFirebase(String myId) {
        Query query = db.collection("USERS").document(myId).collection("chatRooms")
                .orderBy("chatRoomId", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<chatRoom> options=new FirestoreRecyclerOptions.Builder<chatRoom>()
                .setQuery(query,chatRoom.class).build();
         adapter= new FirestoreRecyclerAdapter<chatRoom, ChatViewHolder>(options) {
            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card, parent, false);
                return new ChatViewHolder(holder);
            }

             @Override
             public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
                 super.onAttachedToRecyclerView(recyclerView);
                AddOnlineStatusFunction();

             }

             @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull chatRoom model) {
                statarray.add(model.getFriendId());
                Glide.with(getContext()).load(model.getFriendProfile()).circleCrop().into(holder.ivGroupChatImage);
                if (model.isOnline()){
                    holder.ivStatus.setVisibility(View.VISIBLE);
                }
                holder.tvLastMsg.setText(model.getLastMsg());
                holder.tvChatTitle.setText(model.getFriendName());
                holder.itemView.setOnClickListener( view -> {
                    Intent intent = new Intent(getActivity(), chatActivity.class);
                    intent.putExtra("chatRoomId", model.getChatRoomId());
                    intent.putExtra("myScore", model.getMyScore());
                    intent.putExtra("freindScore", model.getFriendScore());
                    intent.putExtra("freindProfile", model.getFriendProfile());
                    intent.putExtra("freindName", model.getFriendName());
                    intent.putExtra("reciverId", model.getFriendId());
                    intent.putExtra("chatRoom",true);
                    startActivity(intent);
                });
                ClickShrinkEffectKt.applyClickShrink(holder.itemView);
            }
        };
         setUpRecyclerView();
    }

    private void AddOnlineStatusFunction() {
        class AddOnlineStatus extends AsyncTask<String, String, Boolean> {
            @Override
            protected Boolean doInBackground(String... strings) {
                db.collection("USERS").document(statarray.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        stat= task.getResult().getBoolean("online");
                    }
                });
                return stat;
            }

            @Override
            protected void onPostExecute(Boolean b) {
                adapter.getItem(i).setOnline(b);
                adapter.notifyItemChanged(i);
                if (statarray.size()>i){
                    i++;
                    AddOnlineStatusFunction();
                }
                super.onPostExecute(b);
            }
        }
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

    class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGroupChatImage,ivStatus;
        TextView tvChatTitle,tvLastMsg;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGroupChatImage = itemView.findViewById(R.id.iv_charCard_groupChatImage);
            tvChatTitle = itemView.findViewById(R.id.tv_chatCard_chatTitle);
            ivStatus=itemView.findViewById(R.id.iv_stat);
            tvLastMsg=itemView.findViewById(R.id.lastmessage);
        }
}


}

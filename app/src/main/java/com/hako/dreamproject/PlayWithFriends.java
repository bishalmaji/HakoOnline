package com.hako.dreamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hako.dreamproject.dialog.showInviteDialog;
import com.hako.dreamproject.model.chatRoom;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.UsableFunctions;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayWithFriends extends AppCompatActivity {

    public FirebaseFirestore db;

    TextView listView;

    ImageView fbIcon, whatsappIcon, messengerIcon, linkIcon, moreIcon;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_with_friends);

        db = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.list_item);

        //Icon ImageViews
        fbIcon = findViewById(R.id.fb_icon);
        whatsappIcon = findViewById(R.id.whatsapp_icon);
        messengerIcon = findViewById(R.id.messenger_icon);
        linkIcon = findViewById(R.id.link_icon);
        moreIcon = findViewById(R.id.more_icon);

        //Editext
        editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener((v, actionId, event) ->  {
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                String num = editText.getText().toString();
                if (!num.isEmpty()) {
                    setInvitationFriend(num);
                }
            }
            return  false;
        });
        //Onclick For the icons
        fbIcon.setOnClickListener(v -> {

            sendWhatsapp("");
        });

        whatsappIcon.setOnClickListener(v -> {
            sendWhatsapp("APP LINK");
        });

        messengerIcon.setOnClickListener(v -> {
            sendWhatsapp("");
        });

        linkIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Link has been copied!", Toast.LENGTH_LONG).show();
        });

        moreIcon.setOnClickListener(v-> {
           sendWhatsapp("");
        });

        getChatRoomsFromFirebase(AppController.getInstance().getUser_unique_id());
    }

    private void setInvitationFriend(String friendId){
        String invitationId = UsableFunctions.getInvitationId();
        String myId = AppController.getInstance().getUser_unique_id();
        String chatRoomId = UsableFunctions.getMessageRoomId();
        Log.d("Play with friends", "friendId: " + friendId + " InvitationId: " + invitationId + "myId: " + myId);
        HashMap<String, String> inviteMap = new HashMap<>();
        inviteMap.put("senderId", myId);
        inviteMap.put("senderName", AppController.getInstance().getName());
        inviteMap.put("senderImage", AppController.getInstance().getProfile());
        inviteMap.put("chatRoomId", chatRoomId);
        inviteMap.put("receiverId", friendId);

        //
        // it sets the data named invitaion in to our app
        db.collection("INVITATION")
                .document(invitationId)
                .set(inviteMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Invitation Sent", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), chatActivity.class);
                    intent.putExtra("chatRoomId", chatRoomId);
                    intent.putExtra("reciverId", friendId);
                    startActivity(intent);

                })
                .addOnFailureListener(e -> {
                    Log.d("Play with friends", "Failed due to: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendWhatsapp(String message){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }


    private void getChatRoomsFromFirebase(String myId){
        Log.d("PLAY_WITH_FRIENDS", "myId: " + myId);
        CollectionReference colRef = db.collection("USERS").document(myId)
                .collection("chatRooms");

        colRef.orderBy("chatRoomId", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<chatRoom> chatRooms = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                try{
                                    chatRooms.add(document.toObject(chatRoom.class));
                                }catch (Exception e){
                                    Log.e("Play_with_friends", "95 Exception: " + e.getMessage());
                                }
                            }
                            String s = "";
                            for(int i=0;i<chatRooms.size();i++) {
                                s = s + chatRooms.get(i).getFriendName()+" \n"+" \n";
                            }
                            listView.setText(s);
                        }
                    }
                });
    }
}
package com.hako.dreamproject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hako.dreamproject.fragment.ChatTabHolderFragment;
import com.hako.dreamproject.fragment.HomeFragment;
import com.hako.dreamproject.fragment.ProfileFragment;
import com.hako.dreamproject.fragment.RewardFragment;
import com.hako.dreamproject.model.chatRoom;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.UsableFunctions;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.hako.dreamproject.utils.Constant.UPDATEURL;

public class HomeActivity extends AppCompatActivity {
   String myId;
    LinearLayout homePressed;
    LinearLayout chatPressed;
    LinearLayout rewardPressed;
    LinearLayout mePressed;
    ImageView homeIcon;
    ImageView chatIcon;
    ImageView rewardIcon;
    ImageView meIcon;

    List<String> invitationList;
    // TAG
    String TAG_HOME_ACTIVITY = "homeActivity";

    // CardView
    CardView caViewInvitation;

    // TextView
    TextView tvFreindInvitationName;

    // ImageView
    ImageView ivFreindInvitationProfile;

    // Button
    TextView btnInvitationAccept, btnInvitationReject,btn_notnow;

    // Firebase
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setViews();
        setClickShrinkEffect();

        homePressed = findViewById(R.id.home);
        chatPressed = findViewById(R.id.chat);
        rewardPressed = findViewById(R.id.reward);
        mePressed = findViewById(R.id.me);
        homeIcon = findViewById(R.id.hi);
        chatIcon = findViewById(R.id.ch);
        rewardIcon = findViewById(R.id.ri);
        meIcon = findViewById(R.id.mi);

        loadFragment(new HomeFragment());

        ClickShrinkEffectKt.applyClickShrink(homePressed);
        ClickShrinkEffectKt.applyClickShrink(chatPressed);
        ClickShrinkEffectKt.applyClickShrink(rewardPressed);
        ClickShrinkEffectKt.applyClickShrink(mePressed);

        homeIcon.setImageResource(R.drawable.joystick_selected);

        homePressed.setOnClickListener(view -> {
            loadFragment(new HomeFragment());
            home();
        });
        chatPressed.setOnClickListener(view -> {
            Log.d("lifecheck", "chatpressed: homeactivity ");
            loadFragment(new ChatTabHolderFragment());
            chat();
        });
        rewardPressed.setOnClickListener(view -> {
            loadFragment(new RewardFragment());
            reward();
        });
        mePressed.setOnClickListener(view -> {
            loadFragment(new ProfileFragment());
            me();
        });
        if(!AppController.getInstance().sharedPref.getString("suserid","12345").equalsIgnoreCase("0")){
           try{
               if(AppController.getInstance().sharedPref.getString("sverson","verson")!=null){
                   // for update check
                   updateCheck();
               }

           }catch (Exception e){
               e.printStackTrace();
           }
        }

        if(UsableFunctions.checkLoggedInOrNot()){
            setInviteListner();
        }
    getFbData();
    }
   public String  points="500";
    public String profilePic="null";
    public String name="UserName";
    private void getFbData() {
        FirebaseFirestore.getInstance().collection("ProfileData").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            points=task.getResult().getLong("points").toString();
                            profilePic=task.getResult().getString("profile");
                            name=task.getResult().getString("name");
                            AppController.getInstance().sharedPref.edit().putString("spoints",points).apply();
                            AppController.getInstance().sharedPref.edit().putString("sname",points).apply();
                            AppController.getInstance().sharedPref.edit().putString("sprofile",profilePic).apply();

                        }
                    }
                });
    }


    private void setViews(){
        //CardView
        caViewInvitation = findViewById(R.id.cardView_homeActivity_invitation);

        //ImageView
        ivFreindInvitationProfile = findViewById(R.id.iv_homeActivityCardView_freindProfile);

        //TextView
        tvFreindInvitationName = findViewById(R.id.tv_homeActivityCardView_freindName);
        // for making the visibility invisible


        //Button
        btnInvitationAccept = findViewById(R.id.btn_homeActivityCardView_accept);
        btnInvitationReject = findViewById(R.id.btn_homeActivityCardView_reject);
        btn_notnow  = findViewById(R.id.btn_homeActivityCardView_notnow );

        //firebase
        db = FirebaseFirestore.getInstance();
    }
    private void setClickShrinkEffect(){
        ClickShrinkEffectKt.applyClickShrink(btnInvitationAccept);
        ClickShrinkEffectKt.applyClickShrink(btnInvitationReject);
    }

    private void setInviteListner(){
        invitationList = new ArrayList<>();
        myId = AppController.getInstance().sharedPref.getString("suserUniqueId","useruid");
        if (myId==null){
           myId= AppController.getInstance().sharedPref.getString("userUniqueId","12345");
        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkInvitation(myId);
            }
        }, 5000, 5000);
    }
    private void checkInvitation(String myId){
        CollectionReference colRef = db.collection("INVITATION");
        colRef.whereEqualTo("receiverId", myId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!invitationList.contains(document.getId())){
                                    invitationList.add(document.getId());
                                    showInviteDialog(document.getData(), document.getId());
                                }
                            }
                        }
                    }
                });
    }
    private void showInviteDialog(Map<String, Object> data, String invitationId){
        caViewInvitation.setVisibility(View.VISIBLE);
        String myName = AppController.getInstance().sharedPref.getString("sname","name");
        String myProfile = AppController.getInstance().sharedPref.getString("sprofile","profile");
        String senderId = (String) data.get("senderId");
        String freindName = (String) data.get("senderName");
        String freindProfileImage = (String) data.get("senderImage");
        String chatRoomId = (String) data.get("chatRoomId");

        tvFreindInvitationName.setText(freindName + " invite you to play games");
        Glide.with(this)
                .load(freindProfileImage)
                .circleCrop()
                .placeholder(getDrawable(R.drawable.profile_holder))
                .into(ivFreindInvitationProfile);

        btnInvitationReject.setOnClickListener( view -> {
            deleteInvitationRequest(invitationId);
            caViewInvitation.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show();
        });
        btnInvitationAccept.setOnClickListener( view -> {
            addChatRoomInUser(chatRoomId, myId, myName, myProfile, senderId, freindName, freindProfileImage);
            deleteInvitationRequest(invitationId);
            caViewInvitation.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, chatActivity.class);
            intent.putExtra("chatRoomId", chatRoomId);
            intent.putExtra("reciverId", senderId);
            startActivity(intent);
        });
        btn_notnow.setOnClickListener( view -> {
           /* addChatRoomInUser(chatRoomId,
                    myId, myName, myProfile,
                    senderId, freindName, freindProfileImage);*/


           // deleteInvitationRequest(invitationId);
            // can we do something with invitaton id

            caViewInvitation.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Not Now", Toast.LENGTH_SHORT).show();
        });

    }

    private void addChatRoomInUser(String chatRoomId,String myId, String myName, String myProfile,
                                   String freindId, String freindName, String freindProfileImage){
        DocumentReference docRef = db.collection("USERS").document(myId)
                .collection("friendRooms").document(chatRoomId);
        chatRoom myChatRoom = new chatRoom(chatRoomId, "0", freindId, freindName, "0", freindProfileImage,  true, "New friend is added to your friend list");
        docRef.set(myChatRoom);

        // Freind room
        DocumentReference freindRef = db.collection("USERS").document(freindId)
                .collection("friendRooms").document(chatRoomId);
        chatRoom freindChatRoom = new chatRoom(chatRoomId, "0", myId, myName, "0", myProfile, false, "New friend is added to your friend list");
        freindRef.set(freindChatRoom);
    }

    private void deleteInvitationRequest(String invitationId){
        DocumentReference docRef = db.collection("INVITATION").document(invitationId);
        docRef.delete();
    }

    public void home() {
        homeIcon.setImageResource(R.drawable.joystick_selected);
        chatIcon.setImageResource(R.drawable.chat);
        rewardIcon.setImageResource(R.drawable.medal);
        meIcon.setImageResource(R.drawable.man);
    }
    private void chat(){
        homeIcon.setImageResource(R.drawable.joystick);
        chatIcon.setImageResource(R.drawable.chat_selected);
        rewardIcon.setImageResource(R.drawable.medal);
        meIcon.setImageResource(R.drawable.man);
    }
    public void reward() {
        homeIcon.setImageResource(R.drawable.joystick);
        chatIcon.setImageResource(R.drawable.chat);
        rewardIcon.setImageResource(R.drawable.medal_selected);
        meIcon.setImageResource(R.drawable.man);
    }
    public void me() {
        homeIcon.setImageResource(R.drawable.joystick);
        chatIcon.setImageResource(R.drawable.chat);
        rewardIcon.setImageResource(R.drawable.medal);
        meIcon.setImageResource(R.drawable.man_selected);
    }


    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    public void updateCheck() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int version = pInfo.versionCode;
            String TAG_VERSION = "versionCode";
            Log.i(TAG_VERSION, "verrsion: " + AppController.getInstance().sharedPref.getString("sverson","verson"));

            if (Integer.parseInt(AppController.getInstance().sharedPref.getString("sverson","verson")) != version) {
                new AlertDialog.Builder(HomeActivity.this, R.style.Theme_AppCompat_Dialog_Alert)
                        .setMessage("Update Available Download our Latest UPDATE")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            Intent browserIntent = null;
                            if (AppController.getInstance().sharedPref.getString("suserid","12345").equalsIgnoreCase("0")) {
                                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UPDATEURL));
                            } else {
                                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppController.getInstance().sharedPref.getString("sapk","apk")));
                            }
                            startActivity(browserIntent);
                        })
                        .setNegativeButton(android.R.string.no, (dialogInterface, i) -> finish())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String,Boolean> data=new HashMap<>();
        data.put("online",true);
        db.collection("USERS").document(myId).set(data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.collection("USERS").document(myId).update("online",false);
    }
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
}

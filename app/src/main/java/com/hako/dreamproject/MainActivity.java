package com.hako.dreamproject;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;
import com.hako.dreamproject.model.Online;
import com.hako.dreamproject.model.chatRoom;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.DATA;
import static com.hako.dreamproject.utils.Constant.ENTRY;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.GAMEICON;
import static com.hako.dreamproject.utils.Constant.GAMEURL;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.PIC;
import static com.hako.dreamproject.utils.Constant.PlAYER;
import static com.hako.dreamproject.utils.Constant.ROOMID;
import static com.hako.dreamproject.utils.Constant.ROTATION;
import static com.hako.dreamproject.utils.Constant.SCORE;
import static com.hako.dreamproject.utils.Constant.SEARCH;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.TRUE;
import static com.hako.dreamproject.utils.Constant.USERID;

public class MainActivity extends AppCompatActivity {
    String userid;
    DatabaseReference databaseReference;
    String myUserid;
    String secondUserid;
    String score = "0";
    long time = System.currentTimeMillis();
    WebView webView;
    String gameId;
    String currrentPageUrl;
    String points = "0";
    String rotation = "0";
    String playerId;
    String name;
    String pic;
    String icon;
    String entryFee;
    String roomid;
    boolean check = true;
    String gname;

    ConstraintLayout clPokerTable;

    String TAG_MAIN_ACTIVITY = "mainActivity";

    JSONObject chatObject = new JSONObject();

    ImageView userImageView, friendImageView;

    ImageView imageView;

    TextView player1, player2;

    String playerName, playerImg, myName;
    //variables for adding chat
    List<String> invitationList;

    @SuppressLint({"ObsoleteSdkInt", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        setContentView(R.layout.activity_main);
        invitationList = new ArrayList<>();
        webView = findViewById(R.id.webView_playGameActivity_playGame);
        playerName = getIntent().getStringExtra("playerName");
        playerImg = getIntent().getStringExtra("playerImg");
        myName = getIntent().getStringExtra("myName");

        userImageView = findViewById(R.id.userProfileImage);
        clPokerTable = findViewById(R.id.cl_mainActivity_pokerTable);
        imageView = findViewById(R.id.micView);
        friendImageView = findViewById(R.id.friendProfileImage);
        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String data = extras.getString(DATA);
            try {
                JSONObject extra = new JSONObject(data);
                chatObject = extra.getJSONObject("Data");
                currrentPageUrl = extra.getString(GAMEURL);
                Log.d(TAG_MAIN_ACTIVITY, "url: " + currrentPageUrl);
                Log.d(TAG_MAIN_ACTIVITY, "chatRoomId: " + chatObject.getString("chatRoomId"));
                myUserid = AppController.getInstance().sharedPref.getString("suserid","12345");
                gameId = extra.getString(ROOMID);
                rotation = extra.getString(ROTATION);
                secondUserid = extra.getString(PlAYER);
                playerId = secondUserid;
                name = extra.getString(NAME);
                gname = extra.getString("gname");
                pic = extra.getString(PIC);
                icon = extra.getString(GAMEICON);
                entryFee = extra.getString(ENTRY);
                roomid = gameId;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG_MAIN_ACTIVITY, "In Catch: " +  e.getMessage());
            }
            if (rotation.matches("1")) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//Set Landscape
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Set Portrait
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            try{
                webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
                webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
                webView.getSettings().setAllowFileAccess(true);
                webView.getSettings().setAppCacheEnabled(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webView.loadUrl(currrentPageUrl);
//                ChromeClient webViewClient = new ChromeClient(this);
//                webView.setWebChromeClient(webViewClient);
                WebViewClientImpl webViewClients = new WebViewClientImpl(this);
                webView.setWebViewClient(webViewClients);
            }
            catch (Exception e){
                Log.e(TAG_MAIN_ACTIVITY, "In Extras != null msg: " + e.getMessage());
            }
        }

        Glide.with(this).load(R.drawable.coin_gif).into(imageView);

        Glide.with(this)
                .load(AppController.getInstance().sharedPref.getString("sprofile","profile"))
                .placeholder(R.drawable.profile_holder)
                .centerCrop()
                .circleCrop()
                .into(userImageView);

        Glide.with(this)
                .load(playerImg)
                .placeholder(R.drawable.profile_holder)
                .centerCrop()
                .circleCrop()
                .into(friendImageView);

        player1.setText(myName);
        player2.setText(playerName);

        final int[] flag = {0};

     /*   new Handler().postDelayed(() -> {
            imageView.setVisibility(View.GONE);
        }, 4500);*/

        ImageView playButton = findViewById(R.id.micView);

      /*  new Handler().postDelayed(() -> {
            playButton.setVisibility(View.VISIBLE);
        }, 5000);*/

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add firebase data here
                addChatRoomInUser();
                flag[0] = 1;
                webView.setVisibility(View.VISIBLE);
                clPokerTable.setVisibility(View.GONE);
            }
        });

//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            Log.d("MainActivity","SDk version above android L so forcibaly enabling ThirdPartyCookies");
//            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
//        }

     /*   new Handler().postDelayed(() -> {
            if(flag[0] ==0) {
                webView.setVisibility(View.VISIBLE);
                clPokerTable.setVisibility(View.GONE);
            }
        }, 1000);*/

            databaseReference.child(secondUserid)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Online user = dataSnapshot.getValue(Online.class);
                    try {
                        if (user != null) {
                            Log.e(TAG_MAIN_ACTIVITY, "In Handler Online: " + user.getOnline() + ", Time " + user.getTime() + ", Score " + user.getScore() + ", status " + user.getStatus());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG_MAIN_ACTIVITY, "In Database Catch Failed to read value: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG_MAIN_ACTIVITY, "In Databse onCanclled Failed to read value.", error.toException());
                }
            });

        /////// GET SECOND USER /////////////////////////////

    }
    private void addChatRoomInUser(){

        if (AppController.getInstance().sharedPref.getString("suserUniqueId","useruid")==null){
          userid=AppController.getInstance().sharedPref.getString("userUniqueId","12345");
        }else {
            userid =AppController.getInstance().sharedPref.getString("suserUniqueId","useruid");
        }
        //checking invite
        CollectionReference colRef = FirebaseFirestore.getInstance().collection("INVITATION");
        colRef.whereEqualTo("receiverId", userid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!invitationList.contains(document.getId())){
                                    invitationList.add(document.getId());
                                    //getting the datas
                                    String myName = AppController.getInstance().sharedPref.getString("sname","name");
                                    String myProfile = AppController.getInstance().sharedPref.getString("sprofile","profile");
                                    String senderId = (String) document.getData().get("senderId");
                                    String freindName = (String) document.getData().get("senderName");
                                    String freindProfileImage = (String) document.getData().get("senderImage");
                                    String chatRoomId = (String) document.getData().get("chatRoomId");
                                    // my room
                                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("USERS").document(userid)
                                            .collection("friendRooms").document(chatRoomId);
                                    Map<String,Object> myChatRoom=new HashMap<>();
                                    myChatRoom.put("chatRoomId",chatRoomId);
                                    myChatRoom.put("myScore","0");
                                    myChatRoom.put("friendId",senderId);
                                    myChatRoom.put("friendName",freindName);
                                    myChatRoom.put("friendScore","0");
                                    myChatRoom.put("friendProfile",freindProfileImage);
                                    myChatRoom.put("online",true);
                                    myChatRoom.put("lastMsg","New friend is added to your friend list");

                                    docRef.set(myChatRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                 // Freind room
                                            DocumentReference freindRef = FirebaseFirestore.getInstance().collection("USERS").document(senderId)
                                                    .collection("friendRooms").document(chatRoomId);
                                            Map<String,Object> freindChatRoom=new HashMap<>();
                                            freindChatRoom.put("chatRoomId",chatRoomId);
                                            freindChatRoom.put("myScore","0");
                                            freindChatRoom.put("friendId",userid);
                                            freindChatRoom.put("friendName",myName);
                                            freindChatRoom.put("friendScore","0");
                                            freindChatRoom.put("friendProfile",myProfile);
                                            freindChatRoom.put("online",false);
                                            freindChatRoom.put("lastMsg","New friend is added to your friend list");
                                            freindRef.set(freindChatRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    deleteInvitationRequest(document.getId());
                                                }
                                            });
                                        }
                                    });



                                }
                            }
                        }
                    }
                });



    }
    private void deleteInvitationRequest(String invitationId){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("INVITATION").document(invitationId);
        docRef.delete();
        Toast.makeText(this, "Invite added to chat ", Toast.LENGTH_LONG).show();
        Log.d("inviteChat", "deleteInvitationRequest: done");
    }

    public class ChromeClient extends WebChromeClient {
        Activity activity = null;

        public ChromeClient(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage msg) {
            if (msg.message().contains("wpwala")) {
                String data = msg.message().substring(msg.message().lastIndexOf("=") + 1);
                points = data;
                Online user = new Online(TRUE, time + "", points, "start");
                databaseReference.child(myUserid).setValue(user);

            }
            else if (msg.message().contains("overdata")) {
                String data = msg.message().substring(msg.message().lastIndexOf("=") + 1);
                points = data;
                if (check) {
                    addScore();
                    check = false;
                }
            }
            Log.d(TAG_MAIN_ACTIVITY, "In chromeCliend onConsoleMessage: " + msg.message());
            return true;
        }
    }
    public class WebViewClientImpl extends WebViewClient {
        Activity activity = null;

        public WebViewClientImpl(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Online user = new Online(FALSE, time + "", points, "paused");
        databaseReference.child(myUserid).setValue(user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Online user = new Online(TRUE, time + "", points, "start");
        databaseReference.child(myUserid).setValue(user);
    }

    public void openDialog() {
        JSONObject json = new JSONObject();
        try {
            Online user = new Online(FALSE, time + "", points, "paused");
            databaseReference.child(myUserid).setValue(user);
            json.put("icon", icon);
            json.put(SCORE, points);
            json.put(PlAYER, playerId);
            json.put(ENTRY, entryFee);
            json.put(PIC, pic);
            json.put(NAME, name);
            json.put("gname", gname);
            json.put(ROOMID, roomid);
            json.put(ROTATION, rotation);
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra(DATA, json.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.d(TAG_MAIN_ACTIVITY, "In openDialog catch: " + e.getMessage()
                    + " onjectsRequireNonNull: " + Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void addScore() {
        class logIn extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("update_score", API);
                params.put(TOKEN, AppController.getInstance().sharedPref.getString("stoken","token"));
                params.put(USERID, AppController.getInstance().sharedPref.getString("suserid","12345"));
                params.put("s_id", gameId);
                params.put(SEARCH, points);
                Log.d(TAG_MAIN_ACTIVITY, "In addSocre doInBackground params: " + params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject js = new JSONObject(s);
                    if (js.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        openDialog();
                    } else {
                        addScore();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        logIn ru = new logIn();
        ru.execute();
    }

    @Override
    public void onBackPressed() {
        try {
            String chatRoomId = chatObject.getString("chatRoomId");
            String myScore = chatObject.getString("myScore");
            String friendId = chatObject.getString("friendId");
            String friendScore = chatObject.getString("friendScore");
            String friendProfile = chatObject.getString("friendProfile");
            String friendName = chatObject.getString("friendName");
            goToChatActivity(chatRoomId, myScore,
                                friendId, friendScore, friendProfile, friendName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void goToChatActivity(String chatRoomId, String myScore,
                                  String freindId, String freindScore, String freindProfile, String freindName){
        Intent intent = new Intent(this, chatActivity.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("reciverId", freindId);
        intent.putExtra("myScore", Long.parseLong(myScore));
        intent.putExtra("freindProfile", freindProfile);
        intent.putExtra("freindName", freindName);
        startActivity(intent);
        finish();
    }

    //https://hoko.orsoot.com/games/sheepfight/index.html?playerusername=Mudssir Ahmed&playeravatarurl=https://lh3.googleusercontent.com/a/AATXAJxFKzBaAPUoIHNg_trmD6JP9jHCqQjXeUhgO0U1=s96-c&playerid=1&roomid=7
    //https://hoko.orsoot.com/games/sheepfight/index.html?playerusername=Muzammil Ahmed&playeravatarurl=https://lh3.googleusercontent.com/a-/AOh14GjV7gKst1gduxyoKm4b6zaVFsZMqqywdIadOYYK=s96-c&playerid=1&roomid=7
}
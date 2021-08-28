package com.hako.dreamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hako.dreamproject.model.GameDataModel;
import com.hako.dreamproject.model.Online;
import com.hako.dreamproject.utils.AppController;

import org.w3c.dom.Document;

import java.util.Map;

import static com.hako.dreamproject.utils.Constant.TRUE;

public class PlayGameRequestActivity extends AppCompatActivity {

    WebView webViewPlayGame;

    // Buttom View
    Button playButton;

    // ConstrintLayoput
    ConstraintLayout clStatus;

    // Imageview
    ImageView ivUserProfile, imageView, friendImageView, userImageView;

    // TextView
    TextView tvStatus, player1, player2;

    // DocumentReference
    DatabaseReference databaseReference;
    GameDataModel gameDataModel;
    String playerId;
    boolean check = false;
    boolean iWin = false;

    ConstraintLayout clPokerTable;

    String TAG = "playGameRequestActivity", friendImage = "", friendName = "", myName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game_request);

        String url = getIntent().getStringExtra("url");
        String rotation = getIntent().getStringExtra("rotation");
        String roomId = getIntent().getStringExtra("roomId");
        playerId = getIntent().getStringExtra("playerId");
        String myId = AppController.getInstance().sharedPref.getString("suserUniqueId","useruid");
        String chatRoomId = getIntent().getStringExtra("chatRoomId");
        friendImage = getIntent().getStringExtra("friendURL");
        myName = getIntent().getStringExtra("myName");
        friendName = getIntent().getStringExtra("friendName");

        Log.d(TAG, "InPlayGameRequestActivity url: " + url);
        Log.d(TAG, "InPlayGameRequestActivity rotation: " + rotation);

        setViews();

        if (rotation.matches("1")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//Set Landscape
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Set Portrait
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        playGame(url);
        setListnerOnRealTimeDataBase(roomId, playerId, myId, chatRoomId);

    }
    private void setViews(){
        //ButtomView
        playButton = findViewById(R.id.playButtom);

        // WebView
        webViewPlayGame = findViewById(R.id.webView_playGameActivity_playGame);

        // ConstraintLayput
        clStatus = findViewById(R.id.cl_playGameRequestActivity_status);

        // ImageView
        ivUserProfile = findViewById(R.id.iv_playGameRequestActivity_profile);
        imageView = findViewById(R.id.micView);
        friendImageView = findViewById(R.id.friendProfileImage);
        userImageView = findViewById(R.id.userProfileImage);

        clPokerTable = findViewById(R.id.cl_mainActivity_pokerTable);

        // TextView
        tvStatus = findViewById(R.id.tv_playGameRequestActivity_status);
        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);
    }
    private void playGame(String url){
        try{
            Glide.with(this)
                    .load(AppController.getInstance().sharedPref.getString("sprofile","profile"))
                    .placeholder(R.drawable.profile_holder)
                    .centerCrop()
                    .circleCrop()
                    .into(ivUserProfile);
            webViewPlayGame.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
            webViewPlayGame.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            webViewPlayGame.getSettings().setAllowFileAccess(true);
            webViewPlayGame.getSettings().setAppCacheEnabled(true);
            webViewPlayGame.getSettings().setJavaScriptEnabled(true);
            webViewPlayGame.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webViewPlayGame.loadUrl(url);
            ChromeClient webViewClient = new ChromeClient(this);
            webViewPlayGame.setWebChromeClient(webViewClient);
            WebViewClientImpl webViewClients = new WebViewClientImpl(this);
            webViewPlayGame.setWebViewClient(webViewClients);
        }
        catch (Exception e){
            Log.e(TAG, "In Extras != null msg: " + e.getMessage());
        }

        Glide.with(this).load(R.drawable.coin_gif).into(imageView);

        Glide.with(this)
                .load(AppController.getInstance().sharedPref.getString("sprofile","profile"))
                .placeholder(R.drawable.profile_holder)
                .centerCrop()
                .circleCrop()
                .into(userImageView);

        Glide.with(this)
                .load(friendImage)
                .placeholder(R.drawable.profile_holder)
                .centerCrop()
                .circleCrop()
                .into(friendImageView);

        player1.setText(myName);
        player2.setText(friendName);

        new Handler().postDelayed(() -> {
            imageView.setVisibility(View.GONE);
        }, 10000);

        final int[] flag = {0};

        new Handler().postDelayed(() ->
        {
            playButton.setVisibility(View.VISIBLE);
        }, 5000);

        playButton.setOnClickListener(v -> {
            flag[0] = 1;
            webViewPlayGame.setVisibility(View.VISIBLE);
            clPokerTable.setVisibility(View.GONE);
        });

        new Handler().postDelayed(() -> {
            if(flag[0] ==0) {
                webViewPlayGame.setVisibility(View.VISIBLE);
                clPokerTable.setVisibility(View.GONE);
            }
        }, 10000);

//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            Log.d("MainActivity","SDk version above android L so forcibaly enabling ThirdPartyCookies");
//            CookieManager.getInstance().setAcceptThirdPartyCookies(webViewPlayGame,true);
//        }
    }

    private void setListnerOnRealTimeDataBase(String roomId, String playerId, String myId, String chatRoomId){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("sheepfight").child("roomID :" + roomId).child(roomId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameDataModel = snapshot.getValue(GameDataModel.class);

                try{
                    if(playerId.equals("p1health")){ // meane I'm player 1
                        checkHealth(gameDataModel.getP1health(), gameDataModel.getP2health(), myId, chatRoomId);
                    }else{
                        checkHealth(gameDataModel.getP2health(), gameDataModel.getP1health(), myId, chatRoomId);
                    }
                }catch (Exception e){
                    Log.d(TAG, "106 Exception: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkHealth(int myHealth, int oponentHealth, String myId, String chatRoomId){
        Log.d(TAG, "117 myHealth: " + myHealth + " oponentHealth: " + oponentHealth + " chatRoomId: " + chatRoomId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("USERS")
                .document(myId).collection("chatRooms")
                .document(chatRoomId);
        if(myHealth == 0){ // I lose
            docRef.update("friendScore", FieldValue.increment(1));
            Toast.makeText(this, "You Lose", Toast.LENGTH_SHORT).show();
            clStatus.setVisibility(View.VISIBLE);
            webViewPlayGame.setVisibility(View.GONE);
            tvStatus.setText(" You Lose ");
        }else if(oponentHealth == 0){ // I Win
            iWin = true;
            docRef.update("myScore", FieldValue.increment(1));
            Toast.makeText(this, "You Win", Toast.LENGTH_SHORT).show();
            clStatus.setVisibility(View.VISIBLE);
            webViewPlayGame.setVisibility(View.GONE);
            tvStatus.setText(" You Win ");

        }
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
            }
            else if (msg.message().contains("overdata")) {
                String data = msg.message().substring(msg.message().lastIndexOf("=") + 1);
            }
            Log.d(TAG, "In chromeCliend onConsoleMessage: " + msg.message());
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
    public void onBackPressed() {
        try{
            if(iWin) {
                finish();
            }else{
            if(playerId.equals("p1health")){ // means I'm player 1
                    gameDataModel.setP1health(0);
                }else{
                    gameDataModel.setP2health(0);
                }
                databaseReference.setValue(gameDataModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                finish();
                            }
                        });
            }
        }catch (Exception e){
            Log.d(TAG, "106 Exception: " + e.getMessage());
        }
    }

}

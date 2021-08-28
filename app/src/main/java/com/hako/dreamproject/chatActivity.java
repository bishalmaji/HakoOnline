package com.hako.dreamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.hako.dreamproject.adapters.chat.MessagesAdapter;
import com.hako.dreamproject.adapters.chat.chatGameAdapter;
import com.hako.dreamproject.audiochat.QuickStartActivity;
import com.hako.dreamproject.model.GameModel;
import com.hako.dreamproject.model.GameRequest;
import com.hako.dreamproject.model.Message;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.hako.dreamproject.utils.UsableFunctions;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.DATA;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.GAMELIST;
import static com.hako.dreamproject.utils.Constant.IMAGE;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.ROTATION;

public class chatActivity extends AppCompatActivity {

    // ImageView
    private ImageView ivBack;
    private ImageView ivMyProfile;
    private ImageView ivFreindProfile;
    private ImageView ivFollowPlayer;
    private  ImageView ivSendMessage;
    private ImageView ivShowEmoji;
    private ImageView ivSwitchAudio;

    // ConstraintLayout
    private ConstraintLayout cLSelectedGame;
    private ImageView cLSelectedGameBackground;
    private ConstraintLayout cLRequestedGame, cLRequestedGameBackground;

    // TextView
    private TextView tvRequestedGameName, tvPlayRequestedGame, tvRejectGameRequest;
    private  TextView tvCancelSelectedGame, tvSelctedGameName;
    private TextView tvMyName, tvMyScore;
    private TextView tvFreindName, tvFreindScore;

    // Progress Loading
    private CircularProgressIndicator pbSelectedGameLoading;
    private CircularProgressIndicator pbRequestedGameLoading;

    // RecyclerView
    private RecyclerView rvGames;
    private RecyclerView rvMessages;

    // EditText
    private EmojiEditText etMessage;

    // Data
    private Boolean Follow = false;
    private Boolean Emoji = false;
    List<GameModel> gameModelList = new ArrayList<>();

    // TAG
    String TAG_CHAT_ACTIVITY = "chatActivity";

    // popup Emoji
    EmojiPopup popup;

    // Adapters
    private MessagesAdapter messagesAdapter;

    // Firebase
    private FirebaseFirestore db;

    // Message data
    String messageRoomId;
    String myId, myProfileImage, myName;
    String  myScore, friendScore;
    String reciverId, freindProfileImage, freindName;

    //Count
    int count = 0;

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get from previous activity
        messageRoomId = getIntent().getStringExtra("chatRoomId");
        myScore =  getIntent().getStringExtra("myScore");
        reciverId = getIntent().getStringExtra("reciverId");
        freindProfileImage = getIntent().getStringExtra("freindProfile");
        freindName = getIntent().getStringExtra("freindName");
        friendScore = getIntent().getStringExtra("freindScore");



        myId = AppController.getInstance().sharedPref.getString("suserUniqueId","useruid");
        if (myId==null){
            myId=AppController.getInstance().sharedPref.getString("userUniqueId","12345");
        }
        myProfileImage = AppController.getInstance().sharedPref.getString("sprofile","profile");
        myName = AppController.getInstance().sharedPref.getString("sname","name");

        setAudioChat();
        setViews();
        setOnClickListners();
        clickShrinkEffect();
        setDataOnViews();
        getGamesData();
        setMessagesIntoRecyclerView();
        getMessageData(messageRoomId);
        setRequestListner();

    }

    private void setAudioChat() {
        String roomId = myId + reciverId;
        // convert input string to char array
        char tempArray[] = roomId.toCharArray();

        // sort tempArray
        Arrays.sort(tempArray);

        // return new sorted string
        roomId = new String(tempArray);
        QuickStartActivity quickStartActivity = new QuickStartActivity();
        quickStartActivity.createEngineButtonClick(this, myId, reciverId, myId);
        Log.e("chatActivity", "myId - "+myId+" receiverID - "+reciverId);
    }

    private void setViews(){
        //ImageView
        ivBack = findViewById(R.id.iv_chatActivity_backPressed);
        ivMyProfile = findViewById(R.id.iv_chatActivity_myProfile);
        ivFreindProfile = findViewById(R.id.iv_chatActivity_freindProfile);
        ivFollowPlayer = findViewById(R.id.iv_chatActivity_followPlayer);
        ivSendMessage = findViewById(R.id.iv_chatActivity_sendMessage);
        ivShowEmoji = findViewById(R.id.iv_chatActivity_showEmoji);
        ivSwitchAudio = findViewById(R.id.iv_chatActivity_audioSwitch);

        // TextView
        tvRequestedGameName = findViewById(R.id.tv_chatActivity_requestedGameName);
        tvPlayRequestedGame = findViewById(R.id.tv_chatActivity_playRequestedGame);
        tvRejectGameRequest = findViewById(R.id.tv_chatActivity_rejectGameRequest);
        tvSelctedGameName = findViewById(R.id.tv_chatActivity_selectedGame);
        tvCancelSelectedGame = findViewById(R.id.tv_chatActivity_cancelSelectedGame);
        tvMyName = findViewById(R.id.tv_chatActivity_myName);
        tvMyScore = findViewById(R.id.tv_chatActivity_myScore);
        tvFreindName = findViewById(R.id.tv_chatActivity_freindName);
        tvFreindScore = findViewById(R.id.tv_chatActivity_freindScore);

        // Constraintlayout
        cLRequestedGame = findViewById(R.id.cL_chatActivity_requestedGame);
        cLRequestedGameBackground = findViewById(R.id.cL_chatActicity_requestedGameBackground);
        cLSelectedGame = findViewById(R.id.cL_chatActivity_selectGame);
        cLSelectedGameBackground = findViewById(R.id.gameViewDark);

        //RecyclerView
        rvGames = findViewById(R.id.rv_chatActivity_games);
        rvMessages = findViewById(R.id.rv_chatActivity_messages);

        // CircularProgressIndicator
        pbSelectedGameLoading = findViewById(R.id.pb_chatActivity_selectedGameLoading);
        pbRequestedGameLoading = findViewById(R.id.pb_chatActivity_requestedGameLoading);

        // EditText
        etMessage = findViewById(R.id.et_chatActivity_message);

        // emoji popup
        popup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.cL_chatActivity_rootView)).build(etMessage);

        // Firebase
        db = FirebaseFirestore.getInstance();
    }
    private void setOnClickListners(){
        ivBack.setOnClickListener( view -> {
            onBackPressed();
        });
        ivFollowPlayer.setOnClickListener( view -> {
            handleFollow();
        });
        ivSendMessage.setOnClickListener( view -> {
            handleSendMessage(etMessage.getText().toString().trim(), messageRoomId);
        });
        ivShowEmoji.setOnClickListener( view -> {
            handleShowEmoji();
        });
        ivSwitchAudio.setOnClickListener( VIEW -> {
            count++;
            ivSwitchAudio.setVisibility(View.INVISIBLE);
            requestAudioPermissions();
        });
    }



    private void clickShrinkEffect(){
        ClickShrinkEffectKt.applyClickShrink(tvPlayRequestedGame);
        ClickShrinkEffectKt.applyClickShrink(tvRejectGameRequest);
        ClickShrinkEffectKt.applyClickShrink(ivBack);
        ClickShrinkEffectKt.applyClickShrink(ivFollowPlayer);
        ClickShrinkEffectKt.applyClickShrink(ivSendMessage);
        ClickShrinkEffectKt.applyClickShrink(ivShowEmoji);
        ClickShrinkEffectKt.applyClickShrink(tvCancelSelectedGame);
    }
    private void setDataOnViews(){
        Glide.with(this).load(myProfileImage).circleCrop().placeholder(getDrawable(R.drawable.profile_holder)).into(ivMyProfile);
        Glide.with(this).load(freindProfileImage).circleCrop().placeholder(getDrawable(R.drawable.profile_holder)).into(ivFreindProfile);
        tvMyName.setText(myName);
        tvFreindName.setText(freindName);
        tvMyScore.setText(myScore);
        tvFreindScore.setText(friendScore);
    }
    public void getGamesData() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put(GAMELIST, API);
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e("loading", "loading..");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject obj = new JSONObject(s);
                    String TAG_GAME = "gamesInChatActivity";
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        String playerUserName = "";
                        String playerAvatarUrl = "";
                        String roomId = "";
                        String playerId = "1";

                        if(UsableFunctions.checkLoggedInOrNot()){
                            playerUserName = AppController.getInstance().sharedPref.getString("sname","name");
                            playerAvatarUrl = AppController.getInstance().sharedPref.getString("sprofile","profile");
                        }

                        gameModelList.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            roomId = UsableFunctions.getGameRoomId();
                            String url = dataobj.getString("url");

                            GameModel game = new GameModel(
                                    dataobj.getString("gameid"),
                                    dataobj.getString("name"),
                                    dataobj.getString("playing"),
                                    dataobj.getString("image"),
                                    dataobj.getString("type"),
                                    url,
                                    dataobj.getString("rotation"),
                                    roomId
                            );
                            gameModelList.add(game);
                        }
                        setGameListOnRecyclerView(gameModelList);
                    } else {
                        rvGames.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }
    private void setGameListOnRecyclerView(List<GameModel> gameModelList){
        chatGameAdapter adapter = new chatGameAdapter(this, gameModelList, new chatGameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id, String name, String image, String url, String rotation) {
                sendGameRequest(reciverId, url, rotation, name);

                rvGames.setVisibility(View.GONE);
                cLSelectedGame.setVisibility(View.VISIBLE);
                cLRequestedGame.setVisibility(View.GONE);
                tvSelctedGameName.setText(name);

                switch (name){
                    case "Sheep Fight": cLSelectedGameBackground.setBackground(getDrawable(R.drawable.sheep_fight_home_logo)); break;
                    case "Ludo": cLSelectedGameBackground.setBackground(getDrawable(R.drawable.wizard_hex_home_crop)); break;
                    case "BULL FIGHT": cLSelectedGameBackground.setBackground(getDrawable(R.drawable.bull_fight_home_crop)); break;
                    case "Bubble Shooter":    cLSelectedGameBackground.setBackground(getDrawable(R.drawable.bubble_shooter_home_crop)); break;
                    default:  cLSelectedGameBackground.setBackground(getDrawable(R.drawable.test_game)); break;
                }
            }
        });
        rvGames.setItemAnimator(new DefaultItemAnimator());
        rvGames.setHasFixedSize(true);
        rvGames.setLayoutManager(new LinearLayoutManager(chatActivity.this, RecyclerView.HORIZONTAL, false));
        rvGames.setAdapter(adapter);
        rvGames.setVisibility(View.VISIBLE);
        rvGames.setAdapter(new ScaleInAnimationAdapter(new AlphaInAnimationAdapter(adapter)));
        adapter.notifyDataSetChanged();
    }
    private void getMessageData(String chatRoomId){
        ArrayList<Message> messages = new ArrayList<>();
        CollectionReference colRef = db.collection("MESSAGES").document(chatRoomId).collection("Message");

        colRef.orderBy("messageId", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG_CHAT_ACTIVITY, "Listen failed.", e);
                            return;
                        }
                        ArrayList<Message> firebaseMessages = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            firebaseMessages.add(doc.toObject(Message.class));
                        }
                        messagesAdapter.addMessageList(firebaseMessages);
                    }
                });
    }
    private void setMessagesIntoRecyclerView(){
        ArrayList<Message> messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messages, myId);
        rvMessages.setAdapter(messagesAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(chatActivity.this, RecyclerView.VERTICAL, false));

        rvMessages.setItemAnimator(new DefaultItemAnimator());
        rvMessages.setHasFixedSize(true);
        rvMessages.setAdapter(new ScaleInAnimationAdapter(new AlphaInAnimationAdapter(messagesAdapter)));
        messagesAdapter.notifyDataSetChanged();
    }
    private void  setRequestListner(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("GAMEREQUEST").document(myId + "_" + reciverId);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot snapshot,
                                @androidx.annotation.Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG_CHAT_ACTIVITY, "Listen failed.", error);
                    pbSelectedGameLoading.setProgressCompat(0, false);
                    rvGames.setVisibility(View.VISIBLE);
                    cLSelectedGame.setVisibility(View.GONE);
                    cLRequestedGame.setVisibility(View.GONE);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    GameRequest gameRequest =  snapshot.toObject(GameRequest.class);

                    final int[] progress = {100};
                    pbRequestedGameLoading.setProgressCompat(progress[0], false);
                    CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            progress[0] -= 3;
                            pbRequestedGameLoading.setProgressCompat(progress[0], true);
                        }
                        @Override
                        public void onFinish() {
                            Log.d(TAG_CHAT_ACTIVITY, " 374 Progress: Finished");
                            rvGames.setVisibility(View.VISIBLE);
                            cLSelectedGame.setVisibility(View.GONE);
                            cLRequestedGame.setVisibility(View.GONE);
                        }
                    }.start();

                    rvGames.setVisibility(View.GONE);
                    cLSelectedGame.setVisibility(View.GONE);
                    cLRequestedGame.setVisibility(View.VISIBLE);
                    tvRequestedGameName.setText(gameRequest.getGameName());

                    switch (gameRequest.getGameName()){
                        case "Sheep Fight": cLSelectedGameBackground.setBackground(getDrawable(R.drawable.sheep_fight_home_logo)); break;
                        case "Ludo": cLSelectedGameBackground.setBackground(getDrawable(R.drawable.wizard_hex_home_crop)); break;
                        case "BULL FIGHT": cLSelectedGameBackground.setBackground(getDrawable(R.drawable.bull_fight_home_crop)); break;
                        case "Bubble Shooter":    cLSelectedGameBackground.setBackground(getDrawable(R.drawable.bubble_shooter_home_crop)); break;
                        default:  cLRequestedGameBackground.setBackground(getDrawable(R.drawable.test_game)); break;
                    }

                    if(gameRequest.getStatus() == 1 || gameRequest.getStatus() == 3){
                        rvGames.setVisibility(View.VISIBLE);
                        cLSelectedGame.setVisibility(View.GONE);
                        cLRequestedGame.setVisibility(View.GONE);
                    }

                    tvPlayRequestedGame.setOnClickListener( view -> {
                        rvGames.setVisibility(View.VISIBLE);
                        cLSelectedGame.setVisibility(View.GONE);
                        cLRequestedGame.setVisibility(View.GONE);
                        gameRequest.setStatus(1);
                        docRef.set(gameRequest);
                        docRef.delete();
                        startGame(gameRequest.getUrl(), gameRequest.getRotation(), gameRequest.getRoomId(), "p2health");
                    });

                    tvRejectGameRequest.setOnClickListener( view -> {
                        rvGames.setVisibility(View.VISIBLE);
                        cLSelectedGame.setVisibility(View.GONE);
                        cLRequestedGame.setVisibility(View.GONE);

                        gameRequest.setStatus(3);
                        docRef.set(gameRequest);
                        docRef.delete();
                    });

                } else {
                    Log.d(TAG_CHAT_ACTIVITY, "Current data: null");
                    rvGames.setVisibility(View.VISIBLE);
                    cLSelectedGame.setVisibility(View.GONE);
                    cLRequestedGame.setVisibility(View.GONE);
                }
            }
        });

    }


    // Switch Mic
    private void switchMic() {
        if(count%2==0)
        {
            QuickStartActivity quickStartActivity = new QuickStartActivity();
            quickStartActivity.onDestroy();
            quickStartActivity.createEngineButtonClick(this, myId, reciverId, myId);
            ivSwitchAudio.setVisibility(View.VISIBLE);
            ivSwitchAudio.setImageResource(R.drawable.ic_audio_mic_off);
        }
        else {
            QuickStartActivity quickStartActivity = new QuickStartActivity();
            quickStartActivity.startPublishingButtonClick(myId);
            ivSwitchAudio.setVisibility(View.VISIBLE);
            ivSwitchAudio.setImageResource(R.drawable.ic_audio_mic_on);
        }
    }
    //Handle Follow
    private void handleFollow(){
        Follow = Follow == true ? false : true;
        if(Follow){
            ivFollowPlayer.setBackground(this.getDrawable(R.drawable.followed));
        }
        else {
            ivFollowPlayer.setBackground(this.getDrawable(R.drawable.follow_player));
        }
    }

    // age
    private void handleSendMessage(String message, String msgRoomId){

        if(!message.isEmpty() && message != ""){
            String msgId = UsableFunctions.getMessageId();
            Message messageObj = new Message(message, msgId, reciverId, myId);
            messagesAdapter.addMessage(messageObj);
            etMessage.setText("");
            rvMessages.scrollToPosition(messagesAdapter.getItemCount() - 1);

            sendMessage(msgRoomId, messageObj);
        }
    }
    private void sendMessage(String msgRoomId, Message messageObj){
        db.collection("MESSAGES")
                .document(msgRoomId)
                .collection("Message")
                .document()
                .set(messageObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG_CHAT_ACTIVITY, "message sends");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG_CHAT_ACTIVITY,"err: " + e.getMessage());
            }
        });
    }
    //HandleShowEmoji
    private void handleShowEmoji(){
        Emoji = Emoji == true ? false : true;
        if(Emoji == true){
            ivShowEmoji.setBackground(this.getDrawable(R.drawable.emoji_opened));
            openEmojiView();
        }
        else {
            ivShowEmoji.setBackground(this.getDrawable(R.drawable.emoji_open));
            closeEmojiView();
        }
    }
    private void openEmojiView(){
        popup.toggle();
    }
    private void closeEmojiView(){
        if(popup.isShowing()){
            popup.dismiss();
        }
    }

    // handle Game Request
    private void sendGameRequest(String freindId, String url, String rotation, String gameName){

        String playerUserName = playerUserName = AppController.getInstance().sharedPref.getString("sname","name");
        String playerAvatarUrl = playerAvatarUrl = AppController.getInstance().sharedPref.getString("sprofile","profile");

        String roomId = UsableFunctions.getGameRoomId();
        String playerId = "1";

        String name[] = playerUserName.split(" ");
        if(name.length > 1){
            playerUserName = name[0];
        }

        String freindUserName[] = freindName.split(" ");
        if(name.length > 1){
            freindName = freindUserName[0];
        }

        String myUrl = url + "?playerusername=" + playerUserName + "&playeravatarurl="
                + playerAvatarUrl + "&roomid=" + roomId + "&playerid=1";
        String freindUrl = url+ "?playerusername=" + freindName + "&playeravatarurl="
                + freindProfileImage + "&roomid=" + roomId + "&playerid=2";

        myUrl = myUrl.replaceAll("\n", "");
        freindUrl = freindUrl.replaceAll("\n", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("GAMEREQUEST").document(freindId + "_" + myId);
        GameRequest gameRequest = new GameRequest( myId+freindId, gameName, freindId, freindUrl, rotation, myId, 0, roomId);

        final int[] progress = {100};
        pbSelectedGameLoading.setProgressCompat(progress[0], false);
        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progress[0] -= 3;
                pbSelectedGameLoading.setProgressCompat(progress[0], true);
            }
            @Override
            public void onFinish() {
                Log.d(TAG_CHAT_ACTIVITY, "Progress: Finished");
                rvGames.setVisibility(View.VISIBLE);
                cLSelectedGame.setVisibility(View.GONE);
                cLRequestedGame.setVisibility(View.GONE);
                deleteGameRequest(docRef);
            }
        }.start();

        tvCancelSelectedGame.setOnClickListener( view -> {
            countDownTimer.cancel();
            rvGames.setVisibility(View.VISIBLE);
            cLSelectedGame.setVisibility(View.GONE);
            cLRequestedGame.setVisibility(View.GONE);
            deleteGameRequest(docRef);
        });

        docRef.set(gameRequest);

        String finalMyUrl = myUrl;
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot snapshot,
                                @androidx.annotation.Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG_CHAT_ACTIVITY, "Listen failed.", error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    GameRequest request =  snapshot.toObject(GameRequest.class);

                    // 0 -> raiseGameRequest
                    // 1 -> acceptGameRequest
                    // 3 -> rejectGameRequest

                    if(request.getStatus() == 1){
                        // StartGame
                        countDownTimer.cancel();
                        rvGames.setVisibility(View.VISIBLE);
                        cLSelectedGame.setVisibility(View.GONE);
                        cLRequestedGame.setVisibility(View.GONE);
                        deleteGameRequest(docRef);
                        startGame(finalMyUrl, rotation, roomId, "p1health");
                    }else if(request.getStatus() == 3){
                        countDownTimer.cancel();
                        rvGames.setVisibility(View.VISIBLE);
                        cLSelectedGame.setVisibility(View.GONE);
                        cLRequestedGame.setVisibility(View.GONE);
                        deleteGameRequest(docRef);
                    }
                } else {
                    Log.d(TAG_CHAT_ACTIVITY, "Current data: null");
                }
            }
        });
    }
    private void startGame(String myUrl, String rotation, String roomId, String playerId){
        handleSendMessage("Game Played", messageRoomId);
        Intent intent = new Intent(this, PlayGameRequestActivity.class);
        intent.putExtra("url", myUrl);
        intent.putExtra("rotation", rotation);
        intent.putExtra("roomId", roomId);
        intent.putExtra("playerId", playerId);
        intent.putExtra("myId", myId);
        intent.putExtra("chatRoomId", messageRoomId);
        intent.putExtra("friendURL", freindProfileImage);
        intent.putExtra("friendName", freindName);
        intent.putExtra("myName", myName);
        startActivity(intent);
    }
    private void deleteGameRequest(DocumentReference docRef){
        docRef.delete();
    }

    @Override
    public void onBackPressed() {
        QuickStartActivity quickStartActivity = new QuickStartActivity();
        quickStartActivity.onDestroy();
        super.onBackPressed();
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            switchMic();
        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    switchMic();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
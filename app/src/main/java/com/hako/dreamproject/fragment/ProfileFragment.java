package com.hako.dreamproject.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hako.dreamproject.BrowserActivity;
import com.hako.dreamproject.EditProfileActivity;
import com.hako.dreamproject.HomeActivity;
import com.hako.dreamproject.LoginActivity;
import com.hako.dreamproject.PendingRequests;
import com.hako.dreamproject.PlayWithFriends;
import com.hako.dreamproject.R;
import com.hako.dreamproject.Settings;
import com.hako.dreamproject.SplashActivity;
import com.hako.dreamproject.chatActivity;
import com.hako.dreamproject.dialog.showInviteDialog;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.hako.dreamproject.utils.UsableFunctions;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.HELPDESK;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.PRIVACY;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERAGREEMENT;
import static com.hako.dreamproject.utils.Constant.USERID;

public class ProfileFragment extends Fragment {
    HomeActivity homeActivity;
    SplashActivity splashActivity1;




    View rootView;

    CircleImageView profile;

    LinearLayout agreements;
    LinearLayout privacy;

    LinearLayout helpDesk;


    // TextView
    TextView name;
    TextView tvMyId;
    TextView totalPlayed;
    TextView todayPlayed;

    SQLiteDatabase dbs;
    // LinearLayout
    LinearLayout edit_profile;
    LinearLayout lL_inviteFreind,lL_profile_settings;
    LinearLayout pendingRequest;
    LinearLayout lL_profile_followUs;

    // Firebase
    FirebaseFirestore db;

    String TAG_INVITE_FRIEND = "inviteFriend";

    ImageView img;
    private RewardFragment rewardFragment1;
    private String nameStr,imageStr;
    private String myId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        setViews();

        setOnClickAnimation();
        setOnClickListeners();

//        if (AppController.getInstance().getId().equalsIgnoreCase("0")) {
//            name.setText("Login");
//        } else {
//            name.setText(AppController.getInstance().getName());
//            tvMyId.setText("ID: " + AppController.getInstance().getUser_unique_id());
//           // for user images
//            Glide.with(getContext()).load(AppController.getInstance().getProfile()).into(profile);

//            stats();
//        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (UsableFunctions.checkLoggedInOrNot()) {
            loadDataFromFirebase();
        }

    }

    private void loadDataFromFirebase() {
        DocumentReference myDocRef = db.collection("ProfileData").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult()!=null&&task.isSuccessful()){
                    if (getActivity()==null){
                        return;
                    }
                    nameStr= task.getResult().getString("name");
                   imageStr= task.getResult().getString("profile");
                    if (AppController.getInstance().sharedPref.getString("suserid","12345").equalsIgnoreCase("0")) {
                        name.setText("Login");

                    } else {
                        name.setText(nameStr);
                        myId = AppController.getInstance().sharedPref.getString("suserUniqueId","useruid");
                        if (myId==null){
                            myId=AppController.getInstance().sharedPref.getString("userUniqueId","12345");
                        }
                    tvMyId.setText(myId);

                        // for user images
                        Glide.with(getContext()).load(imageStr).into(profile);
                        stats();
                    }
                }
            }
        });
    }

    private void setViews() {
        profile = rootView.findViewById(R.id.profile);
        name = rootView.findViewById(R.id.name);
        tvMyId = rootView.findViewById(R.id.tv_profileFrag_myId);
        totalPlayed = rootView.findViewById(R.id.totalPlayed);
        todayPlayed = rootView.findViewById(R.id.today);
        img = rootView.findViewById(R.id.fav_img);


        agreements = rootView.findViewById(R.id.agrements);
        privacy = rootView.findViewById(R.id.privacy);
        helpDesk = rootView.findViewById(R.id.help);

        // LinearLayout
        edit_profile=rootView.findViewById(R.id.edit_profile);
        lL_inviteFreind = rootView.findViewById(R.id.lL_profile_inviteFreinds);
        pendingRequest = rootView.findViewById(R.id.lL_pending_request);
        lL_profile_settings = rootView.findViewById(R.id.lL_profile_settings);
        lL_profile_followUs = rootView.findViewById(R.id.lL_profile_followUs);



        Glide.with(getActivity()).load(getImg()).into(img);
        // Firebase
        db = FirebaseFirestore.getInstance();
    }

    String getImg() {
        Cursor c = null;
        String i = "";
        dbs = getActivity().openOrCreateDatabase("UserData", MODE_PRIVATE, null);
        dbs.execSQL("create table if not exists userfav (count text,img text);");
        c = dbs.rawQuery("select * from userfav;", null);
        c.moveToFirst();
        for (int ii = 0; c.moveToPosition(ii); ii++) {
            i = c.getString(1);
        }
        return i;
    }

    private void setOnClickAnimation() {
//        ClickShrinkEffectKt.applyClickShrink(edit_profile);
        ClickShrinkEffectKt.applyClickShrink(lL_inviteFreind);
        ClickShrinkEffectKt.applyClickShrink(pendingRequest);
        ClickShrinkEffectKt.applyClickShrink(lL_profile_settings);
        ClickShrinkEffectKt.applyClickShrink(agreements);
        ClickShrinkEffectKt.applyClickShrink(privacy);
        ClickShrinkEffectKt.applyClickShrink(helpDesk);
        ClickShrinkEffectKt.applyClickShrink(lL_profile_followUs);
    }


    private void setOnClickListeners() {
        edit_profile.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });

        lL_inviteFreind.setOnClickListener(view -> {
            if (UsableFunctions.checkLoggedInOrNot()) {
                startActivity(new Intent(getContext(), PlayWithFriends.class));
            } else {
                Toast.makeText(requireContext(), "Please Login First", Toast.LENGTH_SHORT).show();
                gotoLoginActivity();
            }
        });


        pendingRequest.setOnClickListener(view -> {
            if (UsableFunctions.checkLoggedInOrNot()) {
               // startActivity(new Intent(getContext(), PlayWithFriends.class));
                Intent intent = new Intent(getContext(), PendingRequests.class);

                startActivity(intent);

                Toast.makeText(requireContext(), "PENDING REQUESTS", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(requireContext(), "Please Login First", Toast.LENGTH_SHORT).show();
                gotoLoginActivity();
            }

        });


        lL_profile_settings.setOnClickListener(view -> {

           // AppController.getInstance().removeData();
            startActivity(new Intent(getContext(), Settings.class));
           // AppController.getInstance().logout(splashActivity1);




           // AppController.getInstance().logout();


           // AppController.getInstance().setProfile("");
            Toast.makeText(requireContext(), "SETTINGS", Toast.LENGTH_SHORT).show();

        });


        agreements.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), BrowserActivity.class);
            intent.putExtra("url", USERAGREEMENT);
            intent.putExtra(NAME, "User Agreements");
            startActivity(intent);
        });
        privacy.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), BrowserActivity.class);
            intent.putExtra("url", PRIVACY);
            intent.putExtra(NAME, "Privacy Policy");
            startActivity(intent);
        });
        helpDesk.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), BrowserActivity.class);
            intent.putExtra("url", HELPDESK);
            intent.putExtra(NAME, "HelpDesk");
            startActivity(intent);
        });

    }

    // ClickFunctions
    private void showPoPForInviteFriends() {
        showInviteDialog inviteDialog = new showInviteDialog();
        Dialog dialog = inviteDialog.showDialog(new Dialog(requireActivity()), requireActivity());
        EditText etInviteId = dialog.findViewById(R.id.et_inviteDialog_playerId);
        dialog.findViewById(R.id.btn_inviteDialog_invite).setOnClickListener(view -> {
            String friendId = etInviteId.getText().toString().trim();
            if (!friendId.isEmpty()) {
                setInvitationFriend(friendId, dialog);
            }
        });
    }

    private void setInvitationFriend(String friendId, Dialog dialog) {
        String invitationId = UsableFunctions.getInvitationId();
        myId = AppController.getInstance().sharedPref.getString("suserUniqueId","useruid");
        if (myId==null){
            myId=AppController.getInstance().sharedPref.getString("userUniqueId","12345");
        }
        String chatRoomId = UsableFunctions.getMessageRoomId();
        Log.d(TAG_INVITE_FRIEND, "friendId: " + friendId + " InvitationId: " + invitationId + "myId: " + myId);

        HashMap<String, String> inviteMap = new HashMap<>();
        inviteMap.put("senderId", myId);
        inviteMap.put("senderName", nameStr);
        inviteMap.put("senderImage", imageStr);
        inviteMap.put("chatRoomId", chatRoomId);
        inviteMap.put("receiverId", friendId);


        db.collection("INVITATION")
                .document(invitationId)
                .set(inviteMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Invitation Sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireContext(), chatActivity.class);
                        intent.putExtra("chatRoomId", chatRoomId);
                        intent.putExtra("reciverId", friendId);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_INVITE_FRIEND, "Failed due to: " + e.getMessage());
                        Toast.makeText(requireContext(), "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void invite(String myProfile, String myId, String chatRoomId, String reciverId, String checkUser, String senderName, String invitationId) {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("senderImage", myProfile);
                params.put("senderId", myId);
                params.put("chatRoomId", chatRoomId);
                params.put("receiverId", reciverId);
                params.put("checkuser", checkUser);
                params.put("senderName", senderName);
                params.put("invitationId", invitationId);

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
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray("data");
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

//    private void addChatRoomInUser(String chatRoomId, String myId, String freindId){
//        DocumentReference docRef = db.collection("USERS").document(myId)
//                .collection("chatRooms").document(chatRoomId);
//        HashMap<String, String> chatRoom = new HashMap<>();
//        chatRoom.put("chatRoomId", chatRoomId);
//        chatRoom.put("freindId", freindId);
//        docRef.set(chatRoom);
//    }


    private void gotoLoginActivity() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void stats() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("stats", API);
                params.put(USERID, AppController.getInstance().sharedPref.getString("suserid","12345"));
                params.put(TOKEN, AppController.getInstance().sharedPref.getString("stoken","token"));
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e("loading", "loading..");
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(ERROR, s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        totalPlayed.setText(obj.getString("total"));
                        todayPlayed.setText(obj.getString("today"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(ERROR, e.getMessage());
                }
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

}

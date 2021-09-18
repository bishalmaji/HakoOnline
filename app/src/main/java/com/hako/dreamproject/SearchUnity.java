package com.hako.dreamproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;
import com.hako.dreamproject.fragment.GameHolder;
import com.hako.dreamproject.utils.AppController;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SearchUnity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    String uid;
    DocumentReference docRefMy;
    private String pname="",puid,pprofile;
    private boolean playflag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_unity);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        uid=auth.getCurrentUser().getUid();
        docRefMy=db.collection("ProfileData").document(uid);
        docRefMy.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    return;
                }if (value!=null&&value.exists()){
               String  gameuid=  value.getData().get("playing").toString();
               String  player=String.valueOf(value.getData().get("player"));
               if (!player.equals("0")){
                   Log.d("tagforbkm", "onEvent: "+player);
                   Gotogame(player,gameuid);
               }

                }
            }

            private void Gotogame(String s, String gameuid) {
                UnityPlayer.UnitySendMessage("FirebaseReciever","RecieveUid",gameuid);
                UnityPlayer.UnitySendMessage("FirebaseReciever","Recieve",AppController.getInstance().sharedPref.getString("sname","Me"));
                UnityPlayer.UnitySendMessage("FirebaseReciever","RecievePid",uid);
                UnityPlayer.UnitySendMessage("FirebaseReciever","RecievePnum",s);
                Intent intent=new Intent(SearchUnity.this, UnityPlayerActivity.class);
                startActivity(intent);


            }
        });
      searching();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        docRefMy.update("g1",false);
    }

    private void searching() {
        docRefMy.update("g1",true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                        db.collection("ProfileData").whereNotEqualTo("uid",uid).whereEqualTo("g1",true).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot snap:task.getResult()
                                ) {
                                    pname=     snap.getString("name");
                                    pprofile=     snap.getString("profile");
                                    puid=   snap.getString("uid");
                                }
                                if (!pname.equals("")){
                                    Random random=new Random();
                                    String  gameroom=String.valueOf(random.nextInt(10000));
                                    Map<String,Object> data=new HashMap<>();
                                    data.put("p1uid",uid);
                                    data.put("p2uid",puid);
                                    data.put("p1image", AppController.getInstance().sharedPref.getString("sprofole","null"));
                                    data.put("p2image",pprofile);
                                    data.put("p1points",0);
                                    data.put("p2points",0);
                                    data.put("p1name",AppController.getInstance().sharedPref.getString("sname","Me"));
                                    data.put("p2name",pname);

                                    db.collection("bubbleRoom").document(gameroom).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            docRefMy.update("playing",gameroom,"player",1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    db.collection("ProfileData").document(puid).update("playing",gameroom,"player",2);

                                                }
                                            });
                                        }
                                    });


                                }

                            }
                        });

            }
        });

    }
}
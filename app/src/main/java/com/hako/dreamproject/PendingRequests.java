package com.hako.dreamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hako.dreamproject.adapters.chat.pendingRequestAdapter;
import com.hako.dreamproject.model.pendingRequestModel;
import com.hako.dreamproject.utils.AppController;

import java.util.ArrayList;
import java.util.List;

public class PendingRequests extends AppCompatActivity {
    FirebaseFirestore db;
    ArrayList<pendingRequestModel> pendingRequestArrayList;
    pendingRequestAdapter adapter;
    RecyclerView recview;
    String myId;
    pendingRequestModel obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        myId = AppController.getInstance().sharedPref.getString("suserUniqueId","useruid");
        String name =  AppController.getInstance().sharedPref.getString("sname","name");
        Log.e("senderid", myId);
        Log.e("sendernamwe",name);
        //getListItem();

        pendingRequestArrayList = new ArrayList<pendingRequestModel>();

        recview = (RecyclerView)findViewById(R.id.pendingRecView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recview.setLayoutManager(layoutManager);
        adapter = new pendingRequestAdapter(pendingRequestArrayList, this);
        recview.setAdapter(adapter);




        db = FirebaseFirestore.getInstance();
        db.collection("INVITATION")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        ArrayList<DocumentSnapshot> list = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot dataSnapshot : list){

                           // obj.setDocumentID(dataSnapshot.getId());

                            obj = dataSnapshot.toObject(pendingRequestModel.class);

                            if(myId.equals(obj.getReceiverId())){
                                pendingRequestArrayList.add(obj);

                            }

                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        /*CollectionReference colRef = db.collection("INVITATION");
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
                });*/







    }

/*

    private  void getListItem(){
        mFirebaseFirestore.collection("INVITATION")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("rajeee", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("rajjeev", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
*/




}






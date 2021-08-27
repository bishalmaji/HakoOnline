package com.hako.dreamproject.adapters.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hako.dreamproject.R;
import com.hako.dreamproject.chatActivity;
import com.hako.dreamproject.model.pendingRequestModel;
import com.hako.dreamproject.utils.AppController;

import java.util.ArrayList;

public class pendingRequestAdapter extends RecyclerView.Adapter<pendingRequestAdapter.pendingRequestholder>{

    private ArrayList<pendingRequestModel> pendingrequstarraylist;
    private Context context;

    public pendingRequestAdapter(ArrayList<pendingRequestModel> pendingrequstarraylist, Context context) {
        this.pendingrequstarraylist = pendingrequstarraylist;
        this.context = context;
    }

    @NonNull
    @Override
    public pendingRequestholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_request_single_row,parent,false);
        return new pendingRequestholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull pendingRequestholder holder, int position) {
       pendingRequestModel pendingRequestModels = pendingrequstarraylist.get(position);
        //holder.rec.setText(pendingRequestModels.getReceiverId());
        holder.senderName.setText(pendingRequestModels.getSenderName()+"wants you to send req");
      //  holder.roomid.setText(pendingRequestModels.getChatRoomId());
        Glide.with(holder.senimg.getContext()).load(pendingRequestModels.getSenderImage()).into(holder.senimg);
        // holder.senimg.Glide.with(this).load(AppController.getInstance().getProfile()).into();
        holder.AcceptReq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , chatActivity.class);
                intent.putExtra("chatRoomId", pendingRequestModels.getChatRoomId());
                intent.putExtra("reciverId", pendingRequestModels.getSenderId());
                context.startActivity(intent);
                // yaha firebase ke invitaion ko delete bhi krna hai taki pending request me show na ho
                 FirebaseFirestore.getInstance().collection("INVITATION").document();
               Log.e("pendingid", pendingRequestModels.getDocumentID());
               // DocumentReference docRef1 = FirebaseFirestore.getInstance().collection("INVITATION").document(pendingRequestModels.getDocumentID());
               // Log.e("pendingid", pendingRequestModels.toString());
              //  docRef1.delete();
            }
        });
        holder.RejectReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return pendingrequstarraylist.size();
    }

    public  class pendingRequestholder extends RecyclerView.ViewHolder{
        TextView senderName , roomid, rec,AcceptReq,RejectReq;
        ImageView senimg;

        public pendingRequestholder(@NonNull View itemView) {
            super(itemView);
            senimg =itemView.findViewById(R.id.pending_row_freindProfile);
            senderName = itemView.findViewById(R.id.pending_row_freindName);
            AcceptReq = itemView.findViewById(R.id.btn__pending_row_accept);
            RejectReq = itemView.findViewById(R.id.btn_pending_row_reject);
            //roomid = itemView.findViewById(R.id.chatroom);

        }
    }
}



    /*sb dkhfvidkncb


public class countryadapter extends RecyclerView.Adapter<countryadapter.countryviewholder> {

    private ArrayList<countrydata> arrayList;
    private Context context;

    public countryadapter(ArrayList<countrydata> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public countryviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_country,parent,false);

        return new countryviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull countryviewholder holder, int position) {

        countrydata countrydataa = arrayList.get(position);

        holder.countrycases.setText(NumberFormat.getInstance().format(Integer.parseInt(countrydataa.getCases())));
        holder.countryname.setText(countrydataa.getCountry());
        holder.sno.setText(String.valueOf(position+1));
        Picasso.get().load(countrydataa.getCountryInfo()).placeholder(R.drawable.ic_launcher_foreground).into(holder.flagimg);

       /
    }
*/



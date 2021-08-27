package com.hako.dreamproject.adapters.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.DiscoverPeople;
import com.hako.dreamproject.R;
import com.hako.dreamproject.model.DiscoverPeopleModel;
import com.hako.dreamproject.model.GameModel;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoverPeopleAdapter extends RecyclerView.Adapter<DiscoverPeopleAdapter.MyHolder>  {

    Activity context;
    ArrayList<String> user_id;
    ArrayList<String> points;
    ArrayList<String> userid;
    ArrayList<String> name;
    ArrayList<String> user_unique_id;
    ArrayList<String> profile;



    public DiscoverPeopleAdapter(Activity context, ArrayList<String> user_id, ArrayList<String> points, ArrayList<String> userid, ArrayList<String> name, ArrayList<String> user_unique_id, ArrayList<String> profile) {
        this.context = context;
        this.user_id=user_id;
        this.points=points;
        this.userid=userid;
        this.name = name;
        this.user_unique_id=user_unique_id;
        this.profile=profile;

    }

    @NonNull
    @Override
    public DiscoverPeopleAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.discover_row_recycler, parent, false);
        return new DiscoverPeopleAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverPeopleAdapter.MyHolder holder, int position) {
        holder.name.setText(name.get(position));
        holder.points.setText(points.get(position));
        Glide.with(context).load(profile.get(position)).into(holder.image);

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return name.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView name,points;
        CircleImageView image;
        public MyHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username);
            image = itemView.findViewById(R.id.userimage);
            points = itemView.findViewById(R.id.points_tv);
        }

    }



}
package com.hako.dreamproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.MainActivity;
import com.hako.dreamproject.R;
import com.hako.dreamproject.utils.AppController;

import static com.hako.dreamproject.utils.Constant.DATA;

public class CoinAddSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_add_splash);
        ImageView coin_gif_one=findViewById(R.id.counonegif);
        ImageView friendImg=findViewById(R.id.friend_img);
        ImageView my_img=findViewById(R.id.my_img);
        Glide.with(this).load(R.drawable.coin_gif).into(coin_gif_one);
        Glide.with(this).load(R.drawable.invite_freind_monkey).into(friendImg);
        Glide.with(this).load(AppController.getInstance().sharedPref.getString("sprofile","00")).into(my_img);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent coinIntent=new Intent(CoinAddSplash.this, MainActivity.class);
                coinIntent.putExtra(DATA,getIntent().getStringExtra(DATA));
                coinIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                coinIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(coinIntent);
            }
        },4000);
    }
}
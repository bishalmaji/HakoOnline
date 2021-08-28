package com.hako.dreamproject;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.Constant;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.HashMap;

import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.NAME;

public class SplashActivity extends AppCompatActivity {
    String email;
    String name;
    String profile;
    FirebaseAuth mAuth;

    ImageView finalGif, imagesplash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        EmojiManager.install(new GoogleEmojiProvider());

        finalGif = findViewById(R.id.finalGif);
        imagesplash = findViewById(R.id.imageView4);


        mAuth = FirebaseAuth.getInstance();
        try {
            Glide.with(this).load(R.drawable.splash_screen_icon).into(imagesplash);
            Glide.with(this).load(R.drawable.hako_title_new).into(finalGif);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }
        if (!isEmulator()) {
            new Handler().postDelayed(() -> {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && !AppController.getInstance().sharedPref.getString("suserid","12345").equalsIgnoreCase("0")) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();
                }

            }, 5000);
        } else {
            Toast.makeText(getApplicationContext(), "This Device Not Supported", Toast.LENGTH_LONG).show();
            finish();
        }

    }



//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
////        if (hasFocus) {
////            getWindow().getDecorView().setSystemUiVisibility(
////                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
////                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
////                            | View.SYSTEM_UI_FLAG_FULLSCREEN
////                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
////        }
//    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }


}

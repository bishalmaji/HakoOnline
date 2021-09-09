package com.hako.dreamproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;


public class LoginActivity extends AppCompatActivity {
    Button loginWithGoogle;
    EditText refer;
    String code;
    String name;
    String email;
    ImageView packman;
    String profile;
    FirebaseAuth mAuth;
    String phone;
    TextView goodmoring;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 7;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        db=FirebaseFirestore.getInstance();
        loginWithGoogle = findViewById(R.id.login_with_google);
        refer = findViewById(R.id.refer);
        packman = findViewById(R.id.img_hori);
        goodmoring = findViewById(R.id.goodmorning);
        String greeting = null;
        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        if (hours >= 0 && hours <= 12) {
            greeting = "Good Morning";
        } else if (hours >= 12 && hours <= 16) {
            greeting = "Good Afternoon";
        } else if (hours >= 16 && hours <= 21) {
            greeting = "Good Evening";
        } else if (hours >= 21 && hours <= 24) {
            greeting = "Good Night";
        }
        goodmoring.setText("Hi' " + greeting);
        Glide.with(this).load(R.drawable.packman).into(packman);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("42809996726-2aabef2tr5bibsr2rkr0ri0h3j7cdnkp.apps.googleusercontent.com")
                .requestIdToken("518654146741-5fa7vqkh8v7kd0145rdkrpj2onlrkhb7.apps.googleusercontent.com")
//                .requestIdToken("735929854041-kig5f76te5rhaab3q9qe1m6h4m2vf7vt.apps.googleusercontent.com")
                .requestEmail()
                .build();
        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginWithGoogle.setOnClickListener(view -> {
            if (refer.length() == 0) {
                code = "0";
            } else {
                code = refer.getText().toString();
            }
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("Login", "Google sign in failed", e);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.e("TAG", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        name = user.getDisplayName();
                        email = user.getEmail();
                        profile = user.getPhotoUrl().toString();
                        phone = "0";
                        addDatatoFirebase();
                        loginMehod();


                    } else {
                        Log.e("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addDatatoFirebase() {
    DocumentReference  myDocRef=db.collection("ProfileData").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
    Map<String ,Object> params=new HashMap<>();
        params.put("email", email);
        params.put("profile", profile);
        params.put("name", name);
        params.put("refer", code);
        params.put("points",500);
        params.put("g1",false);
        params.put("uid",mAuth.getCurrentUser().getUid());
        params.put("playing","");
        params.put("player",0);
        myDocRef.set(params);
    }

    public void loginMehod() {
        class Login extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("register_or_login", "1");
                params.put("email", email);
                params.put("profile", profile);
                params.put("name", name);
                params.put("refer", code);
                Log.e("Constants.TAG", params.toString());
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(ERROR, FALSE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (AppController.getInstance().login(s)) {
                    if (AppController.getInstance().sharedPref.getString("smode","0").equalsIgnoreCase("1")) {
                        Toast.makeText(getApplicationContext(), "Maintance Mode", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this, Maintance.class);
                        startActivity(i);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else if (AppController.getInstance().sharedPref.getString("sstatus","status").equalsIgnoreCase("1") && AppController.getInstance().sharedPref.getString("smode","mode").equalsIgnoreCase("0")) {
                        Toast.makeText(getApplicationContext(), "Your Account Was Suspendend", Toast.LENGTH_LONG).show();
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                } else {
                    Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }
        Login ru = new Login();
        ru.execute();
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
}


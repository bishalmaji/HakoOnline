package com.hako.dreamproject.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.hako.dreamproject.ActivityTracker;
import com.hako.dreamproject.CoinMarket;
import com.hako.dreamproject.LoginActivity;
import com.hako.dreamproject.R;
import com.hako.dreamproject.model.checkModel;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.LOADING;
import static com.hako.dreamproject.utils.Constant.MESSAGE;
import static com.hako.dreamproject.utils.Constant.TAG;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERID;

public class RewardFragment extends Fragment implements OnUserEarnedRewardListener {
    View rootView;
    List<checkModel> checkModelList = new ArrayList<>();
    CheckAdapter checkAdapter;
    RecyclerView rvChecking;
    SwipeRefreshLayout swipetoRefresh;

    LinearLayout loading;
    LinearLayout noItem;
    NestedScrollView scrollView;
    FirebaseAuth mAuth;
    String userid;
    String token;
    TextView claim;
    TextView play1ads;
    TextView play3ads;
    TextView fiftenMinutes;
    int total = 0;
    int days = 0;
    TextView hone;
    TextView htwo;
    TextView hthree;
    TextView hfour;
    TextView totalComplete;
    TextView points;
    LinearLayout pointsClick;
    private int flag = 0;
    Date current, previous;
    SimpleDateFormat sdf;
    TextView reward;
    private RewardedInterstitialAd rewardedInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.reward_fragment, container, false);
        rvChecking = rootView.findViewById(R.id.rvcheckin);
        swipetoRefresh = rootView.findViewById(R.id.pulltorefresh);
        noItem = rootView.findViewById(R.id.noitem);
        loading = rootView.findViewById(R.id.loading);
        reward = rootView.findViewById(R.id.reward);
        scrollView = rootView.findViewById(R.id.scrollview);
        claim = rootView.findViewById(R.id.claim);
            play3ads = rootView.findViewById(R.id.playThreeads);
            play1ads = rootView.findViewById(R.id.playoneads);
        fiftenMinutes = rootView.findViewById(R.id.playfifteen);
        hfour = rootView.findViewById(R.id.hfour);
        hone = rootView.findViewById(R.id.hone);
        points = rootView.findViewById(R.id.points);
        hthree = rootView.findViewById(R.id.hthree);
        htwo = rootView.findViewById(R.id.htwo);
        totalComplete = rootView.findViewById(R.id.totalComplete);


        scrollView.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        swipetoRefresh.setOnRefreshListener(() -> {
            swipetoRefresh.setRefreshing(true);
            getPlayerData();
        });


        MobileAds.initialize(getActivity());

        reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAds();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            if (user != null) {
                userid = AppController.getInstance().getId();
                token = AppController.getInstance().getToken();
                points.setText(numberCalculation(Integer.parseInt(AppController.getInstance().getCoins())));

            } else {
                userid = "none";
                token = "none";
                points.setText("0");
                claim.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                // some code
                flag = 1;
                claim.setEnabled(true);
            }
        };
        timer.schedule(t, 0l, 1000 * 60 * 60 * 24);

        pointsClick = rootView.findViewById(R.id.pointsClick);
        pointsClick.setOnClickListener(view -> {
            if (AppController.getInstance().getId().equalsIgnoreCase("0")) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), ActivityTracker.class);
                startActivity(intent);
            }
        });


        try {
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date()); // Using today's date
            c.add(Calendar.DATE, 0); // Adding 5 days
            String output = sdf.format(c.getTime());
            Date date = sdf.parse(output);
            //  Toast.makeText(getActivity(), getCDate() + "  ", Toast.LENGTH_SHORT).show();
            //  Toast.makeText(getActivity(), date + "  ", Toast.LENGTH_SHORT).show();

            if (getCDate() == "") {
                insertData(sdf.format(new Date()));
                //   Toast.makeText(getActivity(), getCDate() + "  ", Toast.LENGTH_SHORT).show();

            } else {
                Date date1;
                date1 = sdf.parse(getCDate());
                if (date.after(date1) && date1 != date) {
                    claim.setEnabled(true);
                } else {
                    claim.setEnabled(false);
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage() + " Error", Toast.LENGTH_SHORT).show();
        }


        claim.setOnClickListener(view -> {

//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            Toast.makeText(getActivity(), sdf.format(new Date()) + " ", Toast.LENGTH_SHORT).show();
//
//
//            if (getCDate() == null) {
//                insertData(sdf.format(new Date()));
//                Toast.makeText(getActivity(), sdf.format(new Date()) + " ", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getActivity(), getCDate() + " ", Toast.LENGTH_SHORT).show();
//                Date date = new Date();
//                try {
//                    date = sdf.parse(getCDate());
//
//                    if (date.before(new Date())) {
//
//                        claim.setEnabled(true);
//
//                    } else {
//                        claim.setEnabled(false);
//                    }
//
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), e.getMessage() + ".", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (flag == 1) {
                flag = 0;
                goForDaily();
            } else {
                Toast.makeText(getContext(), "Claim Every 24 Hours!", Toast.LENGTH_SHORT).show();
            }
            insertData(sdf.format(new Date()));
            claim.setEnabled(false);
        });
        getPlayerData();
        mylay();



        play1ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAds();
            }
        });
        /*play1ads.setOnClickListener(view -> {
                loadAds();
                }
              //  loadFragment(new HomeFragment())
        );*/
       /* play3ads.setOnClickListener(view ->
                loadFragment(new HomeFragment())
        );*/
        play3ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAds();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadAds();

                        //Do something here
                    }
                }, 7000);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadAds();
                        //Do something here
                    }
                }, 14000);
            }
        });

        fiftenMinutes.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), CoinMarket.class);
                    startActivity(intent);
                }
        );
        return rootView;
    }

    private void loadAds() {
        RewardedInterstitialAd.load(getActivity(), "ca-app-pub-3940256099942544/5354046379",
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        super.onAdLoaded(ad);
                        Log.e(TAG, "onAdLoaded");
                        ad.show(getActivity(), RewardFragment.this::onUserEarnedReward);
                        //     Toast.makeText(getActivity(), ad.getResponseInfo() + "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e(TAG, "onAdFailedToLoad");
                        Log.e("adddss", " adss loading failed");
                    }
                });
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        goForDaily();
        Toast.makeText(getActivity(), "Reward Added To Wallet", Toast.LENGTH_SHORT).show();
    }


    SQLiteDatabase db;

    void insertData(String currentDate) {
        Cursor c = null;

        db = getActivity().openOrCreateDatabase("UserData", MODE_PRIVATE, null);
        String sql = "create table if not exists userdata (currentDate text);";
        db.execSQL(sql);
        ContentValues values = new ContentValues();
        values.put("currentDate", currentDate);
//        values.put("mon", mon);
//        values.put("year", year);
        db.insert("userdata", null, values);
        //    db.execSQL("update userdata set val='" + val + "' where cDate='" + currentDate + "';");
    }

    String getCDate() {
        Cursor c = null;
        String i = "";
        db = getActivity().openOrCreateDatabase("UserData", MODE_PRIVATE, null);
        db.execSQL("create table if not exists userdata (currentDate text);");
        c = db.rawQuery("select * from userdata;", null);
        c.moveToFirst();
        for (int ii = 0; c.moveToPosition(ii); ii++) {
            i = c.getString(0);
        }
        return i;
    }


    @SuppressLint("DefaultLocale")
    private String numberCalculation(long number) {
        if (number < 1000)
            return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f %c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    public void getPlayerData() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("dailycheck", API);
                params.put(USERID, userid);
                params.put(TOKEN, token);
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, LOADING);
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(ERROR, s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        checkModelList.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            checkModel ff = new checkModel();
                            ff.setDate(dataobj.getString("days"));
                            ff.setPoints(dataobj.getString("points"));
                            ff.setJoined(dataobj.getString("joined"));
                            checkModelList.add(ff);
                        }
                        checkAdapter = new CheckAdapter(getContext(), checkModelList);
                        rvChecking.setItemAnimator(new DefaultItemAnimator());
                        rvChecking.setHasFixedSize(true);
                        rvChecking.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        rvChecking.setAdapter(checkAdapter);
                        rvChecking.setVisibility(View.VISIBLE);
                    } else {
                        rvChecking.setVisibility(View.GONE);
                        noItem.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(ERROR, e.getMessage());
                }
                swipetoRefresh.setRefreshing(false);
                loading.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                noItem.setVisibility(View.GONE);
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

    public void mylay() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("myplay", API);
                params.put(USERID, userid);
                params.put(TOKEN, token);
                return requestHandler.sendPostRequest(BASEURL, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, LOADING);
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        total = obj.getInt("total");
                        days = obj.getInt("day");
                        if (total < 101) {
                            totalComplete.setText(total + "/100");
                        } else {
                            totalComplete.setText("100/100");
                        }
                        if (days < 30) {
                            totalComplete.setText(total + "/100");
                        } else {
                            totalComplete.setText("30/30");
                        }
                        try {
                            if (total == 0) {
                                hone.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);

                            } else if (total > 0 && total > 15) {
                                hone.getBackground().setColorFilter(getContext().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                htwo.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);

                            } else if (total > 15 && total > 51) {
                                hone.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                htwo.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                hthree.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                            } else if (total > 50 && total > 100) {
                                hone.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                htwo.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                hthree.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                                hfour.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        Bnner ru = new Bnner();
        ru.execute();
    }

    public void goForDaily() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("claim_daily", API);
                params.put("userid", userid);
                params.put("token", token);
                params.put("points", "100");
                params.put("op", "1");
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
                        Toast.makeText(getContext(), obj.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                        getPlayerData();
                    } else {
                        Toast.makeText(getContext(), obj.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                        claim.setEnabled(false);
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


    public class CheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        LayoutInflater inflater;
        List<checkModel> data = Collections.emptyList();

        // create constructor to innitilize context and data sent from MainActivity
        public CheckAdapter(Context context, List<checkModel> data) {
            this.context = context;
            this.data = data;
        }

        // Inflate the layout when viewholder created
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_dailycheckin, parent, false);
            return new MyHolder(view);
        }

        // Bind data
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // Get current position of item in recyclerview to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            final checkModel current = data.get(position);
            myHolder.day.setText("Day " + current.getDate());
            myHolder.points.setText("+" + current.getPoints());
            if (position == (getItemCount() - 1)) {
                myHolder.view.setVisibility(View.GONE);
            }
            if (current.getJoined().equalsIgnoreCase("1")) {
                myHolder.points.setText("✔");
            }
        }

        // return total item from List
        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView day;
            TextView points;
            View view;

            public MyHolder(View itemView) {
                super(itemView);
                day = itemView.findViewById(R.id.day);
                points = itemView.findViewById(R.id.points);
                view = itemView.findViewById(R.id.viewtwo);
            }

        }
    }

}

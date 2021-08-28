package com.hako.dreamproject.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hako.dreamproject.ActivityTracker;
import com.hako.dreamproject.DiscoverPeople;
import com.hako.dreamproject.HomeActivity;
import com.hako.dreamproject.LoginActivity;
import com.hako.dreamproject.PlayWithFriends;
import com.hako.dreamproject.PlayerSearching;
import com.hako.dreamproject.R;
import com.hako.dreamproject.activities.QuizActivity;
import com.hako.dreamproject.model.GameModel;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hako.dreamproject.utils.UsableFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

import static android.content.Context.MODE_PRIVATE;
import static com.hako.dreamproject.utils.Constant.API;
import static com.hako.dreamproject.utils.Constant.BASEURL;
import static com.hako.dreamproject.utils.Constant.DATA;
import static com.hako.dreamproject.utils.Constant.ERROR;
import static com.hako.dreamproject.utils.Constant.FALSE;
import static com.hako.dreamproject.utils.Constant.GAMELIST;
import static com.hako.dreamproject.utils.Constant.IMAGE;
import static com.hako.dreamproject.utils.Constant.MESSAGE;
import static com.hako.dreamproject.utils.Constant.NAME;
import static com.hako.dreamproject.utils.Constant.ROTATION;
import static com.hako.dreamproject.utils.Constant.TAG;
import static com.hako.dreamproject.utils.Constant.TOKEN;
import static com.hako.dreamproject.utils.Constant.USERID;

public class HomeFragment extends Fragment {
    DocumentReference myDocRef;
    View rootView;
    List<GameModel> gameModelList = new ArrayList<>();
    List<GameModel> populor = new ArrayList<>();
    MoreAdapter moreAdapter, playerAdapter;
    RecyclerView rvGame;
    RecyclerView rvMore;
    SwipeRefreshLayout swipetoRefresh;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout discoverpeople;

    public static int val1 = 0;
    public static int val2 = 0;
    public static int val3 = 0;


    LinearLayout loading, playWithFriends;
    LinearLayout noItem;
    NestedScrollView scrollView;
    ImageView ivUserProfile, homeGif1, homeGif2, coin_img;

    Random randoms;
    ImageView daily;
    String entry = "Entry Fee : ";
    // TAG
    String TAG_HOME_FRAGMENT = "homeFragment";

    // TextView
    TextView tvProfileIcon;
    //points
    String pointsStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        setViews();
        setOnClickListener();

//        ClickShrinkEffectKt.applyClickShrink(daily);

        loading.setVisibility(View.VISIBLE);
        TextView points = rootView.findViewById(R.id.points);
        points.setText(numberCalculation(Long.parseLong(AppController.getInstance().sharedPref.getString("spoints","0"))));
        points.setOnClickListener(v -> {
            Fragment fragment = new RewardFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        playWithFriends = rootView.findViewById(R.id.imageView5);
        playWithFriends.setOnClickListener(v -> startActivity(new Intent(getContext(), PlayWithFriends.class)));

        discoverpeople=rootView.findViewById(R.id.discoverpeople);
        discoverpeople.setOnClickListener(v -> startActivity(new Intent(getContext(), DiscoverPeople.class)));

        homeGif1 = rootView.findViewById(R.id.home_screen_gif1);
        homeGif2 = rootView.findViewById(R.id.home_screen_gif2);
        coin_img = rootView.findViewById(R.id.coin_img);


        try {
            Glide.with(this).load(R.drawable.invite_friend).into(homeGif1);
            Glide.with(this).load(R.drawable.discoverpeople).into(homeGif2);
            Glide.with(this).load(R.drawable.coin).into(coin_img);

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }

        if (UsableFunctions.checkLoggedInOrNot()){
            Glide.with(getActivity())
                    .load(AppController.getInstance().sharedPref.getString("sprofile","profile"))
                    .centerCrop()
                    .circleCrop()
                    .placeholder(R.drawable.profile_holder)
                    .into(ivUserProfile);
        }
        getPlayerData();
        return rootView;
    }


    private void setViews() {
        rvGame = rootView.findViewById(R.id.rvGame);
        rvMore = rootView.findViewById(R.id.rvMore);
        swipetoRefresh = rootView.findViewById(R.id.pulltorefresh);
        noItem = rootView.findViewById(R.id.noitem);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        loading = rootView.findViewById(R.id.loading);
        scrollView = rootView.findViewById(R.id.scrollview);
        scrollView.setVisibility(View.GONE);
        daily = rootView.findViewById(R.id.daily);



        // TextView

        // ImageView
        ivUserProfile = rootView.findViewById(R.id.iv_homeFrag_userProfile);
    }

    private void setOnClickListener() {
        swipetoRefresh.setOnRefreshListener(() -> {
            swipetoRefresh.setRefreshing(true);
            getPlayerData();
        });
        ivUserProfile.setOnClickListener(view -> {
            Fragment fragment = new ProfileFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .addToBackStack(null)
                    .commit();


        });
        daily.setOnClickListener(view -> {
            if (AppController.getInstance().sharedPref.getString("suserid","12345").equalsIgnoreCase("0")) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
//                goForDaily();
                Intent intent = new Intent(getContext(), QuizActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String numberCalculation(long number) {
        if (number < 1000)
            return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f %c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public void getPlayerData() {
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
                    if (obj.getString(ERROR).equalsIgnoreCase(FALSE)) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        String playerUserName = "";
                        String playerAvatarUrl = "";
                        String roomId = "";
                        String playerId = "1";

                        if (UsableFunctions.checkLoggedInOrNot()) {
                            playerUserName = AppController.getInstance().sharedPref.getString("sname","name");
                            playerAvatarUrl = AppController.getInstance().sharedPref.getString("sprofile","profile");
                        }

                        populor.clear();
                        gameModelList.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            roomId = UsableFunctions.getGameRoomId();
                            try {
                                String name[] = playerUserName.split(" ");
                                if (name.length > 1) {
                                    playerUserName = name[0];
                                }
                            } catch (Exception e) {
                            }

                            String url = dataobj.getString("url") + "?playerusername=" + playerUserName
                                    + "&playeravatarurl=" + playerAvatarUrl;

                            Log.d(TAG_HOME_FRAGMENT, "data: " + url);

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
                            if (dataobj.getString("type").equalsIgnoreCase("1")) {
                                populor.add(game);
                            }
                            gameModelList.add(game);
                        }
                        setUpRecyclerView(populor, gameModelList);
                    } else {
                        rvGame.setVisibility(View.GONE);
                        noItem.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void setUpRecyclerView(List<GameModel> popularGames, List<GameModel> gameModelList) {
        moreAdapter = new MoreAdapter(getActivity(), gameModelList);
        rvMore.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvMore.setItemAnimator(new DefaultItemAnimator());
        rvMore.setHasFixedSize(true);
        rvMore.setAdapter(moreAdapter);
        rvMore.setAdapter(new ScaleInAnimationAdapter(new AlphaInAnimationAdapter(moreAdapter)));

        playerAdapter = new MoreAdapter(getActivity(), popularGames);
        rvGame.setItemAnimator(new DefaultItemAnimator());
        rvGame.setHasFixedSize(true);
        rvGame.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvGame.setAdapter(playerAdapter);
        rvGame.setVisibility(View.VISIBLE);
        rvGame.setAdapter(new AlphaInAnimationAdapter(playerAdapter));

        moreAdapter.notifyDataSetChanged();
        playerAdapter.notifyDataSetChanged();
    }

    public class MoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        LayoutInflater inflater;
        List<GameModel> data = Collections.emptyList();
        int[] images = {R.drawable.hexa_gona, R.drawable.bull_fight, R.drawable.bubble_shooter};

        // create constructor to innitilize context and data sent from MainActivity
        public MoreAdapter(Context context, List<GameModel> data) {
            this.context = context;
            this.data = data;
        }

        // Inflate the layout when viewholder created
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_more, parent, false);
            return new MyHolder(view);
        }

        // Bind data
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // Get current position of item in recyclerview to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            final GameModel current = data.get(position);
            myHolder.name.setText(current.getName());
            String a = numberCalculation(Integer.parseInt(current.getPlaying()));
            myHolder.playing.setText(current.getPlaying() + " playing");
            switch (current.getName()) {
                case "Sheep Fight":
                    myHolder.clGame.setBackground(context.getDrawable(R.drawable.sheep_fight_home_logo));
                    break;
                case "Ludo":
                    myHolder.clGame.setBackground(context.getDrawable(R.drawable.wizard_hex_home_crop));
                    break;
                case "BULL FIGHT":
                    myHolder.clGame.setBackground(context.getDrawable(R.drawable.bull_fight_home_crop));
                    break;
                case "Bubble Shooter":
                    myHolder.clGame.setBackground(context.getDrawable(R.drawable.bubble_shooter_home_crop));
                    break;
                default:
                    myHolder.clGame.setBackground(context.getDrawable(R.drawable.test_game));
                    break;
            }

            try {
                //                Glide.with(context)
                //                        .load(current.getImage())
                //                        .placeholder(R.drawable.test_game)
                //                        .into(myHolder.image);
            } catch (Exception e) {
                Log.e(TAG_HOME_FRAGMENT, "exp: " + e.getMessage());
            }

            myHolder.holdres.setOnClickListener(
                    view ->
                            startGame(current.getId(), current.getName(), current.getImage(), current.getUrl(), current.getRotation())

            );

            if (getCount() == null) {
                insertData("1", current.getImage());

            } else {
                int val = Integer.parseInt(getCount()) + 1;
                insertData(String.valueOf(val), current.getImage());
            }

        }

        SQLiteDatabase db;

        void insertData(String count, String img) {
            Cursor c = null;
            db = getActivity().openOrCreateDatabase("UserData", MODE_PRIVATE, null);
            String sql = "create table if not exists userfav (count text,img text);";
            db.execSQL(sql);
            ContentValues values = new ContentValues();
            values.put("count", count);
            values.put("img", img);

            db.insert("userfav", null, values);
        }

        String getCount() {
            Cursor c = null;
            String i = "1";
            db = getActivity().openOrCreateDatabase("UserData", MODE_PRIVATE, null);
            db.execSQL("create table if not exists userfav (count text,img text);");
            c = db.rawQuery("select * from userfav;", null);
            c.moveToFirst();
            for (int ii = 0; c.moveToPosition(ii); ii++) {
                i = c.getString(0);
            }
            return i;
        }


        // return total item from List
        @Override
        public int getItemCount() {
            return data.size();
        }

        @SuppressLint("DefaultLocale")
        private String numberCalculation(long number) {
            if (number < 1000)
                return "" + number;
            int exp = (int) (Math.log(number) / Math.log(1000));
            return String.format("%.1f %c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView playing;
            ImageView image;
            ConstraintLayout holdres;
            ImageView clGame;

            public MyHolder(View itemView) {
                super(itemView);
                clGame = itemView.findViewById(R.id.cl_gameItem);
                name = itemView.findViewById(R.id.name);
                playing = itemView.findViewById(R.id.playing);
                image = itemView.findViewById(R.id.iv_itemMore_gameImage);
                holdres = itemView.findViewById(R.id.game_holder);
            }
        }
    }


    public void goForDaily() {
        class Bnner extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("claim_daily", API);
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
                        Toast.makeText(getContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), obj.getString(MESSAGE), Toast.LENGTH_LONG).show();
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

    public class PlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        LayoutInflater inflater;
        List<GameModel> data = Collections.emptyList();

        // create constructor to innitilize context and data sent from MainActivity
        public PlayerAdapter(Context context, List<GameModel> data) {
            this.context = context;
            this.data = data;
        }

        // Inflate the layout when viewholder created
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_game, parent, false);
            return new MyHolder(view);
        }

        // Bind data
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            // Get current position of item in recyclerview to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            final GameModel current = data.get(position);
            if (current.getType().equalsIgnoreCase("1")) {
                myHolder.name.setText(current.getName());
                try {
                    Glide.with(context).load(current.getImage()).placeholder(R.drawable.extra_placeholder).into(myHolder.image);
                } catch (Exception e) {
                    Log.e(ERROR, e.getMessage());
                }
                myHolder.holders.setOnClickListener(view ->
                        join(current.getId(), current.getName(), current.getImage(), current.getUrl(), current.getRotation(), position)
                );
            }


        }

        // return total item from List
        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView image;
            LinearLayout holders;

            public MyHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                image = itemView.findViewById(R.id.iv_itemMore_gameImage);
                holders = itemView.findViewById(R.id.lL_chatGameCard_game);

            }
        }

    }

    @SuppressLint("SetTextI18n")
    public void join(String id, String names, String image, String url, String rotation, int position) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.entryfee_layout, null);

        final TextView ok = view.findViewById(R.id.cancle);
        CircleImageView icon = view.findViewById(R.id.icon);
        ImageButton ten = view.findViewById(R.id.ten);
        ImageButton twenty = view.findViewById(R.id.twenty);
        ImageButton fifty = view.findViewById(R.id.fifty);
        TextView entyFee = view.findViewById(R.id.entryfee);
        Glide.with(getActivity()).load(image).into(icon);
        AtomicInteger total = new AtomicInteger();
        entyFee.setText(entry + total);
        ok.setOnClickListener(view12 -> {
            if (total.get() == 0) {
                Toast.makeText(getActivity(), "Select Entry FEE First", Toast.LENGTH_LONG).show();
                return;
            } else {
                if (AppController.getInstance().sharedPref.getString("suserid","12345").equalsIgnoreCase("0")) {
                    bottomSheetDialog.dismiss();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Log.e("HomeFragment", "val " + id);
                    int mypoint = Integer.parseInt(AppController.getInstance().sharedPref.getString("spoints","0"));
                    int fee = Integer.parseInt(total + "");
                    if (mypoint >= fee) {
                        JSONObject js = new JSONObject();
                        try {
                            js.put("id", id);
                            js.put("entry_fee", fee + "");
                            js.put("url", url + "");
                            js.put(IMAGE, image);
                            js.put(ROTATION, rotation);
                            js.put(NAME, names);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bottomSheetDialog.dismiss();
                        Intent i = new Intent(getActivity(), PlayerSearching.class);
                        i.putExtra(DATA, js.toString());
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    } else {
                        Toast.makeText(getActivity(), "Not Enough Coins", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        ten.setOnClickListener(view1 -> {
            ten.setBackground(requireContext().getDrawable(R.drawable.ten_pressed));
            twenty.setBackground(requireContext().getDrawable(R.drawable.twenty));
            fifty.setBackground(requireContext().getDrawable(R.drawable.fifty));

            total.set(10);
            entyFee.setText(entry + total);
            ok.getBackground().setColorFilter(getResources().getColor(R.color.green),
                    PorterDuff.Mode.SRC_ATOP);
        });
        twenty.setOnClickListener(view1 -> {
            ten.setBackground(requireContext().getDrawable(R.drawable.ten));
            twenty.setBackground(requireContext().getDrawable(R.drawable.twenty_pressed));
            fifty.setBackground(requireContext().getDrawable(R.drawable.fifty));

            total.set(20);
            entyFee.setText(entry + total);
            ok.getBackground().setColorFilter(getResources().getColor(R.color.green),
                    PorterDuff.Mode.SRC_ATOP);
        });
        fifty.setOnClickListener(view1 -> {
            ten.setBackground(requireContext().getDrawable(R.drawable.ten));
            twenty.setBackground(requireContext().getDrawable(R.drawable.twenty));
            fifty.setBackground(requireContext().getDrawable(R.drawable.fifty_pressed));

            total.set(50);
            entyFee.setText(entry + total);
            ok.getBackground().setColorFilter(getResources().getColor(R.color.green),
                    PorterDuff.Mode.SRC_ATOP);
        });

        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    private void startGame(String id, String names, String image, String url, String rotation) {
        int fee = 20;
        if (AppController.getInstance().sharedPref.getString("suserid","12345").equalsIgnoreCase("0")) {
            bottomSheetDialog.dismiss();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id.equals("3") || id.equals("4")) {
            Toast.makeText(getActivity(), "Coming Soon...", Toast.LENGTH_LONG).show();
        } else {
//            AppController.getInstance().sharedPref.edit().putString("spoints","10000").apply();
            int mypoint = Integer.parseInt(AppController.getInstance().sharedPref.getString("spoints","0"));
//            int fee = Integer.parseInt(total + "");
            if (mypoint >= fee) {
                JSONObject js = new JSONObject();
                try {
                    js.put("id", id);
                    js.put("entry_fee", fee);
                    js.put("url", url);
                    js.put(IMAGE, image);
                    js.put(ROTATION, rotation);
                    js.put(NAME, names);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bottomSheetDialog.dismiss();
                Intent i = new Intent(getActivity(), PlayerSearching.class);
                i.putExtra(DATA, js.toString());
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            } else {
                Toast.makeText(getActivity(), "Not Enough Coins", Toast.LENGTH_LONG).show();
            }
        }
    }
//    public void random() {
//        Dialog alertDialog = new Dialog(getActivity());
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alertDialog.setContentView(R.layout.random_dialog);
//        ImageView image = alertDialog.findViewById(R.id.iv_itemMore_gameImage);
//        TextView next = alertDialog.findViewById(R.id.next);
//        TextView name = alertDialog.findViewById(R.id.name);
//        TextView play = alertDialog.findViewById(R.id.play);
//        TextView playing = alertDialog.findViewById(R.id.playing);
//        CardView details = alertDialog.findViewById(R.id.details);
//        ImageView load = alertDialog.findViewById(R.id.load);
//        Glide.with(getActivity()).load(R.drawable.loading).into(load);
//        final GameModel[] n = {null};
//        n[0] = getRandomItem(gameModelList);
//        name.setText(n[0].getName());
//        playing.setText(n[0].getPlaying()+" playing");
//        try {
//            Glide.with(getActivity()).load(n[0].getImage()).into(image);
//        } catch (Exception e) {
//            Log.e(ERROR, e.getMessage());
//        }
//        GameModel finalN = n[0];
//        play.setOnClickListener(view ->
//                join(finalN.getId(), finalN.getName(), finalN.getImage(), finalN.getUrl(), finalN.getRotation())
//        );
//        next.setOnClickListener(view -> {
//            load.setVisibility(View.VISIBLE);
//            details.setVisibility(View.GONE);
//            new Handler().postDelayed(() -> {
//                load.setVisibility(View.GONE);
//                details.setVisibility(View.VISIBLE);
//                n[0] = getRandomItem(gameModelList);
//                name.setText(n[0].getName());
//                playing.setText(n[0].getPlaying()+" playing");
//                try {
//                    Glide.with(getActivity()).load(n[0].getImage()).into(image);
//                } catch (Exception e) {
//                    Log.e(ERROR, e.getMessage());
//                }
//            }, 3000);
//        });
//        new Handler().postDelayed(() -> {
//            load.setVisibility(View.GONE);
//            details.setVisibility(View.VISIBLE);
//        }, 3000);
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.show();
//    }
//
//    public <T> T getRandomItem(List<T> list) {
//        randoms = new Random();
//        int listSize = list.size();
//        int randomIndex = randoms.nextInt(listSize);
//        return list.get(randomIndex);
//    }

}

package com.hako.dreamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hako.dreamproject.adapters.chat.DiscoverPeopleAdapter;
import com.hako.dreamproject.model.DiscoverPeopleModel;
import com.hako.dreamproject.model.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverPeople extends AppCompatActivity {
//    List<DiscoverPeopleModel> peopleList;
    RecyclerView discover_recycler;
    ArrayList<String > user_id=new ArrayList<>();
    ArrayList<String> points=new ArrayList<>();
    ArrayList<String > userid=new ArrayList<>();
    ArrayList<String > name=new ArrayList<>();
    ArrayList<String > user_unique_id=new ArrayList<>();
    ArrayList<String> profile=new ArrayList<>();
     CircleImageView ivr1,ivr2,ivr3;
     TextView name1,name2,name3,p1,p2,p3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_people);
        ivr1=findViewById(R.id.iv_rankone);
        ivr2=findViewById(R.id.iv_rank2);
        ivr3=findViewById(R.id.iv_rank3);
        name1=findViewById(R.id.firstname);
        name2=findViewById(R.id.secondname);
        name3=findViewById(R.id.thirdname);
        p1=findViewById(R.id.firstpoint);
        p2=findViewById(R.id.secondpoint);
        p3=findViewById(R.id.thirdpoint);
        discover_recycler=findViewById(R.id.recyclerview);
        discover_recycler.setLayoutManager(new LinearLayoutManager(this));
        discover_recycler.setHasFixedSize(true);

        GetPeople getPeople=new GetPeople();
        getPeople.execute();

    }


    public class GetPeople extends AsyncTask<String,String ,String > {
        @Override
        protected String doInBackground(String... strings) {
            String current="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL("https://hoko.orsoot.com/api/index.php?leaderboard=1");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    current += (char) data;
                    data = inputStreamReader.read();
                }
                return current;
            }catch (MalformedURLException e){
            e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if (urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
            return current;
        //end of do
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject=new JSONObject(s);
                JSONArray jsonArray=jsonObject.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    user_id.add(jsonObject1.getString("user_id"));
                    points.add(jsonObject1.getString("points"));

                    JSONObject jsonArray1=jsonObject1.getJSONObject("userData");
                    userid.add(jsonArray1.getString("userid"));
                    name.add(jsonArray1.getString("name"));
                    user_unique_id.add(jsonArray1.getString("user_unique_id"));
                    profile.add(jsonArray1.getString("profile"));


                }



            }catch (JSONException e){
                e.printStackTrace();
            }
            Glide.with(DiscoverPeople.this).load(profile.get(0)).into(ivr1);
            Glide.with(DiscoverPeople.this).load(profile.get(1)).into(ivr2);
            Glide.with(DiscoverPeople.this).load(profile.get(2)).into(ivr3);
            name1.setText(name.get(0));
            name2.setText(name.get(1));
            name3.setText(name.get(2));
            p1.setText(points.get(0));
            p2.setText(points.get(1));
            p3.setText(points.get(2));
            DiscoverPeopleAdapter adapter = new DiscoverPeopleAdapter(DiscoverPeople.this, user_id, points, userid, name, user_unique_id, profile);
            discover_recycler.setAdapter(adapter);

        }

    }
}
package com.hako.dreamproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.hako.dreamproject.R;
import com.hako.dreamproject.model.QuizQuestion;
import com.hako.dreamproject.utils.AppController;
import com.hako.dreamproject.utils.RequestHandler;
import com.hardik.clickshrinkeffect.ClickShrinkEffectKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

public class QuizActivity extends AppCompatActivity {

    // TextView
    TextView tvAnsweredQuestion, tvTotalQuestion;
    TextView tvQuestion;

    // RadioGroup & RadioButton
    RadioGroup rgOptions;
    RadioButton rbOption1, rbOption2, rbOption3, rbOption4, rbOption5;

    // ProgressBar
    LinearProgressIndicator pbQuestionProgress;

    // Button
    Button btnNext;

    // ConstraintLayout
    ConstraintLayout cLMainSection;

    // CardView
    CardView cv1, cv2, cv3;

    // Live Data
    int totalLength = 0;
    int progressPerQuestion;
    Boolean isAnswerd = true;
    ArrayList<QuizQuestion> questionList = new ArrayList<>();
    MutableLiveData<Boolean> isQuestions = new MutableLiveData<Boolean>();
    MutableLiveData<Integer> questionNumber = new MutableLiveData<>();

    // TAG
    String TAG = "quizActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // setData
        isQuestions.setValue(false);
        questionNumber.setValue(0);

        setViews();
        setShrinkEffect();
        setOnClickListeners();
        getQuizQuestions();
        initiliseListner();

    }
    private void setViews(){
        // TextView
        tvAnsweredQuestion = findViewById(R.id.tv_quizActivity_answeredQuestions);
        tvTotalQuestion = findViewById(R.id.tv_quizActivity_totalQuestions);
        tvQuestion = findViewById(R.id.tv_quizActivity_question);

        // Button
        btnNext = findViewById(R.id.btn_quizActivity_next);

        // RadioGroup & RadioButton
        rgOptions = findViewById(R.id.rg_quizActivity_options);
        rbOption1 = findViewById(R.id.rb_quizActivity_option1);
        rbOption2 = findViewById(R.id.rb_quizActivity_option2);
        rbOption3 = findViewById(R.id.rb_quizActivity_option3);
        rbOption4 = findViewById(R.id.rb_quizActivity_option4);
        rbOption5 = findViewById(R.id.rb_quizActivity_option5);

        // ProgressBar
        pbQuestionProgress = findViewById(R.id.pb_quizActivity_questionProgress);

        // ConstraintLayout
        cLMainSection = findViewById(R.id.cl_quizActivity_mainSection);

        // CardViews
        cv1 = findViewById(R.id.cv_quizActivity_cv1);
        cv2 = findViewById(R.id.cv_quizActivity_cv2);
        cv3 = findViewById(R.id.cv_quizActivity_cv3);

    }
    private void setShrinkEffect(){
        ClickShrinkEffectKt.applyClickShrink(btnNext);
        ClickShrinkEffectKt.applyClickShrink(rbOption1);
        ClickShrinkEffectKt.applyClickShrink(rbOption2);
        ClickShrinkEffectKt.applyClickShrink(rbOption3);
        ClickShrinkEffectKt.applyClickShrink(rbOption4);

    }
    private void setOnClickListeners(){
        btnNext.setOnClickListener( view -> {
            if(isQuestions.getValue()){
                questionNumber.setValue(questionNumber.getValue() + 1);
            }
        });
        rbOption1.setOnClickListener( view -> {
            if(rbOption1.isChecked()){
                QuizQuestion quizQuestion = questionList.get(questionNumber.getValue());
                String questionId = quizQuestion.getQuestionId();
                submitAnswer(1, questionId, quizQuestion.getOptions(), 0, rbOption1);
            }
        });
        rbOption2.setOnClickListener( view -> {
            if(rbOption2.isChecked()){
                QuizQuestion quizQuestion = questionList.get(questionNumber.getValue());
                String questionId = quizQuestion.getQuestionId();
                submitAnswer(2, questionId, quizQuestion.getOptions(), 1, rbOption2);
            }
        });
        rbOption3.setOnClickListener( view -> {
            if(rbOption3.isChecked()){
                QuizQuestion quizQuestion = questionList.get(questionNumber.getValue());
                String questionId = quizQuestion.getQuestionId();
                submitAnswer(3, questionId, quizQuestion.getOptions(), 2, rbOption3);
            }
        });
        rbOption4.setOnClickListener( view -> {
            if(rbOption4.isChecked()){
                QuizQuestion quizQuestion = questionList.get(questionNumber.getValue());
                String questionId = quizQuestion.getQuestionId();
                submitAnswer(4, questionId, quizQuestion.getOptions(), 3, rbOption4);
            }
        });
        rbOption5.setOnClickListener( view -> {
            if(rbOption5.isChecked()){
                QuizQuestion quizQuestion = questionList.get(questionNumber.getValue());
                String questionId = quizQuestion.getQuestionId();
                submitAnswer(5, questionId, quizQuestion.getOptions(), 4, rbOption5);
            }
        });
    }
    private void initiliseListner(){
        isQuestions.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    btnNext.setBackground(getDrawable(R.drawable.btn_next));
                }else {
                    btnNext.setBackground(getDrawable(R.drawable.btn_next_fadded));
                }
            }
        });
        questionNumber.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(isQuestions.getValue()){
                    int currentQuestion = integer + 1;
                    if(currentQuestion == totalLength){
                        isQuestions.setValue(false);
                    }
                    tvAnsweredQuestion.setText("Question " + String.valueOf(integer+1));

                    int currentProgress = pbQuestionProgress.getProgress();
                    pbQuestionProgress.setProgressCompat(currentProgress+progressPerQuestion, true);

                    QuizQuestion quizQuestion = questionList.get(integer);
                    ArrayList<QuizQuestion.Options> options = quizQuestion.getOptions();
                    QuizQuestion.Options options1 = options.get(0);
                    QuizQuestion.Options options2 = options.get(1);
                    QuizQuestion.Options options3 = options.get(2);
                    QuizQuestion.Options options4 = options.get(3);
                    QuizQuestion.Options options5 = options.get(4);

                    tvQuestion.setText(quizQuestion.getTitle());
                    rbOption1.setText(options1.getOption());
                    rbOption2.setText(options2.getOption());
                    rbOption3.setText(options3.getOption());
                    rbOption4.setText(options4.getOption());
                    rbOption5.setText(options5.getOption());

                }
            }
        });
    }
    private void getQuizQuestions(){
        class Quiz extends AsyncTask<Void, Void, String>{

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                String url = "https://hoko.orsoot.com/quiz/public/api/category/1/1";
                HashMap<String, String> params = new HashMap<>();
                return requestHandler.sendPostRequest(url, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try{
                    JSONObject obj = new JSONObject(s);
                    JSONArray questions = obj.getJSONArray("availableQuestionList");
                    tvTotalQuestion.setText("/" + String.valueOf(questions.length()));
                    totalLength = questions.length();
                    progressPerQuestion = 100/totalLength;
                    Log.d(TAG, " Questions: " + questions);
                    for(int i = 0; i< questions.length(); i++){
                        JSONObject jsonObject = questions.getJSONObject(i);
                        QuizQuestion quizQuestion = new QuizQuestion();
                        quizQuestion.setCategory(jsonObject.getString("category"));
                        quizQuestion.setSubCategory(jsonObject.getString("sub_category"));
                        quizQuestion.setCategoryId(jsonObject.getInt("category_id"));
                        quizQuestion.setSubCategoryId(jsonObject.getString("sub_category_id"));
                        quizQuestion.setId(jsonObject.getInt("id"));
                        quizQuestion.setQuestionId(jsonObject.getString("question_id"));
                        quizQuestion.setTitle(jsonObject.getString("title"));
                        quizQuestion.setHasImage(jsonObject.getInt("has_image"));
                        quizQuestion.setImage(jsonObject.getString("image"));
                        quizQuestion.setPoints(jsonObject.getString("point"));
                        quizQuestion.setCoin(jsonObject.getString("coin"));
                        quizQuestion.setTimeLimit(jsonObject.getString("time_limit"));
                        quizQuestion.setStatus(jsonObject.getString("status"));
                        quizQuestion.setHints(jsonObject.getString("hints"));
                        quizQuestion.setSkipCoins(jsonObject.getString("skip_coin"));
                        quizQuestion.setOptionType(jsonObject.getString("option_type"));

                        ArrayList<QuizQuestion.Options> options = new ArrayList<>();
                        JSONArray jsonOptions = jsonObject.getJSONArray("options");
                        for(int j =0; j<jsonOptions.length(); j++){
                            QuizQuestion.Options op = new QuizQuestion.Options();
                            JSONObject jsonOption = jsonOptions.getJSONObject(j);
                            op.setId(jsonOption.getInt("id"));
                            op.setOption(jsonOption.getString("question_option"));
                            op.setType(jsonOption.getInt("type"));
                            op.setAnswer(jsonOption.getString("is_answer"));
                            options.add(op);
                        }

                        quizQuestion.setOptions(options);
                        questionList.add(quizQuestion);
                    }
                    isQuestions.setValue(true);
                    questionNumber.setValue(0);

                }catch (JsonIOException | JSONException e){
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        }
        Quiz quiz = new Quiz();
        quiz.execute();
    }


    private void submitAnswer(int id, String questionId,ArrayList<QuizQuestion.Options> options, int selected, RadioButton rb){
        Log.d(TAG, "ID: " + id);

        int currentPosition = questionNumber.getValue() + 1;
        if(isAnswerd){
            setAnswer(options, selected, rb);

            class submitAnswer extends AsyncTask<Void, Void, String >{

                @Override
                protected String doInBackground(Void... voids) {
                    String url = "https://hoko.orsoot.com/quiz/public/api/submit-answer/" + questionId;
                    RequestHandler requestHandler = new RequestHandler();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("answer", String.valueOf(id));
                    params.put("user_id", AppController.getInstance().getUser_unique_id());

                    return requestHandler.sendPostRequest(url, params);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    try{
                        JSONObject jsonObject = new JSONObject(s);
                        Log.d(TAG, " SubmitAnswerData: " + s);
                        if(currentPosition == totalLength){
                            isAnswerd = false;
                        }
                    }catch (Exception e){
                        Log.e(TAG, " SubmitAnswerData Exception: " + e.getMessage());
                    }
                }
            }
            submitAnswer submitAnswer = new submitAnswer();
            submitAnswer.execute();
        }else{
            Toast.makeText(this, "All Questions are completed", Toast.LENGTH_SHORT).show();
        }
    }
    private void setAnswer(ArrayList<QuizQuestion.Options> options, int selected, RadioButton rb){
        if(options.get(0).getAnswer().equals("1")){
            rbOption1.setBackground(getDrawable(R.drawable.radio_button_green));
            rbOption1.setTextColor(getColor(R.color.white));
            rbOption1.setButtonTintList(this.getResources().getColorStateList(R.color.white));
        }else if(options.get(1).getAnswer().equals("1")){
            rbOption2.setBackground(getDrawable(R.drawable.radio_button_green));
            rbOption2.setTextColor(getColor(R.color.white));
            rbOption2.setButtonTintList(this.getResources().getColorStateList(R.color.white));
        }else if(options.get(2).getAnswer().equals("1")){
            rbOption3.setBackground(getDrawable(R.drawable.radio_button_green));
            rbOption3.setTextColor(getColor(R.color.white));
            rbOption3.setButtonTintList(this.getResources().getColorStateList(R.color.white));
        }else if(options.get(3).getAnswer().equals("1")){
            rbOption4.setBackground(getDrawable(R.drawable.radio_button_green));
            rbOption4.setTextColor(getColor(R.color.white));
            rbOption4.setButtonTintList(this.getResources().getColorStateList(R.color.white));
        }else if(options.get(4).getAnswer().equals("1")){
            rbOption5.setBackground(getDrawable(R.drawable.radio_button_green));
            rbOption5.setTextColor(getColor(R.color.white));
            rbOption5.setButtonTintList(this.getResources().getColorStateList(R.color.white));
        }

        QuizQuestion.Options selectedOption = options.get(selected);
        if(selectedOption.getAnswer().equals("1")){
            rb.setBackground(getDrawable(R.drawable.radio_button_green));
            rb.setTextColor(getColor(R.color.white));
            rb.setButtonTintList(this.getResources().getColorStateList(R.color.white));
        }else{
            rb.setBackground(getDrawable(R.drawable.radio_button_red));
            rb.setTextColor(getColor(R.color.white));
            rb.setButtonTintList(this.getResources().getColorStateList(R.color.white));
        }

    }

}
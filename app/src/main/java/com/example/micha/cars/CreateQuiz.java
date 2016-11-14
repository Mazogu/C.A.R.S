package com.example.micha.cars;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateQuiz extends AppCompatActivity {
    private String username;
    private String quiz;
    private String classroom;
    private EditText question;
    private EditText answer1;
    private EditText answer2;
    private EditText answer3;
    private EditText answer4;
    private Button add;
    private Button submit;
    private URL url;
    private String responseString;
    private RadioGroup group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            username = extra.getString("username");
            classroom = extra.getString("class");
            quiz = extra.getString("quiz");
        }
        add = (Button) findViewById(R.id.addquestion);
        submit = (Button) findViewById(R.id.submit);
        answer1 = (EditText) findViewById(R.id.answer1);
        answer2 = (EditText) findViewById(R.id.answer2);
        answer3 = (EditText) findViewById(R.id.answer3);
        answer4 = (EditText) findViewById(R.id.answer4);
        question = (EditText) findViewById(R.id.askquestion);
        group = (RadioGroup) findViewById(R.id.answers);

        add.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                String a1,a2,a3,a4;
                String q = Html.escapeHtml(question.getText().toString());
                a1 = Html.escapeHtml(answer1.getText().toString());
                a2 = Html.escapeHtml(answer2.getText().toString());
                a3 = Html.escapeHtml(answer3.getText().toString());
                a4 = Html.escapeHtml(answer4.getText().toString());
                int answer = group.getCheckedRadioButtonId();
                RadioButton selected = (RadioButton)findViewById(answer);
                String correct = Html.escapeHtml(selected.getText().toString());
                Log.i("What is Correct?",correct);
                QuizTask task = new QuizTask(q,a1,a2,a3,a4,correct);
                task.execute();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    /**
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    **/
    private class QuizTask extends AsyncTask<String,Double,String> {
        private String thisQuestion,a1,a2,a3,a4,correct;
        public QuizTask(String thisQuestion,String a1, String a2, String a3, String a4,String correct){
            this.thisQuestion = thisQuestion;
            this.a1 = a1;
            this.a2 = a2;
            this.a3 = a3;
            this.a4 = a4;
            this.correct = correct;
        }
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected String doInBackground(String... params) {
            url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://ec2-35-160-178-210.us-west-2.compute.amazonaws.com:8080/quizquestions");
                connection = (HttpURLConnection) url.openConnection();
                OutputStream os = null;
                InputStream is = null;
                connection.setReadTimeout(10000 );
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Host", "android.schoolportal.gr");
                connection.connect();
                String request = "{\"class\":\""+classroom+"\",\"new\":\""+"1"+"\",\"Quiz\":\""+quiz+"\",\"question\":\""+thisQuestion+"\",\"answerA\":\""+a1+"\",\"answerB\":\""+a2+"\",\"answerC\":\""+a3+"\",\"answerD\":\""+a4+"\",\"correct\":\""+correct+"\"}";
                request = Html.escapeHtml(request);
                os = connection.getOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(os);
                out.write(request);
                out.close();
                String maybe = connection.getResponseMessage();
                is = connection.getInputStream();
                InputStreamReader in = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(in);
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                responseString = result.toString();
            }
            catch(IOException e){
                Log.i("Darn",e.toString());
            }
            finally{
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.i("Hope",responseString);
        }
    }
}

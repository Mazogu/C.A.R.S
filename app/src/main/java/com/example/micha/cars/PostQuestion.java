package com.example.micha.cars;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostQuestion extends AppCompatActivity {
    private Button button;
    private String username;
    private String classroom;
    private String responseString;
    private URL url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final EditText subject = (EditText) findViewById(R.id.questionsubject);
        final EditText question = (EditText) findViewById(R.id.questionfield);
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            username = extra.getString("username");
            classroom = extra.getString("classroom");
        }
        button = (Button) findViewById(R.id.postquestion);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String questionString = subject.getText()+": "+question.getText();
                PostTask post = new PostTask(username,classroom,questionString);
                post.execute();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private class PostTask extends AsyncTask<String,Double,String>{
        private final String user,room,question;
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public PostTask(String user, String room, String question){
            this.user = user;
            this.room = room;
            this.question = Html.escapeHtml(question);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected String doInBackground(String... params) {
            url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://ec2-35-160-178-210.us-west-2.compute.amazonaws.com:8080/questions");
                connection = (HttpURLConnection) url.openConnection();
                OutputStream os = null;
                InputStream is = null;
                connection.setReadTimeout(10000 /* milliseconds */);
                connection.setConnectTimeout(15000 /* milliseconds */);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Host", "android.schoolportal.gr");
                connection.connect();
                String request = "{\"user\":\""+user+"\",\"class\":\""+room+"\",\"new\":\""+"1"+"\",\"question\":\""+question+"\"}";
                os = connection.getOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(os);
                request = Html.escapeHtml(request);
                out.write(request);
                out.close();
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
            Intent intent = new Intent(PostQuestion.this,QuestionsBoard.class);
            intent.putExtra("username",username);
            intent.putExtra("classroom",classroom);
            startActivity(intent);
        }
    }

}

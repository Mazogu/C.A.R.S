package com.example.micha.cars;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;


public class ClassRoomActivity extends ListActivity {
    private String responseString;
    private String quizString;
    private Button button;
    private Button button1;
    private URL url;
    private String username;
    private String classroom;
    View taView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        taView = findViewById(R.id.Ta);
        taView.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.quizbutton);
        Bundle extra = getIntent().getExtras();
        ClassRoomsTask server = new ClassRoomsTask();
        if(extra!=null){
            username = extra.getString("username");
            classroom = extra.getString("class");
        }
        server.execute();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizTask quiz = new QuizTask();
                quiz.execute();
            }
        });
        button1 = (Button) findViewById(R.id.boardbutton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassRoomActivity.this,QuestionsBoard.class);
                intent.putExtra("username",username);
                intent.putExtra("classroom",classroom);
                startActivity(intent);
            }
        });
    }
    private class QuizTask extends AsyncTask<String,Double,String>{
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected String doInBackground(String... params) {
            url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://ec2-35-160-178-210.us-west-2.compute.amazonaws.com:8080/quizzes");
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
                String request = "{\"user\":\""+username+"\",\"class\":\""+classroom+"\",\"new\":\""+"1"+"\",\"Quiz\":\""+"Tquiz"+"\",\"duration\":\""+"3600"+"\"}";
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
                quizString = result.toString();
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
            Intent intent = new Intent(ClassRoomActivity.this,CreateQuiz.class);
            intent.putExtra("user",username);
            intent.putExtra("class",classroom);
            intent.putExtra("quiz","T3quiz");
            startActivity(intent);
        }
    }
    private class ClassRoomsTask extends AsyncTask<String,Double,String> {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected String doInBackground(String... params) {
            url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://ec2-35-160-178-210.us-west-2.compute.amazonaws.com:8080/quizzes");
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
                String request = "{\"user\":\""+username+"\",\"class\":\""+classroom+"\",\"new\":\""+"0"+"\",\"Quiz\":\""+"0"+"\",\"duration\":\""+"0"+"\"}";
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
            if(responseString!=null) {
                populate(responseString);
                Log.i("Perhaps",responseString);
            }
            else{
                Log.i("Ummm","Crap");
            }
        }
    }
    protected void populate(String parse){
        String[] quizzes = parse.split(">");
        for(int i = 0;i < quizzes.length;i++){
            quizzes[i] = StringEscapeUtils.unescapeHtml3(quizzes[i]);
        }
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,quizzes));
        getListView().setTextFilterEnabled(true);
    }
}

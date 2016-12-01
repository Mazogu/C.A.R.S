package com.example.micha.cars;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ViewQuestion extends AppCompatActivity {
    TextView questionView;
    ListView list;
    Button post;
    EditText answer;
    URL url;
    String responseString;
    String answerString;
    ArrayList<Post> answers;
    String user;
    String classname;
    String question;
    boolean teacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        questionView = (TextView) findViewById(R.id.viewedquestion);
        list = (ListView) findViewById(android.R.id.list);
        post = (Button) findViewById(R.id.postreponse);
        answer = (EditText) findViewById(R.id.writeresponse);
        answers = new ArrayList<Post>();
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            user = extra.getString("username");
            classname = extra.getString("class");
            question = extra.getString("question");
            teacher = extra.getBoolean("teacher");
        }
        PostTask posts = new PostTask();
        posts.execute();
        questionView.setText(question);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = answer.getText().toString();
                ResponseTask post = new ResponseTask(response);
                post.execute();
                Intent intent = new Intent(ViewQuestion.this,QuestionsBoard.class);
                intent.putExtra("username",user);
                intent.putExtra("classroom",classname);
                startActivity(intent);
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private class PostTask extends AsyncTask<String,Double,String> {
        public PostTask(){

        }
        @Override
        protected String doInBackground(String... params) {
            url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://ec2-35-160-178-210.us-west-2.compute.amazonaws.com:8080/answers");
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
                String request = "{\"user\":\""+user+"\",\"class\":\""+classname+"\",\"new\":\""+"0"+"\",\"question\":\""+question+"\",\"answer\":\""+""+"\",\"anonymous\":\""+"1"+"\"}";
                os = connection.getOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(os);
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
                Log.i("Maybe",request);
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
            answers = populate(responseString);
            PostAdapter adapter = new PostAdapter(ViewQuestion.this, R.layout.answer_layout,R.id.postTime,answers);
            list.setAdapter(adapter);
            Log.i("Server",responseString);
            if(responseString.contentEquals(""))
                Log.i("MMMM","Darn");
        }
    }
    private class ResponseTask extends AsyncTask<String,Double,String> {
        private final String answer;
        public ResponseTask(String answer){
            this.answer = answer;
        }
        @Override
        protected String doInBackground(String... params) {
            url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://ec2-35-160-178-210.us-west-2.compute.amazonaws.com:8080/answers");
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
                String request = "{\"user\":\""+user+"\",\"class\":\""+classname+"\",\"new\":\""+"1"+"\",\"question\":\""+question+"\",\"answer\":\""+answer+"\",\"anonymous\":\""+"1"+"\"}";
                os = connection.getOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(os);
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
                Log.i("Maybe",request);
                answerString = result.toString();
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
            //Log.i("Server",s);
            Log.i("M",answerString);
            if(answerString.contentEquals(""))
                Log.i("A","Whelp");
        }
    }
    private class Post{
        private final String user,time,response;
        public Post(String user, String time, String response){
            this.user = user;
            this.time = time;
            this.response  = response;
        }
        public String getUser(){
            return user;
        }
        public String getTime(){
            return time;
        }
        public String getResponse(){
            return response;
        }
    }
    protected ArrayList<Post> populate(String parse){
        String[] list = parse.split(">");
        ArrayList<Post> posts = new ArrayList<Post>();
        for(int i = 0;i < list.length;i++) {
            list[i] = StringEscapeUtils.unescapeHtml3(list[i]);
            String[] values = list[i].split("<");
            if(values.length > 1){
                if(values[2].contentEquals("1")&&!teacher){
                    values[1] = "Anonymous";
                }
                Post post = new Post(values[1],values[3],values[0]);
                posts.add(post);
            }
        }
        return posts;
    }
    private class PostAdapter extends ArrayAdapter{
        ArrayList<Post> replies;
        public PostAdapter(Context context, int resource, int viewId, ArrayList<Post> replies) {
            super(context, resource, viewId, replies);
            this.replies = replies;
        }

        @Override
        public Object getItem(int position) {
            return replies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position,convertView,parent);
            TextView postText = (TextView) view.findViewById(R.id.post);
            TextView userText = (TextView) view.findViewById(R.id.poster);
            TextView timeText = (TextView) view.findViewById(R.id.postTime);
            if(!replies.isEmpty()) {
                postText.setText(replies.get(position).getResponse());
                userText.setText(replies.get(position).getUser());
                timeText.setText(replies.get(position).getTime());
            }
            Log.i("Test","What is happening?");
            return view;
        }

        @Override
        public int getCount() {
            return replies.size();
        }
    }
}

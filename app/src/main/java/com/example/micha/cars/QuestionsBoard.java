package com.example.micha.cars;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class QuestionsBoard extends ListActivity {
    private Button button;
    private URL url;
    private QuestionsTask server = null;
    String responseString;
    String user;
    String classroom;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        //list = (ListView)findViewById(R.id.list);
        if(extra!=null){
            user = extra.getString("username");
            classroom = extra.getString("classroom");
        }
        server = new QuestionsTask(user,classroom);
        server.execute();
        setContentView(R.layout.activity_questions_board);
        button = (Button) findViewById(R.id.postbutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionsBoard.this,PostQuestion.class);
                intent.putExtra("username",user);
                intent.putExtra("classroom",classroom);
                startActivity(intent);
            }
        });
    }
    private class QuestionsTask extends AsyncTask<String,Double,String>{
        private final String user,classroom;
        public QuestionsTask(String user, String classroom){
            this.user = user;
            this.classroom = classroom;
        }
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
                String request = "{\"user\":\""+user+"\",\"class\":\""+classroom+"\",\"new\":\""+"0"+"\",\"question\":\""+""+"\"}";
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
           //Log.i("Server",s);
            Log.i("Test",responseString);
            populate(responseString);
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void populate(String parse){
        String[] questions = parse.split(">");
        for(int i = 0;i < questions.length;i++){
            questions[i] = StringEscapeUtils.unescapeHtml3(questions[i]);
        }
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,questions));
        getListView().setTextFilterEnabled(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

    }
}

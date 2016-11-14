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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClassPage extends ListActivity {
    private String username;
    URL url;
    ClassesTask server;
    String responseString;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            username = extras.getString("username");
        server = new ClassesTask(username);
        server.execute();
    }
    private class ClassesTask extends AsyncTask<String,Double,String> {
        private final String user;
        public ClassesTask(String user){
            this.user = user;
        }
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected String doInBackground(String... params) {
            url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://ec2-35-160-178-210.us-west-2.compute.amazonaws.com:8080/classes");
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
                String request = "{\"user\":\""+user+"\",\"teacher\":\""+"0"+"\",\"class\":\""+""+"\",\"new\":\""+"0"+"\"}";
                request = Html.escapeHtml(request);
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
            populate(responseString);
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void populate(String parse){
        String[] classes = parse.split(">");
        for(int i = 0;i < classes.length;i++){
            classes[i] = StringEscapeUtils.unescapeHtml3(classes[i]);
        }
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,classes));
        getListView().setTextFilterEnabled(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position,id);
        String classroom = getListView().getItemAtPosition(position).toString();
        Log.i("Confused",classroom);
        Intent intent = new Intent(ClassPage.this,ClassRoomActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("class",classroom);
        startActivity(intent);
    }
}

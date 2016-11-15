package com.example.micha.cars;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddClass extends Activity {
    EditText classname;
    Button add;
    URL url;
    String responseString;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        add = (Button) findViewById(R.id.addclass);
        classname = (EditText) findViewById(R.id.classname);
        Bundle extra = getIntent().getExtras();
        if(extra !=null)
            username = extra.getString("username");
        DisplayMetrics m = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(m);
        int width = m.widthPixels;
        int height = m.heightPixels;
        getWindow().setLayout((int) Math.round(width * .8),(int) Math.round(height*.4));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassesTask server = new ClassesTask(username,classname.getText().toString());
                server.execute();
            }
        });

    }

    private class ClassesTask extends AsyncTask<String,Double,String> {
        private final String user,classRoom;
        public ClassesTask(String user,String classname){
            this.user = user;
            this.classRoom = classname;
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
                String request = "{\"user\":\""+user+"\",\"teacher\":\""+"1"+"\",\"class\":\""+classRoom+"\",\"new\":\""+"1"+"\"}";
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
            if(responseString.contentEquals("1")) {
                Intent intent = new Intent(AddClass.this, ClassPage.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
            else{
                Log.i("Didn't work",responseString);
            }
        }
    }

}

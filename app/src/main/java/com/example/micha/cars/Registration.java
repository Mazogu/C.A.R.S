package com.example.micha.cars;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class Registration extends AppCompatActivity {
    private URL url;
    private EditText emailView;
    private EditText passwordView;
    private EditText FnameView;
    private EditText LnameView;
    private EditText confirmView;
    private Button button;
    private Server server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        emailView = (EditText) findViewById(R.id.rEmail);
        passwordView = (EditText) findViewById(R.id.password);
        FnameView = (EditText) findViewById(R.id.fName);
        LnameView = (EditText) findViewById(R.id.lName);
        confirmView = (EditText) findViewById(R.id.confirm);
        button = (Button) findViewById(R.id.register);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
    }
    protected void attemptRegistration(){
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String fname = FnameView.getText().toString();
        String lname = LnameView.getText().toString();
        server = new Server(email,password,fname,lname);
        if(checkEmail(email)&&confirmPassword()){
                server.execute();
        }
    }
    protected boolean checkEmail(String email){
        if(email.contains("@")&&(email.endsWith(".com")||email.endsWith(".edu")||email.endsWith(".net"))){
            return true;
        }
        return false;
    }
    protected boolean confirmPassword(){
        String password = passwordView.getText().toString();
        String confirm = confirmView.getText().toString();

        if(password.length() < 6|| TextUtils.isEmpty((password))){
            passwordView.setError("Password is too short");
            return false;
        }
        if(!password.contentEquals(confirm)){
            passwordView.setError("Entries 'password' and 'confirm password' don't match");
            return false;
        }
        return true;
    }
    private class Server extends AsyncTask<String,Double,String>{
        private final String email,password,fName,lName;
        public Server(String email,String password,String fName, String lName){
            this.email = email;
            this.password = password;
            this.fName = fName;
            this.lName = lName;
        }
        @Override
        protected String doInBackground(String... params) {
            url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://ec2-35-160-178-210.us-west-2.compute.amazonaws.com:8080/login");
                connection = (HttpURLConnection) url.openConnection();
                OutputStream is = null;
                connection.setReadTimeout(10000 /* milliseconds */);
                connection.setConnectTimeout(15000 /* milliseconds */);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Host", "android.schoolportal.gr");
                connection.connect();

                String request = "{\"user\":\""+email+"\",\"pass\":\""+password+"\",\"toReg\":\""+1+"\",\"first\":\""+fName+"\",\"last\":\""+lName+"\"}";
                is = connection.getOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(is);
                out.write(request);
                out.close();
                String perhaps = connection.getResponseMessage();
                Log.i(perhaps,"Dang");


            }
            catch(IOException e){
                Log.i("Hmmm","Bleeeegh");
            }
            finally{
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i(s,"Did it work?");
            startActivity(new Intent(Registration.this,LoginActivity.class));
        }
    }


}

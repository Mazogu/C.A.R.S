package com.example.micha.cars;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class ClassPage extends ListActivity {
    private String username;
    URL url;
    ClassesTask server;
    String responseString;
    Button newClass;
    Button register;
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
        newClass = (Button) findViewById(R.id.createclass);
        register = (Button) findViewById(R.id.registerclass);
        newClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassPage.this,AddClass.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassPage.this,RegisterClass.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
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
            Log.i("Just checking",responseString);
            populate(responseString);
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void populate(String parse){
        String[] classes = parse.split(">");
        ArrayList<Classes> classList = new ArrayList<Classes>();
        if(classes.length > 0) {
            for (int i = 0; i < classes.length; i++) {
                classes[i] = StringEscapeUtils.unescapeHtml3(classes[i]);
                String[] info = classes[i].split("<");
                boolean teacher = false;
                if(info.length>1) {
                    if (info[1].contentEquals("1"))
                        teacher = true;
                    classList.add(new Classes(info[0], teacher));
                }
            }
        }
        setListAdapter(new ClassAdapter(this,android.R.layout.simple_list_item_2,android.R.id.text1,classList));
        getListView().setTextFilterEnabled(true);
    }
    private class Classes{
        private final String className;
        private final boolean teacher;
        public Classes(String className,boolean teacher){
            this.className = className;
            this.teacher = teacher;
        }
        public String getClassname(){
            return className;
        }
        public boolean getTeacher(){
            return teacher;
        }
    }
    private class ClassAdapter extends ArrayAdapter{
        ArrayList<Classes> rooms;
        public ClassAdapter(Context context, int resource, int viewId, ArrayList<Classes> rooms) {
            super(context, resource, viewId, rooms);
            this.rooms = rooms;
        }

        @Override
        public Object getItem(int position) {
            return rooms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position,convertView,parent);
            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            TextView text2 = (TextView) view.findViewById(android.R.id.text2);
            if(!rooms.isEmpty()) {
                text1.setText(rooms.get(position).getClassname());
                if (rooms.get(position).getTeacher()) {
                    text2.setText("Teacher");
                } else {
                    text2.setText("Student");
                }
            }
            return view;
        }

        @Override
        public int getCount() {
            return rooms.size();
        }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position,id);
        Classes classroom = (Classes) getListView().getItemAtPosition(position);
        Intent intent = new Intent(ClassPage.this,ClassRoomActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("class",classroom.getClassname());
        intent.putExtra("teacher",classroom.getTeacher());
        startActivity(intent);
    }
}

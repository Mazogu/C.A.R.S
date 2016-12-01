package com.example.micha.cars;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Quiz extends FragmentActivity {
    ViewPager pager;
    MyAdapter adapter;
    Button button;
    String responseString;
    URL url;
    static int NUM = 10;
    static String[] questions;
    String classroom;
    String quiz;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            username = extra.getString("username");
            classroom = extra.getString("class");
            quiz = extra.getString("quiz");
        }
        pager = (ViewPager)findViewById(R.id.pager);
        QuizTask currentQuiz = new QuizTask();
        currentQuiz.execute();
        button = (Button) findViewById(R.id.questionbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Quiz.this,Results.class));
            }
        });
    }
    public static class MyAdapter extends FragmentPagerAdapter {
        private SparseArrayCompat<Fragment> pages = new SparseArrayCompat<Fragment>();
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            pages.put(position,fragment);
            return fragment;
        }

        public Fragment returnFragment(int position){
            return pages.get(position);
        }
    }

    public static class ArrayListFragment extends Fragment {
        String text;
        TextView questionTitle;
        RadioButton a1;
        RadioButton a2;
        RadioButton a3;
        RadioButton a4;


        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int pos) {
            ArrayListFragment f = new ArrayListFragment();
            String value = questions[pos];
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("question", value);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.content_quiz, container, false);
            questionTitle = (TextView) v.findViewById(R.id.currentquestion);
            a1 = (RadioButton) v.findViewById(R.id.ans1);
            a2 = (RadioButton) v.findViewById(R.id.ans2);
            a3 = (RadioButton) v.findViewById(R.id.ans3);
            a4 = (RadioButton) v.findViewById(R.id.ans4);

            String [] var = new String[0];
            text = getArguments() != null ? getArguments().getString("question") : "";
            if(text==null)
                text = "";
            if(!text.contentEquals("")){
                var = text.split("<");
                questionTitle.setText(var[0]);
                a1.setText(var[1]);
                a2.setText(var[2]);
                a3.setText(var[3]);
                a4.setText(var[4]);
            }
            return v;
        }

    }
    private class QuizTask extends AsyncTask<String,Double,String> {

        public QuizTask(){

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
                String request = "{\"class\":\""+classroom+"\",\"quiz\":\""+quiz+"\",\"new\":\""+"0"+"\",\"question\":\""+""+"\",\"answerA\":\""+""+"\",\"answerB\":\""+""+"\",\"answerC\":\""+""+"\",\"answerD\":\""+""+"\",\"correct\":\""+"0"+"\"}";
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
                Log.i("Value",request);
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
            questions = populate(responseString,">");
            NUM = questions.length;
            adapter = new MyAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(NUM);
            if(responseString.contentEquals(""))
                Log.i("Hope","Noooo");
        }
    }
    protected String[] populate(String parse,String split){
        String[] list = parse.split(split);
        for(int i = 0;i < list.length;i++){
            list[i] = StringEscapeUtils.unescapeHtml3(list[i]);
        }
        return list;
    }

}

package com.example.micha.cars;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class QuestionsBoard extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        button = (Button) findViewById(R.id.postbutton);
        setSupportActionBar(toolbar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuestionsBoard.this,PostQuestion.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

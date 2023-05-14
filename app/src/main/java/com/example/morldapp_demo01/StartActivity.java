package com.example.morldapp_demo01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.graphics.LinearGradient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.morldapp_demo01.camera.VideoRecordingActivity;

public class StartActivity extends AppCompatActivity {

    private Long startTime;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Thread mythread = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                    Intent intent = new Intent();
                    intent= new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        mythread.start();
    }
}
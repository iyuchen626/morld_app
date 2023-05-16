package com.example.morldapp_demo01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.graphics.LinearGradient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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

                    boolean cameraHasGone = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        cameraHasGone = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED;
                    }
                    boolean externalReadGone = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        externalReadGone = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED;
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String[] permissions;

                        permissions = new String[4];
                        permissions[0] = Manifest.permission.CAMERA;
                        permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        permissions[2] = Manifest.permission.READ_EXTERNAL_STORAGE;
                        permissions[3] = Manifest.permission.RECORD_AUDIO;

                        requestPermissions(permissions, 100);
                    }
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
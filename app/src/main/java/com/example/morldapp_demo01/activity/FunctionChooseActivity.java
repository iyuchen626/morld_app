package com.example.morldapp_demo01.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.camera.VideoRecordingActivity;

public class FunctionChooseActivity extends Base
{

    Button ShortVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_choose);
        ShortVideo=(Button) findViewById(R.id.button3);

        ShortVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent= new Intent(FunctionChooseActivity.this, VideoRecordingActivity.class);
                startActivity(intent);
            }
        });
    }
}
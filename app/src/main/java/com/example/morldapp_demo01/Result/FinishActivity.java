package com.example.morldapp_demo01.Result;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;

import com.example.morldapp_demo01.R;

import java.io.File;

public class FinishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        String filename = "pose_detect.txt";
        // 存放檔案位置在 內部空間/Download/
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(path, filename);
        file.delete();
    }
}
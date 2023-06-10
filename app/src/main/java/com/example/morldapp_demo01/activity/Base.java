package com.example.morldapp_demo01.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.impl.utils.EnqueueRunnable;

public class Base extends AppCompatActivity
{
    protected Handler handler=new Handler();
    double totalHttpDownloaded;
    double totalP2pDownloaded;
    double totalP2pUploaded;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
            }
        }, 50);
    }

    protected AppCompatActivity getActivity()
    {
        return this;
    }
}



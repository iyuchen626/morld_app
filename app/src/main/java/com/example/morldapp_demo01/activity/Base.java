package com.example.morldapp_demo01.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;


import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.pojo.User;

import java.util.LinkedList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;
import androidx.work.impl.utils.EnqueueRunnable;

public class Base extends AppCompatActivity
{
    protected Handler handler=new Handler();
    double totalHttpDownloaded;
    double totalP2pDownloaded;
    double totalP2pUploaded;
    protected LinkedList<String> queue;
    protected StringBuilder sb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        queue = new LinkedList<String>();
        sb = new StringBuilder();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        User user = user();
        if(user != null)
        {
            Config.mmToken = user.token;
        }
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

    protected User user()
    {
        String s = Tools.mmRead(getActivity(), Config.KEY_User);
        User u = Tools.getGson().fromJson(s, User.class);
        return u;
    }

    protected AppCompatActivity getActivity()
    {
        return this;
    }
}



package com.example.morldapp_demo01.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.kongzue.dialogx.DialogX;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Base extends AppCompatActivity
{
    double totalHttpDownloaded;
    double totalP2pDownloaded;
    double totalP2pUploaded;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);
        DialogX.init(this);
    }

    protected AppCompatActivity getActivity()
    {
        return this;
    }
}



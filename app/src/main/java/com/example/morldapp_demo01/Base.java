package com.example.morldapp_demo01;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.kongzue.dialogx.DialogX;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Base extends AppCompatActivity
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected AppCompatActivity getActivity()
    {
        return this;
    }
}



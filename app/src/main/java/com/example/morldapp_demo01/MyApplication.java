package com.example.morldapp_demo01;

import android.util.Log;


public class MyApplication extends android.app.Application
{
	private static MyApplication instance;
	public boolean is曾經開過 = false;

	public static MyApplication getInstance()
	{
		return instance;
	}

	@Override
	public void onCreate()
	{
		instance = this;
		super.onCreate();
		Log.v(Config.TAG,"1");
		Log.v(Config.TAG,"2");
	}
}
package com.example.morldapp_demo01;

import android.util.Log;

import com.p2pengine.core.p2p.P2pConfig;
import com.p2pengine.core.tracking.TrackerZone;
import com.p2pengine.sdk.P2pEngine;


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
		P2pConfig config = new P2pConfig.Builder()
				.trackerZone(TrackerZone.USA)
				.build();
		P2pEngine.init(this, "CU898SlVR", config);
	}
}
package com.example.morldapp_demo01;

import android.os.Bundle;
import android.view.View;

import com.example.morldapp_demo01.databinding.ActivityMainBinding;
import com.example.morldapp_demo01.fragmemt.HomeFragment;
import com.example.morldapp_demo01.fragmemt.VideoFragment;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity
{

	ActivityMainBinding binding;
	// @SuppressLint("NonConstantResourceId")
	private CastContext mCastContext;
	private Executor localExecutor = Executors.newSingleThreadExecutor();
	private CastSession mCastSession;
	private final SessionManagerListener<CastSession> mSessionManagerListener = new MySessionManagerListener();
	private CastStateListener mCastStateListener;

	private class MySessionManagerListener implements SessionManagerListener<CastSession>
	{

		@Override
		public void onSessionEnded(CastSession session, int error)
		{
			if (session == mCastSession)
			{
				mCastSession = null;
			}
			invalidateOptionsMenu();
		}

		@Override
		public void onSessionResumed(CastSession session, boolean wasSuspended)
		{
			mCastSession = session;
			invalidateOptionsMenu();
		}

		@Override
		public void onSessionStarted(CastSession session, String sessionId)
		{
			mCastSession = session;
			invalidateOptionsMenu();
		}

		@Override
		public void onSessionStarting(CastSession session)
		{
		}

		@Override
		public void onSessionStartFailed(CastSession session, int error)
		{
		}

		@Override
		public void onSessionEnding(CastSession session)
		{
		}

		@Override
		public void onSessionResuming(CastSession session, String sessionId)
		{
		}

		@Override
		public void onSessionResumeFailed(CastSession session, int error)
		{
		}

		@Override
		public void onSessionSuspended(CastSession session, int reason)
		{
		}
	}

	BottomNavigationView BtnNavViewMain;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mCastStateListener = new CastStateListener()
		{
			@Override
			public void onCastStateChanged(int newState)
			{
				if (newState != CastState.NO_DEVICES_AVAILABLE)
				{
					//	showIntroductoryOverlay();
				}
			}
		};

		CastContext.getSharedInstance(this, localExecutor).addOnCompleteListener(new OnCompleteListener<CastContext>()
		{
			@Override
			public void onComplete(@NonNull Task<CastContext> task)
			{
				mCastContext = task.getResult();
				mCastContext.addCastStateListener(mCastStateListener);
				mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
				if (mCastSession == null)
				{
					mCastSession = CastContext.getSharedInstance(MainActivity.this, localExecutor)
							.getResult()
							.getSessionManager()
							.getCurrentCastSession();
				}

				CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), binding.mediaRouteButton);

			}
		});
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		binding.imageView9.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{

			}
		});
		//setContentView(R.layout.activity_main);

		repleceFragment(new HomeFragment());
		BtnNavViewMain = (BottomNavigationView) findViewById(R.id.BtnNavView_Main);

		BtnNavViewMain.setOnItemSelectedListener(item ->
		{

			switch (item.getItemId())
			{

				case R.id.BtnNavView_Main_BtnHome:
					repleceFragment(new HomeFragment());
					break;

				case R.id.BtnNavView_Main_BtnEditor:
					repleceFragment(new VideoFragment());
					finish();
					break;

			}

			return true;
		});


//            binding.BtnNavViewMain.setOnItemSelectedListener(item -> {
//
//                switch (item.getItemId()){
//
//                    case R.id.BtnNavView_Main_BtnHome:
//                        repleceFragment(new HomeFragment());
//                        break;
//
//                    case R.id.BtnNavView_Main_BtnEditor:
//                        repleceFragment(new VideoFragment());
//                        break;
//
//                }
//
//                return true;
//            });
	}

	private void repleceFragment(Fragment fragment)
	{
		FragmentManager fragmentManager;
		FragmentTransaction fragmentTransaction;
		fragmentManager = getSupportFragmentManager();//抽象類，負責一系列對fragment的操作，getChildFragmentManager()可獲得子級fragment的引用，parent則是getParentFragmentManager()。
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.FrameLayout_Main, fragment);
		fragmentTransaction.commit();
	}


	@Override
	protected void onPause()
	{
		if (mCastContext != null)
		{
			mCastContext.removeCastStateListener(mCastStateListener);
			mCastContext.getSessionManager().removeSessionManagerListener(
					mSessionManagerListener, CastSession.class);
		}
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (mCastContext != null)
		{
			mCastContext.addCastStateListener(mCastStateListener);
			mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
			if (mCastSession == null)
			{
				mCastSession = CastContext.getSharedInstance(this, localExecutor)
						.getResult()
						.getSessionManager()
						.getCurrentCastSession();
			}
		}
	}
}
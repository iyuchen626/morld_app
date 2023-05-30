package com.example.morldapp_demo01.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.databinding.VideoLandscapeBinding;
import com.example.morldapp_demo01.pojo.FilmPOJO;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

public class VideoLandscape extends Base
{
	VideoLandscapeBinding binding;
	FilmPOJO data;
	private SimpleExoPlayer player;
	private boolean playWhenReady = true;
	private int currentWindow = 0;
	private long playbackPosition = 0L;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		binding = VideoLandscapeBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		data = (FilmPOJO) getIntent().getExtras().getSerializable("data");
	}

	void initializePlayer()
	{
		PlayerView playerView = binding.videoView;
		player = new SimpleExoPlayer.Builder(getActivity()).build();
		playerView.setPlayer(player);
		MediaItem mediaItem = MediaItem.fromUri(data.video_slug);
		player.setMediaItem(mediaItem);
		player.setPlayWhenReady(playWhenReady);
		player.seekTo(currentWindow, playbackPosition);
		player.prepare();
		View controlView = playerView.findViewById(R.id.exo_controller);
		ImageView fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
		fullscreenIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		if (Util.SDK_INT >= 24)
		{
			initializePlayer();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		hideSystemUi();
		if ((Util.SDK_INT < 24 || player == null))
		{
			initializePlayer();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (Util.SDK_INT < 24)
		{
			releasePlayer();
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		if (Util.SDK_INT >= 24)
		{
			releasePlayer();
		}
	}

	private void releasePlayer()
	{
		playbackPosition = player.getCurrentPosition();
		currentWindow = player.getCurrentWindowIndex();
		playWhenReady = player.getPlayWhenReady();
		player.release();
		player = null;
	}

	@SuppressLint("InlinedApi")
	private void hideSystemUi()
	{
		binding.videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

}



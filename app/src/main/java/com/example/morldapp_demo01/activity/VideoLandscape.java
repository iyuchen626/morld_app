package com.example.morldapp_demo01.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.databinding.VideoLandscapeBinding;
import com.example.morldapp_demo01.pojo.FilmPOJO;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.p2pengine.core.p2p.P2pStatisticsListener;
import com.p2pengine.core.p2p.PlayerInteractor;
import com.p2pengine.sdk.P2pEngine;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;

public class VideoLandscape extends Base
{
	VideoLandscapeBinding binding;
	FilmPOJO data;
	private SimpleExoPlayer player;
	private boolean playWhenReady = true;
	private int currentWindow = 0;
	private long playbackPosition = 0L;
	String MEDIA_TYPE;

	void initializeFile播放器(String url)
	{
		PlayerView playerView = binding.videoView;
		player = new SimpleExoPlayer.Builder(getActivity()).build();
		playerView.setPlayer(player);
		File ff  = getExternalFilesDir(null);
		ff = new File(ff, data.uuid);
		MediaItem mediaItem = MediaItem.fromUri(url);
		if(ff.exists())
		{
			mediaItem = MediaItem.fromUri(ff.getAbsolutePath());
		}
		player.setMediaItem(mediaItem);
		player.setPlayWhenReady(playWhenReady);
		player.seekTo(currentWindow, playbackPosition);
		player.prepare();
		View controlView = playerView.findViewById(R.id.exo_controller);
		ImageView fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
		fullscreenIcon.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	void initP2P播放器(String url)
	{
		PlayerView playerView = binding.videoView;
		player = new SimpleExoPlayer.Builder(getActivity()).build();

		P2pEngine.getInstance().setPlayerInteractor(new PlayerInteractor() {
			@Override
			public long onBufferedDuration()
			{
				if (player != null) {
					return player.getBufferedPosition() - player.getCurrentPosition();
				}
				return -1;
			}
		});

		P2pEngine.getInstance().addP2pStatisticsListener(new P2pStatisticsListener() {
			@Override
			public void onHttpDownloaded(int i)
			{
				totalHttpDownloaded += i;
				refreshRatio();
			}

			@Override
			public void onP2pDownloaded(int i, int i1)
			{
				totalP2pDownloaded+=i;
				refreshRatio();
			}

			@Override
			public void onP2pUploaded(int i, int i1)
			{
				totalP2pUploaded+=i;
			}

			@Override
			public void onPeers(@NonNull List<String> list)
			{

			}

			@Override
			public void onServerConnected(boolean b)
			{

			}
		});

		if (player != null) {
			player.stop();
		}

		String parsedUrl = P2pEngine.getInstance().parseStreamUrl(url);
		LoadControl loadControl = new DefaultLoadControl.Builder()
				.setAllocator(new DefaultAllocator(true,16))
				.setBufferDurationsMs(
						5000,
						60000,
						5000,
						5000
				)
				.setTargetBufferBytes(-1)
				.setPrioritizeTimeOverSizeThresholds(true)
				.build();
		DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
//		Uri uri = Uri.parse(url);
		MediaSource mediaSource =  new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(parsedUrl)));
		playerView.setPlayer(player);
		player.setMediaSource(mediaSource);
		player.setPlayWhenReady(playWhenReady);
		player.seekTo(currentWindow, playbackPosition);
		player.prepare();
		View controlView = playerView.findViewById(R.id.exo_controller);
		ImageView fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
		fullscreenIcon.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		MEDIA_TYPE = getIntent().getExtras().getString("MEDIA_TYPE");
		binding = VideoLandscapeBinding.inflate(getLayoutInflater());
		binding.imageRetry.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				playbackPosition = 0;
				player.seekTo(currentWindow, playbackPosition);
				player.play();
			}
		});
		binding.imageDownload.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				File ff  = getExternalFilesDir(null);
				ff = new File(ff, data.uuid);
				if(ff.exists()) ff.delete();
//				File[] ffs = getExternalFilesDir(Environment.DIRECTORY_MOVIES).listFiles();
//				for(File f : ffs)
//				{
//					Log.i(Config.TAG, f.getName());
//				}
				DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				Uri uri = Uri.parse(data.video_slug);
				DownloadManager.Request request = new DownloadManager.Request(uri);
				request.setTitle("下載影片");
				request.setDescription(data.title);
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				request.setVisibleInDownloadsUi(false);
				request.setDestinationInExternalFilesDir(getApplicationContext(),  Environment.DIRECTORY_MOVIES, data.uuid);
				downloadmanager.enqueue(request);
			}
		});
		setContentView(binding.getRoot());
		data = (FilmPOJO) getIntent().getExtras().getSerializable("data");
	}

	void initializePlayer()
	{
		if(MEDIA_TYPE.equals("file")) initializeFile播放器(data.video_slug);
		if(MEDIA_TYPE.equals("stream")) initP2P播放器(data.video_hls_slug);
//		PlayerView playerView = binding.videoView;
//		player = new SimpleExoPlayer.Builder(getActivity()).build();
//		playerView.setPlayer(player);
//		File ff  = getExternalFilesDir(null);
//		ff = new File(ff, data.uuid);
//		MediaItem mediaItem = MediaItem.fromUri(data.video_slug);
//		if(ff.exists())
//		{
//			mediaItem = MediaItem.fromUri(ff.getAbsolutePath());
//		}
//		player.setMediaItem(mediaItem);
//		player.setPlayWhenReady(playWhenReady);
//		player.seekTo(currentWindow, playbackPosition);
//		player.prepare();
//		View controlView = playerView.findViewById(R.id.exo_controller);
//		ImageView fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
//		fullscreenIcon.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				finish();
//			}
//		});
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

	@Override
	public void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		P2pEngine.getInstance().stopP2p();
	}

	private void refreshRatio() {
		double ratio = 0.0;
		if (totalHttpDownloaded + totalP2pDownloaded != 0.0) {
			ratio = totalP2pDownloaded / (totalHttpDownloaded + totalP2pDownloaded);
		}
		String s = String.format("P2P Ratio: %.0f%%", ratio * 100);
		binding.debugRatio.setText(s);
	}
}



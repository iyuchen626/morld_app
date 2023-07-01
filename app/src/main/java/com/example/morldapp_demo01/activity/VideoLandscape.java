package com.example.morldapp_demo01.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import com.example.morldapp_demo01.CameraXViewModel;
import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.Edit.AnalyzePoseGraphic;
import com.example.morldapp_demo01.Edit.CalculateScore;
import com.example.morldapp_demo01.Edit.FileMangement;
import com.example.morldapp_demo01.Edit.StructureAnalyze;
import com.example.morldapp_demo01.Edit.structurepoint;
import com.example.morldapp_demo01.PreferenceUtils;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.classification.posedetector.PoseDetectorProcessor;
import com.example.morldapp_demo01.databinding.VideoLandscapeBinding;
import com.example.morldapp_demo01.pojo.FilmPOJO;
import com.example.morldapp_demo01.pojo.TxtConfigPOJO;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.p2pengine.core.p2p.P2pStatisticsListener;
import com.p2pengine.core.p2p.PlayerInteractor;
import com.p2pengine.sdk.P2pEngine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;
import static com.example.morldapp_demo01.Edit.CalculateScore.getScoreResult;

public class VideoLandscape extends Base
{
	VideoLandscapeBinding binding;
	FilmPOJO data;
	private SimpleExoPlayer player;
	private boolean playWhenReady = true;
	private int currentWindow = 0;
	private long playbackPosition = 0L;
	String MEDIA_TYPE;
	private HashMap<String,  structurepoint[]> posestructurepoint=new HashMap<>();
	private ExecutorService executorService= Executors.newSingleThreadExecutor();
	float height,width;
	private PoseDetectorProcessor imageProcessor;
	private ImageAnalysis analysisUseCase;
	private ProcessCameraProvider cameraProvider;
	boolean isRunCalcScore = false;

	void initializeFile播放器(String url)
	{
		PlayerView playerView = binding.videoView;
		player = new SimpleExoPlayer.Builder(getActivity()).build();
		player.addListener(new Player.Listener() {
			@Override
			public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
				if (playWhenReady && playbackState == Player.STATE_ENDED) {
					binding.imageRetry.setVisibility(View.VISIBLE);
					if(isRunCalcScore) imageProcessor.stop();
				}
			}
		});
		playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
			@Override
			public void onVisibilityChange(int visibility) {
				if(isRunCalcScore) return;
				if(visibility == View.VISIBLE) {
					mm顯示控制項(true);
					binding.videoStructure.setVisibility(View.INVISIBLE);
					//player.setPlayWhenReady(playWhenReady);

				}
				else {
					mm顯示控制項(false);
					binding.videoStructure.setVisibility(View.VISIBLE);
					player.setPlayWhenReady(playWhenReady);
				}
			}
		});
		playerView.setPlayer(player);
		File ff  = getExternalFilesDir("videos");
		ff = new File(ff, data.uuid);
		MediaItem mediaItem = MediaItem.fromUri(url);
		if(ff.exists())
		{
			mediaItem = MediaItem.fromUri(ff.getAbsolutePath());
		}
		else
		{
			player.prepare();
		}
		player.setMediaItem(mediaItem);
		player.setPlayWhenReady(playWhenReady);
		playerView.hideController();
		player.seekTo(currentWindow, playbackPosition);
		View controlView = playerView.findViewById(R.id.exo_controller);
		ImageView fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
		ImageView pausetostart = controlView.findViewById(R.id.exo_play);
		fullscreenIcon.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		pausetostart.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				player.setPlayWhenReady(playWhenReady);
				//Tools.toast(getActivity(), "HIDE");
				//playerView.setVisibility(View.INVISIBLE);
				binding.videoStructure.setVisibility(View.VISIBLE);
				playerView.hideController();
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
		handler.postDelayed(myrunnable, 50);
		binding.imageRetry.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(isRunCalcScore)
				{
					bindAllCameraUseCases(cameraProvider);
				}
				playbackPosition = 0;
				player.seekTo(currentWindow, playbackPosition);
				player.play();
			}
		});
		setContentView(binding.getRoot());
		findViewById(R.id.des).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				mm遞減骨骼();
			}
		});
		findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				mm遞增骨骼();
			}
		});
		binding.imageClock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if(cameraProvider == null) {
					Tools.showError(getActivity(), "你無法運算骨骼");
					return;
				}
				if(isRunCalcScore)
				{
					isRunCalcScore = false;
					imageProcessor.stop();
					cameraProvider.unbindAll();
					binding.recordStructure.clear();
					binding.textScore.setVisibility(View.INVISIBLE);
				}
				else
				{
					isRunCalcScore = true;
					bindAllCameraUseCases(cameraProvider);
					mm顯示控制項(true);
					//binding.videoView.setVisibility(View.INVISIBLE);
					binding.textScore.setVisibility(View.VISIBLE);
				}
			}
		});
		data = (FilmPOJO) getIntent().getExtras().getSerializable("data");
		File ff  = getExternalFilesDir("videos");
		ff = new File(ff, data.uuid);
		if(ff.exists())
		{
			binding.imageDownload.setVisibility(View.GONE);
			FileMangement.ReadFileFromTxt(getActivity(), data.txt_slug, (long) (data.video_offset * 1000 * 1000), new FileMangement.OnReadFileFromTxtListener() {
				@Override
				public void onTxt(HashMap<String, structurepoint[]> s)
				{
					TxtConfigPOJO txt = Tools.getGson().fromJson(Tools.mmRead(getActivity(), Config.KEY_TXT_CONFIG), TxtConfigPOJO.class);
					width = txt.width;
					height = txt.height;
					//Tools.toast(getActivity(), "width:"+width+"height"+height);
					if(height>width)
					{
						//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					}
					else
					{
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					}
					binding.editOffsetLayout.setVisibility(View.VISIBLE);
					posestructurepoint = s;
					player.prepare();
				}
			});
		}
		new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
				.get(CameraXViewModel.class)
				.getProcessCameraProvider()
				.observe(
						this,
						provider ->
						{
							cameraProvider = provider;
						});
	}

	void mm下載影片()
	{
		Tools.showQuestion(getActivity(), "訊息", "確認下載影片? (下載後才可提供骨骼播放功能)", "是", "否", new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				File ff  = getExternalFilesDir("videos");
				ff = new File(ff, data.uuid);
				if(ff.exists())
				{

				}
				else
				{
					Tools.mmSave(getApplicationContext(), "videos/"+data.uuid, Tools.getGson().toJson(data, FilmPOJO.class));
					DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
					Uri uri = Uri.parse(data.video_slug);
					DownloadManager.Request request = new DownloadManager.Request(uri);
					request.setTitle("下載影片");
					request.setDescription(data.title);
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					request.setVisibleInDownloadsUi(false);
					request.setDestinationInExternalFilesDir(getApplicationContext(), "videos", data.uuid);
					downloadmanager.enqueue(request);
					binding.imageDownload.setVisibility(View.GONE);
				}
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{

			}
		});
	}

	void mm顯示控制項(boolean isShow)
	{
		if(isShow)
		{
			player.setPlayWhenReady(false);
			binding.imageClock.setVisibility(View.VISIBLE);
			binding.imageUser.setVisibility(View.VISIBLE);
			binding.imageRetry.setVisibility(View.VISIBLE);
			binding.imageDownload.setVisibility(View.VISIBLE);
			binding.title.setVisibility(View.VISIBLE);
			binding.editOffsetLayout.setVisibility(View.VISIBLE);
			File ff  = getExternalFilesDir("videos");
			ff = new File(ff, data.uuid);
			if(ff.exists())
			{
				binding.imageDownload.setImageResource(R.drawable.icon_setting);
				binding.imageDownload.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						binding.editOffsetLayout.setVisibility(View.VISIBLE);
					}
				});
			}
			else
			{
				binding.imageDownload.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						mm下載影片();
					}
				});
			}
			if(player.getCurrentPosition() < player.getDuration())
			{
				binding.imageRetry.setVisibility(View.GONE);
			}
		}
		else
		{
			binding.imageClock.setVisibility(View.GONE);
			binding.imageUser.setVisibility(View.GONE);
			binding.imageRetry.setVisibility(View.GONE);
			binding.imageDownload.setVisibility(View.GONE);
			binding.title.setVisibility(View.GONE);
			binding.editOffsetLayout.setVisibility(View.GONE);
			binding.editOffsetLayout.setVisibility(View.GONE);
		}
	}

	void mm遞減骨骼()
	{
		data.video_offset -= 0.05f;
		Tools.toast(getActivity(), "減少偏差值至:"+data.video_offset);
		FileMangement.ReadFileFromTxt(getActivity(), data.txt_slug, (long) (data.video_offset * 1000 * 1000), new FileMangement.OnReadFileFromTxtListener() {
			@Override
			public void onTxt(HashMap<String, structurepoint[]> s)
			{
				binding.editOffsetLayout.setVisibility(View.VISIBLE);
				posestructurepoint = s;
			}
		});
	}

	void mm遞增骨骼()
	{
		data.video_offset += 0.05f;
		Tools.toast(getActivity(), "減少偏差值至:"+data.video_offset);
		FileMangement.ReadFileFromTxt(getActivity(), data.txt_slug, (long) (data.video_offset * 1000 * 1000), new FileMangement.OnReadFileFromTxtListener() {
			@Override
			public void onTxt(HashMap<String, structurepoint[]> s)
			{
				binding.editOffsetLayout.setVisibility(View.VISIBLE);
				posestructurepoint = s;
			}
		});
	}

	private Runnable myrunnable = new Runnable()
	{
		@Override
		public void run()
		{
			float fps = 30; //後端也要存fps，或著存到txt表頭去
			long delay = (long) ((1.0 / fps) * 1000);
			if (!binding.videoView.getPlayer().isPlaying())
			{
				handler.postDelayed(myrunnable, delay); return;
			}
			long currentTimeMicrosecond = (binding.videoView.getPlayer().getCurrentPosition() * 1000);
			String wantId = Tools.mm取得對應時間軸之key(posestructurepoint, currentTimeMicrosecond);
			if (!wantId.equals(""))
			{
				Log.i(Config.TAG, "wantID="+wantId +" currentTimeMicrosecond="+currentTimeMicrosecond);
				binding.videoStructure.clear();
				structurepoint[] structurepoints = posestructurepoint.get(wantId);
				binding.videoStructure.add(new AnalyzePoseGraphic(binding.videoStructure, structurepoints,height,width));
			}
			handler.postDelayed(myrunnable, delay / 2);
		}
	};

	void initializePlayer()
	{
		if(MEDIA_TYPE.equals("file")) initializeFile播放器(data.video_slug);
		if(MEDIA_TYPE.equals("stream")) initP2P播放器(data.video_hls_slug);
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
		if (imageProcessor != null && cameraProvider != null)
		{
			bindAllCameraUseCases(cameraProvider);
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
		if (imageProcessor != null)
		{
			imageProcessor.stop();
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

	private void bindAllCameraUseCases(ProcessCameraProvider cameraProvider)
	{
		cameraProvider.unbindAll();
		Size highSize = new Size(3000, 4000);
		Preview.Builder builder = new Preview.Builder();
		builder.setTargetResolution(highSize);
		builder.setDefaultResolution(highSize);
		builder.setMaxResolution(highSize);
		Preview previewUseCase = builder.build();
		previewUseCase.setSurfaceProvider(binding.PreViewEditor.getSurfaceProvider());
		CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
		Camera camera = cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, previewUseCase);
		try
		{
			Size size = Tools.mm取得相機支援最小的4_3解析度(getActivity(), camera);
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			int screen_h = displayMetrics.heightPixels;
			int screen_w = displayMetrics.widthPixels;
			binding.recordStructure.scaleFactor = (float) screen_w / (float) size.getWidth();
			Log.i(Config.TAG, "screen w=" + screen_w + " screen h=" + screen_h);
			cameraProvider.unbind(previewUseCase);
			if (imageProcessor != null)
			{
				imageProcessor.stop();
			}
			PoseDetectorOptionsBase poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
			boolean shouldShowInFrameLikelihood = PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
			boolean visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
			boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
			boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this);
			imageProcessor = new PoseDetectorProcessor(this, poseDetectorOptions, shouldShowInFrameLikelihood, visualizeZ, rescaleZ, runClassification, true);
			ImageAnalysis.Builder imageAnalyBuilder = new ImageAnalysis.Builder();
			imageAnalyBuilder.setTargetResolution(size);
			imageAnalyBuilder.setDefaultResolution(size);
			imageAnalyBuilder.setMaxResolution(size);
			imageAnalyBuilder.setOutputImageRotationEnabled(true);
			imageAnalyBuilder.setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST);
			analysisUseCase = imageAnalyBuilder.build();
			analysisUseCase.setAnalyzer(
					ContextCompat.getMainExecutor(this),
					imageProxy ->
					{
						try
						{
							Log.i(Config.TAG, "imageProxy w="+imageProxy.getWidth()+" h="+imageProxy.getHeight());
							binding.recordStructure.setImageSourceInfo(imageProxy.getWidth(), imageProxy.getHeight(), false);
							imageProcessor.processImageProxy(imageProxy, binding.recordStructure);

							InputImage inputImage = InputImage.fromMediaImage(imageProxy.getImage(), 0);
							StructureAnalyze.Analyze_Structure(inputImage, new StructureAnalyze.OnAnalyzeStructureListener()
							{
								@Override
								public void onDone(String result)
								{
									if(result.equals("")) return;
									structurepoint[] structurepoints = FileMangement.ReadFronOneLine(result);
									new CalculateScore(true, structurepoints);
									binding.textScore.setText(""+getScoreResult());
								}
							});
						}
						catch (Exception e)
						{
							e.printStackTrace();
							Tools.toast(getActivity(), e.getMessage());
						}
					});
			cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
		}
		catch (Exception e)
		{
			Tools.showError(getActivity(), e.getMessage());
		}
	}
}



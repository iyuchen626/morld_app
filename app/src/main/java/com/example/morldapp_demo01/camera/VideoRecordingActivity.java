package com.example.morldapp_demo01.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.morldapp_demo01.CameraXViewModel;
import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.Edit.AnalyzePoseGraphic;
import com.example.morldapp_demo01.Edit.FileMangement;
import com.example.morldapp_demo01.Edit.ShowVideoStructureActivity;
import com.example.morldapp_demo01.Edit.StructureAnalyze;
import com.example.morldapp_demo01.Edit.structurepoint;
import com.example.morldapp_demo01.PreferenceUtils;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.VisionImageProcessor;
import com.example.morldapp_demo01.activity.Base;
import com.example.morldapp_demo01.adapter.ControlMenuAdapter;
import com.example.morldapp_demo01.adapter.SnapPagerScrollListener;
import com.example.morldapp_demo01.classification.posedetector.PoseDetectorProcessor;
import com.example.morldapp_demo01.fastextraction.URIPathHelper;
import com.example.morldapp_demo01.fastextraction.Utils;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executor;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Recorder;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;

public class VideoRecordingActivity extends Base
{

	private PreviewView PreView;
	private com.example.morldapp_demo01.GraphicOverlay GraphicOverlay;
	private Boolean A;
	private Boolean RecordingVideo = false;

	private ImageButton ImgBtnPhoneVideoGallery;
	private Uri videoUri = null;
	long starttime = 0, currenttime = 0, diff = 0, minutes, second;
	@Nullable
	private Preview previewUseCase;
	ProcessCameraProvider cameraProvider;
	@Nullable
	private ImageAnalysis analysisUseCase;
	@Nullable
	private VisionImageProcessor imageProcessor;
	@Nullable
	private VideoCapture videoCaptureUseCase;
	private boolean needUpdateGraphicOverlayImageSourceInfo;
	Recorder recorder;
	private int lensFacing = CameraSelector.LENS_FACING_BACK;
	private static final String POSE_DETECTION = "Pose Detection";

	private String selectedModel = POSE_DETECTION;
	private CameraSelector cameraSelector;

	private ImageView Act_TogBtnCameraFacing;
	private ImageView Act_ImgBtnCameraRecording;
	private ImageView Act_ImgBtnAlbumChoose;
	private ProgressBar Act_ProgressBarCameraRecording;
	private TextView Act_TextView_RecordingTime;
	private ImageCapture imageCapture;
	private ImageView imageCoach;
	private com.example.morldapp_demo01.GraphicOverlay GraphicOverlay_coach;
	private VerticalSeekBar seekBar1;
	int currentIndex = 0;

	// private TextView TextViewPKTime;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
		setContentView(R.layout.activity_video_recording);
		seekBar1 = findViewById(R.id.seekBar1);
		PreView = findViewById(R.id.PreView_Editor);
		GraphicOverlay = findViewById(R.id.GraphicOverlay_Record);
		GraphicOverlay_coach = findViewById(R.id.GraphicOverlay_coach);
		Act_TogBtnCameraFacing = findViewById(R.id.Layout_TogBtnCameraFacing);
		imageCoach = findViewById(R.id.imageCoach);
		Act_ImgBtnCameraRecording = findViewById(R.id.Layout_ImgBtnCameraRecording);
		Act_ImgBtnAlbumChoose = findViewById(R.id.Layout_ImgBtnAlbumChoose);
		Act_TextView_RecordingTime = findViewById(R.id.Layout_TextView_RecordingTime);
		Act_ProgressBarCameraRecording = findViewById(R.id.Layout_ProgressBarCameraRecording);
		Act_ProgressBarCameraRecording.setVisibility(View.INVISIBLE);

		new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
				.get(CameraXViewModel.class)
				.getProcessCameraProvider()
				.observe(
						this,
						provider ->
						{
							cameraProvider = provider;
							bindAllCameraUseCases(provider);
						});

		RecyclerView controlMenu = findViewById(R.id.recycle_view);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		PagerSnapHelper snapHelper = new PagerSnapHelper();
		controlMenu.setLayoutManager(layoutManager);
		snapHelper.attachToRecyclerView(controlMenu);
		ControlMenuAdapter adapter = new ControlMenuAdapter(getActivity(), Arrays.asList(new String[]{"Photo", "Video", "Model"}));
		controlMenu.setAdapter(adapter);
		controlMenu.addOnScrollListener(new SnapPagerScrollListener(snapHelper, SnapPagerScrollListener.ON_SCROLL, false, new SnapPagerScrollListener.OnChangeListener()
		{
			@Override
			public void onSnapped(int position)
			{
				currentIndex = position;
				Log.i(Config.TAG, "選到" + position);
				mm初始化按鈕事件(position);
			}
		}));
		mm拍照();
		int step = 1;
		int max = 100;
		int min = 0;
		seekBar1.setMax((max - min) / step);
		seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				double value = min + (progress * step);
				Log.i(Config.TAG, "onProgressChanged=" + value);
				imageCoach.setAlpha((float) value * 0.01f);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{

			}
		});
		mm初始化按鈕事件(0);
	}

	private void bindAllCameraUseCases(ProcessCameraProvider cameraProvider)
	{
		cameraProvider.unbindAll();
		Size highSize = new Size(3000, 4000);
		Preview.Builder builder = new Preview.Builder();
		builder.setTargetResolution(highSize);
		builder.setDefaultResolution(highSize);
		builder.setMaxResolution(highSize);
		previewUseCase = builder.build();
		previewUseCase.setSurfaceProvider(PreView.getSurfaceProvider());
		Camera camera = cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, previewUseCase);
		try
		{
			Size size = Tools.mm取得相機支援最小的4_3解析度(getActivity(), camera);
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			int screen_h = displayMetrics.heightPixels;
			int screen_w = displayMetrics.widthPixels;
			GraphicOverlay.scaleFactor = (float) screen_w / (float) size.getWidth();
			Log.i(Config.TAG, "screen w=" + screen_w + " screen h=" + screen_h);
			cameraProvider.unbind(previewUseCase);
			imageCapture = new ImageCapture.Builder().setTargetResolution(highSize).setDefaultResolution(highSize).setMaxResolution(highSize).build();
			videoCaptureUseCase = new VideoCapture.Builder().setTargetResolution(highSize).setDefaultResolution(highSize).setMaxResolution(highSize).setVideoFrameRate(60).build();
			if (imageProcessor != null)
			{
				imageProcessor.stop();
			}
			PoseDetectorOptionsBase poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
			boolean shouldShowInFrameLikelihood = PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
			boolean visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
			boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
			boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this);
			imageProcessor = new PoseDetectorProcessor(this, poseDetectorOptions, false, visualizeZ, rescaleZ, runClassification, true);
			ImageAnalysis.Builder imageAnalyBuilder = new ImageAnalysis.Builder();
			imageAnalyBuilder.setTargetResolution(size);
			imageAnalyBuilder.setDefaultResolution(size);
			imageAnalyBuilder.setMaxResolution(size);
			imageAnalyBuilder.setOutputImageRotationEnabled(true);
			imageAnalyBuilder.setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST);
			analysisUseCase = imageAnalyBuilder.build();
			if(currentIndex != 2)
			analysisUseCase.setAnalyzer(
					ContextCompat.getMainExecutor(this),
					imageProxy ->
					{
						try
						{
							float df = (float) Math.max(imageProxy.getWidth(),imageProxy.getHeight()) / (float) Math.min(imageProxy.getWidth(),imageProxy.getHeight());
							df = (float) (Math.floor(df * 100.0) / 100.0);
							if(df != 1.33f) throw new Exception("骨骼運算，無法鎖定在4:3");
							GraphicOverlay.setImageSourceInfo(imageProxy.getWidth(), imageProxy.getHeight(), false);
							imageProcessor.processImageProxy(imageProxy, GraphicOverlay);
						}
						catch (Exception e)
						{
							imageProcessor.stop();
							e.printStackTrace();
							Tools.toast(getActivity(), e.getMessage());
						}
					});
			if(currentIndex == 2) cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase, imageCapture, videoCaptureUseCase);
			else cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase, imageCapture, videoCaptureUseCase);
		}
		catch (Exception e)
		{
			Tools.showError(getActivity(), e.getMessage());
		}
	}

	void mm初始化按鈕事件(int pos)
	{
		if (pos == 0)
		{
			GraphicOverlay_coach.clear();
			imageCoach.setVisibility(View.INVISIBLE);
			seekBar1.setVisibility(View.INVISIBLE);
			Act_ImgBtnCameraRecording.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mm拍照();
				}
			});
			Act_ImgBtnAlbumChoose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					pickvideogallery();
				}
			});
		}
		if (pos == 1)
		{
			GraphicOverlay_coach.clear();
			imageCoach.setVisibility(View.INVISIBLE);
			seekBar1.setVisibility(View.INVISIBLE);
			Act_ImgBtnCameraRecording.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (RecordingVideo)
					{
						RecordingVideo = false;
						videoCaptureUseCase.stopRecording();
					}
					else
					{
						RecordingVideo = true;
						RecordVideo();
					}
				}
			});
			Act_ImgBtnAlbumChoose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					pickvideogallery();
				}
			});
			GraphicOverlay.setVisibility(View.VISIBLE);
			bindAllCameraUseCases(cameraProvider);
		}
		if (pos == 2)
		{
			imageCoach.setImageResource(0);
			imageCoach.setVisibility(View.VISIBLE);
			seekBar1.setVisibility(View.VISIBLE);
			Act_ImgBtnAlbumChoose.setVisibility(View.VISIBLE);
			Act_ImgBtnCameraRecording.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					mm拍照();
				}
			});
			Act_ImgBtnAlbumChoose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					pickimagegallery();
				}
			});
			GraphicOverlay.setVisibility(View.GONE);
			GraphicOverlay.clear();
			bindAllCameraUseCases(cameraProvider);
		}
		Act_TogBtnCameraFacing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				lensFacing = lensFacing == CameraSelector.LENS_FACING_FRONT ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT;
				cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
				try
				{
					if (cameraProvider.hasCamera(cameraSelector))
					{
						bindAllCameraUseCases(cameraProvider);
					}
				}
				catch (Exception e)
				{
					Tools.showError(getActivity(), e.getMessage());
				}
			}
		});
	}

	private void mm拍照()
	{
		// 确保imageCapture 已经被实例化, 否则程序将可能崩溃
		if (imageCapture != null)
		{
			Act_ImgBtnCameraRecording.setVisibility(View.INVISIBLE);
			Act_ProgressBarCameraRecording.setVisibility(View.VISIBLE);
			String name = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TRADITIONAL_CHINESE).format(System.currentTimeMillis());
			ContentValues contentValues = new ContentValues();
			contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
			contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
			{
				contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Morld");
			}

			ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build();

			imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback()
			{
				@Override
				public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults)
				{
                    Tools.toastSuccess(getActivity(), "已儲存照片");
					Act_ImgBtnCameraRecording.setVisibility(View.VISIBLE);
					Act_ProgressBarCameraRecording.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onError(@NonNull ImageCaptureException exception)
				{
					Log.e(Config.TAG, "Photo capture failed: " + exception.getMessage());
					Act_ImgBtnCameraRecording.setVisibility(View.VISIBLE);
					Act_ProgressBarCameraRecording.setVisibility(View.INVISIBLE);
					Tools.showError(getActivity(), "拍照失敗:" + exception.getMessage());
				}
			});
		}
	}

	@SuppressLint("RestrictedApi")
	private void RecordVideo()
	{
		if (videoCaptureUseCase == null) return;
		long timestamp = System.currentTimeMillis();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
		contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
		{
			contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Morld");
		}
		starttime = SystemClock.elapsedRealtime();
		try
		{
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
			{
				return;
			}
			videoCaptureUseCase.startRecording(
					new VideoCapture.OutputFileOptions.Builder(
							getContentResolver(),
							MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
							contentValues
					).build(),
					getExecutor(),
					new VideoCapture.OnVideoSavedCallback()
					{
						@Override
						public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults)
						{
							Act_ImgBtnCameraRecording.setImageResource(R.drawable.take_pic);
							Act_ProgressBarCameraRecording.setVisibility(View.INVISIBLE);
							Log.i(Config.TAG, "影片儲存成功:" + outputFileResults.getSavedUri().getPath());
                            Tools.toastSuccess(getActivity(), "已儲存影片");
						}

						@Override
						public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause)
						{
							Toast.makeText(getApplicationContext(), "Error saving video: " + message, Toast.LENGTH_SHORT).show();
						}
					}
			);
            Act_ImgBtnCameraRecording.setImageResource(R.drawable.take_video);
            Act_ProgressBarCameraRecording.setVisibility(View.VISIBLE);
			RecordingVideo = true;
		}
		catch (Exception e)
		{
			Tools.showError(getActivity(), e.getMessage());
		}
	}

	Executor getExecutor()
	{
		return ContextCompat.getMainExecutor(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (imageProcessor != null)
		{
			imageProcessor.stop();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (imageProcessor != null && cameraProvider != null)
		{
			bindAllCameraUseCases(cameraProvider);
		}
	}

	private void pickimagegallery()
	{
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //極重要，用android 11以上，用ACTION_PICK必閃退
		intent.setType("image/*");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		galleryActivityResultLauncher.launch(intent);
	}

	private void pickvideogallery()
	{
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //極重要，用android 11以上，用ACTION_PICK必閃退
		intent.setType("video/*");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		videoActivityResultLauncher.launch(intent);
	}

	private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>()
			{
				@SuppressLint("WrongConstant")
				@Override
				public void onActivityResult(ActivityResult result)
				{
					if (result.getResultCode() == Activity.RESULT_OK)
					{
						try
						{
							Uri viseoUri = result.getData().getData();
							Log.i(Config.TAG, "選擇路徑="+viseoUri.getPath());
							imageCoach.setImageURI(viseoUri);

							InputImage inputImage = InputImage.fromFilePath(getActivity(), viseoUri);
							StructureAnalyze.Analyze_Structure(inputImage, new StructureAnalyze.OnAnalyzeStructureListener()
							{
								@Override
								public void onDone(String result)
								{
									if(result.equals(""))
									{
										Tools.showError(getActivity(), "未偵測到骨骼資訊");
										return;
									}
									structurepoint[] structurepoints = FileMangement.ReadFronOneLine(result);
									GraphicOverlay_coach.clear();
									GraphicOverlay_coach.add(new AnalyzePoseGraphic(GraphicOverlay_coach, structurepoints, inputImage.getHeight(), inputImage.getWidth()));
								}
							});
						}
						catch (Exception e)
						{
							Tools.showError(getActivity(), e.getMessage());
						}
					}
				}
			}
	);

	private ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>()
			{
				@SuppressLint("WrongConstant")
				@Override
				public void onActivityResult(ActivityResult result)
				{
					if (result.getResultCode() == Activity.RESULT_OK)
					{
						Uri viseoUri = null;
						Intent data = result.getData();
						viseoUri = data.getData();

						try
						{
							URIPathHelper uriPathHelper = new URIPathHelper();
							String videoInputPath = uriPathHelper.getPath(getActivity(), viseoUri).toString();
							File src = new File(videoInputPath);
							FileInputStream inStream = new FileInputStream(videoInputPath);
							File path = getExternalFilesDir(null);
							File file = new File(path, src.getName());
							FileOutputStream outStream = new FileOutputStream(file.getAbsolutePath());
							FileChannel inChannel = inStream.getChannel();
							FileChannel outChannel = outStream.getChannel();
							inChannel.transferTo(0, inChannel.size(), outChannel);
							inStream.close();
							outStream.close();

							Intent intent = new Intent(VideoRecordingActivity.this, ShowVideoStructureActivity.class);
							Bundle objbundle = new Bundle();
							objbundle.putString("urivideostr", videoInputPath);
							intent.putExtras(objbundle);
							startActivity(intent);
						}
						catch (Exception e)
						{
							Tools.showError(getActivity(), e.getMessage());
						}

					}
					else
					{

					}
				}
			}
	);

}
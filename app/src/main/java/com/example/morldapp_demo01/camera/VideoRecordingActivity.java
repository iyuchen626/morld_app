package com.example.morldapp_demo01.camera;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.morldapp_demo01.CameraXViewModel;
import com.example.morldapp_demo01.PreferenceUtils;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.VisionImageProcessor;
import com.example.morldapp_demo01.classification.posedetector.PoseDetectorProcessor;
import com.example.morldapp_demo01.classification.posedetector.PosePKResult;
import com.example.morldapp_demo01.video.video_editor_time;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import java.util.concurrent.Executor;

public class VideoRecordingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private PreviewView PreView;
    private com.example.morldapp_demo01.GraphicOverlay GraphicOverlay;
    private Boolean A;
    private Boolean RecordingVideo=false;

    private ImageButton ImgBtnPhoneVideoGallery;
    private Uri videoUri =null;

    @Nullable
    private ProcessCameraProvider cameraProvider;
    @Nullable
    private Preview previewUseCase;
    @Nullable
    private ImageAnalysis analysisUseCase;
    @Nullable
    private VisionImageProcessor imageProcessor;
    @Nullable
    private VideoCapture videoCaptureUseCase;
    private boolean needUpdateGraphicOverlayImageSourceInfo;

    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private static final String POSE_DETECTION = "Pose Detection";

    private String selectedModel = POSE_DETECTION;
    private CameraSelector cameraSelector;

    private ToggleButton Act_TogBtnCameraFacing;
    private ImageButton Act_ImgBtnCameraRecording;
    private ProgressBar Act_ProgressBarCameraRecording;

    private float intPKTime = 0;
    private float PKTime = 0;
   // private TextView TextViewPKTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        setContentView(R.layout.activity_video_recording);
        PreView = findViewById(R.id.PreView_Editor);
        GraphicOverlay = findViewById(R.id.GraphicOverlay_Record);
       // TextViewPKTime=findViewById(R.id.TextView_PKTime);


        Act_TogBtnCameraFacing = findViewById(R.id.Layout_TogBtnCameraFacing);
        Act_TogBtnCameraFacing.setOnCheckedChangeListener(this);

        Act_ImgBtnCameraRecording = findViewById(R.id.Layout_ImgBtnCameraRecording);
        Act_ImgBtnCameraRecording.setOnClickListener(this);

        Act_ProgressBarCameraRecording = findViewById(R.id.Layout_ProgressBarCameraRecording);
        Act_ProgressBarCameraRecording.setVisibility(View.INVISIBLE);

        new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        this,
                        provider -> {
                            cameraProvider = provider;
                            bindAllCameraUseCases();
                        });
    }

    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindVideoCaptcureUseCase();
            bindAnalysisUseCase();
        }
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
        try {
            PoseDetectorOptionsBase poseDetectorOptions =
                    PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
            boolean shouldShowInFrameLikelihood =
                    PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
            boolean visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
            boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
            boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this);
            imageProcessor =
                    new PoseDetectorProcessor(
                            this,
                            poseDetectorOptions,
                            shouldShowInFrameLikelihood,
                            visualizeZ,
                            rescaleZ,
                            runClassification,
                            /* isStreamMode = */ true);
        } catch (Exception e) {
        }


        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution);
        }
        analysisUseCase = builder.build();

        needUpdateGraphicOverlayImageSourceInfo = true;
        analysisUseCase.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(this),
                imageProxy -> {
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            GraphicOverlay.setImageSourceInfo(
                                    imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                        } else {
                            GraphicOverlay.setImageSourceInfo(
                                    imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false;
                    }
                    try {
                        imageProcessor.processImageProxy(imageProxy, GraphicOverlay);
                        if(RecordingVideo==true) {
                            A = PosePKResult.getPKTimeResult();
                            if (A.toString().equals("true")) {
                                intPKTime++;
                                if (intPKTime == 30) {
                                    intPKTime = 0;
                                    PKTime++;
                                }
                            } else {
                                intPKTime = 0;
                            }
                          //  TextViewPKTime.setText(String.valueOf(PKTime));
                        }
                    } catch (MlKitException e) {
                        e.printStackTrace();
                    }
                });

        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, analysisUseCase);
    }

    @SuppressLint("RestrictedApi")
    private void bindVideoCaptcureUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (videoCaptureUseCase != null) {
            cameraProvider.unbind(videoCaptureUseCase);
        }
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
        try {

        } catch (Exception e) {
            return;
        }

        videoCaptureUseCase = new VideoCapture.Builder()
                .setVideoFrameRate(30)
                .build();
        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, videoCaptureUseCase);
    }

    private void bindPreviewUseCase() {
        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return;
        }
        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution);
        }
        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(PreView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, previewUseCase);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        if ((view.getId())==(R.id.Layout_ImgBtnCameraRecording)) {
            if (RecordingVideo == false) {
                RecordingVideo = true;
                Act_ProgressBarCameraRecording.setVisibility(View.VISIBLE);
                RecordVideo();
                intPKTime = 0;
                PKTime = 0;
            } else {
                RecordingVideo = false;
                Act_ProgressBarCameraRecording.setVisibility(View.INVISIBLE);
                videoCaptureUseCase.stopRecording();
                //cameraProvider.unbindAll();



//                AlertDialog.Builder builder = new AlertDialog.Builder(VideoRecordingActivity.this);
//                builder.setCancelable(false);
//                //這邊是設定使用者可否點擊空白處返回
//                //builder.setIcon();
//                //setIcon可以在Title旁邊放一個小插圖
//                builder.setTitle("結果");
//                builder.setMessage("本次獲得"+PKTime+" 幣");
//                builder.setIcon(R.drawable.morldstart);
//                intPKTime = 0;
//                PKTime = 0;
//                //TextViewPKTime.setText(String.valueOf(PKTime));
//                builder.setPositiveButton("還要挑戰", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent();
//                        intent.setClass(VideoRecordingActivity.this, video_pose_detector_editor.class);
//                        startActivity(intent);
//                        //finish();
//                    }
//                });
//                builder.setNegativeButton("結束 App", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//
//                builder.show();


            }
        }
//            if (Act_ImgBtnCameraRecording.getText().equals("start")) {
//                Act_ImgBtnCameraRecording.setText("stop");
//                RecordVideo();
//            } else {
//                Act_ImgBtnCameraRecording.setText("start");
//                videoCaptureUseCase.stopRecording();
//                //needRecordVideo=false;
//                //Intent intent = new Intent();
//                //intent.setClass(VideoRecordingActivity.this, VideoTrimActivity.class);
//                //startActivity(intent);
//                //finish();
//            }
//        }
//        else if ((view.getId())==(R.id.ImgBtn_Phone_Video_Choose))
//        {
//            pickvideogallery();
//        }
        else
        {

        }
    }

//    private void pickvideogallery()
//    {
//        Intent intent =new Intent(Intent.ACTION_PICK);
//        intent.setType("video/*");
//        galleryActivityResultLauncher.launch(intent);
//
//    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK)
                    {
//                        Intent data=result.getData();
//                        videoUri = data.getData();
//                        Intent intent2 = new Intent();
//                        intent2= new Intent(VideoRecordingActivity.this,VideoTrimActivity.class);
//                        Bundle objbundle = new Bundle();
//                        objbundle.putString("pVideoUri",videoUri.toString());
//                        intent2.putExtras(objbundle);
//                        startActivity(intent2);
                        //finish();
                        //finishsignalActivity(VideoRecordingActivity.this);
                    }
                    else
                    {

                    }
                }
            }
    );



    @SuppressLint("RestrictedApi")
    private void RecordVideo() {
        if (videoCaptureUseCase != null) {
            long timestamp = System.currentTimeMillis();
            //long timestamp = 333;
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            Act_ProgressBarCameraRecording.setVisibility(View.VISIBLE);

            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                videoCaptureUseCase.startRecording(
                        new VideoCapture.OutputFileOptions.Builder(
                                getContentResolver(),
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                        ).build(),
                        getExecutor(),
                        new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                                Toast.makeText(getApplicationContext(), "Video has been saved successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent();
                                intent2= new Intent(VideoRecordingActivity.this, video_editor_time.class);

                                Bundle objbundle = new Bundle();
                                objbundle.putLong("video_name",timestamp);
                                intent2.putExtras(objbundle);
                                startActivity(intent2);
                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                                Toast.makeText(getApplicationContext(), "Error saving video: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    Executor getExecutor() {
        return  ContextCompat.getMainExecutor(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.Layout_TogBtnCameraFacing:
                if (cameraProvider == null) {
                    return;
                }
                int newLensFacing =
                        lensFacing == CameraSelector.LENS_FACING_FRONT
                                ? CameraSelector.LENS_FACING_BACK
                                : CameraSelector.LENS_FACING_FRONT;
                CameraSelector newCameraSelector =
                        new CameraSelector.Builder().requireLensFacing(newLensFacing).build();
                try {
                    if (cameraProvider.hasCamera(newCameraSelector)) {
                        lensFacing = newLensFacing;
                        cameraSelector = newCameraSelector;
                        bindAllCameraUseCases();
                        return;
                    }
                } catch (CameraInfoUnavailableException e) {
                    // Falls through
                }
                Toast.makeText(
                                getApplicationContext(),
                                "This device does not have lens with facing: " + newLensFacing,
                                Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        bindAllCameraUseCases();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }
}
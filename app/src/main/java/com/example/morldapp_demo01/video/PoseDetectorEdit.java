package com.example.morldapp_demo01.video;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import wseemann.media.FFmpegMediaMetadataRetriever;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.morldapp_demo01.MainActivity;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.camera.VideoPoseDetectActivity;
import com.example.morldapp_demo01.classification.posedetector.DetectPoseGraphic;
import com.example.morldapp_demo01.classification.posedetector.EditorPoseGraphic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PoseDetectorEdit extends AppCompatActivity {

    public int count= 0;
    public boolean saved=false;
    public boolean analyze_flag=false,draw_flag=false;
    private com.example.morldapp_demo01.EditGraphicOverlay EditGraphicOverlay;

    private String input_video_path;
    long timestamp ;
    long timestamp_A ;
    long countmax ;
    long timestamp_B ;
    FFmpegMediaMetadataRetriever retriever;
    VideoView Act_VideoView_Pose;
    Bitmap Act_Bitmap_PoseEditor;
    ImageView Act_ImgView_Pose;
    private Uri imageUri =null;
    private PoseDetector poseDetector;
    Bitmap bitmap = null ;
    Boolean analyze=false;
    Bitmap bitmap2 =null;
    private MediaController vidControl;
    int show=0;
    int showTEST=1000000;

    Uri uri ;
    String VID_input_video_path ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_detector_edit);
        Act_ImgView_Pose= findViewById(R.id.ImageView_Editor);
        EditGraphicOverlay = findViewById(R.id.GraphicOverlay_Edit);
        Act_VideoView_Pose=findViewById(R.id.DETEDCTOR_VideView_Trimvideo);

//      CanvasView canvasView = findViewById(R.id.myCanvasView);
//      Bundle objgetbundle = this.getIntent().getExtras();
//      timestamp = objgetbundle.getLong("video_name");

        retriever=new FFmpegMediaMetadataRetriever();
        input_video_path= Environment.getExternalStorageDirectory().getPath();
        input_video_path=input_video_path+"/Movies/"+timestamp+".mp4";
        input_video_path= "https://storage.googleapis.com/download/storage/v1/b/db-morld-photo/o/3.mp4?generation=1673540748956486&alt=media";
        retriever.setDataSource(input_video_path);
        Act_Bitmap_PoseEditor=null;
        Act_Bitmap_PoseEditor=retriever.getFrameAtTime(2000);
        //Act_VideoView_Pose.start();

//      canvasView.setBackground(Act_Bitmap_PoseEditor);
//      Act_ImgView_Pose.setImageBitmap(Act_Bitmap_PoseEditor);

        Button btClear,btRed,btBlue,btGreen;
        btClear = findViewById(R.id.button_Clear);
        btGreen = findViewById(R.id.button_Green);
        btBlue = findViewById(R.id.button_Blue);

        btClear.setOnClickListener(v->{
            pickvideogallery();
            showTEST=1000000;
            //long duriztions=Act_VideoView_Pose.getDuration();
            //countmax=duriztions/1000;
            //Toast.makeText(PoseDetectorEdit.this,"TIME : "+countmax,Toast.LENGTH_LONG).show();

        });

        btGreen.setOnClickListener(v -> {
            analyze = false;
            analyze_flag=false;
            draw_flag=true;



       //     Intent intent2 = new Intent();
       //     intent2= new Intent(PoseDetectorEdit.this, VideoPoseDetectActivity.class);
//            Bundle objbundle = new Bundle();
//            objbundle.putLong("video_name",timestamp);
//            intent2.putExtras(objbundle);
        //   startActivity(intent2);
        });


        btBlue.setOnClickListener(v -> {
            long duriztions=Act_VideoView_Pose.getDuration();                                  
            countmax=duriztions/1000;
            draw_flag=false;//analyze_flag=true;
            Toast.makeText(PoseDetectorEdit.this,"TIME : "+countmax,Toast.LENGTH_LONG).show(); //count = count + 1;
            analyze_flag=true;
            analyze=true;
            count=0;
            //Act_VideoView_Pose.start();
            handler.post(myrunnable);
            //Act_ImgView_Pose.setVisibility(View.VISIBLE);
             //for(count=0;count<10;count++) {
                // count = count + 1;
                 //count=count+1;
             //}

        });

    }

    private Handler handler=new Handler();

        private Runnable myrunnable =new Runnable() {
            @Override
            public void run() {
                if (analyze_flag==true) {
                    if (count ==0)
                    {
                        //Act_VideoView_Pose.start();
                        String filename = "pose_detect_0514_9.txt";
                        // 存放檔案位置在 內部空間/Download/
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File file = new File(path, filename);
                        if(file.isFile())
                        {
                            file.delete();
                        }

                    }

                    //countmax
                    if (count <=countmax) {
                        Log.i(Tools.TAG, String.format("count=%d", count));
                        Act_Bitmap_PoseEditor = null;
                        //timestamp_A=Act_VideoView_Pose.getCurrentPosition() * 1000+800000;
                        //timestamp_A = (Act_VideoView_Pose.getCurrentPosition() * 1000);
                        timestamp_A = (count * 1000000);
                        //timestamp_A=timestamp_A+500;
                        //timestamp_A = (count* 1000000);
                        count = count + 1;
                        //timestamp_B=timestamp_A+600;
                        Act_Bitmap_PoseEditor = retriever.getFrameAtTime(timestamp_A);
                        bitmap = Act_Bitmap_PoseEditor;
                        if (analyze = true) {
                            AccuratePoseDetectorOptions options =
                                    new AccuratePoseDetectorOptions.Builder()
                                            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                                            .build();
                            poseDetector = PoseDetection.getClient(options);
                            analyze_picture();

                            handler.postDelayed(this, 1000);

                            if (count == countmax) {
                                analyze = false;
                                analyze_flag=false;
                                count = 0;
                                draw_flag=true;
//                                 EditGraphicOverlay.clear();
//                                 EditGraphicOverlay.add(
//                                         new DetectPoseGraphic(                        //Act_VideoView_Pose.start();
//                                                 EditGraphicOverlay, 0                 //draw_flag=true;
//                                         ));                                           //Act_VideoView_Pose.start();
                                show=1000000;
                            }

                        }
                    }
                }
                else
                {

                    if(draw_flag==true) {

                        if(show==1000000)
                        {
                            Act_VideoView_Pose.start();
                            //EditGraphicOverlay.clear();
//                            EditGraphicOverlay.add(
//                                    new DetectPoseGraphic(                        //Act_VideoView_Pose.start();
//                                            EditGraphicOverlay, 0                 //draw_flag=true;
//                                    ));
                            showTEST=0;
                            show=0;
                        }

                        //handler.postDelayed(this, 800);
                        //if(count==0)
                       // {
                             //Act_VideoView_Pose.start();
                            showTEST=Act_VideoView_Pose.getCurrentPosition()/1000;
                           // if(showTEST!=show) {
                               show = showTEST;
                               EditGraphicOverlay.clear();
                                EditGraphicOverlay.add(
                                        new DetectPoseGraphic(                        //Act_VideoView_Pose.start();
                                                EditGraphicOverlay, show                 //draw_flag=true;
                                        ));
                                //Toast.makeText(PoseDetectorEdit.this, "TTTTIME : " + show, Toast.LENGTH_LONG).show();
                          //  }
                            //Act_VideoView_Pose.start();
                      /*  if(showTEST==0) {
                            EditGraphicOverlay.clear();
                            EditGraphicOverlay.add(
                                    new DetectPoseGraphic(
                                            EditGraphicOverlay, 0
                                    ));
                        }     */
                            if(show==(countmax-1))
                            {
                                draw_flag=false;
                            }
                        handler.postDelayed(this, 1000);
                       // }

                        //handler.postDelayed(this, 1000);
                        //if (count < 30) {
                            //EditGraphicOverlay.clear();
                            /*EditGraphicOverlay.add(
                                    new DetectPoseGraphic(
                                            EditGraphicOverlay, count
                                    ));*/
                         //   count = count + 1;
                       // }
                    }
                    else
                    {
                        handler.postDelayed(this, 1000);
                        //handler.postDelayed(this, 2000);
                        //handler.postDelayed(this, 1000);
                    }
                }

            }
        };



    private void pickimagegallery()
    {
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private void pickvideogallery()
    {
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        videoActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK)
                    {
                        Intent data=result.getData();
                        imageUri = data.getData();
                        try {
                             bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {

                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> videoActivityResultLauncher =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        String VID_input_video_path = getRealPathFromURI(PoseDetectorEdit.this, uri);
                        retriever.setDataSource(VID_input_video_path);
                        Act_VideoView_Pose.setVideoPath(VID_input_video_path);
//                        for (int n = 0; n < 10; n++)
//                        {
//
//                        Act_Bitmap_PoseEditor = null;
//                        Act_Bitmap_PoseEditor = retriever.getFrameAtTime(n* 1000);
//                            bitmap=Act_Bitmap_PoseEditor;
//                            AccuratePoseDetectorOptions options =
//                                    new AccuratePoseDetectorOptions.Builder()
//                                            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
//                                            .build();
//                            poseDetector = PoseDetection.getClient(options);
//                            analyze_picture();
//                        }

                    }
                    else
                    {

                    }
                }
            }
    );

    private void analyze_picture() {
        float sx=0,sy=0,Adjustscale=0,test=0;
        //InputImage inputImage = InputImage.fromFilePath(this, imageUri);
        //Act_ImgView_Pose.setImageURI(imageUri);
        if(bitmap==null) return;
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        //Act_ImgView_Pose.setImageBitmap(bitmap);

        if((bitmap.getWidth()>1080)||(bitmap.getHeight()>1920))
        {
            sx = (float) 1920/(bitmap.getHeight());
            sy = (float)1080/(bitmap.getWidth());
             Adjustscale = (sx < sy) ? sx : sy;

            //Toast.makeText(PoseDetectorEdit.this,"OKOK"+sx+sy,Toast.LENGTH_SHORT).show();

            Matrix matrix=new Matrix();
            test=(float) (Math.floor(Adjustscale*100.0)/100.0);
            matrix.postScale(test,test);

            //Toast.makeText(PoseDetectorEdit.this,"OKOK"+test+test,Toast.LENGTH_SHORT).show();
            //matrix.postRotate(90.0f);
             bitmap2=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            inputImage = InputImage.fromBitmap(bitmap2, 0);
           // Act_ImgView_Pose.setImageBitmap(bitmap2);
        }
        else if((bitmap.getWidth()<1080)&&(bitmap.getHeight()<1920))
        {
            sx = (float) 1920/(bitmap.getHeight());
            sy = (float)1080/(bitmap.getWidth());
            Adjustscale = (sx < sy) ? sx : sy;
            //Toast.makeText(PoseDetectorEdit.this,"OKOK"+sx+sy,Toast.LENGTH_SHORT).show();

            Matrix matrix=new Matrix();
            matrix.postScale(Adjustscale,Adjustscale);
            //matrix.postRotate(90.0f);
             bitmap2=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            inputImage = InputImage.fromBitmap(bitmap2, 0);
           // Act_ImgView_Pose.setImageBitmap(bitmap2);
        }


        Task<Pose> result = poseDetector.process(inputImage)
                .addOnSuccessListener(
                new OnSuccessListener<Pose>() {
                    @Override
                    public void onSuccess(Pose pose) {
                        List<PoseLandmark> allPoseLandmarks = pose.getAllPoseLandmarks();
                        if(allPoseLandmarks.isEmpty())
                        {
                            //Toast.makeText(PoseDetectorEdit.this,"ZERO",Toast.LENGTH_SHORT).show();
                            EditGraphicOverlay.clear();
                            Act_ImgView_Pose.setImageBitmap(bitmap2);
                        }
                        else
                        {
                            //Toast.makeText(PoseDetectorEdit.this,"OKOK"+count,Toast.LENGTH_SHORT).show();
                            EditGraphicOverlay.clear();
                            EditGraphicOverlay.add(
                                    new EditorPoseGraphic(
                                            EditGraphicOverlay,
                                            pose,count));
                            Act_ImgView_Pose.setImageBitmap(bitmap2);

                            poseDetector.close();
                        }
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                    return;
                            }
                        }
                );

    }
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

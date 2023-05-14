package com.example.morldapp_demo01.video;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.camera.VideoPoseDetectActivity;
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

import java.io.FileNotFoundException;
import java.util.List;

public class PoseDetectorEdit extends AppCompatActivity {

    public int count= -1;
    public boolean saved=false;
    private com.example.morldapp_demo01.EditGraphicOverlay EditGraphicOverlay;

    private String input_video_path;
    long timestamp ;
    MediaMetadataRetriever retriever;
    Bitmap Act_Bitmap_PoseEditor;
    ImageView Act_ImgView_Pose;
    private Uri imageUri =null;
    private PoseDetector poseDetector;
    Bitmap bitmap = null ;
    Boolean analyze=false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_detector_edit);
        Act_ImgView_Pose= findViewById(R.id.ImageView_Editor);
        EditGraphicOverlay = findViewById(R.id.GraphicOverlay_Edit);

//      CanvasView canvasView = findViewById(R.id.myCanvasView);
//      Bundle objgetbundle = this.getIntent().getExtras();
//      timestamp = objgetbundle.getLong("video_name");

        retriever=new MediaMetadataRetriever();
        input_video_path= Environment.getExternalStorageDirectory().getPath();
        input_video_path=input_video_path+"/Movies/"+timestamp+".mp4";
        input_video_path= "https://storage.googleapis.com/download/storage/v1/b/db-morld-photo/o/3.mp4?generation=1673540748956486&alt=media";
        retriever.setDataSource(input_video_path);
        Act_Bitmap_PoseEditor=null;
        Act_Bitmap_PoseEditor=retriever.getFrameAtTime(2000);

//      canvasView.setBackground(Act_Bitmap_PoseEditor);
//      Act_ImgView_Pose.setImageBitmap(Act_Bitmap_PoseEditor);



        Button btClear,btRed,btBlue,btGreen;
        btClear = findViewById(R.id.button_Clear);
        btGreen = findViewById(R.id.button_Green);
        btBlue = findViewById(R.id.button_Blue);

        btClear.setOnClickListener(v->{
            count=count+1;
            pickimagegallery();
            analyze=true;
        });
        btGreen.setOnClickListener(v -> {
            Intent intent2 = new Intent();
            intent2= new Intent(PoseDetectorEdit.this, VideoPoseDetectActivity.class);

            Bundle objbundle = new Bundle();
            objbundle.putLong("video_name",timestamp);
            intent2.putExtras(objbundle);
            startActivity(intent2);
        });
        btBlue.setOnClickListener(v -> {
            if(analyze=true)
            {
                AccuratePoseDetectorOptions options =
                        new AccuratePoseDetectorOptions.Builder()
                                .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                                .build();
                poseDetector = PoseDetection.getClient(options);
                analyze_picture();
                analyze=false;
            }

        });

    }

    private void pickimagegallery()
    {
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
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

    private void analyze_picture() {
        float sx=0,sy=0,Adjustscale=0,test=0;
        //InputImage inputImage = InputImage.fromFilePath(this, imageUri);
        //Act_ImgView_Pose.setImageURI(imageUri);
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        Act_ImgView_Pose.setImageBitmap(bitmap);

        if((bitmap.getWidth()>1080)||(bitmap.getHeight()>1920))
        {
            sx = (float) 1920/(bitmap.getHeight());
            sy = (float)1080/(bitmap.getWidth());
             Adjustscale = (sx < sy) ? sx : sy;

            //Toast.makeText(PoseDetectorEdit.this,"OKOK"+sx+sy,Toast.LENGTH_SHORT).show();

            Matrix matrix=new Matrix();
            test=(float) (Math.floor(Adjustscale*100.0)/100.0);
            matrix.postScale(test,test);

            Toast.makeText(PoseDetectorEdit.this,"OKOK"+test+test,Toast.LENGTH_SHORT).show();
            //matrix.postRotate(90.0f);
            Bitmap bitmap2=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            inputImage = InputImage.fromBitmap(bitmap2, 0);
            Act_ImgView_Pose.setImageBitmap(bitmap2);
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
            Bitmap bitmap2=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            inputImage = InputImage.fromBitmap(bitmap2, 0);
            Act_ImgView_Pose.setImageBitmap(bitmap2);
        }


        Task<Pose> result = poseDetector.process(inputImage)
                .addOnSuccessListener(
                new OnSuccessListener<Pose>() {
                    @Override
                    public void onSuccess(Pose pose) {
                        List<PoseLandmark> allPoseLandmarks = pose.getAllPoseLandmarks();
                        if(allPoseLandmarks.isEmpty())
                        {
                            Toast.makeText(PoseDetectorEdit.this,"ZERO",Toast.LENGTH_SHORT).show();
                            EditGraphicOverlay.clear();
                        }
                        else
                        {
                            Toast.makeText(PoseDetectorEdit.this,"OKOK",Toast.LENGTH_SHORT).show();
                            EditGraphicOverlay.clear();
                            EditGraphicOverlay.add(
                                    new EditorPoseGraphic(
                                            EditGraphicOverlay,
                                            pose,count));

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
}

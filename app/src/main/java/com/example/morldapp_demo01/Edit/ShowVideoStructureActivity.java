package com.example.morldapp_demo01.Edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.activity.Base;
import com.google.mlkit.vision.common.InputImage;

public class ShowVideoStructureActivity extends Base implements View.OnClickListener{

    VideoView Act_VideoView_Pose;
    MediaMetadataRetriever retriever;
    MediaController mediaController;
    Bitmap Act_Bitmap_VideoPoseEditor=null,Adjust_Bitmap_ShowVideo=null;
    ImageView Act_ImageView_ShowVideo;

    GraphicOverlay Act_GraphicOverlay_ShowVideoStructure;
    Button Act_Button_VideoStructureEdit,Act_Button_VideoStructureShow;
    private structurepoint[][] posestructurepoint=new structurepoint[100][12];
    int count=0;
    private String StructureUriStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video_structure);
        retriever=new MediaMetadataRetriever();

        Act_VideoView_Pose=findViewById(R.id.Layout_VideoView_ShowVideo);
        Act_ImageView_ShowVideo=findViewById(R.id.Layout_ImageView_ShowVideo);
        Act_GraphicOverlay_ShowVideoStructure=findViewById(R.id.Layout_GraphicOverlay_ShowVideoStructure);
        Act_Button_VideoStructureEdit=findViewById(R.id.Layout_Button_VideoStructureEdit);
        Act_Button_VideoStructureShow=findViewById(R.id.Layout_Button_VideoStructureShow);

        Act_Button_VideoStructureEdit.setOnClickListener(this);
        Act_Button_VideoStructureShow.setOnClickListener(this);


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        StructureUriStr = bundle.getString("urivideostr");

        Act_VideoView_Pose.setVideoURI(Uri.parse(StructureUriStr));


        retriever.setDataSource(StructureUriStr);
        Act_Bitmap_VideoPoseEditor=null;

        mediaController=new MediaController(this);
        mediaController.setVisibility(View.VISIBLE);
        Act_VideoView_Pose.setMediaController(mediaController);

        for(int idx=0;idx<30;idx++) {
            Act_Bitmap_VideoPoseEditor = retriever.getFrameAtTime(idx*1000000);

            Adjust_Bitmap_ShowVideo = StructureAnalyze.Adjust_picture(Act_Bitmap_VideoPoseEditor);
            Act_ImageView_ShowVideo.setImageBitmap(Adjust_Bitmap_ShowVideo);

            InputImage inputImage = InputImage.fromBitmap(Adjust_Bitmap_ShowVideo, 0);
            StructureAnalyze.Analyze_Structure(inputImage, Act_GraphicOverlay_ShowVideoStructure, "yuiop1.txt", idx);
            Act_GraphicOverlay_ShowVideoStructure.clear();
        }
        mediaController=new MediaController(this);
        Act_VideoView_Pose.setMediaController(mediaController);

        for (int countidx = 0; countidx < 30; countidx++) {
            posestructurepoint[countidx] = FileMangement.ReadFile("yuiop1.txt", countidx);
        }

      //  Act_VideoView_Pose.start();
        Act_VideoView_Pose.start();
        handler.post(myrunnable);

    }

    void video_structure_show()
    {
        Act_VideoView_Pose.start();
        handler.post(myrunnable);


     }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Layout_Button_VideoStructureShow:
                //video_structure_show();
                int Time=((Act_VideoView_Pose.getCurrentPosition())/1000);
                Act_GraphicOverlay_ShowVideoStructure.clear();

                Act_GraphicOverlay_ShowVideoStructure.add(
                        new AnalyzePoseGraphic(
                                Act_GraphicOverlay_ShowVideoStructure,
                                posestructurepoint[Time]));
                break;

            case R.id.Layout_Button_VideoStructureEdit:

                int Timecount=((Act_VideoView_Pose.getCurrentPosition())/1000);
                Intent intent2 = new Intent();
                intent2= new Intent(ShowVideoStructureActivity.this, AdjustVideoStructureActivity.class);
                Bundle objbundle = new Bundle();
                objbundle.putString("uristr_Edit",StructureUriStr);
                objbundle.putInt("Timecount",Timecount);
                intent2.putExtras(objbundle);

                startActivity(intent2);
                finish();

                break;

            default:
                break;

        }
    }

    private Handler handler=new Handler();

    private Runnable myrunnable =new Runnable() {
        @Override
        public void run() {

            int Time=((Act_VideoView_Pose.getCurrentPosition()+20)/1000);

             Act_GraphicOverlay_ShowVideoStructure.clear();

            Act_GraphicOverlay_ShowVideoStructure.add(
                    new AnalyzePoseGraphic(
                            Act_GraphicOverlay_ShowVideoStructure,
                            posestructurepoint[Time]));

            handler.postDelayed(myrunnable, 980);

            if(Time>10)
            {

                handler.removeCallbacks(this);
            }
        }
    };
}
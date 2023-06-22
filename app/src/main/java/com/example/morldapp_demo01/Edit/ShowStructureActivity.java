package com.example.morldapp_demo01.Edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.activity.Base;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.FileNotFoundException;
import java.util.List;

public class ShowStructureActivity extends Base
{

    private Uri Uri_photo=null;
    ImageView Act_ImageView_ShowPhoto;
    Bitmap Bitmap_ShowPhoto=null,Adjust_Bitmap_ShowPhoto=null;

    GraphicOverlay Act_GraphicOverlay_ShowStructure;
    Pose ShowStructurepose;
    List<PoseLandmark> landmarks=null;

    Button Act_Button_StructureEdit,Act_Button_StructureShow;

    private structurepoint[] posestructurepoint=new structurepoint[12];
    private String StructureUriStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_structure);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
         StructureUriStr = bundle.getString("uristr");
        Uri_photo=Uri.parse((String)StructureUriStr);

        Act_ImageView_ShowPhoto=findViewById(R.id.Layout_ImageView_ShowPhoto);
        Act_GraphicOverlay_ShowStructure = findViewById(R.id.Layout_GraphicOverlay_ShowStructure);
        Act_Button_StructureEdit=findViewById(R.id.Layout_Button_StructureEdit);
        Act_Button_StructureShow=findViewById(R.id.Layout_Button_StructureShow);

        try {
            Bitmap_ShowPhoto= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri_photo));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Act_ImageView_ShowPhoto.setImageURI(Uri_photo);
        Adjust_Bitmap_ShowPhoto=StructureAnalyze.Adjust_picture(Bitmap_ShowPhoto);
        Act_ImageView_ShowPhoto.setImageBitmap(Adjust_Bitmap_ShowPhoto);

        InputImage inputImage=InputImage.fromBitmap(Adjust_Bitmap_ShowPhoto, 0);

//        new FileMangement(Act_GraphicOverlay_ShowStructure);
//        FileMangement.DeleteFile("qwertyq.txt");


        StructureAnalyze.Analyze_Structure(getActivity(), inputImage,Act_GraphicOverlay_ShowStructure,"yuiop.txt",0);
        ShowStructurepose=StructureAnalyze.StructurePose();
//        landmarks = ShowStructurepose.getAllPoseLandmarks();
//        if (landmarks.isEmpty()) {
//           // Act_Button_StructureShow.setEnabled(false);
//        }
//        else
//        {
//           // Act_Button_StructureShow.setEnabled(true);
//           // Act_Button_StructureEdit.setEnabled(true);
//        }

//        try {
//            new FileMangement(Act_GraphicOverlay_ShowStructure);
//            FileMangement.SaveFile("qwertyqwertyu.txt",ShowStructurepose,0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Act_Button_StructureShow.setOnClickListener(v->{

//            posestructurepoint=FileMangement.ReadFile(getActivity(),"qwertyq.txt",0);
            Act_GraphicOverlay_ShowStructure.clear();

            Act_GraphicOverlay_ShowStructure.add(
                    new AnalyzePoseGraphic(
                            Act_GraphicOverlay_ShowStructure,
                            posestructurepoint,1,1));
        });

        Act_Button_StructureEdit.setOnClickListener(v->{
//            if (landmarks.isEmpty()) {
//
//                Intent intent2 = new Intent();
//                intent2= new Intent(ShowStructureActivity.this, VideoRecordingActivity.class);
//                startActivity(intent2);
//                finish();
//            }
//            else
//            {
                Intent intent2 = new Intent();
                intent2= new Intent(ShowStructureActivity.this, AdjustStructureActivity.class);
                Bundle objbundle = new Bundle();
                objbundle.putString("uristr_Edit",StructureUriStr);
                intent2.putExtras(objbundle);

                startActivity(intent2);
                finish();
//            }
//

        });

    }

}


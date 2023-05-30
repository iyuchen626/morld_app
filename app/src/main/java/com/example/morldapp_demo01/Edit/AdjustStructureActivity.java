package com.example.morldapp_demo01.Edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.activity.Base;
import com.example.morldapp_demo01.camera.VideoDetectActivity;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AdjustStructureActivity extends Base implements View.OnClickListener{


    private Uri UriStructureEdit=null;
//
    GraphicOverlay Act_GraphicOverlay_EditStructure;
   ImageView Act_ImageView_EditFrame;
   Bitmap Bitmap_EditFramePicture=null,Adjust_Bitmap_EditFramePicture=null;
   Button Act_Button_StructureEditUp,Act_Button_StructureEditDown,Act_Button_StructureEditLeft,Act_Button_StructureEditRight,Act_Button_StructureEditDone,Act_Button_Structure_Ｗeight_Edit;
   Button Act_Button_Structure_Point_0,Act_Button_Structure_Point_1,Act_Button_Structure_Point_2,Act_Button_Structure_Point_3,Act_Button_Structure_Point_4,Act_Button_Structure_Point_5,Act_Button_Structure_Point_6,Act_Button_Structure_Point_7,Act_Button_Structure_Point_8,Act_Button_Structure_Point_9,Act_Button_Structure_Point_10,Act_Button_Structure_Point_11;

    EditText ActText;
    TextView Act_TextView_Structure_point;
    private int PointIdx=0;
    private structurepoint[] posestructurepoint=new structurepoint[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_structure);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String StructureUriStr = bundle.getString("uristr_Edit");
        UriStructureEdit= Uri.parse((String)StructureUriStr);

        Act_ImageView_EditFrame=findViewById(R.id.Layout_ImageView_EditFrame);
        Act_GraphicOverlay_EditStructure = findViewById(R.id.Layout_GraphicOverlay_EditStructure);

        Act_Button_StructureEditUp=findViewById(R.id.Layout_Button_StructureEditUp);
        Act_Button_StructureEditDown=findViewById(R.id.Layout_Button_StructureEditDown);
        Act_Button_StructureEditLeft=findViewById(R.id.Layout_Button_StructureEditLeft);
        Act_Button_StructureEditRight=findViewById(R.id.Layout_Button_StructureEditRight);
        Act_Button_StructureEditDone=findViewById(R.id.Layout_Button_StructureEditDone);

        Act_Button_Structure_Point_0=findViewById(R.id.layout_Button_Structure_Point_0);
        Act_Button_Structure_Point_1=findViewById(R.id.layout_Button_Structure_Point_1);
        Act_Button_Structure_Point_2=findViewById(R.id.layout_Button_Structure_Point_2);
        Act_Button_Structure_Point_3=findViewById(R.id.layout_Button_Structure_Point_3);
        Act_Button_Structure_Point_4=findViewById(R.id.layout_Button_Structure_Point_4);
        Act_Button_Structure_Point_5=findViewById(R.id.layout_Button_Structure_Point_5);
        Act_Button_Structure_Point_6=findViewById(R.id.layout_Button_Structure_Point_6);
        Act_Button_Structure_Point_7=findViewById(R.id.layout_Button_Structure_Point_7);
        Act_Button_Structure_Point_8=findViewById(R.id.layout_Button_Structure_Point_8);
        Act_Button_Structure_Point_9=findViewById(R.id.layout_Button_Structure_Point_9);
        Act_Button_Structure_Point_10=findViewById(R.id.layout_Button_Structure_Point_10);
        Act_Button_Structure_Point_11=findViewById(R.id.layout_Button_Structure_Point_11);

        Act_Button_Structure_Ｗeight_Edit=findViewById(R.id.Layout_Button_Structure_ＷeightEdit);


        Act_TextView_Structure_point=findViewById(R.id.Structure_point);

        ActText=findViewById(R.id.input_PointIdx);

        Act_Button_StructureEditUp.setOnClickListener(this);
        Act_Button_StructureEditDown.setOnClickListener(this);
        Act_Button_StructureEditLeft.setOnClickListener(this);
        Act_Button_StructureEditRight.setOnClickListener(this);
        Act_Button_StructureEditDone.setOnClickListener(this);

        Act_Button_Structure_Point_0.setOnClickListener(this);
        Act_Button_Structure_Point_1.setOnClickListener(this);
        Act_Button_Structure_Point_2.setOnClickListener(this);
        Act_Button_Structure_Point_3.setOnClickListener(this);
        Act_Button_Structure_Point_4.setOnClickListener(this);
        Act_Button_Structure_Point_5.setOnClickListener(this);
        Act_Button_Structure_Point_6.setOnClickListener(this);
        Act_Button_Structure_Point_7.setOnClickListener(this);
        Act_Button_Structure_Point_8.setOnClickListener(this);
        Act_Button_Structure_Point_9.setOnClickListener(this);
        Act_Button_Structure_Point_10.setOnClickListener(this);
        Act_Button_Structure_Point_11.setOnClickListener(this);
        Act_Button_Structure_Ｗeight_Edit.setOnClickListener(this);

////
//
        try {
            Bitmap_EditFramePicture= BitmapFactory.decodeStream(getContentResolver().openInputStream(UriStructureEdit));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        //Act_ImageView_ShowPhoto.setImageURI(Uri_photo);
        Adjust_Bitmap_EditFramePicture=StructureAnalyze.Adjust_picture(Bitmap_EditFramePicture);
        Act_ImageView_EditFrame.setImageBitmap(Adjust_Bitmap_EditFramePicture);
//
        new FileMangement(Act_GraphicOverlay_EditStructure);
        posestructurepoint=FileMangement.ReadFile("qwertyq.txt",0);
        Act_GraphicOverlay_EditStructure.clear();

        Act_GraphicOverlay_EditStructure.add(
                new AnalyzePoseGraphic(
                        Act_GraphicOverlay_EditStructure,
                        posestructurepoint));

        Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
        ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
//
//
    }

    @Override
    public void onClick(View v) {

        float original_x=posestructurepoint[PointIdx].getStructpoint_x();
        float original_y=posestructurepoint[PointIdx].getStructpoint_y();

        switch(v.getId())
        {
            case R.id.Layout_Button_StructureEditUp:
                posestructurepoint[PointIdx].setStructpoint_y(original_y-20);
                break;
            case R.id.Layout_Button_StructureEditDown:
                posestructurepoint[PointIdx].setStructpoint_y(original_y+20);
                break;
            case R.id.Layout_Button_StructureEditLeft:
                posestructurepoint[PointIdx].setStructpoint_x(original_x-20);
                break;
            case R.id.Layout_Button_StructureEditRight:
                posestructurepoint[PointIdx].setStructpoint_x(original_x+20);
                break;
            case R.id.layout_Button_Structure_Point_0:
                PointIdx=0;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;

            case R.id.layout_Button_Structure_Point_1:
                PointIdx=1;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;
            case R.id.layout_Button_Structure_Point_2:
                PointIdx=2;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;
            case R.id.layout_Button_Structure_Point_3:
                PointIdx=3;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;

            case R.id.layout_Button_Structure_Point_4:
                PointIdx=4;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));

                break;
            case R.id.layout_Button_Structure_Point_5:
                PointIdx=5;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;
            case R.id.layout_Button_Structure_Point_6:
                PointIdx=6;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;
            case R.id.layout_Button_Structure_Point_7:
                PointIdx=7;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;

            case R.id.layout_Button_Structure_Point_8:
                PointIdx=8;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));

                break;
            case R.id.layout_Button_Structure_Point_9:
                PointIdx=9;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;
            case R.id.layout_Button_Structure_Point_10:
                PointIdx=10;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;
            case R.id.layout_Button_Structure_Point_11:
                PointIdx=11;
                Act_TextView_Structure_point.setText(String.valueOf(PointIdx));
                ActText.setText(String.valueOf(posestructurepoint[PointIdx].getStructpoint_weight()));
                break;

            case R.id.Layout_Button_Structure_ＷeightEdit:
                posestructurepoint[PointIdx].setStructpoint_weight(Float.parseFloat(String.valueOf(ActText.getText())));
                break;


            case R.id.Layout_Button_StructureEditDone:

                try {
                    new FileMangement(Act_GraphicOverlay_EditStructure);
                    FileMangement.ReSaveFile("yuiop.txt",posestructurepoint,0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //               FileMangement.SaveFile(filename,structurepoint,0);
//                posestructurepoint[PointIdx].setStructpoint_x(original_x+20);
                Intent intent2 = new Intent();
                intent2= new Intent(AdjustStructureActivity.this, VideoDetectActivity.class);
                startActivity(intent2);
                finish();
                break;
            default:
                break;

        }

        Act_GraphicOverlay_EditStructure.clear();

        Act_GraphicOverlay_EditStructure.add(
                new AnalyzePoseGraphic(
                        Act_GraphicOverlay_EditStructure,
                        posestructurepoint));

    }
}
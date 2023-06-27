package com.example.morldapp_demo01.Edit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.ducky.fastvideoframeextraction.fastextraction.Frame;
import com.ducky.fastvideoframeextraction.fastextraction.FrameExtractor;
import com.ducky.fastvideoframeextraction.fastextraction.IVideoFrameExtractor;
import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.activity.Base;
import com.example.morldapp_demo01.fastextraction.Utils;
import com.example.morldapp_demo01.pojo.TxtConfigPOJO;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShowStructureActivity extends Base implements View.OnClickListener
{

    ImageView Act_ImageView_Pose;
    GraphicOverlay Act_GraphicOverlay_ShowImageStructure;
    ImageButton Act_ImgButton_ImageStructureEdit;
    ImageButton Act_ImgImgButton_Image_StructureEditUp,Act_ImgImgButton_Image_StructureEditDown,Act_ImgImgButton_Image_StructureEditLeft,Act_ImgImgButton_Image_StructureEditRight;
    Button Act_Button_ImageStructureShow;
    Bitmap Bitmap_ShowPhoto=null;
    float a=0;

    private HashMap<String,  structurepoint[]> posestructurepoint=new HashMap<>();
    int count=0;

    private String StructureUriStr;
    private ExecutorService executorService= Executors.newSingleThreadExecutor();
    private LinkedList<String> queue;
    private StringBuilder sb;
    String filename = "yuiop1.txt";
    private FrameExtractor frameExtractor;
    int orientation;
    int lastFindIndex = 0;
    private Boolean structure_show=false;
    int PointIdx = 0;
    Boolean EditStructureFlag = false;
    structurepoint[] structurepoints;
    private float original_y, original_x,height,width;
    boolean is設定旋轉與鏡像 = false;
    int degress = 0;
    boolean isFlip = false;
    Uri Uri_photo;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_structure);


        findViewById(R.id.layout_Button_ReproducePose_Image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                is設定旋轉與鏡像 = false;
                mm產生骨骼();
                try {
                    Bitmap_ShowPhoto= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(StructureUriStr)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });

        Act_ImageView_Pose=findViewById(R.id.Layout_ImageView_ShowImage);
        Act_GraphicOverlay_ShowImageStructure=findViewById(R.id.Layout_GraphicOverlay_ShowImageStructure);
        Act_ImgButton_ImageStructureEdit=findViewById(R.id.layout_ImgButton_Image_StructureEdit);
        Act_Button_ImageStructureShow=findViewById(R.id.Layout_Button_ImageStructureShow);


        Act_ImgImgButton_Image_StructureEditUp=findViewById(R.id.layout_ImgButton_Image_StructureEditUp);
        Act_ImgImgButton_Image_StructureEditLeft=findViewById(R.id.layout_ImgButton_Image_StructureEditLeft);
        Act_ImgImgButton_Image_StructureEditRight=findViewById(R.id.layout_ImgButton_Image_StructureEditRight);
        Act_ImgImgButton_Image_StructureEditDown=findViewById(R.id.layout_ImgButton_Image_StructureEditDown);

        Act_ImgButton_ImageStructureEdit.setOnClickListener(this);
        Act_Button_ImageStructureShow.setOnClickListener(this);

        Act_ImgImgButton_Image_StructureEditUp.setOnClickListener(this);
        Act_ImgImgButton_Image_StructureEditLeft.setOnClickListener(this);
        Act_ImgImgButton_Image_StructureEditRight.setOnClickListener(this);
        Act_ImgImgButton_Image_StructureEditDown.setOnClickListener(this);


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        StructureUriStr = bundle.getString("uriimagestr");
        filename = Tools.md5(StructureUriStr);
        Act_ImageView_Pose.setImageURI(Uri.parse(StructureUriStr));

    }

    void mm讀取骨骼資料()
    {
        float offset = 0;
        try
        {
            offset = Float.parseFloat(Tools.mmRead(getActivity(), Config.KEY_骨骼時間軸偏差值+filename));
        }
        catch (Exception e)
        {
            Log.e(Config.TAG, e.getMessage());
        }


        posestructurepoint = FileMangement.ReadFile(getActivity(), filename, (long) (offset*1000*1000));

        TxtConfigPOJO txt = Tools.getGson().fromJson(Tools.mmRead(getActivity(), Config.KEY_TXT_CONFIG), TxtConfigPOJO.class);
        if(txt!=null) {
            width = txt.width;
            height = txt.height;

        }

        if((posestructurepoint.size()==0)||(width==0)||(height==0))
        {
            Tools.toast(getActivity(), "骨骼分析失敗..... 請重新分析 "+posestructurepoint.size()+","+width+","+height);
            //mm產生骨骼();
        }
    }


    void mm產生骨骼()
    {
        try {
            Bitmap_ShowPhoto= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(new File(StructureUriStr))));
            Tools.toast(getActivity(), "AA");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        float a;
//        try {
       // Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(StructureUriStr)));
//        File file =new File(StructureUriStr);
//         a=file.length();
//        Toast.makeText("AAA").show();


//;       } catch (IOException e) {
//
//        }
//        try {
//           // Bitmap_ShowPhoto= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri_photo));
//            //Tools.toast(getActivity(), "AA");
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        // Tools.showProgress(getActivity(), "生成骨骼中");
       // mm校正流程();
//        try
//        {
//            Bitmap_ShowPhoto= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(StructureUriStr)));
//           // sb.append(Bitmap_ShowPhoto.getWidth()+","+Bitmap_ShowPhoto.getHeight()+"\n");
//            InputImage inputImage = InputImage.fromBitmap(Bitmap_ShowPhoto, 0);
//            queue.offer("0");
//            StructureAnalyze.Analyze_Structure(inputImage, new StructureAnalyze.OnAnalyzeStructureListener()
//            {
//                @Override
//                public void onDone(String result)
//                {
//                    synchronized (queue)
//                    {
//                        queue.poll();
//                        Log.i(Config.TAG, "queue size=" + queue.size() + " currentFrame="+ 0);
//                        if (!result.equals("")) sb.append("0#" + result);
//                        if (queue.size() == 0)
//                        {
//                            FileMangement.SaveFile(getActivity(), filename, sb.toString());
//                            Tools.hideProgress(getActivity());
//                            Tools.toastSuccess(getActivity(), "骨骼已儲存txt");
//                            mm讀取骨骼資料();
//                        }
//                    }
//
//
//                }
//            });


//        }
//        catch (Exception exception)
//        {
//            exception.printStackTrace();
//        }

    }

    void mm校正流程()
    {
        Tools.showQuestion(getActivity(), "校正", "請問畫面是否鏡像?", "是", "否", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFlip = true;
            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFlip = false;
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}


package com.example.morldapp_demo01.Edit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ducky.fastvideoframeextraction.fastextraction.Frame;
import com.ducky.fastvideoframeextraction.fastextraction.FrameExtractor;
import com.ducky.fastvideoframeextraction.fastextraction.IVideoFrameExtractor;
import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.activity.Base;
import com.example.morldapp_demo01.activity.EditFinishActivity;
import com.example.morldapp_demo01.fastextraction.URIPathHelper;
import com.example.morldapp_demo01.fastextraction.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class ShowVideoStructureActivity extends Base implements View.OnClickListener, IVideoFrameExtractor
{

    VideoView Act_VideoView_Pose;
    MediaController mediaController;
    ImageView Act_ImageView_ShowVideo;
    GraphicOverlay Act_GraphicOverlay_ShowVideoStructure;
    ImageButton Act_ImgButton_VideoStructureEdit;
    ImageButton Act_ImgImgButton_video_StructureEditUp,Act_ImgImgButton_video_StructureEditDown,Act_ImgImgButton_video_StructureEditLeft,Act_ImgImgButton_video_StructureEditRight;
    Button Act_Button_VideoStructureShow;
    private HashMap<String,  structurepoint[]> posestructurepoint=new HashMap<>();
    int count=0;
    private String StructureUriStr,videoInputPath;
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
    float original_y, original_x;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video_structure);
        frameExtractor = new FrameExtractor(ShowVideoStructureActivity.this);
        handler.postDelayed(myrunnable, 500);
//        findViewById(R.id.layout_Button_ReproducePose).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                mm產生骨骼();
//            }
//        });
        Act_VideoView_Pose=findViewById(R.id.Layout_VideoView_ShowVideo);
        Act_ImageView_ShowVideo=findViewById(R.id.Layout_ImageView_ShowVideo);
        Act_GraphicOverlay_ShowVideoStructure=findViewById(R.id.Layout_GraphicOverlay_ShowVideoStructure);
        Act_ImgButton_VideoStructureEdit=findViewById(R.id.layout_ImgButton_video_StructureEdit);
        Act_Button_VideoStructureShow=findViewById(R.id.Layout_Button_VideoStructureShow);


        Act_ImgImgButton_video_StructureEditUp=findViewById(R.id.layout_ImgButton_video_StructureEditUp);
        Act_ImgImgButton_video_StructureEditLeft=findViewById(R.id.layout_ImgButton_video_StructureEditLeft);
        Act_ImgImgButton_video_StructureEditRight=findViewById(R.id.layout_ImgButton_video_StructureEditRight);
        Act_ImgImgButton_video_StructureEditDown=findViewById(R.id.layout_ImgButton_video_StructureEditDown);

        Act_ImgButton_VideoStructureEdit.setOnClickListener(this);
        Act_Button_VideoStructureShow.setOnClickListener(this);

        Act_ImgImgButton_video_StructureEditUp.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditLeft.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditRight.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditDown.setOnClickListener(this);


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        StructureUriStr = bundle.getString("urivideostr");
        filename = Tools.md5(StructureUriStr);
        Act_VideoView_Pose.setVideoURI(Uri.parse(StructureUriStr));
        mediaController=new MediaController(this);
        mediaController.setVisibility(View.VISIBLE);
        Act_VideoView_Pose.setMediaController(mediaController);
        sb = new StringBuilder();
        queue = new LinkedList<String>();
        posestructurepoint = FileMangement.ReadFile(getActivity(), filename, (long) (-0.6*1000*1000));
        URIPathHelper uriPathHelper = new URIPathHelper();
        Uri uri = Uri.parse(StructureUriStr);
        videoInputPath = uriPathHelper.getPath(getActivity(), uri).toString();
        frameExtractor.init(videoInputPath);
        orientation = frameExtractor.getOrientation(videoInputPath);
        mm產生骨骼();
    }

    void mm產生骨骼()
    {
        Tools.showProgress(getActivity(), "生成骨骼中");
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    URIPathHelper uriPathHelper = new URIPathHelper();
                    Uri uri = Uri.parse(StructureUriStr);
                    String videoInputPath = uriPathHelper.getPath(getActivity(), uri).toString();
                    File videoInputFile = new File(videoInputPath);
                    frameExtractor.extractFrames(videoInputFile.getAbsolutePath());
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Act_GraphicOverlay_ShowVideoStructure.clear();
        switch (v.getId()) {
            case R.id.Layout_Button_VideoStructureShow:
                if(EditStructureFlag==false) {
                    HomeEditor_Dialog();
                }
                else
                {
                    Act_ImgImgButton_video_StructureEditUp.setVisibility(View.INVISIBLE);
                    Act_ImgImgButton_video_StructureEditLeft.setVisibility(View.INVISIBLE);
                    Act_ImgImgButton_video_StructureEditRight.setVisibility(View.INVISIBLE);
                    Act_ImgImgButton_video_StructureEditDown.setVisibility(View.INVISIBLE);
                    EditStructureFlag=false;
                    Act_GraphicOverlay_ShowVideoStructure.setVisibility(View.INVISIBLE);

                }
                break;

            case R.id.layout_ImgButton_video_StructureEdit:
                Structure_Dialog();
                break;

            case R.id.layout_ImgButton_video_StructureEditUp:
                original_y=structurepoints[PointIdx].getStructpoint_y();
                structurepoints[PointIdx].setStructpoint_y(original_y-20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));
                break;

            case R.id.layout_ImgButton_video_StructureEditLeft:
                original_x=structurepoints[PointIdx].getStructpoint_x();
                structurepoints[PointIdx].setStructpoint_x(original_x-20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));
                break;

            case R.id.layout_ImgButton_video_StructureEditRight:
                original_x=structurepoints[PointIdx].getStructpoint_x();
                structurepoints[PointIdx].setStructpoint_x(original_x+20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));
                break;

            case R.id.layout_ImgButton_video_StructureEditDown:
                original_y=structurepoints[PointIdx].getStructpoint_y();
                structurepoints[PointIdx].setStructpoint_y(original_y+20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));

                break;


            default:
                break;

        }
    }

    String mm取得對應時間軸之key(long currentTimeMicrosecond)
    {
        long distance = Long.MAX_VALUE; //預設極大值
        String wantId = "";
        Set<String> keysOri = posestructurepoint.keySet();
        List<String> keys = new ArrayList<String>();
        keys.addAll(keysOri);
        Collections.sort(keys);
        int i;
        for (i = 0; i < keys.size(); i++)
        {
            String f = keys.get(i);
            long id = Long.parseLong(f);
            long currentDis = Math.abs(id - currentTimeMicrosecond);
            if (currentDis < distance) //求跟目前影片播放時間距離最近的key，反查出姿態點
            {
                distance = currentDis;
                wantId = f;
            }
        }
        return wantId;
    }

    private Runnable myrunnable =new Runnable() {
        @Override
        public void run() {
            long delay = (long) ((1.0 / frameExtractor.getFPS())*1000);
            if(!Act_VideoView_Pose.isPlaying()) {            handler.postDelayed(myrunnable, delay); return;}
            long currentTimeMicrosecond=(Act_VideoView_Pose.getCurrentPosition() * 1000);
            String wantId = mm取得對應時間軸之key(currentTimeMicrosecond);
            if(!wantId.equals(""))
            {
                structurepoints = posestructurepoint.get(wantId);
                Act_GraphicOverlay_ShowVideoStructure.clear();
                Act_GraphicOverlay_ShowVideoStructure.add(new AnalyzePoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints));
            }

            handler.postDelayed(myrunnable, delay);
//            handler.postDelayed(myrunnable, 500);
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacks(myrunnable);
    }

    @Override
    public void onCurrentFrameExtracted(@NonNull Frame currentFrame)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                Tools.showProgress(getActivity(), "生成骨骼中\n" + currentFrame.getPosition());
            }
        });

        Bitmap imageBitmap = Utils.fromBufferToBitmap(currentFrame.getByteBuffer(), currentFrame.getWidth(), currentFrame.getHeight());
        int degress = 0;
        if(orientation == 90) degress = 180;
        if(orientation == 270) degress = 180;
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, degress);
        queue.offer(String.valueOf(currentFrame.getTimestamp()));
        StructureAnalyze.Analyze_Structure(inputImage, new StructureAnalyze.OnAnalyzeStructureListener()
        {
            @Override
            public void onDone(String result)
            {
                synchronized (queue)
                {
                    queue.poll();
                    Log.i(Config.TAG, "queue size=" + queue.size() + " currentFrame="+ currentFrame.getTimestamp());
                    if (!result.equals("")) sb.append(currentFrame.getTimestamp() + "#" + result);
                    if (queue.size() == 0)
                    {
                        FileMangement.SaveFile(getActivity(), filename, sb.toString());
                        posestructurepoint = FileMangement.ReadFile(getActivity(), filename, (long) (0.2*1000*1000));
                        Tools.hideProgress(getActivity());
                        Tools.toastSuccess(getActivity(), "骨骼已儲存txt");
                    }
                }
            }
        });
    }

    @Override
    public void onAllFrameExtracted(int processedFrameCount, long processedTimeMs)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                DecimalFormat f = new DecimalFormat("#0.00");
                String d = f.format(processedTimeMs / 1000.0);
                Tools.toastSuccess(getActivity(), "骨骼已運算生成，費時:" + d + "秒");
            }
        });
    }

    void HomeEditor_Dialog()
    {
        Dialog Editor_dialog=new Dialog(this.getActivity());
        View view=getLayoutInflater().inflate(R.layout.dialog_editor_save,null);
        Editor_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {


            }
        });
        Editor_dialog.setContentView(view);

        Button Act_Button_Cancel=Editor_dialog.findViewById(R.id.button＿cancel);
        Button Act_Button_Finish=Editor_dialog.findViewById(R.id.button_finish);


        Act_Button_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                //intent= new Intent(this.getActivity(), FunctionChooseActivity.class);
                intent= new Intent(ShowVideoStructureActivity.this, EditFinishActivity.class);
                startActivity(intent);

                finish();

            }
        });
        Act_Button_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Editor_dialog.dismiss();

            }
        });


        Editor_dialog.show();
    }

    void Structure_Dialog()
    {
        Dialog Editor_dialog=new Dialog(this.getActivity());
        View view=getLayoutInflater().inflate(R.layout.dialog_structure_option,null);
        Editor_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {


            }
        });
        Editor_dialog.setContentView(view);

        Window window = Editor_dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x =(int)Act_ImgButton_VideoStructureEdit.getScaleX();
        params.y = (int)Act_ImgButton_VideoStructureEdit.getScaleY();
        window.setAttributes(params);

        ImageButton Act_Button_Structure_Show=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_Show);
        ImageButton Act_Button_Structure_NotShow=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_NotShow);
        ImageButton Act_Button_Structure_AdjustPostion=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_Adjust_Postion);


        Act_Button_Structure_Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Act_GraphicOverlay_ShowVideoStructure.setVisibility(View.VISIBLE);
                Editor_dialog.dismiss();
            }
        });

        Act_Button_Structure_NotShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Act_GraphicOverlay_ShowVideoStructure.setVisibility(View.INVISIBLE);
                Editor_dialog.dismiss();
            }
        });

        Act_Button_Structure_AdjustPostion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Act_GraphicOverlay_ShowVideoStructure.setVisibility(View.VISIBLE);
                Editor_dialog.dismiss();
                Poistion_Dialog();
            }
        });



        Editor_dialog.show();
    }

    void Poistion_Dialog()
    {
        EditStructureFlag=true;
        Act_VideoView_Pose.pause();

        BottomSheetDialog Editor_dialog = new BottomSheetDialog(this.getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_editor_pointchoose, null);
        Button Act_Button_Structure_Point_0, Act_Button_Structure_Point_1, Act_Button_Structure_Point_2, Act_Button_Structure_Point_3, Act_Button_Structure_Point_4, Act_Button_Structure_Point_5, Act_Button_Structure_Point_6, Act_Button_Structure_Point_7, Act_Button_Structure_Point_8, Act_Button_Structure_Point_9, Act_Button_Structure_Point_10, Act_Button_Structure_Point_11;

        Editor_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Editor_dialog.dismiss();

            }
        });

        Editor_dialog.setContentView(view);

        Act_Button_Structure_Point_0 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_0);
        Act_Button_Structure_Point_1 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_1);
        Act_Button_Structure_Point_2 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_2);
        Act_Button_Structure_Point_3 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_3);
        Act_Button_Structure_Point_4 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_4);
        Act_Button_Structure_Point_5 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_5);
        Act_Button_Structure_Point_6 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_6);
        Act_Button_Structure_Point_7 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_7);
        Act_Button_Structure_Point_8 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_8);
        Act_Button_Structure_Point_9 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_9);
        Act_Button_Structure_Point_10 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_10);
        Act_Button_Structure_Point_11 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_11);

        Act_Button_Structure_Point_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=0;
                Editor_dialog.dismiss();
            }
        });

        Act_Button_Structure_Point_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=1;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=2;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=3;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=4;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=5;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=6;
                poistioneditshow();
               Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=7;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=8;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=9;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=10;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=11;
                poistioneditshow();
                Editor_dialog.dismiss();

            }
        });
        Act_ImgImgButton_video_StructureEditUp.setVisibility(View.VISIBLE);
        Act_ImgImgButton_video_StructureEditLeft.setVisibility(View.VISIBLE);
        Act_ImgImgButton_video_StructureEditRight.setVisibility(View.VISIBLE);
        Act_ImgImgButton_video_StructureEditDown.setVisibility(View.VISIBLE);

        Editor_dialog.show();
    }

    void poistioneditshow()
    {

        long currentTimeMicrosecond=(Act_VideoView_Pose.getCurrentPosition() * 1000);
        String wantId = mm取得對應時間軸之key(currentTimeMicrosecond);
        if(!wantId.equals(""))
        {
            structurepoints = posestructurepoint.get(wantId);
            Act_GraphicOverlay_ShowVideoStructure.clear();
            Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));
        }
    }
}

package com.example.morldapp_demo01.Edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.morldapp_demo01.fastextraction.URIPathHelper;
import com.example.morldapp_demo01.fastextraction.Utils;
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
    Button Act_Button_VideoStructureEdit,Act_Button_VideoStructureShow;
    private HashMap<String,  structurepoint[]> posestructurepoint=new HashMap<>();
    int count=0;
    private String StructureUriStr,videoInputPath;
    private ExecutorService executorService= Executors.newSingleThreadExecutor();
    private LinkedList<String> queue;
    private StringBuilder sb;
    String filename = "yuiop1.txt";
    private FrameExtractor frameExtractor;
    int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video_structure);
        frameExtractor = new FrameExtractor(ShowVideoStructureActivity.this);
        handler.postDelayed(myrunnable, 500);
        findViewById(R.id.layout_Button_ReproducePose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mm產生骨骼();
            }
        });
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
        filename = Tools.md5(StructureUriStr);
        Act_VideoView_Pose.setVideoURI(Uri.parse(StructureUriStr));
        mediaController=new MediaController(this);
        mediaController.setVisibility(View.VISIBLE);
        Act_VideoView_Pose.setMediaController(mediaController);
        sb = new StringBuilder();
        queue = new LinkedList<String>();
//        posestructurepoint = FileMangement.ReadFile(getActivity(), filename, (long) (-0.8*1000*1000));
        posestructurepoint = FileMangement.ReadFile(getActivity(), filename, (long) (0));
        URIPathHelper uriPathHelper = new URIPathHelper();
        Uri uri = Uri.parse(StructureUriStr);
        videoInputPath = uriPathHelper.getPath(getActivity(), uri).toString();
        frameExtractor.init(videoInputPath);
        orientation = frameExtractor.getOrientation(videoInputPath);
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
        switch (v.getId()) {
            case R.id.Layout_Button_VideoStructureShow:
//                int Time=((Act_VideoView_Pose.getCurrentPosition())/1000);
//                Act_GraphicOverlay_ShowVideoStructure.clear();
//
//                Act_GraphicOverlay_ShowVideoStructure.add(
//                        new AnalyzePoseGraphic(
//                                Act_GraphicOverlay_ShowVideoStructure,
//                                posestructurepoint[Time]));
                break;

            case R.id.Layout_Button_VideoStructureEdit:
                Act_VideoView_Pose.pause();
                long currentTimeMicrosecond=(Act_VideoView_Pose.getCurrentPosition() * 1000);
                Intent intent2 = new Intent();
                intent2= new Intent(ShowVideoStructureActivity.this, AdjustVideoStructureActivity.class);
                Bundle objbundle = new Bundle();
                objbundle.putString("uristr_Edit",StructureUriStr);
//                objbundle.putInt("Timecount",Timecount);
                intent2.putExtras(objbundle);
                startActivity(intent2);

                break;

            default:
                break;

        }
    }

    String mm取得對應時間軸之key(long currentTimeMicrosecond)
    {
        boolean isFound = false;
        long distance = Long.MAX_VALUE; //預設極大值
        String wantId = "";
        Set<String> keysOri = posestructurepoint.keySet();
        List<String> keys = new ArrayList<String>();
        keys.addAll(keysOri);
        Collections.sort(keys);
        for (String f : keys) {
            long id = Long.parseLong(f);
            long currentDis = Math.abs(id - currentTimeMicrosecond);
            long upper = (long) (currentTimeMicrosecond + ((1.0 / frameExtractor.getFPS() * 1) * 1000 * 1000));
            long lower = (long) (currentTimeMicrosecond - ((1.0 / frameExtractor.getFPS() * 1) * 1000 * 1000));
            if(currentDis >= lower && currentDis <= upper) //求跟目前影片播放時間距離最近的key，反查出姿態點
            {
                if(currentDis < distance) {
                    distance = currentDis;
                    wantId = f;
                    isFound = true;
                }
            }
//            else if(isFound) {
//                break;
//            }
        }
        return wantId;
    }

    private Runnable myrunnable =new Runnable() {
        @Override
        public void run() {
            long currentTimeMicrosecond=(Act_VideoView_Pose.getCurrentPosition() * 1000);
            String wantId = mm取得對應時間軸之key(currentTimeMicrosecond);
            if(!wantId.equals(""))
            {
                structurepoint[] structurepoints = posestructurepoint.get(wantId);
                Act_GraphicOverlay_ShowVideoStructure.clear();
                Act_GraphicOverlay_ShowVideoStructure.add(new AnalyzePoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints));
            }
            long delay = (long) ((1.0 / frameExtractor.getFPS())*1000);
//            handler.postDelayed(myrunnable, delay);
            handler.postDelayed(myrunnable, delay);
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
}
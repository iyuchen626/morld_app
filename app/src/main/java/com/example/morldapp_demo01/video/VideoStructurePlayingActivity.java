package com.example.morldapp_demo01.video;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ducky.fastvideoframeextraction.fastextraction.Frame;
import com.ducky.fastvideoframeextraction.fastextraction.FrameExtractor;
import com.ducky.fastvideoframeextraction.fastextraction.IVideoFrameExtractor;
import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.Edit.AnalyzePoseGraphic;
import com.example.morldapp_demo01.Edit.FileMangement;
import com.example.morldapp_demo01.Edit.structurepoint;
import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.activity.Base;
import com.example.morldapp_demo01.fastextraction.URIPathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VideoStructurePlayingActivity extends Base implements View.OnClickListener, IVideoFrameExtractor
{

    private FrameExtractor frameExtractor;
    VideoView Act_VideoView_EditPlaying;
    GraphicOverlay Act_GraphicOverlay_ShowVideoStructure;
    MediaController mediaController;
    String filename = "yuiop1.txt";
    int orientation;
    private String StructureUriStr,videoInputPath;
    private LinkedList<String> queue;
    GraphicOverlay Act_GraphicOverlay_EditStructure;
    structurepoint[] structurepoints;
    private HashMap<String,  structurepoint[]> posestructurepoint=new HashMap<>();
    float height,width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_structure_playing);


        frameExtractor = new FrameExtractor(VideoStructurePlayingActivity.this);
        handler.postDelayed(myrunnable, 50);

        Act_VideoView_EditPlaying=findViewById(R.id.Layout_VideoView_ShowPlayingVideo);
        Act_GraphicOverlay_ShowVideoStructure=findViewById(R.id.Layout_GraphicOverlay_ShowplayingVideoStructure);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        StructureUriStr = bundle.getString("uristr_Playing");

        Act_VideoView_EditPlaying.setVideoURI(Uri.parse(StructureUriStr));
        mediaController=new MediaController(getApplicationContext());
        mediaController.setVisibility(View.VISIBLE);
        Act_VideoView_EditPlaying.setMediaController(mediaController);


//        queue = new LinkedList<String>();
//        URIPathHelper uriPathHelper = new URIPathHelper();
//        Uri uri = Uri.parse(StructureUriStr);
//        videoInputPath = uriPathHelper.getPath(getActivity(), uri).toString();
//        frameExtractor.init(videoInputPath);
//        orientation = frameExtractor.getOrientation(videoInputPath);

//        findViewById(R.id.playingdes).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                mm遞減骨骼();
//            }
//        });
//        findViewById(R.id.playingadd).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                mm遞增骨骼();
//            }
//        });
      //  mm讀取骨骼資料();
     //   mm讀取骨骼資料();

    }

        @Override
        public void onClick(View v) {

        }

        @Override
        public void onCurrentFrameExtracted(@NonNull Frame currentFrame) {
           height=currentFrame.getHeight();
            width=currentFrame.getWidth();
        }

        @Override
        public void onAllFrameExtracted(int processedFrameCount, long processedTimeMs) {

        }

    private Runnable myrunnable =new Runnable() {
        @Override
        public void run() {
            long delay = (long) ((1.0 / frameExtractor.getFPS())*1000);
            if(!Act_VideoView_EditPlaying.isPlaying()) {            handler.postDelayed(myrunnable, delay); return;}
            long currentTimeMicrosecond=(Act_VideoView_EditPlaying.getCurrentPosition() * 1000);
            String wantId = mm取得對應時間軸之key(currentTimeMicrosecond);
            if(!wantId.equals(""))
            {
                structurepoints = posestructurepoint.get(wantId);
                Act_GraphicOverlay_ShowVideoStructure.clear();
                Act_GraphicOverlay_ShowVideoStructure.add(new AnalyzePoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,height,width));
            }
            handler.postDelayed(myrunnable, delay / 2);
        }
    };

    void mm遞減骨骼()
    {
        String s = Tools.mmRead(getActivity(), Config.KEY_骨骼時間軸偏差值+filename);
        float offset = 0;
        try
        {
            offset = Float.parseFloat(s);
            offset -= 0.05;
        }
        catch (Exception e)
        {
            offset -= 0.05;
        }
        Tools.toast(getActivity(), "減少偏差值至:"+offset);
        Tools.mmSave(getActivity(), Config.KEY_骨骼時間軸偏差值+filename, String.valueOf(offset));
        mm讀取骨骼資料();
    }

    void mm遞增骨骼()
    {
        String s = Tools.mmRead(getActivity(), Config.KEY_骨骼時間軸偏差值+filename);
        float offset = 0;
        try
        {
            offset = Float.parseFloat(s);
            offset += 0.05;
        }
        catch (Exception e)
        {
            offset += 0.05;
        }
        Tools.toast(getActivity(), "增加偏差值至:"+offset);
        Tools.mmSave(getActivity(), Config.KEY_骨骼時間軸偏差值+filename, String.valueOf(offset));
        mm讀取骨骼資料();
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

    void mm讀取骨骼資料()
    {
        float offset = 0;
        try
        {
            offset = Float.parseFloat(Tools.mmRead(getActivity(), Config.KEY_骨骼時間軸偏差值+filename));
        }
        catch (Exception e)
        {
        }
        posestructurepoint = FileMangement.ReadFile(getActivity(), filename, (long) (offset*1000*1000));
    }



}
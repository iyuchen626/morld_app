package com.example.morldapp_demo01.test;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.morldapp_demo01.Edit.AnalyzePoseGraphic;
import com.example.morldapp_demo01.Edit.FileMangement;
import com.example.morldapp_demo01.Edit.structurepoint;
import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.activity.Base;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

public class Test_EXO_Activity extends Base {

    PlayerView playerView;
    SimpleExoPlayer player;
    Boolean playwhenReady =true;
    int currentwindow =0;
    long playbackPosition=0;

    private String StructureUriStr;
    Bitmap Act_Bitmap_VideoPoseEditor=null,Adjust_Bitmap_ShowVideo=null;
    ImageView Act_ImageView_ShowVideo;

    GraphicOverlay Act_GraphicOverlay_ShowVideoStructure;
    private structurepoint[][] posestructurepoint=new structurepoint[100][12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_exo);

        playerView=(PlayerView)findViewById(R.id.Layout_PlayerView);

       // StructureUriStr= "android.resource:"+getActivity().getPackageName() + R.raw.test_4;
      // retriever.setDataSource(getActivity(), Uri.parse(StructureUriStr));

//        for(int idx=0;idx<30;idx++) {
//            Act_Bitmap_VideoPoseEditor = retriever.getFrameAtTime(idx*1000000);
//            if(Act_Bitmap_VideoPoseEditor==null) continue;
//            Adjust_Bitmap_ShowVideo = StructureAnalyze.Adjust_picture(Act_Bitmap_VideoPoseEditor);
//            Act_ImageView_ShowVideo.setImageBitmap(Adjust_Bitmap_ShowVideo);
//
//            InputImage inputImage = InputImage.fromBitmap(Adjust_Bitmap_ShowVideo, 0);
//            StructureAnalyze.Analyze_Structure(getActivity(), inputImage, Act_GraphicOverlay_ShowVideoStructure, "yuiop1.txt", idx);
//            Act_GraphicOverlay_ShowVideoStructure.clear();
//        }
//
        for (int countidx = 0; countidx < 30; countidx++) {
            posestructurepoint[countidx] = FileMangement.testReadFile(getActivity(), "structure_data.txt", countidx);
        }
//
//        initplayer();
    }

        private void initplayer() {
        player = new SimpleExoPlayer.Builder(getActivity()).build();
        playerView.setPlayer(player);
        File ff= new File("android.resource://com.android/" + R.raw.test_4);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("android.resource://com.android/" + R.raw.test_4));
        if(ff.exists())
        {
            mediaItem = MediaItem.fromUri(ff.getAbsolutePath());
        }

        player.setMediaItem(mediaItem);
        player.setPlayWhenReady(playwhenReady);
        player.seekTo(currentwindow, playbackPosition);
        player.prepare();



    }


        @Override
        protected void onStart()
        {
            super.onStart();
            if (Util.SDK_INT >= 24)
            {
                initplayer();
            }
        }


        @Override
        protected void onStop()
        {
            super.onStop();
            if (Util.SDK_INT >= 24)
            {
                releasePlayer();
            }
        }

        @Override
        protected void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24)
        {
            releasePlayer();
        }
    }

        @Override
        protected void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 ||player==null))
        {
            initplayer();
        }
    }

        private void hideSystemUi() {
        playerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

        private void releasePlayer() {
        if(player!=null) {
            playbackPosition = player.getCurrentPosition();
            currentwindow = player.getCurrentWindowIndex();
            playwhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private Handler handler=new Handler();

    private Runnable myrunnable =new Runnable() {
        @Override
        public void run() {

            long Time=((player.getCurrentPosition()+20)/1000);

            Act_GraphicOverlay_ShowVideoStructure.clear();

            Act_GraphicOverlay_ShowVideoStructure.add(
                    new AnalyzePoseGraphic(
                            Act_GraphicOverlay_ShowVideoStructure,
                            posestructurepoint[Integer.parseInt(String.valueOf(Time))]));

            handler.postDelayed(myrunnable, 980);

            if(Time>10)
            {

                handler.removeCallbacks(this);
            }
        }
    };


}
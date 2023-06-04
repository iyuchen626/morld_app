package com.example.morldapp_demo01.test;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_exo);

        playerView=(PlayerView)findViewById(R.id.Layout_PlayerView);
        //initplayer();
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


}
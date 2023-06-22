package com.example.morldapp_demo01.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.morldapp_demo01.R;

public class EditFinishActivity extends Base {

    VideoView Act_VideoView_ShowPlayingVideo;
    String StructureUriStr;
    MediaController mediaController;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_finish);

        Act_VideoView_ShowPlayingVideo=findViewById(R.id.Layout_VideoView_ShowPlayingVideo);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        StructureUriStr = bundle.getString("uristr_Playing");


        Uri UriStructureEdit= Uri.parse((String)StructureUriStr);

        Act_VideoView_ShowPlayingVideo.setVideoURI(UriStructureEdit);
        mediaController=new MediaController(this);
        mediaController.setVisibility(View.VISIBLE);
        Act_VideoView_ShowPlayingVideo.setMediaController(mediaController);

//        Intent intent=getIntent();
//        Bundle bundle=intent.getExtras();
//        StructureUriStr = bundle.getString("uristr_Playing");
//
//        Act_VideoView_EditPlaying.setVideoURI(Uri.parse(StructureUriStr));
//        mediaController=new MediaController(getApplicationContext());
//        mediaController.setVisibility(View.VISIBLE);
//        Act_VideoView_EditPlaying.setMediaController(mediaController);

    }
}
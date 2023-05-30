package com.example.morldapp_demo01.video;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.activity.Base;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionDeniedResponse;
//import com.karumi.dexter.listener.PermissionGrantedResponse;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.single.PermissionListener;

public class video_editor_time extends Base
{

    private VideoView ViewVideo_Player;
    MediaController mediaController;
    private String input_video_path;
    private final int PICK_IMAGE = 100;
    long timestamp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_editor_time);

        Bundle objgetbundle = this.getIntent().getExtras();
        timestamp = objgetbundle.getLong("video_name");

        ViewVideo_Player=findViewById(R.id.VideView_Trimvideo);
        mediaController=new MediaController(this);

        input_video_path= Environment.getExternalStorageDirectory().getPath();
        input_video_path=input_video_path+"/Movies/"+timestamp+".mp4";
        ViewVideo_Player.setVideoURI(Uri.parse(input_video_path));
        mediaController.setMediaPlayer(ViewVideo_Player);

//        Intent intent2 = new Intent();
//        intent2= new Intent(video_editor_time.this, PoseDetectorEdit.class);
//
//        Bundle objbundle = new Bundle();
//        objbundle.putLong("video_name",timestamp);
//        intent2.putExtras(objbundle);
//        startActivity(intent2);


        //ViewVideo_Player.start();

        //checkP();


    }


}
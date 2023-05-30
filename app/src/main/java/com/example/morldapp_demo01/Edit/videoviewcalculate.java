package com.example.morldapp_demo01.Edit;

import android.view.ViewGroup;
import android.widget.VideoView;

public class videoviewcalculate {

    public static void videoviewadjust(VideoView videoView, float newVideoWidht, float newVideoHeight) {


        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width = (int) newVideoWidht;
        layoutParams.height = (int) newVideoHeight;
        videoView.setLayoutParams(layoutParams);

    }

}

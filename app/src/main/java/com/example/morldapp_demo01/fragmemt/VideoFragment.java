package com.example.morldapp_demo01.fragmemt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.morldapp_demo01.FunctionChooseActivity;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.camera.VideoRecordingActivity;
import com.example.morldapp_demo01.video.PoseDetectorEdit;


public class VideoFragment extends Fragment {
    private View mview;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview=inflater.inflate(R.layout.fragment_video,container,false);
        Intent intent = new Intent();
        //intent= new Intent(this.getActivity(), FunctionChooseActivity.class);
        intent= new Intent(this.getActivity(), VideoRecordingActivity.class);
        startActivity(intent);
        return mview;
    }
}

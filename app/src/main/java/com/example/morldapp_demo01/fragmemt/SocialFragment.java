package com.example.morldapp_demo01.fragmemt;



import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.morldapp_demo01.MainActivity;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.camera.VideoRecordingActivity;

public class SocialFragment extends Fragment{
    private View mview;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview=inflater.inflate(R.layout.fragment_social,container,false);
        return mview;
    }
}



package com.example.morldapp_demo01.fragmemt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.morldapp_demo01.R;

public class ＭarketFragment extends Fragment{
    private View mview;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview=inflater.inflate(R.layout.fragment_home,container,false);
        return mview;
    }
}



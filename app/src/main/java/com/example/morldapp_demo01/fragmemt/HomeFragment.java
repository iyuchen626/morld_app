package com.example.morldapp_demo01.fragmemt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.databinding.FragmentHomeBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment{
    private View mview;
    FragmentHomeBinding binding;
    ImageButton Activity_ImgBtnShortVideo1,Activity_ImgBtnShortVideo2,Activity_ImgBtnShortVideo3,Activity_ImgBtnShortVideo4,Activity_ImgBtnShortVideo5,Activity_ImgBtnShortVideo6;
    ImageButton Activity_ImgBtnTop1,Activity_ImgBtnTop2,Activity_ImgBtnTop3,Activity_ImgBtnTop4,Activity_ImgBtnTop5,Activity_ImgBtnTop6,Activity_ImgBtnTop7,Activity_ImgBtnTop8,Activity_ImgBtnTop9,Activity_ImgBtnTop10;
    TextView Activity_TxtViewShortVideo1;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        binding.LayoutLinearLayoutAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getActivity(), Login.class));
            }
        });
        mview=binding.getRoot();
        Activity_ImgBtnShortVideo1 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Morldment_1));
        //Activity_ImgBtnShortVideo1.setImageDrawable(getResources().getDrawable(R.drawable.shortvideo));
        Activity_ImgBtnShortVideo2 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Morldment_2));
        //Activity_ImgBtnShortVideo2.setImageDrawable(getResources().getDrawable(R.drawable.shortvideo));
        Activity_ImgBtnShortVideo3 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Morldment_3));
        //Activity_ImgBtnShortVideo3.setImageDrawable(getResources().getDrawable(R.drawable.shortvideo));
        Activity_ImgBtnShortVideo4 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Morldment_4));
        //Activity_ImgBtnShortVideo4.setImageDrawable(getResources().getDrawable(R.drawable.shortvideo));
        Activity_ImgBtnShortVideo5 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Morldment_5));
        //Activity_ImgBtnShortVideo5.setImageDrawable(getResources().getDrawable(R.drawable.shortvideo));
        Activity_ImgBtnShortVideo6 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Morldment_6));
        //Activity_ImgBtnShortVideo6.setImageDrawable(getResources().getDrawable(R.drawable.shortvideo));


        Activity_ImgBtnTop1 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_1));
        Activity_ImgBtnTop1.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop2 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_2));
        Activity_ImgBtnTop2.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop3 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_3));
        Activity_ImgBtnTop3.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop4 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_4));
        Activity_ImgBtnTop4.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop5 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_5));
        Activity_ImgBtnTop5.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop6 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_6));
        Activity_ImgBtnTop6.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop7 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_7));
        Activity_ImgBtnTop7.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop8 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_8));
        Activity_ImgBtnTop8.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop9 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_9));
        Activity_ImgBtnTop9.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        Activity_ImgBtnTop10 = (ImageButton)(mview.findViewById(R.id.ImgBtn_Top_10));
        Activity_ImgBtnTop10.setImageDrawable(getResources().getDrawable(R.drawable.fregmenthome_ad));
        return mview;
    }
}



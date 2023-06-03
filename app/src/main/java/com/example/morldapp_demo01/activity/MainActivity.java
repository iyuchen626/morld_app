package com.example.morldapp_demo01.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.camera.VideoRecordingActivity;
import com.example.morldapp_demo01.fragmemt.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends Base
{

        //ActivityMainBinding binding;
       // @SuppressLint("NonConstantResourceId")
    BottomNavigationView BtnNavViewMain;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //binding=ActivityMainBinding.inflate(getLayoutInflater());
            //setContentView(binding.getRoot());
            setContentView(R.layout.activity_main);

            repleceFragment(new HomeFragment());
            BtnNavViewMain=(BottomNavigationView)findViewById(R.id.BtnNavView_Main);

            BtnNavViewMain.setOnItemSelectedListener(item -> {

                switch (item.getItemId()){

                    case R.id.BtnNavView_Main_BtnHome:
                        repleceFragment(new HomeFragment());
                        break;

                    case R.id.BtnNavView_Main_BtnEditor:
                        //repleceFragment(new VideoFragment());
                        //finish();
                        HomeEditor_Dialog();
                        break;

                }

                return true;
            });


//            binding.BtnNavViewMain.setOnItemSelectedListener(item -> {
//
//                switch (item.getItemId()){
//
//                    case R.id.BtnNavView_Main_BtnHome:
//                        repleceFragment(new HomeFragment());
//                        break;
//
//                    case R.id.BtnNavView_Main_BtnEditor:
//                        repleceFragment(new VideoFragment());
//                        break;
//
//                }
//
//                return true;
//            });
        }

        private void repleceFragment(Fragment fragment) {
            FragmentManager fragmentManager;
            FragmentTransaction fragmentTransaction;
            fragmentManager = getSupportFragmentManager();//抽象類，負責一系列對fragment的操作，getChildFragmentManager()可獲得子級fragment的引用，parent則是getParentFragmentManager()。
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.FrameLayout_Main,fragment);
            fragmentTransaction.commit();
        }


    void HomeEditor_Dialog()
    {
        BottomSheetDialog Editor_dialog=new BottomSheetDialog(this.getActivity());
        View view=getLayoutInflater().inflate(R.layout.dialog_editor,null);
        Editor_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {


            }
        });
        Editor_dialog.setContentView(view);

        LinearLayout Act_Layout_Edit=Editor_dialog.findViewById(R.id.Layout_Layout_Edit);
        LinearLayout Act_Layout_Morldment=Editor_dialog.findViewById(R.id.Layout_Layout_Morldment);


        Act_Layout_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                //intent= new Intent(this.getActivity(), FunctionChooseActivity.class);
                intent= new Intent(MainActivity.this, VideoRecordingActivity.class);
                startActivity(intent);

                finish();

            }
        });


        Act_Layout_Morldment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                //intent= new Intent(this.getActivity(), FunctionChooseActivity.class);
                intent= new Intent(MainActivity.this, VideoRecordingActivity.class);
                startActivity(intent);

                finish();

            }
        });


        Editor_dialog.show();



    }

    }
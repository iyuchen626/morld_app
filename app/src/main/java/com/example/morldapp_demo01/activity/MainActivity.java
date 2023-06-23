package com.example.morldapp_demo01.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.camera.VideoRecordingActivity;
import com.example.morldapp_demo01.event.SearchVideoEvent;
import com.example.morldapp_demo01.fragmemt.HomeFragment;
import com.example.morldapp_demo01.mirror.Client;
import com.example.morldapp_demo01.mirror.ScreenService;
import com.example.morldapp_demo01.pojo.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends Base
{
    BottomNavigationView BtnNavViewMain;
    private MediaProjectionManager mediaProjectionManager;
    private static final int PROJECTION_REQUEST_CODE = 1001;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView userNmae;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            //binding=ActivityMainBinding.inflate(getLayoutInflater());
            //setContentView(binding.getRoot());
            setContentView(R.layout.main);
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if(item.getItemId() == R.id.nav_channel)
                {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                    startActivity(new Intent(getActivity(), MyVideo.class));
                    return true;
                }
                return false;
            }
        });
            userNmae = navigationView.getHeaderView(0).findViewById(R.id.name);
            navigationView.getHeaderView(0).findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                    startActivity(new Intent(getActivity(), Login.class));
                }
            });
            findViewById(R.id.imageView10).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            });
            findViewById(R.id.imageView9).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    startActivity(new Intent(getActivity(), SearchVideo.class));
                }
            });
            findViewById(R.id.media_route_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("選擇類型");
                    String[] animals = {"投影", "客戶端"};
                    builder.setItems(animals, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case 0:
                                    startProjection();
//                                    startActivity(new Intent(getActivity(), ScreenShotActivity.class));
                                    break;
                                case 1:
                                    startActivity(new Intent(getActivity(), Client.class));
                                    break;
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            repleceFragment(new HomeFragment());
            BtnNavViewMain=(BottomNavigationView)findViewById(R.id.BtnNavView_Main);

            BtnNavViewMain.setOnItemSelectedListener(item -> {

                switch (item.getItemId()){

                    case R.id.BtnNavView_Main_BtnHome:
                        repleceFragment(new HomeFragment());
                        break;

                    case R.id.BtnNavView_Main_BtnEditor:
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
        Editor_dialog.findViewById(R.id.Layout_Layout_media).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), MyDownloadVideo.class));
            }
        });

        Act_Layout_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
//                intent= new Intent(getActivity(), FunctionChooseActivity.class);
                intent= new Intent(MainActivity.this, VideoRecordingActivity.class);
                startActivity(intent);
            }
        });


        Act_Layout_Morldment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //intent= new Intent(this.getActivity(), FunctionChooseActivity.class);
                intent= new Intent(MainActivity.this, VideoRecordingActivity.class);
                startActivity(intent);
            }
        });


        Editor_dialog.show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        User user = user();
        if(user != null) {
            userNmae.setText("已登入");
        }
    }

    // 请求开始录屏
    private void startProjection()
    {
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, PROJECTION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            return;
        }
        if (requestCode == PROJECTION_REQUEST_CODE)
        {
            Intent service = new Intent(this, ScreenService.class);
            service.putExtra("code", resultCode);
            service.putExtra("data", data);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                startForegroundService(service);
            }
            else
            {
                startService(service);
            }
        }
    }

    void mm搜尋()
    {
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("搜尋影片")
                .setMessage("請輸入標題關鍵字")
                .setView(taskEditText)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        EventBus.getDefault().post(new SearchVideoEvent(task));
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
    }
}
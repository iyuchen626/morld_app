package com.example.morldapp_demo01.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.morldapp_demo01.Edit.ShowVideoStructureActivity;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;

public class StartActivity extends Base
{

    private Long startTime;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Tools.mm請求所有必要權限(getActivity(), new Tools.OnPermissionListener() {
                    @Override
                    public void onGranted()
                    {
                        finish();
                        startActivity(new Intent(getActivity(), MainActivity.class));
//                        Bundle bundle = new Bundle();
//                        bundle.putString("urivideostr","content://com.android.providers.media.documents/document/video%3A42");
//                        startActivity(new Intent(getActivity(), ShowVideoStructureActivity.class).putExtras(bundle));
                    }

                    @Override
                    public void onDenied()
                    {
                    }
                });
            }
        }, 2000);

    }
}
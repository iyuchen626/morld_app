package com.example.morldapp_demo01.fragmemt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.pojo.LoginRequest;
import com.example.morldapp_demo01.pojo.RegisterRequest;
import com.example.morldapp_demo01.pojo.RegisterResponse;
import com.example.morldapp_demo01.retrofit2.ApiStrategy;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment{
    private View mview;
    private GoogleApiClient client;
    private Executor localExecutor = Executors.newSingleThreadExecutor();
    ImageButton Activity_ImgBtnShortVideo1,Activity_ImgBtnShortVideo2,Activity_ImgBtnShortVideo3,Activity_ImgBtnShortVideo4,Activity_ImgBtnShortVideo5,Activity_ImgBtnShortVideo6;
    ImageButton Activity_ImgBtnTop1,Activity_ImgBtnTop2,Activity_ImgBtnTop3,Activity_ImgBtnTop4,Activity_ImgBtnTop5,Activity_ImgBtnTop6,Activity_ImgBtnTop7,Activity_ImgBtnTop8,Activity_ImgBtnTop9,Activity_ImgBtnTop10;
    TextView Activity_TxtViewShortVideo1;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();
        mview=inflater.inflate(R.layout.fragment_home,container,false);
        mview.findViewById(R.id.Layout_ImageView_AD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("動作");
                String[] animals = {"投放", "API註冊", "API登入"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent("android.settings.CAST_SETTINGS"));
                                break;
                            case 1:
                                test註冊();
                                break;
                            case 2:
                                test登入();
                                break;
                            case 3: // sheep
                            case 4: // goat
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


//                MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
//
//                movieMetadata.putString(MediaMetadata.KEY_TITLE, "標題");
//                movieMetadata.addImage(new WebImage(Uri.parse("https://images.unsplash.com/photo-1575936123452-b67c3203c357?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8aW1hZ2V8ZW58MHx8MHx8&w=1000&q=80")));
//                movieMetadata.addImage(new WebImage(Uri.parse("https://www.photoshopbuzz.com/wp-content/uploads/change-color-part-of-image-psd16.png")));
//
//                MediaInfo mediaInfo = new MediaInfo.Builder("https://download.samplelib.com/mp4/sample-30s.mp4")
//                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
//                        .setContentType("videos/mp4")
//                        .setMetadata(movieMetadata)
////						.setStreamDuration(mSelectedMedia.getDuration() * 1000)
//                        .build();
//                RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
//                if(remoteMediaClient ==null) return;;
//                remoteMediaClient.load(new MediaLoadRequestData.Builder().setMediaInfo(mediaInfo).build());
            }
        });
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

    void test註冊()
    {
        RegisterRequest model = new RegisterRequest();
        model.email = UUID.randomUUID().toString();
        Disposable disposable = ApiStrategy.getApiService().mm註冊(model).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RegisterResponse>()
        {
            @Override
            public void accept(RegisterResponse res) throws Exception
            {
                Tools.showInfo((AppCompatActivity) getActivity(), "結果", res.message);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                Tools.toast(getActivity(), "網路錯誤!");
            }
        });
    }

    void test登入()
    {
        LoginRequest model = new LoginRequest();
        model.email = UUID.randomUUID().toString();
        model.captcha = "123";
        Disposable disposable = ApiStrategy.getApiService().mmd登入(model).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RegisterResponse>()
        {
            @Override
            public void accept(RegisterResponse res) throws Exception
            {
                Tools.showInfo((AppCompatActivity) getActivity(), "結果", res.message);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                Tools.toast(getActivity(), "網路錯誤!");
            }
        });
    }


//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
//    }

    private RemoteMediaClient getRemoteMediaClient() {
        CastSession castSession =
                CastContext.getSharedInstance(getContext(),localExecutor)
                        .getResult()
                        .getSessionManager()
                        .getCurrentCastSession();
        if (castSession != null && castSession.isConnected()) {
            return castSession.getRemoteMediaClient();
        }
        return null;
    }
}



package com.example.morldapp_demo01.Edit;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ducky.fastvideoframeextraction.fastextraction.Frame;
import com.ducky.fastvideoframeextraction.fastextraction.FrameExtractor;
import com.ducky.fastvideoframeextraction.fastextraction.IVideoFrameExtractor;
import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.Tools;
import com.example.morldapp_demo01.activity.Base;
import com.example.morldapp_demo01.activity.EditFinishActivity;
import com.example.morldapp_demo01.activity.MainActivity;
import com.example.morldapp_demo01.camera.VideoRecordingActivity;
import com.example.morldapp_demo01.fastextraction.URIPathHelper;
import com.example.morldapp_demo01.fastextraction.Utils;
import com.example.morldapp_demo01.pojo.TxtConfigPOJO;
import com.example.morldapp_demo01.pojo.UploadVideoResponsePOJO;
import com.example.morldapp_demo01.retrofit2.ApiStrategy;
import com.example.morldapp_demo01.video.VideoStructurePlayingActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.io.ByteStreams;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ShowVideoStructureActivity extends Base implements View.OnClickListener, IVideoFrameExtractor
{

    VideoView Act_VideoView_Pose;
    MediaController mediaController;
    ImageView Act_ImageView_ShowVideo;
    GraphicOverlay Act_GraphicOverlay_ShowVideoStructure;
    ImageButton Act_ImgButton_VideoStructureEdit,Act_ImgButton_video_StructureSave;
    ImageButton Act_ImgImgButton_video_StructureEditUp,Act_ImgImgButton_video_StructureEditDown,Act_ImgImgButton_video_StructureEditLeft,Act_ImgImgButton_video_StructureEditRight;
    private HashMap<String,  structurepoint[]> posestructurepoint=new HashMap<>();
    int count=0;
    private String StructureUriStr;
    private ExecutorService executorService= Executors.newSingleThreadExecutor();
    private StringBuilder sb;
    String filename = "yuiop1.txt";
    private FrameExtractor frameExtractor;
    int orientation;
    int lastFindIndex = 0;
    private Boolean structure_show=false;
    int PointIdx = 0;
    Boolean EditStructureFlag = false;
    structurepoint[] structurepoints;
    private float original_y, original_x,height,width;
    boolean is設定旋轉與鏡像 = false;
    int degress = 0;
    boolean isFlip = false;
    long currentTime;
    Boolean Timeout=false;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video_structure);
        frameExtractor = new FrameExtractor(ShowVideoStructureActivity.this);
        handler.postDelayed(myrunnable, 50);
        findViewById(R.id.layout_Button_ReproducePose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                is設定旋轉與鏡像 = false;
                currentTime=System.currentTimeMillis();
                mm產生骨骼();
                Act_VideoView_Pose.pause();
            }
        });
        findViewById(R.id.layout_ImgButton_video_StructureBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        Act_VideoView_Pose=findViewById(R.id.Layout_VideoView_ShowVideo);
        Act_ImageView_ShowVideo=findViewById(R.id.Layout_ImageView_ShowVideo);
        Act_GraphicOverlay_ShowVideoStructure=findViewById(R.id.Layout_GraphicOverlay_ShowVideoStructure);
        Act_ImgButton_VideoStructureEdit=findViewById(R.id.layout_ImgButton_video_StructureEdit);
        Act_ImgButton_video_StructureSave=findViewById(R.id.layout_ImgButton_video_StructureSave);


        Act_ImgImgButton_video_StructureEditUp=findViewById(R.id.layout_ImgButton_video_StructureEditUp);
        Act_ImgImgButton_video_StructureEditLeft=findViewById(R.id.layout_ImgButton_video_StructureEditLeft);
        Act_ImgImgButton_video_StructureEditRight=findViewById(R.id.layout_ImgButton_video_StructureEditRight);
        Act_ImgImgButton_video_StructureEditDown=findViewById(R.id.layout_ImgButton_video_StructureEditDown);

        Act_ImgButton_VideoStructureEdit.setOnClickListener(this);
        Act_ImgButton_video_StructureSave.setOnClickListener(this);

        Act_ImgImgButton_video_StructureEditUp.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditLeft.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditRight.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditDown.setOnClickListener(this);


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        StructureUriStr = bundle.getString("urivideostr");
        filename = Tools.md5(StructureUriStr);
        Act_VideoView_Pose.setVideoPath(StructureUriStr);
        mediaController=new MediaController(this);
        mediaController.setVisibility(View.VISIBLE);
        Act_VideoView_Pose.setMediaController(mediaController);
        frameExtractor.init(StructureUriStr);
        orientation = frameExtractor.getOrientation(StructureUriStr);
        findViewById(R.id.des).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mm遞減骨骼();

            }
        });
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mm遞增骨骼();
            }
        });

    }


    void mm讀取骨骼資料()
    {
        float offset = 0;
        try
        {
            offset = Float.parseFloat(Tools.mmRead(getActivity(), Config.KEY_骨骼時間軸偏差值+filename));
        }
        catch (Exception e)
        {
            Log.e(Config.TAG, e.getMessage());
        }


        posestructurepoint = FileMangement.ReadFile(getActivity(), filename, (long) (offset*1000*1000));

        TxtConfigPOJO txt = Tools.getGson().fromJson(Tools.mmRead(getActivity(), Config.KEY_TXT_CONFIG), TxtConfigPOJO.class);
        if(txt!=null) {
            width = txt.width;
            height = txt.height;
        }

        if((posestructurepoint.size()==0)||(width==0)||(height==0))
        {
            Tools.toast(getActivity(), "骨骼分析失敗..... 請重新分析 "+posestructurepoint.size()+","+width+","+height);
            //mm產生骨骼();
        }
    }

    void mm遞減骨骼()
    {

        String s = Tools.mmRead(getActivity(), Config.KEY_骨骼時間軸偏差值+filename);
        float offset = 0;
        try
        {
            offset = Float.parseFloat(s);
            offset -= 0.05;
        }
        catch (Exception e)
        {
            offset -= 0.05;
        }
        Tools.toast(getActivity(), "減少偏差值至:"+offset);
        Tools.mmSave(getActivity(), Config.KEY_骨骼時間軸偏差值+filename, String.valueOf(offset));
        mm讀取骨骼資料();
    }

    void mm遞增骨骼()
    {
        String s = Tools.mmRead(getActivity(), Config.KEY_骨骼時間軸偏差值+filename);
        float offset = 0;
        try
        {
            offset = Float.parseFloat(s);
            offset += 0.05;
        }
        catch (Exception e)
        {
            offset += 0.05;
        }
        Tools.toast(getActivity(), "增加偏差值至:"+offset);
        Tools.mmSave(getActivity(), Config.KEY_骨骼時間軸偏差值+filename, String.valueOf(offset));
        mm讀取骨骼資料();
    }

    void mm上傳影片(final String title,final String desc) throws IOException
    {
        Tools.showProgress(getActivity(), "上傳中");
        File videoInputFile = new File(StructureUriStr);
        InputStream is = new FileInputStream(videoInputFile);
        byte[] inputData = ByteStreams.toByteArray(is);
        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), inputData);
        MultipartBody.Part multipartFilm = MultipartBody.Part.createFormData("film", videoInputFile.getName(), requestFile);

        Utils.urlToBytes("https://picsum.photos/seed/"+ System.currentTimeMillis() + "/500/500", new Utils.OnDownloadDoneListener() {
            @Override
            public void onDone(byte[] bb)
            {
                RequestBody requestFile2 = RequestBody.create(MediaType.parse("*/*"), bb);
                MultipartBody.Part multipartImage = MultipartBody.Part.createFormData("present_img", "f.jpg", requestFile2);
                RequestBody requestFile3 = RequestBody.create(MediaType.parse("*/*"), FileMangement.ReadFile(getActivity(), filename));
                MultipartBody.Part multipartTxt = MultipartBody.Part.createFormData("film_txt", "f.txt", requestFile3);
                RequestBody title_ = RequestBody.create(MediaType.parse("text/plain"), title);
                RequestBody desc_ = RequestBody.create(MediaType.parse("text/plain"), desc);
                RequestBody film_type_id = RequestBody.create(MediaType.parse("text/plain"), "1");
                RequestBody sell = RequestBody.create(MediaType.parse("text/plain"), "1");
                RequestBody public_ = RequestBody.create(MediaType.parse("text/plain"), "1");
                Disposable disposable = ApiStrategy.getApiService().mm上傳個人影片(multipartFilm, multipartImage, multipartTxt, title_,desc_, film_type_id,sell,public_).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String res) throws Exception
                    {
                        Tools.hideProgress(getActivity());
                        if (res.equals(""))
                        {
                            Tools.showError(getActivity(), "請登入才能上傳影片");
                            return;
                        }
//
                        JSONObject data = new JSONObject(res);
                        if (data.getString("error").equals(""))
                        {
                            UploadVideoResponsePOJO uploadVideoResponsePOJO = Tools.getGson().fromJson(res, UploadVideoResponsePOJO.class);

                            Tools.showInfo(getActivity(), "已上傳", uploadVideoResponsePOJO.data.uuid,  new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent =new Intent(ShowVideoStructureActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });





                            return;
                        }

                        JSONObject app_error = data.getJSONObject("error");
                        while (app_error.keys().hasNext())
                        {
                            String key = app_error.keys().next();
                            if (app_error.get(key) instanceof JSONArray)
                            {
                                JSONArray arr = app_error.getJSONArray(key);
                                Tools.showError(getActivity(), arr.getString(0));
                                return;
                            }
                        }

                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        Tools.hideProgress(getActivity());
                        Tools.toast(getApplicationContext(), throwable.getMessage());
                    }
                });
            }
        });

    }

    void mm產生骨骼()
    {
        Tools.mmSave(getActivity(), Config.KEY_骨骼時間軸偏差值+filename, "");
        Tools.showProgress(getActivity(), "生成骨骼中");
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    File videoInputFile = new File(StructureUriStr);
                    frameExtractor.extractFrames(videoInputFile.getAbsolutePath());
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Act_GraphicOverlay_ShowVideoStructure.clear();
        switch (v.getId()) {
            case R.id.layout_ImgButton_video_StructureSave:
                if(EditStructureFlag==false) {
                    HomeEditor_Dialog();
                }
                else
                {
                    Act_ImgImgButton_video_StructureEditUp.setVisibility(View.INVISIBLE);
                    Act_ImgImgButton_video_StructureEditLeft.setVisibility(View.INVISIBLE);
                    Act_ImgImgButton_video_StructureEditRight.setVisibility(View.INVISIBLE);
                    Act_ImgImgButton_video_StructureEditDown.setVisibility(View.INVISIBLE);
                    EditStructureFlag=false;
                    Act_GraphicOverlay_ShowVideoStructure.setVisibility(View.INVISIBLE);

                }
                break;

            case R.id.layout_ImgButton_video_StructureEdit:
                Structure_Dialog();
                Act_ImgImgButton_video_StructureEditUp.setVisibility(View.INVISIBLE);
                Act_ImgImgButton_video_StructureEditLeft.setVisibility(View.INVISIBLE);
                Act_ImgImgButton_video_StructureEditRight.setVisibility(View.INVISIBLE);
                Act_ImgImgButton_video_StructureEditDown.setVisibility(View.INVISIBLE);
                break;

            case R.id.layout_ImgButton_video_StructureEditUp:
                original_y=structurepoints[PointIdx].getStructpoint_y();
                structurepoints[PointIdx].setStructpoint_y(original_y-20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx,height,width));
                break;

            case R.id.layout_ImgButton_video_StructureEditLeft:
                original_x=structurepoints[PointIdx].getStructpoint_x();
                structurepoints[PointIdx].setStructpoint_x(original_x-20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx,height,width));
                break;

            case R.id.layout_ImgButton_video_StructureEditRight:
                original_x=structurepoints[PointIdx].getStructpoint_x();
                structurepoints[PointIdx].setStructpoint_x(original_x+20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx,height,width));
                break;

            case R.id.layout_ImgButton_video_StructureEditDown:
                original_y=structurepoints[PointIdx].getStructpoint_y();
                structurepoints[PointIdx].setStructpoint_y(original_y+20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx,height,width));

                break;


            default:
                break;

        }
    }

    private Runnable myrunnable =new Runnable() {
        @Override
        public void run() {
            long delay = (long) ((1.0 / frameExtractor.getFPS())*1000);
            if(!Act_VideoView_Pose.isPlaying()) {            handler.postDelayed(myrunnable, delay); return;}
            long currentTimeMicrosecond=(Act_VideoView_Pose.getCurrentPosition() * 1000);
            String wantId = Tools.mm取得對應時間軸之key(posestructurepoint, currentTimeMicrosecond);
            if(!wantId.equals(""))
            {
                structurepoints = posestructurepoint.get(wantId);
                Act_GraphicOverlay_ShowVideoStructure.clear();
                Act_GraphicOverlay_ShowVideoStructure.add(new AnalyzePoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,height,width));
            }
            handler.postDelayed(myrunnable, delay / 2);
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacks(myrunnable);
    }

    void mm校正流程(Frame currentFrame)
    {
        Tools.showQuestion(getActivity(), "校正", "請問畫面是否鏡像?", "是", "否", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFlip = true;
            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFlip = false;
            }
        });
    }

    @Override
    public void onCurrentFrameExtracted(@NonNull Frame currentFrame)
    {

        Bitmap imageBitmap = Utils.fromBufferToBitmap(currentFrame.getByteBuffer(), currentFrame.getWidth(), currentFrame.getHeight(), degress, isFlip);
        if(frameExtractor.isPause()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if(!is設定旋轉與鏡像)
                {
                    sb.append(currentFrame.getWidth()+","+currentFrame.getHeight()+"\n");
                    //Act_ImageView_ShowVideo.setImageBitmap(imageBitmap);
                    //Act_ImageView_ShowVideo.setVisibility(View.VISIBLE);
                    frameExtractor.setPause(true);
                    mm校正流程(currentFrame);
                    //Act_ImageView_ShowVideo.setVisibility(View.INVISIBLE);
                    frameExtractor.setPause(false);
                    is設定旋轉與鏡像 = true;
                }
                Tools.showProgress(getActivity(), "生成骨骼中\n" + currentFrame.getPosition());
            }
        });
        if(!is設定旋轉與鏡像) return;
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);
        queue.offer(String.valueOf(currentFrame.getTimestamp()));
        StructureAnalyze.Analyze_Structure(inputImage, new StructureAnalyze.OnAnalyzeStructureListener()
        {
            @Override
            public void onDone(String result)
            {
                synchronized (queue)
                {
                    long now=System.currentTimeMillis()-currentTime;
                    long second=now/1000;

                    if(second>((Act_VideoView_Pose.getDuration())*3))
                    {

                        if(Timeout==false) {
                            Timeout=true;
                            Tools.showQuestion(getActivity(), "Timeout", "分析失敗可能為影片轉碼問題，請將影片轉碼再次上傳，感謝您的配合！", "回到首頁", "選擇其他影片", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent =new Intent(ShowVideoStructureActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                        }

                    }
                    else
                    {
                        queue.poll();
                        Log.i(Config.TAG, "queue size=" + queue.size() + " currentFrame=" + currentFrame.getTimestamp());
                        if (!result.equals(""))
                            sb.append(currentFrame.getTimestamp() + "#" + result);
                        if (queue.size() == 0) {
                            FileMangement.SaveFile(getActivity(), filename, sb.toString());
                            Tools.hideProgress(getActivity());
                            //Tools.toastSuccess(getActivity(), "骨骼已儲存txt");
                            Tools.toastSuccess(getActivity(), "骨骼已儲存");
                            mm讀取骨骼資料();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onAllFrameExtracted(int processedFrameCount, long processedTimeMs)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                DecimalFormat f = new DecimalFormat("#0.00");
                String d = f.format(processedTimeMs / 1000.0);
                Tools.toastSuccess(getActivity(), "骨骼已運算生成，費時:" + d + "秒");
            }
        });
    }

    void HomeEditor_Dialog()
    {
        mm讀取骨骼資料();
        Dialog Editor_dialog=new Dialog(this.getActivity());
        View view=getLayoutInflater().inflate(R.layout.dialog_editor_save,null);
        EditText editTextTextTitle = view.findViewById(R.id.editTextTextTitle);
        EditText editTextTextContent = view.findViewById(R.id.editTextTextContent);

        Editor_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });

        Editor_dialog.setContentView(view);
        Button Act_Button_Cancel=Editor_dialog.findViewById(R.id.button＿cancel);
        Button Act_Button_Finish=Editor_dialog.findViewById(R.id.button_finish);

        Act_Button_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    Editor_dialog.dismiss();
                    mm上傳影片(editTextTextTitle.getText().toString(),editTextTextContent.getText().toString());


                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }



            }
        });
        Act_Button_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editor_dialog.dismiss();
            }
        });
        Editor_dialog.show();
    }

    void Structure_Dialog()
    {
        Dialog Editor_dialog=new Dialog(this.getActivity());
        View view=getLayoutInflater().inflate(R.layout.dialog_structure_option,null);
        Editor_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {


            }
        });
        Editor_dialog.setContentView(view);

        Window window = Editor_dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 3;
        params.y = 250;
        window.setAttributes(params);

        ImageButton Act_Button_Structure_Show=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_Show);
        ImageButton Act_Button_Structure_NotShow=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_NotShow);
        ImageButton Act_Button_Structure_AdjustPostion=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_Adjust_Postion);


        Act_Button_Structure_Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posestructershow();
                Act_GraphicOverlay_ShowVideoStructure.setVisibility(View.VISIBLE);
                Editor_dialog.dismiss();
            }
        });

        Act_Button_Structure_NotShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Act_GraphicOverlay_ShowVideoStructure.setVisibility(View.INVISIBLE);
                Editor_dialog.dismiss();
            }
        });

        Act_Button_Structure_AdjustPostion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Act_GraphicOverlay_ShowVideoStructure.setVisibility(View.VISIBLE);
                Editor_dialog.dismiss();
                Poistion_Dialog();
            }
        });
        Editor_dialog.show();
    }

    void Poistion_Dialog()
    {
        EditStructureFlag=true;
        Act_VideoView_Pose.pause();

        BottomSheetDialog Editor_dialog = new BottomSheetDialog(this.getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_editor_pointchoose, null);
        Button Act_Button_Structure_Point_0, Act_Button_Structure_Point_1, Act_Button_Structure_Point_2, Act_Button_Structure_Point_3, Act_Button_Structure_Point_4, Act_Button_Structure_Point_5, Act_Button_Structure_Point_6, Act_Button_Structure_Point_7, Act_Button_Structure_Point_8, Act_Button_Structure_Point_9, Act_Button_Structure_Point_10, Act_Button_Structure_Point_11;

        Editor_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Editor_dialog.dismiss();

            }
        });

        Editor_dialog.setContentView(view);

        Act_Button_Structure_Point_0 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_0);
        Act_Button_Structure_Point_1 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_1);
        Act_Button_Structure_Point_2 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_2);
        Act_Button_Structure_Point_3 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_3);
        Act_Button_Structure_Point_4 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_4);
        Act_Button_Structure_Point_5 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_5);
        Act_Button_Structure_Point_6 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_6);
        Act_Button_Structure_Point_7 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_7);
        Act_Button_Structure_Point_8 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_8);
        Act_Button_Structure_Point_9 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_9);
        Act_Button_Structure_Point_10 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_10);
        Act_Button_Structure_Point_11 = Editor_dialog.findViewById(R.id.layout_Button_Structure_video_Point_11);

        Act_Button_Structure_Point_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=0;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });

        Act_Button_Structure_Point_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=1;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=2;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=3;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=4;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=5;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=6;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=7;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=8;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=9;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=10;
                poistioneditshow();
                Editor_dialog.dismiss();
            }
        });
        Act_Button_Structure_Point_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointIdx=11;
                poistioneditshow();
                Editor_dialog.dismiss();

            }
        });
        Act_ImgImgButton_video_StructureEditUp.setVisibility(View.VISIBLE);
        Act_ImgImgButton_video_StructureEditLeft.setVisibility(View.VISIBLE);
        Act_ImgImgButton_video_StructureEditRight.setVisibility(View.VISIBLE);
        Act_ImgImgButton_video_StructureEditDown.setVisibility(View.VISIBLE);

        Editor_dialog.show();
    }

    void poistioneditshow()
    {

        long currentTimeMicrosecond=(Act_VideoView_Pose.getCurrentPosition() * 1000);
        String wantId =  Tools.mm取得對應時間軸之key(posestructurepoint, currentTimeMicrosecond);
        if(!wantId.equals(""))
        {
            structurepoints = posestructurepoint.get(wantId);
            Act_GraphicOverlay_ShowVideoStructure.clear();
            Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx, height, width));
        }
    }

    void posestructershow()
    {

        long currentTimeMicrosecond=(Act_VideoView_Pose.getCurrentPosition() * 1000);
        String wantId =  Tools.mm取得對應時間軸之key(posestructurepoint, currentTimeMicrosecond);
        if(!wantId.equals(""))
        {
            structurepoints = posestructurepoint.get(wantId);
            Act_GraphicOverlay_ShowVideoStructure.clear();
            Act_GraphicOverlay_ShowVideoStructure.add(new AnalyzePoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints, height, width));
        }
    }
}

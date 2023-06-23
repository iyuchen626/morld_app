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
import com.example.morldapp_demo01.camera.VideoRecordingActivity;
import com.example.morldapp_demo01.fastextraction.URIPathHelper;
import com.example.morldapp_demo01.fastextraction.Utils;
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
    ImageButton Act_ImgButton_VideoStructureEdit;
    ImageButton Act_ImgImgButton_video_StructureEditUp,Act_ImgImgButton_video_StructureEditDown,Act_ImgImgButton_video_StructureEditLeft,Act_ImgImgButton_video_StructureEditRight;
    Button Act_Button_VideoStructureShow;
    private HashMap<String,  structurepoint[]> posestructurepoint=new HashMap<>();
    int count=0;
    private String StructureUriStr,videoInputPath;
    private ExecutorService executorService= Executors.newSingleThreadExecutor();
    private LinkedList<String> queue;
    private StringBuilder sb;
    String filename = "yuiop1.txt";
    private FrameExtractor frameExtractor;
    int orientation;
    int lastFindIndex = 0;
    private Boolean structure_show=false;
    int PointIdx = 0;
    Boolean EditStructureFlag = false;
    structurepoint[] structurepoints;
    float original_y, original_x,height,width;
    boolean is設定旋轉與鏡像 = false;
    int degress = 0;
    boolean isFlip = false;

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
                mm產生骨骼();
            }
        });
        Act_VideoView_Pose=findViewById(R.id.Layout_VideoView_ShowVideo);
        Act_ImageView_ShowVideo=findViewById(R.id.Layout_ImageView_ShowVideo);
        Act_GraphicOverlay_ShowVideoStructure=findViewById(R.id.Layout_GraphicOverlay_ShowVideoStructure);
        Act_ImgButton_VideoStructureEdit=findViewById(R.id.layout_ImgButton_video_StructureEdit);
        Act_Button_VideoStructureShow=findViewById(R.id.Layout_Button_VideoStructureShow);


        Act_ImgImgButton_video_StructureEditUp=findViewById(R.id.layout_ImgButton_video_StructureEditUp);
        Act_ImgImgButton_video_StructureEditLeft=findViewById(R.id.layout_ImgButton_video_StructureEditLeft);
        Act_ImgImgButton_video_StructureEditRight=findViewById(R.id.layout_ImgButton_video_StructureEditRight);
        Act_ImgImgButton_video_StructureEditDown=findViewById(R.id.layout_ImgButton_video_StructureEditDown);

        Act_ImgButton_VideoStructureEdit.setOnClickListener(this);
        Act_Button_VideoStructureShow.setOnClickListener(this);

        Act_ImgImgButton_video_StructureEditUp.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditLeft.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditRight.setOnClickListener(this);
        Act_ImgImgButton_video_StructureEditDown.setOnClickListener(this);


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        StructureUriStr = bundle.getString("urivideostr");
        filename = Tools.md5(StructureUriStr);
        Act_VideoView_Pose.setVideoURI(Uri.parse(StructureUriStr));
        mediaController=new MediaController(this);
        mediaController.setVisibility(View.VISIBLE);
        Act_VideoView_Pose.setMediaController(mediaController);
        sb = new StringBuilder();
        queue = new LinkedList<String>();
        URIPathHelper uriPathHelper = new URIPathHelper();
        Uri uri = Uri.parse(StructureUriStr);
        videoInputPath = uriPathHelper.getPath(getActivity(), uri).toString();
        frameExtractor.init(videoInputPath);
        orientation = frameExtractor.getOrientation(videoInputPath);
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
        mm讀取骨骼資料();

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
        }

        posestructurepoint = FileMangement.ReadFile(getActivity(), filename, (long) (offset*1000*1000));

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
        URIPathHelper uriPathHelper = new URIPathHelper();
        Uri uri = Uri.parse(StructureUriStr);
        String videoInputPath = uriPathHelper.getPath(getActivity(), uri).toString();
        File videoInputFile = new File(videoInputPath);
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
                            Tools.showInfo(getActivity(), "已上傳", uploadVideoResponsePOJO.data.uuid);


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
                    URIPathHelper uriPathHelper = new URIPathHelper();
                    Uri uri = Uri.parse(StructureUriStr);
                    String videoInputPath = uriPathHelper.getPath(getActivity(), uri).toString();
                    File videoInputFile = new File(videoInputPath);
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
            case R.id.Layout_Button_VideoStructureShow:
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
                break;

            case R.id.layout_ImgButton_video_StructureEditUp:
                original_y=structurepoints[PointIdx].getStructpoint_y();
                structurepoints[PointIdx].setStructpoint_y(original_y-20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));
                break;

            case R.id.layout_ImgButton_video_StructureEditLeft:
                original_x=structurepoints[PointIdx].getStructpoint_x();
                structurepoints[PointIdx].setStructpoint_x(original_x-20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));
                break;

            case R.id.layout_ImgButton_video_StructureEditRight:
                original_x=structurepoints[PointIdx].getStructpoint_x();
                structurepoints[PointIdx].setStructpoint_x(original_x+20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));
                break;

            case R.id.layout_ImgButton_video_StructureEditDown:
                original_y=structurepoints[PointIdx].getStructpoint_y();
                structurepoints[PointIdx].setStructpoint_y(original_y+20);
                Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));

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
        Tools.showQuestion(getActivity(), "校正", "請問畫面是否為正的?", "是", "否", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Tools.showQuestion(getActivity(), "校正", "請問畫面是否鏡像?", "是", "否", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        isFlip = true;
                        Bitmap b = Utils.fromBufferToBitmap(currentFrame.getByteBuffer(), currentFrame.getWidth(), currentFrame.getHeight(), degress, isFlip);
                        Act_ImageView_ShowVideo.setImageBitmap(b);
                        Tools.showQuestion(getActivity(), "校正", "請問畫面是否正常?", "是", "否", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Act_ImageView_ShowVideo.setVisibility(View.INVISIBLE);
                                frameExtractor.setPause(false);
                                is設定旋轉與鏡像 = true;
                            }
                        }, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mm校正流程(currentFrame);
                            }
                        });
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Act_ImageView_ShowVideo.setVisibility(View.INVISIBLE);
                        frameExtractor.setPause(false);
                        is設定旋轉與鏡像 = true;
                    }
                });

            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                degress += 90;
                Bitmap b = Utils.fromBufferToBitmap(currentFrame.getByteBuffer(), currentFrame.getWidth(), currentFrame.getHeight(), degress, isFlip);
                Act_ImageView_ShowVideo.setImageBitmap(b);
                mm校正流程(currentFrame);
            }
        });
    }

    @Override
    public void onCurrentFrameExtracted(@NonNull Frame currentFrame)
    {
        Bitmap imageBitmap = Utils.fromBufferToBitmap(currentFrame.getByteBuffer(), currentFrame.getWidth(), currentFrame.getHeight(), degress, isFlip);
        height=currentFrame.getHeight();
        width=currentFrame.getWidth();

        sb.append("currentFrame height"+height+ "currentFrame width"+width);

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if(!is設定旋轉與鏡像)
                {
                    Act_ImageView_ShowVideo.setImageBitmap(imageBitmap);
                    Act_ImageView_ShowVideo.setVisibility(View.VISIBLE);
                    frameExtractor.setPause(true);
                    mm校正流程(currentFrame);
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
                    queue.poll();
                    Log.i(Config.TAG, "queue size=" + queue.size() + " currentFrame="+ currentFrame.getTimestamp());
                    if (!result.equals("")) sb.append(currentFrame.getTimestamp() + "#" + result);
                    if (queue.size() == 0)
                    {
                        FileMangement.SaveFile(getActivity(), filename, sb.toString());
                        mm讀取骨骼資料();
                        Tools.hideProgress(getActivity());
                        Tools.toastSuccess(getActivity(), "骨骼已儲存txt");
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
        params.x =(int)Act_ImgButton_VideoStructureEdit.getScaleX();
        params.y = (int)Act_ImgButton_VideoStructureEdit.getScaleY();
        window.setAttributes(params);

        ImageButton Act_Button_Structure_Show=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_Show);
        ImageButton Act_Button_Structure_NotShow=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_NotShow);
        ImageButton Act_Button_Structure_AdjustPostion=Editor_dialog.findViewById(R.id.Layout_ImgBtn_Structure_Adjust_Postion);


        Act_Button_Structure_Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            Act_GraphicOverlay_ShowVideoStructure.add(new EditPoseGraphic(Act_GraphicOverlay_ShowVideoStructure, structurepoints,PointIdx));
        }
    }
}

package com.example.morldapp_demo01.Edit;

import android.content.Context;
import android.util.Log;

import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.activity.Base;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileMangement extends Base
{
    public  FileMangement( ) {
    }

    public static void SaveFile(Context context, String filename, String data)
    {
        try
        {
            File path = context.getExternalFilesDir(null);
            File file = new File(path, filename);
            FileOutputStream Output = new FileOutputStream(file, false);
            Output.write(data.getBytes());
            Output.close();
        }
        catch (Exception e)
        {
            Log.e(Config.TAG, e.getMessage());
        }
    }
    public static void SaveFilePose(Context context, String filename, Pose pose, float[]Poseweight, int count) throws IOException {

        String string;
        File path = context.getExternalFilesDir(null);
        File file = new File(path, filename);
        structurepoint[] savepointscalepoint= new structurepoint[12];

        for(int idx=0;idx<12;idx++)
        {
            savepointscalepoint[idx]=new structurepoint();
        }


        try {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output;
            if (count == 0) {
                Output = new FileOutputStream(file, false);
            } else {
                Output = new FileOutputStream(file, true);
            }

            savepointscalepoint=Translatepoint(pose,Poseweight);

            string = SavePoint(savepointscalepoint[0]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[1]);
            Output.write(string.getBytes());


            string = SavePoint(savepointscalepoint[2]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[3]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[4]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[5]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[6]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[7]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[8]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[9]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[10]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[11]);
            Output.write(string.getBytes());

            Output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void ReSaveFile(Context context, String filename, structurepoint[] savepointscalepoint, int count) throws IOException {

        String string;
        File path = context.getExternalFilesDir(null);
        File file = new File(path, filename);

        try {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output;
            if (count == 0) {
                Output = new FileOutputStream(file, false);
            } else {
                Output = new FileOutputStream(file, true);
            }


            string = SavePoint(savepointscalepoint[0]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[1]);
            Output.write(string.getBytes());


            string = SavePoint(savepointscalepoint[2]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[3]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[4]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[5]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[6]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[7]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[8]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[9]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[10]);
            Output.write(string.getBytes());

            string = SavePoint(savepointscalepoint[11]);
            Output.write(string.getBytes());

            Output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void DeleteFile(Context context, String filename)
    {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, filename);
        if(file.exists())
        {
            file.delete();
        }
    }

    public static void ReadFileFromTxt(AppCompatActivity context, String link, long offset, OnReadFileFromTxtListener onReadFileFromTxtListener)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                HashMap<String, structurepoint[]> res = new HashMap<>();
                try
                {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(link).build();
                    Response response = client.newCall(request).execute();
                    String res2 = response.body().string();
                    String[] ss = res2.split("\n");
                    List<String> lines =  Arrays.asList(ss);
                    for (String s : lines)
                    {
                        String[] data = s.split("#");
                        long time = Long.parseLong(data[0]);
                        time += offset;
                        data[0] = String.valueOf(time);
                        structurepoint[] structurepoints = new structurepoint[12];
                        for (int i = 1; i < 13; i++)
                        {
                            String[] data2 = data[i].split("Data");
                            structurepoint posestructurepoint = new structurepoint();
                            posestructurepoint.setStructpoint_x(Float.valueOf(data2[1]));
                            posestructurepoint.setStructpoint_y(Float.valueOf(data2[2]));
                            posestructurepoint.setStructpoint_weight(Float.valueOf(data2[3]));
                            structurepoints[i - 1] = posestructurepoint;
                        }
                        res.put(data[0], structurepoints);
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            onReadFileFromTxtListener.onTxt(res);
                        }
                    });
                }
                catch (Exception e) {}
            }
        }).start();
    }

    public static HashMap<String, structurepoint[]> ReadFile(Context context, String filename, long offset)
    {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, filename);

        HashMap<String, structurepoint[]> res = new HashMap<>();
        try
        {
            byte[] bbs = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            String sss = new String(bbs);
            List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.defaultCharset());
            for (String s : lines)
            {
                String[] data = s.split("#");
                long time = Long.parseLong(data[0]);
                time += offset;
                data[0] = String.valueOf(time);
                structurepoint[] structurepoints = new structurepoint[12];
                for (int i = 1; i < 13; i++)
                {
                    String[] data2 = data[i].split("Data");
                    structurepoint posestructurepoint = new structurepoint();
                    posestructurepoint.setStructpoint_x(Float.valueOf(data2[1]));
                    posestructurepoint.setStructpoint_y(Float.valueOf(data2[2]));
                    posestructurepoint.setStructpoint_weight(Float.valueOf(data2[3]));
                    structurepoints[i - 1] = posestructurepoint;
                }
                res.put(data[0], structurepoints);
            }

        }
        catch (Exception e) {}
        return res;
    }

    public static String ReadFile(Context context, String filename)
    {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, filename);

        HashMap<String, structurepoint[]> res = new HashMap<>();
        try
        {
            byte[] bbs = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            String s = new String(bbs);
            return s;
        }
        catch (Exception e) {}
        return "";
    }

    public static structurepoint[] Translatepoint(Pose pose,float []Poseweight) {
        PoseLandmark landmark;
        PointF3D point ;
        structurepoint[] pointscalepoint=new structurepoint[12];

        landmark = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        point = landmark.getPosition3D();
        pointscalepoint[0]=new structurepoint();
        pointscalepoint[0].setStructpoint_x(point.getX());
        pointscalepoint[0].setStructpoint_y(point.getY());
        pointscalepoint[0].setStructpoint_weight(Poseweight[0]);

        landmark = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        point = landmark.getPosition3D();
        pointscalepoint[1]=new structurepoint();
        pointscalepoint[1].setStructpoint_x(point.getX());
        pointscalepoint[1].setStructpoint_y(point.getY());
        pointscalepoint[1].setStructpoint_weight(Poseweight[1]);

        landmark = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        point = landmark.getPosition3D();
        pointscalepoint[2]=new structurepoint();
        pointscalepoint[2].setStructpoint_x(point.getX());
        pointscalepoint[2].setStructpoint_y(point.getY());
        pointscalepoint[2].setStructpoint_weight(Poseweight[2]);

        landmark = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        point = landmark.getPosition3D();
        pointscalepoint[3]=new structurepoint();
        pointscalepoint[3].setStructpoint_x(point.getX());
        pointscalepoint[3].setStructpoint_y(point.getY());
        pointscalepoint[3].setStructpoint_weight(Poseweight[3]);

        landmark = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
        point = landmark.getPosition3D();
        pointscalepoint[4]=new structurepoint();
        pointscalepoint[4].setStructpoint_x(point.getX());
        pointscalepoint[4].setStructpoint_y(point.getY());
        pointscalepoint[4].setStructpoint_weight(Poseweight[4]);

        landmark = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
        point = landmark.getPosition3D();
        pointscalepoint[5]=new structurepoint();
        pointscalepoint[5].setStructpoint_x(point.getX());
        pointscalepoint[5].setStructpoint_y(point.getY());
        pointscalepoint[5].setStructpoint_weight(Poseweight[5]);

        landmark = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        point = landmark.getPosition3D();
        pointscalepoint[6]=new structurepoint();
        pointscalepoint[6].setStructpoint_x(point.getX());
        pointscalepoint[6].setStructpoint_y(point.getY());
        pointscalepoint[6].setStructpoint_weight(Poseweight[6]);

        landmark = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        point = landmark.getPosition3D();
        pointscalepoint[7]=new structurepoint();
        pointscalepoint[7].setStructpoint_x(point.getX());
        pointscalepoint[7].setStructpoint_y(point.getY());
        pointscalepoint[7].setStructpoint_weight(Poseweight[7]);

        landmark = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        point = landmark.getPosition3D();
        pointscalepoint[8]=new structurepoint();
        pointscalepoint[8].setStructpoint_x(point.getX());
        pointscalepoint[8].setStructpoint_y(point.getY());
        pointscalepoint[8].setStructpoint_weight(Poseweight[8]);

        landmark = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        point = landmark.getPosition3D();
        pointscalepoint[9]=new structurepoint();
        pointscalepoint[9].setStructpoint_x(point.getX());
        pointscalepoint[9].setStructpoint_y(point.getY());
        pointscalepoint[9].setStructpoint_weight(Poseweight[9]);

        landmark = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
        point = landmark.getPosition3D();
        pointscalepoint[10]=new structurepoint();
        pointscalepoint[10].setStructpoint_x(point.getX());
        pointscalepoint[10].setStructpoint_y(point.getY());
        pointscalepoint[10].setStructpoint_weight(Poseweight[10]);

        landmark = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        point = landmark.getPosition3D();
        pointscalepoint[11]=new structurepoint();
        pointscalepoint[11].setStructpoint_x(point.getX());
        pointscalepoint[11].setStructpoint_y(point.getY());
        pointscalepoint[11].setStructpoint_weight(Poseweight[11]);


        return pointscalepoint;
    }

    public static String SavePoint(structurepoint pointscale)
    {
        String strline;
        strline = "Data" + pointscale.getStructpoint_x() + "Data" +
                pointscale.getStructpoint_y() + "Data" +
                pointscale.getStructpoint_weight() + "#";
        return strline;
    }

    public interface OnReadFileFromTxtListener
    {
        void onTxt(HashMap<String, structurepoint[]>  s);
    }
}

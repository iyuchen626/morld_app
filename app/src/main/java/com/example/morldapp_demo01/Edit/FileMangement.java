package com.example.morldapp_demo01.Edit;

import android.content.Context;
import android.os.Environment;

import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.R;
import com.example.morldapp_demo01.activity.Base;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileMangement extends Base
{

    public static GraphicOverlay overlay;
    public boolean isImageFlipped;

    public  FileMangement(GraphicOverlay overlay) {
        this.overlay = overlay;
    }
    public boolean isImageFlipped() {
        return overlay.isImageFlipped;
    }

    /** Adjusts the supplied value from the image scale to the view scale. */
    public static float scale(float imagePixel) {
        return imagePixel * overlay.scaleFactor;
    }

    public static void SaveFilePose(Context context, String filename, Pose pose, float[]Poseweight, int count) throws IOException {

        String string;
        File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
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
        File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
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
        File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, filename);
        if(!file.exists())
        {
            file.delete();
        }
    }

    public static structurepoint[] testReadFile(Context context, String filename, int count) {
       // File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        //File file = new File(path, filename);
        File path = "android.resource://com.android/";
        //File file = new File(path, filename);

        File file= new File("android.resource://com.android/" + R.raw.structure_data);
//        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("android.resource://com.android/" + R.raw.test_4));
//        String Path= "android.resource://"+context.getPackageName()+"/"+ R.raw.structure_data;
//        File file= new File(Path);
        structurepoint[] posestructurepoint=new structurepoint[12];

        try {
            FileReader fr = new FileReader(file);
            BufferedReader bufFile = new BufferedReader(fr);

            String readData = "";
            String Filetemp = bufFile.readLine(); //readLine()讀取一整行
            String[] data = new String[4];
            int FileIdx=0,ReadIdx=0;
            //detectcount
            while ((Filetemp!=null)&&(FileIdx<12*count)){
//              readData+=temp +  "/n";
//              str2[idx]=temp.split("Data");
//              idx=idx+1;
                FileIdx=FileIdx+1;
                Filetemp=bufFile.readLine();
            }

            while ((Filetemp!=null)&&(ReadIdx<12)){
                data=Filetemp.split("Data");
                posestructurepoint[ReadIdx]=new structurepoint();
                //posestructurepoint[ReadIdx].setStructpoint_x(translateX(Float.valueOf(data[1])));
                //posestructurepoint[ReadIdx].setStructpoint_y(translateY(Float.valueOf(data[2])));
                posestructurepoint[ReadIdx].setStructpoint_x(Float.valueOf(data[1]));
                posestructurepoint[ReadIdx].setStructpoint_y(Float.valueOf(data[2]));
                posestructurepoint[ReadIdx].setStructpoint_weight(Float.valueOf(data[3]));

                ReadIdx++;
                Filetemp=bufFile.readLine();
            }
            bufFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return posestructurepoint;
    }


    public static structurepoint[] ReadFile(Context context, String filename, int count) {
        File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, filename);
        structurepoint[] posestructurepoint=new structurepoint[12];

        try {
            FileReader fr = new FileReader(file);
            BufferedReader bufFile = new BufferedReader(fr);

            String readData = "";
            String Filetemp = bufFile.readLine(); //readLine()讀取一整行
            String[] data = new String[4];
            int FileIdx=0,ReadIdx=0;
            //detectcount
            while ((Filetemp!=null)&&(FileIdx<12*count)){
//              readData+=temp +  "/n";
//              str2[idx]=temp.split("Data");
//              idx=idx+1;
                FileIdx=FileIdx+1;
                Filetemp=bufFile.readLine();
            }

            while ((Filetemp!=null)&&(ReadIdx<12)){
                data=Filetemp.split("Data");
                posestructurepoint[ReadIdx]=new structurepoint();
               //posestructurepoint[ReadIdx].setStructpoint_x(translateX(Float.valueOf(data[1])));
               //posestructurepoint[ReadIdx].setStructpoint_y(translateY(Float.valueOf(data[2])));
                posestructurepoint[ReadIdx].setStructpoint_x(Float.valueOf(data[1]));
                posestructurepoint[ReadIdx].setStructpoint_y(Float.valueOf(data[2]));
                posestructurepoint[ReadIdx].setStructpoint_weight(Float.valueOf(data[3]));

                ReadIdx++;
                Filetemp=bufFile.readLine();
            }
            bufFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return posestructurepoint;
    }

    static structurepoint[] Translatepoint(Pose pose,float []Poseweight) {
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

    static String SavePoint(structurepoint pointscale) {

        String strline;
        //updatePaintColorByZValue(
        //       paint, canvas, visualizeZ, rescaleZForVisualization, point.getZ(), zMin, zMax);

//        strline= "Data"+translateX(point.getX())+"Data"+
//                translateY(point.getY())+"\n";
        strline= "Data"+pointscale.getStructpoint_x()+"Data"+
                pointscale.getStructpoint_y()+"Data"+
                pointscale.getStructpoint_weight()+"\n";
        return strline;
    }

    public static float translateX(float x) {
        if (overlay.isImageFlipped) {
            return overlay.getWidth() - (scale(x) - overlay.postScaleWidthOffset);
        } else {
            return scale(x) - overlay.postScaleWidthOffset;
        }
    }

    public static float translateY(float y) {
        return scale(y) - overlay.postScaleHeightOffset;
    }
}

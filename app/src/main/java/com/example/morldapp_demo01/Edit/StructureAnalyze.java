package com.example.morldapp_demo01.Edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.icu.util.Output;
import android.util.Log;

import com.example.morldapp_demo01.Config;
import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.classification.posedetector.EditorPoseGraphic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;

public class StructureAnalyze {
    public static Pose StructurePose;
    public static Bitmap Adjust_picture(Bitmap OriginalBitmap) {
        float sx = 0, sy = 0, Adjustscale = 0, test = 0;
        Bitmap Adjust_Bitmap_ShowPhoto = null;

        Matrix matrix = new Matrix();

        if ((OriginalBitmap.getWidth() > 1080) || (OriginalBitmap.getHeight() > 1920)) {
            sx = (float) 1920/(OriginalBitmap.getHeight());
            sy = (float) 1080/(OriginalBitmap.getWidth());
            Adjustscale = (sx < sy) ? sx : sy;

            test=(float) (Math.floor(Adjustscale*100.0)/100.0);
            matrix.postScale(test,test);

            Adjust_Bitmap_ShowPhoto = Bitmap.createBitmap(OriginalBitmap, 0, 0, OriginalBitmap.getWidth(), OriginalBitmap.getHeight(), matrix, true);

        } else if ((OriginalBitmap.getWidth() < 1080) && (OriginalBitmap.getHeight() < 1920)) {
            sx = (float) 1920 / (OriginalBitmap.getHeight());
            sy = (float) 1080 / (OriginalBitmap.getWidth());
            Adjustscale = (sx < sy) ? sx : sy;

            matrix.postScale(Adjustscale, Adjustscale);

            Adjust_Bitmap_ShowPhoto=Bitmap.createBitmap(OriginalBitmap, 0, 0, OriginalBitmap.getWidth(), OriginalBitmap.getHeight(), matrix, true);
        }

        return Adjust_Bitmap_ShowPhoto;
    }

    public static void Analyze_Structure(Context context, InputImage inputImage, GraphicOverlay graphicoverlay, String filename, int count) {

        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        PoseDetector poseDetector = PoseDetection.getClient(options);

        poseDetector.process(inputImage)
                .addOnSuccessListener(
                        new OnSuccessListener<Pose>() {
                            @Override
                            public void onSuccess(Pose pose) {
                                List<PoseLandmark> allPoseLandmarks = pose.getAllPoseLandmarks();
                                if(allPoseLandmarks.isEmpty())
                                {
                                    graphicoverlay.clear();
                                }
                                else
                                {
                                    float StructurePoseWeight[]={10,10,10,10,10,10,10,10,10,10,10,10};

                                    StructurePose=pose;
                                    graphicoverlay.clear();
                                    graphicoverlay.add(
                                            new EditorPoseGraphic(
                                                    graphicoverlay,
                                                    pose,count));
                                    try {
                                        FileMangement.SaveFilePose(context, filename,StructurePose,StructurePoseWeight,count);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    poseDetector.close();

                                }

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                return;
                            }
                        }
                );
    }

    public static String toSaveFilePoseFormat(Pose pose)
    {
        float StructurePoseWeight[]={10,10,10,10,10,10,10,10,10,10,10,10};
        structurepoint[] savepointscalepoint = FileMangement.Translatepoint(pose, StructurePoseWeight);
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < 12; idx++)
        {
            sb.append(FileMangement.SavePoint(savepointscalepoint[idx]));
        }
        sb = sb.deleteCharAt(sb.length()-1);
        sb.append('\n');
        String final_s = sb.toString();
        return final_s;
    }

    public static void Analyze_Structure(InputImage inputImage, OnAnalyzeStructureListener onAnalyzeStructureListener)
    {
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        PoseDetector poseDetector = PoseDetection.getClient(options);
        poseDetector.process(inputImage).addOnSuccessListener(
                new OnSuccessListener<Pose>()
                {
                    @Override
                    public void onSuccess(Pose pose)
                    {
                        List<PoseLandmark> allPoseLandmarks = pose.getAllPoseLandmarks();
                        if (!allPoseLandmarks.isEmpty())
                        {
                            String s = toSaveFilePoseFormat(pose);
                            onAnalyzeStructureListener.onDone(s);
                        }
                        else onAnalyzeStructureListener.onDone("");
                        poseDetector.close();
                    }
                }).addOnFailureListener(
                new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        poseDetector.close();
                        Log.e(Config.TAG, e.getMessage());
                        onAnalyzeStructureListener.onDone("");
                    }
                }
        );
    }

    public static Pose StructurePose()
    {
        return StructurePose;
    }

    public interface OnAnalyzeStructureListener {
        void onDone(String s);
    }
}

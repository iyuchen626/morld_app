package com.example.morldapp_demo01.classification.posedetector;

import android.os.Environment;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;



public class PoseDetectResult {

    static double showResult;

    PoseDetectResult(Double Score)
    {

        this.showResult=Score;


    }

    public static double getPoseDetectResult()
    {
        return showResult;
    }


}

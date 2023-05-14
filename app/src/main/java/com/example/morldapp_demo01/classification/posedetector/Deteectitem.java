package com.example.morldapp_demo01.classification.posedetector;

public class Deteectitem {

    static boolean Detect ;

    public Deteectitem(boolean showDetectResult)
    {
        this.Detect=showDetectResult;
    }

    public static boolean getDetectResult()
    {
        return Detect;
    }


}

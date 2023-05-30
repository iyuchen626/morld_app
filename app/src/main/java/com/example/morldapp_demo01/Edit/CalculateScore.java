package com.example.morldapp_demo01.Edit;

import com.google.mlkit.vision.pose.Pose;

public class CalculateScore {

    static boolean ShowScoreResultFlag ;
    static int ScoreResult=0;
    private static structurepoint[] posestructuresamplepoint=new structurepoint[12];


    public CalculateScore(boolean ShowScoreResultFlag,structurepoint[] posestructuresamplepoint)
    {
        this.ShowScoreResultFlag=ShowScoreResultFlag;
        this.posestructuresamplepoint=posestructuresamplepoint;
    }

    public static void getScore(Pose UserPose) {

        structurepoint[] posestructureuserpoint=new structurepoint[12];
        float structureweight[]={1,1,1,1,1,1,1,1,1,1,1,1};

        for(int idx=0;idx<12;idx++)
        {
            posestructureuserpoint[idx]=new structurepoint();
        }

        posestructureuserpoint=FileMangement.Translatepoint(UserPose,structureweight);


        for(int idx=0;idx<12;idx++)
        {
            PointScore(posestructuresamplepoint[idx],posestructureuserpoint[idx]);
        }




        if (ShowScoreResultFlag) {
            ScoreResult = 80;

        } else {
            ScoreResult = 0;

        }
    }

    private static float PointScore(structurepoint structuresamplepoint, structurepoint structureuserpoint) {

        float scorebypoint=0;
        structuresamplepoint.getStructpoint_x();
        structuresamplepoint.getStructpoint_y();
        structuresamplepoint.getStructpoint_weight();
        structureuserpoint.getStructpoint_x();
        structureuserpoint.getStructpoint_y();

        return scorebypoint;

    }


    public static int getScoreResult()
    {
        return ScoreResult;
    }




}

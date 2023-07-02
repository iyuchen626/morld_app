package com.example.morldapp_demo01.Edit;

import com.google.mlkit.vision.pose.Pose;

public class CalculateScore {

    static boolean ShowScoreResultFlag ;
    static int ScoreResult=120;
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
//            if(posestructureuserpoint==null) break;
//            if(posestructuresamplepoint==null) break;
            if(posestructureuserpoint[idx]==null) break;
            if(posestructuresamplepoint[idx]==null) break;
            PointScore(posestructuresamplepoint[idx],posestructureuserpoint[idx]);
        }




        if (ShowScoreResultFlag) {
            ScoreResult=120;
            for(int idx=0;idx<12;idx++)
            {
                ScoreResult -= PointScore(posestructuresamplepoint[idx],posestructureuserpoint[idx])/12;
            }

        }
        if (ScoreResult < 0)
        {
            ScoreResult = 0;
        }
        if (ScoreResult > 100)
        {
            ScoreResult = 100;
        }
    }

    private static float PointScore(structurepoint structuresamplepoint, structurepoint structureuserpoint) {

        double scorebypoint=0;
        structuresamplepoint.getStructpoint_x();
        structuresamplepoint.getStructpoint_y();
        structuresamplepoint.getStructpoint_weight();
        structureuserpoint.getStructpoint_x();
        structureuserpoint.getStructpoint_y();

        //scorebypoint = Math.abs(((Math.abs((double)((structureuserpoint.getStructpoint_y()-structuresamplepoint.getStructpoint_y()))))-300));
        //scorebypoint =(Math.abs(((double)((structureuserpoint.getStructpoint_y()-structuresamplepoint.getStructpoint_y())))+15));
        scorebypoint =(Math.abs(((double)((structureuserpoint.getStructpoint_y()-structuresamplepoint.getStructpoint_y())))+15))+(Math.abs(((double)((structureuserpoint.getStructpoint_x()-structuresamplepoint.getStructpoint_x())))+0));
        if (scorebypoint >120)
        {
            scorebypoint =120;
        }

        //scorebypoint=Math.pow((double)((structureuserpoint.getStructpoint_x()-structuresamplepoint.getStructpoint_x())*(structureuserpoint.getStructpoint_x()-structuresamplepoint.getStructpoint_x())/200000)+(double)((structureuserpoint.getStructpoint_y()-structuresamplepoint.getStructpoint_y())*(structureuserpoint.getStructpoint_y()-structuresamplepoint.getStructpoint_y())/200000),1);

        return (float) scorebypoint;

    }


    public static int getScoreResult()
    {
        return ScoreResult;
    }
}
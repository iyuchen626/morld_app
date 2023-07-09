package com.example.morldapp_demo01.Edit;

import android.widget.Toast;

import com.example.morldapp_demo01.Tools;
import com.google.mlkit.vision.pose.Pose;

public class CalculateScore {

    static boolean ShowScoreResultFlag ;
    static float scalex=0,scaley=0 ;
    static float ScoreResult=0;
    static float Scoredelete=0;
    private static structurepoint[] posestructuresamplepoint=new structurepoint[12];


    public  CalculateScore(boolean ShowScoreResultFlag,structurepoint[] posestructuresamplepoint,float scalex,float scaley)
    {
        this.ShowScoreResultFlag=ShowScoreResultFlag;
        this.posestructuresamplepoint=posestructuresamplepoint;
        this.scalex=scalex;
        this.scaley=scaley;

    }

    public static void getScore(Pose UserPose) {

        structurepoint[] posestructureuserpoint=new structurepoint[12];
        float structureweight[]={1,1,1,1,1,1,1,1,1,1,1,1};

        if(ShowScoreResultFlag==false)
        {

        }
        else {
            ScoreResult=0;
            for (int idx = 0; idx < 12; idx++) {
                posestructureuserpoint[idx] = new structurepoint();
            }

            posestructureuserpoint = FileMangement.Translatepoint(UserPose, structureweight);

//        if (ShowScoreResultFlag) {
//
//            ScoreResult=0;
//        }

            for (int idx = 0; idx < 12; idx++) {
//            if(posestructureuserpoint==null) break;
//            if(posestructuresamplepoint==null) break;
                if (posestructureuserpoint[idx] == null) break;
                if (posestructuresamplepoint[idx] == null) break;
                ScoreResult += PointScore(posestructuresamplepoint[idx], posestructureuserpoint[idx]);
            }

            ScoreResult=((120-ScoreResult/12)/12)*10;

            if (ScoreResult < 0)
            {
                ScoreResult = 0;
            }


//            ScoreResult=0;
////            for(int idx=0;idx<12;idx++)
////            {
////                ScoreResult = PointScore(posestructuresamplepoint[idx],posestructureuserpoint[idx])/12;
////            }
//
//        }
            ShowScoreResultFlag=false;
        }
    }

    private static float PointScore(structurepoint structuresamplepoint, structurepoint structureuserpoint) {

        double scorebypoint=0,scorebypoint1=0,scorebypointdiff_y=0,scorebypointdiff_x=0,scorebypointx=0,scorebypointy=0;

        scorebypointdiff_y=(double)(structureuserpoint.getStructpoint_y()-(structuresamplepoint.getStructpoint_y()*scaley)+15);
        scorebypointdiff_x=(double)(structureuserpoint.getStructpoint_x()-(structuresamplepoint.getStructpoint_x()*scalex)+60); // user - sample = -50 ~ -60



        //scorebypoint = Math.pow(scorebypointdiff_x, 2)+Math.pow(scorebypointdiff_y, 2);
        //scorebypoint = Math.pow(scorebypoint, 0.5);

        //scorebypoint =(Math.abs(scorebypointdiff_x))/3; //Math.abs(scorebypointdiff_y))/2;
        scorebypoint =(Math.abs(scorebypointdiff_y)-135)*2/3;
        if (scorebypoint < 0)
        {
            scorebypoint = 0;
        }
        if (scorebypoint > 120)
        {
            scorebypoint = 120;
        }

        scorebypoint1 =(Math.abs(scorebypointdiff_x))/3;
        if (scorebypoint1 > 120)
        {
            scorebypoint1 = 120;
        }
        scorebypoint =scorebypoint+scorebypoint1;
        return (float) scorebypoint;

    }


    public static float getScoreResult()
    {
        return ScoreResult;
    }
}
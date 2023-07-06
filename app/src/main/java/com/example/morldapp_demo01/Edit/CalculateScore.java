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

            ScoreResult=((120-ScoreResult)/12)*10;

//        ScoreResult=((120-Scoredelete)/12)*10;


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
//        structuresamplepoint.getStructpoint_x();
//        structuresamplepoint.getStructpoint_y();
//        structuresamplepoint.getStructpoint_weight();
//        structureuserpoint.getStructpoint_x();
//        structureuserpoint.getStructpoint_y();
        

        scorebypointdiff_x=(double)(structureuserpoint.getStructpoint_x()-(structuresamplepoint.getStructpoint_x()));
        scorebypointdiff_y=(double)(structureuserpoint.getStructpoint_y()-(structuresamplepoint.getStructpoint_y()));




        //scorebypoint = Math.abs(((Math.abs((double)((structureuserpoint.getStructpoint_y()-structuresamplepoint.getStructpoint_y()))))-300));
        //scorebypoint =(Math.abs(((double)((9structureuserpoint.getStructpoint_y()-structuresamplepoint.getStructpoint_y())))+15));
//        scorebypoint =(Math.abs(((double)((structureuserpoint.getStructpoint_y()-(structuresamplepoint.getStructpoint_y()*scaley))))+15))+(Math.abs(((double)((structureuserpoint.getStructpoint_x()-(structuresamplepoint.getStructpoint_x()*scalex))))+0));

        scorebypointx =Math.abs(scorebypointdiff_x);
        scorebypointx =Math.abs(scorebypointdiff_y);

        if(scorebypointx<=5) {
            scorebypoint = 0;
        }
        else if((scorebypointx>5)&&(scorebypointx<=10))
        {
            scorebypoint=1;
        }
        else if(scorebypointx>28)
        {
            scorebypoint=12;
        }
        else
        {
            scorebypoint=((scorebypointx-10)/2)+1;
        }

        scorebypointy =Math.abs(scorebypointdiff_x);

        if(scorebypointy<=5) {
            scorebypoint1 = 0;
        }
        else if((scorebypointy>5)&&(scorebypointy<=10))
        {
            scorebypoint1=1;
        }
        else if(scorebypointy>28)
        {
            scorebypoint1=12;
        }
        else
        {
            scorebypoint1=((scorebypointy-10)/2)+1;
        }



//        Tools.toast(getActivity(), e.getMessage());

        scorebypoint +=scorebypoint1;
        scorebypoint=scorebypoint/2;

        return (float) scorebypoint;

    }


    public static float getScoreResult()
    {
        return ScoreResult;
    }
}
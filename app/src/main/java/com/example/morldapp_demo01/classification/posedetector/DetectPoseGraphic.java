/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.morldapp_demo01.classification.posedetector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;

import com.example.morldapp_demo01.EditGraphicOverlay;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/** Draw the detected pose in preview. */
public class DetectPoseGraphic extends EditGraphicOverlay.EditGraphic {

  private static final float DOT_RADIUS = 8.0f;
  private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
  private static final float STROKE_WIDTH = 7.0f;
  private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;
    public  static final String[][][] str2 = new String[100][12][3];


  //private final Pose pose;

  private float zMin = Float.MAX_VALUE;
  private float zMax = Float.MIN_VALUE;

  //private final boolean posedetectresult = false;


  private final Paint classificationTextPaint;
  private final Paint whitePaint;
  private int detectcount;
  private Boolean ReadDATA;


   public DetectPoseGraphic(
           EditGraphicOverlay overlay,
           int detect_count,
           Boolean Read_DATA

           ) {
    super(overlay);
    this.detectcount = detect_count;
    this.ReadDATA = Read_DATA;

    classificationTextPaint = new Paint();
    classificationTextPaint.setColor(Color.WHITE);
    classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);
    classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);

    whitePaint = new Paint();
    whitePaint.setStrokeWidth(STROKE_WIDTH);
    whitePaint.setColor(Color.BLUE);
    whitePaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

  }

  @Override
  public void draw(Canvas canvas) {
       if((ReadDATA==true)) {
           String filename = "AAApose_detect_0516.txt";
           File path = getApplicationContext().getExternalFilesDir("txt");
           File file = new File(path, filename);
           int idx = 0, testidx = 0,count=0;
           try {
               //建立FileReader物件，並設定讀取的檔案為CheckFlie.txt
               FileReader fr = new FileReader(file);
               //將BufferedReader與FileReader做連結
               BufferedReader bufFile = new BufferedReader(fr);

               String readData = "";
               String temp = bufFile.readLine(); //readLine()讀取一整行
               //detectcount
//               while ((temp != null) && (testidx < 12 * detectcount)) {
////              readData+=temp +
////              "/n";
////              str2[idx]=temp.split("Data");
////              idx=idx+1;
//                   testidx = testidx + 1;
//                   temp = bufFile.readLine();
//               }

               while ((temp != null)) {
                   for(idx=0;idx<12;idx++)
                   {
                       readData += temp + "/n";
                       str2[count][idx] = temp.split("Data");
                       temp = bufFile.readLine();
                   }
                   count=count+1;
               }
               bufFile.close();
               fr.close();
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       else {


//
//// Face
//      //drawLine(canvas, nose, lefyEyeInner, whitePaint);
//      //drawLine(canvas, lefyEyeInner, lefyEye, whitePaint);
//      //drawLine(canvas, lefyEye, leftEyeOuter, whitePaint);
//      //drawLine(canvas, leftEyeOuter, leftEar, whitePaint);
//      //drawLine(canvas, nose, rightEyeInner, whitePaint);
//      //drawLine(canvas, rightEyeInner, rightEye, whitePaint);
//      //drawLine(canvas, rightEye, rightEyeOuter, whitePaint);
//      //drawLine(canvas, rightEyeOuter, rightEar, whitePaint);
//      //drawLine(canvas, leftMouth, rightMouth, whitePaint);
//
           drawLine(canvas, Float.valueOf(str2[detectcount][6][1]), Float.valueOf(str2[detectcount][6][2]), Float.valueOf(str2[detectcount][0][1]), Float.valueOf(str2[detectcount][0][2]), whitePaint);
           drawLine(canvas, Float.valueOf(str2[detectcount][8][1]), Float.valueOf(str2[detectcount][8][2]), Float.valueOf(str2[detectcount][2][1]), Float.valueOf(str2[detectcount][2][2]), whitePaint);
//
//      // Left body
           drawLine(canvas, Float.valueOf(str2[detectcount][6][1]), Float.valueOf(str2[detectcount][6][2]), Float.valueOf(str2[detectcount][7][1]), Float.valueOf(str2[detectcount][7][2]), whitePaint);
           drawLine(canvas, Float.valueOf(str2[detectcount][7][1]), Float.valueOf(str2[detectcount][7][2]), Float.valueOf(str2[detectcount][11][1]), Float.valueOf(str2[detectcount][11][2]), whitePaint);
           //drawLine(canvas, leftShoulder, leftHip, whitePaint);
//      //2
           drawLine(canvas, Float.valueOf(str2[detectcount][6][1]), Float.valueOf(str2[detectcount][6][2]), Float.valueOf(str2[detectcount][8][1]), Float.valueOf(str2[detectcount][8][2]), whitePaint);
           //3
           drawLine(canvas, Float.valueOf(str2[detectcount][8][1]), Float.valueOf(str2[detectcount][8][2]), Float.valueOf(str2[detectcount][6][1]), Float.valueOf(str2[detectcount][6][2]), whitePaint);
           drawLine(canvas, Float.valueOf(str2[detectcount][8][1]), Float.valueOf(str2[detectcount][8][2]), Float.valueOf(str2[detectcount][9][1]), Float.valueOf(str2[detectcount][9][2]), whitePaint);
           drawLine(canvas, Float.valueOf(str2[detectcount][9][1]), Float.valueOf(str2[detectcount][9][2]), Float.valueOf(str2[detectcount][10][1]), Float.valueOf(str2[detectcount][10][2]), whitePaint);
//      //drawLine(canvas, leftWrist, leftThumb, whitePaint);
//      //drawLine(canvas, leftWrist, leftPinky, whitePaint);
//      //drawLine(canvas, leftWrist, leftIndex, whitePaint);
//      //drawLine(canvas, leftIndex, leftPinky, whitePaint);
//      //drawLine(canvas, leftAnkle, leftHeel, whitePaint);
//      //drawLine(canvas, leftHeel, leftFootIndex, whitePaint);
//
//      // Right body
           drawLine(canvas, Float.valueOf(str2[detectcount][0][1]), Float.valueOf(str2[detectcount][0][2]), Float.valueOf(str2[detectcount][1][1]), Float.valueOf(str2[detectcount][1][2]), whitePaint);
           drawLine(canvas, Float.valueOf(str2[detectcount][1][1]), Float.valueOf(str2[detectcount][1][2]), Float.valueOf(str2[detectcount][5][1]), Float.valueOf(str2[detectcount][5][2]), whitePaint);
//      //drawLine(canvas, rightShoulder, rightHip, whitePaint);
////2
           drawLine(canvas, Float.valueOf(str2[detectcount][0][1]), Float.valueOf(str2[detectcount][0][2]), Float.valueOf(str2[detectcount][2][1]), Float.valueOf(str2[detectcount][2][2]), whitePaint);
//   //3
           drawLine(canvas, Float.valueOf(str2[detectcount][2][1]), Float.valueOf(str2[detectcount][2][2]), Float.valueOf(str2[detectcount][0][1]), Float.valueOf(str2[detectcount][0][2]), whitePaint);
           drawLine(canvas, Float.valueOf(str2[detectcount][2][1]), Float.valueOf(str2[detectcount][2][2]), Float.valueOf(str2[detectcount][3][1]), Float.valueOf(str2[detectcount][3][2]), whitePaint);
           drawLine(canvas, Float.valueOf(str2[detectcount][3][1]), Float.valueOf(str2[detectcount][3][2]), Float.valueOf(str2[detectcount][4][1]), Float.valueOf(str2[detectcount][4][2]), whitePaint);
//      //drawLine(canvas, rightWrist, rightThumb, whitePaint);
//      //drawLine(canvas, rightWrist, rightPinky, whitePaint);
//      //drawLine(canvas, rightWrist, rightIndex, whitePaint);
//      //drawLine(canvas, rightIndex, rightPinky, whitePaint);
//      //drawLine(canvas, rightAnkle, rightHeel, whitePaint);
//      //drawLine(canvas, rightHeel, rightFootIndex, whitePaint)
//
////      drawPoint(canvas, rightShoulder, whitePaint);0
////      drawPoint(canvas, rightElbow, whitePaint);1
////      drawPoint(canvas, rightHip, whitePaint);2
////      //drawPoint(canvas, rightHeel, whitePaint);
////      drawPoint(canvas, rightKnee, whitePaint);3
////      drawPoint(canvas, rightAnkle, whitePaint);4
////      drawPoint(canvas, rightWrist, whitePaint);5
////
////      drawPoint(canvas, leftShoulder, whitePaint);6
////      drawPoint(canvas, leftElbow, whitePaint);7
////      drawPoint(canvas, leftHip, whitePaint);8
////      //drawPoint(canvas, leftHeel, whitePaint);
////      drawPoint(canvas, leftKnee, whitePaint);9
////      drawPoint(canvas, leftAnkle, whitePaint);10
////      drawPoint(canvas, leftWrist, whitePaint);1

           //drawLine(canvas, Float.valueOf(str2[6][1]),Float.valueOf(str2[6][2]), Float.valueOf(str2[0][1]),Float.valueOf(str2[0][2]), whitePaint);
           // drawLine(canvas, Float.valueOf(str2[8][1]),Float.valueOf(str2[8][2]), Float.valueOf(str2[2][1]),Float.valueOf(str2[2][2]), whitePaint);

           drawPoint(canvas, Float.valueOf(str2[detectcount][0][1]), Float.valueOf(str2[detectcount][0][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][1][1]), Float.valueOf(str2[detectcount][1][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][2][1]), Float.valueOf(str2[detectcount][2][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][3][1]), Float.valueOf(str2[detectcount][3][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][4][1]), Float.valueOf(str2[detectcount][4][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][5][1]), Float.valueOf(str2[detectcount][5][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][6][1]), Float.valueOf(str2[detectcount][6][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][7][1]), Float.valueOf(str2[detectcount][7][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][8][1]), Float.valueOf(str2[detectcount][8][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][9][1]), Float.valueOf(str2[detectcount][9][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][10][1]), Float.valueOf(str2[detectcount][10][2]), whitePaint);
           drawPoint(canvas, Float.valueOf(str2[detectcount][11][1]), Float.valueOf(str2[detectcount][11][2]), whitePaint);


           //drawPoint(Canvas canvas,Float.valueOf(str2[0]),str ,Y, ,whi)tePaint);


//    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
//    if (landmarks.isEmpty()) {
//      return;
//    }
//    else
//    {
//    }

           // Draw pose classification text.
//    float classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f;
//    for (int i = 0; i < poseClassification.size(); i++) {
//      float classificationY =
//              (canvas.getHeight()
//                      - POSE_CLASSIFICATION_TEXT_SIZE * 1.5f * (poseClassification.size() - i));
//      canvas.drawText(
//              poseClassification.get(i), classificationX, classificationY, classificationTextPaint);
//    }

           // Draw all the points


//    PoseLandmark nose = pose.getPoseLandmark(PoseLandmark.NOSE);
//    PoseLandmark lefyEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER);
//    PoseLandmark lefyEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE);
//    PoseLandmark leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER);
//    PoseLandmark rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER);
//    PoseLandmark rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE);
//    PoseLandmark rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER);
//    PoseLandmark leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR);
//    PoseLandmark rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR);
//    PoseLandmark leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH);
//    PoseLandmark rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH);
//
//    PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
//    PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
//    PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
//    PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
//    PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
//    PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
//    PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
//    PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
//    PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
//    PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
//    PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
//    PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
//
//    PoseLandmark leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY);
//    PoseLandmark rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY);
//    PoseLandmark leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX);
//    PoseLandmark rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX);
//    PoseLandmark leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB);
//    PoseLandmark rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB);
//    PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);
//    PoseLandmark rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL);
//    PoseLandmark leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX);
//    PoseLandmark rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX);

           // Face
//    drawLine(canvas, nose, lefyEyeInner, whitePaint);
//    drawLine(canvas, lefyEyeInner, lefyEye, whitePaint);
//    drawLine(canvas, lefyEye, leftEyeOuter, whitePaint);
//    drawLine(canvas, leftEyeOuter, leftEar, whitePaint);
//    drawLine(canvas, nose, rightEyeInner, whitePaint);
//    drawLine(canvas, rightEyeInner, rightEye, whitePaint);
//    drawLine(canvas, rightEye, rightEyeOuter, whitePaint);
//    drawLine(canvas, rightEyeOuter, rightEar, whitePaint);
//    drawLine(canvas, leftMouth, rightMouth, whitePaint);
//
//    drawLine(canvas, leftShoulder, rightShoulder, whitePaint);
//    drawLine(canvas, leftHip, rightHip, whitePaint);
//
//    // Left body
//    drawLine(canvas, leftShoulder, leftElbow, whitePaint);
//    drawLine(canvas, leftElbow, leftWrist, whitePaint);
//    drawLine(canvas, leftShoulder, leftHip, whitePaint);
//    drawLine(canvas, leftHip, leftKnee, whitePaint);
//    drawLine(canvas, leftKnee, leftAnkle, whitePaint);
//    drawLine(canvas, leftWrist, leftThumb, whitePaint);
//    drawLine(canvas, leftWrist, leftPinky, whitePaint);
//    drawLine(canvas, leftWrist, leftIndex, whitePaint);
//    drawLine(canvas, leftIndex, leftPinky, whitePaint);
//    drawLine(canvas, leftAnkle, leftHeel, whitePaint);
//    drawLine(canvas, leftHeel, leftFootIndex, whitePaint);
//
//    // Right body
//    drawLine(canvas, rightShoulder, rightElbow, whitePaint);
//    drawLine(canvas, rightElbow, rightWrist, whitePaint);
//    drawLine(canvas, rightShoulder, rightHip, whitePaint);
//    drawLine(canvas, rightHip, rightKnee, whitePaint);
//    drawLine(canvas, rightKnee, rightAnkle, whitePaint);
//    drawLine(canvas, rightWrist, rightThumb, whitePaint);
//    drawLine(canvas, rightWrist, rightPinky, whitePaint);
//    drawLine(canvas, rightWrist, rightIndex, whitePaint);
//    drawLine(canvas, rightIndex, rightPinky, whitePaint);
//    drawLine(canvas, rightAnkle, rightHeel, whitePaint);
           //  drawLine(canvas, whitePaint);

//    drawPoint(canvas, rightShoulder, whitePaint);
//    drawPoint(canvas, rightElbow, whitePaint);
//    drawPoint(canvas, rightHip, whitePaint);
//    drawPoint(canvas, rightHeel, whitePaint);
//    drawPoint(canvas, rightKnee, whitePaint);
//
//    drawPoint(canvas, leftShoulder, whitePaint);
//    drawPoint(canvas, leftElbow, whitePaint);
//    drawPoint(canvas, leftHip, whitePaint);
//    drawPoint(canvas, leftHeel, whitePaint);
           // drawPoint(canvas, whitePaint);
       }

  }




  void drawPoint(Canvas canvas,float X,float Y,Paint paint) {
//    PointF3D point = landmark.getPosition3D();
    //updatePaintColorByZValue(
    //       paint, canvas, visualizeZ, rescaleZForVisualization, point.getZ(), zMin, zMax);
//    canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, paint);
    canvas.drawCircle(X, Y, DOT_RADIUS, paint);
  }

  void drawLine(Canvas canvas,float startX,float startY,float endX,float endY,  Paint paint) {
   // PointF3D start = startLandmark.getPosition3D();
   // PointF3D end = endLandmark.getPosition3D();

    // Gets average z for the current body line
   // float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;

    canvas.drawLine(
            startX,startY,endX,endY,
            paint);
  }




}

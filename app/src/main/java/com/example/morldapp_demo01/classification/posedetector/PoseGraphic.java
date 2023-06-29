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

import com.example.morldapp_demo01.Edit.CalculateScore;
import com.example.morldapp_demo01.Edit.structurepoint;
import com.example.morldapp_demo01.GraphicOverlay;
import com.example.morldapp_demo01.GraphicOverlay.Graphic;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;

/** Draw the detected pose in preview. */
public class PoseGraphic extends Graphic {

  private static final float DOT_RADIUS = 15.0f;
  private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
  private static final float STROKE_WIDTH = 12.0f;
  private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;

  private final Pose pose;
  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private float zMin = Float.MAX_VALUE;
  private float zMax = Float.MIN_VALUE;

  private final boolean posedetectresult = false;

  private final List<String> poseClassification;

  private final Paint classificationTextPaint;
  private final Paint RedPaint;

  private structurepoint[] Userposestructurepoint=new structurepoint[12];

  PoseGraphic(
          GraphicOverlay overlay,
          Pose pose,
          boolean showInFrameLikelihood,
          boolean visualizeZ,
          boolean rescaleZForVisualization,
          List<String> poseClassification) {
    super(overlay);
    this.pose = pose;
    this.showInFrameLikelihood = showInFrameLikelihood;
    this.visualizeZ = visualizeZ;
    this.rescaleZForVisualization = rescaleZForVisualization;

    this.poseClassification = poseClassification;
    classificationTextPaint = new Paint();
    classificationTextPaint.setColor(Color.WHITE);
    classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);
    classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);

    RedPaint = new Paint();
    RedPaint.setStrokeWidth(STROKE_WIDTH);
    RedPaint.setColor(Color.RED);
    RedPaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

  }

  @Override
  public void draw(Canvas canvas) {
    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
    if (landmarks.isEmpty()) {
      return;
    }
    else
    {
    }


    PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
    PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
    PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
    PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
    PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
    PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
    PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
    PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
    PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
    PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
    PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
    PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

    drawLine(canvas, leftShoulder, rightShoulder, RedPaint);
    drawLine(canvas, leftHip, rightHip, RedPaint);

    // Left body
    drawLine(canvas, leftShoulder, leftElbow, RedPaint);
    drawLine(canvas, leftElbow, leftWrist, RedPaint);
    drawLine2(canvas, leftShoulder, leftHip, RedPaint);
    drawLine3(canvas, leftHip, leftShoulder, RedPaint);
    drawLine(canvas, leftHip, leftKnee, RedPaint);
    drawLine(canvas, leftKnee, leftAnkle, RedPaint);

    // Right body
    drawLine(canvas, rightShoulder, rightElbow, RedPaint);
    drawLine(canvas, rightElbow, rightWrist, RedPaint);
    drawLine2(canvas, rightShoulder, rightHip, RedPaint);
    drawLine3(canvas, rightHip, rightShoulder, RedPaint);
    drawLine(canvas, rightHip, rightKnee, RedPaint);
    drawLine(canvas, rightKnee, rightAnkle, RedPaint);

    drawPoint(canvas, rightShoulder, RedPaint);
    drawPoint(canvas, rightElbow, RedPaint);
    drawPoint(canvas, rightHip, RedPaint);
    drawPoint(canvas, rightKnee, RedPaint);
    drawPoint(canvas, rightAnkle, RedPaint);
    drawPoint(canvas, rightWrist, RedPaint);

    drawPoint(canvas, leftShoulder, RedPaint);
    drawPoint(canvas, leftElbow, RedPaint);
    drawPoint(canvas, leftHip, RedPaint);
    drawPoint(canvas, leftKnee, RedPaint);
    drawPoint(canvas, leftAnkle, RedPaint);
    drawPoint(canvas, leftWrist, RedPaint);

    drawPoint2(canvas, leftShoulder, leftHip, RedPaint);
    drawPoint2(canvas, rightShoulder, rightHip, RedPaint);


      //n≈≈ew CalculateScore(true);
      CalculateScore.getScore(pose);


  }



  void drawPoint(Canvas canvas, PoseLandmark landmark, Paint paint) {
    PointF3D point = landmark.getPosition3D();
    canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, paint);
  }

  void drawPoint2(Canvas canvas, PoseLandmark landmark, PoseLandmark endLandmark, Paint paint) {
    PointF3D point = landmark.getPosition3D();
    PointF3D point2 = endLandmark.getPosition3D();
      canvas.drawCircle(translateX((point.getX()*1/3+point2.getX()*2/3)), translateY((point.getY()*1/3+point2.getY()*2/3)), DOT_RADIUS, paint);
  }

  void drawLine(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
    PointF3D start = startLandmark.getPosition3D();
    PointF3D end = endLandmark.getPosition3D();

    // Gets average z for the current body line
    float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;

    canvas.drawLine(
            translateX(start.getX()),
            translateY(start.getY()),
            translateX(end.getX()),
            translateY(end.getY()),
            paint);
  }

  void drawLine2(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
    PointF3D start = startLandmark.getPosition3D();
    PointF3D end = endLandmark.getPosition3D();

    // Gets average z for the current body line
    float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;

    canvas.drawLine(
            translateX(start.getX()),
            translateY(start.getY()),
            translateX(start.getX()*1/3+end.getX()*2/3),
            translateY(start.getY()*1/3+end.getY()*2/3),
            paint);
  }
  void drawLine3(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
    PointF3D start = startLandmark.getPosition3D();
    PointF3D end = endLandmark.getPosition3D();

    // Gets average z for the current body line
    float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;

    canvas.drawLine(
            translateX(start.getX()),
            translateY(start.getY()),
            translateX(start.getX()*2/3+end.getX()*1/3),
            translateY(start.getY()*2/3+end.getY()*1/3),
            paint);
  }
  double Diff(PoseLandmark landmark,float ansX,float ansY) {
    PointF3D point = landmark.getPosition3D();
    float X=0,Y=0;
    double Result=0;
    X=translateX(point.getX());
    Y=translateY(point.getY());
    Result=Math.pow((double)((X-ansX)*(X-ansX)/200000)+(double)((Y-ansY)*(Y-ansY)/200000),1);

    return Result;
  }
  public float translateX(float x) {
    return overlay.scaleFactor * x;
  }
  public float translateY(float y) {
    return overlay.scaleFactor * y;
  }

}
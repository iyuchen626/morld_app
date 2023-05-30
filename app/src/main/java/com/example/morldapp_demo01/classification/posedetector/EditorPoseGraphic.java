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

import com.example.morldapp_demo01.Edit.FileMangement;
import com.example.morldapp_demo01.GraphicOverlay;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/** Draw the detected pose in preview. */
public class EditorPoseGraphic extends GraphicOverlay.Graphic {

  private static final float DOT_RADIUS = 8.0f;
  private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
  private static final float STROKE_WIDTH = 7.0f;
  private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;

  private final Pose pose;
  private final int count;

  private float zMin = Float.MAX_VALUE;
  private float zMax = Float.MIN_VALUE;

  private final boolean posedetectresult = false;


  private final Paint classificationTextPaint;
  private final Paint whitePaint;

   public EditorPoseGraphic(
           GraphicOverlay overlay,
           Pose pose,int count) {
    super(overlay);
    this.pose = pose;
    this.count = count;

    classificationTextPaint = new Paint();
    classificationTextPaint.setColor(Color.WHITE);
    classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);
    classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);

    whitePaint = new Paint();
    whitePaint.setStrokeWidth(STROKE_WIDTH);
    whitePaint.setColor(Color.WHITE);
    whitePaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

  }

  @Override
  public void draw(Canvas canvas) {

       List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
       if (landmarks.isEmpty()) {
         return;
       } else {

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


           drawLine(canvas, leftShoulder, rightShoulder, whitePaint);
           drawLine(canvas, leftHip, rightHip, whitePaint);

           // Left body
           drawLine(canvas, leftShoulder, leftElbow, whitePaint);
           drawLine(canvas, leftElbow, leftWrist, whitePaint);
           drawLine2(canvas, leftShoulder, leftHip, whitePaint);
           drawLine3(canvas, leftHip, leftShoulder, whitePaint);
           drawLine(canvas, leftHip, leftKnee, whitePaint);
           drawLine(canvas, leftKnee, leftAnkle, whitePaint);

           // Right body
           drawLine(canvas, rightShoulder, rightElbow, whitePaint);
           drawLine(canvas, rightElbow, rightWrist, whitePaint);
           drawLine2(canvas, rightShoulder, rightHip, whitePaint);
           drawLine3(canvas, rightHip, rightShoulder, whitePaint);
           drawLine(canvas, rightHip, rightKnee, whitePaint);
           drawLine(canvas, rightKnee, rightAnkle, whitePaint);

           drawPoint(canvas, rightShoulder, whitePaint);
           drawPoint(canvas, rightElbow, whitePaint);
           drawPoint(canvas, rightHip, whitePaint);
           drawPoint(canvas, rightKnee, whitePaint);
           drawPoint(canvas, rightAnkle, whitePaint);
           drawPoint(canvas, rightWrist, whitePaint);

           drawPoint(canvas, leftShoulder, whitePaint);
           drawPoint(canvas, leftElbow, whitePaint);
           drawPoint(canvas, leftHip, whitePaint);
           drawPoint(canvas, leftKnee, whitePaint);
           drawPoint(canvas, leftAnkle, whitePaint);
           drawPoint(canvas, leftWrist, whitePaint);

       }

  }


    void drawPoint(Canvas canvas, PoseLandmark landmark, Paint paint) {
    PointF3D point = landmark.getPosition3D();
    //updatePaintColorByZValue(
    //       paint, canvas, visualizeZ, rescaleZForVisualization, point.getZ(), zMin, zMax);
    canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, paint);
  }

  void drawLine(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
    PointF3D start = startLandmark.getPosition3D();
    PointF3D end = endLandmark.getPosition3D();

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
}

package com.example.morldapp_demo01.classification.posedetector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.util.List;

public class CanvasView extends View {

    private  Pose pose;
    Context context;
    private Paint   mBitmapPaint;
    private Bitmap mBitmap,background;
    private Canvas  mCanvas;
    private PoseDetector poseDetector ;
    private static final float DOT_RADIUS = 8.0f;

    //畫筆
    private Path mPath;

    private Paint circlePaint,mPaint;
    private Path circlePath;
    //暫存使用者手指的X,Y座標
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    Pose Editor_pose;


    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //this.pose = pose;
        //this.Editor_pose=pose;

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //畫點選畫面時顯示的圈圈
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        //繪製線條
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

       // background = BitmapFactory.decodeResource(getResources(),R);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化空畫布
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //不要背景圖的話，請從這邊刪
        @SuppressLint("DrawAllocation")
        Bitmap res = Bitmap.createScaledBitmap(background
                ,getWidth(),getHeight(),true);
        canvas.drawBitmap(res,0,0,mBitmapPaint);
        //到這邊

        //取得上一個動作所畫過的內容
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        //依據移動路徑畫線
        canvas.drawPath( mPath,  mPaint);
        //畫圓圈圈
        canvas.drawPath( circlePath,  circlePaint);
    }

    /**覆寫:偵測使用者觸碰螢幕的事件*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(),y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                break;
        }
        invalidate();
        return true;

    }
    /**觸碰到螢幕時，取得手指的X,Y座標；並順便設定為線的起點*/
    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    /**在螢幕上滑動時，不斷刷新移動路徑*/
    private void touch_move(float x, float y) {
        //取得目前位置與前一點位置的X距離
        float dx = Math.abs(x - mX);
        //取得目前位置與前一點位置的Y距離
        float dy = Math.abs(y - mY);
        //判斷此兩點距離是否有大於預設的最小值，有才把他畫進去
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            //畫貝爾茲曲線
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            //更新上一點X座標
            mX = x;
            //更新上一點Y座標
            mY = y;

            //消滅上一點時的小圈圈位置
            circlePath.reset();
            //更新小圈圈的位置
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }
    /**當使用者放開時，把位置設為終點*/
    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        mCanvas.drawPath(mPath,  mPaint);
        mPath.reset();
    }
    /**清除所有畫筆內容*/
    public void clear(){
        setDrawingCacheEnabled(false);
        onSizeChanged(getWidth(),getHeight(),getWidth(),getHeight());
        invalidate();
        setDrawingCacheEnabled(true);

    }
    /**設置接口從外部設置畫筆顏色*/
    public void setColor(int color){
        mPaint.setColor(color);
    }

    /**設置接口從外部設置背景圖片*/
    public void setPosePoint(){
        //Bitmap bitmap
//        AccuratePoseDetectorOptions options =
//                new AccuratePoseDetectorOptions.Builder()
//                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
//                        .build();
//
//        poseDetector = PoseDetection.getClient(options);
//        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
//        Task<Pose> result = poseDetector.process(bitmap,0);
//
//        List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
//        if (landmarks.isEmpty()) {
//            return;
//        }
//
//        PoseLandmark nose = pose.getPoseLandmark(PoseLandmark.NOSE);
//        PoseLandmark lefyEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER);
//        PoseLandmark lefyEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE);
//        PoseLandmark leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER);
//        PoseLandmark rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER);
//        PoseLandmark rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE);
//        PoseLandmark rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER);
//        PoseLandmark leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR);
//        PoseLandmark rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR);
//        PoseLandmark leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH);
//        PoseLandmark rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH);
//
//        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
//        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
//        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
//        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
//        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
//        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
//        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
//        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
//        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
//        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
//        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
//        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
//
//        PoseLandmark leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY);
//        PoseLandmark rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY);
//        PoseLandmark leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX);
//        PoseLandmark rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX);
//        PoseLandmark leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB);
//        PoseLandmark rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB);
//        PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);
//        PoseLandmark rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL);
//        PoseLandmark leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX);
//        PoseLandmark rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX);

        mCanvas.drawLine(
                0,
                0,
                340,
               370,
                mPaint);
//        // Face
//        drawLine_pose(mCanvas, nose, lefyEyeInner, circlePaint);
//        drawLine_pose(mCanvas, lefyEyeInner, lefyEye, circlePaint);
//        drawLine_pose(mCanvas, lefyEye, leftEyeOuter, circlePaint);
//        drawLine_pose(mCanvas, leftEyeOuter, leftEar, circlePaint);
//        drawLine_pose(mCanvas, nose, rightEyeInner, circlePaint);
//        drawLine_pose(mCanvas, rightEyeInner, rightEye, circlePaint);
//        drawLine_pose(mCanvas, rightEye, rightEyeOuter, circlePaint);
//        drawLine_pose(mCanvas, rightEyeOuter, rightEar, circlePaint);
//        drawLine_pose(mCanvas, leftMouth, rightMouth, circlePaint);
//
//        drawLine_pose(mCanvas, leftShoulder, rightShoulder, circlePaint);
//        drawLine_pose(mCanvas, leftHip, rightHip, circlePaint);
//
//        // Left body
//        drawLine_pose(mCanvas, leftShoulder, leftElbow, circlePaint);
//        drawLine_pose(mCanvas, leftElbow, leftWrist, circlePaint);
//        drawLine_pose(mCanvas, leftShoulder, leftHip, circlePaint);
//        drawLine_pose(mCanvas, leftHip, leftKnee, circlePaint);
//        drawLine_pose(mCanvas, leftKnee, leftAnkle, circlePaint);
//        drawLine_pose(mCanvas, leftWrist, leftThumb, circlePaint);
//        drawLine_pose(mCanvas, leftWrist, leftPinky, circlePaint);
//        drawLine_pose(mCanvas, leftWrist, leftIndex, circlePaint);
//        drawLine_pose(mCanvas, leftIndex, leftPinky, circlePaint);
//        drawLine_pose(mCanvas, leftAnkle, leftHeel, circlePaint);
//        drawLine_pose(mCanvas, leftHeel, leftFootIndex, circlePaint);
//
//        // Right body
//        drawLine_pose(mCanvas, rightShoulder, rightElbow, circlePaint);
//        drawLine_pose(mCanvas, rightElbow, rightWrist, circlePaint);
//        drawLine_pose(mCanvas, rightShoulder, rightHip, circlePaint);
//        drawLine_pose(mCanvas, rightHip, rightKnee, circlePaint);
//        drawLine_pose(mCanvas, rightKnee, rightAnkle, circlePaint);
//        drawLine_pose(mCanvas, rightWrist, rightThumb, circlePaint);
//        drawLine_pose(mCanvas, rightWrist, rightPinky, circlePaint);
//        drawLine_pose(mCanvas, rightWrist, rightIndex, circlePaint);
//        drawLine_pose(mCanvas, rightIndex, rightPinky, circlePaint);
//        drawLine_pose(mCanvas, rightAnkle, rightHeel, circlePaint);
//        drawLine_pose(mCanvas, rightHeel, rightFootIndex, circlePaint);
//
//        drawPoint_pose(mCanvas, rightShoulder, circlePaint);
//        drawPoint_pose(mCanvas, rightElbow, circlePaint);
//        drawPoint_pose(mCanvas, rightHip, circlePaint);
//        drawPoint_pose(mCanvas, rightHeel, circlePaint);
//        drawPoint_pose(mCanvas, rightKnee, circlePaint);
//
//        drawPoint_pose(mCanvas, leftShoulder, circlePaint);
//        drawPoint_pose(mCanvas, leftElbow, circlePaint);
//        drawPoint_pose(mCanvas, leftHip, circlePaint);
//        drawPoint_pose(mCanvas, leftHeel, circlePaint);
//        drawPoint_pose(mCanvas, leftKnee, circlePaint);

    }

    /**設置接口從外部設置背景圖片*/
    public void setBackground(Bitmap bitmap){
        //BitmapFactory.decodeResource(getResources(),R.drawable.aaaaa);
        background = bitmap;
        invalidate();
    }

    void drawPoint_pose(Canvas canvas, PoseLandmark landmark, Paint paint) {
        PointF3D point = landmark.getPosition3D();
        //updatePaintColorByZValue(
        //       paint, canvas, visualizeZ, rescaleZForVisualization, point.getZ(), zMin, zMax);
        //canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, paint);
        canvas.drawCircle(point.getX(), point.getY(), DOT_RADIUS, paint);

    }

    void drawLine_pose(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
        PointF3D start = startLandmark.getPosition3D();
        PointF3D end = endLandmark.getPosition3D();

        // Gets average z for the current body line
        float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;

//        canvas.drawLine(
//                translateX(start.getX()),
//                translateY(start.getY()),
//                translateX(end.getX()),
//                translateY(end.getY()),
//                paint);
        canvas.drawLine(
                start.getX(),
                start.getY(),
                end.getX(),
                end.getY(),
                paint);
    }
}
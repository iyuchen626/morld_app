package com.example.morldapp_demo01.Edit;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.ducky.fastvideoframeextraction.fastextraction.FrameExtractor;
import com.example.morldapp_demo01.GraphicOverlay;

public class AnalyzePoseGraphic extends GraphicOverlay.Graphic{

    private static final float DOT_RADIUS = 8.0f;
    private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
    private static final float STROKE_WIDTH = 7.0f;
    private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;
    private final Paint BluePaint;
    structurepoint[] structurepoint= new structurepoint[12];
    GraphicOverlay graphicOverlay;
    float height=0, width=0;

    public AnalyzePoseGraphic(GraphicOverlay overlay,structurepoint[] structurepoint,float height,float width) {
        super(overlay);
        this.graphicOverlay = overlay;
        BluePaint = new Paint();
        BluePaint.setStrokeWidth(STROKE_WIDTH);
        BluePaint.setColor(Color.BLUE);
        BluePaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);
        this.structurepoint=structurepoint;
        this.height=height;
        this.width=width;

    }

    @Override
    public void draw(Canvas canvas) {
//        float scale1 = (float) (canvas.getHeight() / 600);
//        float scale2 = (float) (canvas.getWidth() / 276);
        float scale1 = (float) (canvas.getHeight())/(float)height;
        float scale2 = (float) (canvas.getWidth())/(float)width;

        //if(scale2 > scale1) scale1 = scale2;
        //scale1*=1.4;
//        float scale1 = (float) canvas.getHeight();
//        float scale2 = (float) canvas.getWidth();
//        if(scale2 < scale1) scale1 = scale2;
//        scale1 = scale1 / FrameExtractor.MAX_RESOLUTION;
//        scale1 *= 1;
//        scale1 *= offset;
//        float hDes = (float) (-canvas.getHeight()*0.01) * offset;
        if(structurepoint==null) return;
        for (int i = 0; i < 12; i++)
        {
            if (structurepoint[i] == null)
            {
                return;
            }
        }

//        canvas.translate(0, hDes);
         canvas.scale((float) scale1, (float)scale2);
        drawLine(canvas, structurepoint[6],structurepoint[0], BluePaint);
        drawLine(canvas, structurepoint[8],structurepoint[2], BluePaint);
        drawLine(canvas, structurepoint[6],structurepoint[7], BluePaint);
        drawLine(canvas, structurepoint[7],structurepoint[11], BluePaint);
        drawLine(canvas, structurepoint[6],structurepoint[8], BluePaint);
        drawLine(canvas, structurepoint[8],structurepoint[6], BluePaint);
        drawLine(canvas, structurepoint[8],structurepoint[9], BluePaint);
        drawLine(canvas, structurepoint[9],structurepoint[10], BluePaint);
        drawLine(canvas, structurepoint[0],structurepoint[1], BluePaint);
        drawLine(canvas, structurepoint[1],structurepoint[5], BluePaint);
        drawLine(canvas, structurepoint[0],structurepoint[2], BluePaint);
        drawLine(canvas, structurepoint[2],structurepoint[0], BluePaint);
        drawLine(canvas, structurepoint[2],structurepoint[3], BluePaint);
        drawLine(canvas, structurepoint[3],structurepoint[4], BluePaint);

        drawPoint(canvas,structurepoint[0], BluePaint);
        drawPoint(canvas,structurepoint[1], BluePaint);
        drawPoint(canvas,structurepoint[2], BluePaint);
        drawPoint(canvas,structurepoint[3], BluePaint);
        drawPoint(canvas,structurepoint[4], BluePaint);
        drawPoint(canvas,structurepoint[5], BluePaint);
        drawPoint(canvas,structurepoint[6], BluePaint);
        drawPoint(canvas,structurepoint[7], BluePaint);
        drawPoint(canvas,structurepoint[8], BluePaint);
        drawPoint(canvas,structurepoint[9], BluePaint);
        drawPoint(canvas,structurepoint[10], BluePaint);
        drawPoint(canvas,structurepoint[11], BluePaint);


    }

    void drawLine(Canvas canvas,structurepoint startstructurepoint,structurepoint endstructurepoint,  Paint paint) {
        canvas.drawLine(
                translateX(startstructurepoint.getStructpoint_x()),translateY(startstructurepoint.getStructpoint_y()),translateX(endstructurepoint.getStructpoint_x()),translateY(endstructurepoint.getStructpoint_y()),
                paint);
    }

    void drawPoint(Canvas canvas,structurepoint startstructurepoint,Paint paint) {
    canvas.drawCircle(translateX(startstructurepoint.getStructpoint_x()), translateY(startstructurepoint.getStructpoint_y()), DOT_RADIUS, paint);
    }
}

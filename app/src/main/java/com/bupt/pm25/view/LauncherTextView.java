/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.bupt.pm25.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by miguangshu on 2016/5/6.
 */
public class LauncherTextView extends View {

    private Paint txtPaint;
    private Shader shader;
    private float dx = 50;
    private long lastTime = System.currentTimeMillis();
    private boolean start = false;
    private float height, width;

    private void init() {
        txtPaint = new Paint();
        txtPaint.setColor(Color.BLUE);
        txtPaint.setAntiAlias(true);
        // txtPaint.setTextAlign(Paint.Align .RIGHT);
        height = this.getHeight();
        width = this.getWidth();

        shader = new LinearGradient(0, 0, 200, 0, new int[] { Color.BLUE,
                Color.GREEN, Color.WHITE }, new float[] { 0, 0.7f, 1 },
                Shader.TileMode.MIRROR);
        // shader = new LinearGradient(0, 0, 200, 0,
        // new int[]{Color.argb(255, 120, 120, 120), Color.argb(255, 120, 120,
        // 120), Color.WHITE}, new float[]{0, 0.7f, 1}, TileMode.MIRROR);
        txtPaint.setShader(shader);
    }

    public LauncherTextView(Context context) {

        super(context);
        init();
    }

    public LauncherTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setStart(boolean start) {
        this.start = start;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w / 2f;
        height = h / 2f;

        Log.d("size", "onSizeChanged: width " + width + ", height " + height);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long now = System.currentTimeMillis();
        float elapsed = (now - lastTime) / 4.5f;
        dx += elapsed;
        Matrix matrix = new Matrix();
        if (start) {
            matrix.setTranslate(dx, 0);
            invalidate();
        } else {
            matrix.setTranslate(0, 0);
        }
        txtPaint.setTextSize(height*1.2f);
        //  txtPaint.setTextScaleX(width / height);
        txtPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = txtPaint.getFontMetrics();
        shader.setLocalMatrix(matrix);
        canvas.drawText("开天眼", width ,
                height / 2 - (fm.ascent + fm.descent) / 2, txtPaint);



        lastTime = now;

    }
}

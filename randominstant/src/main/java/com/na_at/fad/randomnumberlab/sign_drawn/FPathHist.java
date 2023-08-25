package com.na_at.fad.randomnumberlab.sign_drawn;

import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

class FPathHist implements Serializable {

    private static final String TAG = FPathHist.class.getSimpleName();

    private ArrayList<FPoint> points = new ArrayList<>();
    private int paintColor;
    private int paintAlpha;
    private float paintWidth;
    private float originX, originY;
    private boolean isPoint;

    private transient Path path = null;
    private transient Paint paint = null;

    FPathHist(@NonNull ArrayList<FPoint> points, @NonNull Paint paint) {
        this.points = new ArrayList<>(points);
        this.paintColor = paint.getColor();
        this.paintAlpha = paint.getAlpha();
        this.paintWidth = paint.getStrokeWidth();
        this.originX = points.get(0).x;
        this.originY = points.get(0).y;
        this.isPoint = DrawUtils.isAPoint(points);

        generatePath();
        generatePaint();
    }

    public void generatePath() {

        path = new Path();

        if (points != null) {
            boolean first = true;

            for (int i = 0; i < points.size(); i++) {

                FPoint FPoint = points.get(i);

                if (first) {
                    path.moveTo(FPoint.x, FPoint.y);
                    first = false;
                } else {
                    path.lineTo(FPoint.x, FPoint.y);
                }
            }
        }
    }

    private void generatePaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);;
        if (isPoint) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setPathEffect(new ComposePathEffect(
                    new CornerPathEffect(100f),
                    new CornerPathEffect(100f)));
            paint.setStyle(Paint.Style.STROKE);
        }

        paint.setStrokeWidth(paintWidth);
        paint.setColor(paintColor);
        paint.setAlpha(paintAlpha);
    }

    public Path getPath() {

        if (path == null) {

            generatePath();
        }

        return path;
    }

    public boolean isPoint() {
        return isPoint;
    }

    public void setPoint(boolean point) {
        isPoint = point;
    }

    public float getOriginX() {
        return originX;
    }

    public void setOriginX(float originX) {
        this.originX = originX;
    }

    public float getOriginY() {
        return originY;
    }

    public void setOriginY(float originY) {
        this.originY = originY;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public int getPaintAlpha() {
        return paintAlpha;
    }

    public void setPaintAlpha(int paintAlpha) {
        this.paintAlpha = paintAlpha;
    }

    public float getPaintWidth() {
        return paintWidth;
    }

    public void setPaintWidth(float paintWidth) {
        this.paintWidth = paintWidth;
    }

    public Paint getPaint() {

        if (paint == null) {
            generatePaint();
        }

        return paint;
    }

    public ArrayList<FPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<FPoint> FPoints) {
        this.points = FPoints;
    }

    @Override
    public String toString() {
        return "Point: " + isPoint + "\n" +
                "Points: " + points + "\n" +
                "Color: " + paintColor + "\n" +
                "Alpha: " + paintAlpha + "\n" +
                "Width: " + paintWidth;
    }
}
package com.na_at.fad.randomnumberlab.sign_drawn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.na_at.fad.randomnumberlab.sign_drawn.model.Signature;
import com.na_at.fad.randomnumberlab.sign_drawn.model.SignaturePoint;

import java.util.ArrayList;

public class DrawingCanvasView extends View {

    private static final String TAG = DrawingCanvasView.class.getSimpleName();

    private Path mPath;
    private Paint mPaint;
    private Paint canvasPaint;
    private Paint circlePaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Signature signature;
    private DrawingCanvasListener drawingSignerListener;

    private boolean bgColorDrawn = false;
    private float mX;
    private float mY;
    private float downX;
    private float downY;
    private static final float TOUCH_TOLERANCE = 4;

    private Paint mBitmapPaint;
    private Canvas temp;
    boolean isMove = false;

    // new model & properties
    private boolean legacyDraw = false;
    private boolean mFinishPath = false;
    private Paint mCurrentPaint;
    private Path mCurrentPath;
    private boolean ignoreEvents = false;

    private ArrayList<FPoint> mFPoints = new ArrayList<>();
    private ArrayList<FPathHist> mPaths = new ArrayList<>();

    public DrawingCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        signature = new Signature();
        setFocusable(true);
        setClickable(true);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        if (ignoreEvents) return super.onHoverEvent(event);

        if (event.getPointerCount() == 1) {
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_HOVER_ENTER: {
                    event.setAction(MotionEvent.ACTION_DOWN);
                }
                break;
                case MotionEvent.ACTION_HOVER_MOVE: {
                    event.setAction(MotionEvent.ACTION_MOVE);
                }
                break;
                case MotionEvent.ACTION_HOVER_EXIT: {
                    event.setAction(MotionEvent.ACTION_UP);
                }
                break;
            }
            return onTouchEvent(event);
        }
        return true;
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    private void setupDrawing() {
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#000000"));
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setStrokeWidth(8);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeJoin(Paint.Join.ROUND);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        int mPaintColor = Color.parseColor("#000000");
        int mPaintAlpha = 255;
        mCurrentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurrentPaint.setColor(mPaintColor);
        mCurrentPaint.setAlpha(mPaintAlpha);
        mCurrentPaint.setStrokeWidth(8);
        mCurrentPaint.setStrokeJoin(Paint.Join.ROUND);
        mCurrentPaint.setStrokeCap(Paint.Cap.ROUND);
        mCurrentPaint.setPathEffect(new ComposePathEffect(
                new CornerPathEffect(100f),
                new CornerPathEffect(100f)));
        mCurrentPaint.setStyle(Paint.Style.STROKE);

        mPaths.clear();
        mFPoints.clear();
    }

    public void setDrawingSignerListener(DrawingCanvasListener drawingSignerListener) {
        this.drawingSignerListener = drawingSignerListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        mFPoints.clear();
    }

    public void reset() {
        setupDrawing();
        // default values
        temp = null;
        bgColorDrawn = false;
        signature = new Signature();
        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    @SuppressLint("CanvasSize")
    protected void onDraw(Canvas canvas) {
        if (legacyDraw) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            canvas.drawPath(mPath, mPaint);
            if (canvasBitmap == null) {
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                Log.d(TAG, "Drawing on: " + width + "x" + height);
                canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }

            if (temp == null)
                temp = new Canvas(canvasBitmap);

            if (!bgColorDrawn) {
                Bitmap backg = Bitmap.createBitmap(canvasBitmap.getWidth(), canvasBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                backg.eraseColor(Color.WHITE);
                BitmapShader backshader = new BitmapShader(backg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(backshader);
                temp.drawBitmap(backg, 0, 0, paint);
                bgColorDrawn = true;
            }
            temp.drawBitmap(canvasBitmap, 0, 0, mBitmapPaint);
            temp.drawPath(mPath, mPaint);
        } else {
            // handle concurrency errors
            final boolean finishedPath = mFinishPath;
            mFinishPath = false;


            if (mPaths.isEmpty()) {
                Bitmap backg = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                backg.eraseColor(Color.WHITE);
                BitmapShader backshader = new BitmapShader(backg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(backshader);
                canvas.drawBitmap(backg, 0, 0, paint);
                drawCanvas.drawBitmap(backg, 0, 0, paint);
            }

            for (FPathHist currentPath : mPaths) {
                // draw single point
                if (currentPath.isPoint()) {
                    canvas.drawCircle(currentPath.getOriginX(), currentPath.getOriginY(),
                            currentPath.getPaint().getStrokeWidth() / 2, currentPath.getPaint());
                } else {
                    // draw path
                    canvas.drawPath(currentPath.getPath(), currentPath.getPaint());
                }
            }

            // new path
            if (mCurrentPath == null) {
                mCurrentPath = new Path();
            } else {
                mCurrentPath.rewind();
            }

            // draw single point
            if (mFPoints.size() == 1 || DrawUtils.isAPoint(mFPoints)) {
                canvas.drawCircle(mFPoints.get(0).x, mFPoints.get(0).y,
                        mCurrentPaint.getStrokeWidth() / 2,
                        createAndCopyColorAndAlphaForFillPaint(mCurrentPaint));
                drawCanvas.drawCircle(mFPoints.get(0).x, mFPoints.get(0).y,
                        mCurrentPaint.getStrokeWidth() / 2,
                        createAndCopyColorAndAlphaForFillPaint(mCurrentPaint));
            } else if (mFPoints.size() != 0) {
                // draw path
                boolean first = true;
                for (FPoint FPoint : mFPoints) {
                    if (first) {
                        mCurrentPath.moveTo(FPoint.x, FPoint.y);
                        first = false;
                    } else {
                        mCurrentPath.lineTo(FPoint.x, FPoint.y);
                    }
                }
                canvas.drawPath(mCurrentPath, mCurrentPaint);
                drawCanvas.drawPath(mCurrentPath, mCurrentPaint);
            }

            // add to history
            if (finishedPath && mFPoints.size() > 0) {
                addNewTrace();
            }
        }

    }

    private void addNewTrace() {
        mPaths.add(new FPathHist(mFPoints, new Paint(mCurrentPaint)));
        mFPoints = new ArrayList<>();
    }

    private Paint createAndCopyColorAndAlphaForFillPaint(Paint from) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(from.getColor());
        paint.setAlpha(from.getAlpha());
        return paint;
    }

    public void ignoreEvents() {
        ignoreEvents = true;
        signature.calculateSpeed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ignoreEvents) return super.onTouchEvent(event);

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = false;
                downX = touchX;
                downY = touchY;
                touchStart(touchX, touchY);
                signature.addAttackPoint();
                signature.addSignaturePoint(new SignaturePoint(touchX, touchY, System.currentTimeMillis()));
                drawingSignerListener.onDrawDown(signature);

                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                touchMove(touchX, touchY);
                signature.addSignaturePoint(new SignaturePoint(touchX, touchY, System.currentTimeMillis()));
                break;
            case MotionEvent.ACTION_UP:

                signature.addSignaturePoint(new SignaturePoint(touchX, touchY, System.currentTimeMillis()));
                signature.addSignaturePoint(new SignaturePoint(-1, -1, System.currentTimeMillis()));
                if (legacyDraw) {
                    if (checkPoints(touchX, touchY)) {
                        drawCicle();
                    } else {
                        touchUp();
                    }
                }
                signature.calculateSpeed();
                drawingSignerListener.onDrawUp(signature);

                break;
            default:
                return false;
        }
        // track points to draw
        MotionEvent motionEvent = event;
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if ((motionEvent.getAction() != MotionEvent.ACTION_UP) &&
                (motionEvent.getAction() != MotionEvent.ACTION_CANCEL)) {
            FPoint fPoint;
            for (int i = 0; i < motionEvent.getHistorySize(); i++) {
                fPoint = new FPoint();
                fPoint.x = motionEvent.getHistoricalX(i);
                fPoint.y = motionEvent.getHistoricalY(i);
                mFPoints.add(fPoint);
            }
            fPoint = new FPoint();
            fPoint.x = motionEvent.getX();
            fPoint.y = motionEvent.getY();
            mFPoints.add(fPoint);
            mFinishPath = false;
        } else {
            mFinishPath = true;
        }
        invalidate();
        return true;
    }

    private void touchStart(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
        drawCanvas.drawPath(mPath, mPaint);
    }

    private boolean checkPoints(float touchX, float touchY) {
        return (touchX == downX && touchY == downY);
    }

    private void drawCicle() {
        mPath.lineTo(mX, mY);
        drawCanvas.drawCircle(mX, mY, 6f, circlePaint);
        mPath.reset();
    }

    public void setColor(String newColor) {
        invalidate();
        mPaint.setColor(Color.parseColor(newColor));
    }

    public Signature getSignature() {
        return signature;
    }

}
package com.example.sungjoekim.drawingexample;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by JOE on 2015-10-23.
 */
public class DrawingView extends View {

    public MainActivity mActivity;

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint, textPaint;
    //initial color
    private int paintColor = 0xFF660000;
    public int paintTempColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;

    private boolean erase=false;

    public static int positionX, positionY;


    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){

        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPaint.setStrokeWidth(brushSize);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(70);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);

    }

    public void drawBmp(Bitmap bmp){

        int bw, bh;

        bw = bmp.getWidth();
        bh = bmp.getHeight();

        drawCanvas.drawRect(0, 0, bw, bh, canvasPaint);
        drawCanvas.drawBitmap(bmp, null, new Rect(0, 0, bw, bh), null);
    }

    public void drawText(String inputText){
        if(MainActivity.isTextMode){

            if(inputText !="" && inputText != null){
                //System.out.println("X & Y" + positionX + ":" + positionY);
                drawCanvas.drawText(inputText, positionX, positionY, textPaint);
                inputText = "";
                positionX = 0;
                positionY = 0;
                invalidate();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(MainActivity.isTextMode) {
                    positionX = (int) touchX;
                    positionY = (int) touchY;
                    mActivity.showInputDialog();
                }else {
                    drawPath.moveTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!MainActivity.isTextMode){
                    drawPath.lineTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!MainActivity.isTextMode) {
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                }
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor){
        //set color
        invalidate();
        if(MainActivity.isTextMode){
            paintColor = Color.parseColor(newColor);
            textPaint.setColor(paintColor);
        }else{
            paintColor = Color.parseColor(newColor);
            drawPaint.setColor(paintColor);
            paintTempColor = paintColor;
        }
    }

    public void setBrushSize(float newSize){
        //update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase){
        //set erase true or false
        erase=isErase;
        if(erase){
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            //this.setColor("#FFFFFF");
        }else{
            drawPaint.setXfermode(null);
        }
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}

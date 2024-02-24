package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class CurrentWord {

    private Rect boundsRect;
    private Rect limits;
    private int y;
    private String word;

    public CurrentWord(int x, int y, int w, int h){
        this.word = "";
        this.boundsRect = new Rect(x,y,x+w,y+h);

        int limitW = (int)(w*0.9);
        int limitH = (int)(h*0.9);
        int nx = x + (w - limitW)/2;
        int ny = y + (h - limitH)/2;

        this.limits = new Rect(nx,ny,nx + limitW,ny+limitH);



    }

    public void setWord(String w){
        this.word = w;
    }

    public void clear(){
        this.word = "";
    }

    public void render(Canvas canvas){

        if (this.word == "") return;

        Paint wordPaint = new Paint();
        wordPaint.setStyle(Paint.Style.FILL);
        wordPaint.setTextAlign(Paint.Align.CENTER);
        wordPaint.setTypeface(Utils.GetMessageTypeFace());
        wordPaint.setColor(Utils.TEXT_200);


        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Utils.BG_300);

        float r = 0.2f*this.boundsRect.height();
        canvas.drawRoundRect(this.boundsRect.left,this.boundsRect.top,this.boundsRect.right,this.boundsRect.bottom,r,r,p);

        // We draw the text.
        wordPaint.setTextSize(Utils.GetTextSizeToFitRect(this.word,limits.width(),limits.height(),wordPaint));
        float baseline = Utils.GetTextBaseLine(this.word,wordPaint,this.limits.centerY());
        canvas.drawText(word,this.limits.centerX(),baseline,wordPaint);

    }

}

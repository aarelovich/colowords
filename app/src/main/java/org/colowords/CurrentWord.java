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
    private Paint wordPaint;
    public CurrentWord(int x, int y, int w, int h){
        this.word = "";
        this.boundsRect = new Rect(x,y,x+w,y+h);

        int limitW = (int)(w*0.9);
        int limitH = (int)(h*0.9);
        int nx = x + (w - limitW)/2;
        int ny = y + (h - limitH)/2;

        this.limits = new Rect(nx,ny,nx + limitW,ny+limitH);

        this.wordPaint = new Paint();
        this.wordPaint.setStyle(Paint.Style.FILL);
        this.wordPaint.setTextAlign(Paint.Align.CENTER);
        Typeface font = Typeface.create("Mono",Typeface.BOLD);
        this.wordPaint.setTypeface(font);
        this.wordPaint.setColor(Utils.FORMED_WORD_LETTER);


    }

    public void setWord(String w){
        this.word = w;
    }

    public void clear(){
        this.word = "";
    }

    public void render(Canvas canvas){

        if (this.word == "") return;

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Utils.FORMED_WORD_BKG);

        float r = 0.2f*this.boundsRect.height();
        canvas.drawRoundRect(this.boundsRect.left,this.boundsRect.top,this.boundsRect.right,this.boundsRect.bottom,r,r,p);

        // We draw the text.
        this.wordPaint.setTextSize(Utils.GetTextSizeToFitRect(this.word,limits.width(),limits.height(),this.wordPaint));
        float baseline = Utils.GetTextBaseLine(this.word,this.wordPaint,this.limits.centerY());
        canvas.drawText(word,this.limits.centerX(),baseline,wordPaint);

    }

}

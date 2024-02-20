package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class CurrentWord {

    private int x;
    private int y;
    private String word;
    private Paint wordPaint;
    public CurrentWord(int x, int y, float text_size){
        this.word = "";
        this.x = x; // The xvalue is considered to be the CENTER of where the word will appear.
        this.y = y;

        this.wordPaint = new Paint();
        this.wordPaint.setStyle(Paint.Style.FILL);
        this.wordPaint.setTextAlign(Paint.Align.CENTER);
        Typeface font = Typeface.create("Mono",Typeface.BOLD);
        this.wordPaint.setTypeface(font);
        this.wordPaint.setTextSize(text_size);
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

        // We need to compute the rectangle to draw.
        Rect textBounds = new Rect();
        wordPaint.getTextBounds(this.word, 0, this.word.length(), textBounds);

        // We draw the rectangle. However we must expand it from the center and the top so it's a bit bigger than the actual word.
        float halfW = textBounds.width()*1.2f/2.0f;
        float newH = textBounds.height()*1.2f;
        float yOffset = (newH - textBounds.height())/2.0f;
        float r = newH*0.1f;

        canvas.drawRoundRect(this.x-halfW,this.y - textBounds.height() - yOffset,this.x + halfW,this.y + yOffset,r,r,p);

        // We draw the text.
        canvas.drawText(word,this.x,this.y,wordPaint);

    }

}

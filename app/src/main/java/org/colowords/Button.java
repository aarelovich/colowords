package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class Button {

    private String text;
    private RectF rect;
    private float rCorner;
    private float baseLine;
    private boolean visible;
    private boolean pressed;
    private Paint textPaint;

    public Button (int top, int left, int width, int height, String text){
        this.rect = new RectF(left,top,left+width,top+height);
        this.text = text;
        this.visible = true;
        this.pressed = false;

        // We will now compute the text size and the base line for text.
        // We start by imagining a smaller rect inside the text button.
        float textW   = width*0.9f;
        float textH   = height*0.9f;

        this.textPaint = new Paint();
        Typeface font = Typeface.create("Mono",Typeface.BOLD);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTypeface(font);
        this.textPaint.setTextSize(Utils.GetTextSizeToFitRect(text,textW,textH,this.textPaint));
        this.textPaint.setColor(Utils.BUTTON_TEXT_COLOR);

        this.baseLine = Utils.GetTextBaseLine(text,this.textPaint,(int)this.rect.centerY());

        this.rCorner = 0.05f*width;

    }

    public void setVisible(boolean v){
        this.visible = v;
    }

    public boolean getIsVisible() {
        return this.visible;
    }
    public boolean fingerDown(int x, int y){
        if (!this.visible) return false;
        if (this.pressed) return false;
        if (this.rect.contains(x,y)) {
            this.pressed = true;
            return true;
        }
        return false;
    }

    public boolean fingerUp(int x, int y){
        if (!this.visible) return false;
        if (!this.pressed) return false;
        // Whenever the finger is lifted the button state must change. But if it's listed outside
        // the button, then it's not clicked.
        this.pressed = false;
        if (this.rect.contains(x,y)) {
            return true;
        }
        return false;
    }

    public void render(Canvas canvas){

        if (!this.visible) return;

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);

        if (this.pressed) p.setColor(Utils.CIRCLE_SELECTOR_BKG_OPTION);
        else p.setColor(Utils.CIRCLE_SELECTOR_BKG_MAIN);

        // Draw the background.
        canvas.drawRoundRect(this.rect,this.rCorner,this.rCorner,p);

        // And draw the text.
        canvas.drawText(this.text,this.rect.centerX(),this.baseLine,this.textPaint);

    }






}

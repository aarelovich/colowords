package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class ExtraWordIndicator {
    private int number = 0;
    private int total = 0;
    private int x,y,d;

    private AnimatorHelper rHelper;
    private Utils.AnimationInterface ai;

    public ExtraWordIndicator(int x, int y, int d, Utils.AnimationInterface ai){
        this.x = x;
        this.y = y;
        this.d = d;
        this.number = 0;
        this.ai = ai;
        this.rHelper = new AnimatorHelper();
    }

    public void setNumber(int number, int total){
        this.number = number;
        this.total = total;
        this.startAnimation();
    }

    public void decreaseNumber(){
        this.number = this.number - 1;
        this.startAnimation();
    }

    private void startAnimation(){
        Rect bRect = new Rect(this.x - this.d/2,this.y - this.d/2,this.x + this.d/2,this.y + this.d/2);
        if (this.rHelper.computeAnimationSteps(bRect,2,300,Utils.ANIMATION_TICK_LENGTH)){
            this.ai.startAnimation(Utils.ANIMATION_ID_EXTRA);
        }
    }

    public boolean fingerUp(int x, int y){
        if (x > this.x + d/2) return false;
        if (x < this.x - d/2) return false;
        if (y > this.y + d/2) return false;
        if (y < this.y - d/2) return false;
        return true;
    }

    public void render(Canvas canvas){

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Utils.EXTRA_IND_BKG);

        // System.err.println("Drawing at " + x + "," + y + " with size of " + d);

        int diameter = this.d;
        Rect r = this.rHelper.getNextRect();
        if (r != null){
            diameter = r.width();
        }
        else {
            ai.stopAnimation(Utils.ANIMATION_ID_EXTRA);
        }

        int left = x - diameter/2;
        int right = x + diameter/2;
        int top = y - diameter/2;
        int bottom = y + diameter/2;
        float innerCircleRad = diameter*0.8f/2;
        float strokeWidth = 0.03f*diameter;
        float angle = (this.total - this.number)*360.0f/this.total;

        //canvas.drawRect(left,top,right,bottom,p);

        // Draw the outer circle.
        canvas.drawCircle(x,y,diameter/2,p);

        // We draw the wedge.
        p.setColor(Utils.EXTRA_IND_FiLL);
        canvas.drawArc(left,top,right,bottom,0,angle,true,p);

        // We draw the inner circle
        p.setColor(Utils.EXTRA_IND_BKG);
        canvas.drawCircle(x,y,innerCircleRad,p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(strokeWidth);
        p.setColor(Utils.EXTRA_IND_LETTER);
        canvas.drawCircle(x,y,innerCircleRad,p);

        // Finally we draw the text.
        float dForLetter = 2*innerCircleRad*0.6f;
        p.setTextAlign(Paint.Align.CENTER);
        p.setStyle(Paint.Style.FILL);
        p.setTypeface(Utils.GetElementTypeFace());
        p.setTextSize(Utils.GetTextSizeToFitRect("A",dForLetter,dForLetter,p));

        String display = Integer.toString(number);
        if (number < 10) display = "0" + display;

        float baseLine  = Utils.GetTextBaseLine(display,p,this.y);

        canvas.drawText(display,this.x,baseLine,p);

    }

}

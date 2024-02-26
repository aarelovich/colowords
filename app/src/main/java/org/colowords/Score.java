package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Score {

    private String score;
    private String multiplier;
    private Utils.AnimationInterface ai;
    private AnimatorHelper rHelper;
    private Rect scoreLine;
    private Rect multLine;

    public Score (int t, int l, int width, int height, Utils.AnimationInterface ai){
        this.ai = ai;
        this.scoreLine = new Rect(l,t,l+width,t+height/2);
        this.multLine  = new Rect(l,t+height/2,l+width,t+height);
        this.rHelper = new AnimatorHelper();
        this.score = "0";
        this.multiplier = "x1";
        //System.err.println("Computed score line at " + this.scoreLine.toString());
    }

    public void updateValues(int s, int m){
        String newscore = Integer.toString(s);
        String oldscore = this.score;
        this.score = newscore;
        this.multiplier = "x" + Integer.toString(m);
        // We only start the animation if the score changed and the animation interface is not null
        if (this.ai == null) return;
        if (!newscore.equals(oldscore)){
            if (this.rHelper.computeAnimationSteps(this.scoreLine,2,200,Utils.ANIMATION_TICK_LENGTH)){
                if (ai != null) ai.startAnimation(Utils.ANIMATION_ID_SCORE);
            }
        }
    }

    public void render(Canvas canvas){

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);

        Rect scoreBKG = this.rHelper.getNextRect();
        if (scoreBKG == null) {
            //System.err.println("Using REGULAR Score line @ " + this.scoreLine.toString());
            if (this.ai != null) ai.stopAnimation(Utils.ANIMATION_ID_SCORE);
            scoreBKG = this.scoreLine;
        }


        // The Score is two lines. The top line is the score and the second line is the multiplier
        p.setTextAlign(Paint.Align.LEFT);
        p.setTypeface(Utils.GetElementTypeFace());
        float textSize = Utils.GetTextSizeToFitRect(this.score,scoreBKG.width(),scoreBKG.height(),p);
        p.setTextSize(textSize);


        p.setColor(Utils.ACCENT_100);
        canvas.drawText(this.score,scoreBKG.left,scoreBKG.bottom,p);
        p.setColor(Utils.ACCENT_200);
        canvas.drawText(this.multiplier,this.multLine.left,this.multLine.bottom,p);


    }




}

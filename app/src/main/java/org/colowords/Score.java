package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.List;

public class Score {

    private String score;
    private String maximumPossibleScore;
    private Utils.AnimationInterface ai;
    private AnimatorHelper rHelper;
    private Rect scoreLine;
    private Rect multLine;
    public Scoring scoreLogic;

    public Score (int t, int l, int width, int height, Utils.AnimationInterface ai){
        this.ai = ai;
        this.scoreLine = new Rect(l,t,l+width,t+height/2);
        this.multLine  = new Rect(l,t+height/2,l+width,t+height);
        this.rHelper = new AnimatorHelper();
        this.score = "0";
        this.maximumPossibleScore = "0";
        this.scoreLogic = new Scoring();
    }

    public String setScoreLogic(CrossWordGrid cwg, List<String> extraWords){
        String highlightWord = this.scoreLogic.createScoringData(cwg.getWordIntersections());
        this.scoreLogic.setMaximumPossibleScore(cwg.getWordIntersections(),extraWords);
        System.err.println("MAXIMUM POSSIBLE SCORE: " + scoreLogic.getMaximumPossibleScore());
        this.maximumPossibleScore = String.valueOf(scoreLogic.getMaximumPossibleScore());
        return highlightWord;
    }
    public String updateScore(String foundWord) {
        Scoring.GridWordFoundReturn gwf = this.scoreLogic.gridWordFound(foundWord);
        this.score = String.valueOf(this.scoreLogic.getCurrentScore());
        return gwf.highlightWord;
    }

    public void updateScoreExtra(String foundWord) {
        this.scoreLogic.hiddenWordFound(foundWord);
        this.syncScore();
    }

    public void syncScore() {
        this.score = String.valueOf(this.scoreLogic.getCurrentScore());
        this.maximumPossibleScore = String.valueOf(this.scoreLogic.getMaximumPossibleScore());
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
        canvas.drawText(this.maximumPossibleScore,this.multLine.left,this.multLine.bottom,p);


    }




}

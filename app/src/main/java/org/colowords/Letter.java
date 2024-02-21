package org.colowords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.selects.WhileSelectKt;

public class Letter {

    private static final int LETTER_STATE_HIDDEN   = 0;
    private static final int LETTER_STATE_HINTED   = 1;
    private static final int LETTER_STATE_REVEALED = 2;

    private String letter;
    private int selectionOrder;
    private int x;
    private int y;
    private int d;
    private int R;
    private float textYBaseLine;
    private boolean gridLetter;
    private int letterState;
    private Paint letterPaint;
    private static float WheelLetterSize;

    // State indexes
    private final int STATE_INDEX_X      = 0;
    private final int STATE_INDEX_Y      = 1;
    private final int STATE_INDEX_LETTER = 2;
    private final int STATE_INDEX_D      = 3;
    private final int STATE_INDEX_STATE  = 4;
    private final int STATE_INDEX_ISGRID = 5;
    private final int STATE_INDEX_SEL     = 6;
    private final int STATE_SIZE          = 7;


    public Letter (boolean forLetterWheel){
        this.x = 0;
        this.y = 0;
        this.letter = "";
        this.d = 0;
        this.textYBaseLine = this.y;
        this.selectionOrder = -1;
        this.gridLetter = !forLetterWheel;
        this.letterState = LETTER_STATE_HIDDEN;
        this.letterPaint = new Paint();
    }

    public Letter (String letterState){
        String[] parts = letterState.split("|");

        if (parts.length != STATE_SIZE){
            System.err.println("Failed restoring GridSize state from string '" + letterState + "'. Number of parts " + parts.length + " instead of " + STATE_SIZE);
            return;
        }

        this.x = Integer.valueOf(parts[STATE_INDEX_X]);
        this.y = Integer.valueOf(parts[STATE_INDEX_Y]);
        this.d = Integer.valueOf(parts[STATE_INDEX_D]);
        this.letterState = Integer.valueOf(parts[STATE_INDEX_STATE]);
        this.selectionOrder = Integer.valueOf(parts[STATE_INDEX_SEL]);
        this.letter = parts[STATE_INDEX_LETTER];
        this.gridLetter = Boolean.valueOf(parts[STATE_INDEX_ISGRID]);

        this.setGeometry(this.x,this.y,this.d);

    }

    public void setSelectionOrder(int index){
        this.selectionOrder = index;
    }

    public void setLetter(String letter){
        this.letter = letter;
        this.selectionOrder = -1;
        this.computeTextYBaseLine();
    }

    public void revealLetter(){
        this.letterState = LETTER_STATE_REVEALED;
    }

    public void hintLetter(){
        // Can only hint the letter when it's hidden
        if (this.letterState == LETTER_STATE_HIDDEN){
            this.letterState = LETTER_STATE_HINTED;
        }
    }

    public Point getPoint() {
        Point p = new Point();
        p.set(this.x,this.y);
        return p;
    }

    public boolean isSelected() {
        return this.selectionOrder != -1;
    }

    public String getLetter() {
        return this.letter;
    }

    public void setGeometry(int x, int y, int d){

        this.x = x;
        this.y = y;
        this.d = d;
        this.R = d/2;

        Typeface font = Typeface.create("Mono",Typeface.BOLD);
        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setTextAlign(Paint.Align.CENTER);
        letterPaint.setTypeface(font);
        float size = Utils.GetTextSizeToFitRect("A",d,d,letterPaint);
        letterPaint.setTextSize(size);
        if (!this.gridLetter) {
            WheelLetterSize = size;
        }
        this.computeTextYBaseLine();
    }

    public boolean isLetterBeingTouched(int x, int y){
        if ((x < this.x - R) || (x > this.x + R)) return false;
        if ((y < this.y - R) || (y > this.y + R) ) return false;
        return true;
    }

    public void render(Canvas canvas){

        Paint bkgPaint = new Paint();

        if (!gridLetter){
            bkgPaint.setStyle(Paint.Style.FILL);
            if (this.selectionOrder >= 0){
                // The letter is selected
                bkgPaint.setColor(Utils.LINE_COLOR);
                letterPaint.setColor(Utils.LETTER_COLOR_WITH_BKG);
            }
            else {
                // The letter is not selected
                bkgPaint.setColor(Utils.TRANSPARENT);
                letterPaint.setColor(Utils.LETTER_COLOR_NO_BKG);
            }

            canvas.drawCircle(this.x,this.y,this.d/2,bkgPaint);
            canvas.drawText(this.letter,this.x,this.textYBaseLine, letterPaint);
        }
        else {

            float r = (float)(this.R)*0.3f;
            float strokeWidth = this.d*0.1f;

            bkgPaint.setColor(Utils.SQUARE_BKG);
            bkgPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(this.x - this.R,this.y - this.R,this.x + this.R,this.y + this.R,r,r,bkgPaint);

            bkgPaint.setColor(Color.WHITE);
            bkgPaint.setStyle(Paint.Style.STROKE);
            bkgPaint.setStrokeWidth(strokeWidth);
            canvas.drawRoundRect(this.x - this.R,this.y - this.R,this.x + this.R,this.y + this.R,r,r,bkgPaint);

            if (this.letterState == LETTER_STATE_HIDDEN) return;

            //this.letterShown = true;
            if (this.letterState == LETTER_STATE_HINTED){
                letterPaint.setColor(Utils.SQUARE_LETTER_HINTED);
            }
            else {
                letterPaint.setColor(Utils.SQUARE_LETTER);
            }

            canvas.drawText(this.letter,this.x,this.textYBaseLine, letterPaint);
        }


    }

    public String getStoreString(){
        List<String> state = new ArrayList<>();
        for (int i = 0; i < STATE_SIZE; i++){
            state.add("");
        }

        state.set(STATE_INDEX_X,Integer.toString(this.x));
        state.set(STATE_INDEX_Y,Integer.toString(this.y));
        state.set(STATE_INDEX_D,Integer.toString(this.d));
        state.set(STATE_INDEX_SEL,Integer.toString(selectionOrder));
        state.set(STATE_INDEX_STATE,Integer.toString(this.letterState));
        state.set(STATE_INDEX_LETTER,this.letter);
        state.set(STATE_INDEX_ISGRID,Boolean.toString(gridLetter));

        return String.join("|",state);
    }

    public static float GetWheelLetterSize() {
        return WheelLetterSize;
    }

    private void computeTextYBaseLine(){
        Rect textBounds = new Rect();
        this.letterPaint.getTextBounds("A", 0, this.letter.length(), textBounds);
        float textHeight = textBounds.height();
        this.textYBaseLine  = this.y + textHeight/2f;
    }

}

package org.colowords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

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
    private boolean gridLetter;
    private int letterState;
    private long fingerDownTime;

    // State indexes
    private static final int STATE_INDEX_X      = 0;
    private static final int STATE_INDEX_Y      = 1;
    private static final int STATE_INDEX_LETTER = 2;
    private static final int STATE_INDEX_D      = 3;
    private static final int STATE_INDEX_STATE  = 4;
    private static final int STATE_INDEX_ISGRID = 5;
    private static final int STATE_INDEX_SEL     = 6;
    private static final int STATE_SIZE          = 7;
    private static final int LONG_PRESS_TIME     = 1000; // milliseconds.
    private static final int SHORT_PRESS_TIME    = 30; // milliseconds.

    // Press Types.
    public static final int PRESS_NO_PRESS      = 0;
    public static final int PRESS_SHORT         = 1;
    public static final int PRESS_LONG          = 2;

    public Letter (boolean forLetterWheel){
        this.x = 0;
        this.y = 0;
        this.letter = "";
        this.d = 0;
        this.selectionOrder = -1;
        this.gridLetter = !forLetterWheel;
        this.letterState = LETTER_STATE_HIDDEN;
        this.fingerDownTime = -1;
    }

    public Rect getBounds(){
        int R = this.d/2;
        return new Rect(this.x-R,this.y-R,this.x+R,this.y+R);
    }

    public boolean isItHidden() {
        return (this.letterState != LETTER_STATE_REVEALED);
    }

    public Letter (String letterState){
        String[] parts = letterState.split("\\|");

        if (parts.length != STATE_SIZE){
            System.err.println("Failed restoring GridSize state from string '" + letterState + "'. Number of parts " + parts.length + " instead of " + STATE_SIZE);
            return;
        }

        this.x = Integer.parseInt(parts[STATE_INDEX_X]);
        this.y = Integer.parseInt(parts[STATE_INDEX_Y]);
        this.d = Integer.parseInt(parts[STATE_INDEX_D]);
        this.letterState = Integer.parseInt(parts[STATE_INDEX_STATE]);
        this.selectionOrder = Integer.parseInt(parts[STATE_INDEX_SEL]);
        this.letter = parts[STATE_INDEX_LETTER];
        this.gridLetter = Boolean.parseBoolean(parts[STATE_INDEX_ISGRID]);

        this.setGeometry(this.x,this.y,this.d);

        this.fingerDownTime = -1;

    }

    public void setSelectionOrder(int index){
        this.selectionOrder = index;
    }

    public void setLetter(String letter){
        this.letter = letter;
        this.selectionOrder = -1;

    }

    public void forceHintedState(){
        this.letterState = LETTER_STATE_HINTED;
    }

    public void revealLetter(){
        this.letterState = LETTER_STATE_REVEALED;
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

    }

    public int[] getGeometry() {
        return new int[] {x, y, d};
    }

    public boolean isLetterBeingTouched(int x, int y){
        if ((x < this.x - R) || (x > this.x + R)) return false;
        if ((y < this.y - R) || (y > this.y + R) ) return false;

        // Only grid letters can be pressed.
        if (this.gridLetter){
           this.fingerDownTime = System.currentTimeMillis();
        }
        return true;
    }

    public boolean isItPressed() {
        return (this.fingerDownTime != -1);
    }

    public void render(Canvas canvas){

        Paint bkgPaint = new Paint();

        Paint letterPaint = new Paint();
        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setTextAlign(Paint.Align.CENTER);
        letterPaint.setTypeface(Utils.GetLetterTypeFace());
        float size = Utils.GetTextSizeToFitRect(this.letter,d,d,letterPaint);
        letterPaint.setTextSize(size);

        float textYBaseLine = Utils.GetTextBaseLine(this.letter,letterPaint,this.y);

        if (!gridLetter){
            bkgPaint.setStyle(Paint.Style.FILL);
            if (this.selectionOrder >= 0){
                // The letter is selected
                bkgPaint.setColor(Utils.PRIMARY_200);
                letterPaint.setColor(Utils.TEXT_100);
            }
            else {
                // The letter is not selected
                bkgPaint.setColor(Utils.TRANSPARENT);
                letterPaint.setColor(Utils.TEXT_200);
            }

            canvas.drawCircle(this.x,this.y,this.R,bkgPaint);
            canvas.drawText(this.letter,this.x,textYBaseLine, letterPaint);
        }
        else {

            float r = (float)(this.R)*0.3f;
            float strokeWidth = this.d*0.1f;

            if (this.fingerDownTime != -1){
                bkgPaint.setColor(Utils.ACCENT_200);
            }
            else {
                bkgPaint.setColor(Utils.ACCENT_100);
            }

            bkgPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(this.x - this.R,this.y - this.R,this.x + this.R,this.y + this.R,r,r,bkgPaint);

            bkgPaint.setColor(Utils.TEXT_200);
            bkgPaint.setStyle(Paint.Style.STROKE);
            bkgPaint.setStrokeWidth(strokeWidth);
            canvas.drawRoundRect(this.x - this.R,this.y - this.R,this.x + this.R,this.y + this.R,r,r,bkgPaint);

            if (this.letterState == LETTER_STATE_HIDDEN) return;

            if (this.letterState == LETTER_STATE_HINTED){
                letterPaint.setColor(Utils.GetHintedLetterColor());
            }
            else {
                letterPaint.setColor(Utils.GetDisplayLetterColor());
            }

            canvas.drawText(this.letter,this.x,textYBaseLine, letterPaint);
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

    public int fingerUp(){

        if (!this.gridLetter) {
            this.fingerDownTime = -1;
            return PRESS_NO_PRESS; // Only grid letters are pressed.
        }

        if (this.fingerDownTime != -1){

            long diff = System.currentTimeMillis() - this.fingerDownTime;
            this.fingerDownTime = -1;

            // System.err.println("LETTER-FINGER UP. Duration: " + diff);

            if (diff >= LONG_PRESS_TIME) {
                // If this is a grid letter,
                // then this means we need to reveal the letter as a hint, assuming that is hidden.
                if (this.letterState == LETTER_STATE_HIDDEN){
                    this.letterState = LETTER_STATE_HINTED;
                    return PRESS_LONG;
                }
                else {
                    return PRESS_NO_PRESS; // WE do nothing.
                }
            }
            else if (diff >= SHORT_PRESS_TIME) {
                // Short press definition request.
                return PRESS_SHORT;
            }

        }

        return PRESS_NO_PRESS;
    }

}

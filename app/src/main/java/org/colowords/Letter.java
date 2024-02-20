package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

import kotlinx.coroutines.selects.WhileSelectKt;

public class Letter {

    private String letter;
    private int selectionOrder;
    private int x;
    private int y;
    private int d;
    private int R;
    private float textYBaseLine;
    private boolean gridLetter;
    private boolean letterShown;
    private static Paint LetterWheelPaint = new Paint();
    private static Paint LetterSquarePaint = new Paint();
    private static float WheelLetterSize;
    public Letter (boolean forLetterWheel){
        this.x = 0;
        this.y = 0;
        this.letter = "";
        this.d = 0;
        this.textYBaseLine = this.y;
        this.selectionOrder = -1;
        this.gridLetter = !forLetterWheel;
        this.letterShown = false;
    }

    public static void ConfigurePaintForLetterWheel(int diameter){
        LetterWheelPaint.setStyle(Paint.Style.FILL);
        LetterWheelPaint.setTextAlign(Paint.Align.CENTER);
        Typeface font = Typeface.create("Mono",Typeface.BOLD);
        LetterWheelPaint.setTypeface(font);
        WheelLetterSize = Utils.GetTextSizeToFitRect("A",diameter,diameter,LetterWheelPaint);
        LetterWheelPaint.setTextSize(WheelLetterSize);
    }

    public static float GetWheelLetterSize() {
        return WheelLetterSize;
    }

    public static void ConfigurePaintForSquareLetters(int side){
        LetterSquarePaint.setStyle(Paint.Style.FILL);
        LetterSquarePaint.setTextAlign(Paint.Align.CENTER);
        Typeface font = Typeface.create("Mono",Typeface.BOLD);
        LetterSquarePaint.setTypeface(font);
        LetterSquarePaint.setTextSize(Utils.GetTextSizeToFitRect("A",side,side,LetterSquarePaint));
    }

    public void computeTextYBaseLine(){
        Rect textBounds = new Rect();
        if (this.gridLetter){
            LetterSquarePaint.getTextBounds(this.letter, 0, this.letter.length(), textBounds);
        }
        else {
            LetterWheelPaint.getTextBounds(this.letter, 0, this.letter.length(), textBounds);
        }
        LetterWheelPaint.getTextBounds(this.letter, 0, this.letter.length(), textBounds);
        float textHeight = textBounds.height();
        this.textYBaseLine  = this.y + textHeight/2f;
    }

    public void setSelectionOrder(int index){
        this.selectionOrder = index;
    }

    public void setLetter(String letter){
        this.letter = letter;
        this.selectionOrder = -1;
    }

    public void revealLetter(){
        this.letterShown = true;
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
        this.computeTextYBaseLine();
    }

    public boolean isLetterBeingTouched(int x, int y){
        if ((x < this.x - R) || (x > this.x + R)) return false;
        if ((y < this.y - R) || (y > this.y + R) ) return false;
        return true;
    }

    public void render(Canvas canvas){

        Paint bkgPaint = new Paint();
        bkgPaint.setStyle(Paint.Style.FILL);

        if (!gridLetter){
            if (this.selectionOrder >= 0){
                // The letter is selected
                bkgPaint.setColor(Utils.LINE_COLOR);
                LetterWheelPaint.setColor(Utils.LETTER_COLOR_WITH_BKG);
            }
            else {
                // The letter is not selected
                bkgPaint.setColor(Utils.TRANSPARENT);
                LetterWheelPaint.setColor(Utils.LETTER_COLOR_NO_BKG);
            }

            canvas.drawCircle(this.x,this.y,this.d/2,bkgPaint);
            canvas.drawText(this.letter,this.x,this.textYBaseLine, LetterWheelPaint);
        }
        else {
            bkgPaint.setColor(Utils.SQUARE_BKG);
            float r = this.R*0.1f;
            canvas.drawRoundRect(this.x - this.R,this.y - this.R,this.x + this.R,this.y + this.R,r,r,bkgPaint);
            //canvas.drawText(this.letter,this.x,this.textYBaseLine, LetterSquarePaint);
            canvas.drawText(this.letter,this.x,this.textYBaseLine, LetterWheelPaint);
        }


    }

}

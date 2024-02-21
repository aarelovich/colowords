package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.ArrayList;

public class LetterWheel {

    private int center_x;
    private int center_y;
    private int diameter;
    private int letterDiameter;
    private ArrayList<Letter> letters;
    private ArrayList<Point> linePoints;
    private String wordFormed;
    private Path shuffleButtonDef;
    private Path shuffleButtonArrows;
    private Paint shuffleButtonPaint;
    private Paint shuffleButtonArrowPaint;
    private RectF shuffleButtonPressArea;
    private boolean shuffleButtonPressed;

    public LetterWheel(int x, int y, int d){
        this.center_x = x;
        this.center_y = y;
        this.diameter = d;
        this.letters = new ArrayList<Letter>();
        this.linePoints = new ArrayList<Point>();
        this.letterDiameter = this.diameter/4;
        this.wordFormed = "";
        this.shuffleButtonPressed = false;

        // We now compute the rectangle that defines the shadow button.
        float rw = d*0.26f;
        float rh = d*0.14f;
        float left = x - rw/2.0f;
        float right = x + rw/2.0f;
        float top = y - rh/2.0f;
        float bottom = y + rh/2.0f;

        // Arrow constants.
        float arrowHeadHeight = 0.6f*rh;
        float arrowHeadWidth  = 0.2f*rw;

        // We need this to see if it's been clicked.
        this.shuffleButtonPressArea = new RectF(left,top,right,bottom);

        // We now defined the draw path.
        this.shuffleButtonDef = new Path();

        // This is top, left to bottom, right line of the shuffle symbol.
        float control_x = rw*0.25f;
        this.shuffleButtonDef.moveTo(left,top);
        this.shuffleButtonDef.lineTo(left + control_x,top);
        this.shuffleButtonDef.lineTo(right - control_x,bottom);
        this.shuffleButtonDef.lineTo(right,bottom);

        // This is the bottom left to top right line of the shuffle symbol.
        this.shuffleButtonDef.moveTo(left,bottom);
        this.shuffleButtonDef.lineTo(left + control_x,bottom);
        this.shuffleButtonDef.lineTo(right - control_x,top);
        this.shuffleButtonDef.lineTo(right,top);

        // Now we add the arrows
        this.shuffleButtonArrows = new Path();

        // We need to shift the right point a bit so the final glyph looks good.
        right = right + rw*0.1f;

        this.shuffleButtonArrows.moveTo(right-arrowHeadWidth,top - arrowHeadHeight/2);
        this.shuffleButtonArrows.lineTo(right-arrowHeadWidth,top + arrowHeadHeight/2);
        this.shuffleButtonArrows.lineTo(right,top);
        this.shuffleButtonArrows.lineTo(right-arrowHeadWidth,top - arrowHeadHeight/2);

        this.shuffleButtonArrows.moveTo(right-arrowHeadWidth,bottom - arrowHeadHeight/2);
        this.shuffleButtonArrows.lineTo(right-arrowHeadWidth,bottom + arrowHeadHeight/2);
        this.shuffleButtonArrows.lineTo(right,bottom);
        this.shuffleButtonArrows.lineTo(right-arrowHeadWidth,bottom - arrowHeadHeight/2);


        this.shuffleButtonPaint = new Paint();
        this.shuffleButtonPaint.setStyle(Paint.Style.STROKE);
        this.shuffleButtonPaint.setStrokeWidth(0.18f*rh);
        this.shuffleButtonPaint.setStrokeCap(Paint.Cap.ROUND);
        this.shuffleButtonPaint.setColor(Utils.SHUFFLE_BUTTON_NOT_PRESSED);

        this.shuffleButtonArrowPaint = new Paint();
        this.shuffleButtonArrowPaint.setStyle(Paint.Style.FILL);
        this.shuffleButtonArrowPaint.setColor(Utils.SHUFFLE_BUTTON_NOT_PRESSED);


    }

    public boolean isShuffleButtonPressed(){
        return this.shuffleButtonPressed;
    }

    public void setLetters(ArrayList<String> letters){

        // Create a new letters struct.
        this.letters.clear();

        // We reset any words formed.
        this.wordFormed = "";
        this.linePoints.clear();

        int N = letters.size();

        // The diameter of the circle where where the letter centers are to be located.
        // It is computed based on the letter diameter and radious of the wheel itself.
        double R = this.diameter/2.0;
        int positionRadius = (int)(R - letterDiameter/2);

        double angleDelta = 2*Math.PI/(N);
        double angle = Math.PI/2;

        for (int i = 0; i < letters.size(); i++){
            Letter letter = new Letter(true);

            // We now compute x,y values.
            int x,y;
            if (i == 0){
                // First point is at 1,0
                x = 0;
                y = positionRadius;
            }
            else {
                angle = angle + angleDelta;
                x = (int)(Math.cos(angle)*positionRadius);
                y = (int)(Math.sin(angle)*positionRadius);
            }

            // Now we transform the x, y values to properly match the positions of the circle.
            x = this.center_x + x;
            y = this.center_y - y; // This is - because a negative value of y is actually a LARGER value of screen y.

            System.err.println("Letter " + letters.get(i) + " @ (" + x + "," + y + "), D: " + letterDiameter);

            letter.setGeometry(x,y,letterDiameter);
            letter.setLetter(letters.get(i));
            //letter.computeTextYBaseLine();

            this.letters.add(letter);
        }
    }

    public String fingerDown(int x, int y){

        // The first check is on the shuffle button.
        if (shuffleButtonPressed) return wordFormed; // Some sort of bug.

        if (shuffleButtonPressArea.contains(x,y)){
            shuffleButtonPressed = true;
            return "";
        }

        if (wordFormed != "") return wordFormed;

        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i).isLetterBeingTouched(x,y)){
                Letter l = letters.get(i);
                l.setSelectionOrder(0);
                wordFormed = l.getLetter();
                Point p = l.getPoint();
                linePoints.add(p);
                linePoints.add(p); // We add it twice. The last point represents where the figer is currently at.
                return wordFormed;
            }
        }

        return "";
    }

    public String fingerMove(int x, int y){

        if (shuffleButtonPressed) return "";

        // When the finger is moving, we only care it it touched a letter first.
        if (wordFormed == "") return "";

        int lastPointIndex = this.linePoints.size()-1;

        // We check if we touched a new letter
        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i).isLetterBeingTouched(x,y) && !letters.get(i).isSelected()){
                Letter l = letters.get(i);
                l.setSelectionOrder(0);
                wordFormed = wordFormed + l.getLetter();
                Point p = l.getPoint();
                linePoints.set(lastPointIndex,p);
                linePoints.add(p); // The new current position.
                return wordFormed;
            }
        }

        // If we got here, no new letter was added so we just update the last line point.
        Point p = new Point();
        p.set(x,y);
        linePoints.set(lastPointIndex,p);

        return wordFormed;
    }

    public String fingerUp() {

        if (shuffleButtonPressed){
            // We do the shuffle.
            this.shuffle();
        }

        // We checked if a word was formed.
        String ret = wordFormed;
        wordFormed = "";
        this.linePoints.clear();
        shuffleButtonPressed = false;

        // Reset the touched flag
        for (int i = 0; i < letters.size(); i++) {
            Letter l = letters.get(i);
            l.setSelectionOrder(-1);
            letters.set(i,l);
        }

        return ret;
    }

    private void shuffle() {

        // First we create an array with all the letters as they are now.
        ArrayList<String> allletters = new ArrayList<String>();
        for (int i = 0; i < this.letters.size(); i++){
            allletters.add(this.letters.get(i).getLetter());
        }

        int i = 0;

        // Now we randomly take a letter and we set them in order in each of the positions.
        while (allletters.size() > 0){

            int index = (int)(Math.random()*allletters.size());
            String l = allletters.remove(index);

            Letter letter = this.letters.get(i);
            letter.setLetter(l);;
            this.letters.set(i,letter);
            i++;

        }

    }

    public void render(Canvas canvas){

        // The first thing we do is to draw the background.
        this.drawBackground(canvas);
        this.drawShuffleButton(canvas);
        this.drawConnectingLine(canvas);
        this.drawLetters(canvas);

    }

    private void drawBackground(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Utils.WHEEL_BKG_COLOR);
        canvas.drawCircle(this.center_x,this.center_y,this.diameter/2,paint);
    }

    /**
     * If it exists, it draws the line connecting the letters.
     * @param canvas
     */
    private void drawConnectingLine(Canvas canvas){
        if (linePoints.size() < 2) return; // Nothing to do.
        // We draw the connecting lines.
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(this.letterDiameter/3);
        linePaint.setColor(Utils.LINE_COLOR);

        for (int i = 1; i < linePoints.size(); i++){
            Point start = linePoints.get(i-1);
            Point end   = linePoints.get(i);
            canvas.drawLine(start.x,start.y,end.x,end.y,linePaint);
        }

    }

    /**
     * Draws the letters. The Backgroudn of the letters is a circle.
     * If the letter is selected then the background the color of the selection background
     * otherwise it is the color of the wheel background. The color of the letter itself changes
     * like wise.
     * @param canvas
     */
    private void drawLetters(Canvas canvas){
        for (int i = 0; i < letters.size(); i++){
            letters.get(i).render(canvas);
        }
    }

    private void drawShuffleButton(Canvas canvas){

        if (wordFormed != "") return; // We don't render it when forming words.

        // The path has all the definitions, we just draw it.
        if (shuffleButtonPressed){
            this.shuffleButtonArrowPaint.setColor(Utils.SHUFFLE_BUTTON_PRESSED);
            this.shuffleButtonPaint.setColor(Utils.SHUFFLE_BUTTON_PRESSED);
        }
        else {
            this.shuffleButtonArrowPaint.setColor(Utils.SHUFFLE_BUTTON_NOT_PRESSED);
            this.shuffleButtonPaint.setColor(Utils.SHUFFLE_BUTTON_NOT_PRESSED);
        }
        canvas.drawPath(this.shuffleButtonDef,this.shuffleButtonPaint);
        canvas.drawPath(this.shuffleButtonArrows,this.shuffleButtonArrowPaint);
    }

}

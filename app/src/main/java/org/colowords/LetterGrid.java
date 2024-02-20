package org.colowords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class LetterGrid {

    private int x;
    private int y;
    private int width;
    private int height;
    private int squareSide;
    private int gridWidth;
    private int gridHeight;
    private boolean configured;
    private List<Letter> letters;

    public LetterGrid (){
        this.configured = false;
        this.letters = new ArrayList<>();
    }

    public void configureGrid (Rect rect, GridSize gr, List<GridWord> wordSpec){

        int x = rect.left;
        int y = rect.top;
        int w = rect.width();
        int h = rect.height();

        // The square is going to be limited to by whichever side is smallest the h or the w side.
        int side_h = h / gr.getRowCount();
        int side_w = w / gr.getColumnCount();
        this.squareSide = Math.min(side_h,side_w);

        this.gridWidth = gr.getColumnCount();
        this.width = this.gridWidth*this.squareSide;
        this.gridHeight = gr.getRowCount();
        this.height = this.gridHeight*this.squareSide;

        // Now based on the calculations above we adjust the x and y values.
        int offset_x = (int)(((double)(w) - (double)(this.width))/2.0);
        int offset_y = (int)(((double)(h) - (double)(this.height))/2.0);

        System.err.println("[DBUG] Input w " + w + " computed w " + this.width + " and final offset is " + offset_x);

        // This should leave it centered in the area originally defined.
        this.x = x + offset_x;
        this.y = y + offset_y;

        configured = true;

        // We need to configure the paint object for the square size (this will set the font size).
        Letter.ConfigurePaintForSquareLetters(this.squareSide);

        // And we now generate the letter array.
        this.letters.clear();
        for (GridWord gw: wordSpec){

            // We need to add a letter for every letter of the word.
            List<GridPoint> gps = gw.toGridPointList();

            for (GridPoint gp: gps){

                Point p = this.gridPositionToScreenPoint(gp.r,gp.c);
                Letter l = new Letter(false);
                l.setLetter("" + gp.character);
                l.setGeometry(p.x,p.y,this.squareSide);
                this.letters.add(l);

            }


        }

    }


    public void render(Canvas canvas){
        this.renderGrind(canvas);
        this.renderLetters(canvas);
    }

    private Point gridPositionToScreenPoint(int row, int col){
        Point p = new Point();

        // These coordinates are for the squares (Letter object). I need to specify the square center
        p.x = this.x + col*this.squareSide + this.squareSide/2;
        p.y = this.y + row*this.squareSide + this.squareSide/2;

        return p;
    }


    /**
     * Will draw the full word grid using lines.
     * Should be used for debugging only.
     * @param canvas
     */
    private void renderGrind(Canvas canvas){

        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.parseColor("#ffffff"));
        p.setStrokeWidth(1.0f);

        canvas.drawRect(this.x,this.y,this.x + this.width,this.y+this.height,p);

        int xacc = this.x;
        int yacc = this.y;

        for (int i = 0; i < this.gridWidth; i++){
            canvas.drawLine(xacc,this.y,xacc,this.y + this.height,p);
            xacc = xacc + this.squareSide;
        }

        for (int i = 0; i < this.gridHeight; i++){
            canvas.drawLine(this.x,yacc,this.x + this.width,yacc,p);
            yacc = yacc + this.squareSide;
        }


    }

    /**
     * Will draw the letters as grid on the screen.
     * @param canvas
     */
    private void renderLetters(Canvas canvas){
        for (Letter l : this.letters){
            l.render(canvas);
        }
    }

}

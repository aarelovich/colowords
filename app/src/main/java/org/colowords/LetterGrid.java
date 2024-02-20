package org.colowords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class LetterGrid {

    private int x;
    private int y;
    private int width;
    private int height;
    private int squareSide;
    private int gridWidth;
    private int gridHeight;

    public LetterGrid (int x, int y, int w, int h, int n_letters_across){

        this.squareSide = (int) Math.floor(w/n_letters_across);
        this.gridWidth = n_letters_across;
        this.width = this.gridWidth*this.squareSide;
        this.gridHeight = (int) Math.floor(h/this.squareSide);
        this.height = this.gridHeight*this.squareSide;

        // Now based on the calculations above we adjust the x and y values.
        int offset_x = (int)(((double)(w) - (double)(this.width))/2.0);
        int offset_y = (int)(((double)(h) - (double)(this.height))/2.0);

        System.err.println("[DBUG] Input w " + w + " computed w " + this.width + " and final offset is " + offset_x);

        // This should leave it centered in the area originally defined.
        this.x = x + offset_x;
        this.y = y + offset_y;

    }


    public void render(Canvas canvas){

        this.renderGrind(canvas);

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

}

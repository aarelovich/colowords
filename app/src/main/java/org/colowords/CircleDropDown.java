package org.colowords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

public class CircleDropDown {

    private List<String> options;
    private List<Rect> touchAreas;
    private boolean deployed;
    private int currentSelectionIndex;
    private int x, y, d;

    public CircleDropDown (int x, int y, int d){
        this.deployed = false;
        this.touchAreas = new ArrayList<Rect>();
        this.options = new ArrayList<>();
        this.currentSelectionIndex = -1;
        this.x = x;
        this.y = y;
        this.d = d;

        // setting the original rect.
        this.touchAreas.add(new Rect(x - d/2, y - d/2,x + d/2, y + d/2));

    }

    public String getCurrentSelection(){
        if ((this.currentSelectionIndex < 0) || (this.currentSelectionIndex >= this.options.size())) return "";
        return this.options.get(this.currentSelectionIndex);
    }

    public void setCurrentIndex(int index){
        if (index < 0) return;
        if (index >= this.options.size()) return;
        this.currentSelectionIndex = index;
    }

    public void setCurrentOption(String desiredValue){
        this.currentSelectionIndex = this.options.indexOf(desiredValue);
    }

    public void addOptions(String option){
        this.options.add(option);
        Rect r = new Rect(this.touchAreas.get(this.touchAreas.size()-1)); // We get the last rect.
        // We move it down by d.
        r.top = r.top + this.d;
        r.bottom = r.bottom + this.d;
        // And that's the new touch area.
        this.touchAreas.add(r);
    }

    public void render(Canvas canvas){

        Paint p = new Paint();

        float dForLetter = this.d*0.8f;
        float borderW = this.d*0.02f;
        p.setStrokeWidth(borderW);

        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Utils.GetElementTypeFace());

        int last = 1;
        if (this.deployed){
            last = this.touchAreas.size();
        }

        int ydraw = this.y;

        for (int i = 0; i < last; i++){
            String text = "";
            int colorForText = Utils.CIRCLE_SELECTOR_TEXT_MAIN;
            int colorForStroke = Color.BLACK;
            if (i == 0){
                p.setColor(Utils.CIRCLE_SELECTOR_BKG_MAIN);
                text = this.getCurrentSelection();
            }
            else {
                text = this.options.get(i-1);
                p.setColor(Utils.CIRCLE_SELECTOR_BKG_OPTION);
                colorForText = Utils.CIRCLE_SELECTOR_TEXT_OPTION;
                colorForStroke = Utils.CIRCLE_SELECTOR_BKG_MAIN;
            }
            // System.err.println("Drawing option " + i + " at " + x + ", " + y + " with " + d);
            // This is the circle.
            canvas.drawCircle(x,ydraw,this.d/2,p);
            // This is the outline
            p.setStyle(Paint.Style.STROKE);
            p.setColor(colorForStroke);
            canvas.drawCircle(x,ydraw,this.d/2,p);

            p.setStyle(Paint.Style.FILL);
            p.setTextSize(Utils.GetTextSizeToFitRect(text,dForLetter,dForLetter,p));

            // Computing the text base line.
            float baseLine = Utils.GetTextBaseLine(text,p,ydraw);
            p.setColor(colorForText);
            canvas.drawText(text,this.x,baseLine,p);

            ydraw = ydraw + this.d;

        }

    }

    public boolean fingerDown(int x, int y){
        if (this.options.isEmpty()) return false; // Nothing to do.
        if (this.deployed) return false; // Nothing to do.
        if (this.touchAreas.isEmpty()) return false;
        if (this.touchAreas.get(0).contains(x,y)){
            System.err.println("Deploying");
            this.deployed = true;
            return true;
        }
        return false;
    }

    public int fingerUp(int x, int y){

        // If this is not deployed, finger up does nothing.
        if (!this.deployed) return -1;

        this.deployed = false;

        for (int i = 1; i < touchAreas.size(); i++){
            if (this.touchAreas.get(i).contains(x,y)) {
                // A selection has been made.
                int new_selection = i-1;
                if (new_selection != this.currentSelectionIndex){
                    this.currentSelectionIndex = new_selection;
                    return this.currentSelectionIndex;
                }
                else return -1;
            }
        }

        return -1;

    }





}

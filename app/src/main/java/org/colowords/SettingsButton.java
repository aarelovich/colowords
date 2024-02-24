package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

public class SettingsButton {

    private RectF bounds;
    private float outerDiameter;
    private float innerDiameter;
    private float rCorner;
    private float beta; // When drawing the cog, this is the angle between the wedges.
    private float alpha; // When drawing the cog this is the angle that each cog tooth sweeps.
    private final float K = 2.0f; // How much bigger is alpha than beta.
    private final int COG_TEETH = 6;
    private boolean pressed;
    public SettingsButton(int xcenter, int ycenter, int w, int h){
        int left = xcenter - w/2;
        int top  = ycenter - h/2;
        this.bounds = new RectF(left,top,left+w,top+h);
        float outerK = 0.8f;
        float innerK = 0.55f;
        this.outerDiameter = Math.min(w*outerK,h*outerK);
        this.innerDiameter = Math.min(w*innerK,h*innerK);
        this.rCorner = 0.05f*Math.max(w,h);

        // Doing the teeth angle math, assuming 6 teeth in a cog.
        int halfNCog = this.COG_TEETH/2;
        this.beta = 180.0f/(halfNCog + halfNCog*K);
        this.alpha = K*beta;

        this.pressed = false;
    }

    public boolean fingerDown(int x, int y){
        if (this.pressed) return false;
        if (this.bounds.contains(x,y)) {
            this.pressed = true;
            return true;
        }
        return false;
    }

    public boolean fingerUp(int x, int y){
        if (!this.pressed) return false;
        // Whenever the finger is lifted the button state must change. But if it's listed outside
        // the button, then it's not clicked.
        this.pressed = false;
        if (this.bounds.contains(x,y)) {
            return true;
        }
        return false;
    }

    public void render(Canvas canvas){

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        int colorCog,colorBKG;

        if (this.pressed){
            colorBKG = Utils.PRIMARY_300;
            colorCog = Utils.ACCENT_100;
        }
        else {
            colorBKG = Utils.PRIMARY_100;
            colorCog = Utils.ACCENT_200;
        }

        p.setColor(colorBKG);

        float innerR = this.innerDiameter/2.0f;
        float outerR = this.outerDiameter/2.0f;
        float xc     = this.bounds.centerX();
        float yc     = this.bounds.centerY();

        // Draw the background
        canvas.drawRoundRect(bounds,rCorner,rCorner,p);

        // Draw the cog. Start With theed.
        p.setColor(colorCog);
        float top  = yc - outerR;
        float left = xc - outerR;
        RectF wedgeBounds = new RectF(left,top,left + this.outerDiameter,top + this.outerDiameter);

        float start = beta/2.0f;
        for (int i = 0; i < COG_TEETH; i++){
           canvas.drawArc(wedgeBounds,start+i*(alpha+beta),alpha,true,p);
        }

        // Draw the inner circle.
        canvas.drawCircle(xc,yc,innerR,p);

        // Draw hole
        p.setColor(colorBKG);
        canvas.drawCircle(xc,yc,innerR*0.5f,p);

    }


}

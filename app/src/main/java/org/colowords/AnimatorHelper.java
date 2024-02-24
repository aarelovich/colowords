package org.colowords;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class AnimatorHelper {

    private List<Rect> rectSteps;
    private List<Integer> displacement;

    public AnimatorHelper() {
        this.rectSteps = new ArrayList<>();
        this.displacement = new ArrayList<>();
    }

    public Rect getNextRect(){
        if (this.rectSteps.isEmpty()) {
            return null;
        }
        Rect r = this.rectSteps.remove(0);
        // System.err.println("Returning rect: " + r.toString() + ". Remaining: " + this.rectSteps.size());
        return r;
    }

    public Integer getNextPosition(){
        if (this.displacement.isEmpty()) return null;
        Integer i = this.displacement.remove(0);
        return i;
    }

    public boolean computeDisplacement(int startingPosition, int endPosition, int holdTime ,int animationDuration, int tickLength){

        if (!this.displacement.isEmpty()) return false;

        int totalSteps = animationDuration/tickLength;
        if ((totalSteps % 2) == 1) totalSteps++; // Making sure we have an even number of steps.
        int halfSteps   = totalSteps/2;

        this.displacement.clear();
        int dp = (endPosition - startingPosition)/halfSteps;

        int p = startingPosition;
        for (int i = 0; i <= halfSteps; i++){
            p = p + dp;
            this.displacement.add(p);

        }

        // IN the middle we need to add the hold time.
        int holdSteps = holdTime/tickLength;
        // That means repeating for several hold steps the last position.
        for (int i = 0; i < holdSteps; i++){
            this.displacement.add(this.displacement.get(this.displacement.size()-1));
        }

        for (int i = halfSteps+1; i < totalSteps; i++){
            p = p - dp;
            this.displacement.add(p);
        }


        return true;
    }

    public boolean computeAnimationSteps(Rect baseRect, float sizeIncrease , int animationDuration, int tickLength){

        // This means an animation is going on. We can't set anything else until it finishes.
        if (!this.rectSteps.isEmpty()) return false;

        // The animation blows up the rectangle and then brings it back down to target size.
        // The number of steps depends on how long the animation lasts sand how long does each step take.

        int totalSteps = animationDuration/tickLength;
        if ((totalSteps % 2) == 1) totalSteps++; // Making sure we have an even number of steps.
        int halfSteps   = totalSteps/2;

        int ow = baseRect.width();
        int oh = baseRect.height();
        int finalW = (int)(ow*sizeIncrease);
        int finalH = (int)(oh*sizeIncrease);

        int dh = (finalH - oh)/halfSteps;
        int dw = (finalW - ow)/halfSteps;

        rectSteps.clear();
        int xc = baseRect.left + ow/2;
        int yc = baseRect.top + oh/2;

        int w = ow;
        int h = oh;

        for (int i = 0; i < totalSteps; i++){

            if (i <= halfSteps){
                w = w + dw;
                h = h + dh;
            }
            else if (i > halfSteps){
                w = w - dw;
                h = h - dh;
            }

            int left   = xc - w/2;
            int right  = left + w;
            int top    = yc - h/2;
            int bottom = yc + h;

            rectSteps.add(new Rect(left, top, right, bottom));

        }

        return true;

    }



}

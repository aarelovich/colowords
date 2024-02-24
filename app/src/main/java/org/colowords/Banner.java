package org.colowords;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Banner {

    private RectF outerBanner;
    private RectF innerBanner;
    private Map<Integer, List<String>> messages;
    private Paint textPaint;
    private RectF textRect;
    private float radious;
    private AnimatorHelper ah;
    private String currentMessage;
    private Utils.AnimationInterface ai;

    public static int MESSAGE_TYPE_WORD_FOUND = 0;
    public static int MESSAGE_TYPE_EXTRA_FOUND = 1;
    public static int MESSAGE_TYPE_NEW_HIGH_SCORE = 2;
    public static int MESSAGE_TYPE_NEW_ZERO_POINT_WORD = 3;

    public Banner(int x, int y, int w, int h, Utils.AnimationInterface ai){

        this.outerBanner = new RectF(x,y,x+w,y+h);

        this.ai = ai;

        this.radious = 0.03f*h;

        // We now compute the inner banner.
        float lw = h*0.05f;
        float xf = x + lw;
        float yf = y + lw;
        float wf = w - 2*lw;
        float hf = h - 2*lw;
        this.innerBanner = new RectF(xf,yf,xf+wf,yf+hf);

        // And the letter paint.
        float textW   = wf*0.9f;
        float textH   = hf*0.9f;
        float textL   = this.innerBanner.centerX() - textW/2;
        float textT   = this.innerBanner.centerY() - textH/2;
        this.textRect = new RectF(textL,textT,textL+textW,textT+textH);

        this.textPaint = new Paint();
        Typeface font = Typeface.create("Mono",Typeface.BOLD);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setTypeface(font);
        //this.textPaint.setTextSize(Utils.GetTextSizeToFitRect(text,textW,textH,this.textPaint));
        this.textPaint.setColor(Utils.BANNER_TEXT);

        // Construct the animation helper.
        this.ah = new AnimatorHelper();

        // And we fill the dictionary.
        this.messages = new HashMap<>();
        this.messages.put(MESSAGE_TYPE_EXTRA_FOUND,getMessages(MESSAGE_TYPE_EXTRA_FOUND));
        this.messages.put(MESSAGE_TYPE_NEW_HIGH_SCORE,getMessages(MESSAGE_TYPE_NEW_HIGH_SCORE));
        this.messages.put(MESSAGE_TYPE_NEW_ZERO_POINT_WORD,getMessages(MESSAGE_TYPE_NEW_ZERO_POINT_WORD));
        this.messages.put(MESSAGE_TYPE_WORD_FOUND,getMessages(MESSAGE_TYPE_WORD_FOUND));

    }

    public void render(Canvas canvas){

        // If we are not moving, then there is not point in rendering.
        // We are hidden.
        Integer pos = this.ah.getNextPosition();
        if (pos == null) {
            this.ai.stopAnimation(Utils.ANIMATION_ID_BANNER);
            return;
        }
        float dy = pos  - this.innerBanner.top;
        //System.err.println("BANNER at Y: " + pos + ". DY: " + dy);

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Utils.BANNER_TEXT);

        this.outerBanner = moveRectToNewY(this.outerBanner,dy);
        this.innerBanner = moveRectToNewY(this.innerBanner,dy);
        this.textRect = moveRectToNewY(this.textRect,dy);

        // Draw the background.
        canvas.drawRoundRect(this.outerBanner,this.radious,this.radious,p);

        // Draw the foreground
        p.setColor(Utils.BANNER_MAIN);
        canvas.drawRoundRect(this.innerBanner,this.radious,this.radious,p);

        // And draw the text.
        this.textPaint.setTextSize(Utils.GetTextSizeToFitRect(this.currentMessage,this.textRect.width(),this.textRect.height(),this.textPaint));
        float baseLine = Utils.GetTextBaseLine(this.currentMessage,this.textPaint,(int)this.textRect.centerY());
        canvas.drawText(this.currentMessage,this.innerBanner.centerX(),baseLine,this.textPaint);

    }

    private RectF moveRectToNewY(RectF r, float dy){
        float h = r.height();
        r.top = r.top + dy;
        r.bottom = r.top + h;
        return r;
    }

    public void showBannerMessage(int type){
        if (!this.messages.containsKey(type)) {
            this.currentMessage =  "";
            return;
        }
        if (this.messages.get(type).size() == 1) {
            this.currentMessage =  this.messages.get(type).get(0);
        }
        else {
            int index = (int)(Math.random()*this.messages.get(type).size());
            this.currentMessage =  this.messages.get(type).get(index);
        }

        // And we start the animation.
        if (this.ah.computeDisplacement(-(int)this.outerBanner.height(),0,800,400,Utils.ANIMATION_TICK_LENGTH)){
            //System.err.println("Staring the banner animation for message: '" + this.currentMessage + "'");
            this.ai.startAnimation(Utils.ANIMATION_ID_BANNER);
        }

    }

    private List<String> getMessages(int type){
        List<String> list = new ArrayList<>();

        if (type == MESSAGE_TYPE_EXTRA_FOUND){
            list.add("FOUND A HIDDEN ONE. GOOD!");
            list.add("THAT WASN'T IN THE PUZZLE!");
            list.add("YOU ARE A TREASURE DIGGER!");
        }
        else if (type == MESSAGE_TYPE_NEW_HIGH_SCORE){
            list.add("CONGRATS! NEW HIGH SCORE");
        }
        else if (type == MESSAGE_TYPE_NEW_ZERO_POINT_WORD){
            list.add("BUMMER. BUT YOU ASKED FOR A HINT");
        }
        else if (type == MESSAGE_TYPE_WORD_FOUND){
            list.add("GOOD!");
            list.add("AWESOME");
            list.add("WAY TO GO!");
            list.add("KEEP AT IT!");
            list.add("YOU ARE ON A ROLL!");
            list.add("NICE!");
            list.add("GOOD GOING!");
        }

        return list;

    }

}
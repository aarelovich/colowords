package org.colowords;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class SettingsScreen {

    private Banner banner;
    private Letter letterHinted;
    private Letter letterShown;
    private LetterWheel letterWheel;
    private Score score;
    private ExtraWordIndicator extraIndicator;
    private CircleDropDown dropDown;
    private CurrentWord madeWord;
    private Context context; // Required for saving the preferences
    private boolean isActive;
    private boolean prefDeleteRequest;
    private RectF divisorDef;
    private RectF titleViewDef;
    private RectF defViewDef;
    private Button btnChangeStyle;
    private Button btnChangeUIFont;
    private Button btnChangeMessageFont;
    private Button btnChangeLetterFont;
    private Button btnSave;
    private Button btnReset;

    public SettingsScreen(Context context, int width, int height){

        this.context = context;
        this.isActive = false;
        this.prefDeleteRequest = false;


        float divisorWidth      = 0.01f*width;

        int width_for_display = (int)(0.7f*width);
        int horizontalMargin  = (int)(0.02f*width);
        int effectiveLeft     = horizontalMargin;
        int effectiveWidth    = (width_for_display-2*horizontalMargin);
        int effectiveRight    = horizontalMargin + effectiveWidth;
        int verticalAir       = (int)(0.03*height);

        this.divisorDef = new RectF(effectiveRight+horizontalMargin,0.01f*height,effectiveRight+horizontalMargin+divisorWidth,0.99f*height);

        // Will be using this to configure all elements.
        int x,y,w,h,d;

        // The banner up top
        int bannerH = (int)(height*0.05f);
        this.banner = new Banner(horizontalMargin,0,effectiveWidth,bannerH);
        this.banner.setMessage("MESSAGE!");

        d = (int)(effectiveWidth*0.2f);
        x = horizontalMargin*2 + d/2;

        // Dropdown with options
        y = bannerH + verticalAir + d/2;
        this.dropDown = new CircleDropDown(x,y,d);
        this.dropDown.addOptions("OPT1");
        this.dropDown.addOptions("OPT2");
        this.dropDown.addOptions("OPT3");
        this.dropDown.setCurrentIndex(0);

        // Now the letters (Display and Hidde)
        y = y + d + verticalAir;
        this.letterShown = new Letter(false);
        this.letterShown.setLetter("D");
        this.letterShown.setGeometry(x,y,d);
        this.letterShown.revealLetter();

        y = y + d + verticalAir;
        this.letterHinted = new Letter(false);
        this.letterHinted.setLetter("H");
        this.letterHinted.setGeometry(x,y,d);
        this.letterHinted.forceHintedState();

        // And the text and definitions view.
        w = effectiveRight - horizontalMargin*7 - d;
        x = effectiveRight - w - horizontalMargin*2;
        int top = bannerH + verticalAir;
        h = (int)(0.05f*height);
        this.titleViewDef = new RectF(x,top,x+w,top+h);
        top = top+h;
        int mustOccupy = verticalAir*2 + d*3;
        h = mustOccupy - h;
        this.defViewDef   = new RectF(x,top,x+w,top+h);

        // The current word
        w = (int)(effectiveWidth*0.5f);
        h = (int)(height*0.08f);
        x = (effectiveWidth - w)/2 + horizontalMargin;
        y = y + d/2 + verticalAir;
        this.madeWord = new CurrentWord(x,y,w,h);
        this.madeWord.setWord("EXAMPLE");

        // The letter wheel.
        x = effectiveWidth/2 + horizontalMargin;
        d = (int)(effectiveWidth*0.6f);
        y = y + h + verticalAir + d/2;
        this.letterWheel = new LetterWheel(x,y,d);
        ArrayList<String> letters = new ArrayList<>();
        letters.add("A"); letters.add("B"); letters.add("C"); letters.add("D");
        this.letterWheel.setLetters(letters);

        // The extra indicator.
        x = effectiveWidth/2 + horizontalMargin;
        y = y + d/2 + verticalAir;
        d = (int)(effectiveWidth*0.4f);
        y = y + d/2;
        this.extraIndicator = new ExtraWordIndicator(x,y,d);
        this.extraIndicator.setNumber(23,70);

        // The Score.
        y = y + d/2 + verticalAir;
        w = (int)(0.6f*effectiveWidth);
        h = (int)(0.1f*height);
        x = (effectiveWidth - w)/2 + horizontalMargin;
        this.score = new Score(y,x,w,h,null);
        this.score.updateValues(3456789,12);

        ///////////////////////////// RIGHT SIDE /////////////////////////////
        int verticalMargin = (int)(height*0.02f);

        w = width - width_for_display - (int)(divisorWidth) - 2*horizontalMargin;
        x = width_for_display + (int)(divisorWidth) + horizontalMargin;
        h = (int)(height*0.05f);
        y = verticalMargin;

        this.btnChangeStyle = new Button(y,x,w,h,"STYLE");
        y = y + verticalMargin + h;
        this.btnChangeLetterFont = new Button(y,x,w,h,"LETTER");
        y = y + verticalMargin + h;
        this.btnChangeMessageFont = new Button(y,x,w,h,"MESSAGE");
        y = y + verticalMargin + h;
        this.btnChangeUIFont = new Button(y,x,w,h,"UI");

        y = height - verticalMargin - h;
        this.btnSave = new Button(y,x,w,h,"BACK");
        y = y - verticalMargin - h;
        this.btnReset = new Button(y,x,w,h,"ZERO SCORE");

    }

    private void renderDefinitionExampleDialog (Canvas canvas) {

        Paint bkg = new Paint();
        bkg.setStyle(Paint.Style.FILL);

        String word = "WORD";
        String def  = "Definition";

        Paint textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Utils.GetTextDefinitionColor());
        textPaint.setTextSize(Utils.GetTextSizeToFitRect(word,this.titleViewDef.width()*0.8f,this.titleViewDef.height()*0.8f,textPaint));

        // First represents the title.
        bkg.setColor(Utils.PRIMARY_100);
        canvas.drawRect(this.titleViewDef,bkg);
        float baseline = Utils.GetTextBaseLine(word,textPaint,(int)this.titleViewDef.centerY());
        canvas.drawText(word,this.titleViewDef.centerX(),baseline,textPaint);


        // Second represents the definitions.
        bkg.setColor(Utils.PRIMARY_200);
        canvas.drawRect(this.defViewDef,bkg);
        baseline = Utils.GetTextBaseLine(def,textPaint,(int)this.defViewDef.centerY());
        canvas.drawText(def,this.defViewDef.centerX(),baseline,textPaint);

    }

    public boolean getPreferenceDeleteRequest(){
        // Since this is a dangerous flag, we make sure that reading it, sets it to false.
        boolean ans = this.prefDeleteRequest;
        this.prefDeleteRequest = false;
        return ans;
    }

    public void show(){
        this.isActive = true;
    }

    public void hide(){
        this.isActive = false;
    }

    public boolean isBeingShown(){
        return this.isActive;
    }

    public void render(Canvas canvas){

        // We paint the background color
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Utils.BG_100);
        canvas.drawPaint(paint);

        this.banner.render(canvas);
        this.letterHinted.render(canvas);
        this.letterShown.render(canvas);
        this.renderDefinitionExampleDialog(canvas);
        this.madeWord.render(canvas);
        this.letterWheel.render(canvas);
        this.extraIndicator.render(canvas);
        this.score.render(canvas);

        // This should be drawn last from the options.
        this.dropDown.render(canvas);

        // Drawing the divisor.
        paint.setColor(Utils.ACCENT_100);
        float r = this.divisorDef.width()/2;
        canvas.drawRoundRect(this.divisorDef,r,r,paint);

        // The right side.
        this.btnChangeUIFont.render(canvas);
        this.btnChangeMessageFont.render(canvas);
        this.btnChangeLetterFont.render(canvas);
        this.btnChangeStyle.render(canvas);
        this.btnReset.render(canvas);
        this.btnSave.render(canvas);

    }

    public boolean fingerDown(int x, int y){

        if (btnSave.fingerDown(x,y)){
            return true;
        }

        if (btnChangeStyle.fingerDown(x,y)){
            return true;
        }

        if (btnChangeUIFont.fingerDown(x,y)){
            return true;
        }

        if (btnChangeLetterFont.fingerDown(x,y)){
            return true;
        }

        if (btnChangeMessageFont.fingerDown(x,y)){
            return true;
        }

        if (btnReset.fingerDown(x,y)){
            return true;
        }

        if (!this.letterWheel.fingerDown(x,y).isEmpty()){
            return true;
        }

        // We now check the color changing elements.
        if (this.letterHinted.getBounds().contains(x,y)){
            Utils.nextHintedLetterColor();
            return true;
        }

        if (this.letterShown.getBounds().contains(x,y)){
            Utils.nextDisplayLetterColor();
            return true;
        }

        if (this.defViewDef.contains(x,y)){
            Utils.nextTextDefColor();
            return true;
        }

        return this.dropDown.fingerDown(x,y);
    }

    public boolean fingerUp(int x, int y){

        if (btnSave.fingerUp(x,y)){
            Utils.SaveStylePreferences(this.context);
            this.isActive = false;
            return true;
        }

        if (btnReset.fingerUp(x,y)){
            this.isActive = false;
            this.prefDeleteRequest = true;
            return true;
        }

        if (btnChangeStyle.fingerUp(x,y)){
            Utils.nextStyle();
            return true;
        }

        if (btnChangeUIFont.fingerUp(x,y)){
            Utils.nextTypeFaceUI();
            return true;
        }

        if (btnChangeLetterFont.fingerUp(x,y)){
            Utils.nextTypeLetter();
            return true;
        }

        if (btnChangeMessageFont.fingerUp(x,y)){
            Utils.nextTypeFaceMessage();
            return true;
        }

        this.letterWheel.fingerUp();
        this.dropDown.fingerUp(x,y);
        // Finger up always invalidates
        return true;
    }

    public boolean fingerMove(int x, int y){
        if (!this.letterWheel.fingerMove(x,y).isEmpty()){
            return true;
        }
        if (this.dropDown.fingerMove(x,y)) return true;
        return false;
    }


}

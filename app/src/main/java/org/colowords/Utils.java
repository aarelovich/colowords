package org.colowords;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Utils {
    static final int BKG_COLOR = Color.parseColor("#472B31");
    static final int LETTER_COLOR_NO_BKG = Color.parseColor("#14111C");
    static final int LINE_COLOR = Color.parseColor("#BF1A2F");
    static final int LETTER_COLOR_WITH_BKG = Color.parseColor("#F3A8B1");
    static final int WHEEL_BKG_COLOR = Color.parseColor("#AD92F1");
    static final int SHUFFLE_BUTTON_NOT_PRESSED = Color.parseColor("#4D5860");
    static final int SHUFFLE_BUTTON_PRESSED = Color.parseColor("#DFE0B3");
    static final int TRANSPARENT = Color.argb(0, 255, 0, 0);
    static final int SQUARE_LETTER_HINTED = Color.parseColor("#ffd299");
    static final int SQUARE_BKG = Color.parseColor("#541f0b");
    static final int SQUARE_LETTER = Color.parseColor("#F3A8B1");
    static final int SQUARE_LETTER_PRESSED = Color.parseColor("#ffd299");
    static final int FORMED_WORD_LETTER = Color.parseColor("#ffffff");
    static final int FORMED_WORD_BKG = Color.parseColor("#E92039");
    static final int EXTRA_IND_BKG = Color.parseColor("#d4eaf7");
    static final int EXTRA_IND_LETTER = Color.parseColor("#3b3c3d");
    static final int EXTRA_IND_FiLL = Color.parseColor("#00668c");
    static final int EXTRA_SELECTED_BKG = Color.parseColor("#cccbc8");
    static final int CIRCLE_SELECTOR_BKG_MAIN = Color.parseColor("#2E8B57");
    static final int CIRCLE_SELECTOR_BKG_OPTION = Color.parseColor("#c6ffe6");
    static final int CIRCLE_SELECTOR_TEXT_MAIN = Color.parseColor("#FFFFFF");
    static final int CIRCLE_SELECTOR_TEXT_OPTION = Color.parseColor("#2d2d2d");
    static final int BUTTON_TEXT_COLOR = Color.parseColor("#e0e0e0");
    static final int BANNER_MAIN = Color.parseColor("#4a9d9c");
    static final int BANNER_TEXT = Color.parseColor("#ffe0c8");

    // Word size constants.
    static final int MAX_WORD_SIZE = 7;
    static final int MIN_WORD_SIZE = 3;

    static final int ANIMATION_TICK_LENGTH= 20;

    // The animation IDS.
    static final int ANIMATION_ID_SCORE = 100;
    static final int ANIMATION_ID_EXTRA = 200;
    static final int ANIMATION_ID_BANNER = 300;

    private static Typeface[] TYPEFACES;

    public interface AnimationInterface {
        public void startAnimation(int id);
        public void stopAnimation(int id);
    }


    public static void LoadTypeFaces(AssetManager asm){
        TYPEFACES = new Typeface[7];
        for (int i = 0; i < TYPEFACES.length; i++){
            String name = "0" + Integer.toString(i);
            TYPEFACES[i] = Typeface.createFromAsset(asm,name + ".otf");
        }
    }

    public static Typeface GetLetterTypeFace(){
        return TYPEFACES[0];
    }

    public static Typeface GetElementTypeFace(){
        return TYPEFACES[1];
    }

    /**
     * I asked ChatGPT to write this code.
     * The point of function is to ensure that a given text, to be drawn with a given paint object,
     * fits within the specified width and height.
     * @param text - The text to be fitted.
     * @param width - The width in which the text must fit.
     * @param height - The height in which the text must fit.
     * @param paint - The paint object to be used.
     * @return the value to pass to the setTextSize of the paint object passed.
     */
    public static float GetTextSizeToFitRect(String text, float width, float height, Paint paint) {
        // Start with an initial guess for text size
        float textSize = 100f;

        // Set the initial text size
        paint.setTextSize(textSize);

        // Get the bounds of the text
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textWidth = paint.measureText(text);
        float textHeight = fm.descent - fm.ascent;

        // Calculate the scaling factors for width and height
        float widthScale = width / textWidth;
        float heightScale = height / textHeight;

        // Use the smaller of the two scales to fit the text inside the rectangle
        float scale = Math.min(widthScale, heightScale);

        // Set the text size based on the scale
        textSize *= scale;

        return textSize;
    }

    /**
     * Computes the y position that I need to pass to the drawText function of the Canvas.
     * @param text - The text to rencer.
     * @param p - The paint object used on the tex. Importat that it has set the typeface and font size.
     * @param y - The y position of the vertical center of the graphical element where the text needs to be shown.
     * @return - The value for the baseline.
     */
    public static float GetTextBaseLine(String text, Paint p, int y) {
        Rect textBounds = new Rect();
        p.getTextBounds(text, 0, text.length(), textBounds);
        float textHeight = textBounds.height();
        return y + textHeight/2f;
    }

    public static int GetRandomInteger(int min, int max) {
        // We add 1 to max so that we INCLUDE the max in the possible output.
        return (int) ((Math.random() * (max+1 - min)) + min);
    }

}

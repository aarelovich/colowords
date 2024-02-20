package org.colowords;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Size;
import android.util.SizeF;

public class Utils {
    static final int BKG_COLOR = Color.parseColor("#472B31");
    static final int LETTER_COLOR_NO_BKG = Color.parseColor("#14111C");
    static final int LINE_COLOR = Color.parseColor("#BF1A2F");
    static final int LETTER_COLOR_WITH_BKG = Color.parseColor("#F3A8B1");
    static final int WHEEL_BKG_COLOR = Color.parseColor("#AD92F1");
    static final int SHUFFLE_BUTTON_NOT_PRESSED = Color.parseColor("#4D5860");
    static final int SHUFFLE_BUTTON_PRESSED = Color.parseColor("#DFE0B3");
    static final int TRANSPARENT = Color.argb(0, 255, 0, 0);
    static final int SQUARE_BKG = Color.parseColor("#BF471A");
    static final int SQUARE_LETTER = Color.parseColor("#F3A8B1");
    static final int FORMED_WORD_LETTER = Color.parseColor("#ffffff");
    static final int FORMED_WORD_BKG = Color.parseColor("#E92039");

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

    public static int GetRandomInteger(int min, int max) {
        // We add 1 to max so that we INCLUDE the max in the possible output.
        return (int) ((Math.random() * (max+1 - min)) + min);
    }

}

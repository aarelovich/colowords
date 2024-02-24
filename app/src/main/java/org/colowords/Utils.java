package org.colowords;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static int PRIMARY_100;
    public static int PRIMARY_200;
    public static int PRIMARY_300;
    public static int ACCENT_100 ;
    public static int ACCENT_200 ;
    public static int TEXT_100;
    public static int TEXT_200;
    public static int BG_100;
    public static int BG_200;
    public static int BG_300;

    public static int TRANSPARENT = Color.parseColor("#00000000");

    // Word size constants.
    static final int MAX_WORD_SIZE = 7;
    static final int MIN_WORD_SIZE = 3;
    static final int ANIMATION_TICK_LENGTH= 20;

    // The animation IDS.
    static final int ANIMATION_ID_SCORE = 100;
    static final int ANIMATION_ID_EXTRA = 200;
    static final int ANIMATION_ID_BANNER = 300;

    private static List<Typeface> TYPEFACES;
    private static List< List<Integer> > STYLES;
    private static final int MAX_FONT_INDEX_TO_LOAD_OTF = 11;
    private static final int MAX_FONT_INDEX_TO_LOAD_TTF = 21;

    private static int SELECTED_STYLE;
    private static int SELECTED_FONT_LETTERS  = 7;
    private static int SELECTED_FONT_MESSAGES = 7;
    private static int SELECTED_FONT_UI       = 7;
    private static int STYLE_COLOR_NUMBER     = 10;

    public interface AnimationInterface {
        public void startAnimation(int id);
        public void stopAnimation(int id);
    }

    public static void LoadStyles(){
        STYLES = new ArrayList<>();
        STYLES.add(ListFromStyleString("#2C3A4F|#56647b|#b4c2dc|#FF4D4D|#ffecda|#FFFFFF|#e0e0e0|#1A1F2B|#292e3b|#414654"));
        STYLES.add(ListFromStyleString("#c21d03|#fd5732|#ffb787|#393939|#bebebe|#232121|#4b4848|#fbfbfb|#f1f1f1|#c8c8c8"));
        STYLES.add(ListFromStyleString("#1F3A5F|#4d648d|#acc2ef|#3D5A80|#cee8ff|#FFFFFF|#e0e0e0|#0F1C2E|#1f2b3e|#374357"));
        STYLES.add(ListFromStyleString("#2E8B57|#61bc84|#c6ffe6|#8FBC8F|#345e37|#FFFFFF|#e0e0e0|#1E1E1E|#2d2d2d|#454545"));
        STYLES.add(ListFromStyleString("#0D6E6E|#4a9d9c|#afffff|#FF3D3D|#ffe0c8|#FFFFFF|#e0e0e0|#0D1F2D|#1d2e3d|#354656"));
        STYLES.add(ListFromStyleString("#E7D1BB|#c8b39e|#84725e|#A096A5|#463e4b|#A096A2|#847a86|#151931|#252841|#3d3f5b"));
        STYLES.add(ListFromStyleString("#6A00FF|#a64aff|#ffb1ff|#00E5FF|#00829b|#FFFFFF|#e0e0e0|#1A1A1A|#292929|#404040"));
        STYLES.add(ListFromStyleString("#1e295a|#4c5187|#abacea|#F18F01|#833500|#353535|#5f5f5f|#F5ECD7|#ebe2cd|#c2baa6"));
        STYLES.add(ListFromStyleString("#0085ff|#69b4ff|#e0ffff|#006fff|#e1ffff|#FFFFFF|#9e9e9e|#1E1E1E|#2d2d2d|#454545"));
        STYLES.add(ListFromStyleString("#805b00|#b38835|#ffe992|#ffc941|#926b00|#d3f1ff|#95cce7|#023047|#194058|#365973"));
        STYLES.add(ListFromStyleString("#C9ADA7|#ab908b|#69514c|#F2CCB8|#8e6d5b|#FFFFFF|#c4c3c3|#44496b|#555a7d|#707499"));
        STYLES.add(ListFromStyleString("#FFDAB9|#dfb28a|#8c633c|#ffbda3|#975f48|#000000|#615353|#F9AFAF|#eea5a5|#c57f80"));
        STYLES.add(ListFromStyleString("#2563eb|#598EF3|#D3E6FE|#d946ef|#fae8ff|#cbd5e1|#94a3b8|#1e293b|#334155|#475569"));
        STYLES.add(ListFromStyleString("#FFD369|#EEEEEE|#fdf6fd|#cfdbd5|#e8eddf|#EEEEEE|#FDEBED|#222831|#393E46|#292524"));
        STYLES.add(ListFromStyleString("#f7bf7a|#CFB997|#fdf6fd|#6f8aa1|#9eb2c2|#F9F9F9|#DCDCDC|#567189|#7B8FA1|#3E5975"));
        STYLES.add(ListFromStyleString("#658864|#B7B78A|#fdf6fd|#bc6c25|#ecd79b|#292524|#78716c|#DDDDDD|#EEEEEE|#d1d1d1"));
        STYLES.add(ListFromStyleString("#03C988|#ebfef5|#cefde5|#a1f9d0|#00815b|#EEEEEE|#FDEBED|#13005A|#00337C|#004cd7"));
        STYLES.add(ListFromStyleString("#F67280|#C06C84|#fdf6fd|#03C988|#FFCEFE|#EEEEEE|#FDEBED|#355C7D|#6C5B7B|#4e84a9"));
        SELECTED_STYLE = 0;
        ChangeStyle();
    }

    public static void SaveStylePreferences(Context context){
        Preferences.Save(context,Preferences.KEY_STYLE,Integer.toString(SELECTED_STYLE));
        Preferences.Save(context,Preferences.KEY_LETTER_FONT,Integer.toString(SELECTED_FONT_LETTERS));
        Preferences.Save(context,Preferences.KEY_MESSAGE_FONT, Integer.toString(SELECTED_FONT_MESSAGES));
        Preferences.Save(context,Preferences.KEY_UI_FONT,Integer.toString(SELECTED_FONT_UI));
    }

    public static void LoadStylePreferences(Context context){
        // If the settings have not been generated yet, then adding the 0 will prevent a format number exception.
        SELECTED_FONT_UI = Integer.parseInt("0" + Preferences.GetPreference(context,Preferences.KEY_UI_FONT));
        SELECTED_FONT_LETTERS = Integer.parseInt("0" + Preferences.GetPreference(context,Preferences.KEY_LETTER_FONT));
        SELECTED_FONT_MESSAGES = Integer.parseInt("0" + Preferences.GetPreference(context,Preferences.KEY_MESSAGE_FONT));
        SELECTED_STYLE = Integer.parseInt("0" + Preferences.GetPreference(context,Preferences.KEY_STYLE));
        ChangeStyle();
    }

    private static void ChangeStyle(){
        List<Integer> style = STYLES.get(SELECTED_STYLE);
        PRIMARY_100 = style.get(0);
        PRIMARY_200 = style.get(1);
        PRIMARY_300 = style.get(2);
        ACCENT_100  = style.get(3);
        ACCENT_200  = style.get(4);
        TEXT_100    = style.get(5);
        TEXT_200    = style.get(6);
        BG_100      = style.get(7);
        BG_200      = style.get(8);
        BG_300      = style.get(9);
    }

    private static List<Integer> ListFromStyleString(String color_list){
        String[] parts = color_list.split("\\|");
        List<Integer> list = new ArrayList<>();
        if (parts.length != STYLE_COLOR_NUMBER) {
            System.err.println("Style list is the wrong size: " + parts.length);
            return list;
        }
        for (String color_string: parts){
            list.add(Color.parseColor(color_string));
        }
        return list;
    }

    public static void LoadTypeFaces(AssetManager asm){
        TYPEFACES = new ArrayList<>();
        for (int i = 0; i <= MAX_FONT_INDEX_TO_LOAD_OTF; i++){
            String name;
            if (i < 10) name = "0" + Integer.toString(i);
            else name = Integer.toString(i);
            TYPEFACES.add(Typeface.createFromAsset(asm,name + ".otf"));
        }

        for (int i = 0; i <= MAX_FONT_INDEX_TO_LOAD_TTF; i++){
            String name;
            if (i < 10) name = "0" + Integer.toString(i);
            else name = Integer.toString(i);
            TYPEFACES.add(Typeface.createFromAsset(asm,name + ".ttf"));
        }

        // Mono is added at the end.
        TYPEFACES.add(Typeface.create("Mono",Typeface.BOLD));

    }

    public static void nextTypeFaceUI(){
        SELECTED_FONT_UI = increaseFontIndex(SELECTED_FONT_UI);
    }

    public static void nextTypeLetter(){
        SELECTED_FONT_LETTERS = increaseFontIndex(SELECTED_FONT_LETTERS);
    }

    public static void nextTypeFaceMessage(){
        SELECTED_FONT_MESSAGES = increaseFontIndex(SELECTED_FONT_MESSAGES);
    }

    public static void nextStyle(){
        SELECTED_STYLE++;
        if (SELECTED_STYLE == STYLES.size()) SELECTED_STYLE = 0;
        ChangeStyle();
    }

    private static int increaseFontIndex(int selection){
        selection++;
        if (selection == TYPEFACES.size()) selection = 0;
        return selection;
    }

    public static Typeface GetLetterTypeFace(){
        return TYPEFACES.get(SELECTED_FONT_LETTERS);
    }

    public static Typeface GetElementTypeFace(){
        return TYPEFACES.get(SELECTED_FONT_UI);
    }

    public static Typeface GetMessageTypeFace() { return TYPEFACES.get(SELECTED_FONT_MESSAGES); }

    public static Point GetPointOnCircleFromAngle(float xc, float yc, float R, double angle){
        double xp = R*Math.cos(angle);
        double yp = R*Math.sin(angle);
        return new Point((int)(xp + xc),(int)(yc - yp));
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

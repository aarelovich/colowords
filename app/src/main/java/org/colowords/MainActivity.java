package org.colowords;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GameScreen gameScreen;
    private CrossWordGenerator crosswordGenerator;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.language = "en";

        // I use the device resolution for everything, so I get it first.
        // Then I use it for the game screen.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        //System.err.println("Device Metrics: " + width + "x" + height);

        this.gameScreen = new GameScreen(this,width,height);
        //setContentView(R.layout.activity_main);
        setContentView(this.gameScreen);


        // Creating the crossword generator.
        this.crosswordGenerator = new CrossWordGenerator();
        this.crosswordGenerator.setWordList(this.loadWordFile());
        this.crosswordGenerator.generateNewWordSet(7,10);

        // Setting the letters of the puzzle.
        this.gameScreen.setLetters(this.crosswordGenerator.getLetterList());

    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        int action = event.getActionMasked();
//
//        System.err.println("On touch even main activity");
//
//        switch(action) {
//            case MotionEvent.ACTION_UP:
//                this.crosswordGenerator.generateNewWordSet(7,10);
//                this.gameScreen.setLetters(this.crosswordGenerator.getLetterList());
//                this.gameScreen.fingerUp(0,0);
//                break;
//        }
//        return true;
//    }

    /**
     * Loads the word list for the specified language.
     * @return
     */
    private ArrayList<String> loadWordFile(){

        ArrayList<String> ans = new ArrayList<String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(this.language + "_words.txt")));

            // do reading, usually loop until end of file reading
            String word;
            while ((word = reader.readLine()) != null) {
                ans.add(word);
            }
        }
        catch (IOException e) {
            System.err.println("Failed loading information from input stream. Reason " + e.getMessage());
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    System.err.println("Failed closing reader. Reason " + e.getMessage());
                }
            }
        }

        return ans;

    }


}
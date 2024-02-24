package org.colowords;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GameScreen gameScreen;
    private CrossWordGenerator crosswordGenerator;
    private CrossWordGrid cwg;
    private final int MAX_N_WORDS   = 15;
    private final int MIN_N_WORDS   = 4;

    public static HashMap<String,String> Dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // I use the device resolution for everything, so I get it first.
        // Then I use it for the game screen.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        //System.err.println("Device Metrics: " + width + "x" + height);
        Utils.LoadTypeFaces(getAssets());

        //Preferences.Save(this,Preferences.KEY_MAX_SCORE,"0");
        this.gameScreen = new GameScreen(this, width, height, new GameScreen.NewGameListener() {
            @Override
            public void newGame() {
                makeNewPuzzle();
            }
        });
        setContentView(this.gameScreen);

        // Creating the crossword generator and generating the first puzzle.
        this.crosswordGenerator = new CrossWordGenerator();

        // And now we use the words to generate the word representation.
        this.cwg = new CrossWordGrid();

        // We load the current dictionary.
        this.loadDictionaryCurrentLanguage();

        // this.makeNewPuzzle();
        this.gameScreen.reloadState();

    }


    private void loadDictionaryCurrentLanguage(){
        String language = Preferences.GetPreference(this,Preferences.KEY_LANGUAGE);
        // language = "EN";
        if (!LanguageDictionary.LoadDictionary(this,language.toLowerCase())){
            System.err.println("Dictionary is not loaded. Cannot continue");
        }
    }


    private void makeNewPuzzle(){

        String difficulty_str = Preferences.GetPreference(this,Preferences.KEY_DIFFICULTY);
        String max_score_str = Preferences.GetPreference(this,Preferences.KEY_MAX_SCORE);
        String language = Preferences.GetPreference(this,Preferences.KEY_LANGUAGE);

        int difficulty = Utils.MAX_WORD_SIZE;

        if (!difficulty_str.isEmpty()){
            difficulty = Integer.valueOf(difficulty_str);
        }

        System.err.println("STARTING NEW GAME: Difficulty:  " + difficulty_str + ". Language: " + language + ". Max Score: " + max_score_str);
        this.loadDictionaryCurrentLanguage();

        // We prepare the cross word generator.
        this.crosswordGenerator.setWordList(LanguageDictionary.GetWordList());

        // We randomly generate games, until we get one with at least 4 words (usually 1 to 3 tries)
        while (true){
            this.crosswordGenerator.generateNewWordSet(difficulty,MAX_N_WORDS);
            if (this.crosswordGenerator.getGeneratedWordList().size() > MIN_N_WORDS){ // We need a at least 4 words.
                break;
            }
        }

        // Then we set the letters of the puzzle.
        this.gameScreen.setLetters(this.crosswordGenerator.getLetterList());

        this.gameScreen.setLetters(this.crosswordGenerator.getLetterList());
        cwg.placeWords(this.crosswordGenerator.getGeneratedWordList());

        // Now we get the extra words.
        List<String> wordsUnableToPlace  = this.cwg.getWordsUnableToPlace();
        List<String> extraWords = this.crosswordGenerator.getExtraWords();
        for (String s: wordsUnableToPlace){
            extraWords.add(s);
        }

        this.gameScreen.setNewCrossWord(cwg,extraWords);
        System.err.println("SOLUTION");
        System.err.println(cwg.getStringRepresentation());
    }

}
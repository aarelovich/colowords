package org.colowords;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    private GameScreen gameScreen;
    private CrossWordGenerator crosswordGenerator;
    private CrossWordGrid cwg;
    private final int MAX_N_WORDS   = 15;
    private final int MIN_N_WORDS   = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // I use the device resolution for everything, so I get it first.
        // Then I use it for the game screen.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // We load the type faces.
        Utils.LoadTypeFaces(getAssets());
        // We load the styles.
        Utils.LoadStyles();
        // And now we can safely load the style preferences.
        Utils.LoadStylePreferences(this);

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
        if (!this.gameScreen.reloadState()) this.makeNewPuzzle();

    }


    private void loadDictionaryCurrentLanguage(){
        String language = Preferences.GetPreference(this,Preferences.KEY_LANGUAGE);
        if (language == "") language = "en";
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

        HashMap< String,List<String> > intersectingWords = cwg.getWordIntersections();

        // Now we get the extra words.
        List<String> wordsUnableToPlace  = this.cwg.getWordsUnableToPlace();
        List<String> extraWords = this.crosswordGenerator.getExtraWords();
        extraWords.addAll(wordsUnableToPlace);

        this.gameScreen.setNewCrossWord(cwg,extraWords);
        System.err.println("SOLUTION");
        System.err.println(cwg.getStringRepresentation());
    }

}
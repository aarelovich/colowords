package org.colowords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * The purpose of this class is to implement the scoring logic and have all the necessary information
 * to successfully, no only compute the score but to also compute the maximum possible score
 * By guessing, with no hints , the sequence of highlighted words
 * The logic is described here:
 *
 * Scoring rules.
 *
 * There are three types of words
 * 1) Highlighted words
 * 2) Hidden words
 * 3) Regular words (on the actual cross word grid).
 *
 * There are two types of hints:
 * Long press on a square reveals the square letter
 * Short press on a square shows definition.
 *
 * A word base score is
 * (Number of letters hidden)*N // Make numbers a bit higher N should be 2 or 3.
 *
 * For words of type 1)
 * Get an x3 multiplier. (Called Highlight Multiplier or Ms)
 *
 * For words of type 2
 * Get an x2 multiplier  (Called Hidden Multiplier or Mh)
 *
 * For words of type 3
 * Get a multiplier. x3 (Called No hint Multiplier or Mn)
 *
 * Hints decrease the multiplier value.
 * Requesting a letter for the word sets Mn to 1.
 * Requesting a definition for the word sets Mn to 2.
 *
 * Some definitions.
 * 1) Hidden words: Words that can be formed with the letter but are not on the grid.
 *
 * 2) Highlighted words: Words that are on the grid, have not been discovered yet. The game will highlight a single square (can be an intersection square thereby creating TWO highlighted words). Once the square is filled (By getting the word right), the highlight moves on to another random empty square. Revealing the letter in the square does not affect anything other than Mn penlization
 *
 * Some rules.
 * -> Mn is the only multiplier that is penalized.
 * -> Mn cannot go back up and is per word.
 * -> If a hint is used on square shared by two words, both words get the penalization
 * -> Asking for definition AFTER having revealed a letter has no effect.
 */

public class Scoring {

    private class ScoreWord {
        public int score;
        public int Mn;
        public List<String> intersectingWords;
        private String word;
        public ScoreWord(String word, boolean fromState) {
            if (fromState){
                this.loadFromState(word);
            }
            else {
                this.word = word;
                this.reset();
            }
        }

        private void loadFromState(String state) {
            String[] parts = state.split(":", -1);
            this.word = parts[0];
            this.score = Integer.parseInt(parts[1]);
            this.Mn = Integer.parseInt(parts[2]);
            this.intersectingWords = new ArrayList<>();
            if (parts.length > 3 && !parts[3].isEmpty()) {
                String[] words = parts[3].split(",");
                this.intersectingWords.addAll(Arrays.asList(words));
            }
        }

        public String getStoreString() {
            return word + ":" + score + ":" + Mn + ":" +
                    String.join(",", intersectingWords);
        }

        public void reset() {
            this.score = this.word.length();
            this.Mn = MnStartingMultiplier;
            this.intersectingWords = new ArrayList<String>();
        }

        public void letterHinted() {
            System.err.println("SCORE: HINTED LETTER Word: " + this.word);
            this.Mn = 1;
        }

        public void definitionHinted() {
            System.err.println("SCORE: Definition Request Word: " + this.word);
            this.Mn = 2;
        }

        public void letterRevealed() {
            this.score--;
        }

    }

    public static class GridWordFoundReturn {
        public int score;
        public String highlightWord;

        public GridWordFoundReturn() {
            this.score = 0;
            this.highlightWord = "";
        }
    }

    private final int N = 2; // Generic multiplier
    private final int Mh = 2; // Hidden Multiplier
    private final int Ms = 3; // Highlight Multiplier
    private final int MnStartingMultiplier = 3; // The full value of Mn before penalization.
    private final List<String> highlightWordList;
    private final HashMap<String, ScoreWord> scoreData;
    private int currentScore;
    private int maximumPossibleScore;
    private String currentHighlightedWord;


    public Scoring() {
        this.highlightWordList = new ArrayList<String>();
        this.scoreData = new HashMap<String, ScoreWord>();
        this.currentScore = 0;
        this.maximumPossibleScore = 0;
    }

    public int getCurrentScore() {
        return this.currentScore;
    }

    public int getMaximumPossibleScore() {
        return this.maximumPossibleScore;
    }

    public String createScoringData(HashMap<String, List<String>> intersectingWords) {

        this.scoreData.clear();
        this.highlightWordList.clear();
        this.currentScore = 0;

        for (String word : intersectingWords.keySet()) {
            this.scoreData.put(word, new ScoreWord(word, false));
            for (String w : Objects.requireNonNull(intersectingWords.get(word))) {
                Objects.requireNonNull(this.scoreData.get(word)).intersectingWords.add(w);
            }
            this.highlightWordList.add(word);
        }

        this.getNextHighlightWord();
        return this.currentHighlightedWord;
    }

    public void setMaximumPossibleScore(HashMap<String, List<String>> intersectingWords, List<String> extraWords) {
        this.maximumPossibleScore = ComputeMaximumPossibleScore(intersectingWords, extraWords);
    }

    public GridWordFoundReturn gridWordFound(String foundWord) {

        GridWordFoundReturn gwr = new GridWordFoundReturn();

        if (!this.scoreData.containsKey(foundWord)) {
            return gwr;
        }

        int highligthMultiplier = 1;
        if (Objects.equals(foundWord, this.currentHighlightedWord)){
            highligthMultiplier = Ms;
            this.getNextHighlightWord();
        }

        // We search for the word in the score list.
        ScoreWord sw = this.scoreData.get(foundWord);
        assert sw != null;
        //System.err.println("SCORING: Found Word: " + foundWord + ". Current Score: " + this.currentScore + ". Word Score Base: " + sw.score + ". N " + N + ". Mn " + sw.Mn + ". Highlight Multiplier " + highligthMultiplier);
        this.currentScore = this.currentScore + sw.score*this.N*sw.Mn*highligthMultiplier;

        // Before we return we must adjust all other word scores.
        for (String word : sw.intersectingWords) {
            Objects.requireNonNull(this.scoreData.get(word)).letterRevealed();
        }

        gwr.highlightWord = this.currentHighlightedWord;
        gwr.score = this.currentScore;
        return gwr;

    }

    public void letterHinted(String word) {
        if (this.scoreData.containsKey(word)) {
            Objects.requireNonNull(this.scoreData.get(word)).letterHinted();
        }
    }

    public void definitionHinted(String word) {
        if (this.scoreData.containsKey(word)) {
            Objects.requireNonNull(this.scoreData.get(word)).definitionHinted();
        }
    }

    public int hiddenWordFound(String word){
        this.currentScore = this.currentScore + word.length()*Mh;
        System.err.println("SCORING: Hidden Word Found: " + word + ". Current Score: " + this.currentScore);
        return this.currentScore;
    }

    private void getNextHighlightWord() {
        if (!this.highlightWordList.isEmpty()) {
            this.currentHighlightedWord = this.highlightWordList.remove(0);
        }
        else this.currentHighlightedWord = "";
    }

    public int doPerfectRun(List<String> extraWords) {

        for (String word: extraWords){
            this.hiddenWordFound(word);
        }

        while (true) {

            GridWordFoundReturn gwr = this.gridWordFound(this.currentHighlightedWord);
            if (Objects.equals(gwr.highlightWord, "")) break;
        }

        return this.currentScore;

    }

    private static int ComputeMaximumPossibleScore(HashMap<String, List<String>> intersectingWords, List<String> extraWords) {

        Scoring scoring = new Scoring();
        scoring.createScoringData(intersectingWords);
        return scoring.doPerfectRun(extraWords);

    }

    public String getStoreString() {
        List<String> scoreDataStrings = new ArrayList<>();
        for (ScoreWord sw : scoreData.values()) {
            scoreDataStrings.add(sw.getStoreString());
        }

        return currentScore + ";" +
                maximumPossibleScore + ";" +
                (currentHighlightedWord != null ? currentHighlightedWord : "") + ";" +
                String.join(",", highlightWordList) + ";" +
                String.join("|", scoreDataStrings);
    }

    public String restoreFromState(String state) {
        String[] parts = state.split(";", -1);
        if (parts.length < 5) return "";

        try {
            this.currentScore = Integer.parseInt(parts[0]);
            this.maximumPossibleScore = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            this.currentScore = 0;
            this.maximumPossibleScore = 0;
        }

        System.err.println("RESTORED SCORE: " + this.currentScore + " -> " + this.maximumPossibleScore);

        this.currentHighlightedWord = parts[2];

        this.highlightWordList.clear();
        if (!parts[3].isEmpty()) {
            String[] words = parts[3].split(",");
            this.highlightWordList.addAll(Arrays.asList(words));
        }

        this.scoreData.clear();
        if (!parts[4].isEmpty()) {
            String[] data = parts[4].split("\\|");
            for (String s : data) {
                ScoreWord sw = new ScoreWord(s,true);
                this.scoreData.put(sw.word, sw);
            }
        }

        return this.currentHighlightedWord;
    }
}

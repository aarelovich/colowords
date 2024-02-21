package org.colowords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrossWordGenerator {

    private HashMap<Integer,ArrayList<String> > words;
    private ArrayList<Character> letterList;
    private ArrayList<String> wordList;
    private ArrayList<String> extraWords;
    public static final int MIN_WORD_LENGTH = 3;
    public static final int MAX_WORD_LENGTH = 7;

    private static final int MIN_WORD_LENGTH_SEED = 5;

    public CrossWordGenerator(){
        words = new HashMap<Integer, ArrayList<String>>();
        this.letterList = new ArrayList<Character>();
        this.wordList   = new ArrayList<String>();
        this.extraWords = new ArrayList<String>();
    }

    public ArrayList<String> getLetterList() {
        ArrayList<String> ans = new ArrayList<>();
        for (Character c: this.letterList){
            ans.add("" + c);
        }
        return ans;
    }

    public List<String> getGeneratedWordList(){
        return this.wordList;
    }

    public void setWordList(ArrayList<String> word_list){
        words.clear();
        for (int i = 0; i < word_list.size(); i++){
            String word = word_list.get(i);
            int l = word.length();
            if (!words.containsKey(l)){
                words.put(l,new ArrayList<String>());
            }
            words.get(l).add(word);
        }
    }

    public void generateNewWordSet(int max_length, int n_words){
        // We generate the seed word. This is the first word in the word list.
        generateSeedWord(max_length);

        System.err.println("Seed Word Generated: " + this.wordList.get(0));
        System.err.println("LetterList: " + this.letterList.toString());

        // We generate all possible words given the seed.
        for (int i = MIN_WORD_LENGTH; i <= max_length; i++){
            ArrayList<String> words_to_add = findRandomWords(i,9999 ); // I use 9999 as infinite so that hte parameter won't matter.
            System.err.println("Words of length " + i + " and found " + words_to_add.size());
            for (String w: words_to_add){
                if (!this.wordList.contains(w)) this.wordList.add(w);
            }
        }

        // If they are too many we randomly cut them out, but the seed word must remain.
        // The ones cut out are the extra words.
        String seed = this.wordList.remove(0);
        while (this.wordList.size() > n_words-1){
            int index_to_remove = (int)(Math.random()*this.wordList.size());
            String removed = this.wordList.remove(index_to_remove);
            //System.err.println("Removed word " + removed);
            this.extraWords.add(removed);
        }
        this.wordList.add(seed);

        System.err.println("Final Letter List: " + this.letterList.toString());
        System.err.println("Final String List: " + this.wordList.toString());
        System.err.println("Extra Word List: " + this.extraWords.toString());

    }

    private void generateSeedWord(int length){
        // We'll assume that the length is valid, that is between 5 and 7.

        // We get the seed word.
        int index = (int)(Math.random()*words.get(length).size());
        String seedWord = words.get(length).get(index);

        this.wordList.clear();
        this.wordList.add(seedWord);
        this.extraWords.clear();

        // And we generate the letters.
        this.letterList.clear();
        char[] letters = seedWord.toCharArray();

        // We need to randomly assign the letters to the letter list. For that we use an array of indexex.
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < length; i++) indexes.add(i);

        while (indexes.size() > 0){
            int index_to_remove = (int)(Math.random()*indexes.size());
            int letter_index = indexes.remove(index_to_remove);
            this.letterList.add(letters[letter_index]);
        }

    }

    private ArrayList<String> findRandomWords(int word_length, int n_words){

        ArrayList<String> possibleWords = new ArrayList<>();
        boolean valid = false;


        // We first form a list of all possible words.
        for (String word: words.get(word_length)){
            char[] word_letter = word.toCharArray();
            valid = true;

            ArrayList<Character> tester = new ArrayList<>();
            for (Character c: this.letterList){
                tester.add(c);
            }

            for (char letter : word_letter){
                int index = tester.indexOf(letter);
                if (index == -1){
                    valid = false;
                    break;
                }
                else {
                    tester.remove(index);
                }
            }


            if (valid){
                possibleWords.add(word);
            }
        }

        // Then we taken N words out of the possible wors.
        if (possibleWords.size() <= n_words){
            return possibleWords; // There aren't enough or there are just enough.
        }

        // We pick randomly.
        ArrayList<String> toreturn = new ArrayList<>();
        for (int i = 0; i < n_words; i++){
            int index = (int)(Math.random()*possibleWords.size());
            toreturn.add(possibleWords.remove(index));
        }

        return toreturn;

    }


}

package org.colowords;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class LanguageDictionary {

    private static HashMap<String, String[]> dictionary;

    public static boolean LoadDictionary(Context context, String lang_code){

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(lang_code + "_words.txt")));

            // do reading, usually loop until end of file reading
            String word;

            if (dictionary == null) dictionary = new HashMap<>();
            else dictionary.clear();

            while ((word = reader.readLine()) != null) {
                String[] word_and_def = word.split("\\|");
                String[] def_and_word;
                if (word_and_def.length == 2) {
                    def_and_word = new String[1];
                }
                else if (word_and_def.length == 3){
                    def_and_word = new String[2];
                    def_and_word[1] = word_and_def[2];
                }
                else continue;
                def_and_word[0] = word_and_def[1];
                dictionary.put(word_and_def[0],def_and_word);
            }

            System.err.println("Finished Loading Dictionary from lang code '" + lang_code + "' With: " + Integer.toString(dictionary.size()) + " words");

        }
        catch (IOException e) {
            System.err.println("Failed loading information from input stream. Reason " + e.getMessage());
            return false;
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

        return true;
    }

    public static String GetDefinition(String word, boolean includeTrueSpelling){
        if (dictionary == null) return "";
        if (!dictionary.containsKey(word)) {
            return "";
        }
        String[] def_and_true_spelling = dictionary.get(word);
        if (includeTrueSpelling){
            if (def_and_true_spelling.length == 2){
                return def_and_true_spelling[1] + ": " + def_and_true_spelling[0];
            }
            else {
                return def_and_true_spelling[0];
            }
        }
        else {
            return def_and_true_spelling[0];
        }
    }

    public static String[] GetWordList() {
        if (dictionary == null) return null;
        return dictionary.keySet().toArray(new String[0]);
    }

    public static String[] GetLongestDefinition(){
        String[] wordlist = GetWordList();
        if (wordlist == null) return null;

        String[] ret = new String[2];

        int max = 0;

        for (int i = 0; i < wordlist.length; i++){
            String s = wordlist[i];
            String def = dictionary.get(s)[0];
            if (def.length() > max){
                ret[0] = s;
                ret[1] = def;
                max = def.length();
            }
        }

        return ret;

    }


}

package org.colowords;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class LanguageDictionary {

    private static HashMap<String, String> dictionary;

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
                if (word_and_def.length != 2) continue;
                dictionary.put(word_and_def[0],word_and_def[1]);
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

    public static String GetDefinition(String word){
        if (dictionary == null) return "";
        if (!dictionary.containsKey(word)) {
            return "";
        }
        return dictionary.get(word);
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
            String def = dictionary.get(s);
            if (def.length() > max){
                ret[0] = s;
                ret[1] = def;
                max = def.length();
            }
        }

        return ret;

    }


}

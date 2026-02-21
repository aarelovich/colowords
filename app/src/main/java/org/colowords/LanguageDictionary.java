package org.colowords;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

public class LanguageDictionary {

    private static HashMap<String, String[]> dictionary;

    public static boolean LoadDictionary(Context context, String lang_code){

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(lang_code + ".json")));

            // do reading, usually loop until end of file reading
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();

            // Now we parse the JSON string.
            JSONObject jsonObject = new JSONObject(jsonString);

            if (dictionary == null) dictionary = new HashMap<>();
            else dictionary.clear();

            // We now iterate over each word.
            Iterator<String> words = jsonObject.keys();

            while (words.hasNext()) {
                String word = words.next();
                JSONArray definitionList = jsonObject.getJSONArray(word);
                StringBuilder definition = new StringBuilder();
                for (int i = 0; i < definitionList.length(); i++){
                    definition.append(Integer.toString(i+1)).append(". ").append(definitionList.getString(i));
                    if (i < definitionList.length() - 1) definition.append("\n");
                }
                dictionary.put(word.toUpperCase(),new String[]{definition.toString()});
            }
            System.err.println("Finished Loading Dictionary from lang code '" + lang_code + "' With: " + Integer.toString(dictionary.size()) + " words");

        }
        catch (Exception e) {
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

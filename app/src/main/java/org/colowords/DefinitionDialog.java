package org.colowords;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class DefinitionDialog extends Dialog {

    private TextView firstWord;
    private TextView firstDefinition;
    private TextView secondWord;
    private TextView secondDefinition;
    private ScrollView secondScrollView;
    private ScrollView firstScrollView;

    public DefinitionDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.definition_dialog);

        firstDefinition = findViewById(R.id.definition1);
        firstWord = findViewById(R.id.word1);
        secondDefinition = findViewById(R.id.definition2);
        secondWord = findViewById(R.id.word2);
        secondScrollView = findViewById(R.id.secondWordScroll);
        firstScrollView = findViewById(R.id.firstWordScroll);

        firstScrollView.setBackgroundColor(Utils.PRIMARY_100);
        secondScrollView.setBackgroundColor(Utils.PRIMARY_100);

        this.configureDefinitionTextView(secondDefinition);
        this.configureDefinitionTextView(firstDefinition);
        this.configureWordTextView(firstWord);
        this.configureWordTextView(secondWord);

    }

    public void setDefinitionsFromGridWord(List<GridWord> words){

        System.err.println("Setting Definitions For the Following Grid Words");
        for (GridWord gw: words){
            System.err.println("  " + gw.toString());
        }

        if (words.isEmpty()) return;
        this.setFirstWord(getWordAndDef(words.get(0)));
        if (words.size() > 1){
            this.setSecondWord(getWordAndDef(words.get(1)));
        }
        else {
            this.setSecondWord(null);
        }
    }

    private String[] getWordAndDef(GridWord gw){

        System.err.println("Trying to get the definition of: " + gw.getString());
        System.err.println("Definition should be: " + LanguageDictionary.GetDefinition(gw.getString(),true));

        String[] ret = new String[2];
        if (gw.getHiddenFlag()){
            if (gw.getHorizontal()){
                ret[0] = "Horizontal";
            }
            else {
                ret[0] = "Vertical";
            }
        }
        else {
            ret[0] = gw.getString();
        }
        ret[1] = LanguageDictionary.GetDefinition(gw.getString(),!gw.getHiddenFlag());
        return ret;
    }

    private void setFirstWord(String[] w_and_d){
        firstWord.setText(w_and_d[0]);
        firstDefinition.setText(w_and_d[1]);
    }

    private void setSecondWord(String[] w_and_d){
        if (w_and_d == null){
            secondScrollView.setVisibility(View.GONE);
            secondWord.setHeight(0);
            secondDefinition.setHeight(0);
        }
        else {
            secondWord.setText(w_and_d[0]);
            secondDefinition.setText(w_and_d[1]);
        }
    }



    // Method to set text dynamically
    public void setWordsAndDefinitions(String word1, String def1, String word2, String def2) {

//        System.err.println("WORD 1: " + word1 + ": " + def1);
//        System.err.println("WORD 2: " + word2 + ": " + def2);

        firstWord.setText(word1);
        firstDefinition.setText(def1);

        if (word2.isEmpty()){
            secondScrollView.setVisibility(View.GONE);
            secondWord.setHeight(0);
            secondDefinition.setHeight(0);
        }
        else {
            secondWord.setText(word2);
            secondDefinition.setText(def2);
        }

    }

    private void configureDefinitionTextView(TextView tv){
        tv.setTextSize(20); // Change 20 to your desired font size
        tv.setBackgroundColor(Utils.PRIMARY_100);
        tv.setTextColor(Utils.GetTextDefinitionColor()); // Change your_color to your desired color
        tv.setGravity(Gravity.LEFT);
        tv.setWidth(getWidth());
    }

    private void configureWordTextView(TextView tv){
        tv.setTypeface(null, Typeface.BOLD_ITALIC);
        tv.setBackgroundColor(Utils.PRIMARY_200);
        tv.setTextColor(Utils.GetTextDefinitionColor()); // Change your_color to your desired color
        tv.setWidth(getWidth());
        tv.setGravity(Gravity.LEFT);
    }

    private int getWidth() {
        return (int)(getContext().getResources().getDisplayMetrics().widthPixels*0.8f);
    }

}

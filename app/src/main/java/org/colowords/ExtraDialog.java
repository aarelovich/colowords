package org.colowords;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class ExtraDialog extends Dialog {

    private TextView definitionView;
    private TextView titleView;
    private ListView wordListView;
    private List<String> wordList;
    private ScrollView srcollViewDef;

    public ExtraDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extra_list);

        titleView = findViewById(R.id.extraDialogTitle);
        definitionView = findViewById(R.id.extraDialogDefinitionView);
        wordListView = findViewById(R.id.extraDialogWordList);
        srcollViewDef = findViewById(R.id.scrollViewDef);

        srcollViewDef.setBackgroundColor(Utils.PRIMARY_200);
        wordListView.setBackgroundColor(Utils.PRIMARY_300);

        // Adjust dialog dimensions to half the screen width and half the screen height
        this.configureDialogSize();

        // Configure the definition view
        this.configureDefinitionView();

        // We set the title for the dialog.
        this.configureTitle();
    }

    public void setWordList (List<String> list){

        this.wordList = list;

        WordListAdapter adapter = new WordListAdapter(getContext(),list);

        wordListView.setAdapter(adapter);

        wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Update TextView with selected item
                String word = wordList.get(position);
                adapter.setCurrentSelection(position);
                definitionView.setText(LanguageDictionary.GetDefinition(word,true));
            }
        });


    }

    private void configureTitle(){
        titleView.setBackgroundColor(Utils.PRIMARY_100);
        titleView.setTextColor(Utils.TEXT_200); // Change your_color to your desired color
        titleView.setGravity(Gravity.CENTER);
        titleView.setText("BONUS WORDS");
    }

    private void configureDefinitionView(){
        definitionView.setTextSize(20); // Change 20 to your desired font size
        definitionView.setTextColor(Utils.TEXT_200); // Change your_color to your desired color
        definitionView.setTypeface(null, Typeface.NORMAL);
        definitionView.setBackgroundColor(Utils.PRIMARY_200);
        definitionView.setGravity(Gravity.LEFT);
    }


    private void configureDialogSize(){
        int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels*0.94f);
        int height = (int)(getContext().getResources().getDisplayMetrics().heightPixels*0.5f);
        getWindow().setLayout(width,height);
    }

}

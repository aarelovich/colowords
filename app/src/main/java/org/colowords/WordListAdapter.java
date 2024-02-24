package org.colowords;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.Gravity;

import java.util.List;

public class WordListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> items;
    private int selected;

    public WordListAdapter(Context context, List<String> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.selected = -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            //itemView = LayoutInflater.from(context).inflate(R.layout.word_list_item, parent, false);
            itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Get the text view from the layout
        TextView textView = itemView.findViewById(android.R.id.text1);

        textView.setTypeface(null, Typeface.BOLD);
        if (position == this.selected){
            textView.setTextColor(Utils.ACCENT_100); // Change your_color to your desired color
            textView.setBackgroundColor(Utils.PRIMARY_100);
        }
        else {
            textView.setTextColor(Utils.TEXT_200); // Change your_color to your desired color
            textView.setBackgroundColor(Utils.PRIMARY_200);
        }
        textView.setGravity(Gravity.CENTER);

        // Set the text for the item
        textView.setText(items.get(position));

        return itemView;
    }

    public void setCurrentSelection(int index){
        this.selected = index;
        this.notifyDataSetChanged();
    }

}


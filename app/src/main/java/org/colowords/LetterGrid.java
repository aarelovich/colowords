package org.colowords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LetterGrid {

    private int x;
    private int y;
    private int width;
    private int height;
    private int squareSide;
    private int gridWidth;
    private int gridHeight;
    private Map<Integer,Letter> letters;
    private List<GridWord> wordList;
    private GridSize gridSize;
    private List<String> wordsRemaining;

    private final int STATE_INDEX_LETTERS   = 0;
    private final int STATE_INDEX_GRIDWORDS = 1;
    private final int STATE_INDEX_GRIDSIZE  = 2;
    private final int STATE_INDEX_REMAINING = 3;
    private final int STATE_SIZE = 4;

    public LetterGrid (){
        this.letters = new HashMap<>();
        this.wordList = new ArrayList<>();
        this.gridSize = new GridSize(0,0);
        this.wordsRemaining = new ArrayList<>();
    }

    public String getStoreState(){

        // The state of the letter map.
        List<String> letterMapState = new ArrayList<>();
        for (Map.Entry<Integer, Letter> entry : this.letters.entrySet()) {
            int key = entry.getKey();
            String letterState = entry.getValue().getStoreString();
            letterMapState.add(Integer.toString(key) + "=" + letterState);
        }

        // The grid word state.
        List<String> gridWordListState = new ArrayList<>();
        for (GridWord gw: this.wordList){
            gridWordListState.add(gw.getStoreString());
        }


        List<String> state = new ArrayList<>();
        for (int i = 0; i < STATE_SIZE; i++){
            state.add("");
        }

        state.set(STATE_INDEX_LETTERS,String.join("@",letterMapState));
        state.set(STATE_INDEX_GRIDWORDS,String.join("@",gridWordListState));
        state.set(STATE_INDEX_REMAINING,String.join("|",this.wordsRemaining));
        state.set(STATE_INDEX_GRIDSIZE,this.gridSize.getStoreString());

        return String.join(">",state);

    }

    public boolean restoreFromState(Rect rect, String state){

        this.letters.clear();
        this.wordList.clear();
        this.wordsRemaining.clear();

        String[] parts = state.split(">");

        if (parts.length != STATE_SIZE){
            System.err.println("Failed restoring GridSize state from string '" + state + "'. Number of parts " + parts.length + " instead of " + STATE_SIZE);
            return false;
        }

        // Now we need to the inverse for the letter map.
        String letterMapState = parts[STATE_INDEX_LETTERS];
        this.letters.clear();
        String[] dictKeyValuePair = letterMapState.split("@");
        for (String pair: dictKeyValuePair){
            String[] index_and_letter = pair.split("=");
            if (index_and_letter.length != 2) continue;
            int index = Integer.valueOf(index_and_letter[0]);
            Letter l = new Letter(index_and_letter[1]);
            this.letters.put(index,l);
        }

        // And now we reverse the grid word list.
        String[] tempGWList = parts[STATE_INDEX_GRIDWORDS].split("@");
        for (String s: tempGWList){
            this.wordList.add(new GridWord(s));
        }

        String[] tempWordsRem = parts[STATE_INDEX_REMAINING].split("|");
        for (String s: tempWordsRem){
            this.wordsRemaining.add(s);
        }

        // Restore the grid state.
        this.gridSize.restoreFromStringState(parts[STATE_INDEX_GRIDSIZE]);

        // And we call configure with an empty list.
        this.configureGrid(rect,this.gridSize,new ArrayList<GridWord>());

        return true;

    }

    public void configureGrid (Rect rect, GridSize gr, List<GridWord> wordSpec){

        int x = rect.left;
        int y = rect.top;
        int w = rect.width();
        int h = rect.height();

        // The square is going to be limited to by whichever side is smallest the h or the w side.
        int side_h = h / gr.getRowCount();
        int side_w = w / gr.getColumnCount();
        this.squareSide = Math.min(side_h,side_w);

        this.gridWidth = gr.getColumnCount();
        this.width = this.gridWidth*this.squareSide;
        this.gridHeight = gr.getRowCount();
        this.height = this.gridHeight*this.squareSide;

        // Now based on the calculations above we adjust the x and y values.
        int offset_x = (int)(((double)(w) - (double)(this.width))/2.0);
        int offset_y = (int)(((double)(h) - (double)(this.height))/2.0);

        // System.err.println("[DBUG] Input w " + w + " computed w " + this.width + " and final offset is " + offset_x);

        // This should leave it centered in the area originally defined.
        this.x = x + offset_x;
        this.y = y + offset_y;

        this.gridSize.adjustGrid(gr.getRowCount(),gr.getColumnCount());

        // We use this as a flag. When restoring the state, we don't need to do any of the stuff below.
        if (wordSpec.isEmpty()) return;

        // And we now generate the letter array.
        this.letters.clear();
        this.wordList.clear();
        this.wordsRemaining.clear();

        // System.err.println("GRID SIZE: " + this.gridSize.toString());

        for (GridWord gw: wordSpec){

            // We need to add a letter for every letter of the word.
            List<GridPoint> gps = gw.toGridPointList();

            this.wordList.add(gw);
            this.wordsRemaining.add(gw.getString());

            // System.err.println("LETTERS FOR WORD: '" + gw.getString() + "'");

            for (GridPoint gp: gps){

                int positionIndex = gr.toIndex(gp);
                // System.err.println("   " + gp.toString() + " -> " + positionIndex);

                Point p = this.gridPositionToScreenPoint(gp.r,gp.c);

                Letter l = new Letter(false);
                l.setLetter("" + gp.character);
                l.setGeometry(p.x,p.y,this.squareSide);

                this.letters.put(positionIndex,l);

            }


        }

        // System.err.println("Total number of letters " + this.letters.size());

    }

    public void render(Canvas canvas){
        //this.renderGrind(canvas);
        this.renderLetters(canvas);
    }

    private Point gridPositionToScreenPoint(int row, int col){
        Point p = new Point();

        // These coordinates are for the squares (Letter object). I need to specify the square center
        p.x = this.x + col*this.squareSide + this.squareSide/2;
        p.y = this.y + row*this.squareSide + this.squareSide/2;

        return p;
    }

    public boolean isWordInCrossWord(String word) {

        for (GridWord gw: this.wordList){

            if (gw.getString().equals(word)){
                // We got one.

                // We now reveal all the letters.
                for (GridPoint l : gw.toGridPointList()){
                    int index = gridSize.toIndex(l);
                    this.letters.get(index).revealLetter();

                    // We remove the word from the list.
                    index = wordsRemaining.indexOf(word);
                    if (index != -1) wordsRemaining.remove(index);
                }

                return true;
            }

        }

        return false;

    }

    public boolean isPuzzleDone(){
        return this.wordsRemaining.isEmpty();
    }

    /**
     * Will draw the full word grid using lines.
     * Should be used for debugging only.
     * @param canvas
     */
    private void renderGrid(Canvas canvas){

        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.parseColor("#ffffff"));
        p.setStrokeWidth(1.0f);

        canvas.drawRect(this.x,this.y,this.x + this.width,this.y+this.height,p);

        int xacc = this.x;
        int yacc = this.y;

        for (int i = 0; i < this.gridWidth; i++){
            canvas.drawLine(xacc,this.y,xacc,this.y + this.height,p);
            xacc = xacc + this.squareSide;
        }

        for (int i = 0; i < this.gridHeight; i++){
            canvas.drawLine(this.x,yacc,this.x + this.width,yacc,p);
            yacc = yacc + this.squareSide;
        }


    }

    /**
     * Will draw the letters as grid on the screen.
     * @param canvas
     */
    private void renderLetters(Canvas canvas){
        for (Map.Entry<Integer, Letter> entry : this.letters.entrySet()) {
            entry.getValue().render(canvas);
        }
    }

}

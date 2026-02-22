package org.colowords;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LetterGrid {

    private static class HighlightTile {
        private int x;
        private int y;
        private int d;
        private int letterIndex;

        public HighlightTile(){
            this.reset();
        }

        public void reset() {
            this.x = 0;
            this.y = 0;
            this.d = 0;
            this.letterIndex = -1;
        }

        public void update(int x, int y, int d, int letterIndex){
            this.x = x;
            this.y = y;
            this.d = d;
            this.letterIndex = letterIndex;
        }

        public void restoreFromString(String s) {
            String[] parts = s.split("\\|");
            if (parts.length != 4) return;
            this.x = Integer.parseInt(parts[0]);
            this.y = Integer.parseInt(parts[1]);
            this.d = Integer.parseInt(parts[2]);
            this.letterIndex = Integer.parseInt(parts[3]);
        }

        public String getStoreString() {
            return this.x + "|" + this.y + "|" + this.d + "|" + this.letterIndex;
        }

        public int[] getGeometry() {
            return new int[] {this.x, this.y, this.d};
        }

        public int getD() {
            return d;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getLetterIndex() {
            return letterIndex;
        }

        public boolean isValid() {
            return (this.letterIndex >= 0);
        }

    }


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
    private List<String> extraWords;
    private List<String> extraWordsFound;
    private List<GridWord> definitionRequests;
    private List<String> lastSetOfAffectedWords;
    private HighlightTile highlightTile;
    private int letterBeingPressed;
    private final int STATE_INDEX_LETTERS      = 0;
    private final int STATE_INDEX_GRIDWORDS    = 1;
    private final int STATE_INDEX_GRIDSIZE     = 2;
    private final int STATE_INDEX_REMAINING    = 3;
    private final int STATE_INDEX_LETTER_WHEEL = 4;
    private final int STATE_INDEX_EXTRA_WORDS  = 5;
    private final int STATE_INDEX_EXTRA_FOUND  = 6;
    private final int STATE_INDEX_HIGHLIGHT    = 7;
    private final int STATE_SIZE               = 8;

    public static final int PRESS_ACTION_NOTHING      = 0;
    public static final int PRESS_ACTION_HINT         = 1;
    public static final int PRESS_ACTION_DEFINITION   = 2;

    /////////// Word found codes.
    public static final int WORD_NOT_A_WORD  = 0;
    public static final int WORD_EXTRA        = 1;
    public static final int WORD_CROSS       = 2;
    public static final int WORD_EXTRA_AGAIN = 3;
    public static final int WORD_CROSS_HAS_HIGHLIGHT = 4;

    public LetterGrid (){
        this.letters = new HashMap<>();
        this.wordList = new ArrayList<>();
        this.gridSize = new GridSize(0,0);
        this.wordsRemaining = new ArrayList<>();
        this.extraWords = new ArrayList<>();
        this.extraWordsFound = new ArrayList<>();
        this.definitionRequests = new ArrayList<>();
        this.lastSetOfAffectedWords = new ArrayList<>();
        this.highlightTile = new HighlightTile();
        this.letterBeingPressed = -1;
    }

    public List<String> getLastSetOfAffectedWords(){
        return this.lastSetOfAffectedWords;
    }

    public void setHighlightWord(String word) {

        System.err.println("HIGHLIGHT: Word: " + word);

        if (this.highlightTile.isValid()){
            // We must check if it's still hidden.
            if (Objects.requireNonNull(this.letters.get(this.highlightTile.getLetterIndex())).isItHidden()){
                return;
            }
        }

        for (GridWord gw: this.wordList) {
            if (gw.getString().equals(word)){
                // We got the word.
                //System.err.println("   HIGHLIGHT: Found in word list " + word);
                List<GridPoint> gps = gw.toGridPointList();
                // We need to highlight the first letter.
                if (!gps.isEmpty()) {
                    //System.err.println("      HIGHLIGHT AT POS: " + gps.get(0).toString());
                    // We need to search for the first tile that is HIDDEN.
                    for (int i = 0; i < gps.size(); i++){
                        int letterIndex = this.gridSize.toIndex(gps.get(i));
                        if (Objects.requireNonNull(this.letters.get(letterIndex)).isItHidden()){
                            int[] geometry = Objects.requireNonNull(this.letters.get(letterIndex)).getGeometry();
                            this.highlightTile.update(geometry[0],geometry[1],geometry[2],letterIndex);
                            return;
                        }
                    }
                }
            }
        }

    }

    public void markAllExtrasAsFound(){
        for (int i = 0; i < this.extraWordsFound.size(); i++){
            this.extraWordsFound.set(i,"1");
        }
    }

    public int[] getTileToHighLight() {
        if (!this.highlightTile.isValid()) return null;
        return this.highlightTile.getGeometry();
    }

    public List<GridWord> getDefinitionRequests(){
        return this.definitionRequests;
    }

    public String saveState(List<String> letters){

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
        state.set(STATE_INDEX_LETTER_WHEEL,String.join("|",letters));
        state.set(STATE_INDEX_GRIDSIZE,this.gridSize.getStoreString());
        state.set(STATE_INDEX_EXTRA_WORDS,this.getStringToStoreFromList(this.extraWords));
        state.set(STATE_INDEX_EXTRA_FOUND,this.getStringToStoreFromList(this.extraWordsFound));
        state.set(STATE_INDEX_HIGHLIGHT,this.highlightTile.getStoreString());

        return String.join(">",state);

    }

    private String[] getStringArrayFromParsed(int index, String[] parts){
        if (parts[index].equals("-")){
            return null;
        }
        else return parts[index].split("\\|");
    }

    private String getStringToStoreFromList(List<String> list){
        if (list.isEmpty()) return "-";
        else return String.join("|",list);
    }

    /**
     * When restoring the state the resulting list is the letters for the wheel.
     * @param rect - The geometry rec for the letter grid.
     * @param state - The encoded state for all objects that make up the screen of the game.
     * @return - The letters for the letter wheel.
     */
    public ArrayList<String> restoreFromState(Rect rect, String state){

        this.letters.clear();
        this.wordList.clear();
        this.wordsRemaining.clear();

        String[] parts = state.split(">");

        if (parts.length != STATE_SIZE){
            System.err.println("Failed in restoring for letter grid from string '" + state + "'. Number of parts " + parts.length + " instead of " + STATE_SIZE);
            return new ArrayList<>();
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

        String[] tempWordsRem = parts[STATE_INDEX_REMAINING].split("\\|");
        for (String s: tempWordsRem){
            if (s.isEmpty()) continue; // Sometimes an empty string is stored. We just ignore it.
            this.wordsRemaining.add(s);
        }

        // Restore the grid state.
        this.gridSize.restoreFromStringState(parts[STATE_INDEX_GRIDSIZE]);
        //System.err.println("Restoring grid size from: " + parts[STATE_INDEX_GRIDSIZE] + " = " + this.gridSize.toString());

        // We now restore the extra words and the ones that were found.
        String[] tempExtra = this.getStringArrayFromParsed(STATE_INDEX_EXTRA_WORDS,parts);
        if (tempExtra != null){
            for (String s: tempExtra){
                if (s.isEmpty()) continue; // Sometimes an empty string is stored. We just ignore it.
                this.extraWords.add(s);
            }
        }

        String[] tempExtraFound = this.getStringArrayFromParsed(STATE_INDEX_EXTRA_FOUND,parts);
        if (tempExtraFound != null){
            for (String s: tempExtraFound){
                if (s.isEmpty()) continue; // Sometimes an empty string is stored. We just ignore it.
                this.extraWordsFound.add(s);
            }
        }

        // And we call configure with empty list. But they have already been filled.
        this.configureGrid(rect,this.gridSize,new ArrayList<GridWord>(),new ArrayList<>());


        // Finally we restore the letter wheel letters.
        String[] tempWheel = parts[STATE_INDEX_LETTER_WHEEL].split("\\|");
        ArrayList<String> ans = new ArrayList<>();
        for (String s: tempWheel){
            ans.add(s);
        }

        // Restore the highlight tile.
        this.highlightTile.restoreFromString(parts[STATE_INDEX_HIGHLIGHT]);

        System.err.println("Restoring State. Remaining words: " + this.wordsRemaining);
        List<String> found = new ArrayList<>();
        List<String> notfound = new ArrayList<>();
        for (int i = 0; i < this.extraWords.size(); i++){
            if (this.extraWordsFound.get(i).equals("0")){
                // Now found
                notfound.add(this.extraWords.get(i));
            }
            else {
                found.add(this.extraWords.get(i));
            }
        }
        System.err.println("Restoring State. Remaining extra words: " + notfound);
        System.err.println("Restoring State. Extra words already found: " + found);
        return ans;

    }

    public void configureGrid (Rect rect, GridSize gr, List<GridWord> wordSpec, List<String> extraWords){

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

        //System.err.println("Input Rect: " + rect.toString() + ". Square Side: " + squareSide + ". Grid Size: " + gr.toString() + ", final size: " + this.width + "x" + this.height);

        // Now based on the calculations above we adjust the x and y values.
        int offset_x = (int)(((double)(w) - (double)(this.width))/2.0);
        int offset_y = (int)(((double)(h) - (double)(this.height))/2.0);

        //System.err.println("[DBUG] Input w " + w + " computed w " + this.width + " and final offset is " + offset_x);

        // This should leave it centered in the area originally defined.
        this.x = x + offset_x;
        this.y = y + offset_y;

        this.gridSize.adjustGrid(gr.getRowCount(),gr.getColumnCount());

        // We use this as a flag. When restoring the state, we don't need to do any of the stuff below.
        if (wordSpec.isEmpty()) return;

        // We prepare to generate the letter map.
        this.letters.clear();
        this.wordList.clear();
        this.wordsRemaining.clear();
        this.extraWords.clear();
        this.extraWordsFound.clear();
        this.highlightTile.reset();

        // We add the extra words.
        for (String ew: extraWords){
            this.extraWords.add(ew);
            this.extraWordsFound.add("0");
        }

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
                //System.err.println("For GridPoint " + gp.toString() + " Setting at " + p.x + "," + p.y);
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

    public List<String> getExtraWordsFound(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < this.extraWords.size(); i++){
            String w = this.extraWords.get(i);
            // System.err.println("For word " + w  + " -> " +  this.extraWordsFound.get(i));
            if (this.extraWordsFound.get(i).equals("1")){
                list.add(w);
            }
        }
        return list;
    }

    public List<String> getExtraWords(){
        return this.extraWords;
    }

    public int isItAWord(String word) {

        System.err.println("Checking For Word Fig: " + word);

        if (this.wordsRemaining.contains(word)){

            for (GridWord gw: this.wordList){

                if (gw.getString().equals(word)){
                    // We got one.

                    // We now reveal all the letters.
                    boolean containsHighlight = false;
                    for (GridPoint l : gw.toGridPointList()){

                        int index = gridSize.toIndex(l);
                        if (index == this.highlightTile.letterIndex){
                            containsHighlight = true;
                        }
                        // System.err.println("Revealing letter at " + l.toString() + " - " + index + " for word: " + gw.toString());
                        Objects.requireNonNull(this.letters.get(index)).revealLetter();

                    }

                    // We remove the word from the list.
                    int index = wordsRemaining.indexOf(word);
                    if (index != -1) wordsRemaining.remove(index);
                    if (containsHighlight) return WORD_CROSS_HAS_HIGHLIGHT;
                    return WORD_CROSS;

                }

            }

        }
        else {
            // So the word is not part of the grid. We check if it's extra.
            int index = this.extraWords.indexOf(word);
            if (index != -1){
                if (this.extraWordsFound.get(index).equals("0")){
                    this.extraWordsFound.set(index,"1");
                    // Hidden words are double score

                    return WORD_EXTRA;
                }
                else {
                    System.err.println("EXTRA that has been found already");
                    return WORD_EXTRA_AGAIN;
                }
            }
        }

        return WORD_NOT_A_WORD;

    }

    public boolean isPuzzleDone(){
        return this.wordsRemaining.isEmpty();
    }

    public boolean fingerDown(int x, int y){
        for (Map.Entry<Integer, Letter> entry : this.letters.entrySet()) {
            if (entry.getValue().isLetterBeingTouched(x,y)){
                if (entry.getValue().isItPressed()){
                    letterBeingPressed = entry.getKey();
                    System.err.println("Pressing letter at: " + this.gridSize.fromIndex(letterBeingPressed).toString());
                    return true;
                }
            }
        }
        return false;
    }

    public int fingerUp(){

        if (letterBeingPressed == -1) return PRESS_ACTION_NOTHING;

        // System.err.println("Finger up after long press of  letter at: " + this.gridSize.fromIndex(letterBeingPressed).toString());

        int code = this.letters.get(letterBeingPressed).fingerUp();

        System.err.println("LetterGrid-FingerUp: " + code);

        if (code != Letter.PRESS_NO_PRESS){
            // The letter was pressed long. So we need to find the one or two words associated wit the pressed letter.

            GridPoint gp = this.gridSize.fromIndex(letterBeingPressed);
            letterBeingPressed = -1;

            List<GridWord> affectedWords = new ArrayList<>();
            for (GridWord gw: this.wordList){
                if (!gw.getLetterAtPosition(gp.r,gp.c).isEmpty()){
                    // The letter corresponds to this word. We se the hidden flag appropriately.
                    // It's hidden
                    gw.setHiddenFlag(this.wordsRemaining.contains(gw.getString()));
                    affectedWords.add(gw);
                }
            }

            // Now what we do will depend on the press type
            if (code == Letter.PRESS_LONG){

                // This was a hint request.
                this.lastSetOfAffectedWords.clear();
                for (GridWord gw: affectedWords){
                    this.lastSetOfAffectedWords.add(gw.getString());
                }

                return PRESS_ACTION_HINT;

            }
            else {
                // This was a short press. Dictionary definition request.
                this.definitionRequests.clear();
                this.definitionRequests.addAll(affectedWords);
                return PRESS_ACTION_DEFINITION;
            }

        }

        return PRESS_ACTION_NOTHING;
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

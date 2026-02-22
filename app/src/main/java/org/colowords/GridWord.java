package org.colowords;
import java.util.ArrayList;
import java.util.List;

public class GridWord {
   
   private String word;
   private boolean horizontal;
   private int row;
   private int column;
   private boolean hidden; // The hidden flag is only used to mark hidden words to know if the name should be hidden in the definition look up.

   // Store indexes
   private final int STATE_INDEX_ROW = 0;
   private final int STATE_INDEX_COL = 1;
   private final int STATE_INDEX_ORIENTATION = 2;
   private final int STATE_INDEX_WORD = 3;
   private final int STATE_SIZE = 4;

   public GridWord(String w, int r, int c, boolean h){
      this.word = w;
      this.column = c;
      this.row = r;
      this.horizontal = h;
   }

   public void setHiddenFlag(boolean h){
      this.hidden = h;
   }

   public boolean getHiddenFlag(){
      return this.hidden;
   }

   public GridWord(String state){
      String[] parts = state.split("\\|");
      if (parts.length != STATE_SIZE){
         System.err.println("Failed restoring GridWord state from string '" + state + "'. Number of parts " + parts.length + " instead of " + STATE_SIZE);
         return;
      }

      this.word = parts[STATE_INDEX_WORD];
      this.horizontal = Boolean.valueOf(parts[STATE_INDEX_ORIENTATION]);
      this.row = Integer.valueOf(parts[STATE_INDEX_ROW]);
      this.column = Integer.valueOf(parts[STATE_INDEX_COL]);

   }

   public String getStoreString() {

      List<String> state = new ArrayList<>();
      for (int i = 0; i < STATE_SIZE; i++){
         state.add("");
      }

      state.set(STATE_INDEX_WORD,word);
      state.set(STATE_INDEX_ROW,Integer.toString(row));
      state.set(STATE_INDEX_COL,Integer.toString(column));
      state.set(STATE_INDEX_ORIENTATION,Boolean.toString(horizontal));

      return String.join("|",state);
   }

   public String toString() {
      String h = "--";
      if (!this.horizontal) h = "|";
      return word + " @(" + Integer.toString(row) + "," + Integer.toString(column) + ") " + h;
   }

   public boolean getHorizontal() {
      return this.horizontal;
   }

   public int getRow() {
      return this.row;
   }

   public int getColumn() {
      return this.column;
   }

   public int getEndRow() {
      if (this.horizontal) return this.row;
      else return this.row + this.word.length() - 1;
   }

   public int getEndColumn(){
      if (this.horizontal) return this.column + this.word.length() - 1;
      else return this.column;
   }

   public char[] getCharSequence(){
      return this.word.toCharArray();
   }

   public String getString(){
      return this.word;
   }

   public void adjustPosition(int dr, int dc){
      this.row = this.row - dr;
      this.column = this.column - dc;
   }

   public void setPosition(int r, int c){
      this.row = r;
      this.column = c;
   }

   public boolean doesWordPassOnGridPoint(int r, int c) {
      List<GridPoint> gps = this.toGridPointList();
      for (GridPoint gp: gps) {
         if (gp.isRowColumn(r, c)) return true;
      }
      return false;
   }

   public void setOrientation(boolean horizontal){
      this.horizontal = horizontal;
   }

   /**
    * Transforms each letter of the word to a grid point object, which contains the character and the grid coordiantes of it. 
    * @return
    */
   public List<GridPoint> toGridPointList() {

      List<GridPoint> list = new ArrayList<>();

      int dr, dc; // Delta in rows or columns (1,0) or (0,1) depeding on word orientation. 
      if (this.horizontal){
         dr = 0; dc = 1;
      }
      else {
         dr = 1; dc = 0;
      }

      char[] characters = this.word.toCharArray();

      for (int i = 0; i < characters.length; i++){
         list.add(new GridPoint(i*dr + this.row,i*dc + this.column,characters[i]));
      }

      return list;

   }

   /**
    * KEY FUNCTION - Creates all the possible gridwords such that they have a valid
    * Single letter intersection with this word. The resulting word always has reverse orientation 
    * with respect to this word. 
    * @param s - The generating string. 
    * @return - The list of possible grid words. 
    */

   public List<GridWord> listIntersectingGridWords(String s){

      List<GridWord> list = new ArrayList<>();

      char[] letters = s.toCharArray();
      char[] localWordLetters = this.word.toCharArray();
      
      // All resulting words need to be perpendicular to this one. 
      boolean orientationHorizontal = !this.horizontal;

      for (int i = 0; i < letters.length; i++){
         
         // Now we check where each letter intersects with this word.
         for (int j = 0; j < localWordLetters.length; j++){

            if (letters[i] == localWordLetters[j]){

               // We have an intersection. We get the interscting grid point
               GridPoint gp = this.getGridPointOfLetterAtIndex(j);

               // And now we make a correction based on the letter index of the input word and it's orientation. 
               if (orientationHorizontal){
                  gp.c = gp.c - i; 
               }
               else {
                  gp.r = gp.r - i;
               }

               // And if the point is valid, we create the grid word.
               if (gp.isValid()){
                  list.add(new GridWord(s, gp.r, gp.c, orientationHorizontal));
               }


            }

         }

      }


      return list;

   }

   /**
    * Returs the grid coordinate of a letter of the word. 
    * @param index - Which letter of the word. 
    * @return The grid point.
    */

   public GridPoint getGridPointOfLetterAtIndex(int index){
      
      GridPoint gp = new GridPoint(-1,-1);
      
      char[] localWordLetters = this.word.toCharArray();
      
      if ((index < 0) || (index >= localWordLetters.length)){
         // Invalid index. 
         return gp;
      }

      if (this.horizontal){
         gp.r = this.row;
         gp.c = this.column + index;
      }
      else {
         gp.r = this.row + index;
         gp.c = this.column;
      }

      return gp;

   }

   /**
    * Retuns the list of grid spaces surrounding a word as indexes. 
    * @param gs - The grid size object to convert grid location to integer
    * @return
    */

   public List<Integer> getSurroundingSpacesAsIndexes(GridSize gs){
      
      List<Integer> list = new ArrayList<>();     

      int L = this.word.toCharArray().length;
      List<GridPoint> gp = new ArrayList<>();

      if (this.horizontal){
         // Adding the squares before the beginning and after the end of the word. 
         gp.add(new GridPoint(this.row,this.column-1));
         gp.add(new GridPoint(this.row,this.column+L));

         // Adding the squres above and below the word.
         for (int i = 0; i < L; i++){
            gp.add(new GridPoint(this.row+1,this.column+i));
            gp.add(new GridPoint(this.row-1,this.column+i));
         }
      }
      else {
         // Adding the squares before the beginning and after the end of the word. 
         gp.add(new GridPoint(this.row-1,this.column));
         gp.add(new GridPoint(this.row+L,this.column));

         // Adding the squares to the right and to the left of the word. 
         for (int i = 0; i < L; i++){
            gp.add(new GridPoint(this.row+i,this.column+1));
            gp.add(new GridPoint(this.row+i,this.column-1));
         }   
      }

      // We only add the valid squares (valid = non negative coordinates)
      for (GridPoint p: gp){
         if (!p.isValid()){
            continue;
         }
         list.add(gs.toIndex(p));
      }

      return list;
   }

   /**
    * Gets the letter at a specific grid position, if it falls within the word. 
    * @param r - The row index
    * @param c - The column index
    * @return The letter at (r,c) if it's part of the word or empty string otherwise. 
    */
   public String getLetterAtPosition(int r, int c){
      if (this.horizontal){
         if (r != this.row) return "";
         if ((c < this.column) || (c > this.getEndColumn())) return "";
         int i = c - this.column;
         return "" + this.word.toCharArray()[i];
      }
      else {
         if (c != this.column) return "";
         if ((r < this.row) || (r > this.getEndRow())) return "";
         int i = r - this.row;
         return "" + this.word.toCharArray()[i];
      }
   }

   public List<GridPoint> getLettersAdjacentToPosition(int r, int c){
      
      String ch;
      GridPoint gp1 = new GridPoint();
      GridPoint gp2 = new GridPoint();

      List<GridPoint> list = new ArrayList<>();

      if (this.horizontal){
         gp1.r = r; gp1.c = c+1;
         gp2.r = r; gp2.c = c-1;
      }
      else {
         gp1.r = r+1; gp1.c = c;
         gp2.r = r-1; gp2.c = c;
      }

      ch = getLetterAtPosition(gp1.r, gp1.c);
      if (!ch.isEmpty()){
         gp1.character = ch.charAt(0);
         list.add(gp1);
      }

      ch = getLetterAtPosition(gp2.r, gp2.c);
      if (!ch.isEmpty()){
         gp2.character = ch.charAt(0);
         list.add(gp2);
      }

      return list;

   }

}


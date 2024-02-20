package org.colowords;
import java.util.ArrayList;
import java.util.List;

public class CrossWordGrid {

   /////////// Private Variables. 
   private ArrayList<GridWord> words;
   private ArrayList<Integer>  occupiedSpaces;
   private GridSize gridSize;
   private List<String> wordsUnableToPlace;

   private final int STARTING_NUMBER_OF_ROWS     = 50;
   private final int STARTING_NUMBER_OF_COLUMNS  = 50;

   private final boolean GEN_DEBUG = false;

   ////////// Function Declaration Start 
   public CrossWordGrid(){
      this.words = new ArrayList<GridWord>();      
      this.gridSize = new GridSize(0, 0);
      this.occupiedSpaces = new ArrayList<>();
      this.wordsUnableToPlace = new ArrayList<>();
   }

   /**
    * Add Word straight up. Mostly used for debugging. 
    * @param word - The word to add
    * @param row - The row coordinate where the word will start.
    * @param column - The column coordinate where the word will start.
    * @param is_horizontal - If true the word is written horizontally from the start, if not, it's written vertically 
    */
   public void addWord(String word, int row, int column, boolean is_horizontal){
      this.words.add(new GridWord(word,row,column,is_horizontal));
      this.adjustGridSize();
   }

   /**
    * Gets the grid size. 
    * @return The grid size
    */
   public GridSize getDimensions() {
      return this.gridSize;
   }

   /**
    * These words are used as extra words if the exist. 
    * @return
    */
   public List<String> getWordsUnableToPlace() {
      return this.wordsUnableToPlace;
   }

   /**
    * Returns the actullay generated description of the words.
    * @return
    */
   public List<GridWord> getCrossWord() {
      return this.words;
   }

   /**
    * Gets a string representation of the grid and the words placed. 
    * @return
    */
   public String getStringRepresentation(){

      GridSize dim = this.getDimensions();
      int N = dim.getRowCount();
      int M = dim.getColumnCount();

      String representation = "";

      for (int c = 0; c < M+1; c++){
         if (c == 0) System.err.print("   |");         
         else {
            int toprint = c-1;
            if (toprint < 10){
               representation = representation + " " + toprint + " |";
            }
            else {
               representation = representation + " " + toprint + "|";
            }            
         }
      }
      representation = representation + "\n";

      for (int c = 0; c < M+1; c++){
         representation = representation + "----";
      }
      representation = representation + "\n";
      
      for (int r = 0; r < N; r++){

         String row = " |";
         if (r < 10) row = " " + Integer.toString(r) + row;
         else row = Integer.toString(r) + row;

         for (int c = 0; c < M; c++){
            
            String display = "";

            for (GridWord gw: words){
               
               String disp = gw.getLetterAtPosition(r, c);
               if (!display.equals(disp)) {
                  //System.err.println("Existing display is '" + display + "'. Wanting to add '" + disp + "'");
                  display = display + disp;
               }

            }

            if (display == "") {
               //System.err.println("Adding the space");
               display = " ";
            }

            row = row + " " + display + " |";

         }

         representation = representation + row + "\n";

         for (int i = 0; i < row.length(); i++){
            representation = representation + "-";
         }
         representation = representation + "\n";

      }

      return representation;

   }

   public boolean placeWords(List<String> wordList){

      // Resetting all variables. 
      this.gridSize.adjustGrid(STARTING_NUMBER_OF_ROWS,STARTING_NUMBER_OF_COLUMNS);
      this.occupiedSpaces.clear();
      this.words.clear();
      this.wordsUnableToPlace.clear();

      // No words were passed. 
      if (wordList.size() == 0){
         this.adjustGridSize();
         return true;
      }

      // Special case of only one word. Nothing to do really. 
      if (wordList.size() == 1){
         this.words.add(new GridWord(wordList.get(0), 0,0, true));
         this.adjustGridSize();
         return true;
      }

      // WE need to sort the words by size. IN this way we attempt to place the longest words first, which are the hardest to place. 
      wordList = this.sortStringListByLength(wordList);
      System.err.println("Sorted input word list: " + wordList);
      // if (wordList.size() > 1) return true;

      // // The seed word will be the longest word. So we search for that. 
      // int longestWordIndex = 0;
      // for (int i = 1; i < wordList.size(); i++){
      //    if (wordList.get(i).length() > wordList.get(longestWordIndex).length()){
      //       longestWordIndex = i;
      //    }
      // }

      // To start we place the seed word. 
      String startingWord = wordList.remove(0);
      
      // Randomly select whether the word is horizontal or not. 
      boolean horizontal = ((int)(Math.random()*2) == 1);
      if (GEN_DEBUG) horizontal = true;
      
      // And compute the starting position. 
      GridPoint starting_pos = this.gridSize.getStartingPosition(startingWord.length(), horizontal);

      System.err.println("Starting Word: '" + startingWord + "'. At " + starting_pos.toString());
      this.words.add(new GridWord(startingWord, starting_pos.r, starting_pos.c, horizontal));
      this.fillOccupiedSpaces();
      
      // And now we try to fit all words. 
      int takeIndex = 0;

      while (wordList.size() > 0){

         // We get the next word.
         String nextWord = wordList.get(takeIndex);

         // We try to place it
         if (this.placeWord(nextWord)){

            // We succeded in placing it so now we take it out. 
            wordList.remove(takeIndex);
            takeIndex = 0;

         }
         else {
            // If possible we move on to the next one. 
            takeIndex++;
            if (takeIndex >= wordList.size()){
               // We've failed to place some words. 
               break;
            }
         }

      }
      
      for (String w: wordList){
         this.wordsUnableToPlace.add(w);
      }

      System.err.println("Finished Placing. Unable to place the following words: " + wordsUnableToPlace);

      // Once all words have been fit we need to adjust the word list. 
      this.adjustWordList();
      //this.adjustGridSize();

      return true;

   }   

   ////////////// Private Functions.

   /**
    * The placing algorithm puts the words in a very large grid.
    * Once all words have been place, the actual grid size can be computed. 
    */
   private void adjustWordList(){

      int min_c = this.STARTING_NUMBER_OF_COLUMNS;
      int min_r = this.STARTING_NUMBER_OF_ROWS;

      for (GridWord gw: this.words){         
         int start_c = gw.getColumn();
         int start_r = gw.getRow();
         if (start_c < min_c) min_c = start_c;
         if (start_r < min_r) min_r = start_r;
      }

      // Once these values are obtained all the data in the words are adjusted. 
      for (int i = 0; i < this.words.size(); i++){
         this.words.get(i).adjustPosition(min_r, min_c);
      }

      // And then we compute the actual grid size.
      this.adjustGridSize();
      this.occupiedSpaces.clear(); // We clear the occupied spaces as adjusting the grid size invalidates them. 

   }

   /*
    * Computes the grid size for an adjusted word list. 
    */
   private void adjustGridSize(){

      if (this.words.isEmpty()){
         this.gridSize.adjustGrid(0,0);
         return;
      }

      int max_c = 0;
      int max_r = 0;

      for (GridWord gw : this.words){

         int r = gw.getEndRow();
         int c = gw.getEndColumn();

         // System.err.println("Word: " + gw.getString() + " ends at " + r + ", "  + c);

         if (c > max_c) max_c = c;
         if (r > max_r) max_r = r;

      }

      // The value returned by get end row and column are last occupied row/column for the words.
      // We need to add one because if something ends in N the array is of size N+1. 
      this.gridSize.adjustGrid(max_r+1, max_c+1);
   }      

   /**
    * This is the main placing algorithm. It will attempt to place the word in the current context by trying out all possible locations that intersect with existing words.
    * It will stop at the first it finds and add it. If it succeeds it will return true, otherwise it will return false. 
    * @param word - The word to place
    * @return True if successfull, false otherwise. 
    */
   private boolean placeWord(String word){

      if (GEN_DEBUG) System.err.println("PLACE WORD: " + word);

      for (GridWord gw : this.words){
         
         if (GEN_DEBUG) System.err.println("- Intersecting with " + gw.getString());
         
         // We get all the possibilities.
         List<GridWord> possibilities = gw.listIntersectingGridWords(word);

         // We now test the validity of each possibility. 
         for (GridWord possibility: possibilities){

            if (GEN_DEBUG) System.err.println("-  Possibility: " + possibility.toString());

            if (canWordBePlaced(possibility)){
               
               // We got it. So we add it to the list. 
               this.words.add(possibility);
               
               // We recompute the used spaces.
               fillOccupiedSpaces();

               // And we return.
               return true;
            }

         }

      }

      return false;

   }

   /**
    * This function checks if a grid word can be placed at the position it's specified. We assume it has NOT been added to the list. 
    * @param word - The word to check.
    * @return True if it can be placed, false otherwise. 
    */
   private boolean canWordBePlaced(GridWord word){

      // First I need to check that any intersection along the word's path has the same letter as the word in that position. 
      List<GridPoint> gridLetters = word.toGridPointList();
      List<Integer> intersection = new ArrayList<>();
      List<Integer> spacesOKToBeOccupied = new ArrayList();

      for (GridPoint gp: gridLetters){

         if (GEN_DEBUG) System.err.println("-   L: " + gp.toString());

         for (GridWord gw: this.words){

            if (GEN_DEBUG) System.err.println("-     " + gw.toString());

            // We get the letter at each potential space might be occupied by a word. 
            // If there is a letter the space, then it must be the same. Other wise, this is not viable. 
            String letter = gw.getLetterAtPosition(gp.r,gp.c);            
            
            if (letter.isEmpty()) continue; // Nothing to intersect. 

            // If the letter exist, then we get the adjcent letters. 
            List<GridPoint> lettersAdjecent = gw.getLettersAdjacentToPosition(gp.r, gp.c);

            if (GEN_DEBUG) System.err.println("-      " + letter);

            if (!letter.equals("" + gp.character)){
               // There is a letter and is not the same letter. 
               if (GEN_DEBUG) System.err.println("-      Not the same");
               return false;
            }
            else {
               // This is an intersection. However it is only valid if the orientation of the words are not the same. 
               if (word.getHorizontal() != gw.getHorizontal()){
                  
                  if (GEN_DEBUG) System.err.println("-      Adding intersection");
                  intersection.add(this.gridSize.toIndex(gp));      

                  // Ok. When adding the intersection the places either above and below or to the left and right to the letter of the intersection might be occupied. 
                  // In that case they are OK to NOT be free. So we add the adjacent letters as exceptions.

                  for (GridPoint ladj : lettersAdjecent){
                     if (GEN_DEBUG) System.err.println("-       AdjLetters: " + ladj.toString());
                     spacesOKToBeOccupied.add(this.gridSize.toIndex(ladj));
                  }                  

               }               
               else {
                  if (GEN_DEBUG) System.err.println("-      Same orientation");
                  return false;
               }
            }

         }

      }

      // Ok. If we got here, no letter along the word path differs from the letter of the word. Now we check that the spaces around the word (except where there was an intersection)
      List<Integer> spacesThatNeedToBeFree = word.getSurroundingSpacesAsIndexes(this.gridSize);

      if (GEN_DEBUG) System.err.println("-  Free Space: " + spacesThatNeedToBeFree.toString());

      for (Integer i: spacesThatNeedToBeFree){

         int r = i/gridSize.getRowCount();
         int c = i - r*gridSize.getRowCount();
         if (GEN_DEBUG)  System.err.println("-   " + r + "," + c);

         if (spacesOKToBeOccupied.contains(i)){
            if (GEN_DEBUG) System.err.println("-   Ocuppied But OK @ " + i);
            continue;
         }

         // We don't check interceptions. 
         if (intersection.contains(i)){
            if (GEN_DEBUG) System.err.println("-   Intersection @ " + i);
            continue;
         }

         // We check the filled space. 
         if (this.occupiedSpaces.contains(i)){
            // One of the spaces that we need to be free is occupied. 
            if (GEN_DEBUG) System.err.println("-   Ocuppied @ " + i);
            return false;
         }

      }

      return true;

   }


   /**
    * Fills the occupied spaces as indexes based on the placed words. 
    */
   private void fillOccupiedSpaces(){
      this.occupiedSpaces.clear();      
      for (GridWord gw: this.words){
         List<GridPoint> list =  gw.toGridPointList();
         for (GridPoint gp: list){
            this.occupiedSpaces.add(this.gridSize.toIndex(gp));
         }         
      }

      if (GEN_DEBUG) System.err.println("Occupied Spaces\n" + this.getOccupiedSpaceRepresentation());

   }

   /**
    * Debug function that prints a simple reprsetation of occupied spaces in the grid. 
    * @return
    */
   private String getOccupiedSpaceRepresentation(){

      String rep = "";

      for (int i = 0; i < gridSize.getRowCount(); i++){
         for (int j = 0; j < gridSize.getColumnCount(); j++){
            int index = gridSize.toIndex(i,j);
            if (this.occupiedSpaces.contains(index)){
               rep = rep + "|X";
            }
            else {
               rep = rep + "| ";
            }
         }
         rep = rep + "|\n";
      }

      return rep;

   }

   /**
    * Sort a word list by size. 
    */
   
   private List<String> sortStringListByLength(List<String> list){

      boolean change = true;

      while (change) {
         change = false;
         for (int j = 0; j < list.size()-1; j++){
            if (list.get(j).length() < list.get(j+1).length()){
               change = true;
               String t = list.get(j);
               list.set(j, list.get(j+1));
               list.set(j+1,t);
            }
         }
      }

      return list;

   }
}


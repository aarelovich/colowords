package org.colowords;

import java.util.ArrayList;
import java.util.List;

public class GridSize {

   private int rows;
   private int columns;

   private final int STATE_INDEX_N = 0;
   private final int STATE_INDEX_M = 1;
   private final int STATE_SIZE    = 2;

   public GridSize (int N, int M){
      this.adjustGrid(N, M);
   }

   public void restoreFromStringState(String state){
      String[] parts = state.split("|");
      if (parts.length != STATE_SIZE){
         System.err.println("Failed restoring GridSize state from string '" + state + "'. Number of parts " + parts.length + " instead of " + STATE_SIZE);
         return;
      }

      this.rows = Integer.valueOf(parts[STATE_INDEX_N]);
      this.columns = Integer.valueOf(parts[STATE_INDEX_M]);
   }

   public int getRowCount() {
      return this.rows;
   }

   public int getColumnCount(){
      return this.columns;
   }

   public void adjustGrid(int N, int M){
      this.rows       = N;
      this.columns    = M;
   }

   public String getStoreString() {

      List<String> state = new ArrayList<>();
      for (int i = 0; i < STATE_SIZE; i++){
         state.add("");
      }

      state.set(STATE_INDEX_N,Integer.toString(rows));
      state.set(STATE_INDEX_M,Integer.toString(columns));

      return String.join("|",state);
   }
   public GridPoint getStartingPosition(int word_length, boolean horizontal){

      int row, column;

      if (horizontal){
         row = (this.rows - word_length)/2;
         column = this.columns/2;
      }
      else {
         column = (this.columns - word_length)/2;
         row = this.rows/2;
      }

      // System.err.println("WL: " + word_length + ". Size: " + this.toString() + ". Is Horizontal: " + Boolean.toString(horizontal));
      return new GridPoint(row,column);

   }

   public String toString() {
      return Integer.toString(this.rows) + "x" + Integer.toString(this.columns);
   }

   public int toIndex(GridPoint gp){
      return this.toIndex(gp.r,gp.c);
   }

   public int toIndex(int r, int c){
      int ans = r*this.columns + c;
      //System.err.println("TOINDEX: " + r + ", " + c + ". N (" + this.rows + "). = " + ans);
      return ans;
   }

} 

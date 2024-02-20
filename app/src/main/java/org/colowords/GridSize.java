package org.colowords;
public class GridSize {

   private int rows;
   private int columns;

   public GridSize (int N, int M){
      this.adjustGrid(N, M);
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
      return r*this.rows + c;
   }

} 

package org.colowords;
public class GridPoint {
   int r;
   int c;
   Character character;

   public GridPoint(){
      r = 0; 
      c = 0;
      character = Character.MIN_VALUE;
   }

   public GridPoint(int r, int c){
      this.r = r;
      this.c = c;
      character = Character.MIN_VALUE;
   }

   public GridPoint(int r, int c, char ch){
      this.r = r;
      this.c = c;
      character = ch;
   }

   public boolean isRowColumn(int r, int c){
      return (this.r == r && this.c == c);
   }

   public boolean isValid(){
      return (this.r >= 0) && (this.c >= 0);
   }

   public String toString(){
      String ans = "(" + Integer.toString(this.r) + "," + Integer.toString(this.c) + ")";
      if (this.character != Character.MIN_VALUE) ans = ans + "[" + this.character + "]";
      return ans;
   }

} 

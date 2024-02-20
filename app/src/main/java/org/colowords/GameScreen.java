package org.colowords;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GameScreen extends View {

    private int WIDTH;
    private int HEIGHT;
    private LetterWheel letterWheel;
    private LetterGrid letterGrid;
    private CurrentWord currentWord;
    private Rect letterGridGeometry;

    public GameScreen(Context context, int width, int height) {
        super(context);
        this.WIDTH = width;
        this.HEIGHT = height;

        // With these values we can now do the math for all elements.
        int x = width/2;
        int d = height/3;
        int bottomMargin = (int)(0.03*height);
        int y = height - d - bottomMargin + d/2;
        this.letterWheel = new LetterWheel(x,y,d);

        // Now the current word
        y = y - d/2 -  (int)(0.02f*height);
        this.currentWord = new CurrentWord(x,y,Letter.GetWheelLetterSize());


        // And the letter grid.
        int w = (int)(width*0.9f);
        int h = (int)(height*0.4f);
        x = (width - w)/2;
        y = (int)(0.15f*height);

        this.letterGridGeometry = new Rect(x,y,x+w,y+h); // The geometry is necessary with every configuration.
        this.letterGrid = new LetterGrid();

    }

    public void setNewCrossWord(CrossWordGrid cwg){
        this.letterGrid.configureGrid(this.letterGridGeometry,cwg.getDimensions(),cwg.getCrossWord());
    }

    public void setLetters(ArrayList<String> letters){
        this.letterWheel.setLetters(letters);
    }

    public void fingerDown(int x, int y){
        String word = this.letterWheel.fingerDown(x,y);

        if (this.letterWheel.isShuffleButtonPressed()){
            this.invalidate();
            return;
        }

        if (word != "") {
            this.invalidate();
            //System.err.println("[DBUG] Word Start '" + word + "'");
            this.currentWord.setWord(word);
        }
    }

    public void fingerMoved(int x, int y){
        String word = this.letterWheel.fingerMove(x,y);
        if (word != ""){
            this.invalidate();
            //System.err.println("[DBUG] Current Word: '" + word + "'");
            this.currentWord.setWord(word);
        }
    }

    public void fingerUp(int x, int y){
        String word = this.letterWheel.fingerUp();
        this.currentWord.clear();
        this.invalidate();
        //System.err.println("[DBUG] Word Formed: '" + word + "'");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // We paint the background color
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Utils.BKG_COLOR);
        canvas.drawPaint(paint);

        // We draw the letter wheel.
        this.letterWheel.render(canvas);
        this.letterGrid.render(canvas);
        this.currentWord.render(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        //int index = event.getActionIndex();
        int action = event.getActionMasked();
        //int pointerId = event.getPointerId(index);

        int x = (int)event.getX();
        int y = (int)event.getY();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                //System.err.println("[DBUG] Down on (" + x + "," + y + ")");
                this.fingerDown(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                //System.err.println("[DBUG] Move on (" + x + "," + y + ")");
                this.fingerMoved(x,y);
                break;
            case MotionEvent.ACTION_UP:
                //System.err.println("[DBUG] Up on (" + x + "," + y + ")");
                this.fingerUp(x,y);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

}
package org.colowords;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameScreen extends View implements Utils.AnimationInterface {

    private LetterWheel letterWheel;
    private LetterGrid letterGrid;
    private CurrentWord currentWord;
    private ExtraWordIndicator extraIndicator;
    private CircleDropDown difficulty;
    private CircleDropDown langSelector;
    private Button newGameButton;
    private Score scoreIndicator;
    private SettingsButton settingsButton;
    private SettingsScreen settings;
    private Rect letterGridGeometry;
    private Timer animationTimer;
    private List<Integer> animationRequests;
    private NewGameListener newGameListener;
    private Banner banner;
    private RectF highScoreTextRect;
    private final String appStateFile = "appstate.txt";

    TimerTask timerTaskObj = new TimerTask() {
        public void run() {
            if (!animationRequests.isEmpty()){
                invalidate();
            }
        }
    };

    public interface NewGameListener {
        void newGame();
    }

    public GameScreen(Context context, int width, int height, NewGameListener listener)  {
        super(context);

        this.newGameListener = listener;
        this.animationRequests = new ArrayList<>();

        // Computing the letter grid bounding geometry.
        int w = (int)(width*0.9f);
        int h = (int)(height*0.4f);
        int x = (width - w)/2;
        int y = (int)(0.10f*height);

        this.letterGridGeometry = new Rect(x,y,x+w,y+h); // The geometry is necessary with every configuration.
        this.letterGrid = new LetterGrid();

        // Now the current word
        int verticalMargin = (int)(height*0.01f);
        y = y + h + verticalMargin;
        h = (int)(height*0.05f);
        w = (int)(0.6f*width);
        x = (width - w)/2;
        this.currentWord = new CurrentWord(x,y,w,h);


        // With these values we can now do the math for all elements.
        x = width/2;
        int d = (int)(height*0.3);
        y = y + h + d/2 + verticalMargin;
        this.letterWheel = new LetterWheel(x,y,d);

        // The extra indicator
        d = (int)(0.2*width);
        int margin = (int)(0.05*width);
        x = width - d/2 - margin;
        y = height - d/2 - margin;
        this.extraIndicator = new ExtraWordIndicator(x,y,d,this);
        this.extraIndicator.setNumber(0,1);

        w = (int)(0.3f*width);
        h = (int)(0.1f*height);;
        int t = height - h - (int)(0.02f*height);
        int l = margin;
        this.scoreIndicator = new Score(t,l,w,h,this);

        // WE get the animation timer running.
        this.animationTimer = new Timer();
        animationTimer.schedule(timerTaskObj, 0, Utils.ANIMATION_TICK_LENGTH);

        // Adding the difficulty selector
        d = (int)(width*0.13);
        x = width - d/2  - (int)(width*0.02);
        y = (int)(height*0.05);
        this.difficulty = new CircleDropDown(x,y,d);
        this.difficulty.addOptions("5");
        this.difficulty.addOptions("6");
        this.difficulty.addOptions("7");
        this.difficulty.setCurrentOption(Preferences.GetPreference(getContext(),Preferences.KEY_DIFFICULTY));

        float topMargin = y - d/2;

        // Adding the language selection.
        d = (int)(width*0.13);
        x = (int)(width*0.02) + d/2;
        y = (int)(height*0.05);
        this.langSelector = new CircleDropDown(x,y,d);
        this.langSelector.addOptions("EN");
        this.langSelector.addOptions("ES");
        this.langSelector.setCurrentOption(Preferences.GetPreference(getContext(),Preferences.KEY_LANGUAGE));

        // The new game button.
        w = (int)(width*0.4f);
        x = (width - w)/2;
        y = y - d/2; // We just want however much space there is from the top, not to the middle.
        h = d;       // We want it to be as large as the options to the side.
        this.newGameButton = new Button(y,x,w,h,"NEW GAME");
        this.newGameButton.setVisible(false);

        // The banner.
        this.banner = new Banner(0,100,width,(int)(h*1.2f),this);

        // The high score rect.
        w = (int)(width*0.6);
        x = (width - w)/2;
        this.highScoreTextRect = new RectF(x,topMargin,x + w,topMargin + d);

        // The settings button.
        x = width/2;
        h = (int)(0.15*width);
        y = height - h/2 - margin;
        w = h;
        this.settingsButton = new SettingsButton(x,y,w,h);

        // Configuring the settings screen.
        this.settings = new SettingsScreen(context,width,height);

    }

    public void setNewCrossWord(CrossWordGrid cwg, List<String> extras){
        this.letterGrid.configureGrid(this.letterGridGeometry,cwg.getDimensions(),cwg.getCrossWord(),extras);
        this.extraIndicator.setNumber(extras.size(),extras.size());
        this.scoreIndicator.updateValues(0,1);
        this.newGameButton.setVisible(false);
        this.storeState();
    }

    public void setLetters(ArrayList<String> letters){
        this.letterWheel.setLetters(letters);
    }

    public void fingerDown(int x, int y){

        if (this.settings.isBeingShown()){
            if (this.settings.fingerDown(x,y)) this.invalidate();
            return;
        }

        if (this.settingsButton.fingerDown(x,y)){
            this.invalidate();
            return;
        }

        if (this.newGameButton.fingerDown(x,y)){
            // We are pressing the button.
            this.invalidate();
            return;
        }

        if (this.difficulty.fingerDown(x,y)){
            this.invalidate();
            return;
        }

        if (this.langSelector.fingerDown(x,y)){
            this.invalidate();
            return;
        }

        String word = this.letterWheel.fingerDown(x,y);

        // We need to check finger down in any of the letters.
        if (this.letterGrid.fingerDown(x,y)){
            this.invalidate();
            return;
        }

        if (this.extraIndicator.fingerUp(x,y)){
            if (letterGrid.getExtraWordsFound().isEmpty()) return;
            ExtraDialog dialog = new ExtraDialog(getContext());
            dialog.show();
            dialog.setWordList(letterGrid.getExtraWordsFound());
            return;
        }


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

        if (this.settings.isBeingShown()){
            if (this.settings.fingerMove(x,y)) this.invalidate();
            return;
        }

        if (this.difficulty.fingerMove(x,y)){
            this.invalidate();
            return;
        }

        if (this.langSelector.fingerMove(x,y)){
            this.invalidate();
            return;
        }

        String word = this.letterWheel.fingerMove(x,y);
        if (word != ""){
            this.invalidate();
            //System.err.println("[DBUG] Current Word: '" + word + "'");
            this.currentWord.setWord(word);
        }
    }

    public void fingerUp(int x, int y){

        if (this.settings.isBeingShown()){
            if (this.settings.fingerUp(x,y)) this.invalidate();

            // We need to check if a request was requested.
            if (this.settings.getPreferenceDeleteRequest()){
                ConfirmationDialog.Show(getContext(), "Reset High Score", "Selecting YES will set the highest \nDo you want to proceed", new ConfirmationDialog.OnOptionSelectedListener() {
                    @Override
                    public void onYesSelected() {
                        Preferences.Save(getContext(),Preferences.KEY_MAX_SCORE,"0");
                    }

                    @Override
                    public void onNoSelected() {
                        // We basically do nothing.
                    }
                });
            }

            return;
        }

        if (this.settingsButton.fingerUp(x,y)){
            this.settings.show();
            this.invalidate();
            return;
        }

        if (this.newGameButton.fingerUp(x,y)){
            // New game button was pressed.
            // Since the button will only be visible for starting a new game, no confirmation dialog is neeeded.
            newGameListener.newGame();
            this.invalidate();
            return;
        }

        int selection = this.difficulty.fingerUp(x,y);
        boolean require_confirmation = false;
        if (selection != -1){
            // We store the new value.
            Preferences.Save(getContext(),Preferences.KEY_DIFFICULTY,this.difficulty.getCurrentSelection());
            require_confirmation = true;
        }

        selection = this.langSelector.fingerUp(x,y);
        if (selection != -1){
            Preferences.Save(getContext(),Preferences.KEY_LANGUAGE,this.langSelector.getCurrentSelection());
            require_confirmation = true;
        }

        if (require_confirmation){
            ConfirmationDialog.Show(getContext(), "Start a new game", "Do you want to start a new game?\nChoosing 'NO' will start the next game with the selected options", new ConfirmationDialog.OnOptionSelectedListener() {
                @Override
                public void onYesSelected() {
                    newGameListener.newGame();
                }

                @Override
                public void onNoSelected() {
                    // We basically do nothing.
                }
            });
            this.invalidate();
            return;
        }

        int code = this.letterGrid.fingerUp();
        if (code == LetterGrid.PRESS_ACTION_HINT){
          // A letter was hinted.
          this.storeState();

          // We update the score and multiplier.
          List<Integer> score_data = this.letterGrid.getScoreAndMultiplier();
          this.scoreIndicator.updateValues(score_data.get(0),score_data.get(1));

          this.invalidate();
          return;
        }
        else if (code == LetterGrid.PRESS_ACTION_DEFINITION){
            DefinitionDialog dd = new DefinitionDialog(getContext());
            dd.show();
            List<GridWord> words = this.letterGrid.getDefinitionRequests();
            dd.setDefinitionsFromGridWord(words);
            this.invalidate(); // Turn off the light.
            return;
        }

        if (this.extraIndicator.fingerUp(x,y)){
            //this.invalidate();
            return;
        }

        // We check the letter whee.
        System.err.println("Checking letter wheel");
        String word = this.letterWheel.fingerUp();
        this.currentWord.clear();
        if (word.isEmpty()) {
            this.invalidate();
            return;
        }

        // We now check if the word is in the puzzle.
        code = this.letterGrid.isItAWord(word);
        if ((code == LetterGrid.WORD_EXTRA) || (code == LetterGrid.WORD_CROSS) || (code == LetterGrid.WORD_CROSS_NO_SCORE)){

            if (code == LetterGrid.WORD_EXTRA) this.banner.showBannerMessage(Banner.MESSAGE_TYPE_EXTRA_FOUND);
            else if (code == LetterGrid.WORD_CROSS) this.banner.showBannerMessage(Banner.MESSAGE_TYPE_WORD_FOUND);
            else this.banner.showBannerMessage(Banner.MESSAGE_TYPE_NEW_ZERO_POINT_WORD);;


            if (code == LetterGrid.WORD_EXTRA){
                this.extraIndicator.decreaseNumber();
            }

            this.storeState();

            // We check if we are done
            if (this.letterGrid.isPuzzleDone()){
                System.err.println("Puzzle Finished");
                List<Integer> score_and_mult = this.letterGrid.getScoreAndMultiplier();
                Preferences.Save(getContext(),Preferences.KEY_MAX_SCORE,score_and_mult.get(0).toString());
                this.letterGrid.markAllExtrasAsFound();
                this.newGameButton.setVisible(true);
            }
        }

        // We update the score and multiplier.
        List<Integer> score_data = this.letterGrid.getScoreAndMultiplier();
        this.scoreIndicator.updateValues(score_data.get(0),score_data.get(1));

        this.invalidate();
        //System.err.println("[DBUG] Word Formed: '" + word + "'");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.settings.isBeingShown()){
            this.settings.render(canvas);
            return;
        }

        // We paint the background color
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Utils.BG_100);
        canvas.drawPaint(paint);

        // We draw the letter wheel.
        this.letterWheel.render(canvas);
        this.letterGrid.render(canvas);
        this.currentWord.render(canvas);
        this.extraIndicator.render(canvas);
        this.scoreIndicator.render(canvas);
        this.difficulty.render(canvas);
        this.langSelector.render(canvas);
        this.newGameButton.render(canvas);
        this.renderScore(canvas);
        this.banner.render(canvas); // Banner must be on top.
        this.settingsButton.render(canvas);

    }

    private void renderScore(Canvas canvas){

        // We render the score as long as the new game button is not there.
        if (this.newGameButton.getIsVisible()) return;

        String text = "HIGH SCORE: " + Preferences.GetPreference(getContext(),Preferences.KEY_MAX_SCORE);
        Paint p = new Paint();
        //Typeface font = Typeface.create("Mono",Typeface.BOLD);
        p.setTextAlign(Paint.Align.CENTER);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Utils.TEXT_200);
        //p.setTypeface(font);
        p.setTypeface(Utils.GetElementTypeFace());
        p.setTextSize(Utils.GetTextSizeToFitRect(text,this.highScoreTextRect.width(),this.highScoreTextRect.height(),p));
        float baseline = Utils.GetTextBaseLine(text,p,(int)this.highScoreTextRect.centerY());

        // The fill.
        canvas.drawText(text,this.highScoreTextRect.centerX(),baseline,p);

        // Then the outline.
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Utils.ACCENT_100);
        p.setStrokeWidth(0.03f*this.highScoreTextRect.height());
        canvas.drawText(text,this.highScoreTextRect.centerX(),baseline,p);


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

    public void storeState() {
        String state = letterGrid.saveState(this.letterWheel.getLetters());
        try {
            FileOutputStream fos = getContext().openFileOutput(this.appStateFile,Context.MODE_PRIVATE);
            fos.write(state.getBytes(StandardCharsets.UTF_8));
            fos.close();
        }
        catch (Exception e){
            System.err.println("Failed to write the state. Reason: " + e.getMessage());
        }
        System.err.println("SAVED STATE");
    }

    public boolean reloadState(){

        String state = "";

        try {
            FileInputStream fis = getContext().openFileInput(this.appStateFile);

            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            state = reader.readLine();
            System.err.println("Loaded STATE: " + state);

        }
        catch (Exception e){
            System.err.println("Unable to reload state Reason: " + e.getMessage());
            return false;
        }

        if (state.isEmpty()) return false;

        // Only request a new game when we are done.
        this.newGameButton.setVisible(false);

        // Now we reload the state.
        ArrayList<String> forWheel = this.letterGrid.restoreFromState(this.letterGridGeometry,state);

        if (forWheel.isEmpty()){
            System.err.println("Unable to load the restore state. Generating a new game");
            this.newGameButton.setVisible(true);
            return false;
        }

        this.letterWheel.setLetters(forWheel);

        int total_extra = this.letterGrid.getExtraWords().size();
        int total_extra_found = this.letterGrid.getExtraWordsFound().size();
        int remaining = total_extra - total_extra_found;

        this.extraIndicator.setNumber(remaining,total_extra);

        // We update the score and multiplier.
        List<Integer> score_data = this.letterGrid.getScoreAndMultiplier();
        this.scoreIndicator.updateValues(score_data.get(0),score_data.get(1));

        // Everything loaded correctly, but puzzle is finished. We need a new one.
        if (this.letterGrid.isPuzzleDone()) {
            System.err.println("Puzzle is finished. Showing the new game button");
            this.newGameButton.setVisible(true);
            return false;
        }

        return true;
    }

    @Override
    public void startAnimation(int id) {
        if (!this.animationRequests.contains(id)){
            this.animationRequests.add(id);
        }
    }

    @Override
    public void stopAnimation(int id){
        int index = this.animationRequests.indexOf(id);
        if (index != -1) this.animationRequests.remove(index);
    }

}
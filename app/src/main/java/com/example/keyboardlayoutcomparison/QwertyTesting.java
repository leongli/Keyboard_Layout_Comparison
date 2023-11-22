package com.example.keyboardlayoutcomparison;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QwertyTesting extends Activity {

    TextView textToType, timeLabel;
    EditText userInput;
    ArrayList<String>testPhaseList;
    //testing set => 1=QWERTY, 2=DVORAK, 3=MESSAGEASE
    int questionPointer, testingSet;
    UserInputListener userTextChangedListener;
    Button nextPhase;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qwerty_testing);
    //set references from view
        textToType = findViewById(R.id.text_to_enter);
        userInput = findViewById(R.id.userInput);
        timeLabel = findViewById(R.id.time);
    //button in-between phases, hidden for now
        nextPhase = findViewById(R.id.next_phase_button);
        nextPhase.setVisibility(View.GONE);
    //generate phase to type
        testPhaseList = generatePhraseSet();
        textToType.setText(testPhaseList.get(questionPointer));
    //set other environment variables
        testingSet=1;
        //initialize stats for each keyboard layout wpm, error rate

    //create timer

    //listener for each character typed, object class defined at very bottom of this activity
        userTextChangedListener = new UserInputListener();
        userInput.addTextChangedListener(userTextChangedListener);
    }

    public void onRestoreInstance(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);


    }

    public void onSaveInstanceState(Bundle savedInstanceState){
        //add to savedInstanceState

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Method to generate Phrases to be used on trials. This method accesses a text file in Res>Raw
     * and randomly selects phrases and stores them in an arrayList to be returned
     * @return ArrayList of phrases to be used in trials
     */
    protected ArrayList<String> generatePhraseSet(){
        return null;
    }

    /**
     * Method to calculate Words Per min by dividing the words in the list of phrases given to user
     * to enter, by the time taken to type those set of phrases. Returns a float number of calculated WPM
     * @param list list of phrases used in the testing set, used to get word count
     * @param millisTime time taken in typing the testing set of phrases
     * @return wordsPerMin
     */
    protected float calculateWPM(ArrayList<String> list, long millisTime){
        float wordsPerMin=0f;
        return wordsPerMin;
    }

    /**
     * Method to calculate error rate by dividing the number of characters typed taken from
     * length of characters in given list of phrases to
     * @param list list of phrases used in the testing set, used to get character count
     * @param numErrors time taken in typing the testing set of phrases
     * @return errorRate
     */
    protected float calculateErrorRate(ArrayList<String> list, int numErrors){
        float errorRate=0f;
        return errorRate;
    }

    /**
     * This method is called when moving to the next set of phrases to be entered with another keyboard
     * layout. This method handles tracks the start & end times of each phrase, and tracking and
     * calculating WPM and error rate at the end of each phrase.
     */
    protected void changeKeyboard(){

    }

    /**
     * This method manages the transition between phases, by removing in-between phases display and
     * and reset current phase's parameters
     * @param view view object for transition
     */
    protected void onClickNextPhrase(View view){

    }

    /**
     * this method is called at the end of the final phase, right before we move onto the next activity
     * of displaying results. it captures all the statistics and saves it into bundles before transporting
     * to next activity.
     */
    protected void getResults(){

    }

    private class UserInputListener implements TextWatcher{
        @Override
        public void afterTextChanged(Editable e){

        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after){

        }



    }



}

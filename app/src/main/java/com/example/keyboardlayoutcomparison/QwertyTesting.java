package com.example.keyboardlayoutcomparison;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

public class QwertyTesting extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qwerty_testing);




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

    }

    /**
     * Method to calculate Words Per min by dividing the words in the list of phrases given to user
     * to enter, by the time taken to type those set of phrases. Returns a float number of calculated WPM
     * @param list
     * @param millisTime
     * @return wordsPerMin
     */
    protected float calculateWPM(ArrayList<String> list, long millisTime){
        float wordsPerMin=0f;
        return wordsPerMin;
    }

    /**
     * Method to calculate error rate by dividing the number of characters typed taken from
     * length of characters in given list of phrases to
     * @param list
     * @param numErrors
     * @return
     */
    protected float calculateErrorRate(ArrayList<String> list, int numErrors){

    }



}

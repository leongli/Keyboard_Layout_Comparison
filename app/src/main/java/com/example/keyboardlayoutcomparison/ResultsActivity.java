package com.example.keyboardlayoutcomparison;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultsActivity extends Activity {
    //State name strings for getting from saved instance state
    private final static String QWERTY_KEYBOARD_WPM="qwerty_keyboard_wpm";
    private final static String QWERTY_KEYBOARD_ERROR_RATE="qwerty_keyboard_error_rate";
    private final static String DVORAK_KEYBOARD_WPM="dvorak_keyboard_wpm";
    private final static String DVORAK_KEYBOARD_ERROR_RATE="dvorak_keyboard_error_rate";
    private final static String MESSAGEASE_KEYBOARD_WPM="messagease_keyboard_wpm";
    private final static String MESSAGEASE_KEYBOARD_ERROR_RATE="messagease_keyboard_error_rate";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //set content view
        setContentView(R.layout.results);
        //get text view references in results.xml
        TextView fastest,mostAccurate, typingSpeedQ, accuracyQ, typingSpeedD, accuracyD, typingSpeedM, accuracyM;
        fastest=findViewById(R.id.fastest);
        mostAccurate=findViewById(R.id.mostAccurate);
        typingSpeedQ=findViewById(R.id.TypingSpeedQ);
        accuracyQ= findViewById(R.id.AccuracyQ);
        typingSpeedD=findViewById(R.id.TypingSpeedD);
        accuracyD= findViewById(R.id.AccuracyD);
        typingSpeedM=findViewById(R.id.TypingSpeedM);
        accuracyM= findViewById(R.id.AccuracyM);
        //initialize variables
        float speedQ = 0f;
        float accQ =0f;
        float speedD = 0f;
        float accD =0f;
        float speedM = 0f;
        float accM =0f;
        String fastestKeyboard="";
        String mostAccurateKeyboard="";
        //populate variables
        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        if(extras!=null){
            speedQ = extras.getFloat(QWERTY_KEYBOARD_WPM);
            accQ=extras.getFloat(QWERTY_KEYBOARD_ERROR_RATE);
            speedD = extras.getFloat(DVORAK_KEYBOARD_WPM);
            accD=extras.getFloat(DVORAK_KEYBOARD_ERROR_RATE);
            speedM = extras.getFloat(MESSAGEASE_KEYBOARD_WPM);
            accM=extras.getFloat(MESSAGEASE_KEYBOARD_ERROR_RATE);
        }
        //set results for 3 keyboard layouts
        typingSpeedQ.setText(String.format("%.1f", speedQ)+"Words Per Minute");
        accuracyQ.setText(String.format("%.1f", accQ)+"%");
        typingSpeedD.setText(String.format("%.1f", speedD)+"Words Per Minute");
        accuracyD.setText(String.format("%.1f", accD)+"%");
        typingSpeedM.setText(String.format("%.1f", speedM)+"Words Per Minute");
        accuracyM.setText(String.format("%.1f", accM)+"%");
        //FINDING OUT WINNERS
        String [] keyboards = {"Qwerty", "Dvorak", "MessagEase"};
        ArrayList<String> maxCategoryWinners = new ArrayList<>();
        float [] wpmValues = {speedQ, speedD, speedM};
        float[] accuracyValues ={accQ, accD, accM};
        float maxWpm = Math.max(speedQ,( Math.max(speedD, speedM)));
        float maxAcc = Math.max(accQ, (Math.max(accD, accM)));
        //find fastest speed
        for(int i=0; i<wpmValues.length; i++){
            if(wpmValues[i]==maxWpm){
                maxCategoryWinners.add(keyboards[i]);
            }
        }
        // use arraylist to find if any ties
        int winnerCounter = maxCategoryWinners.size();
        for(String s: maxCategoryWinners){
            if(winnerCounter>1){
                fastestKeyboard+= s +" tied with";
                winnerCounter--;
            }else{
                fastestKeyboard+=s;
            }
        }
        //reset & solve most accurate
        maxCategoryWinners.clear();
        for(int i=0; i<accuracyValues.length; i++){
            if(accuracyValues[i]==maxAcc){
                maxCategoryWinners.add(keyboards[i]);
            }
        }
        winnerCounter = maxCategoryWinners.size();
        for(String s: maxCategoryWinners){
            if(winnerCounter>1){
                mostAccurateKeyboard+= s +" tied with";
                winnerCounter--;
            }else{
                mostAccurateKeyboard+=s;
            }
        }
        //display overall winners
        fastest.setText(fastestKeyboard);
        mostAccurate.setText(mostAccurateKeyboard);

    }

}

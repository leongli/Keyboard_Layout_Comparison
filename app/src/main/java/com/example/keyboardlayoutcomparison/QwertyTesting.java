package com.example.keyboardlayoutcomparison;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class QwertyTesting extends Activity {

    private final static String MYDEBUG = "MYDEBUG";
    private final static int NUMBER_OF_TRIAL_PHRASES=5;
    private final static int PHRASES_SOURCE = R.raw.phrases;

    TextView textToType, timeLabel;
    EditText userInput;
    ArrayList<String>testPhraseList, qwertyPhraseList, dvorakPhraseList, messageasePhraseList;
    int currentPhraseNumber,currentKeyboard;
    UserInputListener userTextChangedListener;
    Button nextPhase;
    float qwertyWPM, dvorakWPM, messageaseWPM, qwertyErrorRate, dvorakErrorRate, messageaseErrorRate;
    private final static int QWERTY=0;
    private final static int DVORAK=1;
    private final static int MESSAGEASE=2;

    int qwertyErrors, dvorakErrors, messageaseErrors, currentKeyboardErrors;
    long currentKeyboardTime,currentKeyboardStartTime, qwertyStartTime, qwertyStopTime, dvorakStartTime, dvorakStopTime, messageaseStartTime, messageaseStopTime;
    CountDownTimer countDownTimer;

    //State String Name Variables to be used in Bundle
    private final static String PHRASES_LIST="phrases_list";
    private final static String PHRASES_LIST_CHARS="phrases_list_chars";
    private final static String CURRENT_KEYBOARD="current_keyboard";
    private final static String CURRENT_KEYBOARD_PHRASE_NUMBER="current_keyboard_phrase_number";
    private final static String CURRENT_KEYBOARD_START_TIME="current_keyboard_start_time";
    private final static String CURRENT_KEYBOARD_ERRORS="current_keyboard_errors";

    private final static String QWERTY_KEYBOARD_START_TIME="qwerty_keyboard_start_time";
    private final static String QWERTY_KEYBOARD_STOP_TIME="qwerty_keyboard_stop_time";
    private final static String QWERTY_KEYBOARD_ERRORS="qwerty_keyboard_errors";
    private final static String QWERTY_KEYBOARD_PHRASE_LIST="qwerty_keyboard_phrase_list";

    private final static String DVORAK_KEYBOARD_START_TIME="dvorak_keyboard_start_time";
    private final static String DVORAK_KEYBOARD_STOP_TIME="dvorak_keyboard_stop_time";
    private final static String DVORAK_KEYBOARD_ERRORS="dvorak_keyboard_errors";
    private final static String DVORAK_KEYBOARD_PHRASE_LIST="dvorak_keyboard_phrase_list";

    private final static String MESSAGEASE_KEYBOARD_START_TIME="messagease_keyboard_start_time";
    private final static String MESSAGEASE_KEYBOARD_STOP_TIME="messagease_keyboard_stop_time";
    private final static String MESSAGEASE_KEYBOARD_ERRORS="messagease_keyboard_errors";
    private final static String MESSAGEASE_KEYBOARD_PHRASE_LIST="messagease_keyboard_phrase_list";



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
        testPhraseList = generatePhraseSet();
        textToType.setText(testPhraseList.get(currentPhraseNumber));
    //set other environment variables
        currentKeyboard=QWERTY;
    //initialize stats for each keyboard layout wpm, error rate
        qwertyWPM=0f;
        qwertyErrorRate=0f;
        qwertyStopTime=0;
        qwertyStartTime=0;
        qwertyPhraseList=new ArrayList<>();
        qwertyErrors=0;

        dvorakWPM=0f;
        dvorakErrorRate=0f;
        dvorakStartTime=0;
        dvorakStopTime=0;
        dvorakPhraseList=new ArrayList<>();
        dvorakErrors=0;

        messageaseWPM=0f;
        messageaseErrorRate=0f;
        messageaseStartTime=0;
        messageaseStopTime=0;
        messageasePhraseList= new ArrayList<>();
        messageaseErrors=0;


    //create timer
        currentKeyboardTime =0;
        countDownTimer= new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //current Keyboard time is just a custom incremented timer, not a system millis
                currentKeyboardTime++;
                long mills= currentKeyboardTime;
                int seconds= (int)(mills/60);
                int minutes= (int) (seconds/60);
                String time = String.format("%02d:%02d", seconds, mills);
                timeLabel.setText(time);
                Log.i("TIME", currentKeyboardTime +" \t"+time);
            }

            @Override
            public void onFinish() {
                //
            }
        };


    //listener for each character typed, object class defined at very bottom of this activity
        userTextChangedListener = new UserInputListener();
        userInput.addTextChangedListener(userTextChangedListener);
    }

    public void onRestoreInstance(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        testPhraseList = savedInstanceState.getStringArrayList(PHRASES_LIST);

        currentPhraseNumber = savedInstanceState.getInt(CURRENT_KEYBOARD_PHRASE_NUMBER);
        currentKeyboardStartTime = savedInstanceState.getLong(CURRENT_KEYBOARD_START_TIME);
        currentKeyboardErrors=savedInstanceState.getInt(CURRENT_KEYBOARD_ERRORS);
        currentKeyboard=savedInstanceState.getInt(CURRENT_KEYBOARD);

        qwertyStartTime = savedInstanceState.getLong(QWERTY_KEYBOARD_START_TIME);
        qwertyStopTime=savedInstanceState.getLong(QWERTY_KEYBOARD_STOP_TIME);
        qwertyErrors=savedInstanceState.getInt(QWERTY_KEYBOARD_ERRORS);
        qwertyPhraseList = savedInstanceState.getStringArrayList(QWERTY_KEYBOARD_PHRASE_LIST);

        dvorakStartTime = savedInstanceState.getLong(DVORAK_KEYBOARD_START_TIME);
        dvorakStopTime=savedInstanceState.getLong(DVORAK_KEYBOARD_STOP_TIME);
        dvorakErrors=savedInstanceState.getInt(DVORAK_KEYBOARD_ERRORS);
        dvorakPhraseList = savedInstanceState.getStringArrayList(DVORAK_KEYBOARD_PHRASE_LIST);

        messageaseStartTime = savedInstanceState.getLong(MESSAGEASE_KEYBOARD_START_TIME);
        messageaseStopTime=savedInstanceState.getLong(MESSAGEASE_KEYBOARD_STOP_TIME);
        messageaseErrors=savedInstanceState.getInt(MESSAGEASE_KEYBOARD_ERRORS);
        messageasePhraseList = savedInstanceState.getStringArrayList(MESSAGEASE_KEYBOARD_PHRASE_LIST);
    }

    public void onSaveInstanceState(Bundle savedInstanceState){
        //add to savedInstanceState
        savedInstanceState.putStringArrayList(PHRASES_LIST,testPhraseList);

        savedInstanceState.putInt(CURRENT_KEYBOARD,currentKeyboard);
        savedInstanceState.putInt(CURRENT_KEYBOARD_ERRORS,currentKeyboardErrors);
        savedInstanceState.putInt(CURRENT_KEYBOARD_PHRASE_NUMBER,currentPhraseNumber);
        savedInstanceState.putLong(CURRENT_KEYBOARD_START_TIME,currentKeyboardStartTime);

        savedInstanceState.putLong(QWERTY_KEYBOARD_START_TIME,qwertyStartTime);
        savedInstanceState.putLong(QWERTY_KEYBOARD_STOP_TIME, qwertyStopTime);
        savedInstanceState.putInt(QWERTY_KEYBOARD_ERRORS,qwertyErrors);
        savedInstanceState.putStringArrayList(QWERTY_KEYBOARD_PHRASE_LIST,qwertyPhraseList);

        savedInstanceState.putLong(DVORAK_KEYBOARD_START_TIME,dvorakStartTime);
        savedInstanceState.putLong(DVORAK_KEYBOARD_STOP_TIME, dvorakStopTime);
        savedInstanceState.putInt(DVORAK_KEYBOARD_ERRORS,dvorakErrors);
        savedInstanceState.putStringArrayList(DVORAK_KEYBOARD_PHRASE_LIST,dvorakPhraseList);

        savedInstanceState.putLong(MESSAGEASE_KEYBOARD_START_TIME,messageaseStartTime);
        savedInstanceState.putLong(MESSAGEASE_KEYBOARD_STOP_TIME, messageaseStopTime);
        savedInstanceState.putInt(MESSAGEASE_KEYBOARD_ERRORS,messageaseErrors);
        savedInstanceState.putStringArrayList(MESSAGEASE_KEYBOARD_PHRASE_LIST,messageasePhraseList);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Method to generate Phrases to be used on trials. This method accesses a text file in Res>Raw
     * and randomly selects phrases and stores them in an arrayList to be returned
     * Phrases in this text file are generated using https://www.thewordfinder.com/random-sentence-generator/
     * and formatted to lowercase and remove special characters using https://textcleaner.net/
     * given that each set of trials uses 5 phrases, and there are 3 sets for this experiment,
     * the text file has 45 phrases of varying length, enough to prevent any repeated phrases on subsequent trials
     * @return ArrayList of phrases to be used in trials
     */
    protected ArrayList<String> generatePhraseSet(){
        ArrayList<String> allPhrases = new ArrayList<>();
        ArrayList<String> phrasesForTrials = new ArrayList<>();
        Resources resources = getApplicationContext().getResources();
        InputStream inputStream = resources.openRawResource(PHRASES_SOURCE);

        try{
            InputStreamReader iRead = new InputStreamReader(inputStream);
            BufferedReader bRead = new BufferedReader(iRead);
            String s=bRead.readLine();
            while(s!=null){
                allPhrases.add(s);
                s=bRead.readLine();
            }
            iRead.close();
            bRead.close();
            Random rand = new Random();
            for(int i=0; i<NUMBER_OF_TRIAL_PHRASES; i++){
                int index = rand.nextInt(allPhrases.size()-1);
                phrasesForTrials.add(allPhrases.get(index));
            }
        }catch(FileNotFoundException e){
            Log.i(MYDEBUG,"File NOT FOUND");
        }catch(IOException e){
            Log.i(MYDEBUG, "IO Exception");
        }

        return phrasesForTrials;
    }

    /**
     * Method to calculate Words Per min by dividing the words in the list of phrases given to user
     * to enter, by the time taken to type those set of phrases. Returns a float number of calculated WPM
     * @param list list of phrases used in the testing set, used to get word count
     * @param millisTime time taken in typing the testing set of phrases
     * @return wordsPerMin
     */
    protected float calculateWPM(ArrayList<String> list, long millisTime){
        //convert milliseconds to seconds
        float secondsTime= millisTime / 1000f;
        int words=0;
        for(int i=0;i<list.size();i++){
            String phrase = list.get(i);
            String [] numWords = phrase.trim().split("\\s+");
            words+=numWords.length;
        }
        //words / minute time
        return words/(secondsTime/60);
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

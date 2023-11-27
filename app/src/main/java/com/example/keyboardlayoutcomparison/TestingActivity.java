package com.example.keyboardlayoutcomparison;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class TestingActivity extends Activity {

    //for debug
    private final static String MYDEBUG = "MYDEBUG";
    private final static String DEBUGLOG = "DEBUGLOG";
    //environment variable: number of phrases for each set of trial
    private final static int NUMBER_OF_TRIAL_PHRASES=5;
    //source of phrases in res>raw>phrases.txt
    private final static int PHRASES_SOURCE = R.raw.phrases;

    LinearLayout testView;
    TextView pageTitle, textToType, timeLabel;
    EditText userInput;
    ArrayList<String>testPhraseList, qwertyPhraseList, dvorakPhraseList, messageasePhraseList;
    int currentPhraseNumber,currentKeyboard;
    UserInputListener userTextChangedListener;
    Button endOfTestOkButton;
    //values to be calculated and sent to results page
    float qwertyWPM, dvorakWPM, messageaseWPM, qwertyErrorRate, dvorakErrorRate, messageaseErrorRate;
    //state tracking variable
    private final static int QWERTY=0;
    private final static int DVORAK=1;
    private final static int MESSAGEASE=2;
    //integer count for each error in typing
    int qwertyErrors, dvorakErrors, messageaseErrors, currentKeyboardErrors;
    //time stored in millis -> long
    long currentKeyboardTime,currentKeyboardStartTime, qwertyStartTime, qwertyStopTime, dvorakStartTime, dvorakStopTime, messageaseStartTime, messageaseStopTime;
    CountDownTimer countDownTimer;
    String previousText;

    //State String Name Variables to be used in Bundle
    private final static String PHRASES_LIST="phrases_list";
    private final static String CURRENT_KEYBOARD="current_keyboard";
    private final static String CURRENT_KEYBOARD_PHRASE_NUMBER="current_keyboard_phrase_number";
    private final static String CURRENT_KEYBOARD_START_TIME="current_keyboard_start_time";
    private final static String CURRENT_KEYBOARD_ERRORS="current_keyboard_errors";

    private final static String QWERTY_KEYBOARD_START_TIME="qwerty_keyboard_start_time";
    private final static String QWERTY_KEYBOARD_STOP_TIME="qwerty_keyboard_stop_time";
    private final static String QWERTY_KEYBOARD_ERRORS="qwerty_keyboard_errors";
    private final static String QWERTY_KEYBOARD_PHRASE_LIST="qwerty_keyboard_phrase_list";
    private final static String QWERTY_KEYBOARD_WPM="qwerty_keyboard_wpm";
    private final static String QWERTY_KEYBOARD_ERROR_RATE="qwerty_keyboard_error_rate";

    private final static String DVORAK_KEYBOARD_START_TIME="dvorak_keyboard_start_time";
    private final static String DVORAK_KEYBOARD_STOP_TIME="dvorak_keyboard_stop_time";
    private final static String DVORAK_KEYBOARD_ERRORS="dvorak_keyboard_errors";
    private final static String DVORAK_KEYBOARD_PHRASE_LIST="dvorak_keyboard_phrase_list";
    private final static String DVORAK_KEYBOARD_WPM="dvorak_keyboard_wpm";
    private final static String DVORAK_KEYBOARD_ERROR_RATE="dvorak_keyboard_error_rate";

    private final static String MESSAGEASE_KEYBOARD_START_TIME="messagease_keyboard_start_time";
    private final static String MESSAGEASE_KEYBOARD_STOP_TIME="messagease_keyboard_stop_time";
    private final static String MESSAGEASE_KEYBOARD_ERRORS="messagease_keyboard_errors";
    private final static String MESSAGEASE_KEYBOARD_PHRASE_LIST="messagease_keyboard_phrase_list";
    private final static String MESSAGEASE_KEYBOARD_WPM="messagease_keyboard_wpm";
    private final static String MESSAGEASE_KEYBOARD_ERROR_RATE="messagease_keyboard_error_rate";



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    //view testing xml layout
        setContentView(R.layout.testing_page);
        testView=findViewById(R.id.testLayout);
        testView.setVisibility(View.VISIBLE);
    //set references from view
        pageTitle = findViewById(R.id.test_title);
        textToType = findViewById(R.id.text_to_enter);
        userInput = findViewById(R.id.userInput);
        timeLabel = findViewById(R.id.time);
    //goNext in-between phases, hidden for now
        endOfTestOkButton = findViewById(R.id.end_of_test_OK);
        endOfTestOkButton.setVisibility(View.GONE);
    //generate phase to type
        testPhraseList = generatePhraseSet();
        textToType.setText(testPhraseList.get(currentPhraseNumber));
    //set other environment variables
        currentKeyboard=QWERTY;
        currentKeyboardStartTime =System.currentTimeMillis();
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
                int seconds= (int)(mills%60);
                int minutes= (int) (mills/60);
                String time = String.format("%02d:%02d", minutes, seconds);
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
    //begin first test
        countDownTimer.start();
        userInput.setSelected(true);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState){
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
        Log.i(MYDEBUG, "words: "+words+" time: "+secondsTime/60);
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
        int numChars=0;
        for(int i=0; i<list.size();i++){
            String s=list.get(i);
            numChars+=s.length();
        }
        //error rate is calculated by ((true-error)/true)*100
        errorRate=((float)(numChars-numErrors)/numChars )* 100f;
        return errorRate;
    }

    /**
     * This method is called when moving to the next set of phrases to be entered with another keyboard
     * layout. This method handles tracks the start & end times of each phrase, and tracking and
     * calculating WPM and error rate at the end of each phrase.
     * called from userInputListener when it detects end of test
     */
    protected void changeKeyboard(){
        Log.i(MYDEBUG,"Changing keyboard from "+currentKeyboard+" to "+ (currentKeyboard+1));
        //remove listener when changing keyboard
        userInput.removeTextChangedListener(userTextChangedListener);
        userInput.setEnabled(false);
        userInput.setText("");
        //enable again for user to switch keyboard layout before beginning next set of phrases
        userInput.setEnabled(true);
        //button to proceed to next set of phrases
        endOfTestOkButton.setVisibility(View.VISIBLE);
        if(currentKeyboard == QWERTY){
            //done qwerty, now switch to dvorak
            //record stats
            qwertyStopTime = System.currentTimeMillis();
            qwertyStartTime = currentKeyboardStartTime;
            qwertyErrors = currentKeyboardErrors;
            qwertyPhraseList.addAll(testPhraseList);
            //cancel & reset timer after recording stop time
            countDownTimer.cancel();
            currentKeyboardTime =0;
            //calculate stats with defined methods calculateWPM and calculateErrorRate
            qwertyWPM = calculateWPM(qwertyPhraseList, (qwertyStopTime - qwertyStartTime));
            qwertyErrorRate = calculateErrorRate(qwertyPhraseList, qwertyErrors);
            Log.i(MYDEBUG, "QWERTY DONE WPM: "+qwertyWPM +"| ACC: "+qwertyErrorRate);
            //moving forward preparing for next keyboard layout
            textToType.setText(getString(R.string.on_finish_test_qwerty));
            pageTitle.setText(getString(R.string.dvorak_title));
            currentKeyboard = DVORAK;
        }else if(currentKeyboard == DVORAK){
            //done dvorak, now switch to messagease
            //record stats
            dvorakStopTime = System.currentTimeMillis();
            dvorakStartTime = currentKeyboardStartTime;
            dvorakErrors = currentKeyboardErrors;
            dvorakPhraseList.addAll(testPhraseList);
            //cancel and reset timer after recording stop time
            countDownTimer.cancel();
            currentKeyboardTime=0;
            // calculate stats with defined methods calculateWPM and calculateErrorRate
            dvorakWPM = calculateWPM(dvorakPhraseList, (dvorakStopTime-dvorakStartTime));
            dvorakErrorRate = calculateErrorRate(dvorakPhraseList, dvorakErrors);
            Log.i(MYDEBUG, "DVORAK DONE WPM: "+dvorakWPM +"| ACC: "+dvorakErrorRate);
            //moving forward preparing for next keyboard layout
            currentKeyboard=MESSAGEASE;
            textToType.setText(getString(R.string.on_finish_test_dvorak));
            pageTitle.setText(getString(R.string.messagease_title));
        }else if(currentKeyboard == MESSAGEASE){
            //done messagease, now calculate stats and pass calculated stats to results page via getResults()
            //record stats
            messageaseStopTime = System.currentTimeMillis();
            messageaseStartTime = currentKeyboardStartTime;
            messageaseErrors = currentKeyboardErrors;
            messageasePhraseList.addAll(testPhraseList);
            //cancel timer
            countDownTimer.cancel();
            //reset text view
            textToType.setText("");
            //calculate stats with defined methods calculateWPM and calculateErrorRate
            messageaseWPM = calculateWPM(messageasePhraseList, (messageaseStopTime-messageaseStartTime));
            messageaseErrorRate = calculateErrorRate(messageasePhraseList, messageaseErrors);
            Log.i(MYDEBUG, "MESSAGEASE DONE WPM: "+messageaseWPM +"| ACC: "+messageaseErrorRate);
            //move to results page
            getResults();
        }
    } //end changeKeyboard()

    /**
     * Button onClick function for button id:"end_of_test_OK" . This method replaces text to type text view
     * with instructions to follow before moving forward with the next set of phrases.
     * @param view : button's view
     */
    public void onClickBeginTest(View view){
        Log.i(MYDEBUG,"onclick begin test clicked ");
        //make sure its visible
        testView.setVisibility(View.VISIBLE);
        //generate new phrase list for new keyboard
        testPhraseList = generatePhraseSet();
        //set first phrase
        textToType.setText(testPhraseList.get(currentPhraseNumber));
        //enable user input
        userInput.setVisibility(View.VISIBLE);
        userInput.setEnabled(true);
        //remove begin test button from view
        endOfTestOkButton.setVisibility(View.GONE);
        //reset current stats for starting set of phrases with new keyboard
        currentKeyboardErrors=0;
        currentKeyboardStartTime=System.currentTimeMillis();
        userInput.setText("");
        userInput.addTextChangedListener(userTextChangedListener);
        //begin new test
        countDownTimer.start();
    }

    /**
     * this method is called at the end of the final phase, right before we move onto the next activity
     * of displaying results. it captures all the statistics and saves it into bundles before transporting
     * to next activity.
     */
    protected void getResults(){
        Intent i = new Intent(getApplicationContext(), ResultsActivity.class);
        Bundle b = new Bundle();
        //store calculated stats in bundle
        b.putFloat(QWERTY_KEYBOARD_WPM, qwertyWPM);
        b.putFloat(QWERTY_KEYBOARD_ERROR_RATE, qwertyErrorRate);
        b.putFloat(DVORAK_KEYBOARD_WPM, dvorakWPM);
        b.putFloat(DVORAK_KEYBOARD_ERROR_RATE, dvorakErrorRate);
        b.putFloat(MESSAGEASE_KEYBOARD_WPM, messageaseWPM);
        b.putFloat(MESSAGEASE_KEYBOARD_ERROR_RATE, messageaseErrorRate);
        Log.i(MYDEBUG,"ON RESULTS: Q WPM: "+qwertyWPM+" Q ACC"+qwertyErrorRate);
        Log.i(MYDEBUG,"ON RESULTS: D WPM: "+dvorakWPM+" D ACC"+dvorakErrorRate);
        Log.i(MYDEBUG,"ON RESULTS: M WPM: "+messageaseWPM+" M ACC"+messageaseErrorRate);
        i.putExtras(b);
        startActivity(i);
        finish();
    }

    private class UserInputListener implements TextWatcher{
        @Override
        public void afterTextChanged(Editable e){

        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){
            //track previous text if error is made
            previousText = s.toString();
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after){
            //do not allow backspace. if new user input length less than previous length
            if(s.length() < previousText.length()){
                Log.i(MYDEBUG, "BACKSPACE DETECTED");
                userInput.setText(previousText);
                userInput.setSelection(start+1);
            }
            //get text to be typed, & index of current user cursor
            String currentPhrase = testPhraseList.get(currentPhraseNumber);
            int indexOfTypedChar = s.length()-1;
            if(indexOfTypedChar < 0 ){
                indexOfTypedChar=0;
            }
            //checking valid
            if(testPhraseList.isEmpty() || currentPhraseNumber >- testPhraseList.size()){
                Log.i(DEBUGLOG, "Invalid phrase number or phrase list empty Phrase "+currentPhraseNumber);
            }
            if(s == null || s.length() <= indexOfTypedChar){
                Log.i(DEBUGLOG, "user input s is null or is negative length");
                return;
            }
            if(indexOfTypedChar < currentPhrase.length() -1){
                //still more characters to be typed in phrase before completing phrase
                char correctChar = currentPhrase.charAt(indexOfTypedChar);
                char typedChar = s.charAt(indexOfTypedChar);
                if(typedChar!=correctChar){
                    //error found
                    //temporarily remove listener & reset user input field to previous text
                    userInput.removeTextChangedListener(this);
                    Log.i(DEBUGLOG,"incorrect char at "+indexOfTypedChar);
                    userInput.setText(currentPhrase.substring(0, Math.min(indexOfTypedChar, currentPhrase.length())));
                    if(indexOfTypedChar < s.length()){
                        userInput.setSelection(indexOfTypedChar);
                    }
                    userInput.addTextChangedListener(this);
                    currentKeyboardErrors++;
                }else{
                    //error not found
                }
            }else{
                //typed char in last character of phrase
                currentPhraseNumber++;
                if(currentPhraseNumber <= testPhraseList.size()-1){
                    //last phrase in test
                    indexOfTypedChar=0;
                    userInput.removeTextChangedListener(this);
                    //adjust selection
                    if(indexOfTypedChar < currentPhrase.length()-1 && indexOfTypedChar < s.length()){
                        userInput.setSelection(indexOfTypedChar);
                    }else{
                        userInput.setSelection(currentPhrase.length());
                    }
                    userInput.removeTextChangedListener(this);
                    //reset userInputBox
                    userInput.setText(new Editable.Factory().newEditable(""));
                    //get next phrase
                    textToType.setText(testPhraseList.get(currentPhraseNumber));
                    Log.i(MYDEBUG,"new phrase: "+testPhraseList.get(currentPhraseNumber));
                    userInput.addTextChangedListener(this);
                }else{
                    //no unused phrases remain
                    currentPhraseNumber=0;
                    changeKeyboard();
                }
            }
        }

    }//end userInputListener()

}//end testingActivity

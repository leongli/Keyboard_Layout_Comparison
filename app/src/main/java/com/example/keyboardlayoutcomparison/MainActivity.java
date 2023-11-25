/**
 * EECS4443 Final project
 *
 *
 * install dvorak apk from https://m.apkpure.com/dvorak-keyboard/com.inmenzo.dvorakkeyboard
 * dvorak playstore equivalent: https://play.google.com/store/apps/details?id=com.inmenzo.dvorakkeyboard&hl=en_AU
 *
 * install messagease apk from https://messagease-keyboard.en.uptodown.com/android
 * messagease playstore equivalent: https://play.google.com/store/apps/details?id=com.exideas.mekb&hl=en_CA&gl=US
 */

package com.example.keyboardlayoutcomparison;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //main.xml introduces users to information about the test. main.xml button onclick triggers proceed()
        setContentView(R.layout.main);
    }
    //qwerty_intro.xml shows users how to switch keyboards with a screenshot. the button in qwerty_intro triggers clickStart when clicked
    public void proceed(View view){
        setContentView(R.layout.qwerty_intro);
    }
    //launches testing activity to begin test when clickStart button clicked
    public void clickStart(View view){
        Intent i = new Intent(getApplicationContext(), TestingActivity.class);
        startActivity(i);
    }

}

package com.example.keyboardlayoutcomparison;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }
    public void proceed(View v){
        setContentView(R.layout.qwerty_intro);
    }
    public void clickStart(View v){
        Intent i = new Intent(getApplicationContext(), QwertyTesting.class);
        startActivity(i);
    }

}

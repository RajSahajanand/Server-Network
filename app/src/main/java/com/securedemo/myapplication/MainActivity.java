package com.securedemo.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import com.securedemo.myapplication.utils.Intent_Pass_interface;

public class MainActivity extends AppCompatActivity {

    public static Intent_Pass_interface intent_pass_interface1;
    public static Activity context;


    public static void checkkthis(Activity context1, Intent_Pass_interface intent_pass_interface, boolean... doShowAds) {

        intent_pass_interface1 = intent_pass_interface;
        context = context1;

        intent_pass_interface1.onIntentpass(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
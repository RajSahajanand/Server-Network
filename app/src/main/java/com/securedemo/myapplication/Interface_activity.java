package com.securedemo.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.securedemo.myapplication.utils.Intent_Pass_interface;
import com.securedemo.myapplication.utils.Parameter_Class;

public class Interface_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_activity);

        Parameter_Class.Server_Show = true;
        Parameter_Class.Server_Id = "touchvpn";
        Parameter_Class.Server_random = false;
        Parameter_Class.Server_Direct_Connect = false;

        MainActivity.checkkthis(Interface_activity.this,new Intent_Pass_interface() {
            @Override
            public void onIntentpass(boolean b) {
                Toast.makeText(Interface_activity.this, "Loggggg" +
                        "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.securedemo.myapplication.connection;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.securedemo.myapplication.R;
import com.securedemo.myapplication.Start_Activity;
import com.securedemo.myapplication.utils.Prefrences;
import com.securedemo.myapplication.utils.Utils;

public class Privacy_Policy_Screen extends AppCompatActivity {
    
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_policy);

        textView = (TextView) findViewById(R.id.txt_privacy);
        TextView next = (TextView) findViewById(R.id.next);
        
        Utils.isConnectingToInternet(Privacy_Policy_Screen.this, new Utils.OnCheckNet() {
            @Override
            public void OnCheckNet(boolean b) {
                if (b) {
                    try {
                        if (Prefrences.getserver_policy() != null) {
                            textView.setMovementMethod(LinkMovementMethod.getInstance());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                textView.setText(Html.fromHtml(Prefrences.getserver_policy(), Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                textView.setText(Html.fromHtml(Prefrences.getserver_policy()));
                            }

                        } else {
                            Toast.makeText(Privacy_Policy_Screen.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException n) {
                        n.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    finishAffinity();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Privacy_Policy_Screen.this, Start_Activity.class));
                finish();
            }
        });
    }


    boolean exit_flag = false;

    @Override
    public void onBackPressed() {
        if (exit_flag) {
            finishAffinity();
        } else {
            exit_flag = true;
            Toast.makeText(this, "Please tap again to exit!", Toast.LENGTH_SHORT).show();
        }

    }
}
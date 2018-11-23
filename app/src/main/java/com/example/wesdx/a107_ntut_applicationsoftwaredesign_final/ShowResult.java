package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowResult extends AppCompatActivity {
    private TextView textView8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        textView8 = (TextView) findViewById(R.id.textView8);

        Bundle bundle = getIntent().getExtras();
        String str = bundle.getString("mystring");

        textView8.setText(str);
    }

}

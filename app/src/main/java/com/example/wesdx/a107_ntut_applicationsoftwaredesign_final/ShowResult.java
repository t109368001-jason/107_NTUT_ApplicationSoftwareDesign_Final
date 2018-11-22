package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ShowResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
    }


    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if(requestCode == 0){
            if(resultCode == 101){
                Bundle bundle = data.getExtras();

            }
        }
    }

}

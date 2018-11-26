package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;

import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        task();
    }

    private void task() {
        new Thread() {
            public void run() {
                List<RailStation> railStationList = API.getStation(API.TRA);
                RailStation.removeUnreservationStation(railStationList);

                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("railStationListGson", (new Gson()).toJson(railStationList));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}

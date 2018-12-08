package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.SignatureException;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button TRA_AND_THSRButton = findViewById(R.id.TRA_AND_THSRButton);
        ImageButton TRAButton = findViewById(R.id.TRAButton);
        ImageButton THSRButton = findViewById(R.id.THSRButton);

        TRAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task(API.TRA);
            }
        });
        TRA_AND_THSRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task(API.TRA_AND_THSR);
            }
        });
        THSRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task(API.THSR);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void task(final String transportation) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog dialog = new ProgressDialog(WelcomeActivity.this);
            private List<RailStation> railStationList;
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if(transportation.equals(API.TRA_AND_THSR)) {
                        List<RailStation> temp = API.getStation(API.THSR);
                        railStationList = API.getStation(API.TRA);
                        railStationList.addAll(temp);
                    } else {
                        railStationList = API.getStation(transportation);
                    }
                    RailStation.removeUnreservationStation(railStationList);
                } catch (SignatureException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("取得車站");
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
                if(railStationList != null) {
                    if(railStationList.size() == 0) {
                        Toast.makeText(WelcomeActivity.this, "Error : 無法取得車站資料", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("railStationListGson", (new Gson()).toJson(railStationList));
                        bundle.putString("transportation", transportation);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(WelcomeActivity.this, "Error : 無法取得車站資料", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}

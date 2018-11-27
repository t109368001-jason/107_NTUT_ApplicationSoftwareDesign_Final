package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        task();
    }

    @SuppressLint("StaticFieldLeak")
    private void task() {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog dialog = new ProgressDialog(WelcomeActivity.this);
            private List<RailStation> railStationList = new ArrayList<>();
            @Override
            protected Void doInBackground(Void... voids) {
                railStationList = API.getStation(API.TRA);
                RailStation.removeUnreservationStation(railStationList);
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
                        Toast.makeText(WelcomeActivity.this, "Error : 無法連線至API", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("railStationListGson", (new Gson()).toJson(railStationList));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(WelcomeActivity.this, "Error : 無法連線至API", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
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
        setInitialData();
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(WelcomeActivity.this);

                final String[] settingList = {"台鐵-台鐵轉乘時間", "台鐵-高鐵轉乘時間"};

                alertDialog.setTitle("選擇設定");
                alertDialog.setItems(settingList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(WelcomeActivity.this);

                        final EditText input = new EditText(WelcomeActivity.this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);

                        alertDialog.setTitle("輸入時間(分鐘)");
                        alertDialog.setView(input);
                        alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which2) {
                                String number = input.getText().toString();
                                if(number.equals("")) {
                                    Toast.makeText(WelcomeActivity.this, "請輸入數字", Toast.LENGTH_SHORT).show();
                                } else {
                                    SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putInt((which == 0 ? "TRAToTRATransferTime" : "TRAToTHSRTransferTime"), Integer.parseInt(input.getText().toString()));
                                    editor.apply();
                                }
                            }
                        });
                        alertDialog.show();
                    }
                });
                alertDialog.show();
                return(true);
            case R.id.action_exit:
                finish();
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @SuppressLint("StaticFieldLeak")
    private void setInitialData() {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog dialog = new ProgressDialog(WelcomeActivity.this);
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    List<RailStation> railStationList = API.getStation(API.TRA);
                    RailStation.removeUnreservationStation(railStationList);
                    Router.saveRailStationListToCache(API.TRA, railStationList);
                    Router.saveRailStationListToCache(API.THSR, API.getStation(API.THSR));
                } catch (SignatureException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("更新資料");
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
            }
        }.execute();
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

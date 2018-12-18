package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

//https://github.com/ptxmotc/Sample-code
//https://ptx.transportdata.tw/PTX/Topic/fbeac0a2-fc53-4ffa-8961-597b2d3e6bdd

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass.MyException;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass.MySQLiteOpenHelper;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass.Router;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass.TrainPath;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailGeneralTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RegionalRailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StationOfLine;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";

    private TextView dateTextView, timeTextView;
    private TextView originStationTextView_buffer, destinationStationTextView_buffer;
    private RailStation originStation;
    private RailStation destinationStation;
    private List<RailStation> railStationList;
    private List<RegionalRailStation> regionalRailStationList;
    private CheckBox isDirectArrivalCheckBox, useTHSR;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private boolean useDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        final TextView originStationTextView = findViewById(R.id.stationNameTextView);
        final TextView destinationStationTextView = findViewById(R.id.destinationStationTextView);
        originStationTextView_buffer = originStationTextView;
        destinationStationTextView_buffer = destinationStationTextView;
        Button searchButton = findViewById(R.id.searchButton);
        Button changeStationButton = findViewById(R.id.changeStationButton);
        isDirectArrivalCheckBox = findViewById(R.id.isDirectArrivalCheckBox);
        useTHSR = findViewById(R.id.useTHSR);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(MainActivity.this);

        dateTextView.setText(API.dateFormat.format(Calendar.getInstance().getTime()));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(year, month, day, 0, 0);
                        dateTextView.setText(API.dateFormat.format(calendar1.getTime()));
                    }
                }, year, month, day).show();
            }
        });

        timeTextView.setText(API.timeFormat.format(Calendar.getInstance().getTime()));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(0, 0, 0, hour, minute);
                        timeTextView.setText(API.timeFormat.format(calendar1.getTime()));
                    }

                }, hour, minute, true).show();
            }
        });

        originStationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStation(originStationTextView);
            }
        });

        destinationStationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStation(destinationStationTextView);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTask();
            }
        });

        changeStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RailStation railStation = originStation;
                originStation = destinationStation;
                destinationStation = railStation;
                originStationTextView_buffer.setText(originStation.StationName.Zh_tw);
                destinationStationTextView_buffer.setText(destinationStation.StationName.Zh_tw);
            }
        });

        isDirectArrivalCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useTHSR.setEnabled(!isDirectArrivalCheckBox.isChecked());
                if(isDirectArrivalCheckBox.isChecked()) {
                    useTHSR.setChecked(false);
                }
            }
        });

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title_bar);

        setInitialData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("useDataBase", useDataBase);
        if(originStation != null) editor.putString("originOperatorIDStationID", originStation.OperatorID + originStation.StationID);
        if(destinationStation != null) editor.putString("destinationOperatorIDStationID", destinationStation.OperatorID + destinationStation.StationID);
        editor.apply();
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
                changeSettings();
                return(true);
            case R.id.action_exit:
                finish();
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    private void changeSettings() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        final String[] settingList = {"變更台鐵-台鐵轉乘時間：" + Long.toString(Router.TRAToTRATransferTime/60/1000), "變更台鐵-高鐵轉乘時間：" + Long.toString(Router.TRAToTHSRTransferTime/60/1000), "其他"};

        alertDialog.setTitle("選擇設定");
        alertDialog.setItems(settingList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                if(which == 2) {
                    final String[] settingList2 = {"效能優先", "節省流量"};
                    alertDialog.setTitle("請選擇");
                    alertDialog.setItems(settingList2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which2) {
                            useDataBase = (which2 == 1);
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("useDataBase", useDataBase);
                            editor.apply();
                            Toast.makeText(MainActivity.this, "已設為" + (settingList2[which2]), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    final EditText input = new EditText(MainActivity.this);
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
                            if (number.equals("")) {
                                Toast.makeText(MainActivity.this, "請輸入數字", Toast.LENGTH_SHORT).show();
                            } else {
                                int time = Integer.parseInt(input.getText().toString());
                                if (time < 0) {
                                    Toast.makeText(MainActivity.this, "請輸入大於等於0的數字", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (which == 0) {
                                        Router.TRAToTRATransferTime = time * 60 * 1000;
                                    } else {
                                        Router.TRAToTHSRTransferTime = time * 60 * 1000;
                                    }
                                    SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putInt((which == 0 ? "TRAToTRATransferTime" : "TRAToTHSRTransferTime"), time);
                                    editor.apply();
                                    Toast.makeText(MainActivity.this, "已將" + (which == 0 ? "台鐵-台鐵轉乘時間" : "台鐵-高鐵轉乘時間") + "設為：" + Integer.toString(time), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                alertDialog.show();
            }
        });
        alertDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void setInitialData() {
        new AsyncTask<Void, String, Void>() {
            private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            private Exception exception;
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    List<RailStation> railStationList_newest;

                    publishProgress("從本地資料庫取得車站資訊");
                    railStationList = mySQLiteOpenHelper.getAllRailStations(API.TRA_AND_THSR);

                    if(railStationList == null) {
                        publishProgress("從MOTC取得車站資訊");
                        if((railStationList_newest = API.getStation(API.TRA_AND_THSR)) == null) throw new MyException("無法從MOTC取得車站資訊");
                        RailStation.removeUnreservationStation(railStationList_newest);
                    } else {
                        publishProgress("檢查車站資訊更新");
                        railStationList_newest = API.getStation(API.TRA_AND_THSR, RailStation.getNewestUpdateTime(API.TRA_AND_THSR, railStationList));
                    }

                    publishProgress("更新本地資料庫車站資訊");
                    for(RailStation railStation:railStationList_newest) {
                        if(railStationList == null) railStationList = new ArrayList<>();
                        railStationList.add(railStation);
                        mySQLiteOpenHelper.addOrUpdateRailStation(railStation);
                    }

                    if(railStationList == null) throw new MyException("無法取得車站資訊");


                    Router.saveRailStationListToCache(API.TRA_AND_THSR, railStationList);

                    regionalRailStationList = RegionalRailStation.convert(railStationList);

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    String originOperatorIDStationID = settings.getString("originOperatorIDStationID", "");
                    String destinationOperatorIDStationID = settings.getString("destinationOperatorIDStationID", "");
                    useDataBase = settings.getBoolean("useDataBase", false);
                    int TRAToTRATransferTime = settings.getInt("TRAToTRATransferTime", 5);
                    int TRAToTHSRTransferTime = settings.getInt("TRAToTHSRTransferTime", 10);
                    Router.TRAToTRATransferTime = TRAToTRATransferTime * 60 * 1000;
                    Router.TRAToTHSRTransferTime = TRAToTHSRTransferTime * 60 * 1000;
                    for (RailStation railStation:railStationList) {
                        if ((railStation.OperatorID + railStation.StationID).equals(originOperatorIDStationID)) {
                            originStationTextView_buffer.setText(railStation.StationName.Zh_tw);
                            originStation = railStation;
                        }
                        if ((railStation.OperatorID + railStation.StationID).equals(destinationOperatorIDStationID)) {
                            destinationStationTextView_buffer.setText(railStation.StationName.Zh_tw);
                            destinationStation = railStation;
                        }
                    }
                } catch (MyException | ParseException | IOException | SignatureException e) {
                    e.printStackTrace();
                    exception = e;
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
                if(exception != null) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setMessage("嚴重錯誤");
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
                if((originStation != null)&&(destinationStation != null)){
                    boolean directArrival = (originStation.OperatorID.equals(API.THSR) && destinationStation.OperatorID.equals(API.THSR));
                    int visibility = (originStation.OperatorID.equals(API.TRA) && destinationStation.OperatorID.equals(API.TRA)) ? View.VISIBLE : View.INVISIBLE;
                    boolean directArrivalEnable = (originStation.OperatorID.equals(API.TRA) && destinationStation.OperatorID.equals(API.TRA));
                    isDirectArrivalCheckBox.setEnabled(directArrivalEnable);
                    isDirectArrivalCheckBox.setChecked(directArrival);
                    useTHSR.setVisibility(visibility);
                }
            }

            @Override
            protected void onProgressUpdate(String... titles) {
                super.onProgressUpdate(titles);
                dialog.setMessage(titles[0]);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void searchTask() {
        if((originStation == null) || (destinationStation == null)) {
            Toast.makeText(MainActivity.this, "請選擇車站", Toast.LENGTH_SHORT).show();
            return;
        }
        new AsyncTask<Void, String, Void>() {
            private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            private List<TrainPath> trainPathList;
            private String errorMessage;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String transportation;
                    if(useTHSR.isChecked()) {
                        transportation = API.TRA_AND_THSR;
                    } else {
                        transportation = (originStation.OperatorID.equals(destinationStation.OperatorID) ? originStation.OperatorID : API.TRA_AND_THSR);
                    }
/*
                    for(int i = 0; i < railStationList.size(); i++) {  //46
                        for(int j = 230; j < railStationList.size(); j++) {
                            if(i == j) continue;
                            trainPathList = Router.getTrainPath(API.TRA_AND_THSR, dateTextView.getText().toString(), null, null, null, railStationList, railStationList.get(i), railStationList.get(j), false);

                            if((trainPathList != null ? trainPathList.size() : 0) == 0) {
                                Log.d("DEBUG1", Integer.toString(i) + " " + railStationList.get(i).StationName.Zh_tw + "→" + Integer.toString(j) + " " + railStationList.get(j).StationName.Zh_tw + " : " + Integer.toString(trainPathList != null ? trainPathList.size() : 0));
                            } else {
                                Log.d("DEBUG2", Integer.toString(i) + " " + railStationList.get(i).StationName.Zh_tw + "→" + Integer.toString(j) + " " + railStationList.get(j).StationName.Zh_tw + " : " + Integer.toString(trainPathList != null ? trainPathList.size() : 0));

                            }
                        }
                    }
*/
                    if(Router.stationOfLineList == null) {
                        List<StationOfLine> stationOfLineList;
                        if(useDataBase) {
                            publishProgress("從本地資料庫取得台鐵路線資訊");
                            stationOfLineList = mySQLiteOpenHelper.getAllStationOfLine();
                            List<StationOfLine> stationOfLineList_newest;
                            if (stationOfLineList == null) {
                                publishProgress("從MOTC取得台鐵路線資訊");
                                if ((stationOfLineList_newest = API.getStationOfLine()) == null)
                                    throw new MyException("無法從MOTC取得台鐵路線資訊");
                                StationOfLine.fixMissing15StationProblem(stationOfLineList_newest);
                            } else {
                                publishProgress("檢查台鐵路線資訊更新");
                                if ((stationOfLineList_newest = API.getStationOfLine(StationOfLine.getNewestUpdateTime(stationOfLineList))) == null)
                                    throw new MyException("無法從MOTC取得台鐵路線資訊");
                                if (stationOfLineList_newest.size() > 0) {
                                    if ((stationOfLineList_newest = API.getStationOfLine()) == null)
                                        throw new MyException("無法從MOTC取得台鐵路線資訊");
                                    StationOfLine.fixMissing15StationProblem(stationOfLineList_newest);
                                }
                            }
                            publishProgress("更新本地資料庫台鐵路線資訊");
                            for (StationOfLine stationOfLine : stationOfLineList_newest) {
                                if (stationOfLineList == null)
                                    stationOfLineList = new ArrayList<>();
                                stationOfLineList.add(stationOfLine);
                                mySQLiteOpenHelper.addOrUpdateStationOfLine(stationOfLine);
                            }

                            if (stationOfLineList == null) throw new MyException("無法取得台鐵路線資訊");
                        } else {
                            publishProgress("從MOTC取得台鐵路線資訊");
                            if ((stationOfLineList = API.getStationOfLine()) == null)
                                throw new MyException("無法從MOTC取得台鐵路線資訊");
                            StationOfLine.fixMissing15StationProblem(stationOfLineList);
                        }
                        Router.stationOfLineList = stationOfLineList;
                    }

                    if(Router.railDailyTimetableListCacheDate_TRA == null || !Router.railDailyTimetableListCacheDate_TRA.equals(dateTextView.getText().toString())) {
                        List<RailDailyTimetable> railDailyTimetableList_TRA = null;
                        List<RailGeneralTimetable> railGeneralTimetableList_TRA = null;
                        if(useDataBase) {
                            if (transportation.equals(API.TRA) || transportation.equals(API.TRA_AND_THSR)) {
                                publishProgress("從本地資料庫取得台鐵班次資訊");
                                railDailyTimetableList_TRA = mySQLiteOpenHelper.getAllRailDailyTimetable(API.TRA, dateTextView.getText().toString());
                                railGeneralTimetableList_TRA = mySQLiteOpenHelper.getAllRailGeneralTimetables(API.TRA);
                                List<RailDailyTimetable> railDailyTimetableList_newest_TRA;
                                List<RailGeneralTimetable> railGeneralTimetableList_newest_TRA;
                                if (railDailyTimetableList_TRA == null) {
                                    publishProgress("從MOTC取得台鐵班次資訊");
                                    if ((railDailyTimetableList_newest_TRA = API.getDailyTimetableByTrainDate(API.TRA, dateTextView.getText().toString())) == null)
                                        throw new MyException("無法從MOTC取得台鐵班次資訊");
                                    if ((railGeneralTimetableList_newest_TRA = API.getGeneralTimetable(API.TRA)) == null)
                                        throw new MyException("無法從MOTC取得台鐵班次資訊");
                                } else {
                                    publishProgress("檢查台鐵班次資訊更新");
                                    if ((railDailyTimetableList_newest_TRA = API.getDailyTimetableByTrainDate(API.TRA, dateTextView.getText().toString(), RailDailyTimetable.getNewestUpdateTime(railDailyTimetableList_TRA))) == null)
                                        throw new MyException("無法從MOTC取得台鐵班次資訊");
                                    if ((railGeneralTimetableList_newest_TRA = API.getGeneralTimetable(API.TRA, RailGeneralTimetable.getNewestUpdateTime(railGeneralTimetableList_TRA))) == null)
                                        throw new MyException("無法從MOTC取得台鐵班次資訊");
                                }
                                publishProgress("更新本地資料庫台鐵班次資訊");
                                RailDailyTimetable.add(railDailyTimetableList_newest_TRA, railGeneralTimetableList_newest_TRA, dateTextView.getText().toString());
                                for (RailDailyTimetable railDailyTimetable : railDailyTimetableList_newest_TRA) {
                                    if (railDailyTimetableList_TRA == null)
                                        railDailyTimetableList_TRA = new ArrayList<>();
                                    railDailyTimetableList_TRA.add(railDailyTimetable);
                                    mySQLiteOpenHelper.addOrUpdateRailDailyTimetable(API.TRA, railDailyTimetable);
                                }
                                for (RailGeneralTimetable railGeneralTimetable : railGeneralTimetableList_newest_TRA) {
                                    if (railGeneralTimetableList_TRA == null)
                                        railGeneralTimetableList_TRA = new ArrayList<>();
                                    railGeneralTimetableList_TRA.add(railGeneralTimetable);
                                    mySQLiteOpenHelper.addOrUpdateRailGeneralTimetable(API.TRA, railGeneralTimetable);
                                }
                            }
                        } else {
                            if ((railDailyTimetableList_TRA = API.getDailyTimetableByTrainDate(API.TRA, dateTextView.getText().toString())) == null)
                                throw new MyException("無法從MOTC取得台鐵班次資訊");
                            if((railGeneralTimetableList_TRA = Router.railGeneralTimetableListCache_TRA) == null) {
                                if ((railGeneralTimetableList_TRA = API.getGeneralTimetable(API.TRA)) == null)
                                    throw new MyException("無法從MOTC取得台鐵班次資訊");
                            }
                        }
                        if (railDailyTimetableList_TRA == null) throw new MyException("無法取得台鐵班次資訊");
                        RailDailyTimetable.add(railDailyTimetableList_TRA, railGeneralTimetableList_TRA, dateTextView.getText().toString());
                        Router.saveRailDailyTimetableListToCache(API.TRA, railDailyTimetableList_TRA, dateTextView.getText().toString());
                        Router.saveRailGeneralTimetableToCache(API.TRA, railGeneralTimetableList_TRA);
                    }

                    if(Router.railDailyTimetableListCacheDate_THSR == null || !Router.railDailyTimetableListCacheDate_THSR.equals(dateTextView.getText().toString())) {
                        List<RailDailyTimetable> railDailyTimetableList_THSR = null;
                        List<RailGeneralTimetable> railGeneralTimetableList_THSR = null;
                        if(useDataBase) {
                            if (transportation.equals(API.THSR) || transportation.equals(API.TRA_AND_THSR)) {
                                publishProgress("從本地資料庫取得高鐵班次資訊");
                                railDailyTimetableList_THSR = mySQLiteOpenHelper.getAllRailDailyTimetable(API.THSR, dateTextView.getText().toString());
                                railGeneralTimetableList_THSR = mySQLiteOpenHelper.getAllRailGeneralTimetables(API.THSR);
                                List<RailDailyTimetable> railDailyTimetableList_newest_THSR;
                                List<RailGeneralTimetable> railGeneralTimetableList_newest_THSR;
                                if (railDailyTimetableList_THSR == null) {
                                    publishProgress("從MOTC取得高鐵班次資訊");
                                    if ((railDailyTimetableList_newest_THSR = API.getDailyTimetableByTrainDate(API.THSR, dateTextView.getText().toString())) == null)
                                        throw new MyException("無法從MOTC取得高鐵班次資訊");
                                    if ((railGeneralTimetableList_newest_THSR = API.getGeneralTimetable(API.THSR)) == null)
                                        throw new MyException("無法從MOTC取得高鐵班次資訊");
                                } else {
                                    publishProgress("檢查高鐵班次資訊更新");
                                    if ((railDailyTimetableList_newest_THSR = API.getDailyTimetableByTrainDate(API.THSR, dateTextView.getText().toString(), RailDailyTimetable.getNewestUpdateTime(railDailyTimetableList_THSR))) == null)
                                        throw new MyException("無法從MOTC取得高鐵班次資訊");
                                    if ((railGeneralTimetableList_newest_THSR = API.getGeneralTimetable(API.THSR, RailGeneralTimetable.getNewestUpdateTime(railGeneralTimetableList_THSR))) == null)
                                        throw new MyException("無法從MOTC取得高鐵班次資訊");
                                }
                                publishProgress("更新本地資料庫高鐵班次資訊");
                                RailDailyTimetable.add(railDailyTimetableList_newest_THSR, railGeneralTimetableList_newest_THSR, dateTextView.getText().toString());
                                for (RailDailyTimetable railDailyTimetable : railDailyTimetableList_newest_THSR) {
                                    if (railDailyTimetableList_THSR == null)
                                        railDailyTimetableList_THSR = new ArrayList<>();
                                    railDailyTimetableList_THSR.add(railDailyTimetable);
                                    mySQLiteOpenHelper.addOrUpdateRailDailyTimetable(API.THSR, railDailyTimetable);
                                }
                                for (RailGeneralTimetable railGeneralTimetable : railGeneralTimetableList_newest_THSR) {
                                    if (railGeneralTimetableList_THSR == null)
                                        railGeneralTimetableList_THSR = new ArrayList<>();
                                    railGeneralTimetableList_THSR.add(railGeneralTimetable);
                                    mySQLiteOpenHelper.addOrUpdateRailGeneralTimetable(API.THSR, railGeneralTimetable);
                                }
                            }
                        } else {
                            if ((railDailyTimetableList_THSR = API.getDailyTimetableByTrainDate(API.THSR, dateTextView.getText().toString())) == null)
                                throw new MyException("無法從MOTC取得高鐵班次資訊");
                            if((railGeneralTimetableList_THSR = Router.railGeneralTimetableListCache_THSR) == null) {
                                if ((railGeneralTimetableList_THSR = API.getGeneralTimetable(API.THSR)) == null)
                                    throw new MyException("無法從MOTC取得高鐵班次資訊");
                            }
                        }
                        if (railDailyTimetableList_THSR == null) throw new MyException("無法取得高鐵班次資訊");
                        RailDailyTimetable.add(railDailyTimetableList_THSR, railGeneralTimetableList_THSR, dateTextView.getText().toString());
                        Router.saveRailDailyTimetableListToCache(API.THSR, railDailyTimetableList_THSR, dateTextView.getText().toString());
                        Router.saveRailGeneralTimetableToCache(API.THSR, railGeneralTimetableList_THSR);
                    }
                    dialog.setMessage("取得班次");
                    trainPathList = Router.getTrainPath(transportation, API.timeFormat.parse(timeTextView.getText().toString()), null, null, railStationList, originStation, destinationStation, isDirectArrivalCheckBox.isChecked());

                } catch (ParseException | SignatureException | IOException | MyException e) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
                if(errorMessage != null) {
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    if ((trainPathList != null ? trainPathList.size() : 0) == 0) {
                        Toast.makeText(MainActivity.this, "查無班次", Toast.LENGTH_SHORT).show();
                    } else {
                        int limitSize = 10;
                        if(trainPathList.size() > limitSize) trainPathList = trainPathList.subList(0, limitSize);

                        Intent intent = new Intent(MainActivity.this, ShowResult.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("trainPathListGson", (new Gson()).toJson(trainPathList));
                        bundle.putString("originStationGson", (new Gson()).toJson(originStation));
                        bundle.putString("destinationStationGson", (new Gson()).toJson(destinationStation));
                        bundle.putInt("limitSize", limitSize);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                dialog.setMessage(values[0]);
            }
        }.execute();
    }

    private void selectStation(final TextView textView) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        String[] regionList = new String[regionalRailStationList.size()];

        for(int i = 0; i < regionalRailStationList.size(); i++) {
            regionList[i] = regionalRailStationList.get(i).regionName;
        }

        alertDialog.setTitle("選擇區域");
        alertDialog.setItems(regionList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                List<String> buffer = new ArrayList<>();

                for(int i = 0; i < regionalRailStationList.get(which).railStationList.size(); i++) {
                    if(regionalRailStationList.get(which).railStationList.get(i).StationName.Zh_tw.equals("古莊")) continue;
                    buffer.add(regionalRailStationList.get(which).railStationList.get(i).StationName.Zh_tw);
                }

                final String[] stationList = new String[buffer.size()];
                for(int i = 0; i < buffer.size(); i++) {
                    stationList[i] = buffer.get(i);
                }

                alertDialog.setTitle("選擇區域");
                alertDialog.setItems(stationList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which2) {
                        String newOriginStationName = (textView == originStationTextView_buffer) ? stationList[which2] : originStationTextView_buffer.getText().toString();
                        String newDestinationStationName = (textView == destinationStationTextView_buffer) ? stationList[which2] : destinationStationTextView_buffer.getText().toString();

                        if(newOriginStationName.equals(newDestinationStationName)) {
                            Toast.makeText(MainActivity.this, "請選擇不同車站", Toast.LENGTH_SHORT).show();
                        } else {
                            textView.setText(stationList[which2]);
                            if(textView == originStationTextView_buffer) {
                                originStation = regionalRailStationList.get(which).railStationList.get(which2);
                            } else if(textView == destinationStationTextView_buffer){
                                destinationStation = regionalRailStationList.get(which).railStationList.get(which2);
                            }
                            if((originStation != null)&&(destinationStation != null)){
                                boolean directArrival = (originStation.OperatorID.equals(API.THSR) && destinationStation.OperatorID.equals(API.THSR));
                                int visibility = (originStation.OperatorID.equals(API.TRA) && destinationStation.OperatorID.equals(API.TRA)) ? View.VISIBLE : View.INVISIBLE;
                                boolean directArrivalEnable = (originStation.OperatorID.equals(API.TRA) && destinationStation.OperatorID.equals(API.TRA));
                                isDirectArrivalCheckBox.setEnabled(directArrivalEnable);
                                isDirectArrivalCheckBox.setChecked(directArrival);
                                useTHSR.setVisibility(visibility);
                            }
                        }
                    }
                });
                alertDialog.show();
            }
        });
        alertDialog.show();
    }
}


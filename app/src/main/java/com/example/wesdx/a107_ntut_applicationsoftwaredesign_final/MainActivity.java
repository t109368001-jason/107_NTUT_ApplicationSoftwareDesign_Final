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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RegionalRailStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";

    private TextView dateTextView, timeTextView;
    private TextView originStationTextView_buffer, destinationStationTextView_buffer;
    private String transportation;
    private RailStation originStation;
    private RailStation destinationStation;
    private List<RailStation> railStationList;
    private List<RegionalRailStation> regionalRailStationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        final TextView originStationTextView = findViewById(R.id.originStationTextView);
        final TextView destinationStationTextView = findViewById(R.id.destinationStationTextView);
        originStationTextView_buffer = originStationTextView;
        destinationStationTextView_buffer = destinationStationTextView;
        Button searchButton = findViewById(R.id.searchButton);
        Button changeStationButton = findViewById(R.id.changeStationButton);
        final CheckBox isDirectArrivalCheckBox = findViewById(R.id.directArrivalCheckBox);

        Bundle bundle;
        if((bundle= getIntent().getExtras()) == null) {
            Toast.makeText(MainActivity.this, "Bundle data losed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String railStationListGson = null;
        if(((railStationListGson = bundle.getString("railStationListGson")) == null)
                || ((transportation = bundle.getString("transportation")) == null)) {
            Toast.makeText(MainActivity.this, "Bundle data losed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }



        if((railStationList = (new Gson()).fromJson(railStationListGson, new TypeToken<List<RailStation>>() {}.getType())) == null) {
            Toast.makeText(MainActivity.this, "Bundle data losed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(!transportation.equals(API.TRA)) {
            isDirectArrivalCheckBox.setVisibility(View.INVISIBLE);
        }

        regionalRailStationList = RegionalRailStation.convert(railStationList);

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
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                if((originStation == null) || (destinationStation == null)) {
                    Toast.makeText(MainActivity.this, "請選擇車站", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                    private List<TrainPath> trainPathList;
                    private String errorMessage;

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
/*
                            trainPathList = Router.getTrainPath(transportation, dateTextView.getText().toString(), API.timeFormat.parse(timeTextView.getText().toString()), null, null, railStationList, originStation, destinationStation, isDirectArrivalCheckBox.isChecked());
                            for(int i = 0; i < railStationList.size(); i++) {   //7
                                for(int j = i + 1; j < railStationList.size(); j++) {
                                    trainPathList = Router.getTrainPath(transportation, dateTextView.getText().toString(), null, null, null, railStationList, railStationList.get(i), railStationList.get(j), isDirectArrivalCheckBox.isChecked());
                                    if((trainPathList != null ? trainPathList.size() : 0) == 0) {
                                        Log.d("DEBUG1", railStationList.get(i).StationName.Zh_tw + "→" + railStationList.get(j).StationName.Zh_tw + " : " + Integer.toString(trainPathList != null ? trainPathList.size() : 0));
                                    } else {
                                        Log.d("DEBUG2", railStationList.get(i).StationName.Zh_tw + "→" + railStationList.get(j).StationName.Zh_tw + " : " + Integer.toString(trainPathList != null ? trainPathList.size() : 0));
                                    }
                                }
                            }
*/
                            trainPathList = Router.getTrainPath(transportation, dateTextView.getText().toString(), API.timeFormat.parse(timeTextView.getText().toString()), null, null, railStationList, originStation, destinationStation, isDirectArrivalCheckBox.isChecked());

                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMessage = e.getMessage();
                        }
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog.setMessage("取得班次");
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
                }.execute();
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

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String originStationID = settings.getString(transportation + "originStationID", "");
        String destinationStationID = settings.getString(transportation + "destinationStationID", "");
        int TRAToTRATransferTime = settings.getInt("TRAToTRATransferTime", 5);
        int TRAToTHSRTransferTime = settings.getInt("TRAToTHSRTransferTime", 10);
        Router.TRAToTRATransferTime = TRAToTRATransferTime * 60 * 1000;
        Router.TRAToTHSRTransferTime = TRAToTHSRTransferTime * 60 * 1000;
        for (RailStation railStation:railStationList) {
            if (railStation.StationID.equals(originStationID)) {
                originStationTextView_buffer.setText(railStation.StationName.Zh_tw);
                originStation = railStation;
            }
            if (railStation.StationID.equals(destinationStationID)) {
                destinationStationTextView_buffer.setText(railStation.StationName.Zh_tw);
                destinationStation = railStation;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(transportation + "originStationID", originStation.StationID);
        editor.putString(transportation + "destinationStationID", destinationStation.StationID);
        editor.apply();
    }

    private void selectStation(final TextView textView) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        if(regionalRailStationList.size() == 1) {
            final String[] stationList = new String[regionalRailStationList.get(0).railStationList.size()];

            for(int i = 0; i < regionalRailStationList.get(0).railStationList.size(); i++) {
                stationList[i] = regionalRailStationList.get(0).railStationList.get(i).StationName.Zh_tw;
            }

            alertDialog.setTitle("選擇區域");
            alertDialog.setItems(stationList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    textView.setText(stationList[which]);
                    if(textView == originStationTextView_buffer) {
                        originStation = regionalRailStationList.get(0).railStationList.get(which);
                    } else if(textView == destinationStationTextView_buffer){
                        destinationStation = regionalRailStationList.get(0).railStationList.get(which);
                    }
                }
            });
        } else {
            String[] regionList = new String[regionalRailStationList.size()];

            for(int i = 0; i < regionalRailStationList.size(); i++) {
                regionList[i] = regionalRailStationList.get(i).regionName;
            }

            alertDialog.setTitle("選擇區域");
            alertDialog.setItems(regionList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, final int which) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                    final String[] stationList = new String[regionalRailStationList.get(which).railStationList.size()];

                    for(int i = 0; i < regionalRailStationList.get(which).railStationList.size(); i++) {
                        stationList[i] = regionalRailStationList.get(which).railStationList.get(i).StationName.Zh_tw;
                    }

                    alertDialog.setTitle("選擇區域");
                    alertDialog.setItems(stationList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which2) {
                            textView.setText(stationList[which2]);
                            if(textView == originStationTextView_buffer) {
                                originStation = regionalRailStationList.get(which).railStationList.get(which2);
                            } else if(textView == destinationStationTextView_buffer){
                                destinationStation = regionalRailStationList.get(which).railStationList.get(which2);
                            }
                        }
                    });
                    alertDialog.show();
                }
            });
        }
        alertDialog.show();
    }
}


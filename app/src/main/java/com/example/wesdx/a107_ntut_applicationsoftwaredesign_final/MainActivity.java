package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

//https://github.com/ptxmotc/Sample-code
//https://ptx.transportdata.tw/PTX/Topic/fbeac0a2-fc53-4ffa-8961-597b2d3e6bdd

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";

    private TextView dateTextView, timeTextView;
    private Spinner originStationSpinner, destinationStationSpinner;
    private CheckBox priceFirstCheckBox;
    private CheckBox useTimeAsDesTimeCheckBox;
    private Button searchButton;
    private Button changeStationButton;

    private String transportation;
    private RailStation originStation;
    private RailStation destinationStation;
    private List<RailStation> railStationList;

    private int priceFirst = 0;
    private int useTimeAsDesTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        originStationSpinner = (Spinner)findViewById(R.id.originStationSpinner);
        destinationStationSpinner = (Spinner)findViewById(R.id.destinationStationSpinner);
        priceFirstCheckBox = (CheckBox) findViewById(R.id.priceFirstCheckBox);
        useTimeAsDesTimeCheckBox = (CheckBox) findViewById(R.id.useTimeAsDesTimeCheckBox);
        searchButton = (Button)findViewById(R.id.searchButton);
        changeStationButton = (Button) findViewById(R.id.changeStationButton);

        Bundle bundle = getIntent().getExtras();
        String railStationListGson = bundle.getString("railStationListGson");
        transportation = bundle.getString("transportation");

        railStationList = (new Gson()).fromJson(railStationListGson, new TypeToken<List<RailStation>>() {}.getType());

        dateTextView.setText((new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime()));
        timeTextView.setText((new SimpleDateFormat("HH:mm")).format(Calendar.getInstance().getTime()));

        myAdapter transAdapter = new myAdapter(railStationList, R.layout.rail_station_spinner_item);
        originStationSpinner.setAdapter(transAdapter);
        destinationStationSpinner.setAdapter(transAdapter);

        priceFirstCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                priceFirst = (isChecked)? 1: 0;
            }
        });

        useTimeAsDesTimeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useTimeAsDesTime = (isChecked)? 1: 0;
            }
        });

        originStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                originStation = railStationList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                originStation = null;
            }
        });

        destinationStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinationStation = railStationList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                destinationStation = null;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                    private List<RailDailyTimetable> railDailyTimetableList;

                    @Override
                    protected Void doInBackground(Void... voids) {
                        RailStation.transferStation(railStationList, originStation);
                        //railDailyTimetableList = Router.get(transportation, dateTextView.getText().toString(), timeTextView.getText().toString(), railStationList, originStation, destinationStation);
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
                        if(railDailyTimetableList != null) {
                            if(railDailyTimetableList.size() == 0) {
                                Toast.makeText(MainActivity.this, "查無班次", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(MainActivity.this, ShowResult.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("railDailyTimetableListGson", (new Gson()).toJson(railDailyTimetableList));
                                bundle.putString("originStationGson", (new Gson()).toJson(originStation));
                                bundle.putString("destinationStationGson", (new Gson()).toJson(destinationStation));
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "查無班次", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });

        changeStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tmp = originStationSpinner.getSelectedItemPosition();
                originStationSpinner.setSelection(destinationStationSpinner.getSelectedItemPosition());
                destinationStationSpinner.setSelection(tmp);
            }
        });

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
                        timeTextView.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar1.getTime()));
                    }
                }, year, month, day).show();
            }
        });

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
                        timeTextView.setText(new SimpleDateFormat("HH:mm").format(calendar1.getTime()));
                    }

                }, hour, minute, true).show();
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String originStationID = settings.getString(transportation + "originStationID", "");
        String destinationStationID = settings.getString(transportation + "destinationStationID", "");
        for (int i = 0; i < railStationList.size(); i++) {
            if (railStationList.get(i).StationID.equals(originStationID)) {
                originStationSpinner.setSelection(i);
            }
            if (railStationList.get(i).StationID.equals(destinationStationID)) {
                destinationStationSpinner.setSelection(i);
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

    private class myAdapter extends BaseAdapter {
        private List<RailStation> data;
        private int view;

        public myAdapter(List<RailStation> data, int view) {
            this.data = data;
            this.view = view;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public RailStation getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(view, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            String stationName = ((data.get(position).ReservationCode == null)? "" : data.get(position).ReservationCode) + data.get(position).StationName.Zh_tw;
            name.setText(stationName);
            return convertView;
        }
    }
}


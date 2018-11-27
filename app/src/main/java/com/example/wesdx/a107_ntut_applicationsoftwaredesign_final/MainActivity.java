package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

//https://github.com/ptxmotc/Sample-code
//https://ptx.transportdata.tw/PTX/Topic/fbeac0a2-fc53-4ffa-8961-597b2d3e6bdd

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";

    private TextView dateTextView, timeTextView;
    private Spinner originStationSpinner, destinationStationSpinner;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private CheckBox checkBox2;
    private Button search;
    private Button changeStation;

    private List<RailStation> railStationList;
    private List<RailDailyTimetable> railDailyTimetableList;

    private RailStation originStation;
    private RailStation destinationStation;

    private int TRAOrTHSR = 0;
    private int price = 0;
    private int arriveTimeFirst = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = (TextView) findViewById(R.id.textView);
        timeTextView = (TextView) findViewById(R.id.textView2);
        originStationSpinner = (Spinner)findViewById(R.id.start_station);
        destinationStationSpinner = (Spinner)findViewById(R.id.arrive_station);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        search = (Button)findViewById(R.id.search);
        changeStation = (Button) findViewById(R.id.changeStation);

        Bundle bundle = getIntent().getExtras();
        String str = bundle.getString("railStationListGson");

        railStationList = (new Gson()).fromJson(str, new TypeToken<List<RailStation>>() {}.getType());

        dateTextView.setText((new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime()));
        timeTextView.setText((new SimpleDateFormat("HH:mm")).format(Calendar.getInstance().getTime()));

        String[] stationName = new String[railStationList.size()];
        for (int i = 0; i < stationName.length; i++) {
            stationName[i] = railStationList.get(i).ReservationCode + railStationList.get(i).StationName.Zh_tw;
        }

        myAdapter transAdapter = new myAdapter(stationName,R.layout.rail_station_spinner_item);
        originStationSpinner.setAdapter(transAdapter);
        destinationStationSpinner.setAdapter(transAdapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton:
                        TRAOrTHSR = 1;
                        break;
                    case R.id.radioButton2:
                        TRAOrTHSR = 2;
                        break;
                    case R.id.radioButton3:
                        TRAOrTHSR = 3;
                        break;
                    default:
                        TRAOrTHSR = 0;
                        break;
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                price = (isChecked)? 1: 0;
            }
        });

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                arriveTimeFirst = (isChecked)? 1: 0;
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

        search.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

                    @Override
                    protected Void doInBackground(Void... voids) {
                        railDailyTimetableList = Router.get(dateTextView.getText().toString(), timeTextView.getText().toString(), originStation, destinationStation);
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

        changeStation.setOnClickListener(new View.OnClickListener() {
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
                        String dateNumber = String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(day);
                        timeTextView.setText(dateNumber);
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
                        String timeNumber = hour + ":" + minute;
                        timeTextView.setText(timeNumber);
                    }

                }, hour, minute, true).show();
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String originStationID = settings.getString("originStationID", "");
        String destinationStationID = settings.getString("destinationStationID", "");
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
    protected void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("originStationID", originStation.StationID);
        editor.putString("destinationStationID", destinationStation.StationID);
        editor.apply();
    }

    public class myAdapter extends BaseAdapter{
        private String[] data;
        private int view;

        public myAdapter(String[] data, int view){
            this.data = data;
            this.view = view;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public String getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(view, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(data[position]);
            return convertView;
        }
    }

}


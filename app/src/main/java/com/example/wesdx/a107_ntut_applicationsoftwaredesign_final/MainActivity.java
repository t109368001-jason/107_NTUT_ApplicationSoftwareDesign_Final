package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

//https://github.com/ptxmotc/Sample-code
//https://ptx.transportdata.tw/PTX/Topic/fbeac0a2-fc53-4ffa-8961-597b2d3e6bdd

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView, textView2, textView3, textView4, textView5;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private CheckBox checkBox2;

    private List<RailStation> railStations;
    private List<RailGeneralTimetable> railGeneralTimetables;
    private List<RailGeneralTrainInfo> railGeneralTrainInfos;

    private RailStation startStation;
    private RailStation arriveStation;
    private int TRAOrTHSR = 0;
    private int price = 0;
    private int arriveTimeFirst = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);

        railStations = TRAAPI.getRailStation();
        RailStation.removeUnreservationStation(railStations);
        railGeneralTimetables = TRAAPI.getRailGeneralTimetable();
        railGeneralTrainInfos = TRAAPI.getRailGeneralTrainInfo();

        textView.setText(railStations.get(0).StationName.Zh_tw);
        textView2.setText(railStations.get(1).StationName.Zh_tw);
        textView3.setText(railStations.get(2).StationName.Zh_tw);
        textView4.setText(railStations.get(3).StationName.Zh_tw);
        textView5.setText(railStations.get(4).StationName.Zh_tw);

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

        String[] stationName = new String[railStations.size()];
        for (int i = 0; i < stationName.length; i++) {
            stationName[i] = railStations.get(i).ReservationCode + railStations.get(i).StationName.Zh_tw;
        }

        Spinner start_station = (Spinner)findViewById(R.id.start_station);
        final Spinner arrive_station = (Spinner)findViewById(R.id.arrive_station);

        myAdapter transAdapter = new myAdapter(stationName,R.layout.spinner_text);
        start_station.setAdapter(transAdapter);
        arrive_station.setAdapter(transAdapter);

        start_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startStation = railStations.get(position);
                if((startStation != null)&& (arriveStation!= null)) {
                    List<RailODFare> railODFares = TRAAPI.getRailODFare(startStation.StationID, arriveStation.StationID);
                    if(railODFares.size() > 0) {
                        textView.setText(railODFares.get(0).Fares.get(0).Price);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        arrive_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arriveStation = railStations.get(position);
                if((startStation != null)&& (arriveStation!= null)) {
                    List<RailODFare> railODFares = TRAAPI.getRailODFare(startStation.StationID, arriveStation.StationID);
                    if(railODFares.size() > 0) {
                        textView.setText(railODFares.get(0).Fares.get(0).Price);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public  class myAdapter extends BaseAdapter{
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


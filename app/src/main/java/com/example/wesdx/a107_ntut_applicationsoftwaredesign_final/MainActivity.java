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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView, textView2, textView3, textView4, textView5;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private CheckBox checkBox2;

    private List<RailStation> TRARailStationList;
    private List<RailStation> THSRRailStationList;
    private List<RailGeneralTimetable> TRARailGeneralTimetableList;
    private List<RailGeneralTimetable> THSRRailGeneralTimetableList;
    private List<RailGeneralTrainInfo> TRARailGeneralTrainInfoList;
    private List<RailGeneralTrainInfo> THSRRailGeneralTrainInfoList;
    private List<RailODDailyTimetable> TRARailODDailyTimetableList;
    private List<RailODDailyTimetable> THSRRailODDailyTimetableList;
    private List<RegionalRailStation> TRARegionalRailStationList;
    private List<RegionalRailStation> THSRRegionalRailStationList;

    private RailStation originStation;
    private RailStation destinationStation;

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

        TRARailStationList = PTXAPI.getRailStation(PTXAPI.TRA);
        RailStation.removeUnreservationStation(TRARailStationList);
        TRARailGeneralTimetableList = PTXAPI.getRailGeneralTimetable(PTXAPI.TRA);
        TRARailGeneralTrainInfoList = PTXAPI.getRailGeneralTrainInfo(PTXAPI.TRA);
        TRARailODDailyTimetableList = PTXAPI.getRailODDailyTimetable(PTXAPI.TRA, "1002", "1004", "2018-11-22");
        TRARegionalRailStationList = RegionalRailStation.convert(TRARailStationList);
        //List<RailODFare> TRARailODFares = TRAAPI.getRailODFare(PTXAPI.TRA, originStation, destinationStation);
        TRARailODDailyTimetableList = RailODDailyTimetable.filter(TRARailODDailyTimetableList, "07:00", "01:00");

        THSRRailStationList = PTXAPI.getRailStation(PTXAPI.THSR);
        THSRRailGeneralTimetableList = PTXAPI.getRailGeneralTimetable(PTXAPI.THSR);
        THSRRailGeneralTrainInfoList = PTXAPI.getRailGeneralTrainInfo(PTXAPI.THSR);
        THSRRailODDailyTimetableList = PTXAPI.getRailODDailyTimetable(PTXAPI.THSR, "1010", "1020", "2018-11-22");
        THSRRegionalRailStationList = RegionalRailStation.convert(TRARailStationList);
        THSRRailODDailyTimetableList = RailODDailyTimetable.filter(THSRRailODDailyTimetableList, "07:00", "01:00");



        textView.setText(TRARailStationList.get(0).StationName.Zh_tw);
        textView2.setText(TRARailStationList.get(1).StationName.Zh_tw);
        textView3.setText(TRARailStationList.get(2).StationName.Zh_tw);
        textView4.setText(TRARailStationList.get(3).StationName.Zh_tw);
        textView5.setText(TRARailStationList.get(4).StationName.Zh_tw);

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

        String[] stationName = new String[TRARailStationList.size()];
        for (int i = 0; i < stationName.length; i++) {
            stationName[i] = TRARailStationList.get(i).ReservationCode + TRARailStationList.get(i).StationName.Zh_tw;
        }

        Spinner start_station = (Spinner)findViewById(R.id.start_station);
        final Spinner arrive_station = (Spinner)findViewById(R.id.arrive_station);

        myAdapter transAdapter = new myAdapter(stationName,R.layout.rail_station_spinner_item);
        start_station.setAdapter(transAdapter);
        arrive_station.setAdapter(transAdapter);

        start_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                originStation = TRARailStationList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                originStation = null;
            }
        });

        arrive_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinationStation = TRARailStationList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                destinationStation = null;
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


package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.List;

public class ShowResult extends AppCompatActivity {
    private ListView listView;
    private TextView ODName;
    RailStation originStation;
    RailStation destinationStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        listView = (ListView) findViewById(R.id.listView);
        ODName = (TextView) findViewById(R.id.ODName);

        Bundle bundle = getIntent().getExtras();
        String railDailyTimetableListGson = bundle.getString("railDailyTimetableListGson");
        String originStationGson = bundle.getString("originStationGson");
        String destinationStationGson = bundle.getString("destinationStationGson");

        List<RailDailyTimetable> railDailyTimetableList = (new Gson()).fromJson(railDailyTimetableListGson, new TypeToken<List<RailDailyTimetable>>() {}.getType());
        originStation = (new Gson()).fromJson(originStationGson, new TypeToken<RailStation>() {}.getType());
        destinationStation = (new Gson()).fromJson(destinationStationGson, new TypeToken<RailStation>() {}.getType());

        RailDailyTimetable.sort(railDailyTimetableList, originStation);

        myAdapter transAdapter = new myAdapter(railDailyTimetableList,R.layout.show_result_listview_item);
        listView.setAdapter(transAdapter);

        ODName.setText(originStation.StationName.Zh_tw + " → " + destinationStation.StationName.Zh_tw);
    }

    public  class myAdapter extends BaseAdapter {
        private List<RailDailyTimetable> railDailyTimetableList;
        private int view;

        public myAdapter(List<RailDailyTimetable> railDailyTimetableList, int view){
            this.railDailyTimetableList = railDailyTimetableList;
            this.view = view;
        }

        @Override
        public int getCount() {
            return railDailyTimetableList.size();
        }

        @Override
        public RailDailyTimetable getItem(int position) {
            return railDailyTimetableList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(view, parent, false);
            RailDailyTimetable railDailyTimetable = railDailyTimetableList.get(position);
            TextView oriStationTextView = convertView.findViewById(R.id.oriStationTextView);
            oriStationTextView.setText(railDailyTimetable.StopTimes.get(0).StationName.Zh_tw);
            TextView destStationTextView = convertView.findViewById(R.id.destStationTextView);
            destStationTextView.setText(railDailyTimetable.StopTimes.get(railDailyTimetable.StopTimes.size()-1).StationName.Zh_tw);
            TextView oriTimeTextView = convertView.findViewById(R.id.oriTimeTextView);
            oriTimeTextView.setText(railDailyTimetable.getStopTimeOfStopTimes(originStation).DepartureTime);
            TextView destTimeTextView = convertView.findViewById(R.id.destTimeTextView);
            destTimeTextView.setText(railDailyTimetable.getStopTimeOfStopTimes(destinationStation).ArrivalTime);
            TextView trainNo = convertView.findViewById(R.id.trainNo);
            trainNo.setText(railDailyTimetable.DailyTrainInfo.TrainNo);
            TextView tripLine = convertView.findViewById(R.id.tripLine);
            tripLine.setText(railDailyTimetable.getTripLineName());
            TextView ODTime = convertView.findViewById(R.id.ODTime);
            ODTime.setText((new SimpleDateFormat("HH小時mm分").format(railDailyTimetable.getODTime(originStation, destinationStation))));

            return convertView;
        }
    }

}

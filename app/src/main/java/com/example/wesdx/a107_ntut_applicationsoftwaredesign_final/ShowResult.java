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
    private TextView ODNameTextView, dataOver50TextView;
    private RailStation originStation;
    private RailStation destinationStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        listView = (ListView) findViewById(R.id.listView);
        ODNameTextView = (TextView) findViewById(R.id.ODNameTextView);
        dataOver50TextView = (TextView) findViewById(R.id.dataOver50TextView);

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

        ODNameTextView.setText(originStation.StationName.Zh_tw + " → " + destinationStation.StationName.Zh_tw);

        if((railDailyTimetableList != null ? railDailyTimetableList.size() : 0) == 30) {
            dataOver50TextView.setText("(前30筆)");
        }
    }

    public  class myAdapter extends BaseAdapter {
        private List<RailDailyTimetable> railDailyTimetableList;
        private int view;

        public myAdapter(List<RailDailyTimetable> railDailyTimetableList, int view) {
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
            ((TextView)convertView.findViewById(R.id.oriStationTextView)).setText(railDailyTimetable.DailyTrainInfo.StartingStationName.Zh_tw);
            ((TextView)convertView.findViewById(R.id.destStationTextView)).setText(railDailyTimetable.DailyTrainInfo.EndingStationName.Zh_tw);
            ((TextView)convertView.findViewById(R.id.oriTimeTextView)).setText(railDailyTimetable.getStopTimeOfStopTimes(originStation).DepartureTime);
            ((TextView)convertView.findViewById(R.id.destTimeTextView)).setText(railDailyTimetable.getStopTimeOfStopTimes(destinationStation).ArrivalTime);
            ((TextView)convertView.findViewById(R.id.trainNo)).setText(railDailyTimetable.DailyTrainInfo.TrainNo);
            ((TextView)convertView.findViewById(R.id.tripLine)).setText(railDailyTimetable.getTripLineName());
            ((TextView)convertView.findViewById(R.id.ODTime)).setText((new SimpleDateFormat("HH小時mm分").format(railDailyTimetable.getODTime(originStation, destinationStation))));
            return convertView;
        }
    }

}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.session.MediaSession;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ShowResult extends AppCompatActivity {
    private ListView listView;
    RailStation originStation;
    RailStation destinationStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        listView = (ListView) findViewById(R.id.listView);

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
            TextView textView9 = (TextView) convertView.findViewById(R.id.textView9);
            textView9.setText(railDailyTimetableList.get(position).StopTimes.get(0).StationName.Zh_tw);
            TextView textView10 = (TextView) convertView.findViewById(R.id.textView10);
            textView10.setText(railDailyTimetableList.get(position).StopTimes.get(railDailyTimetableList.get(position).StopTimes.size()-1).StationName.Zh_tw);
            TextView textView11 = (TextView) convertView.findViewById(R.id.textView11);
            textView11.setText(railDailyTimetableList.get(position).getStopTimeOfStopTimes(originStation).DepartureTime);
            TextView textView12 = (TextView) convertView.findViewById(R.id.textView12);
            textView12.setText(railDailyTimetableList.get(position).getStopTimeOfStopTimes(destinationStation).ArrivalTime);
            Date a = railDailyTimetableList.get(position).getODTime(originStation, destinationStation);
            return convertView;
        }
    }

}

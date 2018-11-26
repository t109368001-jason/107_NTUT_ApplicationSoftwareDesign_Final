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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ShowResult extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        listView = (ListView) findViewById(R.id.listView);

        Bundle bundle = getIntent().getExtras();
        String str = bundle.getString("railDailyTimetableListGson");

        List<RailDailyTimetable> railDailyTimetableList = (new Gson()).fromJson(str, new TypeToken<List<RailDailyTimetable>>() {}.getType());

        List<String> stringList = new ArrayList<>();

        for(int i = 0; i < railDailyTimetableList.size(); i++) {
            stringList.add(railDailyTimetableList.get(i).DailyTrainInfo.TrainNo);
        }


        myAdapter transAdapter = new myAdapter(stringList,R.layout.rail_station_spinner_item);
        listView.setAdapter(transAdapter);

    }

    public  class myAdapter extends BaseAdapter {
        private List<String> data;
        private int view;

        public myAdapter(List<String> data, int view){
            this.data = data;
            this.view = view;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
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
            name.setText(data.get(position));
            return convertView;
        }
    }

}

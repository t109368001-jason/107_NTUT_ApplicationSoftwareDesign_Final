package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

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

import org.w3c.dom.Text;

import java.util.Collections;
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

        Collections.sort(railDailyTimetableList);

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
            TextView oriStationTextView = convertView.findViewById(R.id.oriStationTextView);
            oriStationTextView.setText(railDailyTimetableList.get(position).StopTimes.get(0).StationName.Zh_tw);
            TextView destStationTextView = convertView.findViewById(R.id.destStationTextView);
            destStationTextView.setText(railDailyTimetableList.get(position).StopTimes.get(railDailyTimetableList.get(position).StopTimes.size()-1).StationName.Zh_tw);
            TextView oriTimeTextView = convertView.findViewById(R.id.oriTimeTextView);
            oriTimeTextView.setText(railDailyTimetableList.get(position).StopTimes.get(0).DepartureTime);
            TextView destTimeTextView = convertView.findViewById(R.id.destTimeTextView);
            destTimeTextView.setText(railDailyTimetableList.get(position).StopTimes.get(railDailyTimetableList.get(position).StopTimes.size()-1).ArrivalTime);
            TextView trainNo = convertView.findViewById(R.id.trainNo);
            trainNo.setText(railDailyTimetableList.get(position).DailyTrainInfo.TrainNo);
            TextView tripLine = convertView.findViewById(R.id.tripLine);
            tripLine.setText(railDailyTimetableList.get(position).DailyTrainInfo.TripLine);
            TextView ODTime = convertView.findViewById(R.id.ODTime);
            //ODTime.setText();
            return convertView;
        }
    }

}

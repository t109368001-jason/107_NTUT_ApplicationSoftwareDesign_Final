package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass.TrainPath;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass.TrainPathPart;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StopTime;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowResultDetailActivity extends AppCompatActivity {

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result_detail);

        Bundle bundle;
        final TrainPath trainPath;
        TrainPathPartAdapter trainPathAdapter;
        final ListView listView = findViewById(R.id.listView);
        TextView originDepartureTimeTextView = findViewById(R.id.arrivalTimeTextView);
        TextView destinationArrivalTImeTextView = findViewById(R.id.destinationArrivalTImeTextView);
        TextView totalTimeTextView = findViewById(R.id.totalTimeTextView);
        TextView originStationTextView = findViewById(R.id.stationNameTextView);
        TextView destinationStationTextView = findViewById(R.id.destinationStationTextView);

        if ((bundle = getIntent().getExtras()) == null) {
            Toast.makeText(ShowResultDetailActivity.this, "Bundle data losed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if((trainPath = (new Gson()).fromJson(bundle.getString("trainPathGson"), new TypeToken<TrainPath>() {}.getType())) == null) {
            Toast.makeText(ShowResultDetailActivity.this, "Bundle data losed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        originDepartureTimeTextView.setText(trainPath.getOrigeinDepartureTime());
        destinationArrivalTImeTextView.setText(trainPath.getDestinationArrivalTime());

        originStationTextView.setText(trainPath.getOriginRailStation().StationName.Zh_tw);
        destinationStationTextView.setText(trainPath.getDestinationRailStation().StationName.Zh_tw);

        try {
            totalTimeTextView.setText((new SimpleDateFormat("HH小時mm分").format(trainPath.getODTime())));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(ShowResultDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        trainPathAdapter = new TrainPathPartAdapter(trainPath.trainPathPartList);
        listView.setAdapter(trainPathAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowResultDetailActivity.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                ListView listView_temp = new ListView(ShowResultDetailActivity.this);
                listView_temp.setLayoutParams(lp);
                TrainPathPartStopTimesAdapter trainPathPartStopTimesAdapter = new TrainPathPartStopTimesAdapter(trainPath.trainPathPartList.get(position).railDailyTimetable.StopTimes);
                listView_temp.setAdapter(trainPathPartStopTimesAdapter);

                alertDialog.setTitle("停靠資訊");
                alertDialog.setView(listView_temp);
                alertDialog.show();
            }
        });

    }

    public class TrainPathPartStopTimesAdapter extends BaseAdapter {
        private List<StopTime> stopTimeList;

        TrainPathPartStopTimesAdapter(List<StopTime> stopTimeList) {
            this.stopTimeList = stopTimeList;
        }

        @Override
        public int getCount() {
            return stopTimeList.size();
        }

        @Override
        public StopTime getItem(int position) {
            return stopTimeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "SetTextI18n"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.trainpathpart_detail_listview_item, parent, false);

            ((TextView)convertView.findViewById(R.id.stationNameTextView)).setText(stopTimeList.get(position).StationName.Zh_tw);
            ((TextView)convertView.findViewById(R.id.arrivalTimeTextView)).setText(stopTimeList.get(position).ArrivalTime + "(到)");
            ((TextView)convertView.findViewById(R.id.departureTimeTextView)).setText(stopTimeList.get(position).DepartureTime + "(開)");
            return convertView;
        }
    }

    public class TrainPathPartAdapter extends BaseAdapter {
        private List<TrainPathPart> trainPathPartList;

        TrainPathPartAdapter(List<TrainPathPart> trainPathPartList) {
            this.trainPathPartList = trainPathPartList;
        }

        @Override
        public int getCount() {
            return trainPathPartList.size();
        }

        @Override
        public TrainPathPart getItem(int position) {
            return trainPathPartList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.trainpath_recyclerview_item_detail, parent, false);
            if(position == 0) {
                convertView.findViewById(R.id.upCircleImageView).setVisibility(View.GONE);
            }
            if(position == (trainPathPartList.size() - 1)) {
                convertView.findViewById(R.id.dowmCircleImageView).setVisibility(View.GONE);
            }
            TrainPathPart trainPathPart = trainPathPartList.get(position);
            ((TextView)convertView.findViewById(R.id.stationNameTextView)).setText(trainPathPart.originStation.StationName.Zh_tw);
            ((TextView)convertView.findViewById(R.id.destinationStationTextView)).setText(trainPathPart.destinationStation.StationName.Zh_tw);
            ((TextView)convertView.findViewById(R.id.arrivalTimeTextView)).setText(trainPathPart.railDailyTimetable.getStopTimeOfStopTimes(trainPathPart.originStation).DepartureTime);
            ((TextView)convertView.findViewById(R.id.departureTimeTextView)).setText(trainPathPart.railDailyTimetable.getStopTimeOfStopTimes(trainPathPart.destinationStation).ArrivalTime);
            ((TextView)convertView.findViewById(R.id.trainNoTextView)).setText(trainPathPart.railDailyTimetable.DailyTrainInfo.TrainNo);
            ((TextView)convertView.findViewById(R.id.trainTypeTextView)).setText((trainPathPart.originStation.OperatorID.equals(API.THSR)) ? "高鐵" : trainPathPart.railDailyTimetable.DailyTrainInfo.TrainTypeName.Zh_tw);
            ((ImageView)convertView.findViewById(R.id.trainIconImageView)).setImageResource((trainPathPart.originStation.OperatorID.equals("THSR") ? R.drawable.thsr : R.drawable.tra));
            return convertView;
        }
    }
}

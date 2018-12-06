package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ShowResult extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        ListView listView = (ListView) findViewById(R.id.listView);
        TextView ODNameTextView = (TextView) findViewById(R.id.ODNameTextView);
        TextView dataOver50TextView = (TextView) findViewById(R.id.dataOver50TextView);

        Bundle bundle = getIntent().getExtras();
        String trainPathListGson = null;
        String originStationGson = null;
        String destinationStationGson = null;
        if (bundle != null) {
            trainPathListGson = bundle.getString("trainPathListGson");
            originStationGson = bundle.getString("originStationGson");
            destinationStationGson = bundle.getString("destinationStationGson");
        } else {
            Toast.makeText(ShowResult.this, "Bundle data losed", Toast.LENGTH_SHORT).show();
            finish();
        }

        List<TrainPath> trainPathList = (new Gson()).fromJson(trainPathListGson, new TypeToken<List<TrainPath>>() {}.getType());
        RailStation originStation = (new Gson()).fromJson(originStationGson, new TypeToken<RailStation>() {
        }.getType());
        RailStation destinationStation = (new Gson()).fromJson(destinationStationGson, new TypeToken<RailStation>() {
        }.getType());

        final TrainPathAdapter trainPathAdapter = new TrainPathAdapter(trainPathList, R.layout.trainpath_listview_item);
        listView.setAdapter(trainPathAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrainPath trainPath = trainPathAdapter.getItem(position);
                Toast.makeText(ShowResult.this, Integer.toString(trainPath.trainPathPartList.size()), Toast.LENGTH_SHORT).show();
            }
        });

        //Toast.makeText(getApplicationContext(), position, Toast.LENGTH_SHORT).show();

        ODNameTextView.setText(originStation.StationName.Zh_tw + " → " + destinationStation.StationName.Zh_tw);

        if((trainPathList != null ? trainPathList.size() : 0) == 30) {
            dataOver50TextView.setText("(前30筆)");
        }
    }

    public class TrainPathAdapter extends BaseAdapter {
        private List<TrainPath> trainPathList;
        private int view;

        public TrainPathAdapter(List<TrainPath> trainPathList, int view) {
            this.trainPathList = trainPathList;
            this.view = view;
        }

        @Override
        public int getCount() {
            return trainPathList.size();
        }

        @Override
        public TrainPath getItem(int position) {
            return trainPathList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "SimpleDateFormat"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(view, parent, false);
            TrainPath trainPath = trainPathList.get(position);
            ((TextView)convertView.findViewById(R.id.originStationNameTextView)).setText(trainPath.trainPathPartList.get(0).originStation.StationName.Zh_tw);
            ((TextView)convertView.findViewById(R.id.destinationStationNameTextView)).setText(trainPath.getLastItem().destinationStation.StationName.Zh_tw);
            ((TextView)convertView.findViewById(R.id.originDepartureTimeTextView)).setText(trainPath.trainPathPartList.get(0).railDailyTimetable.getStopTimeOfStopTimes(trainPath.trainPathPartList.get(0).originStation.StationID).DepartureTime);
            ((TextView)convertView.findViewById(R.id.destinationArrivalTImeTextView)).setText(trainPath.getLastItem().railDailyTimetable.getStopTimeOfStopTimes(trainPath.getLastItem().destinationStation.StationID).ArrivalTime);
            Date originTime = null;
            Date destinationTime = null;
            try {
                originTime = API.timeFormat.parse(trainPath.trainPathPartList.get(0).railDailyTimetable.getStopTimeOfStopTimes(trainPath.trainPathPartList.get(0).originStation.StationID).DepartureTime);
                destinationTime = API.timeFormat.parse(trainPath.getLastItem().railDailyTimetable.getStopTimeOfStopTimes(trainPath.getLastItem().destinationStation.StationID).ArrivalTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(originTime == null || destinationTime == null) throw new NullPointerException();

            Date total = new Date(destinationTime.getTime() - originTime.getTime());
            total.setHours(total.getHours() - TimeZone.getDefault().getRawOffset()/1000/60/60);
            ((TextView)convertView.findViewById(R.id.totalTimeTextView)).setText((new SimpleDateFormat("HH小時mm分").format(total)));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowResult.this, LinearLayoutManager.HORIZONTAL, false);
            ((RecyclerView)convertView.findViewById(R.id.recyclerView)).setAdapter(new TrainPathPartAdapter(trainPath.trainPathPartList, R.layout.trainpath_recyclerview_item));
            ((RecyclerView)convertView.findViewById(R.id.recyclerView)).setLayoutManager(linearLayoutManager);
            return convertView;
        }
    }

        public class TrainPathPartAdapter extends RecyclerView.Adapter<TrainPathPartAdapter.MyViewHolder> {
            private List<TrainPath.TrainPathPart> trainPathPartList;
            private int view;

            public class MyViewHolder extends RecyclerView.ViewHolder {
                public View mView;
                public MyViewHolder(View v) {
                    super(v);
                    mView = v;
                }
            }

            public TrainPathPartAdapter(List<TrainPath.TrainPathPart> trainPathPartList, int view) {
                this.trainPathPartList = trainPathPartList;
                this.view = view;
            }

            @NonNull
            @Override
            public TrainPathPartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = (View) LayoutInflater.from(parent.getContext()).inflate(view, parent, false);

                MyViewHolder vh = new MyViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                TrainPath.TrainPathPart trainPathPart = trainPathPartList.get(position);
                if(position == 0) {
                    ((TextView)holder.mView.findViewById(R.id.leftArrowTextView)).setText("");
                }
                ((TextView)holder.mView.findViewById(R.id.originStationNameTextView)).setText(trainPathPart.originStation.StationName.Zh_tw);
                ((TextView)holder.mView.findViewById(R.id.destinationStationNameTextView)).setText(trainPathPart.destinationStation.StationName.Zh_tw);
                ((TextView)holder.mView.findViewById(R.id.originDepartureTimeTextView)).setText(trainPathPart.railDailyTimetable.getStopTimeOfStopTimes(trainPathPart.originStation).DepartureTime);
                ((TextView)holder.mView.findViewById(R.id.destinationArrivalTimeTextView)).setText(trainPathPart.railDailyTimetable.getStopTimeOfStopTimes(trainPathPart.destinationStation).ArrivalTime);
                ((TextView)holder.mView.findViewById(R.id.trainNoTextView)).setText(trainPathPart.railDailyTimetable.DailyTrainInfo.TrainNo);
                ((ImageView)holder.mView.findViewById(R.id.trainIconImageView)).setImageResource((trainPathPart.originStation.OperatorID.equals("THSR") ? R.drawable.thsr : R.drawable.tra));
            }

            @Override
            public int getItemCount() {
                return trainPathPartList.size();
            }
        }
}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass.TrainPath;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass.TrainPathPart;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowResult extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Bundle bundle;
        int limitSize;
        List<TrainPath> trainPathList;
        RailStation originStation;
        RailStation destinationStation;
        TrainPathAdapter trainPathAdapter;

        ListView listView = findViewById(R.id.listView);
        TextView originStationTextView = findViewById(R.id.stationNameTextView);
        TextView destinationStationTextView = findViewById(R.id.destinationStationTextView);
        TextView dataOver50TextView = findViewById(R.id.dataOver50TextView);

        if((bundle = getIntent().getExtras()) == null) {
            Toast.makeText(ShowResult.this, "Bundle data losed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        limitSize = bundle.getInt("limitSize");
        if(((trainPathList = (new Gson()).fromJson(bundle.getString("trainPathListGson"), new TypeToken<List<TrainPath>>() {}.getType())) == null)
                || ((originStation = (new Gson()).fromJson(bundle.getString("originStationGson"), new TypeToken<RailStation>() {}.getType())) == null)
                || ((destinationStation = (new Gson()).fromJson(bundle.getString("destinationStationGson"), new TypeToken<RailStation>() {}.getType())) == null))
        {
            Toast.makeText(ShowResult.this, "Bundle data losed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        originStationTextView.setText(originStation.StationName.Zh_tw);
        destinationStationTextView.setText(destinationStation.StationName.Zh_tw);
        dataOver50TextView.setText(trainPathList.size() == limitSize ?"(前" + Integer.toString(limitSize) + "筆)" : "");

        trainPathAdapter = new TrainPathAdapter(trainPathList);
        listView.setAdapter(trainPathAdapter);
    }

    public class TrainPathAdapter extends BaseAdapter {
        private List<TrainPath> trainPathList;

        TrainPathAdapter(List<TrainPath> trainPathList) {
            this.trainPathList = trainPathList;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            TrainPath trainPath = trainPathList.get(position);
            convertView = getLayoutInflater().inflate(R.layout.trainpath_listview_item, parent, false);
            ((TextView)convertView.findViewById(R.id.arrivalTimeTextView)).setText(trainPath.getOrigeinDepartureTime());
            ((TextView)convertView.findViewById(R.id.destinationArrivalTImeTextView)).setText(trainPath.getDestinationArrivalTime());

            try {
                ((TextView)convertView.findViewById(R.id.totalTimeTextView)).setText((new SimpleDateFormat("HH小時mm分").format(trainPath.getODTime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowResult.this, LinearLayoutManager.HORIZONTAL, false);
            ((RecyclerView)convertView.findViewById(R.id.recyclerView)).setAdapter(new TrainPathPartAdapter(trainPath.trainPathPartList));
            ((RecyclerView)convertView.findViewById(R.id.recyclerView)).setLayoutManager(linearLayoutManager);

            //convertView.findViewById(R.id.showDetailButton).setVisibility((trainPath.trainPathPartList.size() <= 1 ? View.INVISIBLE : View.VISIBLE));
            convertView.findViewById(R.id.showDetailButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowResult.this, ShowResultDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("trainPathGson", (new Gson()).toJson(trainPathList.get(position)));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public class TrainPathPartAdapter extends RecyclerView.Adapter<TrainPathPartAdapter.MyViewHolder> {
        private List<TrainPathPart> trainPathPartList;

        class MyViewHolder extends RecyclerView.ViewHolder {
            View mView;
            MyViewHolder(View v) {
                super(v);
                mView = v;
            }
        }

        TrainPathPartAdapter(List<TrainPathPart> trainPathPartList) {
            this.trainPathPartList = trainPathPartList;
        }

        @NonNull
        @Override
        public TrainPathPartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = (trainPathPartList.size() <= 1) ? R.layout.trainpath_recyclerview_item_detail : R.layout.trainpath_recyclerview_item;
            View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            TrainPathPart trainPathPart = trainPathPartList.get(position);
            if(trainPathPartList.size() <= 1)
            {
                holder.mView.findViewById(R.id.upCircleImageView).setVisibility(View.GONE);
                holder.mView.findViewById(R.id.dowmCircleImageView).setVisibility(View.GONE);
                holder.mView.findViewById(R.id.upBigCircleImageView).setVisibility(View.GONE);
                holder.mView.findViewById(R.id.stationNameTextView).setVisibility(View.GONE);
                holder.mView.findViewById(R.id.destinationStationTextView).setVisibility(View.GONE);
                holder.mView.findViewById(R.id.arrivalTimeTextView).setVisibility(View.GONE);
                holder.mView.findViewById(R.id.departureTimeTextView).setVisibility(View.GONE);
                holder.mView.findViewById(R.id.textView).setVisibility(View.GONE);

                ((TextView)holder.mView.findViewById(R.id.trainNoTextView)).setText(trainPathPart.railDailyTimetable.DailyTrainInfo.TrainNo);
                ((TextView)holder.mView.findViewById(R.id.trainTypeTextView)).setText(trainPathPart.destinationStation.OperatorID.equals(API.TRA) ? trainPathPart.railDailyTimetable.DailyTrainInfo.TrainTypeName.Zh_tw : "高鐵");
                ((ImageView)holder.mView.findViewById(R.id.trainIconImageView)).setImageResource((trainPathPart.originStation.OperatorID.equals("THSR") ? R.drawable.thsr : R.drawable.tra));
            } else {
                if (position == 0) {
                    holder.mView.findViewById(R.id.leftArrowTextView).setVisibility(View.GONE);
                }
                ((TextView) holder.mView.findViewById(R.id.trainNoTextView)).setText(trainPathPart.railDailyTimetable.DailyTrainInfo.TrainNo);
                ((ImageView) holder.mView.findViewById(R.id.trainIconImageView)).setImageResource((trainPathPart.originStation.OperatorID.equals("THSR") ? R.drawable.thsr : R.drawable.tra));
            }
        }

        @Override
        public int getItemCount() {
            return trainPathPartList.size();
        }
    }
}

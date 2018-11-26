package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ShowResult extends AppCompatActivity {
    private TextView textView8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        textView8 = (TextView) findViewById(R.id.textView8);

        Bundle bundle = getIntent().getExtras();
        String str = bundle.getString("mystring");

        textView8.setText(str);

        Data[] showresult = new Data[10];
        for(int i = 0; i<showresult.length; i++){
            showresult[i] = new Data();
        }

        for(int i = 0; i<showresult.length; i++){
            showresult[i].startStation = "起站" + i;
            showresult[i].arriveStation = "到站" + i;
            showresult[i].startTime = "起站時間" + i;
            showresult[i].arriveTime = "到站時間" + i;
        }

        ListView show_result = (ListView)findViewById(R.id.listView);

        myAdapter transAdapter = new myAdapter(showresult,R.layout.show_result_listview_item);
        show_result.setAdapter(transAdapter);

    }

    class Data{
        String startStation;
        String arriveStation;
        String startTime;
        String arriveTime;
    }

    public  class myAdapter extends BaseAdapter {
        private Data[] data;
        private int view;

        public myAdapter(Data[] data, int view){
            this.data = data;
            this.view = view;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Data getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(view, parent, false);
            TextView textView9 = (TextView) convertView.findViewById(R.id.textView9);
            textView9.setText(data[position].startStation);
            TextView textView10 = (TextView) convertView.findViewById(R.id.textView10);
            textView10.setText(data[position].arriveStation);
            TextView textView11 = (TextView) convertView.findViewById(R.id.textView11);
            textView11.setText(data[position].startTime);
            TextView textView12 = (TextView) convertView.findViewById(R.id.textView12);
            textView12.setText(data[position].arriveTime);
            return convertView;
        }
    }

}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

//https://github.com/ptxmotc/Sample-code
//https://ptx.transportdata.tw/PTX/Topic/fbeac0a2-fc53-4ffa-8961-597b2d3e6bdd

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String XMLUrl = "https://ptx.transportdata.tw/MOTC/v2/Rail/TRA/Station?$top=30&$format=xml";
    private final static String APIUrl = "http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/Station?$top=30&$format=JSON";

    private TextView textView, textView2, textView3, textView4, textView5;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private CheckBox checkBox2;

    private String startStationText;
    private String arriveStationText;
    private int railOrTHSR = 0;
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


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton:
                        railOrTHSR = 1;
                        break;
                    case R.id.radioButton2:
                        railOrTHSR = 2;
                        break;
                    case R.id.radioButton3:
                        railOrTHSR = 3;
                        break;
                    default:
                        railOrTHSR = 0;
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


        RailStationTask railStationTask = new RailStationTask(APIUrl);
        List<RailStation> railStations = railStationTask.runAsyncTask();

        String[] stationName = new String[railStations.size()];
        for (int i = 0; i < stationName.length; i++) {
            stationName[i] = railStations.get(i).StationName.Zh_tw;
        }

        Spinner start_station = (Spinner)findViewById(R.id.start_station);
        final Spinner arrive_station = (Spinner)findViewById(R.id.arrive_station);

        myAdapter transAdapter = new myAdapter(stationName,R.layout.spinner_text);
        start_station.setAdapter(transAdapter);
        arrive_station.setAdapter(transAdapter);

        start_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView name = (TextView) view.findViewById(R.id.name);
                startStationText = name.getText().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        arrive_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView name = (TextView) view.findViewById(R.id.name);
                arriveStationText = name.getText().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
/*
                HttpURLConnection connection = null;
                //申請的APPID
                //（FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF 為 Guest 帳號，以IP作為API呼叫限制，請替換為註冊的APPID & APPKey）
                String APPID = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
                //申請的APPKey
                String APPKey = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";

                //取得當下的UTC時間，Java8有提供時間格式DateTimeFormatter.RFC_1123_DATE_TIME
                //但是格式與C#有一點不同，所以只能自行定義
                String xdate = getServerTime();
                String SignDate = "x-date: " + xdate;


                String Signature = "";
                try {
                    //取得加密簽章
                    Signature = HMAC_SHA1.Signature(SignDate, APPKey);
                } catch (SignatureException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                System.out.println("Signature :" + Signature);
                String sAuth = "hmac username=\"" + APPID + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"" + Signature + "\"";
                System.out.println(sAuth);

                try {
                    URL url = new URL(this.url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", sAuth);
                    connection.setRequestProperty("x-date", xdate);
                    connection.setRequestProperty("Accept-Encoding", "gzip");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //將InputStream轉換為Byte
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    byte[] buff = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(buff)) != -1) {
                        bao.write(buff, 0, bytesRead);
                    }

                    //解開GZIP
                    ByteArrayInputStream bais = new ByteArrayInputStream(bao.toByteArray());
                    GZIPInputStream gzis = new GZIPInputStream(bais);
                    InputStreamReader reader = new InputStreamReader(gzis);
                    BufferedReader in = new BufferedReader(reader);

                    //讀取回傳資料
                    String line, response = "";
                    while ((line = in.readLine()) != null) {
                        response += (line + "\n");
                    }

                    Type RailStationListType = new TypeToken<ArrayList<RailStation>>() {
                    }.getType();
                    Gson gsonReceiver = new Gson();
                    List<RailStation> obj = gsonReceiver.fromJson(response, RailStationListType);
                    System.out.println(response);

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
 */
package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

public class TRAAPI {
    private final String APPID = "6066d2cbc3324183bbaf01e2515df9df";

    private final String APPKey = "CphTjey0dfL8Hqz1O7kdHq34GEY";

    @SuppressLint("StaticFieldLeak")
    AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
        @Override
        protected String doInBackground(String... APIUrl) {
            HttpURLConnection connection = null;

            String xdate = getServerTime();
            String SignDate = "x-date: " + xdate;

            String Signature = "";

            try {
                Signature = HMAC_SHA1.Signature(SignDate, APPKey);
            } catch (SignatureException e1) {
                e1.printStackTrace();
            }

            System.out.println("Signature :" + Signature);
            String sAuth = "hmac username=\"" + APPID + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"" + Signature + "\"";
            System.out.println(sAuth);

            try {
                URL url = new URL(APIUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", sAuth);
                connection.setRequestProperty("x-date", xdate);
                connection.setRequestProperty("Accept-Encoding", "gzip");
                connection.setDoInput(true);
                //connection.setDoOutput(true);

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
                return response;

            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    };

    public TRAAPI(String APIUrl) {
        task.execute(APIUrl);
    }

    private String getAPIResponse() {
        String result = "";
        try {
            result = task.get();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<RailStation> getRailStation() {
        TRAAPI getAPI = (new TRAAPI("http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/Station?format=JSON"));
        return (new Gson()).fromJson(getAPI.getAPIResponse(), new TypeToken<List<RailStation>>() {}.getType());
    }

    public static List<RailGeneralTimetable> getRailGeneralTimetable() {
        TRAAPI getAPI = (new TRAAPI("http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/GeneralTimetable?format=JSON"));
        return (new Gson()).fromJson(getAPI.getAPIResponse(), new TypeToken<List<RailGeneralTimetable>>() {}.getType());
    }

    public static List<RailODFare> getRailODFare(String originStationID, String destinationStationID) {
        TRAAPI getAPI = (new TRAAPI("http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/ODFare/" + originStationID + "/to/" + destinationStationID + "?format=JSON"));
        return (new Gson()).fromJson(getAPI.getAPIResponse(), new TypeToken<List<RailODFare>>() {}.getType());
    }

    public static List<RailODFare> getRailODFare(RailStation originStation, RailStation destinationStation) {
        TRAAPI getAPI = (new TRAAPI("http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/ODFare/" + originStation.StationID + "/to/" + destinationStation.StationID + "?format=JSON"));
        return (new Gson()).fromJson(getAPI.getAPIResponse(), new TypeToken<List<RailODFare>>() {}.getType());
    }

    public static List<RailGeneralTrainInfo> getRailGeneralTrainInfo() {
        TRAAPI getAPI = (new TRAAPI("http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/GeneralTrainInfo?format=JSON"));
        return (new Gson()).fromJson(getAPI.getAPIResponse(), new TypeToken<List<RailGeneralTrainInfo>>() {}.getType());
    }

    public static List<RailODDailyTimetable> getRailODDailyTimetable(String originStationID, String destinationStationID, String TrainDate) {
        TRAAPI getAPI = (new TRAAPI("http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/DailyTimetable/OD/" + originStationID + "/to/" + destinationStationID + "/" + TrainDate + "?$format=JSON"));
        return (new Gson()).fromJson(getAPI.getAPIResponse(), new TypeToken<List<RailODDailyTimetable>>() {}.getType());
    }

    public static List<RailODDailyTimetable> getRailODDailyTimetable(RailStation originStation, RailStation destinationStation, String trainDate) {
        TRAAPI getAPI = (new TRAAPI("http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/DailyTimetable/OD/" + originStation.StationID + "/to/" + destinationStation.StationID + "/" + trainDate + "?$format=JSON"));
        return (new Gson()).fromJson(getAPI.getAPIResponse(), new TypeToken<List<RailODDailyTimetable>>() {}.getType());
    }

    //取得當下UTC時間
    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

public class API {
    public final static String TRA = "TRA";
    public final static String THSR = "THSR";
    public final static String TRA_AND_THSR = "TRA_AND_THSR";

    private final static String APPID = "6066d2cbc3324183bbaf01e2515df9df";
    private final static String APPKey = "CphTjey0dfL8Hqz1O7kdHq34GEY";

    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat updateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat timeFormat2 = new SimpleDateFormat("HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static String getAPIResponse(String domain, String service, String application, String queryOpyions) throws SignatureException, IOException {
        String APIUrl = "http://ptx.transportdata.tw/MOTC/v2/" + domain + '/' + service + '/' + application + '?' + queryOpyions + "$format=JSON";
        HttpURLConnection connection;

        String xdate = getServerTime();
        String SignDate = "x-date: " + xdate;

        String Signature = HMAC_SHA1.Signature(SignDate, APPKey);

        String sAuth = "hmac username=\"" + APPID + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"" + Signature + "\"";

        URL url = new URL(APIUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", sAuth);
        connection.setRequestProperty("x-date", xdate);
        connection.setRequestProperty("Accept-Encoding", "gzip");
        connection.setDoInput(true);

        //將InputStream轉換為Byte
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buff)) != -1) {
            bao.write(buff, 0, bytesRead);
        }

        //解開GZIP
        ByteArrayInputStream bais = new ByteArrayInputStream(bao.toByteArray());
        GZIPInputStream gzis = new GZIPInputStream(bais);
        InputStreamReader reader = new InputStreamReader(gzis);
        BufferedReader in = new BufferedReader(reader);

        //讀取回傳資料
        String line;StringBuilder response = new StringBuilder();
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        return response.toString();
    }

    public static List<RailStation> getStation(String transportation) throws SignatureException, IOException {
        if(transportation.equals(TRA_AND_THSR)) {
            List<RailStation> railStationList_TRA = getStation(TRA);
            List<RailStation> railStationList_THSR = getStation(THSR);
            railStationList_TRA.addAll(railStationList_THSR);
            return railStationList_TRA;
        } else {
            String application = "Station";
            return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application, ""), new TypeToken<List<RailStation>>() {}.getType());
        }
    }

    public static List<RailStation> getStation(String transportation, Date updateTime) throws SignatureException, IOException {
        if(transportation.equals(TRA_AND_THSR)) {
            List<RailStation> railStationList_TRA = getStation(TRA, updateTime);
            List<RailStation> railStationList_THSR = getStation(THSR, updateTime);
            railStationList_TRA.addAll(railStationList_THSR);
            return railStationList_TRA;
        } else {
            String application = "Station";
            String queryOpyions = "$filter=date(UpdateTime) ge " + API.dateFormat.format(updateTime) + " and time(UpdateTime) gt " + API.timeFormat2.format(updateTime) + "&";
            return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application, queryOpyions), new TypeToken<List<RailStation>>() {}.getType());
        }
    }

    //Line
    public static List<Line> getLine() throws SignatureException, IOException {
        String application = "Line";
        return (new Gson()).fromJson(getAPIResponse("Rail", TRA, application, ""), new TypeToken<List<Line>>() {}.getType());
    }

    //StationOfLine
    public static List<StationOfLine> getStationOfLine() throws SignatureException, IOException {
        String application = "StationOfLine";
        return (new Gson()).fromJson(getAPIResponse("Rail", TRA, application,""), new TypeToken<List<StationOfLine>>() {}.getType());
    }

    //StationOfLine
    public static List<StationOfLine> getStationOfLine(Date updateTime) throws SignatureException, IOException {
        String application = "StationOfLine";
        String queryOpyions = "$filter=date(UpdateTime) ge " + API.dateFormat.format(updateTime) + " and time(UpdateTime) gt " + API.timeFormat2.format(updateTime) + "&";
        return (new Gson()).fromJson(getAPIResponse("Rail", TRA, application, queryOpyions), new TypeToken<List<StationOfLine>>() {}.getType());
    }

    public static List<TrainType> getTrainType() throws SignatureException, IOException {
        String application = "TrainType";
        return (new Gson()).fromJson(getAPIResponse("Rail", TRA, application,""), new TypeToken<List<TrainType>>() {}.getType());
    }

    public static List<TRAShape> getTRAShape(String transportation) throws SignatureException, IOException {
        String application = "Shape";
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<TRAShape>>() {}.getType());
    }

    public static List<RailODFare> getODFare(String transportation, String originStationID, String destinationStationID) throws SignatureException, IOException {
        String application = "ODFare/" + originStationID + "/to/" + destinationStationID;
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailODFare>>() {}.getType());
    }

    public static List<RailGeneralTrainInfo> getGeneralTrainInfo(String transportation) throws SignatureException, IOException {
        String application = "GeneralTrainInfo";
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailGeneralTrainInfo>>() {}.getType());
    }

    public static List<RailGeneralTrainInfo> getGeneralTrainInfo(String transportation, String TrainTypeID) throws SignatureException, IOException {
        String application = "GeneralTrainInfo/TrainNo/" + TrainTypeID;
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailGeneralTrainInfo>>() {}.getType());
    }

    public static List<RailGeneralTimetable> getGeneralTimetable(String transportation) throws SignatureException, IOException {
        String application = "GeneralTimetable";
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailGeneralTimetable>>() {}.getType());
    }

    public static List<RailGeneralTimetable> getGeneralTimetable(String transportation, Date updateTime) throws SignatureException, IOException {
        String application = "GeneralTimetable";
        String queryOpyions = "$filter=date(UpdateTime) ge " + API.dateFormat.format(updateTime) + " and time(UpdateTime) gt " + API.timeFormat2.format(updateTime) + "&";
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,queryOpyions), new TypeToken<List<RailGeneralTimetable>>() {}.getType());
    }

    public static List<RailGeneralTimetable> getGeneralTimetable(String transportation, String TrainTypeID) throws SignatureException, IOException {
        String application = "GeneralTimetable/TrainNo/" + TrainTypeID;
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailGeneralTimetable>>() {}.getType());
    }

    public static List<RailDailyTimetable> getDailyTimetableToday(String transportation) throws SignatureException, IOException {
        String application = "DailyTimetable/Today";
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailDailyTimetable>>() {}.getType());
    }

    public static List<RailDailyTimetable> getDailyTimetableTodayByTrainNo(String transportation, String trainNo) throws SignatureException, IOException {
        String application = "DailyTimetable/Today/TrainNo/" + trainNo;
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailDailyTimetable>>() {}.getType());
    }

    public static List<RailDailyTimetable> getDailyTimetableByTrainDate(String transportation, String trainDate) throws SignatureException, IOException {
        String application = "DailyTimetable/TrainDate/" + trainDate;
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailDailyTimetable>>() {}.getType());
    }

    public static List<RailDailyTimetable> getDailyTimetableByTrainDate(String transportation, String trainDate, Date updateTime) throws SignatureException, IOException {
        String application = "DailyTimetable/TrainDate/" + trainDate;
        String queryOpyions = "$filter=date(UpdateTime) ge " + API.dateFormat.format(updateTime) + " and time(UpdateTime) gt " + API.timeFormat2.format(updateTime) + "&";
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,queryOpyions), new TypeToken<List<RailDailyTimetable>>() {}.getType());
    }

    public static List<RailDailyTimetable> getDailyTimetable(String transportation, String trainNo, String trainDate) throws SignatureException, IOException {
        String application = "DailyTimetable/TrainNo/" + trainNo + "/TrainDate/" + trainDate;
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailDailyTimetable>>() {}.getType());
    }

    public static List<RailStationTimetable> getStationTimetable(String transportation, String stationID, String trainDate) throws SignatureException, IOException {
        String application = "DailyTimetable/Station/" + stationID + "/" + trainDate;
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailStationTimetable>>() {}.getType());
    }

    public static List<RailODDailyTimetable> getODDailyTimetable(String transportation, String originStationID, String destinationStationID, String TrainDate) throws SignatureException, IOException {
        String application = "DailyTimetable/OD/" + originStationID + "/to/" + destinationStationID + "/" + TrainDate;
        return (new Gson()).fromJson(getAPIResponse("Rail", transportation, application,""), new TypeToken<List<RailODDailyTimetable>>() {}.getType());
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

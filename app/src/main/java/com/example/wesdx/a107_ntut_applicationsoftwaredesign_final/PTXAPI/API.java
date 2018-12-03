package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

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
import java.util.zip.GZIPInputStream;

class APIURL {
    private String preFix;
    public String transportation;
    public String function;
    private String postFix;

    public APIURL() {
        preFix = "http://ptx.transportdata.tw/MOTC/v2/Rail/";
        transportation = "TRA / THSR";
        function = "Station / GeneralTimetable / ODFare/OS/to/DS / GeneralTrainInfo / DailyTimetable/OS/to/DS";
        postFix = "?format=JSON";
    }

    public APIURL(String transportation, String function) {
        this.preFix = "http://ptx.transportdata.tw/MOTC/v2/Rail/";
        this.transportation =transportation;
        this.function = function;
        this.postFix = "?format=JSON";
    }

    public String get() {
        return preFix + transportation + '/' + function + postFix;
    }
}

public class API {
    public final static String TRA = "TRA";
    public final static String THSR = "THSR";
    public final static int TRAIN_NO = 1;
    public final static int TRAIN_DATE = 2;

    private final static String APPID = "6066d2cbc3324183bbaf01e2515df9df";
    private final static String APPKey = "CphTjey0dfL8Hqz1O7kdHq34GEY";

    private final static String getAPIResponse(String APIUrl) {
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
            URL url = new URL(APIUrl);
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

    //Network

    public static List<RailStation> getStation(String transportation) {
        APIURL apiurl = new APIURL(transportation, "Station");
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailStation>>() {}.getType());
    }

    //Line
    public static List<Line> getLine(String transportation) {
        APIURL apiurl = new APIURL(transportation, "Line");
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<Line>>() {}.getType());
    }

    //StationOfLine
    public static List<StationOfLine> getStationOfLine(String transportation) {
        APIURL apiurl = new APIURL(transportation, "StationOfLine");
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<StationOfLine>>() {}.getType());
    }

    //TrainType
    public static List<TrainType> getTrainType(String transportation) {
        APIURL apiurl = new APIURL(transportation, "TrainType");
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<TrainType>>() {}.getType());
    }

    //Shape
    public static List<TRAShape> getTRAShape(String transportation) {
        APIURL apiurl = new APIURL(transportation, "Shape");
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<TRAShape>>() {}.getType());
    }

    public static List<RailODFare> getODFare(String transportation, String originStationID, String destinationStationID) {
        APIURL apiurl = new APIURL(transportation, "ODFare/" + originStationID + "/to/" + destinationStationID);
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailODFare>>() {}.getType());
    }

    public static List<RailODFare> getRailODFare(String transportation, RailStation originStation, RailStation destinationStation) {
        return getODFare(transportation, originStation.StationID, destinationStation.StationID);
    }

    public static List<RailGeneralTrainInfo> getGeneralTrainInfo(String transportation, APIURL apiurl) {
        if(transportation == THSR) return null;
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailGeneralTrainInfo>>() {}.getType());
    }

    public static List<RailGeneralTrainInfo> getGeneralTrainInfo(String transportation) {
        APIURL apiurl = new APIURL(transportation, "GeneralTrainInfo");
        return getGeneralTrainInfo(transportation, apiurl);
    }

    public static List<RailGeneralTrainInfo> getGeneralTrainInfo(String transportation, String TrainTypeID) {
        APIURL apiurl = new APIURL(transportation, "GeneralTrainInfo/TrainNo/" + TrainTypeID);
        return getGeneralTrainInfo(transportation, apiurl);
    }

    public static List<RailGeneralTimetable> getGeneralTimetable(String transportation) {
        APIURL apiurl = new APIURL(transportation, "GeneralTimetable");
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailGeneralTimetable>>() {}.getType());
    }

    public static List<RailGeneralTimetable> getGeneralTimetable(String transportation, String TrainTypeID) {
        APIURL apiurl = new APIURL(transportation, "GeneralTimetable/TrainNo/" + TrainTypeID);
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailGeneralTimetable>>() {}.getType());
    }

    //DailyTrainInfo/Today
    //DailyTrainInfo/Today/TrainNo/{TrainNo}
    //DailyTrainInfo/TrainDate/{TrainDate}
    //DailyTrainInfo/TrainNo/{TrainNo}/TrainDate{TrainDate}

    public static List<RailDailyTimetable> getDailyTimetable(String transportation, String functionParameter) {
        APIURL apiurl = new APIURL(transportation, "DailyTimetable/" + functionParameter);
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailDailyTimetable>>() {}.getType());
    }

    public static List<RailDailyTimetable> getDailyTimetable(String transportation) {
        String functionParameter = "Today";
        return getDailyTimetable(transportation, functionParameter);
    }

    public static List<RailDailyTimetable> getDailyTimetable(String transportation, int serachBy, String key) {
        String functionParameter = "";
        if(serachBy == TRAIN_NO) {
            functionParameter = "Today/TrainNo/" + key;
        } else if(serachBy == TRAIN_DATE){
            functionParameter = "TrainDate/" + key;
        }
        return getDailyTimetable(transportation, functionParameter);
    }

    public static List<RailDailyTimetable> getDailyTimetable(String transportation, String trainNo, String trainDate) {
        String functionParameter = "TrainNo/" + trainNo + "/TrainDate/" + trainDate;
        APIURL apiurl = new APIURL(transportation, "DailyTimetable/" + functionParameter);
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailDailyTimetable>>() {}.getType());
    }

    public static List<RailStationTimetable> getStationTimetable(String transportation, String stationID, String trainDate) {
        String functionParameter = "Station/" + stationID + "/" + trainDate;
        APIURL apiurl = new APIURL(transportation, "DailyTimetable/" + functionParameter);
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailStationTimetable>>() {}.getType());
    }

    public static List<RailODDailyTimetable> getODDailyTimetable(String transportation, String originStationID, String destinationStationID, String TrainDate) {
        String functionParameter = "OD/" + originStationID + "/to/" + destinationStationID + "/" + TrainDate;
        APIURL apiurl = new APIURL(transportation, "DailyTimetable/" + functionParameter);
        return (new Gson()).fromJson(getAPIResponse(apiurl.get()), new TypeToken<List<RailDailyTimetable>>() {}.getType());
    }

    public static List<RailODDailyTimetable> getODDailyTimetable(String transportation, RailStation originStation, RailStation destinationStation, String trainDate) {
        return getODDailyTimetable(transportation, originStation.StationID, destinationStation.StationID, trainDate);
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

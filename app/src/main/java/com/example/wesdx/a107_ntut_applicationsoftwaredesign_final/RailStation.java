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
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

/**
 * 台鐵車站基本資料
 * 
 * @author Lin
 *
 */

public class RailStation  implements Comparable<RailStation> {
    public String StationID;
	public Zh_tw_En StationName;
	public StationPositionC StationPosition;
    public String StationAddress;
    public String StationPhone;
    public String StationClass;
    public String ReservationCode;
    public String UpdateTime;
    public String VersionID;

    public static void removeUnreservationStation(List<RailStation> list)
    {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).ReservationCode == null)
            {
                list.remove(i);
                i--;
            }
        }
    }
    @Override
    public int compareTo(RailStation f) {

        if (Integer.parseInt(ReservationCode) > Integer.parseInt(f.ReservationCode)) {
            return 1;
        }
        else if (Integer.parseInt(ReservationCode) < Integer.parseInt(f.ReservationCode)) {
            return -1;
        }
        else {
            return 0;
        }

    }
}

class Zh_tw_En
{
    public String Zh_tw;
    public String En;
}
class StationPositionC
{
    public String PositionLon;
    public String PositionLat;
}

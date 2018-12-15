package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.Router;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StationOfLine {
    public String LineNo;
    public String LineID;
    public List<LineStation> Stations;
    public String UpdateTime;

    public StationOfLine(StationOfLine obj) {
        LineNo = obj.LineNo;
        LineID = obj.LineID;
        Stations = new ArrayList<>(obj.Stations);
        UpdateTime = obj.UpdateTime;
    }

    public final static String E_EL = "E-EL";    //東部幹線  八堵-臺東
    public final static String B_PX = "B-PX";    //平溪線   三貂嶺-菁桐
    public final static String B_SA = "B-SA";    //深澳線   瑞芳-深澳
    public final static String B_NW = "B-NW";    //內灣線   新竹-內灣
    public final static String B_OM = "B-OM";    //舊山線   三義-后里
    public final static String B_JJ = "B-JJ";    //集集線   二水-車埕
    public final static String B_SL = "B-SL";    //沙崙線   中洲-沙崙
    public final static String B_LJ = "B-LJ";    //六家線   竹中-六家
    public final static String W_TL_N = "W-TL-N";  //  基隆-竹南
    public final static String W_TL_M = "W-TL-M";  //  竹南-彰化
    public final static String W_TL_C = "W-TL-C";  //  竹南-彰化
    public final static String W_TL_S = "W-TL-S";  //  彰化-高雄
    public final static String W_PL = "W-PL";    //屏東縣   高雄-枋寮
    public final static String S_SL = "S-SL";    //南迴線   枋寮-臺東

    public boolean isBrenchLine() {
        switch (LineNo) {
            case E_EL:
            case W_TL_N:
            case W_TL_M:
            case W_TL_C:
            case W_TL_S:
            case W_PL:
            case S_SL:
                return false;
        }
        return true;
    }

    public int getStationIndexOfStationsByID(String stationID) {
        for(int i = 0; i < Stations.size(); i++) {
            if(Stations.get(i).StationID.equals(stationID)) return i;
        }
        return -1;
    }

    public static StationOfLine getStationIndexOfStationsByLineNo(List<StationOfLine> stationOfLineList, String lineNo) {
        for(StationOfLine stationOfLine:stationOfLineList) {
            if(stationOfLine.LineNo.equals(lineNo)) {
                return stationOfLine;
            }
        }
        return null;
    }

    public static StationOfLine getStationOfLineByLineNo(List<StationOfLine> stationOfLineList, String lineNo) {
        for(StationOfLine stationOfLine:stationOfLineList) {
            if(stationOfLine.LineNo.equals(lineNo)) return stationOfLine;
        }
        return null;
    }

    public static StationOfLine getStationOfLine(List<StationOfLine> stationOfLineList, String stationID) {
        StationOfLine stationOfLine_new = null;
        for(StationOfLine stationOfLine:stationOfLineList) {
            for(LineStation lineStation:stationOfLine.Stations) {
                if(lineStation.StationID.equals(stationID)) {
                    if(stationOfLine_new == null) stationOfLine_new = new StationOfLine(stationOfLine);
                    else {
                        switch (stationOfLine_new.LineNo) {
                            case E_EL:
                            case W_TL_N:
                            case W_TL_S:
                            case W_PL:
                            case S_SL:
                                continue;
                        }
                        switch (stationOfLine.LineNo) {
                            case E_EL:
                            case W_TL_N:
                            case W_TL_S:
                            case W_PL:
                            case S_SL:
                            case W_TL_C:
                            case W_TL_M:
                                stationOfLine_new = new StationOfLine(stationOfLine);
                        }
                    }
                }
            }
        }
        return stationOfLine_new;
    }

    public static void fixMissing15StationProblem(List<StationOfLine> stationOfLineList) throws Router.RouterException {
        StationOfLine B_NW_StationOfLine = StationOfLine.getStationOfLineByLineNo(stationOfLineList, StationOfLine.B_NW);
        if(B_NW_StationOfLine == null) throw new Router.RouterException("stationOfLineList illegal");

        for(StationOfLine stationOfLine:stationOfLineList) {
            switch (stationOfLine.LineNo) {
                case StationOfLine.B_LJ:
                    for (int j = 0; j < 3; j++) {
                        stationOfLine.Stations.add(j, B_NW_StationOfLine.Stations.get(j));
                    }
                    break;
                case StationOfLine.E_EL:
                    for (int j = 0; j < stationOfLine.Stations.size(); j++) {
                        if (stationOfLine.Stations.get(j).StationName.equals("豐田")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "林榮新光";
                            lineStation.StationID = "1608";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                    }
                    break;
                case StationOfLine.W_TL_N:
                    for (int j = 0; j < stationOfLine.Stations.size(); j++) {
                        if (stationOfLine.Stations.get(j).StationName.equals("樹林")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "南樹林";
                            lineStation.StationID = "1034";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("富岡")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "新富";
                            lineStation.StationID = "1036";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                    }
                    break;
                case StationOfLine.W_TL_M:
                    for (int j = 0; j < stationOfLine.Stations.size(); j++) {
                        if (stationOfLine.Stations.get(j).StationName.equals("豐原")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "栗林";
                            lineStation.StationID = "1325";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("潭子")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "頭家厝";
                            lineStation.StationID = "1326";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("頭家厝")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "松竹";
                            lineStation.StationID = "1327";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("太原")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "精武";
                            lineStation.StationID = "1328";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("臺中")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "五權";
                            lineStation.StationID = "1329";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                    }
                    break;
                case StationOfLine.W_TL_S:
                    for (int j = 0; j < stationOfLine.Stations.size(); j++) {
                        if (stationOfLine.Stations.get(j).StationName.equals("左營")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "內惟";
                            lineStation.StationID = "1245";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("內惟")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "美術館";
                            lineStation.StationID = "1246";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("美術館")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "鼓山";
                            lineStation.StationID = "1237";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("鼓山")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "三塊厝";
                            lineStation.StationID = "1247";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("高雄")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "民族";
                            lineStation.StationID = "1419";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("民族")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "科工館";
                            lineStation.StationID = "1420";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                        if (stationOfLine.Stations.get(j).StationName.equals("科工館")) {
                            LineStation lineStation = new LineStation();
                            lineStation.StationName = "正義";
                            lineStation.StationID = "1421";
                            stationOfLine.Stations.add(j + 1, lineStation);
                        }
                    }
                    break;
            }
        }
    }

    public static Date getNewestUpdateTime(List<StationOfLine> stationOfLineList) throws ParseException {
        Date newest = null;
        for(StationOfLine stationOfLine:stationOfLineList) {
            if(stationOfLine.UpdateTime.length() < 11) continue;
            Date temp = API.updateTimeFormat.parse(stationOfLine.UpdateTime);
            if(newest == null) newest = temp;
            if (temp.after(newest)) newest = temp;
        }
        return newest;
    }

}



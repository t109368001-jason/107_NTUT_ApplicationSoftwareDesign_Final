package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.util.ArrayList;
import java.util.List;

public class StationOfLine {
   public String LineNo;
   public String LineID;
   public List<LineStation> Stations;
   public String UpdateTime;

   public StationOfLine(StationOfLine obj) {
        LineNo = new String(obj.LineNo);
        LineID = new String(obj.LineID);
        Stations = new ArrayList<>(obj.Stations);
        UpdateTime = new String(obj.UpdateTime);
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

   public static void fixB_LJProblem(List<StationOfLine> stationOfLineList) {
      StationOfLine B_NW = StationOfLine.getStationOfLineByLineNo(stationOfLineList, StationOfLine.B_NW);

      for(int i = 0; i < stationOfLineList.size(); i++) {
         if(stationOfLineList.get(i).LineNo.equals(StationOfLine.B_LJ)) {
            for(int j = 0; j < 3; j++) {
               stationOfLineList.get(i).Stations.add(j, B_NW.Stations.get(j));
            }
         }
      }
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

   public static List<StationOfLine> getStationOfLineList(List<StationOfLine> stationOfLineList, String stationID) {
      List<StationOfLine> stationOfLineList_new = null;
      for(StationOfLine stationOfLine:stationOfLineList) {
         for(LineStation lineStation:stationOfLine.Stations) {
            if(lineStation.StationID.equals(stationID)) {
               if(stationOfLineList_new == null) stationOfLineList_new = new ArrayList<>();
               stationOfLineList_new.add(stationOfLine);
            }
         }
      }
      return stationOfLineList_new;
   }

   public static StationOfLine getStationOfLine(List<StationOfLine> stationOfLineList, String stationID) {
      StationOfLine stationOfLine_new = null;
      for(StationOfLine stationOfLine:stationOfLineList) {
         for(LineStation lineStation:stationOfLine.Stations) {
            if(lineStation.StationID.equals(stationID)) {
               if(stationOfLine_new == null) stationOfLine_new = new StationOfLine(stationOfLine);
               else {
                  switch (stationOfLine.LineNo) {
                     case E_EL:
                     case W_TL_N:
                     case W_TL_M:
                     case W_TL_C:
                     case W_TL_S:
                     case W_PL:
                     case S_SL:
                        stationOfLine_new = new StationOfLine(stationOfLine);
                  }
               }
            }
         }
      }
      return stationOfLine_new;
   }

   public static List<StationOfLine> getStationOfLineList(List<StationOfLine> stationOfLineList, RailStation railStation) {
      return getStationOfLineList(stationOfLineList, railStation.StationID);
   }

   public static StationOfLine getMainLine(List<StationOfLine> stationOfLineList) {
      for(StationOfLine stationOfLine:stationOfLineList) {
         switch (stationOfLine.LineNo) {
            case E_EL:
            case W_TL_N:
            case W_TL_M:
            case W_TL_C:
            case W_TL_S:
            case W_PL:
            case S_SL:
               return stationOfLine;
         }
      }
      return null;
   }
}



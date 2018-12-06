package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 *20181125  1649  完成
 * GET /v2/Rail/THSR/DailyTimetable/Today
 *
 */
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RailDailyTimetable {
  public String TrainDate;
  public RailDailyTrainInfo DailyTrainInfo;
  public List<StopTime> StopTimes;
  public String  UpdateTime;
  public String  VersionID;

  public static List<RailDailyTimetable> filterByOD(List<RailDailyTimetable> railDailyTimetableList, RailStation originStation, RailStation destinationStation, boolean isDirectional) {//選擇方向性且一定要經過起站到站的車次
    List<RailDailyTimetable> railDailyTimetableList_new = null;

    for(RailDailyTimetable railDailyTimetable:railDailyTimetableList) {
      boolean findBeg = false;
      boolean findEnd = false;
      for(int i = 0; i < railDailyTimetable.StopTimes.size(); i++) {
        if(railDailyTimetable.StopTimes.get(i).StationID.equals(destinationStation.StationID)) {
          if((!findBeg)&&isDirectional) {
            break;
          }
          findEnd = true;
        }
        if(railDailyTimetable.StopTimes.get(i).StationID.equals(originStation.StationID)) {
          findBeg = true;
        }
        if(findBeg&&findEnd) {
          if(railDailyTimetableList_new == null) railDailyTimetableList_new = new ArrayList<>();
          railDailyTimetableList_new.add(railDailyTimetable);
          break;
        }
      }
    }
    return railDailyTimetableList_new;
  }

  public static List<RailDailyTimetable> filterByPath(List<RailDailyTimetable> railDailyTimetableList, List<RailStation> railStationList, boolean isDirectional, int stopTimes) {//根據有方向性的路徑去設定列車要怎麼經過以及要經過幾站
    List<RailDailyTimetable> railDailyTimetableList_new = null;
    for(RailDailyTimetable railDailyTimetable:railDailyTimetableList) {
      int railStationStartIndex = 0;
      boolean findStart = false;
      int totalStopTimes = 0;
      boolean next = false;
      for(int i = 0; i < railDailyTimetable.StopTimes.size(); i++) {
        for(int j = railStationStartIndex; j < railStationList.size(); j++) {
          if(railDailyTimetable.StopTimes.get(i).StationID.equals(railStationList.get(j).StationID)) {
            totalStopTimes += 1;
            if(!findStart) {
              findStart = true;
              if(isDirectional) {
                railStationStartIndex = j + 1;
              }
            }
            if(totalStopTimes >= stopTimes) {
              if(railDailyTimetableList_new == null) railDailyTimetableList_new = new ArrayList<>();
              railDailyTimetableList_new.add(railDailyTimetable);
              next = true;
              break;
            }
          }
        }
        if(next) break;
      }
    }
    return railDailyTimetableList_new;
  }

  public boolean afterOverNightStation(String stationID) {//判斷該站有沒有過夜，
    if(DailyTrainInfo.OverNightStationID == null) return false;
    for(StopTime stopTime:StopTimes) {
      if(stopTime.StationID.equals(stationID)) return false;
      if(stopTime.StationID.equals(DailyTrainInfo.OverNightStationID)) return true;
    }
    return false;
  }

  public StopTime findStopTime(List<RailStation> railStationList) {//給路徑，判斷該火車經過該路徑的第一站，回傳停靠資訊
    for(int i = 0; i < this.StopTimes.size(); i++) {
      for(int j = 0; j < railStationList.size(); j++) {
        if(this.StopTimes.get(i).StationID.equals(railStationList.get(j).StationID)) {
          return this.StopTimes.get(i);
        }
      }
    }
    return null;
  }

  public StopTime findLastStopTime(List<RailStation> railStationList) {//給路徑，判斷該火車經過該路徑的最後一站，回傳停靠資訊
    for(int i = (this.StopTimes.size()-1); i >= 0; i--) {
      for(int j = (railStationList.size()-1); j >= 0; j--) {
        if(this.StopTimes.get(i).StationID.equals(railStationList.get(j).StationID)) {
          return this.StopTimes.get(i);
        }
      }
    }
    return null;
  }

  public StopTime getStopTimeOfStopTimes(RailStation railStation) {//給站，回傳停靠資訊
    for(int i = 0; i < this.StopTimes.size(); i++) {
      if(StopTimes.get(i).StationID.equals(railStation.StationID)) {
        return StopTimes.get(i);
      }
    }
    return null;
  }

  public StopTime getStopTimeOfStopTimes(String StationID) {//給StationID，回傳停靠資訊
    for(int i = 0; i < this.StopTimes.size(); i++) {
      if(StopTimes.get(i).StationID.equals(StationID)) {
        return StopTimes.get(i);
      }
    }
    return null;
  }

  public String getTripLineName() {
    if(DailyTrainInfo.TripLine == null) return "";
    switch(DailyTrainInfo.TripLine) {
      case "0":
        return "";
      case "1":
        return "山線";
      case "2":
        return "海線";
    }
    return "";
  }

  public Date getODTime(RailStation originStation, RailStation destinationStation) {//給起站終站，取得起終站的行經時間
    try {
      Date originTime = API.timeFormat.parse(this.getStopTimeOfStopTimes(originStation).DepartureTime);
      Date destinationTime = API.timeFormat.parse(this.getStopTimeOfStopTimes(destinationStation).ArrivalTime);
      Date total = new Date(destinationTime.getTime() - originTime.getTime());
      total.setHours(total.getHours() - TimeZone.getDefault().getRawOffset()/1000/60/60);
      return total;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static List<RailDailyTimetable> filter(List<RailDailyTimetable> railDailyTimetableList, RailStation originStation, RailStation destinationStation) {
    List<RailDailyTimetable> railDailyTimetableList1_new = new ArrayList<>();

    for(int i = 0; i < railDailyTimetableList.size(); i++) {
      boolean originFind = false;
      for(int j = 0; j < railDailyTimetableList.get(i).StopTimes.size(); j++) {
        if(!originFind) {
          if(railDailyTimetableList.get(i).StopTimes.get(j).StationID.equals(originStation.StationID)) {
            originFind = true;
          }
        } else {
          if(railDailyTimetableList.get(i).StopTimes.get(j).StationID.equals(destinationStation.StationID)) {
            railDailyTimetableList1_new.add(railDailyTimetableList.get(i));
            break;
          }
        }
      }
    }

    return railDailyTimetableList1_new;
  }

  public static List<RailDailyTimetable> filter(List<RailDailyTimetable> railDailyTimetableList, RailStation originStation, RailStation destinationStation, String startTime /*HH:mm*/, String lessThanEndTimeOfHours) {
    List<RailDailyTimetable> railDailyTimetableList1_new = new ArrayList<>();
    Date start;
    Date endAdd;
    Date end;

    try {
      start = API.timeFormat.parse(startTime);
      endAdd = API.timeFormat.parse(lessThanEndTimeOfHours);
      if(endAdd.compareTo(API.timeFormat.parse("00:00")) == 0) {
        endAdd = API.timeFormat.parse("24:00");
      }

      for(int i = 0; i < railDailyTimetableList.size(); i++) {
        Date temp;
        temp = API.timeFormat.parse(railDailyTimetableList.get(i).getStopTimeOfStopTimes(originStation).DepartureTime);

        if(temp.after(start)) {
          railDailyTimetableList1_new.add(railDailyTimetableList.get(i));
        }
      }

      if(railDailyTimetableList1_new.size() == 0) return null;

      end = API.timeFormat.parse(railDailyTimetableList1_new.get(0).getStopTimeOfStopTimes(destinationStation).ArrivalTime);

      for(int i = 0; i < railDailyTimetableList1_new.size(); i++) {
        Date temp;
        temp = API.timeFormat.parse(railDailyTimetableList1_new.get(i).getStopTimeOfStopTimes(destinationStation).ArrivalTime);

        if(temp.before(end)&&temp.after(start)) {
          end = temp;
        }
      }

      end = new Date(end.getTime() + endAdd.getTime());

      for(int i = 0; i < railDailyTimetableList1_new.size(); i++) {
        Date temp;
        temp = API.timeFormat.parse(railDailyTimetableList1_new.get(i).getStopTimeOfStopTimes(destinationStation).ArrivalTime);

        if(temp.after(end)) {
          railDailyTimetableList1_new.remove(i);
          i--;
        }
      }

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return railDailyTimetableList1_new;
  }

  public static void sort(List<RailDailyTimetable> railDailyTimetableList, final RailStation originStation) {
    Collections.sort(railDailyTimetableList, new Comparator<RailDailyTimetable>(){
      public int compare(RailDailyTimetable obj1, RailDailyTimetable obj2) {
        try {
          if ((API.timeFormat.parse(obj1.getStopTimeOfStopTimes(originStation).DepartureTime).after(API.timeFormat.parse(obj2.getStopTimeOfStopTimes(originStation).DepartureTime)))) {
            return 1;
          }
          else if ((API.timeFormat.parse(obj1.getStopTimeOfStopTimes(originStation).DepartureTime).before(API.timeFormat.parse(obj2.getStopTimeOfStopTimes(originStation).DepartureTime)))) {
            return -1;
          }
          else {
            return 0;
          }
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return 0;
      }
    });
  }
}

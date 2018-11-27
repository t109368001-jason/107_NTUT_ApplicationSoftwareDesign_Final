package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 *20181125  1649  完成
 * GET /v2/Rail/THSR/DailyTimetable/Today
 *
 */
import android.annotation.SuppressLint;
import android.provider.ContactsContract;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RailDailyTimetable
{
  public String TrainDate;
  public RailDailyTrainInfo DailyTrainInfo;
  public List<StopTime> StopTimes;
  public String  UpdateTime;
  public String  VersionID;

  public StopTime getStopTimeOfStopTimes(RailStation railStation) {
    for(int i = 0; i < this.StopTimes.size(); i++) {
      String s = StopTimes.get(i).StationID;
      if(StopTimes.get(i).StationID.equals(railStation.StationID)) {
        return StopTimes.get(i);
      }
    }
    return null;
  }

  public String getTripLineName() {
    switch(this.DailyTrainInfo.TripLine) {
      case "0":
        return "";
      case "1":
        return "山線";
      case "2":
        return "海線";
    }
    return "";
  }

  public Date getODTime(RailStation originStation, RailStation destinationStation) {

    try {
      Date originTime = (new SimpleDateFormat("HH:mm")).parse(this.getStopTimeOfStopTimes(originStation).DepartureTime);
      Date destinationTime = (new SimpleDateFormat("HH:mm")).parse(this.getStopTimeOfStopTimes(destinationStation).ArrivalTime);
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
            //j = 0;
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
    Date start = new Date(0);
    Date endAdd = new Date(0);
    Date end = new Date(0);

    try {
      start = (new SimpleDateFormat("HH:mm")).parse(startTime);
      endAdd = (new SimpleDateFormat("HH:mm")).parse(lessThanEndTimeOfHours);
      if(endAdd.compareTo((new SimpleDateFormat("HH:mm")).parse("00:00")) == 0) {
        endAdd = (new SimpleDateFormat("HH:mm")).parse("24:00");
      }

      for(int i = 0; i < railDailyTimetableList.size(); i++) {
        Date temp = new Date(0);
        temp = (new SimpleDateFormat("HH:mm")).parse(railDailyTimetableList.get(i).getStopTimeOfStopTimes(originStation).DepartureTime);

        if(temp.after(start)) {
          railDailyTimetableList1_new.add(railDailyTimetableList.get(i));
        }
      }

      end = (new SimpleDateFormat("HH:mm")).parse(railDailyTimetableList1_new.get(0).getStopTimeOfStopTimes(destinationStation).ArrivalTime);

      for(int i = 0; i < railDailyTimetableList1_new.size(); i++) {
        Date temp = new Date(0);
        temp = (new SimpleDateFormat("HH:mm")).parse(railDailyTimetableList1_new.get(i).getStopTimeOfStopTimes(destinationStation).ArrivalTime);

        if(temp.before(end)&&temp.after(start)) {
          end = temp;
        }
      }

      end = new Date(end.getTime() + endAdd.getTime());

      for(int i = 0; i < railDailyTimetableList1_new.size(); i++) {
        Date temp = new Date(0);
        temp = (new SimpleDateFormat("HH:mm")).parse(railDailyTimetableList1_new.get(i).getStopTimeOfStopTimes(destinationStation).ArrivalTime);

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
          if ((new SimpleDateFormat("HH:mm").parse(obj1.getStopTimeOfStopTimes(originStation).DepartureTime).after((new SimpleDateFormat("HH:mm").parse(obj2.getStopTimeOfStopTimes(originStation).DepartureTime))))) {
            return 1;
          }
          else if ((new SimpleDateFormat("HH:mm").parse(obj1.getStopTimeOfStopTimes(originStation).DepartureTime).before((new SimpleDateFormat("HH:mm").parse(obj2.getStopTimeOfStopTimes(originStation).DepartureTime))))) {
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

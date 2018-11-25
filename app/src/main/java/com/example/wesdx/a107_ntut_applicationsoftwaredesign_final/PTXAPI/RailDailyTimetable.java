package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 *20181125  1649  完成
 * GET /v2/Rail/THSR/DailyTimetable/Today
 *
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RailDailyTimetable
{
  public String TrainDate;
  public RailDailyTrainInfo DailyTrainInfo;
  public List<StopTime> StopTimes;
  public String  UpdateTime;
  public String  VersionID;

  public static List<RailDailyTimetable> filter(List<RailDailyTimetable> railDailyTimetableList, RailStation originStation, RailStation destinationStation) {
    List<RailDailyTimetable> railDailyTimetableList1_new = new ArrayList<>();

    for(int i = 0; i < railDailyTimetableList.size(); i++) {
      boolean originFind = false;
      for(int j = 0; j < railDailyTimetableList.get(i).StopTimes.size(); j++) {
        if(!originFind) {
          if(railDailyTimetableList.get(i).StopTimes.get(j).StationID.equals(originStation.StationID)) {
            originFind = true;
            j = 0;
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

  public static List<RailDailyTimetable> filter(List<RailDailyTimetable> railDailyTimetableList, String startTime /*HH:mm*/, String lessThanEndTimeOfHours) {
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
        temp = (new SimpleDateFormat("HH:mm")).parse(railDailyTimetableList.get(i).StopTimes.get(0).DepartureTime);

        if(temp.after(start)) {
          railDailyTimetableList1_new.add(railDailyTimetableList.get(i));
        }
      }

      end = (new SimpleDateFormat("HH:mm")).parse(railDailyTimetableList1_new.get(0).StopTimes.get(railDailyTimetableList1_new.get(0).StopTimes.size()-1).ArrivalTime);

      for(int i = 0; i < railDailyTimetableList1_new.size(); i++) {
        Date temp = new Date(0);
        temp = (new SimpleDateFormat("HH:mm")).parse(railDailyTimetableList1_new.get(i).StopTimes.get(railDailyTimetableList1_new.get(i).StopTimes.size()-1).ArrivalTime);

        if(temp.before(end)&&temp.after(start)) {
          end = temp;
        }
      }

      end = new Date(end.getTime() + endAdd.getTime());

      for(int i = 0; i < railDailyTimetableList1_new.size(); i++) {
        Date temp = new Date(0);
        temp = (new SimpleDateFormat("HH:mm")).parse(railDailyTimetableList1_new.get(i).StopTimes.get(railDailyTimetableList1_new.get(i).StopTimes.size()-1).ArrivalTime);

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
}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 *20181125  1649  完成
 * GET /v2/Rail/THSR/DailyTimetable/Today
 *
 */
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RailDailyTimetable {
    public String TrainDate;
    public RailDailyTrainInfo DailyTrainInfo;
    public List<StopTime> StopTimes;
    public String  UpdateTime;
    public String  VersionID;

    public boolean afterOverNightStation(String stationID) {//判斷該站有沒有過夜，
        if(DailyTrainInfo.OverNightStationID == null) return false;
        for(StopTime stopTime:StopTimes) {
            if(stopTime.StationID.equals(DailyTrainInfo.OverNightStationID)) return true;
            if(stopTime.StationID.equals(stationID)) return false;
        }
        return false;
    }

    public StopTime getStopTimeOfStopTimes(String StationID) {//給StationID，回傳停靠資訊
        for(int i = 0; i < this.StopTimes.size(); i++) {
            if(StopTimes.get(i).StationID.equals(StationID)) {
                return StopTimes.get(i);
            }
        }
        return null;
    }

    public StopTime getStopTimeOfStopTimes(RailStation railStation) {//給站，回傳停靠資訊
        return getStopTimeOfStopTimes(railStation.StationID);
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

    public Date getDepartureTimeDateByStationID(String stationID) throws ParseException {
        Date time = this.getStopTimeOfStopTimes(stationID).getDepartureTimeDate();

        if(this.afterOverNightStation(stationID)) {
            time.setDate(time.getDate() + 1);
        }
        return time;
    }

    public Date getArrivalTimeDateByStationID(String stationID) throws ParseException {
        Date time = this.getStopTimeOfStopTimes(stationID).getArrivalTimeDate();

        if(this.afterOverNightStation(stationID)) {
            time.setDate(time.getDate() + 1);
        }
        return time;
    }

    public static List<RailDailyTimetable> filterByOD(List<RailDailyTimetable> railDailyTimetableList, RailStation originStation, RailStation destinationStation, Date originDepartureTime, Date destinationArrivalTime, boolean isDirectional) throws ParseException {
        List<RailDailyTimetable> railDailyTimetableList_new = null;

        for(RailDailyTimetable railDailyTimetable:railDailyTimetableList) {
            boolean findBeg = false;
            boolean findEnd = false;
            for(int i = 0; i < railDailyTimetable.StopTimes.size(); i++) {
                if(railDailyTimetable.StopTimes.get(i).StationID.equals(destinationStation.StationID)) {
                    if((!findBeg)&&isDirectional) {
                        break;
                    }
                    if(destinationArrivalTime != null) {
                        Date arrivalTime = railDailyTimetable.StopTimes.get(i).getArrivalTimeDate();
                        if(railDailyTimetable.afterOverNightStation(destinationStation.StationID)) {
                            arrivalTime.setDate(arrivalTime.getDate() + 1);
                        }
                        if (arrivalTime.after(destinationArrivalTime)) {
                            break;
                        }
                    }
                    findEnd = true;
                }
                if(railDailyTimetable.StopTimes.get(i).StationID.equals(originStation.StationID)) {
                    if(originDepartureTime != null) {
                        if (railDailyTimetable.StopTimes.get(i).getDepartureTimeDate().before(originDepartureTime)) {
                            break;
                        }
                    }
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
}

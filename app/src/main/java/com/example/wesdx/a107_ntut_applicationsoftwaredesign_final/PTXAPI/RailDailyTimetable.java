package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 *20181125  1649  完成
 * GET /v2/Rail/THSR/DailyTimetable/Today
 *
 */
import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RailDailyTimetable {
    public String TrainDate;
    public RailDailyTrainInfo DailyTrainInfo;
    public List<StopTime> StopTimes;
    public String  UpdateTime;
    public String  VersionID;

    private RailDailyTimetable(RailGeneralTimetable railGeneralTimetable, String date) throws ParseException {
        this.TrainDate = date;
        this.DailyTrainInfo = new RailDailyTrainInfo();
        this.DailyTrainInfo.TrainNo = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.TrainNo;
        this.DailyTrainInfo.Direction = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.Direction;
        this.DailyTrainInfo.StartingStationID = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.StartingStationID;
        this.DailyTrainInfo.StartingStationName = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.StartingStationName;
        this.DailyTrainInfo.EndingStationID = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.EndingStationID;
        this.DailyTrainInfo.EndingStationName = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.EndingStationName;
        //this.DailyTrainInfo.TripHeadsign = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.TripHeadsign;
        this.DailyTrainInfo.TrainTypeID = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.TrainTypeID;
        this.DailyTrainInfo.TrainTypeCode = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.TrainTypeCode;
        this.DailyTrainInfo.TrainTypeName = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.TrainTypeName;
        this.DailyTrainInfo.TripLine = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.TripLine;
        if(railGeneralTimetable.GeneralTimetable.StopTimes.get(0).ArrivalTime != null) {
            int size = railGeneralTimetable.GeneralTimetable.StopTimes.size();
            for(int i = 0; i < (size - 1); i++) {
                if(railGeneralTimetable.GeneralTimetable.StopTimes.get(i).getArrivalTimeDate().after(railGeneralTimetable.GeneralTimetable.StopTimes.get(i).getDepartureTimeDate())) {
                    this.DailyTrainInfo.OverNightStationID = railGeneralTimetable.GeneralTimetable.StopTimes.get(i).StationID;
                    break;
                }
                if(railGeneralTimetable.GeneralTimetable.StopTimes.get(i).getArrivalTimeDate().after(railGeneralTimetable.GeneralTimetable.StopTimes.get(i+1).getArrivalTimeDate())) {
                    this.DailyTrainInfo.OverNightStationID = railGeneralTimetable.GeneralTimetable.StopTimes.get(i+1).StationID;
                    break;
                }
                if(i == (size - 2)) {
                    if(railGeneralTimetable.GeneralTimetable.StopTimes.get(i+1).getArrivalTimeDate().after(railGeneralTimetable.GeneralTimetable.StopTimes.get(i+1).getDepartureTimeDate())) {
                        this.DailyTrainInfo.OverNightStationID = railGeneralTimetable.GeneralTimetable.StopTimes.get(i+1).StationID;
                        break;
                    }
                }
            }
        }
        this.DailyTrainInfo.WheelchairFlag = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.WheelchairFlag;
        this.DailyTrainInfo.PackageServiceFlag = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.PackageServiceFlag;
        this.DailyTrainInfo.DiningFlag = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.DiningFlag;
        this.DailyTrainInfo.BikeFlag = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.BikeFlag;
        this.DailyTrainInfo.BreastFeedingFlag = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.BreastFeedingFlag;
        this.DailyTrainInfo.DailyFlag = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.DailyFlag;
        //this.DailyTrainInfo.ServiceAddedFlag = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.ServiceAddedFlag;
        this.DailyTrainInfo.Note = railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.Note;
        this.StopTimes = new ArrayList<>(railGeneralTimetable.GeneralTimetable.StopTimes);
        this.UpdateTime = railGeneralTimetable.UpdateTime;
        this.VersionID = railGeneralTimetable.VersionID;
    }

    public boolean afterOverNightStation(String stationID) {//判斷該站有沒有過夜，
        if(DailyTrainInfo.OverNightStationID == null) return false;
        for(StopTime stopTime:StopTimes) {
            if(stopTime.StationID.equals(DailyTrainInfo.OverNightStationID)) return true;
            if(stopTime.StationID.equals(stationID)) return false;
        }
        return false;
    }

    private StopTime getStopTimeOfStopTimes(String StationID) {//給StationID，回傳停靠資訊
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.requireNonNull(this.getStopTimeOfStopTimes(stationID)).getDepartureTimeDate());
        if(this.afterOverNightStation(stationID)) {
            calendar.add(Calendar.HOUR_OF_DAY, 24);
        }
        return calendar.getTime();
    }

    public Date getArrivalTimeDateByStationID(String stationID) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.requireNonNull(this.getStopTimeOfStopTimes(stationID)).getArrivalTimeDate());
        if(this.afterOverNightStation(stationID)) {
            calendar.add(Calendar.HOUR_OF_DAY, 24);
        }
        return calendar.getTime();
    }

    public static Date getNewestUpdateTime(List<RailDailyTimetable> railDailyTimetableList) throws ParseException {
        Date newest = null;
        for(RailDailyTimetable railDailyTimetable:railDailyTimetableList) {
            if(railDailyTimetable.UpdateTime.length() < 11) continue;
            Date temp = API.updateTimeFormat.parse(railDailyTimetable.UpdateTime);
            if(newest == null) newest = temp;
            if (temp.after(newest)) newest = temp;
        }
        return newest;
    }

    public static void add(List<RailDailyTimetable> railDailyTimetableList, List<RailGeneralTimetable> railGeneralTimetableList, String date) throws ParseException {

        List<RailDailyTimetable> railDailyTimetableList_temp = new ArrayList<>();

        @SuppressLint("SimpleDateFormat") int dayOfWeek = Integer.parseInt(new SimpleDateFormat("u").format(new SimpleDateFormat("yyyy-MM-dd").parse(date)));

        for (RailGeneralTimetable railGeneralTimetable : railGeneralTimetableList) {
            if (railGeneralTimetable.GeneralTimetable.ServiceDay.compare(dayOfWeek)) {
                railDailyTimetableList_temp.add(new RailDailyTimetable(railGeneralTimetable, date));
            }
        }
        for (RailDailyTimetable railDailyTimetable_temp : railDailyTimetableList_temp) {
            boolean addToList = true;
            for (RailDailyTimetable railDailyTimetable_temp2 : railDailyTimetableList) {
                if (railDailyTimetable_temp.DailyTrainInfo.TrainNo.equals(railDailyTimetable_temp2.DailyTrainInfo.TrainNo)) {
                    addToList = false;
                    break;
                }
            }
            if (addToList) {
                railDailyTimetableList.add(railDailyTimetable_temp);
            }
        }
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
                        Calendar arrivalTime = Calendar.getInstance();
                        arrivalTime.setTime(railDailyTimetable.StopTimes.get(i).getArrivalTimeDate());
                        if(railDailyTimetable.afterOverNightStation(destinationStation.StationID)) {
                            arrivalTime.add(Calendar.HOUR_OF_DAY, 24);
                        }
                        if (arrivalTime.getTime().after(destinationArrivalTime)) {
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

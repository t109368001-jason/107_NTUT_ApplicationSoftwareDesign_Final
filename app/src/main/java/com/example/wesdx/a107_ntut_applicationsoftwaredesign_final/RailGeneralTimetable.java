package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import java.util.List;

/**
 * 臺鐵定期時刻表資料
 *
 * 20181120 1342 完成
 *
 */
public class RailGeneralTimetable
{
    public String UpdateTime;
    public String VersionID;
    public GeneralTimetableC GeneralTimetable;
}

class GeneralTimetableC {
    public GeneralTrainInfoC GeneralTrainInfo;
    public List<StopTimeC> StopTimes;
    public ServiceDayC ServiceDay;
    public String SrcUpdateTime;
}
class GeneralTrainInfoC{
    public String TrainNo;
    public String Direction;
    public String StartingStationID;
    Zh_tw_En StartingStationName;
    public  String EndingStationID;
    Zh_tw_En EndingStationName;
    public String TrainTypeID;
    public String TrainTypeCode;
    Zh_tw_En TrainTypeName;
    public String TripLine;
    public String WheelchairFlag;
    public String PackageServiceFlag;
    public String DiningFlag;
    public String BikeFlag;
    public String BreastFeedingFlag;
    public String DailyFlag;
    Zh_tw_En Note;
}

class StopTimeC{
    public String StopSequence;
    public String StationID;
    Zh_tw_En StationName;
    public String ArrivalTime;
    public String DepartureTime;
}

class ServiceDayC{
    public String Monday;
    public String Tuesday;
    public String Wednesday;
    public String Thursday;
    public String Friday;
    public String Saturday;
    public String Sunday;
}
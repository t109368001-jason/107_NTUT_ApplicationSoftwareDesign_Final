package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;
/**
 * 臺鐵定期時刻表資料
 *
 * 20181121 1241 完成
 *Implementation Notes
 * 取得指定[日期],[起迄站間]之站間時刻表資料
 */
public class RailODDailyTimetable {
    public String TrainDate;
    public DailyTrainInfoC DailyTrainInfo;
    public RailStopTimeC OriginStopTime;
    public RailStopTimeC DestinationStopTime;
    public String UpdateTime;
    public String VersionID;

}
class DailyTrainInfoC{
    public String TrainNo;
    public String Direction;
    public String StartingStationID;
    public Zh_tw_En StartingStationName;
    public String EndingStationID;
    public Zh_tw_En EndingStationName;
    public String TripHeadsign;
    public String TrainTypeID;
    public String TrainTypeCode;
    public Zh_tw_En TrainTypeName;
    public String TripLine;
    public String OverNightStationID;
    public String WheelchairFlag;
    public String PackageServiceFlag;
    public String DiningFlag;
    public String BikeFlag;
    public String BreastFeedingFlag;
    public String DailyFlag;
    public String ServiceAddedFlag;
    public Zh_tw_En Note;
}
class RailStopTimeC{
    public String StopSequence;
    public String StationID;
    public Zh_tw_En StationName;
    public String ArrivalTime;
    public String DepartureTime;

}

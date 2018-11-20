package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;
/**
 * 臺鐵定期時刻表資料
 *
 * 20181120 1342 完成
 *
 */
public class RailGeneralTimetable
{
    public String UpdateTime;
    public GeneralTimetableC GeneralTimetable;


}

class GeneralTimetableC {
    GeneralTrainInfoC GeneralTrainInfo;
    StopTimesC StopTimes;
    ServiceDayC ServiceDay;
    public Boolean SrcUpdateTime;         //SrcUpdateTime xsi:nil  變數宣告有省略字元
}
class GeneralTrainInfoC{
    public String TrainNo;
    public String Direction;
    public String StartingStationID;
    StartingStationNameC StartingStationName;
    public  String EndingStationID;
    EndingStationNameC EndingStationName;
    public String TrainTypeID;
    public String TrainTypeCode;
    TrainTypeNameC TrainTypeName;
    public String TripLine;
    public String WheelchairFlag;
    public String PackageServiceFlag;
    public String DiningFlag;
    public String BikeFlag;
    public String BreastFeedingFlag;
    public String DailyFlag;
    NoteC Note;
}

class StartingStationNameC{
    public String Zh_tw;
    public String En;
}
class EndingStationNameC{
    public String Zh_tw;
    public String En;
}
class TrainTypeNameC{
    public String Zh_tw;
    public String En;
}
class  NoteC{
    public String Zh_tw;
    public String En;
}

class StopTimesC{
    StopTimeC StopTime;
}
class StopTimeC{
    public String StopSequence;
    public String StationID;
    StationNameC StationName;//不重複宣告class
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
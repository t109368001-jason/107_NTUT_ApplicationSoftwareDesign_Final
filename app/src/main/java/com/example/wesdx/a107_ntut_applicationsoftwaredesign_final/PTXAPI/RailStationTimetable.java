package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 *20181125  1649  完成
 * GET /v2/Rail/TRA/DailyTimetable/Station/{StationID}/{TrainDate}
 *
 */
public class RailStationTimetable
{
   public String TrainDate;
   public String StationID;
   public NameType StationName;
   public String TrainNo;
   public String Direction;
   public String TripLine;
   public String TrainTypeID;
   public String TrainTypeCode;
   public String TrainTypeName;
   public String StartingStationID;
   public String StartingStationName;
   public String EndingStationID;
   public String EndingStationName;
   public String ArrivalTime;
   public String DepartureTime;
   public String UpdateTime;
   public String VersionID;

}

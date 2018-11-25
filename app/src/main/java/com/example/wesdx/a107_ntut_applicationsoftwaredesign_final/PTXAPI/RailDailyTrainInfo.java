package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

/**
 *20181125  1649  完成
 * GET /v2/Rail/THSR/DailyTimetable/Today
 *與GeneralTimetable類似，參考該classj完成。
 * GET /v2/Rail/TRA/DailyTimetable/Today
 *
 */
public class RailDailyTrainInfo
{
   public String TrainNo;//高鐵
   public String Direction;//高鐵
   public String StartingStationID;//高鐵
   public NameType StartingStationName;//高鐵
   public String EndingStationID;//高鐵
   public NameType EndingStationName;//高鐵
   public String TripHeadsign;//台鐵
   public String TrainTypeID;//台鐵
   public String TrainTypeCode;//台鐵
   public NameType TrainTypeName;//台鐵
   public String TripLine;//台鐵
   public String OverNightStationID//台鐵;
   public String WheelchairFlag;//台鐵
   public String PackageServiceFlag//台鐵;
   public String DiningFlag;//台鐵
   public String BikeFlag;//台鐵
   public String BreastFeedingFlag;//台鐵
   public String DailyFlag;//台鐵
   public String ServiceAddedFlag;//台鐵
   public NameType Note;

}

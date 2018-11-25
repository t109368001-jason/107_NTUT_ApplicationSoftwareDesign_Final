package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 * 臺鐵定期車次資料
 *
 * 20181120 1404  完成
 *
 */
public class RailGeneralTrainInfo {
    public String TrainNo;
    public String Direction;
    public String StartingStationID;
    public NameType StartingStationName;//前面宣告過了
    public  String EndingStationID;
    public NameType EndingStationName; //前面宣告過了
    public String TrainTypeID;
    public String TrainTypeCode;
    public NameType TrainTypeName; //前面宣告過了
    public String TripLine;
    public String WheelchairFlag;
    public String PackageServiceFlag;
    public String DiningFlag;
    public String BikeFlag;
    public String BreastFeedingFlag;
    public String DailyFlag;
    public NameType Note; //前面宣告過了
}

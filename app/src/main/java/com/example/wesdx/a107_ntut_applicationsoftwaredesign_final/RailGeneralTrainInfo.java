package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;
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
    Zh_tw_En StartingStationName;//前面宣告過了
    public  String EndingStationID;
    Zh_tw_En EndingStationName; //前面宣告過了
    public String TrainTypeID;
    public String TrainTypeCode;
    Zh_tw_En TrainTypeName; //前面宣告過了
    public String TripLine;
    public String WheelchairFlag;
    public String PackageServiceFlag;
    public String DiningFlag;
    public String BikeFlag;
    public String BreastFeedingFlag;
    public String DailyFlag;
    Zh_tw_En Note; //前面宣告過了
}

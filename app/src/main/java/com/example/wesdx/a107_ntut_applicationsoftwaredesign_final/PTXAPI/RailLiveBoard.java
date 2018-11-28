package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 *20181125  1703  完成
 * GET /v2/Rail/TRA/LiveBoard
 *
 */
public class RailLiveBoard {
    public String StationID;
    public NameType StationName;
    public String TrainNo;
    public String Direction;
    public String TrainTypeID;
    public String TrainTypeCode;
    public NameType TrainTypeName;
    public String TripLine;
    public String EndingStationID;
    public NameType EndingStationName;
    public String ScheduledArrivalTime;
    public String ScheduledDepartureTime;
    public String DelayTime;
    public String SrcUpdateTime;
    public String UpdateTime;
}

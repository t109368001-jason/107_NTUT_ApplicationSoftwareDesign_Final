package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

/**
 * 臺鐵起迄站間票價資料
 *
 * 20181120 1351 完成
 *
 */
public class RailODFare {
    public String OriginStationID;
    OriginStationNameC OriginStationName;
    public String DestinationStationID;
    DestinationStationNameC DestinationStationName;
    public String Direction;
    FaresC Fares;
    public String UpdateTime;
}
class OriginStationNameC{
    public String Zh_tw;
    public String En;
}
class DestinationStationNameC{
    public String Zh_tw;
    public String En;
}
class FaresC{
    FareC Fare;
}

class  FareC{
    public String TicketType;
    public String Price;
}


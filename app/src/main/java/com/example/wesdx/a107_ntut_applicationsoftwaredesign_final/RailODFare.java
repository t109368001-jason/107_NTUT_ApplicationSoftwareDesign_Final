package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import java.util.List;

/**
 * 臺鐵起迄站間票價資料
 *
 * 20181120 1351 完成
 *
 */
public class RailODFare {
    public String OriginStationID;
    Zh_tw_En OriginStationName;
    public String DestinationStationID;
    Zh_tw_En DestinationStationName;
    public String Direction;
    List<FareC> Fares;
    public String UpdateTime;


    public String SrcUpdateTime;//高鐵
    public String VersionID;//高鐵
}

class  FareC{
    public String TicketType;
    public String Price;
}


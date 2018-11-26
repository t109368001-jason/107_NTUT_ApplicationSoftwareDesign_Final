package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.util.List;

/**
 * 臺鐵起迄站間票價資料
 *
 * 20181120 1351 完成
 *
 */
public class RailODFare {
    public String OriginStationID;
    NameType OriginStationName;
    public String DestinationStationID;
    NameType DestinationStationName;
    public String Direction;
    List<Fare> Fares;
    public String UpdateTime;


    public String SrcUpdateTime;//高鐵
    public String VersionID;//高鐵
}



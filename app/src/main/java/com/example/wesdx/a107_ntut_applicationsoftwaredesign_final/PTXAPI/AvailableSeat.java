package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.util.List;

/**
 *20181125  1719  完成
 *GET /v2/Rail/THSR/AvailableSeatStatusList/{StationID}
 *
 */
public class AvailableSeat {
    public String TrainNo;
    public String Direction;
    public String StationID;
    public NameType StationName;
    public String DepartureTime;
    public String EndingStationID;
    public NameType EndingStationName;
    public List<StopStation> StopStations;
    public String SrcRecTime;
    public String UpdateTime;
}

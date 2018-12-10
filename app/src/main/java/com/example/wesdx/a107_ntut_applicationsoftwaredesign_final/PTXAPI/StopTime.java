package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.text.ParseException;
import java.util.Date;

public class StopTime {
    public String StopSequence;
    public String StationID;
    public NameType StationName;
    public String ArrivalTime;
    public String DepartureTime;

    public Date getDepartureTimeDate() throws ParseException {
        return API.timeFormat.parse(this.DepartureTime);
    }

    public Date getArrivalTimeDate() throws ParseException {
        return API.timeFormat.parse(this.ArrivalTime);
    }
}
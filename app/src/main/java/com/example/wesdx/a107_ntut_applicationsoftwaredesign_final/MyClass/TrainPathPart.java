package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StopTime;

import java.text.ParseException;
import java.util.Date;

public class TrainPathPart {
    public RailStation originStation;
    public RailStation destinationStation;
    public RailDailyTimetable railDailyTimetable;

    TrainPathPart(RailStation originStation, RailStation destinationStation, RailDailyTimetable railDailyTimetable) {
        this.originStation = originStation;
        this.destinationStation = destinationStation;
        this.railDailyTimetable = railDailyTimetable;
    }

    Date getOriginDepartureTimeDate() throws ParseException {
        return railDailyTimetable.getDepartureTimeDateByStationID(originStation.StationID);
    }

    Date getDestinationArrivalTimeDate() throws ParseException {
        return railDailyTimetable.getArrivalTimeDateByStationID(destinationStation.StationID);
    }

    StopTime getOriginStopTime() {
        return this.railDailyTimetable.getStopTimeOfStopTimes(this.originStation);
    }

    StopTime getDestinationStopTime() {
        return this.railDailyTimetable.getStopTimeOfStopTimes(this.destinationStation);
    }
}

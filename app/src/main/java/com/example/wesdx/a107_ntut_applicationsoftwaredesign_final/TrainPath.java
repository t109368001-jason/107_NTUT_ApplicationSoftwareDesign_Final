package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;

import java.util.List;

public class TrainPath {
    public static class TrainPathPart {
        RailStation originStation;
        RailStation destinationStation;
        RailDailyTimetable railDailyTimetable;
    }

    List<TrainPathPart> trainPathPartList;
}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.DailyTrainInfo;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailGeneralTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailGeneralTrainInfo;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailODDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RegionalRailStation;

import java.util.ArrayList;
import java.util.List;

public class Router {
    public static List<RailDailyTimetable> get(String date, String time, RailStation originStation, RailStation destinationStation) {
        List<RailDailyTimetable> railODDailyTimetableList = new ArrayList<>();

        //List<RailStation> TRARailStationList = API.getStation(API.TRA);
        //RailStation.removeUnreservationStation(TRARailStationList);
        //List<RailGeneralTimetable> TRARailGeneralTimetableList = API.getGeneralTimetable(API.TRA);
        //List<RailGeneralTrainInfo> TRARailGeneralTrainInfoList = API.getGeneralTrainInfo(API.TRA);
        //List<RailODDailyTimetable> TRARailODDailyTimetableList = API.getDailyTimetable(API.TRA, originStation, destinationStation, date);
        //List<RegionalRailStation> TRARegionalRailStationList = RegionalRailStation.convert(TRARailStationList);
        //List<RailODFare> TRARailODFares = TRAAPI.getRailODFare(PTXAPI.TRA, originStation, destinationStation);
        //TRARailODDailyTimetableList = RailODDailyTimetable.filter(TRARailODDailyTimetableList, time, "24:00");

        //List<RailStation> THSRRailStationList = API.getStation(API.THSR);
        //List<RailGeneralTimetable> THSRRailGeneralTimetableList = API.getGeneralTimetable(API.THSR);
        //List<RailODDailyTimetable> THSRRailODDailyTimetableList = API.getDailyTimetable(API.THSR, "1010", "1020", "2018-11-30");
        //List<RegionalRailStation> THSRRegionalRailStationList = RegionalRailStation.convert(TRARailStationList);
        //THSRRailODDailyTimetableList = RailODDailyTimetable.filter(THSRRailODDailyTimetableList, "07:00", "01:00");

        //List<RailGeneralTimetable> TRARailGeneralTimetableList = API.getGeneralTimetable(API.TRA);
        List<RailDailyTimetable> TRARailDailyTimetableList = API.getDailyTimetable(API.TRA, API.TRAIN_DATE, date);
        TRARailDailyTimetableList = RailDailyTimetable.filter(TRARailDailyTimetableList, time, "24:00");
        TRARailDailyTimetableList = RailDailyTimetable.filter(TRARailDailyTimetableList, originStation, destinationStation);

        railODDailyTimetableList = TRARailDailyTimetableList;

        return railODDailyTimetableList;
    }
}

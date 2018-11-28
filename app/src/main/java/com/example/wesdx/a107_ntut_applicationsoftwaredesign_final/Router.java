package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;

import java.util.ArrayList;
import java.util.List;

public class Router {
    public static List<RailDailyTimetable> get(String transportation, String date, String time, RailStation originStation, RailStation destinationStation) {
        List<RailDailyTimetable> railDailyTimetableList_new = new ArrayList<>();

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
        List<RailDailyTimetable> railDailyTimetableList = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);
        railDailyTimetableList = RailDailyTimetable.filter(railDailyTimetableList, originStation, destinationStation);
        railDailyTimetableList = RailDailyTimetable.filter(railDailyTimetableList, originStation, destinationStation, time, "24:00");
        RailDailyTimetable.sort(railDailyTimetableList, originStation);

        for(int i = 50; i < railDailyTimetableList.size(); i++) {
            railDailyTimetableList.remove(i);
            i--;
        }

        railDailyTimetableList_new = railDailyTimetableList;

        return railDailyTimetableList_new;
    }
}

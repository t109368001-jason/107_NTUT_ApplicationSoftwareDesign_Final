package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;

import java.util.ArrayList;
import java.util.List;

public class Router {
    public static class Test2 {
        Test destinationStation;
        List<RailDailyTimetable> railDailyTimetableList;
        public Test2(Test destinationStation, List<RailDailyTimetable> railDailyTimetableList) {
            this.destinationStation = destinationStation;
            this.railDailyTimetableList = railDailyTimetableList;
        }
    }
    public static class Test {
        RailStation railStation;
        List<Test2> test2;
        public void add(Test destinationStation, List<RailDailyTimetable> railDailyTimetableList) {
            Test2 test2 = new Test2(destinationStation, railDailyTimetableList);
            this.test2.add(test2);
        }
        public Test(RailStation originStation) {
            this.railStation = originStation;
            this.test2 = new ArrayList<>();

        }
        public String getTop(int topNum) {
            if(test2.size() == 0) {
                return null;
            } else {
                List<List<RailDailyTimetable>> railDailyTimetableListList = new ArrayList<>();
                for(int i = 0; i < test2.size(); i++) {
                    for(int j = 0; j < test2.get(i).railDailyTimetableList.size(); j++) {

                    }
                }
            }
            return "";
        }
    }

    public static List<RailDailyTimetable> get(String transportation, String date, String time, List<RailStation> railStationList, RailStation originStation, RailStation destinationStation) {
        List<RailDailyTimetable> railDailyTimetableList_new = new ArrayList<>();
/*
        if(transportation.equals(API.TRA)) {
            List<RailStation> railStationList_temp = RailStation.split(railStationList, originStation, destinationStation);
            List<RailDailyTimetable> railDailyTimetableList = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);
            List<Test> testList = new ArrayList<>();
            for(int i = 0; i < railStationList_temp.size(); i++) {
                Test test = new Test(railStationList_temp.get(i));
                testList.add(test);
            }
            for(int i = 0; i < railStationList_temp.size(); i++) {
                for(int j = 0; j < railStationList_temp.size(); j++) {
                    if (j <= i) continue;
                    List<RailDailyTimetable> railDailyTimetableList_temp = new ArrayList<>(railDailyTimetableList);
                    railDailyTimetableList_temp = RailDailyTimetable.filter(railDailyTimetableList_temp, railStationList_temp.get(i), railStationList_temp.get(j));
                    if((railDailyTimetableList_temp != null ? railDailyTimetableList_temp.size() : 0) == 0) continue;
                    railDailyTimetableList_temp = RailDailyTimetable.filter(railDailyTimetableList_temp, railStationList_temp.get(i), railStationList_temp.get(j), time, "24:00");
                    if((railDailyTimetableList_temp != null ? railDailyTimetableList_temp.size() : 0) == 0) continue;
                    RailDailyTimetable.sort(railDailyTimetableList_temp, originStation);
                    testList.get(i).add(testList.get(j), railDailyTimetableList_temp);
                }
            }

        }
        */
        List<RailDailyTimetable> railDailyTimetableList = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);
        railDailyTimetableList = RailDailyTimetable.filter(railDailyTimetableList, originStation, destinationStation);
        railDailyTimetableList = RailDailyTimetable.filter(railDailyTimetableList, originStation, destinationStation, time, "24:00");
        RailDailyTimetable.sort(railDailyTimetableList, originStation);

        for(int i = 30; i < (railDailyTimetableList != null ? railDailyTimetableList.size() : 0); i++) {
            railDailyTimetableList.remove(i);
            i--;
        }

        railDailyTimetableList_new = railDailyTimetableList;

        return railDailyTimetableList_new;
    }
}

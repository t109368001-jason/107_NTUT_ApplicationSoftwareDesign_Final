package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;

import java.util.List;

public class DEBUGFunction {

    public static class Temp {
        String StationName;
        int num;
    }
    private static Temp[] get(List<RailDailyTimetable> railDailyTimetableList, List<RailStation> railStationList) {

        Temp[] x = new Temp[railStationList.size()];
        for(int i = 0; i < x.length; i++) {
            x[i] = new Temp();
            x[i].num = 0;
            x[i].StationName = railStationList.get(i).StationName.Zh_tw;
        }
        for(int i = 0; i < railDailyTimetableList.size(); i++) {
            if(!railDailyTimetableList.get(i).DailyTrainInfo.TrainTypeName.Zh_tw.contains("自強")) continue;
            for(int j = 0; j < railDailyTimetableList.get(i).StopTimes.size(); j++) {
                for(int k = 0; k < railStationList.size(); k++) {
                    if(railDailyTimetableList.get(i).StopTimes.get(j).StationID.equals(railStationList.get(k).StationID)) {
                        x[k].num++;
                        break;
                    }
                }
            }
        }

        for (Temp aX : x) {
            System.out.print(aX.StationName);
            System.out.print('\t');
            System.out.println(aX.num);
        }
        return x;
    }
}

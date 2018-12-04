package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrainPath {
    public static class TrainPathPart {
        RailStation originStation;
        RailStation destinationStation;
        RailDailyTimetable railDailyTimetable;
    }

    List<TrainPathPart> trainPathPartList;

    public String getOrigeinDepartureTime() {
        return this.trainPathPartList.get(0).railDailyTimetable.getStopTimeOfStopTimes(this.trainPathPartList.get(0).originStation).DepartureTime;
    }

    public String getDestinationArrivalTime() {
        return this.getLastItem().railDailyTimetable.getStopTimeOfStopTimes(this.getLastItem().destinationStation).ArrivalTime;
    }

    public TrainPathPart getLastItem() {
        return this.trainPathPartList.get(this.trainPathPartList.size()-1);
    }

    public static List<TrainPath> filter(List<TrainPath> trainPathList) {
        List<TrainPath> trainPathList_new = new ArrayList<>();

        try {
            for(TrainPath trainPath_temp1:trainPathList) {
                boolean addToList = true;
                for(int i = 0; i < trainPathList_new.size(); i++) {
                    boolean remove = false;
                    if(trainPath_temp1.getLastItem().railDailyTimetable.DailyTrainInfo.TrainNo.equals(trainPathList_new.get(i).getLastItem().railDailyTimetable.DailyTrainInfo.TrainNo)) {
                        Date time1 = new SimpleDateFormat("HH:mm").parse(trainPath_temp1.getOrigeinDepartureTime());
                        Date time2 = new SimpleDateFormat("HH:mm").parse(trainPathList_new.get(i).getOrigeinDepartureTime());

                        if(time1.before(time2)) {
                            if(trainPath_temp1.trainPathPartList.size() > 1) {
                                addToList = false;
                            }
                        } else {
                            if(trainPathList_new.get(i).trainPathPartList.size() > 1) {
                                trainPathList_new.remove(i);
                                i--;
                            }
                        }
                    }
                }
                if(addToList) {
                    trainPathList_new.add(trainPath_temp1);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return trainPathList_new;
    }
}

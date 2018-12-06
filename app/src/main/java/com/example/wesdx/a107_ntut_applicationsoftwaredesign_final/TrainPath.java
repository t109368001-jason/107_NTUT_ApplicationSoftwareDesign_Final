package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
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

        public TrainPathPart() {
            this.originStation = null;
            this.destinationStation = null;
            this.railDailyTimetable = null;
        }

        public TrainPathPart(RailStation originStation, RailStation destinationStation, RailDailyTimetable railDailyTimetable) {
            this.originStation = originStation;
            this.destinationStation = destinationStation;
            this.railDailyTimetable = railDailyTimetable;
        }
    }

    List<TrainPathPart> trainPathPartList;

    public TrainPath() {
        this.trainPathPartList = null;
    }

    public TrainPath(List<TrainPathPart> trainPathPartList) {
        this.trainPathPartList = trainPathPartList;
    }

    public TrainPath(TrainPathPart trainPathPart) {
        this.trainPathPartList = new ArrayList<>();
        this.trainPathPartList.add(trainPathPart);
    }

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
                    if(trainPath_temp1.getLastItem().railDailyTimetable.DailyTrainInfo.TrainNo.equals(trainPathList_new.get(i).getLastItem().railDailyTimetable.DailyTrainInfo.TrainNo)) {
                        Date time1 = API.timeFormat.parse(trainPath_temp1.getOrigeinDepartureTime());
                        Date time2 = API.timeFormat.parse(trainPathList_new.get(i).getOrigeinDepartureTime());

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

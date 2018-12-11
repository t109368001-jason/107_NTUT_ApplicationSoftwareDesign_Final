package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StopTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        public Date getOriginDepartureTimeDate() throws ParseException {
            return railDailyTimetable.getDepartureTimeDateByStationID(originStation.StationID);
        }

        public Date getDestinationArrivalTimeDate() throws ParseException {
            return railDailyTimetable.getArrivalTimeDateByStationID(destinationStation.StationID);
        }

        public StopTime getOriginStopTime() {
            return this.railDailyTimetable.getStopTimeOfStopTimes(this.originStation);
        }

        public StopTime geDestinationStopTime() {
            return this.railDailyTimetable.getStopTimeOfStopTimes(this.destinationStation);
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

    public Date getOriginDepartureTimeDate() throws ParseException {
        return this.trainPathPartList.get(0).getOriginDepartureTimeDate();
    }

    public Date getDestinationArrivalTimeDate() throws ParseException {
        return this.getLastItem().getDestinationArrivalTimeDate();
    }

    public RailStation getOriginRailStation() {
        return this.trainPathPartList.get(0).originStation;
    }

    public RailStation getDestinationRailStation() {
        return this.getLastItem().destinationStation;
    }

    private String getOrigeinDepartureTime() {
        return this.trainPathPartList.get(0).railDailyTimetable.getStopTimeOfStopTimes(this.trainPathPartList.get(0).originStation).DepartureTime;
    }

    private String getDestinationArrivalTime() {
        return this.getLastItem().railDailyTimetable.getStopTimeOfStopTimes(this.getLastItem().destinationStation).ArrivalTime;
    }

    public TrainPathPart getLastItem() {
        return this.trainPathPartList.get(this.trainPathPartList.size()-1);
    }

    public static TrainPath getBest(List<TrainPath> trainPathList, boolean useOriginDeparetureTime, boolean useEarliest) throws ParseException {
        TrainPath trainPath = null;
        for(TrainPath trainPath_temp:trainPathList) {
            if(trainPath == null) trainPath = trainPath_temp;
            else {
                if(useOriginDeparetureTime) {
                    if(trainPath_temp.getOriginDepartureTimeDate().before(trainPath.getOriginDepartureTimeDate()) ^ (!useEarliest)) {
                        trainPath = trainPath_temp;
                    }
                } else {
                    if(trainPath_temp.getDestinationArrivalTimeDate().before(trainPath.getDestinationArrivalTimeDate()) ^ (!useEarliest)) {
                        trainPath = trainPath_temp;
                    }
                }
            }
        }
        return trainPath;
    }

    public static List<TrainPath> filter(List<RailDailyTimetable> railDailyTimetableList, List<RailStation> railStationList, Date firstDepartureTime, Date lastArrivalTime, boolean isDirectional, int stopTimes) throws ParseException {
        List<TrainPath> trainPathList = new ArrayList<>();

        for(RailDailyTimetable railDailyTimetable:railDailyTimetableList) {
            int stopTimes_temp = 0;
            RailStation firstStation = null;
            RailStation lastStation = null;
            for(int i = 0; i < railStationList.size(); i++) {
                for(int j = 0 ; j < railDailyTimetable.StopTimes.size(); j++) {
                    if(railDailyTimetable.StopTimes.get(j).StationID.equals(railStationList.get(i).StationID)) {
                        stopTimes_temp++;
                        if(firstStation == null) firstStation = railStationList.get(i);
                        lastStation = railStationList.get(i);
                    }
                }
            }
            if(stopTimes_temp < stopTimes) continue;
            if((firstStation == null)||(lastStation == null)) continue;
            if(firstDepartureTime != null) {
                if(railDailyTimetable.getDepartureTimeDateByStationID(firstStation.StationID).before(firstDepartureTime)) continue;
            }
            if(lastArrivalTime != null) {
                if(railDailyTimetable.getArrivalTimeDateByStationID(lastStation.StationID).after(lastArrivalTime)) continue;
            }
            if(railDailyTimetable.getDepartureTimeDateByStationID(firstStation.StationID).after(railDailyTimetable.getArrivalTimeDateByStationID(lastStation.StationID))) continue;
            trainPathList.add(new TrainPath(new TrainPathPart(firstStation, lastStation, railDailyTimetable)));
        }

        return trainPathList;
    }

    public static List<TrainPath> filter(List<TrainPath> trainPathList) throws ParseException {
        List<TrainPath> trainPathList_new = new ArrayList<>(trainPathList);

        for(int i = 0; i < trainPathList_new.size(); i++) {
            boolean nextI = false;
            TrainPath trainPath1 = trainPathList_new.get(i);
            for(int j = i + 1; j < trainPathList_new.size(); j++) {
                boolean nextJ = false;
                TrainPath trainPath2 = trainPathList_new.get(j);
                for(TrainPathPart trainPathPart1:trainPath1.trainPathPartList) {
                    for(TrainPathPart trainPathPart2:trainPath2.trainPathPartList) {
                        if(trainPathPart1.railDailyTimetable.DailyTrainInfo.TrainNo.equals(trainPathPart2.railDailyTimetable.DailyTrainInfo.TrainNo)) {
                            switch (trainPath1.getDestinationArrivalTimeDate().compareTo(trainPath2.getDestinationArrivalTimeDate())) {
                                case 1:     //trainPathPart1 > trainPathPart2
                                    if(trainPath1.trainPathPartList.size() > 1) {
                                        trainPathList_new.remove(i);
                                        i--;
                                        nextI = true;
                                    }
                                    break;
                                case 0:     //trainPathPart1 = trainPathPart2
                                    switch (trainPath1.getOriginDepartureTimeDate().compareTo(trainPath2.getOriginDepartureTimeDate())) {
                                        case 1:     //trainPathPart1 > trainPathPart2
                                            if(trainPath2.trainPathPartList.size() > 1) {
                                                trainPathList_new.remove(j);
                                                j--;
                                            }
                                            break;
                                        case 0:     //trainPathPart1 = trainPathPart2
                                            if(trainPath1.trainPathPartList.size() < trainPath2.trainPathPartList.size()) {
                                                trainPathList_new.remove(j);
                                                j--;
                                            } else if(trainPath1.trainPathPartList.size() > trainPath2.trainPathPartList.size()){
                                                trainPathList_new.remove(i);
                                                i--;
                                                nextI = true;
                                            }
                                            break;
                                        case -1:    //trainPathPart1 < trainPathPart2
                                            if(trainPath1.trainPathPartList.size() > 1) {
                                                trainPathList_new.remove(i);
                                                i--;
                                                nextI = true;
                                            }
                                            break;
                                    }
                                    break;
                                case -1:    //trainPathPart1 < trainPathPart2
                                    if(trainPath2.trainPathPartList.size() > 1) {
                                        trainPathList_new.remove(j);
                                        j--;
                                    }
                                    break;
                            }
                            nextJ = true;
                        }
                        if(nextJ) break;
                    }
                    if(nextJ) break;
                }
                if(nextI) break;
            }
        }

        return trainPathList_new;
    }

    public static List<RailDailyTimetable> convert(List<TrainPath> trainPathList) {
        List<RailDailyTimetable> railDailyTimetableList = new ArrayList<>();

        for(TrainPath trainPath:trainPathList) {
            if(trainPath.trainPathPartList.size() == 1) {
                railDailyTimetableList.add(trainPath.trainPathPartList.get(0).railDailyTimetable);
            }
        }
        return railDailyTimetableList;
    }

    public static void sort(List<TrainPath> trainPathList) {
        Collections.sort(trainPathList, new Comparator<TrainPath>() {
            public int compare(TrainPath obj1, TrainPath obj2) {
                try {
                    Date obj1ArrivalTime = API.timeFormat.parse(obj1.getLastItem().railDailyTimetable.getStopTimeOfStopTimes(obj1.getLastItem().destinationStation).ArrivalTime);
                    Date obj2ArrivalTime = API.timeFormat.parse(obj2.getLastItem().railDailyTimetable.getStopTimeOfStopTimes(obj2.getLastItem().destinationStation).ArrivalTime);
                    if(obj1.getLastItem().railDailyTimetable.afterOverNightStation(obj1.getLastItem().destinationStation.StationID)) {
                        obj1ArrivalTime.setDate(obj1ArrivalTime.getDate() + 1);
                    }
                    if(obj2.getLastItem().railDailyTimetable.afterOverNightStation(obj2.getLastItem().destinationStation.StationID)) {
                        obj2ArrivalTime.setDate(obj2ArrivalTime.getDate() + 1);
                    }
                    return obj1ArrivalTime.compareTo(obj2ArrivalTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}

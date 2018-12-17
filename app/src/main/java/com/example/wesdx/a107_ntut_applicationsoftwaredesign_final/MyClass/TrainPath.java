package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TrainPath implements Comparable<TrainPath> {
    public List<TrainPathPart> trainPathPartList;

    TrainPath() {
        this.trainPathPartList = null;
    }

    TrainPath(TrainPathPart trainPathPart) {
        this.trainPathPartList = new ArrayList<>();
        this.trainPathPartList.add(trainPathPart);
    }

    Date getOriginDepartureTimeDate() throws ParseException {
        return this.trainPathPartList.get(0).getOriginDepartureTimeDate();
    }

    Date getDestinationArrivalTimeDate() throws ParseException {
        return this.getLastItem().getDestinationArrivalTimeDate();
    }

    static TrainPath getBest(List<TrainPath> trainPathList, boolean useOriginDeparetureTime, boolean useEarliest) throws ParseException {
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

    static List<TrainPath> filter(List<RailDailyTimetable> railDailyTimetableList, List<RailStation> railStationList, Date firstDepartureTime, Date lastArrivalTime, boolean isDirectional, int stopTimes) throws ParseException {
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
            if(isDirectional) {
                if (railDailyTimetable.getDepartureTimeDateByStationID(firstStation.StationID).after(railDailyTimetable.getArrivalTimeDateByStationID(lastStation.StationID))) continue;
            }
            trainPathList.add(new TrainPath(new TrainPathPart(firstStation, lastStation, railDailyTimetable)));
        }

        return trainPathList;
    }

    static List<TrainPath> filter(List<TrainPath> trainPathList) throws ParseException {
        if(trainPathList == null) return null;
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
                                            } else {
                                                if(trainPath1.trainPathPartList.size() > 1) {
                                                    trainPathList_new.remove(j);
                                                    j--;
                                                }
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

    static List<RailDailyTimetable> convert(List<TrainPath> trainPathList) {
        List<RailDailyTimetable> railDailyTimetableList = new ArrayList<>();

        for(TrainPath trainPath:trainPathList) {
            if(trainPath.trainPathPartList.size() == 1) {
                railDailyTimetableList.add(trainPath.trainPathPartList.get(0).railDailyTimetable);
            }
        }
        return railDailyTimetableList;
    }

    static void sort(List<TrainPath> trainPathList) {
        Collections.sort(trainPathList);
    }

    private TrainPathPart getLastItem() {
        return this.trainPathPartList.get(this.trainPathPartList.size()-1);
    }

    public Date getODTime() throws ParseException {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(getDestinationArrivalTimeDate());
        calendar2.setTime(getOriginDepartureTimeDate());
        calendar1.add(Calendar.HOUR_OF_DAY, -calendar2.get(Calendar.HOUR_OF_DAY));
        calendar1.add(Calendar.MINUTE, -calendar2.get(Calendar.MINUTE));
        return calendar1.getTime();
    }

    public RailStation getOriginRailStation() {
        return this.trainPathPartList.get(0).originStation;
    }

    public RailStation getDestinationRailStation() {
        return this.getLastItem().destinationStation;
    }

    public String getOrigeinDepartureTime() {
        return this.trainPathPartList.get(0).getOriginStopTime().DepartureTime;
    }

    public String getDestinationArrivalTime() {
        return this.getLastItem().getDestinationStopTime().ArrivalTime;
    }

    @Override
    public int compareTo(TrainPath obj2) {
        try {
            Calendar calendarObj1 = Calendar.getInstance();
            Calendar calendarObj2 = Calendar.getInstance();
            calendarObj1.setTime(this.getDestinationArrivalTimeDate());
            calendarObj2.setTime(obj2.getDestinationArrivalTimeDate());
            if(this.getLastItem().railDailyTimetable.afterOverNightStation(this.getLastItem().destinationStation.StationID)) {
                calendarObj1.add(Calendar.HOUR_OF_DAY, 24);
            }
            if(obj2.getLastItem().railDailyTimetable.afterOverNightStation(obj2.getLastItem().destinationStation.StationID)) {
                calendarObj2.add(Calendar.HOUR_OF_DAY, 24);
            }

            return calendarObj1.compareTo(calendarObj2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

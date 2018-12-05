package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StationOfLine;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StopTime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Router {
    public static long TRANSFER_TIME = 10 * 60 * 1000;
    public static List<StationOfLine> stationOfLineList;

    public static String TRARailDailyTimetableListCacheDate;
    public static List<RailDailyTimetable> TRARailDailyTimetableListCache;
    public static String THSRRailDailyTimetableListCacheDate;
    public static List<RailDailyTimetable> THSRRailDailyTimetableListCache;

    public static List<TrainPath> getTranserPath(String transportation, String date, String takeTimeString, List<RailStation> railStationList, RailStation originStation, RailStation destinationStation) {
        List<TrainPath> trainPathList = new ArrayList<>();

        if(stationOfLineList == null) {
            stationOfLineList = API.getStationOfLine(API.TRA);
            StationOfLine.fixMissing15StationProblem(stationOfLineList);
            if(stationOfLineList == null) return null;
        }

        if(transportation.equals(API.TRA)) {
            try {
                Date takeTime = API.timeFormat.parse(takeTimeString);
                List<List<RailStation>> railStationList_List = MyRailStation.getRailStationList(railStationList, originStation, destinationStation);

                List<RailDailyTimetable> railDailyTimetableList_all;

                if(date.equals(TRARailDailyTimetableListCacheDate)) {
                    if(TRARailDailyTimetableListCache == null) {
                        TRARailDailyTimetableListCacheDate = date;
                        TRARailDailyTimetableListCache = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);
                    }
                    railDailyTimetableList_all = TRARailDailyTimetableListCache;
                } else {
                    railDailyTimetableList_all = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);
                    TRARailDailyTimetableListCacheDate = date;
                    TRARailDailyTimetableListCache = railDailyTimetableList_all;
                }

                if(railStationList_List == null) return null;

                for(int i = 0; i < railStationList_List.size(); i++) {
                    for(int j = i+1; j < railStationList_List.size(); j++) {
                        boolean same = true;
                        if(railStationList_List.get(i).size() == railStationList_List.get(j).size()) {
                            for(int k = 0; k < railStationList_List.get(i).size(); k++) {
                                if(!railStationList_List.get(i).get(k).StationID.equals(railStationList_List.get(j).get(k).StationID)) {
                                    same = false;
                                    break;
                                }
                            }
                            if(same) {
                                railStationList_List.remove(j);
                            }
                        }
                    }
                }

                for(List<RailStation> railStationList_current:railStationList_List) {
                    List<RailDailyTimetable> railDailyTimetableList = RailDailyTimetable.filterByPath(railDailyTimetableList_all, railStationList_current, true, 2);

                    if(railDailyTimetableList == null) {
                        continue;
                    }

                    for(RailDailyTimetable railDailyTimetable_mid:railDailyTimetableList) {
                        TrainPath trainPath = new TrainPath();
                        trainPath.trainPathPartList = new ArrayList<>();

                        RailStation firstRailStation, lastRailStation;
                        StopTime firstStopTime, lastStopTime;
                        Date firstTime, lastTime, firstTimeThreshold, lastTimeThreshold;

                        firstStopTime = railDailyTimetable_mid.findStopTime(railStationList_current);
                        lastStopTime = railDailyTimetable_mid.findLastStopTime(railStationList_current);
                        if((firstStopTime == null)||(lastStopTime == null)) {
                            continue;
                        }

                        firstTime = API.timeFormat.parse(firstStopTime.DepartureTime);
                        if(railDailyTimetable_mid.afterOverNightStation(firstStopTime.StationID)) {
                            firstTime.setDate(firstTime.getDate() + 1);
                        }
                        lastTime = API.timeFormat.parse(lastStopTime.DepartureTime);
                        if(railDailyTimetable_mid.afterOverNightStation(lastStopTime.StationID)) {
                            lastTime.setDate(lastTime.getDate() + 1);
                        }

                        if(firstTime.after(lastTime)) {
                            continue;
                        }

                        if(firstTime.before(takeTime)) {
                            continue;
                        }

                        firstTimeThreshold = new Date(firstTime.getTime() - TRANSFER_TIME);
                        lastTimeThreshold = new Date(lastTime.getTime() + TRANSFER_TIME);
                        firstRailStation = RailStation.find(railStationList_current, firstStopTime.StationID);
                        lastRailStation = RailStation.find(railStationList_current, lastStopTime.StationID);
                        if((firstRailStation == null)||(lastRailStation == null)) {
                            continue;
                        }

                        if(!firstStopTime.StationID.equals(originStation.StationID)) {
                            RailDailyTimetable railDailyTimetable_best = null;
                            Date arrivalFirstTime_best = null;

                            for(RailDailyTimetable railDailyTimetable_first:railDailyTimetableList) {
                                StopTime originStopTime, arrivalFirstStopTime;
                                Date originTime, arrivalFirstTime;

                                originStopTime = railDailyTimetable_first.getStopTimeOfStopTimes(originStation.StationID);
                                arrivalFirstStopTime = railDailyTimetable_first.getStopTimeOfStopTimes(firstStopTime.StationID);
                                if((originStopTime == null)||(arrivalFirstStopTime == null)) {
                                    continue;
                                }
                                originTime = API.timeFormat.parse(originStopTime.DepartureTime);
                                if(railDailyTimetable_first.afterOverNightStation(originStopTime.StationID)) {
                                    originTime.setDate(originTime.getDate() + 1);
                                }
                                arrivalFirstTime = API.timeFormat.parse(arrivalFirstStopTime.DepartureTime);
                                if(railDailyTimetable_first.afterOverNightStation(arrivalFirstStopTime.StationID)) {
                                    arrivalFirstTime.setDate(arrivalFirstTime.getDate() + 1);
                                }

                                if(originTime.after(arrivalFirstTime)) {
                                    continue;
                                }

                                if(originTime.before(takeTime)) {
                                    continue;
                                }

                                if(arrivalFirstTime.after(firstTimeThreshold)) {
                                    continue;
                                }

                                if(railDailyTimetable_best == null) {
                                    railDailyTimetable_best = railDailyTimetable_first;
                                    arrivalFirstTime_best = arrivalFirstTime;
                                } else {
                                    if(arrivalFirstTime.after(arrivalFirstTime_best)) {
                                        railDailyTimetable_best = railDailyTimetable_first;
                                        arrivalFirstTime_best = arrivalFirstTime;
                                    }
                                }
                            }

                            if(railDailyTimetable_best != null) {
                                TrainPath.TrainPathPart trainPathPart_first = new TrainPath.TrainPathPart();
                                trainPathPart_first.originStation = originStation;
                                trainPathPart_first.destinationStation = firstRailStation;
                                trainPathPart_first.railDailyTimetable = railDailyTimetable_best;
                                trainPath.trainPathPartList.add(trainPathPart_first);
                            } else {
                                continue;
                            }
                        }

                        TrainPath.TrainPathPart trainPathPart_mid = new TrainPath.TrainPathPart();
                        trainPathPart_mid.originStation = firstRailStation;
                        trainPathPart_mid.destinationStation = lastRailStation;
                        trainPathPart_mid.railDailyTimetable = railDailyTimetable_mid;
                        trainPath.trainPathPartList.add(trainPathPart_mid);

                        if(!lastRailStation.StationID.equals(destinationStation.StationID)) {
                            RailDailyTimetable railDailyTimetable_best = null;
                            Date destinationTime_best = null;

                            for(RailDailyTimetable railDailyTimetable_last:railDailyTimetableList) {
                                StopTime departureLastStopTime, destinationStopTime;
                                Date departureLastTime, destinationTime;

                                departureLastStopTime = railDailyTimetable_last.getStopTimeOfStopTimes(lastRailStation.StationID);
                                destinationStopTime = railDailyTimetable_last.getStopTimeOfStopTimes(destinationStation.StationID);
                                if((departureLastStopTime == null)||(destinationStopTime == null)) {
                                    continue;
                                }
                                departureLastTime = API.timeFormat.parse(departureLastStopTime.DepartureTime);
                                if(railDailyTimetable_last.afterOverNightStation(departureLastStopTime.StationID)) {
                                    departureLastTime.setDate(departureLastTime.getDate() + 1);
                                }
                                destinationTime = API.timeFormat.parse(destinationStopTime.DepartureTime);
                                if(railDailyTimetable_last.afterOverNightStation(destinationStopTime.StationID)) {
                                    destinationTime.setDate(destinationTime.getDate() + 1);
                                }

                                if(departureLastTime.after(destinationTime)) {
                                    continue;
                                }

                                if(departureLastTime.before(lastTimeThreshold)) {
                                    continue;
                                }

                                if(railDailyTimetable_best == null) {
                                    railDailyTimetable_best = railDailyTimetable_last;
                                    destinationTime_best = destinationTime;
                                } else {
                                    if(destinationTime.before(destinationTime_best)) {
                                        railDailyTimetable_best = railDailyTimetable_last;
                                        destinationTime_best = destinationTime;
                                    }
                                }
                            }

                            if(railDailyTimetable_best != null) {
                                TrainPath.TrainPathPart trainPathPart_last = new TrainPath.TrainPathPart();
                                trainPathPart_last.originStation = lastRailStation;
                                trainPathPart_last.destinationStation = destinationStation;
                                trainPathPart_last.railDailyTimetable = railDailyTimetable_best;
                                trainPath.trainPathPartList.add(trainPathPart_last);
                            } else {
                                continue;
                            }
                        }
                        trainPathList.add(trainPath);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

        } else if(transportation.equals(API.THSR)) {
            List<RailDailyTimetable> railDailyTimetableList;

            if(date.equals(THSRRailDailyTimetableListCacheDate)) {
                if(THSRRailDailyTimetableListCache == null) {
                    THSRRailDailyTimetableListCacheDate = date;
                    THSRRailDailyTimetableListCache = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);
                }
                railDailyTimetableList = THSRRailDailyTimetableListCache;
            } else {
                railDailyTimetableList = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);
                THSRRailDailyTimetableListCacheDate = date;
                THSRRailDailyTimetableListCache = railDailyTimetableList;
            }


            railDailyTimetableList = RailDailyTimetable.filterByOD(railDailyTimetableList, originStation, destinationStation, true);

            for(RailDailyTimetable railDailyTimetable:railDailyTimetableList) {
                TrainPath.TrainPathPart trainPathPart = new TrainPath.TrainPathPart();
                TrainPath trainPath = new TrainPath();
                trainPath.trainPathPartList = new ArrayList<>();
                trainPathPart.originStation = originStation;
                trainPathPart.destinationStation = destinationStation;
                trainPathPart.railDailyTimetable = railDailyTimetable;
                trainPath.trainPathPartList.add(trainPathPart);
                trainPathList.add(trainPath);
            }
        }

        trainPathList = TrainPath.filter(trainPathList);

        Collections.sort(trainPathList, new Comparator<TrainPath>(){
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
                    if (obj1ArrivalTime.after(obj2ArrivalTime)) {
                        return 1;
                    }
                    else if (obj1ArrivalTime.before(obj2ArrivalTime)) {
                        return -1;
                    }
                    else {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });


        if(trainPathList.size() == 0) return null;
        for(int i = 10; i < trainPathList.size(); i++) {
            trainPathList.remove(i);
            i--;
        }

        return trainPathList;
    }
}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.util.Log;

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

public class Router {
    public static long TRANSFER_TIME = 10 * 60 * 1000;
    public static List<TrainPath> getTranserPath(String transportation, String date, String takeTimeString, List<RailStation> railStationList, RailStation originStation, RailStation destinationStation) {
        List<TrainPath> trainPathList = new ArrayList<>();

        if(transportation.equals(API.TRA)) {
            try {
                SimpleDateFormat simpleDateFormat_HHmm = new SimpleDateFormat("HH:mm");
                List<RailStation> railStationList_all;
                List<RailDailyTimetable> railDailyTimetableList_all, railDailyTimetableList_all_TC;
                Date takeTime = simpleDateFormat_HHmm.parse(takeTimeString);

                //railStationList_all = MyRailStation.getRailStationList(railStationList, originStation, destinationStation);
                railStationList_all = RailStation.split(railStationList, originStation, destinationStation);
                if(railStationList_all == null) return null;
                railDailyTimetableList_all = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);

                //移除停靠站數少於2的列車
                railDailyTimetableList_all = RailDailyTimetable.filter(railDailyTimetableList_all, railStationList_all, 2);


                //取得是自強的班次
                railDailyTimetableList_all_TC = new ArrayList<>();
                for(int i = 0; i < railDailyTimetableList_all.size(); i++) {
                    if(railDailyTimetableList_all.get(i).DailyTrainInfo.TrainTypeName.Zh_tw.contains("自強")) {
                        railDailyTimetableList_all_TC.add(railDailyTimetableList_all.get(i));
                    }
                }

                for(RailDailyTimetable railDailyTimetable_TC:railDailyTimetableList_all_TC) {
                    TrainPath trainPath = new TrainPath();
                    trainPath.trainPathPartList = new ArrayList<>();

                    RailStation firstRailStation, lastRailStation;
                    StopTime firstStopTime, lastStopTime;
                    Date firstTime, lastTime, firstTimeThreshold, lastTimeThreshold;

                    firstStopTime = railDailyTimetable_TC.findStopTime(railStationList_all);
                    lastStopTime = railDailyTimetable_TC.findLastStopTime(railStationList_all);
                    if((firstStopTime == null)||(lastStopTime == null)) continue;
                    firstTime = simpleDateFormat_HHmm.parse(firstStopTime.DepartureTime);
                    if(!railDailyTimetable_TC.beforeOverNightStation(firstStopTime.StationID)) {
                        firstTime.setDate(firstTime.getDate() + 1);
                    }
                    lastTime = simpleDateFormat_HHmm.parse(lastStopTime.DepartureTime);
                    if(!railDailyTimetable_TC.beforeOverNightStation(lastStopTime.StationID)) {
                        lastTime.setDate(lastTime.getDate() + 1);
                    }

                    if(firstTime.after(lastTime)) continue;

                    firstTimeThreshold = new Date(firstTime.getTime() - TRANSFER_TIME);
                    lastTimeThreshold = new Date(lastTime.getTime() + TRANSFER_TIME);
                    firstRailStation = RailStation.find(railStationList_all, firstStopTime.StationID);
                    lastRailStation = RailStation.find(railStationList_all, lastStopTime.StationID);
                    if((firstRailStation == null)||(lastRailStation == null)) continue;

                    if(!firstStopTime.StationID.equals(originStation.StationID)) {
                        RailDailyTimetable railDailyTimetable_best = null;
                        Date arrivalFirstTime_best = null;

                        for(RailDailyTimetable railDailyTimetable_first:railDailyTimetableList_all) {
                            StopTime originStopTime, arrivalFirstStopTime;
                            Date originTime, arrivalFirstTime;

                            originStopTime = railDailyTimetable_first.getStopTimeOfStopTimes(originStation.StationID);
                            arrivalFirstStopTime = railDailyTimetable_first.getStopTimeOfStopTimes(firstStopTime.StationID);
                            if((originStopTime == null)||(arrivalFirstStopTime == null)) continue;
                            originTime = simpleDateFormat_HHmm.parse(originStopTime.DepartureTime);
                            if(!railDailyTimetable_first.beforeOverNightStation(originStopTime.StationID)) {
                                originTime.setDate(originTime.getDate() + 1);
                            }
                            arrivalFirstTime = simpleDateFormat_HHmm.parse(arrivalFirstStopTime.DepartureTime);
                            if(!railDailyTimetable_first.beforeOverNightStation(arrivalFirstStopTime.StationID)) {
                                arrivalFirstTime.setDate(arrivalFirstTime.getDate() + 1);
                            }

                            if(originTime.after(arrivalFirstTime)) continue;

                            if(originTime.before(takeTime)) continue;

                            if(arrivalFirstTime.after(firstTimeThreshold)) continue;

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
                    trainPathPart_mid.railDailyTimetable = railDailyTimetable_TC;
                    trainPath.trainPathPartList.add(trainPathPart_mid);

                    if(!lastRailStation.StationID.equals(destinationStation.StationID)) {
                        RailDailyTimetable railDailyTimetable_best = null;
                        Date destinationTime_best = null;

                        for(RailDailyTimetable railDailyTimetable_last:railDailyTimetableList_all) {
                            StopTime departureLastStopTime, destinationStopTime;
                            Date departureLastTime, destinationTime;

                            departureLastStopTime = railDailyTimetable_last.getStopTimeOfStopTimes(lastRailStation.StationID);
                            destinationStopTime = railDailyTimetable_last.getStopTimeOfStopTimes(destinationStation.StationID);
                            if((departureLastStopTime == null)||(destinationStopTime == null)) continue;
                            departureLastTime = simpleDateFormat_HHmm.parse(departureLastStopTime.DepartureTime);
                            if(!railDailyTimetable_last.beforeOverNightStation(departureLastStopTime.StationID)) {
                                departureLastTime.setDate(departureLastTime.getDate() + 1);
                            }
                            destinationTime = simpleDateFormat_HHmm.parse(destinationStopTime.DepartureTime);
                            if(!railDailyTimetable_last.beforeOverNightStation(destinationStopTime.StationID)) {
                                destinationTime.setDate(destinationTime.getDate() + 1);
                            }

                            if(departureLastTime.after(destinationTime)) continue;

                            if(departureLastTime.before(lastTimeThreshold)) continue;

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

                Collections.sort(trainPathList, new Comparator<TrainPath>(){
                    public int compare(TrainPath obj1, TrainPath obj2) {
                        try {
                            StopTime obj1StopTime = obj1.trainPathPartList.get(obj1.trainPathPartList.size()-1).railDailyTimetable.getStopTimeOfStopTimes(obj1.trainPathPartList.get(obj1.trainPathPartList.size()-1).destinationStation.StationID);
                            StopTime obj2StopTime = obj2.trainPathPartList.get(obj2.trainPathPartList.size()-1).railDailyTimetable.getStopTimeOfStopTimes(obj2.trainPathPartList.get(obj2.trainPathPartList.size()-1).destinationStation.StationID);
                            if ((new SimpleDateFormat("HH:mm").parse(obj1StopTime.ArrivalTime).after((new SimpleDateFormat("HH:mm").parse(obj2StopTime.ArrivalTime))))) {
                                return 1;
                            }
                            else if ((new SimpleDateFormat("HH:mm").parse(obj1StopTime.ArrivalTime).before((new SimpleDateFormat("HH:mm").parse(obj2StopTime.ArrivalTime))))) {
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

                Log.d("trainPathList", "/////////////////////////////////////");
                Log.d("trainPathList", "/////////////////////////////////////");
                Log.d("trainPathList", "/////////////////////////////////////");
                Log.d("trainPathList", "/////////////////////////////////////");
                Log.d("trainPathList", "/////////////////////////////////////");
                Log.d("trainPathList", "/////////////////////////////////////");
                for(TrainPath trainPath:trainPathList) {
                    String info = "";
                    for(TrainPath.TrainPathPart trainPathPart:trainPath.trainPathPartList) {
                        info += trainPathPart.originStation.StationName.Zh_tw;
                        info += "(" + trainPathPart.railDailyTimetable.getStopTimeOfStopTimes(trainPathPart.originStation.StationID).DepartureTime + ")";
                        info += trainPathPart.destinationStation.StationName.Zh_tw;
                        info += "(" + trainPathPart.railDailyTimetable.getStopTimeOfStopTimes(trainPathPart.destinationStation.StationID).ArrivalTime + ")";
                        info += "(No: " + trainPathPart.railDailyTimetable.DailyTrainInfo.TrainNo + ")";
                        info += " → ";
                    }
                    Log.d("trainPathList", info);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

        }
        if(trainPathList.size() == 0) return null;
        return trainPathList;
    }

    public static List<RailDailyTimetable> get(String transportation, String date, String takeTimeString, List<RailStation> railStationList, RailStation originStation, RailStation destinationStation) {
        List<RailDailyTimetable> railDailyTimetableList_new = new ArrayList<>();

        List<RailDailyTimetable> railDailyTimetableList = API.getDailyTimetable(transportation, API.TRAIN_DATE, date);
        railDailyTimetableList = RailDailyTimetable.filter(railDailyTimetableList, originStation, destinationStation);
        railDailyTimetableList = RailDailyTimetable.filter(railDailyTimetableList, originStation, destinationStation, takeTimeString, "24:00");
        //RailDailyTimetable.sort(railDailyTimetableList, originStation);

        for(int i = 30; i < (railDailyTimetableList != null ? railDailyTimetableList.size() : 0); i++) {
            railDailyTimetableList.remove(i);
            i--;
        }

        railDailyTimetableList_new = railDailyTimetableList;


        List<List<RailStation>> railStationListList = MyRailStation.getRailStationList(railStationList, originStation, destinationStation);


        return railDailyTimetableList_new;
    }
}

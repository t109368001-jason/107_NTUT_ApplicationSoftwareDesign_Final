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

    public static List<TrainPath> getTranserPath(String transportation, String date, String takeTimeString, List<RailStation> railStationList, final RailStation originStation, final RailStation destinationStation) {
        List<TrainPath> trainPathList = new ArrayList<>();

        if(stationOfLineList == null) {
            stationOfLineList = API.getStationOfLine(API.TRA);
            StationOfLine.fixMissing15StationProblem(stationOfLineList);
            if(stationOfLineList == null) return null;
        }
        try {
            Date takeTime = API.timeFormat.parse(takeTimeString);

            if (transportation.equals(API.TRA_AND_THSR)) {
                List<RailStation> railStationList_THSR_ALL = API.getStation(API.THSR);//匯入高鐵所有站
                List<RailStation> THSR_have_TRA = new ArrayList<>();
                List<RailStation> TRA_have_THSR = new ArrayList<>();

                for (RailStation railStation_THSR:railStationList_THSR_ALL){
                    if((RailStation.transferStation(railStationList, railStation_THSR)) != null){
                        THSR_have_TRA.add(railStation_THSR);
                    }
                }
                for (RailStation railStation_TRA:THSR_have_TRA){
                    TRA_have_THSR.add(RailStation.transferStation(railStationList, railStation_TRA));
                }

                List<RailDailyTimetable> railDailyTimetableList_TRA_ALL;//當天台鐵的所有班次
                if(date.equals(TRARailDailyTimetableListCacheDate)) {
                    if(TRARailDailyTimetableListCache == null) {
                        TRARailDailyTimetableListCacheDate = date;
                        TRARailDailyTimetableListCache = API.getDailyTimetable(API.TRA, API.TRAIN_DATE, date);
                    }
                    railDailyTimetableList_TRA_ALL = TRARailDailyTimetableListCache;
                } else {
                    railDailyTimetableList_TRA_ALL = API.getDailyTimetable(API.TRA, API.TRAIN_DATE, date);
                    TRARailDailyTimetableListCacheDate = date;
                    TRARailDailyTimetableListCache = railDailyTimetableList_TRA_ALL;
                }

                List<RailDailyTimetable> railDailyTimetableList_THSR_ALL;//當天高鐵的所有班次
                if(date.equals(THSRRailDailyTimetableListCacheDate)) {
                    if(THSRRailDailyTimetableListCache == null) {
                        THSRRailDailyTimetableListCacheDate = date;
                        THSRRailDailyTimetableListCache = API.getDailyTimetable(API.THSR, API.TRAIN_DATE, date);
                    }
                    railDailyTimetableList_THSR_ALL = THSRRailDailyTimetableListCache;
                } else {
                    railDailyTimetableList_THSR_ALL = API.getDailyTimetable(API.THSR, API.TRAIN_DATE, date);
                    THSRRailDailyTimetableListCacheDate = date;
                    THSRRailDailyTimetableListCache = railDailyTimetableList_THSR_ALL;
                }

                RailStation originStation_THSR = (originStation.OperatorID.equals("TRA"))?RailStation.transferStation(railStationList, originStation):originStation;//把輸入車站一律轉換成高鐵，若無法轉換則為null
                RailStation destinationStation_THSR = (destinationStation.OperatorID.equals("TRA"))?RailStation.transferStation(railStationList, destinationStation):destinationStation;
                RailStation originStation_TRA = (originStation.OperatorID.equals("THSR"))?RailStation.transferStation(railStationList, originStation):originStation;//把輸入車站一律轉換為台鐵，若輸入為沒有台鐵的高鐵站則為null
                RailStation destinationStation_TRA = (destinationStation.OperatorID.equals("THSR"))?RailStation.transferStation(railStationList, destinationStation):destinationStation;

                if(( (originStation_TRA.OperatorID.equals("THSR")) && ((destinationStation_TRA.OperatorID.equals("THSR")) || (destinationStation_THSR != null ))) ||
                        ((originStation_THSR != null ) && ((destinationStation_THSR != null) || (destinationStation_TRA.OperatorID.equals("THSR"))) )) {//如果起站跟終站都是高鐵的話不轉乘
                    return getTranserPath(API.THSR, date, takeTimeString, railStationList_THSR_ALL, originStation_THSR, destinationStation_THSR);
                } else if((originStation_TRA.OperatorID.equals("THSR"))||(originStation_THSR != null)){//如果起站是高鐵終站是臺鐵的話一段轉乘
                    if(originStation_THSR != null){//如果起站是高鐵而且同時有臺鐵的話
                        List<List<RailStation>> railStationList_List = MyRailStation.getRailStationList(railStationList, originStation_TRA, destinationStation_TRA);//台鐵的起站到終站的所有班次裡的所有站

                        for (List<RailStation> railStationList_current : railStationList_List) {//把台鐵高鐵當天每個班次裡會經過2站以上的班次篩選出來
                            List<RailDailyTimetable> railDailyTimetableList_TRA = RailDailyTimetable.filterByPath(railDailyTimetableList_TRA_ALL, railStationList_current, true, 2);
                            List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStationList_current, railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來
                            List<RailDailyTimetable> railDailyTimetableList_THSR = RailDailyTimetable.filterByPath(railDailyTimetableList_THSR_ALL, railStationList_THSR, true, 2);//在該路徑下含有台鐵的高鐵最遠可以走的班次表


                            for(RailDailyTimetable railDailyTimetableList_THSR_temp:railDailyTimetableList_THSR){
                                StopTime THSR_LastStopTime = railDailyTimetableList_THSR_temp.findLastStopTime(railStationList_THSR);
                                RailStation lastStation_THSR = RailStation.find(railStationList_THSR, THSR_LastStopTime.StationID);
                                Date THSR_ArrivalTime = API.timeFormat.parse(THSR_LastStopTime.ArrivalTime);
                                Date TRA_DepartureTime = new Date(THSR_ArrivalTime.getTime() + TRANSFER_TIME);

                                List<TrainPath> TRA_trainPath;

                                if((TRA_trainPath = getTranserPath(API.TRA, date, API.timeFormat.format(TRA_DepartureTime), railStationList_current, RailStation.transferStation(railStationList, lastStation_THSR), destinationStation)) == null) continue;

                                TrainPath best = null;

                                for(TrainPath TRA_trainPath_temp:TRA_trainPath){
                                    if(best == null) best = TRA_trainPath_temp;
                                    else {
                                        if(API.timeFormat.parse(TRA_trainPath_temp.getDestinationArrivalTime()).before(API.timeFormat.parse(best.getDestinationArrivalTime()))){
                                            best = TRA_trainPath_temp;
                                        }
                                    }
                                }

                                if(best == null) continue;

                                TrainPath trainPath = new TrainPath();
                                trainPath.trainPathPartList = new ArrayList<>();
                                TrainPath.TrainPathPart trainPathPart = new TrainPath.TrainPathPart();
                                trainPathPart.originStation = originStation_THSR;
                                trainPathPart.destinationStation = lastStation_THSR;
                                trainPathPart.railDailyTimetable = railDailyTimetableList_THSR_temp;
                                trainPath.trainPathPartList.add(trainPathPart);
                                trainPath.trainPathPartList.addAll(best.trainPathPartList);
                                trainPathList.add(trainPath);
                            }
                        }
                    } else {//如果起站是高鐵但沒有臺鐵的話

                    }
                } else if(destinationStation_TRA.OperatorID.equals("THSR")||(destinationStation_THSR != null)){//如果起站是臺鐵終站是高鐵的話一段轉乘
                    if(destinationStation_THSR != null){//如果終站是高鐵而且同時有臺鐵的話
                        List<List<RailStation>> railStationList_List = MyRailStation.getRailStationList(railStationList, originStation_TRA, destinationStation_TRA);//有方向性的所有路徑

                        for (List<RailStation> railStationList_current : railStationList_List) {//把台鐵高鐵當天班次裡會經過2站以上的班次篩選出來
                            List<RailDailyTimetable> railDailyTimetableList_TRA = RailDailyTimetable.filterByPath(railDailyTimetableList_TRA_ALL, railStationList_current, true, 2);
                            List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStationList_current, railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來
                            List<RailDailyTimetable> railDailyTimetableList_THSR = RailDailyTimetable.filterByPath(railDailyTimetableList_THSR_ALL, railStationList_THSR, true, 2);
                        }
                    } else {//如果終站是高鐵但沒有臺鐵的話

                    }
                } else {//如果起站跟終站都是臺鐵的話二段轉乘
                    //遞迴賢杰功能
                    List<List<RailStation>> railStationList_List = MyRailStation.getRailStationList(railStationList, originStation_TRA, destinationStation_TRA);//台鐵的起站到終站的所有班次裡的所有站

                    for (List<RailStation> railStationList_current : railStationList_List) {//把台鐵高鐵當天班次裡會經過2站以上的班次篩選出來
                        List<RailDailyTimetable> railDailyTimetableList_TRA = RailDailyTimetable.filterByPath(railDailyTimetableList_TRA_ALL, railStationList_current, true, 2);
                        List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStationList_current, railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來
                        List<RailDailyTimetable> railDailyTimetableList_THSR = RailDailyTimetable.filterByPath(railDailyTimetableList_THSR_ALL, railStationList_THSR, true, 2);
                    }
                }
            } else if(transportation.equals(API.TRA)) {

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
} catch (ParseException e) {
        e.printStackTrace();
        return null;
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

        return trainPathList;
    }
}

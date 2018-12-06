package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StationOfLine;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StopTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Router {
    public static class RouterException extends Exception {
        public static String INPUT_OBJECT_IS_NULL = "Input object is null";
        public static String ORIGINSTATION_EQUALS_DESTINATIONSTATION = "Origin station equals destination station";
        public RouterException(String message)
        {
            super(message);
        }
    }

    public static long TRANSFER_TIME = 10 * 60 * 1000;
    public static List<StationOfLine> stationOfLineList;

    public static String TRARailDailyTimetableListCacheDate;
    public static List<RailDailyTimetable> TRARailDailyTimetableListCache;
    public static String THSRRailDailyTimetableListCacheDate;
    public static List<RailDailyTimetable> THSRRailDailyTimetableListCache;

    public static List<RailDailyTimetable> getFromCache(String transportation, String date) {
        if(transportation.equals(API.TRA) && date.equals(TRARailDailyTimetableListCacheDate)) {
            return new ArrayList<>(TRARailDailyTimetableListCache);
        } else if(transportation.equals(API.THSR) && date.equals(THSRRailDailyTimetableListCacheDate)) {
            return new ArrayList<>(THSRRailDailyTimetableListCache);
        }
        return null;
    }

    public static void seveToCache(String transportation, String date, List<RailDailyTimetable> railDailyTimetableList) {
        if(transportation.equals(API.TRA)) {
            TRARailDailyTimetableListCacheDate = date;
            TRARailDailyTimetableListCache = railDailyTimetableList;
        } else if(transportation.equals(API.THSR)) {
            THSRRailDailyTimetableListCacheDate = date;
            THSRRailDailyTimetableListCache = railDailyTimetableList;
        }
    }


    public static List<TrainPath> getTranserPath(String transportation, String date, String takeTimeString, List<RailStation> railStationList, RailStation originStation, RailStation destinationStation, boolean isDirectArrival) throws ParseException, RouterException {
        List<TrainPath> trainPathList = new ArrayList<>();

        if(stationOfLineList == null) {
            stationOfLineList = API.getStationOfLine(API.TRA);
            StationOfLine.fixMissing15StationProblem(stationOfLineList);
            if(stationOfLineList == null) return null;
        }

        if((transportation == null) || (date == null) || (takeTimeString == null) || (railStationList == null) || (originStation == null) || (destinationStation == null)) throw new RouterException(RouterException.INPUT_OBJECT_IS_NULL);
        if(originStation.StationID.equals(destinationStation.StationID)) throw new RouterException(RouterException.ORIGINSTATION_EQUALS_DESTINATIONSTATION);

        Date takeTime = API.timeFormat.parse(takeTimeString);

        if(isDirectArrival) {
            List<RailDailyTimetable> railDailyTimetableList;

            if(transportation.equals(API.TRA_AND_THSR)) return getTranserPath(transportation, date, takeTimeString, railStationList, originStation, destinationStation, false);

            if((railDailyTimetableList = getFromCache(transportation, date)) == null) {
                if((railDailyTimetableList = API.getDailyTimetable(transportation, API.TRAIN_DATE, date)) == null) return null;
                seveToCache(transportation, date, railDailyTimetableList);
            }

            if((railDailyTimetableList = RailDailyTimetable.filterByOD(railDailyTimetableList, originStation, destinationStation, takeTime, true)) == null) return null;

            for(RailDailyTimetable railDailyTimetable:railDailyTimetableList) {
                TrainPath.TrainPathPart trainPathPart = new TrainPath.TrainPathPart(originStation, destinationStation, railDailyTimetable);
                TrainPath trainPath = new TrainPath(trainPathPart);
                trainPathList.add(trainPath);
            }
        } else {
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
                    return getTranserPath(API.THSR, date, takeTimeString, railStationList_THSR_ALL, originStation_THSR, destinationStation_THSR, true);
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

                                if((TRA_trainPath = getTranserPath(API.TRA, date, API.timeFormat.format(TRA_DepartureTime), railStationList_current, RailStation.transferStation(railStationList, lastStation_THSR), destinationStation, false)) == null) continue;

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
            } else if (transportation.equals(API.TRA)) {
                List<List<RailStation>> railStationList_List;
                List<RailDailyTimetable> railDailyTimetableList_all;

                if((railDailyTimetableList_all = getFromCache(transportation, date)) == null) {
                    if((railDailyTimetableList_all = API.getDailyTimetable(transportation, API.TRAIN_DATE, date)) == null) return null;
                    seveToCache(transportation, date, railDailyTimetableList_all);
                }

                if((railStationList_List = MyRailStation.getRailStationList(railStationList, originStation, destinationStation)) == null) return null;

                if((railStationList_List = RailStation.removeRepeatedRailStationList(railStationList_List)) == null) return null;

                for (List<RailStation> railStationList_current : railStationList_List) {
                    List<RailDailyTimetable> railDailyTimetableList;

                    if((railDailyTimetableList = RailDailyTimetable.filterByPathAndFirstDepartureTime(railDailyTimetableList_all, railStationList_current, true, 2, takeTime)) == null) continue;

                    for (RailDailyTimetable railDailyTimetable_mid : railDailyTimetableList) {
                        TrainPath trainPath = new TrainPath();
                        trainPath.trainPathPartList = new ArrayList<>();
                        RailDailyTimetable railDailyTimetable_first = null;
                        RailDailyTimetable railDailyTimetable_last = null;

                        Date firstTime, lastTime;
                        StopTime firstStopTime, lastStopTime;
                        RailStation firstRailStation, lastRailStation;

                        if((firstStopTime = railDailyTimetable_mid.findStopTime(railStationList_current)) == null) {
                            continue;
                        }
                        if((lastStopTime = railDailyTimetable_mid.findLastStopTime(railStationList_current)) == null) {
                            continue;
                        }

                        firstTime = API.timeFormat.parse(firstStopTime.DepartureTime);
                        if (railDailyTimetable_mid.afterOverNightStation(firstStopTime.StationID)) {
                            firstTime.setDate(firstTime.getDate() + 1);
                        }
                        lastTime = API.timeFormat.parse(lastStopTime.DepartureTime);
                        if (railDailyTimetable_mid.afterOverNightStation(lastStopTime.StationID)) {
                            lastTime.setDate(lastTime.getDate() + 1);
                        }

                        if (firstTime.after(lastTime)) {
                            continue;
                        }
                        if (firstTime.before(takeTime)) {
                            continue;
                        }

                        if((firstRailStation = RailStation.find(railStationList_current, firstStopTime.StationID)) == null) {
                            continue;
                        }
                        if((lastRailStation = RailStation.find(railStationList_current, lastStopTime.StationID)) == null) {
                            continue;
                        }

                        if (!firstStopTime.StationID.equals(originStation.StationID)) {
                            RailDailyTimetable railDailyTimetable_best = null;
                            Date originTime_best = null;
                            Date firstTimeThreshold = new Date(firstTime.getTime() - TRANSFER_TIME);

                            for (RailDailyTimetable railDailyTimetable_first_temp : railDailyTimetableList) {
                                StopTime originStopTime, arrivalFirstStopTime;
                                Date originTime, arrivalFirstTime;

                                if((originStopTime = railDailyTimetable_first_temp.getStopTimeOfStopTimes(originStation.StationID)) == null) continue;
                                if((arrivalFirstStopTime = railDailyTimetable_first_temp.getStopTimeOfStopTimes(firstStopTime.StationID)) == null) continue;

                                originTime = API.timeFormat.parse(originStopTime.DepartureTime);
                                if (railDailyTimetable_first_temp.afterOverNightStation(originStopTime.StationID)) {
                                    originTime.setDate(originTime.getDate() + 1);
                                }
                                arrivalFirstTime = API.timeFormat.parse(arrivalFirstStopTime.DepartureTime);
                                if (railDailyTimetable_first_temp.afterOverNightStation(arrivalFirstStopTime.StationID)) {
                                    arrivalFirstTime.setDate(arrivalFirstTime.getDate() + 1);
                                }

                                if (originTime.after(arrivalFirstTime)) {
                                    continue;
                                }

                                if (originTime.before(takeTime)) {
                                    continue;
                                }

                                if (arrivalFirstTime.after(firstTimeThreshold)) {
                                    continue;
                                }

                                if (railDailyTimetable_best == null) {
                                    railDailyTimetable_best = railDailyTimetable_first_temp;
                                    originTime_best = originTime;
                                } else if (originTime.after(originTime_best)) {
                                    railDailyTimetable_best = railDailyTimetable_first_temp;
                                    originTime_best = arrivalFirstTime;
                                }
                            }

                            if((railDailyTimetable_first = railDailyTimetable_best) == null) {
                                continue;
                            }
                        }

                        if (!lastRailStation.StationID.equals(destinationStation.StationID)) {
                            RailDailyTimetable railDailyTimetable_best = null;
                            Date destinationTime_best = null;
                            Date lastTimeThreshold = new Date(lastTime.getTime() + TRANSFER_TIME);

                            for (RailDailyTimetable railDailyTimetable_last_temp : railDailyTimetableList) {
                                StopTime departureLastStopTime, destinationStopTime;
                                Date departureLastTime, destinationTime;

                                if((departureLastStopTime = railDailyTimetable_last_temp.getStopTimeOfStopTimes(lastRailStation.StationID)) == null) {
                                    continue;
                                }
                                if((destinationStopTime = railDailyTimetable_last_temp.getStopTimeOfStopTimes(destinationStation.StationID)) == null) {
                                    continue;
                                }

                                departureLastTime = API.timeFormat.parse(departureLastStopTime.DepartureTime);
                                if (railDailyTimetable_last_temp.afterOverNightStation(departureLastStopTime.StationID)) {
                                    departureLastTime.setDate(departureLastTime.getDate() + 1);
                                }
                                destinationTime = API.timeFormat.parse(destinationStopTime.DepartureTime);
                                if (railDailyTimetable_last_temp.afterOverNightStation(destinationStopTime.StationID)) {
                                    destinationTime.setDate(destinationTime.getDate() + 1);
                                }

                                if (departureLastTime.after(destinationTime)) {
                                    continue;
                                }

                                if (departureLastTime.before(lastTimeThreshold)) {
                                    continue;
                                }

                                if (railDailyTimetable_best == null) {
                                    railDailyTimetable_best = railDailyTimetable_last_temp;
                                    destinationTime_best = destinationTime;
                                } else {
                                    if (destinationTime.before(destinationTime_best)) {
                                        railDailyTimetable_best = railDailyTimetable_last_temp;
                                        destinationTime_best = destinationTime;
                                    }
                                }
                            }

                            if((railDailyTimetable_last = railDailyTimetable_best) == null) {
                                continue;
                            }

                        }

                        if(railDailyTimetable_first != null) {
                            TrainPath.TrainPathPart trainPathPart_first = new TrainPath.TrainPathPart(originStation, firstRailStation, railDailyTimetable_first);
                            trainPath.trainPathPartList.add(trainPathPart_first);
                        }

                        {
                            TrainPath.TrainPathPart trainPathPart_mid = new TrainPath.TrainPathPart(firstRailStation, lastRailStation, railDailyTimetable_mid);
                            trainPath.trainPathPartList.add(trainPathPart_mid);
                        }

                        if (railDailyTimetable_last != null) {
                            TrainPath.TrainPathPart trainPathPart_last = new TrainPath.TrainPathPart(lastRailStation, destinationStation, railDailyTimetable_last);
                            trainPath.trainPathPartList.add(trainPathPart_last);
                        }

                        trainPathList.add(trainPath);
                    }
                }
            } else if(transportation.equals(API.THSR)) {
                return getTranserPath(transportation, date, takeTimeString, railStationList, originStation, destinationStation, true);
            }
        }

        trainPathList = TrainPath.filter(trainPathList);

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


        if(trainPathList.size() == 0) return null;

        return trainPathList;
    }
}

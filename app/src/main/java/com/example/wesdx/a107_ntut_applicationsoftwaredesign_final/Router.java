package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StationOfLine;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StopTime;

import java.security.interfaces.RSAKey;
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

    public static long TRANSFER_TIME = 1 * 60 * 1000;
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

    public static List<TrainPath> getTrainPath(String transportation, String date, Date originDepartureTime, Date destinationArrivalTime, List<RailDailyTimetable> railDailyTimetableList_input, final List<RailStation> railStationList, RailStation originStation, RailStation destinationStation, boolean isDirectArrival) throws ParseException, RouterException {
        List<TrainPath> trainPathList = new ArrayList<>();

        if(stationOfLineList == null) {
            stationOfLineList = API.getStationOfLine(API.TRA);
            StationOfLine.fixMissing15StationProblem(stationOfLineList);
            if(stationOfLineList == null) return null;
        }
        if((!isDirectArrival)&&(railStationList == null)) throw new RouterException(RouterException.INPUT_OBJECT_IS_NULL);
        if((transportation == null) || (date == null) || (originStation == null) || (destinationStation == null)) throw new RouterException(RouterException.INPUT_OBJECT_IS_NULL);
        if(originStation.StationID.equals(destinationStation.StationID)) throw new RouterException(RouterException.ORIGINSTATION_EQUALS_DESTINATIONSTATION);

        if(isDirectArrival) {
            List<RailDailyTimetable> railDailyTimetableList_temp;

            if(railDailyTimetableList_input == null) {
                if ((railDailyTimetableList_temp = getFromCache(transportation, date)) == null) {
                    if ((railDailyTimetableList_temp = API.getDailyTimetable(transportation, API.TRAIN_DATE, date)) == null)
                        return null;
                    seveToCache(transportation, date, railDailyTimetableList_temp);
                }
            } else {
                railDailyTimetableList_temp = new ArrayList<>(railDailyTimetableList_input);
            }

            if((railDailyTimetableList_temp = RailDailyTimetable.filterByOD(railDailyTimetableList_temp, originStation, destinationStation, originDepartureTime, destinationArrivalTime, true)) == null) return null;

            for(RailDailyTimetable railDailyTimetable:railDailyTimetableList_temp) {
                TrainPath.TrainPathPart trainPathPart = new TrainPath.TrainPathPart(originStation, destinationStation, railDailyTimetable);
                TrainPath trainPath = new TrainPath(trainPathPart);
                trainPathList.add(trainPath);
            }
        } else {
            if (transportation.equals(API.TRA_AND_THSR)) {
                List<RailStation> railStationList_THSR_ALL = API.getStation(API.THSR);//匯入高鐵所有站
                final List<RailStation> railStations_TRA_ALL = API.getStation(API.TRA);
                RailStation.removeUnreservationStation(railStations_TRA_ALL);
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

                if((originStation.OperatorID.equals("THSR") || (RailStation.transferStation(railStationList, originStation) != null)) && (destinationStation.OperatorID.equals("THSR") || (RailStation.transferStation(railStationList, destinationStation) != null))) {//如果起站跟終站都是高鐵的話不轉乘
                    trainPathList = getTrainPath(API.THSR, date, originDepartureTime, destinationArrivalTime, null, null, originStation.OperatorID.equals(API.THSR) ? originStation : RailStation.transferStation(railStationList, originStation), destinationStation.OperatorID.equals(API.THSR) ? destinationStation : RailStation.transferStation(railStationList, destinationStation), true);
                } else {
                    boolean boolorininStationisTHSR  = originStation.OperatorID.equals("THSR");//把輸入車站一律轉換成高鐵，若無法轉換則為null
                    boolean booldestinationStationisTHSR = destinationStation.OperatorID.equals("THSR");
                    List<RailStation> originStation_THSR_twoSide = new ArrayList<>();
                    List<RailStation> destinationStation_THSR_twoSide = new ArrayList<>();
                    RailStation firstRailStation = new RailStation();
                    RailStation lastRailStation = new RailStation();
                    RailStation originStation_TRA = new RailStation();
                    RailStation destinationStation_TRA = new RailStation();
                    RailStation originStation_THSR = new RailStation();
                    RailStation destinationStation_THSR = new RailStation();


                    if (boolorininStationisTHSR && booldestinationStationisTHSR) return null;

                    if (boolorininStationisTHSR) {
                        originStation_THSR_twoSide.add(originStation);
                        if (RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(0)) == null) {
                            originStation_THSR_twoSide = RailStation.getTwoSide(railStationList, originStation);
                        }
                        firstRailStation = originStation;
                    } else {
                        originStation_TRA = originStation;
                    }

                    if (booldestinationStationisTHSR) {
                        destinationStation_THSR_twoSide.add(destinationStation);
                        if (RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(0)) == null) {
                            destinationStation_THSR_twoSide = RailStation.getTwoSide(railStationList, destinationStation);
                        }
                        lastRailStation = destinationStation;
                    } else {
                        destinationStation_TRA = destinationStation;
                    }

                    if ((originStation_THSR_twoSide == null) || (destinationStation_THSR_twoSide == null))
                        return null;

                    if (originStation_THSR_twoSide.size() > 1) {
                        int min_THSR = 239;
                        int min_THSR_Index = 0;
                        for (int i = 0; i < originStation_THSR_twoSide.size(); i++) {
                            RailStation railStation_TRA_temp = RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(i));
                            if (railStation_TRA_temp == null) throw new RouterException("170");

                            List<List<RailStation>> railStation_list_list = MyRailStation.getRailStationList(railStations_TRA_ALL, railStation_TRA_temp, destinationStation);

                            int min = 239;
                            int minIndex = 0;

                            for (int j = 0; j < railStation_list_list.size(); j++) {
                                if (railStation_list_list.get(j).size() < min) {
                                    min = railStation_list_list.get(j).size();
                                    minIndex = j;
                                }
                            }

                            List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                            if (railStationList_THSR.size() < min_THSR) {
                                min_THSR = railStationList_THSR.size();
                                min_THSR_Index = i;
                                if (railStationList_THSR.size() > 0) {
                                    lastRailStation = railStationList_THSR.get(railStationList_THSR.size() - 1);
                                } else {
                                    lastRailStation = null;
                                }
                            }
                        }
                        originStation_TRA = RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(min_THSR_Index));
                    } else if (originStation_THSR_twoSide.size() == 1) {
                        RailStation firstRailStation_temp = RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(0));
                        firstRailStation = originStation_THSR_twoSide.get(0);

                        List<List<RailStation>> railStation_list_list = MyRailStation.getRailStationList(railStations_TRA_ALL, firstRailStation_temp, destinationStation);

                        int min = 239;
                        int minIndex = 0;

                        for (int j = 0; j < railStation_list_list.size(); j++) {
                            if (railStation_list_list.get(j).size() < min) {
                                min = railStation_list_list.get(j).size();
                                minIndex = j;
                            }
                        }

                        List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                        if (railStationList_THSR.size() > 0) {
                            lastRailStation = railStationList_THSR.get(railStationList_THSR.size() - 1);
                        } else {
                            lastRailStation = null;
                        }
                        originStation_TRA = RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(0));
                    }

                    if (destinationStation_THSR_twoSide.size() > 1) {
                        int min_THSR = 239;
                        int min_THSR_Index = 0;
                        for (int i = 0; i < destinationStation_THSR_twoSide.size(); i++) {
                            RailStation railStation_TRA_temp = RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(i));
                            if (railStation_TRA_temp == null) throw new RouterException("239");

                            List<List<RailStation>> railStation_list_list = MyRailStation.getRailStationList(railStations_TRA_ALL, originStation, railStation_TRA_temp);

                            int min = 239;
                            int minIndex = 0;

                            for (int j = 0; j < railStation_list_list.size(); j++) {
                                if (railStation_list_list.get(j).size() < min) {
                                    min = railStation_list_list.get(j).size();
                                    minIndex = j;
                                }
                            }

                            List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                            if (railStationList_THSR.size() < min_THSR) {
                                min_THSR = railStationList_THSR.size();
                                min_THSR_Index = i;
                                if (railStationList_THSR.size() > 0) {
                                    firstRailStation = railStationList_THSR.get(0);
                                } else {
                                    firstRailStation = null;
                                }
                            }
                        }
                        destinationStation_TRA = RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(min_THSR_Index));

                    } else if (destinationStation_THSR_twoSide.size() == 1) {
                        RailStation lastRailStation_temp = RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(0));
                        lastRailStation = destinationStation_THSR_twoSide.get(0);

                        List<List<RailStation>> railStation_list_list = MyRailStation.getRailStationList(railStations_TRA_ALL, originStation, lastRailStation_temp);

                        int min = 239;
                        int minIndex = 0;

                        for (int j = 0; j < railStation_list_list.size(); j++) {
                            if (railStation_list_list.get(j).size() < min) {
                                min = railStation_list_list.get(j).size();
                                minIndex = j;
                            }
                        }

                        List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                        if (railStationList_THSR.size() > 0) {
                            firstRailStation = railStationList_THSR.get(0);
                        } else {
                            firstRailStation = null;
                        }

                        destinationStation_TRA = RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(0));
                    }

                    if(!(boolorininStationisTHSR&&booldestinationStationisTHSR)) {

                        List<List<RailStation>> railStation_list_list = MyRailStation.getRailStationList(railStations_TRA_ALL, originStation_TRA, destinationStation_TRA);

                        int min = 239;
                        int minIndex = 0;

                        for (int j = 0; j < railStation_list_list.size(); j++) {
                            if (railStation_list_list.get(j).size() < min) {
                                min = railStation_list_list.get(j).size();
                                minIndex = j;
                            }
                        }

                        List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                        if((railStationList_THSR != null ? railStationList_THSR.size() : 0) == 0) {
                            return getTrainPath(API.TRA, date, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                        }
                        firstRailStation = railStationList_THSR.get(0);
                        lastRailStation = railStationList_THSR.get(railStationList_THSR.size()-1);
                    }

                    if((originStation.OperatorID.equals(API.THSR) ? originStation : firstRailStation).StationID.equals((destinationStation.OperatorID.equals(API.THSR) ? destinationStation : lastRailStation).StationID)&&(!(boolorininStationisTHSR&&booldestinationStationisTHSR))){
                        trainPathList = getTrainPath(API.TRA, date, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                    } else {

                        List<TrainPath> trainPathList_mid_all = getTrainPath(API.THSR, date, originDepartureTime, destinationArrivalTime, null, null, (originStation.OperatorID.equals(API.THSR) ? originStation : firstRailStation), (destinationStation.OperatorID.equals(API.THSR) ? destinationStation : lastRailStation), true);


                        for (TrainPath trainPath_mid : trainPathList_mid_all) {
                            TrainPath trainPath_first = null;
                            TrainPath trainPath_last = null;
                            RailStation firstRailStation_TRA = RailStation.transferStation(railStationList, trainPath_mid.getOriginRailStation());
                            RailStation lastRailStation_TRA = RailStation.transferStation(railStationList, trainPath_mid.getDestinationRailStation());
                            Date firstTime = trainPath_mid.getOriginDepartureTimeDate();
                            Date lastTime = trainPath_mid.getDestinationArrivalTimeDate();

                            TrainPath trainPath_temp = new TrainPath();
                            trainPath_temp.trainPathPartList = new ArrayList<>();

                            if ((originStation_TRA != null) && (firstRailStation_TRA != null)) {
                                if (!(originStation_TRA.StationID.equals(firstRailStation_TRA.StationID))) {
                                    Date firstTimeThreshold = new Date(firstTime.getTime() - TRANSFER_TIME);
                                    List<TrainPath> trainPathList_first;
                                    if ((trainPathList_first = getTrainPath(API.TRA, date, originDepartureTime, firstTimeThreshold, null, null, originStation_TRA, firstRailStation_TRA, true)) == null)
                                        continue;

                                    if ((trainPath_first = TrainPath.getBest(trainPathList_first, true, false)) == null)
                                        continue;
                                }
                            }

                            if ((destinationStation_TRA != null) && (lastRailStation_TRA != null)) {
                                if (!(destinationStation_TRA.StationID.equals(lastRailStation_TRA.StationID))) {
                                    Date lastTimeThreshold = new Date(lastTime.getTime() + TRANSFER_TIME);
                                    List<TrainPath> trainPathList_last;
                                    if ((trainPathList_last = getTrainPath(API.TRA, date, lastTimeThreshold, destinationArrivalTime, null, null, lastRailStation_TRA, destinationStation_TRA, true)) == null)
                                        continue;

                                    if ((trainPath_last = TrainPath.getBest(trainPathList_last, false, true)) == null)
                                        continue;
                                }
                            }

                            if (trainPath_first != null)
                                trainPath_temp.trainPathPartList.addAll(trainPath_first.trainPathPartList);

                            trainPath_temp.trainPathPartList.addAll(trainPath_mid.trainPathPartList);

                            if (trainPath_last != null)
                                trainPath_temp.trainPathPartList.addAll(trainPath_last.trainPathPartList);

                            trainPathList.add(trainPath_temp);
                        }
                        if(!(boolorininStationisTHSR&&booldestinationStationisTHSR)) {
                            List<TrainPath> trainPathList_temp = getTrainPath(API.TRA, date, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                            trainPathList.addAll(trainPathList_temp);
                        }
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
                if((railStationList_List = RailStation.filter(railStationList_List, 2)) == null) return null;

                for (List<RailStation> railStationList_current : railStationList_List) {

                    List<TrainPath> trainPathList_mid_all = TrainPath.filter(railDailyTimetableList_all, railStationList_current, originDepartureTime, destinationArrivalTime, true, 2);
                    List<RailDailyTimetable> railDailyTimetableList_mid_all = TrainPath.convert(trainPathList_mid_all);

                    for(TrainPath trainPath_mid:trainPathList_mid_all) {
                        TrainPath trainPath_first = null;
                        TrainPath trainPath_last = null;
                        RailStation firstRailStation = trainPath_mid.getOriginRailStation();
                        RailStation lastRailStation = trainPath_mid.getDestinationRailStation();
                        Date firstTime = trainPath_mid.getOriginDepartureTimeDate();
                        Date lastTime = trainPath_mid.getDestinationArrivalTimeDate();

                        TrainPath trainPath_temp = new TrainPath();
                        trainPath_temp.trainPathPartList = new ArrayList<>();

                        if (!trainPath_mid.getOriginRailStation().StationID.equals(originStation.StationID)) {
                            Date firstTimeThreshold = new Date(firstTime.getTime() - TRANSFER_TIME);
                            List<TrainPath> trainPathList_first;
                            if((trainPathList_first = getTrainPath(API.TRA, date, originDepartureTime, firstTimeThreshold, railDailyTimetableList_mid_all, null, originStation, firstRailStation, true)) == null) continue;

                            if((trainPath_first = TrainPath.getBest(trainPathList_first, true, false)) == null) continue;
                        }

                        if (!lastRailStation.StationID.equals(destinationStation.StationID)) {
                            Date lastTimeThreshold = new Date(lastTime.getTime() + TRANSFER_TIME);
                            List<TrainPath> trainPathList_last;
                            if((trainPathList_last = getTrainPath(API.TRA, date, lastTimeThreshold, destinationArrivalTime, railDailyTimetableList_mid_all, null, lastRailStation, destinationStation, true)) == null) continue;

                            if((trainPath_last = TrainPath.getBest(trainPathList_last, false, true)) == null) continue;
                        }

                        if(trainPath_first != null) trainPath_temp.trainPathPartList.addAll(trainPath_first.trainPathPartList);

                        trainPath_temp.trainPathPartList.addAll(trainPath_mid.trainPathPartList);

                        if(trainPath_last != null) trainPath_temp.trainPathPartList.addAll(trainPath_last.trainPathPartList);

                        trainPathList.add(trainPath_temp);
                    }
                }
            } else if(transportation.equals(API.THSR)) {
                trainPathList =  getTrainPath(API.THSR, date, originDepartureTime, destinationArrivalTime, null, null, originStation, destinationStation, true);
            }
        }

        trainPathList = TrainPath.filter(trainPathList);

        if((trainPathList != null ? trainPathList.size() : 0) == 0) return null;

        TrainPath.sort(trainPathList);

        return trainPathList;
    }
}

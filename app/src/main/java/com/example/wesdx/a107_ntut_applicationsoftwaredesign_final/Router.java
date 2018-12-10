package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StationOfLine;

import java.util.ArrayList;
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

    public static long TRAToTRATransferTime = 5 * 60 * 1000;
    public static long TRAToTHSRTransferTime = 10 * 60 * 1000;
    public static List<StationOfLine> stationOfLineList;

    private static List<RailStation> railStationListCache_TRA;
    private static List<RailStation> railStationListCache_THSR;
    private static String railDailyTimetableListCacheDate_TRA;
    private static List<RailDailyTimetable> railDailyTimetableListCache_TRA;
    private static String railDailyTimetableListCacheDate_THSR;
    private static List<RailDailyTimetable> railDailyTimetableListCache_THSR;

    public static List<RailStation> getRailStationListFromCache(String transportation) {
        if(transportation.equals(API.TRA)) {
            return new ArrayList<>(railStationListCache_TRA);
        } else if(transportation.equals(API.THSR)) {
            return new ArrayList<>(railStationListCache_THSR);
        }
        return null;
    }

    public static void saveRailStationListToCache(String transportation, List<RailStation> railStationList) {
        if(transportation.equals(API.TRA)) {
            railStationListCache_TRA = new ArrayList<>(railStationList);
        } else {
            railStationListCache_THSR = new ArrayList<>(railStationList);
        }
    }

    public static List<RailDailyTimetable> getFromCache(String transportation, String date) {
        if(transportation.equals(API.TRA) && date.equals(railDailyTimetableListCacheDate_TRA)) {
            return new ArrayList<>(railDailyTimetableListCache_TRA);
        } else if(transportation.equals(API.THSR) && date.equals(railDailyTimetableListCacheDate_THSR)) {
            return new ArrayList<>(railDailyTimetableListCache_THSR);
        }
        return null;
    }

    public static void seveToCache(String transportation, String date, List<RailDailyTimetable> railDailyTimetableList) {
        if(transportation.equals(API.TRA)) {
            railDailyTimetableListCacheDate_TRA = date;
            railDailyTimetableListCache_TRA = railDailyTimetableList;
        } else if(transportation.equals(API.THSR)) {
            railDailyTimetableListCacheDate_THSR = date;
            railDailyTimetableListCache_THSR = railDailyTimetableList;
        }
    }

    public static List<TrainPath> getTrainPath(final String transportation, final String date, final Date originDepartureTime, final Date destinationArrivalTime, final List<RailDailyTimetable> railDailyTimetableList_input, final List<RailStation> railStationList, final RailStation originStation, final RailStation destinationStation, final boolean isDirectArrival) throws Exception {
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
                    if ((railDailyTimetableList_temp = API.getDailyTimetable(transportation, API.TRAIN_DATE, date)) == null) return null;
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
                            Date firstTimeThreshold = new Date(firstTime.getTime() - TRAToTRATransferTime);
                            List<TrainPath> trainPathList_first;
                            if((trainPathList_first = getTrainPath(API.TRA, date, originDepartureTime, firstTimeThreshold, railDailyTimetableList_mid_all, null, originStation, firstRailStation, true)) == null) continue;

                            if((trainPath_first = TrainPath.getBest(trainPathList_first, true, false)) == null) continue;
                        }

                        if (!lastRailStation.StationID.equals(destinationStation.StationID)) {
                            Date lastTimeThreshold = new Date(lastTime.getTime() + TRAToTRATransferTime);
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

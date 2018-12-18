package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.MyClass;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailGeneralTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StationOfLine;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Router {
    public static long TRAToTRATransferTime = 5 * 60 * 1000;
    public static long TRAToTHSRTransferTime = 10 * 60 * 1000;
    public static List<StationOfLine> stationOfLineList;
    public static String railDailyTimetableListCacheDate_TRA;
    public static String railDailyTimetableListCacheDate_THSR;
    public static List<RailGeneralTimetable> railGeneralTimetableListCache_TRA;
    public static List<RailGeneralTimetable> railGeneralTimetableListCache_THSR;

    private static List<RailStation> railStationListCache_TRA;
    private static List<RailStation> railStationListCache_THSR;
    private static List<RailDailyTimetable> railDailyTimetableListCache_TRA;
    private static List<RailDailyTimetable> railDailyTimetableListCache_THSR;

    private static List<RailStation> getRailStationListFromCache(String transportation) {
        if(transportation.equals(API.TRA)) {
            return new ArrayList<>(railStationListCache_TRA);
        } else if(transportation.equals(API.THSR)) {
            return new ArrayList<>(railStationListCache_THSR);
        }
        return null;
    }

    private static List<RailDailyTimetable> getRailDailyTimetableList(String transportation){
        //upDateRailDailyTimetableList(transportation, date);
        if(transportation.equals(API.TRA)) {
            return new ArrayList<>(railDailyTimetableListCache_TRA);
        } else if(transportation.equals(API.THSR)) {
            return new ArrayList<>(railDailyTimetableListCache_THSR);
        }
        return null;
    }

    public static void saveRailStationListToCache(String transportation, List<RailStation> railStationList) {
        switch (transportation) {
            case API.TRA:
                railStationListCache_TRA = new ArrayList<>(railStationList);
                break;
            case API.THSR:
                railStationListCache_THSR = new ArrayList<>(railStationList);
                break;
            case API.TRA_AND_THSR:
                for (RailStation railStation : railStationList) {
                    if(railStation.OperatorID.equals(API.TRA)) {
                        if(railStationListCache_TRA == null) railStationListCache_TRA = new ArrayList<>();
                        railStationListCache_TRA.add(railStation);
                    } else if(railStation.OperatorID.equals(API.THSR)){
                        if(railStationListCache_THSR == null) railStationListCache_THSR = new ArrayList<>();
                        railStationListCache_THSR.add(railStation);
                    }
                }
                break;
        }
    }

    public static void saveRailDailyTimetableListToCache(String transportation, List<RailDailyTimetable> railDailyTimetableList, String date) {
        switch (transportation) {
            case API.TRA:
                railDailyTimetableListCache_TRA = new ArrayList<>(railDailyTimetableList);
                railDailyTimetableListCacheDate_TRA = date;
                break;
            case API.THSR:
                railDailyTimetableListCache_THSR = new ArrayList<>(railDailyTimetableList);
                railDailyTimetableListCacheDate_THSR = date;
                break;
        }
    }

    public static void saveRailGeneralTimetableToCache(String transportation, List<RailGeneralTimetable> railGeneralTimetableList) {
        switch (transportation) {
            case API.TRA:
                railGeneralTimetableListCache_TRA = new ArrayList<>(railGeneralTimetableList);
                break;
            case API.THSR:
                railGeneralTimetableListCache_THSR = new ArrayList<>(railGeneralTimetableList);
                break;
        }
    }

    public static List<TrainPath> getTrainPath(final String transportation, final Date originDepartureTime, final Date destinationArrivalTime, final List<RailDailyTimetable> railDailyTimetableList_input, final List<RailStation> railStationList, final RailStation originStation, final RailStation destinationStation, final boolean isDirectArrival) throws ParseException, MyException {
        List<TrainPath> trainPathList = new ArrayList<>();

        if((!isDirectArrival)&&(railStationList == null)) throw new MyException("Router 缺少參數");
        if((transportation == null) || (originStation == null) || (destinationStation == null)) throw new MyException("Router 缺少參數");
        if(originStation.StationID.equals(destinationStation.StationID)) throw new MyException("Router 起訖站相同");

        if(isDirectArrival) {
            List<RailDailyTimetable> railDailyTimetableList_temp;

            if(railDailyTimetableList_input == null) {
                if ((railDailyTimetableList_temp = getRailDailyTimetableList(transportation)) == null) return null;
            } else {
                railDailyTimetableList_temp = railDailyTimetableList_input;
            }

            if((railDailyTimetableList_temp = RailDailyTimetable.filterByOD(railDailyTimetableList_temp, originStation, destinationStation, originDepartureTime, destinationArrivalTime, true)) == null) return null;

            for(RailDailyTimetable railDailyTimetable:railDailyTimetableList_temp) {
                TrainPathPart trainPathPart = new TrainPathPart(originStation, destinationStation, railDailyTimetable);
                TrainPath trainPath = new TrainPath(trainPathPart);
                trainPathList.add(trainPath);
            }
        } else {
            switch (transportation) {
                case API.TRA_AND_THSR:
                    List<RailStation> railStationList_THSR_ALL;//匯入高鐵所有站

                    List<RailStation> railStations_TRA_ALL;

                    if ((railStations_TRA_ALL = getRailStationListFromCache(API.TRA)) == null) {
                        for (RailStation railStation_temp : railStationList) {
                            if (railStation_temp.OperatorID.equals(API.TRA)) {
                                railStations_TRA_ALL.add(railStation_temp);
                            }
                        }
                    }

                    if ((railStationList_THSR_ALL = getRailStationListFromCache(API.THSR)) == null) {
                        for (RailStation railStation_temp : railStationList) {
                            if (railStation_temp.OperatorID.equals(API.THSR)) {
                                railStationList_THSR_ALL.add(railStation_temp);
                            }
                        }
                    }

                    if ((originStation.OperatorID.equals("THSR") || (RailStation.transferStation(railStationList, originStation) != null)) && (destinationStation.OperatorID.equals("THSR") || (RailStation.transferStation(railStationList, destinationStation) != null))) {//如果起站跟終站都是高鐵的話不轉乘
                        trainPathList = getTrainPath(API.THSR, originDepartureTime, destinationArrivalTime, null, null, originStation.OperatorID.equals(API.THSR) ? originStation : RailStation.transferStation(railStationList, originStation), destinationStation.OperatorID.equals(API.THSR) ? destinationStation : RailStation.transferStation(railStationList, destinationStation), true);
                    } else {
                        boolean boolorininStationisTHSR = originStation.OperatorID.equals("THSR");//把輸入車站一律轉換成高鐵，若無法轉換則為null
                        boolean booldestinationStationisTHSR = destinationStation.OperatorID.equals("THSR");
                        boolean flag = false;
                        List<RailStation> originStation_THSR_twoSide = new ArrayList<>();
                        List<RailStation> destinationStation_THSR_twoSide = new ArrayList<>();
                        RailStation firstRailStation = new RailStation();
                        RailStation lastRailStation = new RailStation();
                        RailStation originStation_TRA = new RailStation();
                        RailStation destinationStation_TRA = new RailStation();


                        if (boolorininStationisTHSR && booldestinationStationisTHSR) return null;

                        if (boolorininStationisTHSR) {
                            flag = true;
                            originStation_THSR_twoSide.add(originStation);
                            if (RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(0)) == null) {
                                originStation_THSR_twoSide = RailStation.getTwoSide(railStationList, originStation);
                            }
                            firstRailStation = originStation;
                        } else {
                            originStation_TRA = originStation;
                            flag = RailStation.transferStation(railStationList, destinationStation) != null;
                        }

                        if (booldestinationStationisTHSR) {
                            flag = true;
                            destinationStation_THSR_twoSide.add(destinationStation);
                            if (RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(0)) == null) {
                                destinationStation_THSR_twoSide = RailStation.getTwoSide(railStationList, destinationStation);
                            }
                            lastRailStation = destinationStation;
                        } else {
                            destinationStation_TRA = destinationStation;
                            flag = RailStation.transferStation(railStationList, destinationStation) != null;
                        }

                        if ((originStation_THSR_twoSide == null) || (destinationStation_THSR_twoSide == null))
                            return null;

                        if (originStation_THSR_twoSide.size() > 1) {
                            int min_THSR = 239;
                            int min_THSR_Index = 0;
                            for (int i = 0; i < originStation_THSR_twoSide.size(); i++) {
                                RailStation railStation_TRA_temp = RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(i));
                                if (railStation_TRA_temp == null) throw new MyException("170");

                                List<List<RailStation>> railStation_list_list = MyRailStation.getRailStationList(railStations_TRA_ALL, railStation_TRA_temp, destinationStation);

                                if ((railStation_list_list = RailStation.removeRepeatedRailStationList(railStation_list_list)) == null)
                                    return null;
                                if ((railStation_list_list = RailStation.filter(railStation_list_list, 2)) == null)
                                    return null;
                                int min = 239;
                                int minIndex = 0;

                                for (int j = 0; j < railStation_list_list.size(); j++) {
                                    if (railStation_list_list.get(j).size() < min) {
                                        min = railStation_list_list.get(j).size();
                                        minIndex = j;
                                    }
                                }

                                List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                                if ((railStationList_THSR != null ? railStationList_THSR.size() : 0) < 2) {
                                    originStation_TRA = RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(min_THSR_Index));
                                    return getTrainPath(API.TRA, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                                }

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

                            if ((railStation_list_list = RailStation.removeRepeatedRailStationList(railStation_list_list)) == null)
                                return null;
                            if ((railStation_list_list = RailStation.filter(railStation_list_list, 2)) == null)
                                return null;
                            int min = 239;
                            int minIndex = 0;

                            for (int j = 0; j < railStation_list_list.size(); j++) {
                                if (railStation_list_list.get(j).size() < min) {
                                    min = railStation_list_list.get(j).size();
                                    minIndex = j;
                                }
                            }

                            List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來
                            RailStation.logD(railStation_list_list.get(minIndex));
                            if ((railStationList_THSR != null ? railStationList_THSR.size() : 0) < 2) {
                                originStation_TRA = RailStation.transferStation(railStationList, originStation_THSR_twoSide.get(0));
                                return getTrainPath(API.TRA, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                            }

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
                                if (railStation_TRA_temp == null) throw new MyException("239");

                                List<List<RailStation>> railStation_list_list = MyRailStation.getRailStationList(railStations_TRA_ALL, originStation, railStation_TRA_temp);

                                if ((railStation_list_list = RailStation.removeRepeatedRailStationList(railStation_list_list)) == null)
                                    return null;
                                if ((railStation_list_list = RailStation.filter(railStation_list_list, 2)) == null)
                                    return null;
                                int min = 239;
                                int minIndex = 0;

                                for (int j = 0; j < railStation_list_list.size(); j++) {
                                    if (railStation_list_list.get(j).size() < min) {
                                        min = railStation_list_list.get(j).size();
                                        minIndex = j;
                                    }
                                }

                                List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                                if ((railStationList_THSR != null ? railStationList_THSR.size() : 0) < 2) {
                                    destinationStation_TRA = RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(min_THSR_Index));
                                    return getTrainPath(API.TRA, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                                }

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

                            if ((railStation_list_list = RailStation.removeRepeatedRailStationList(railStation_list_list)) == null)
                                return null;
                            if ((railStation_list_list = RailStation.filter(railStation_list_list, 2)) == null)
                                return null;
                            int min = 239;
                            int minIndex = 0;

                            for (int j = 0; j < railStation_list_list.size(); j++) {
                                if (railStation_list_list.get(j).size() < min) {
                                    min = railStation_list_list.get(j).size();
                                    minIndex = j;
                                }
                            }

                            List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                            if ((railStationList_THSR != null ? railStationList_THSR.size() : 0) < 2) {
                                destinationStation_TRA = RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(0));
                                return getTrainPath(API.TRA, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                            }

                            if (railStationList_THSR.size() > 0) {
                                firstRailStation = railStationList_THSR.get(0);
                            } else {
                                firstRailStation = null;
                            }

                            destinationStation_TRA = RailStation.transferStation(railStationList, destinationStation_THSR_twoSide.get(0));
                        }

                        if (!(boolorininStationisTHSR || booldestinationStationisTHSR)) {

                            List<List<RailStation>> railStation_list_list = MyRailStation.getRailStationList(railStations_TRA_ALL, originStation_TRA, destinationStation_TRA);

                            if ((railStation_list_list = RailStation.removeRepeatedRailStationList(railStation_list_list)) == null)
                                return null;
                            if ((railStation_list_list = RailStation.filter(railStation_list_list, 2)) == null)
                                return null;
                            int min = 239;
                            int minIndex = 0;

                            for (int j = 0; j < railStation_list_list.size(); j++) {
                                if (railStation_list_list.get(j).size() < min) {

                                    min = railStation_list_list.get(j).size();
                                    minIndex = j;
                                }
                            }

                            List<RailStation> railStationList_THSR = RailStation.filterTHSR(railStation_list_list.get(minIndex), railStationList);//在臺鐵當下班次裡把高鐵有經過的站列出來

                            if ((railStationList_THSR != null ? railStationList_THSR.size() : 0) < 2) {
                                return getTrainPath(API.TRA, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                            }
                            firstRailStation = railStationList_THSR.get(0);
                            lastRailStation = railStationList_THSR.get(railStationList_THSR.size() - 1);
                        }

                        if ((originStation.OperatorID.equals(API.THSR) ? originStation : firstRailStation).StationID.equals((destinationStation.OperatorID.equals(API.THSR) ? destinationStation : lastRailStation).StationID) && (!(boolorininStationisTHSR || booldestinationStationisTHSR))) {
                            trainPathList = getTrainPath(API.TRA, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false);
                        } else {

                            List<TrainPath> trainPathList_mid_all = getTrainPath(API.THSR, originDepartureTime, destinationArrivalTime, null, null, (originStation.OperatorID.equals(API.THSR) ? originStation : firstRailStation), (destinationStation.OperatorID.equals(API.THSR) ? destinationStation : lastRailStation), true);

                            for (TrainPath trainPath_mid : trainPathList_mid_all) {
                                TrainPath trainPath_first = null;
                                TrainPath trainPath_last = null;
                                RailStation firstRailStation_TRA = RailStation.transferStation(railStationList, trainPath_mid.getOriginRailStation());
                                RailStation lastRailStation_TRA = RailStation.transferStation(railStationList, trainPath_mid.getDestinationRailStation());
                                Date firstTime = trainPath_mid.getOriginDepartureTimeDate();
                                Date lastTime = trainPath_mid.getDestinationArrivalTimeDate();

                                TrainPath trainPath_temp = new TrainPath();
                                trainPath_temp.trainPathPartList = new ArrayList<>();

                                if ((originStation_TRA != null) && (firstRailStation_TRA != null) && !flag) {
                                    if (!(originStation_TRA.StationID.equals(firstRailStation_TRA.StationID))) {
                                        Date firstTimeThreshold = new Date(firstTime.getTime() - TRAToTHSRTransferTime);
                                        List<TrainPath> trainPathList_first;
                                        if ((trainPathList_first = getTrainPath(API.TRA, originDepartureTime, firstTimeThreshold, null, null, originStation_TRA, firstRailStation_TRA, true)) == null)
                                            continue;

                                        if ((trainPath_first = TrainPath.getBest(trainPathList_first, true, false)) == null)
                                            continue;
                                    }
                                }

                                if ((destinationStation_TRA != null) && (lastRailStation_TRA != null) && !flag) {
                                    if (!(destinationStation_TRA.StationID.equals(lastRailStation_TRA.StationID))) {
                                        Date lastTimeThreshold = new Date(lastTime.getTime() + TRAToTHSRTransferTime);
                                        List<TrainPath> trainPathList_last;
                                        if ((trainPathList_last = getTrainPath(API.TRA, lastTimeThreshold, destinationArrivalTime, null, null, lastRailStation_TRA, destinationStation_TRA, true)) == null)
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
                            if (!((originStation_THSR_twoSide.size() > 1) || (destinationStation_THSR_twoSide.size() > 1))) {
                                List<TrainPath> trainPathList_temp;
                                if ((trainPathList_temp = getTrainPath(API.TRA, originDepartureTime, destinationArrivalTime, null, railStations_TRA_ALL, originStation_TRA, destinationStation_TRA, false)) != null) {
                                    trainPathList.addAll(trainPathList_temp);
                                }
                            }
                        }
                    }
                    break;
                case API.TRA:
                    List<List<RailStation>> railStationList_List;
                    List<RailDailyTimetable> railDailyTimetableList_all;

                    if ((railDailyTimetableList_all = getRailDailyTimetableList(transportation)) == null)
                        return null;

                    if ((railStationList_List = MyRailStation.getRailStationList(railStationList, originStation, destinationStation)) == null)
                        return null;

                    if ((railStationList_List = RailStation.removeRepeatedRailStationList(railStationList_List)) == null)
                        return null;
                    if ((railStationList_List = RailStation.filter(railStationList_List, 2)) == null)
                        return null;

                    for (List<RailStation> railStationList_current : railStationList_List) {
                        RailStation.logD(railStationList_current);
                        List<TrainPath> trainPathList_mid_all = TrainPath.filter(railDailyTimetableList_all, railStationList_current, originDepartureTime, destinationArrivalTime, true, 2);
                        List<RailDailyTimetable> railDailyTimetableList_mid_all = TrainPath.convert(trainPathList_mid_all);

                        for (TrainPath trainPath_mid : trainPathList_mid_all) {
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
                                if ((trainPathList_first = getTrainPath(API.TRA, originDepartureTime, firstTimeThreshold, railDailyTimetableList_mid_all, null, originStation, firstRailStation, true)) == null)
                                    continue;

                                if ((trainPath_first = TrainPath.getBest(trainPathList_first, true, false)) == null)
                                    continue;
                            }

                            if (!lastRailStation.StationID.equals(destinationStation.StationID)) {
                                Date lastTimeThreshold = new Date(lastTime.getTime() + TRAToTRATransferTime);
                                List<TrainPath> trainPathList_last;
                                if ((trainPathList_last = getTrainPath(API.TRA, lastTimeThreshold, destinationArrivalTime, railDailyTimetableList_mid_all, null, lastRailStation, destinationStation, true)) == null)
                                    continue;

                                if ((trainPath_last = TrainPath.getBest(trainPathList_last, false, true)) == null)
                                    continue;
                            }

                            if (trainPath_first != null)
                                trainPath_temp.trainPathPartList.addAll(trainPath_first.trainPathPartList);

                            trainPath_temp.trainPathPartList.addAll(trainPath_mid.trainPathPartList);

                            if (trainPath_last != null)
                                trainPath_temp.trainPathPartList.addAll(trainPath_last.trainPathPartList);

                            trainPathList.add(trainPath_temp);
                        }
                    }
                    break;
                case API.THSR:
                    trainPathList = getTrainPath(API.THSR, originDepartureTime, destinationArrivalTime, null, null, originStation, destinationStation, true);
                    break;
            }
        }

        trainPathList = TrainPath.filter(trainPathList);

        if((trainPathList != null ? trainPathList.size() : 0) == 0) return null;

        TrainPath.sort(trainPathList);

        return trainPathList;
    }
}

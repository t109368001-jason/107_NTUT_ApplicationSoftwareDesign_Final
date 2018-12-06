package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.LineStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StationOfLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyRailStation {

    public static List<List<RailStation>> getRailStationList(List<RailStation> railStationList, RailStation originStation, RailStation destinationStation) {
        List<List<RailStation>> out = null;

        final StationOfLine originStationOfLine = StationOfLine.getStationOfLine(Router.stationOfLineList, originStation.StationID);
        final StationOfLine destinationStationOfLine = StationOfLine.getStationOfLine(Router.stationOfLineList, destinationStation.StationID);

        final StationOfLine E_EL = StationOfLine.getStationIndexOfStationsByLineNo(Router.stationOfLineList, StationOfLine.E_EL);
        final StationOfLine W_TL_N = StationOfLine.getStationIndexOfStationsByLineNo(Router.stationOfLineList, StationOfLine.W_TL_N);
        final StationOfLine W_TL_M = StationOfLine.getStationIndexOfStationsByLineNo(Router.stationOfLineList, StationOfLine.W_TL_M);
        final StationOfLine W_TL_C = StationOfLine.getStationIndexOfStationsByLineNo(Router.stationOfLineList, StationOfLine.W_TL_C);
        final StationOfLine W_TL_S = StationOfLine.getStationIndexOfStationsByLineNo(Router.stationOfLineList, StationOfLine.W_TL_S);
        final StationOfLine W_PL = StationOfLine.getStationIndexOfStationsByLineNo(Router.stationOfLineList, StationOfLine.W_PL);
        final StationOfLine S_SL = StationOfLine.getStationIndexOfStationsByLineNo(Router.stationOfLineList, StationOfLine.S_SL);

        if(originStationOfLine.LineNo.equals(destinationStationOfLine.LineNo)) {
            boolean startAddToNewList = false;
            boolean originInStationsIsStart = false;
            List<RailStation> railStationList_same = null;
            for(LineStation lineStation:originStationOfLine.Stations) {
                if(!startAddToNewList) {
                    if(lineStation.StationID.equals(originStation.StationID)) {
                        originInStationsIsStart = true;
                        startAddToNewList = true;
                        railStationList_same = new ArrayList<>();
                        railStationList_same.add(RailStation.find(railStationList, lineStation.StationID));
                    } else if(lineStation.StationID.equals(destinationStation.StationID)) {
                        originInStationsIsStart = false;
                        startAddToNewList = true;
                        railStationList_same = new ArrayList<>();
                        railStationList_same.add(RailStation.find(railStationList, lineStation.StationID));
                    }
                } else {
                    railStationList_same.add(RailStation.find(railStationList, lineStation.StationID));
                    if(originInStationsIsStart) {
                        if(lineStation.StationID.equals(destinationStation.StationID)) {
                            break;
                        }
                    } else {
                        if(lineStation.StationID.equals(originStation.StationID)) {
                            break;
                        }
                    }
                }
            }
            if(!originInStationsIsStart) {
                Collections.reverse(railStationList_same);
            }
            out = new ArrayList<>();
            out.add(railStationList_same);
        } else {
            RailStation beginOfMainLine, endOfMainLine;
            List<RailStation> railStationList_front = new ArrayList<>();
            List<RailStation> railStationList_tail = new ArrayList<>();
            if(originStationOfLine.isBrenchLine()) {
                for(int i = originStationOfLine.getStationIndexOfStationsByID(originStation.StationID); i >= 0; i--) {
                    railStationList_front.add(RailStation.find(railStationList, originStationOfLine.Stations.get(i).StationID));
                }
                beginOfMainLine = RailStation.find(railStationList, originStationOfLine.Stations.get(0).StationID);
            } else {
                beginOfMainLine = originStation;
            }
            if(beginOfMainLine == null) {
                return null;
            }

            if(destinationStationOfLine.isBrenchLine()) {
                for(int i = 0; i <= destinationStationOfLine.getStationIndexOfStationsByID(destinationStation.StationID); i++) {
                    railStationList_tail.add(RailStation.find(railStationList, destinationStationOfLine.Stations.get(i).StationID));
                }
                endOfMainLine = RailStation.find(railStationList, destinationStationOfLine.Stations.get(0).StationID);
            } else {
                endOfMainLine = destinationStation;
            }

            if(endOfMainLine == null) {
                return null;
            }

            final StationOfLine begStationOfLine = StationOfLine.getStationOfLine(Router.stationOfLineList, beginOfMainLine.StationID);
            final StationOfLine endStationOfLine = StationOfLine.getStationOfLine(Router.stationOfLineList, endOfMainLine.StationID);

            if(begStationOfLine.LineNo.equals(StationOfLine.W_TL_C)||begStationOfLine.LineNo.equals(StationOfLine.W_TL_M)||endStationOfLine.LineNo.equals(StationOfLine.W_TL_C)||endStationOfLine.LineNo.equals(StationOfLine.W_TL_M)) {
                List<RailStation> railStationList_C = new ArrayList<>();
                List<RailStation> railStationList_CC = new ArrayList<>();

                StationOfLine currentStationOfLine_C = new StationOfLine(begStationOfLine);
                StationOfLine currentStationOfLine_CC = new StationOfLine(begStationOfLine);

                boolean C_finish = false;
                boolean CC_finish = false;
                boolean C_ori_pass = false;
                boolean CC_ori_pass = false;

                while(!(C_finish&&CC_finish)) {
                    if(!C_finish) {
                        int begIndex_C;
                        int endIndex_C;
                        switch (currentStationOfLine_C.LineNo) {
                            case StationOfLine.W_TL_N:
                                Collections.reverse(currentStationOfLine_C.Stations);
                                break;
                            case StationOfLine.E_EL:
                                break;
                            case StationOfLine.S_SL:
                                Collections.reverse(currentStationOfLine_C.Stations);
                                break;
                            case StationOfLine.W_PL:
                                Collections.reverse(currentStationOfLine_C.Stations);
                                break;
                            case StationOfLine.W_TL_S:
                                Collections.reverse(currentStationOfLine_C.Stations);
                                break;
                            case StationOfLine.W_TL_C:
                                Collections.reverse(currentStationOfLine_C.Stations);
                                break;
                            case StationOfLine.W_TL_M:
                                Collections.reverse(currentStationOfLine_C.Stations);
                                break;
                        }

                        if((currentStationOfLine_C.LineNo.equals(begStationOfLine.LineNo))&&(!C_ori_pass)) {
                            begIndex_C = currentStationOfLine_C.getStationIndexOfStationsByID(beginOfMainLine.StationID);
                            C_ori_pass = true;
                        } else {
                            begIndex_C = 0;
                        }
                        if(currentStationOfLine_C.LineNo.equals(endStationOfLine.LineNo)) {
                            endIndex_C = currentStationOfLine_C.getStationIndexOfStationsByID(endOfMainLine.StationID);
                        } else {
                            endIndex_C = currentStationOfLine_C.Stations.size() - 1;
                        }

                        for(int i = begIndex_C; i <= endIndex_C; i++) {
                            railStationList_C.add(RailStation.find(railStationList, currentStationOfLine_C.Stations.get(i).StationID));
                            if(currentStationOfLine_C.Stations.get(i).StationID.equals(endOfMainLine.StationID)) {
                                C_finish = true;
                                break;
                            }
                        }

                        switch (currentStationOfLine_C.LineNo) {
                            case StationOfLine.W_TL_C:
                            case StationOfLine.W_TL_M:
                                currentStationOfLine_C = new StationOfLine(W_TL_N);
                                break;
                            case StationOfLine.W_TL_N:
                                currentStationOfLine_C = new StationOfLine(E_EL);
                                break;
                            case StationOfLine.E_EL:
                                currentStationOfLine_C = new StationOfLine(S_SL);
                                break;
                            case StationOfLine.S_SL:
                                currentStationOfLine_C = new StationOfLine(W_PL);
                                break;
                            case StationOfLine.W_PL:
                                currentStationOfLine_C = new StationOfLine(W_TL_S);
                                break;
                            default:
                                currentStationOfLine_C = new StationOfLine(endStationOfLine);
                                break;
                        }
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(!CC_finish) {
                        int begIndex_CC;
                        int endIndex_CC;

                        switch (currentStationOfLine_CC.LineNo) {
                            case StationOfLine.W_TL_N:
                                break;
                            case StationOfLine.E_EL:
                                Collections.reverse(currentStationOfLine_CC.Stations);
                                break;
                            case StationOfLine.S_SL:
                                break;
                            case StationOfLine.W_PL:
                                break;
                            case StationOfLine.W_TL_S:
                                break;
                            case StationOfLine.W_TL_C:
                                break;
                            case StationOfLine.W_TL_M:
                                break;
                        }
                        if((currentStationOfLine_CC.LineNo.equals(begStationOfLine.LineNo))&&(!CC_ori_pass)) {
                            begIndex_CC = currentStationOfLine_CC.getStationIndexOfStationsByID(beginOfMainLine.StationID);
                            CC_ori_pass = true;
                        } else {
                            begIndex_CC = 0;
                        }
                        if(currentStationOfLine_CC.LineNo.equals(endStationOfLine.LineNo)) {
                            endIndex_CC = currentStationOfLine_CC.getStationIndexOfStationsByID(endOfMainLine.StationID);
                        } else {
                            endIndex_CC = currentStationOfLine_CC.Stations.size() - 1;
                        }
                        for(int i = begIndex_CC; i <= endIndex_CC; i++) {
                            railStationList_CC.add(RailStation.find(railStationList, currentStationOfLine_CC.Stations.get(i).StationID));
                            if(currentStationOfLine_CC.Stations.get(i).StationID.equals(endOfMainLine.StationID)) {
                                CC_finish = true;
                                break;
                            }
                        }

                        switch (currentStationOfLine_CC.LineNo) {
                            case StationOfLine.W_TL_C:
                            case StationOfLine.W_TL_M:
                                currentStationOfLine_CC = new StationOfLine(W_TL_S);
                                break;
                            case StationOfLine.W_TL_S:
                                currentStationOfLine_CC = new StationOfLine(W_PL);
                                break;
                            case StationOfLine.W_PL:
                                currentStationOfLine_CC = new StationOfLine(S_SL);
                                break;
                            case StationOfLine.S_SL:
                                currentStationOfLine_CC = new StationOfLine(E_EL);
                                break;
                            case StationOfLine.E_EL:
                                currentStationOfLine_CC = new StationOfLine(W_TL_N);
                                break;
                            case StationOfLine.W_TL_N:
                                currentStationOfLine_CC = new StationOfLine(endStationOfLine);
                                break;
                        }
                    }
                }

                out = new ArrayList<>();
                List<RailStation> temp_C = new ArrayList<>(railStationList_front);
                List<RailStation> temp_CC = new ArrayList<>(railStationList_front);

                temp_C.addAll(railStationList_C);
                temp_CC.addAll(railStationList_CC);
                temp_C.addAll(railStationList_tail);
                temp_CC.addAll(railStationList_tail);

                out.add(temp_C);
                out.add(temp_CC);
            } else {
                List<RailStation> railStationList_WC = new ArrayList<>();
                List<RailStation> railStationList_WCC = new ArrayList<>();
                List<RailStation> railStationList_CC = new ArrayList<>();
                List<RailStation> railStationList_CCC = new ArrayList<>();

                StationOfLine currentStationOfLine_WC = begStationOfLine;
                StationOfLine currentStationOfLine_WCC = begStationOfLine;
                StationOfLine currentStationOfLine_CC = begStationOfLine;
                StationOfLine currentStationOfLine_CCC = begStationOfLine;
                boolean WC_finish = false;
                boolean WCC_finish = false;
                boolean CC_finish = false;
                boolean CCC_finish = false;

                boolean WC_ori_pass = false;
                boolean WCC_ori_pass = false;
                boolean CC_ori_pass = false;
                boolean CCC_ori_pass = false;

                while(!(WC_finish&&WCC_finish&&CC_finish&&CCC_finish)) {
                    if(!WC_finish) {
                        int begIndex_WC;
                        int endIndex_WC;
                        switch (currentStationOfLine_WC.LineNo) {
                            case StationOfLine.W_TL_N:
                                Collections.reverse(currentStationOfLine_WC.Stations);
                                break;
                            case StationOfLine.E_EL:
                                break;
                            case StationOfLine.S_SL:
                                Collections.reverse(currentStationOfLine_WC.Stations);
                                break;
                            case StationOfLine.W_PL:
                                Collections.reverse(currentStationOfLine_WC.Stations);
                                break;
                            case StationOfLine.W_TL_S:
                                Collections.reverse(currentStationOfLine_WC.Stations);
                                break;
                            case StationOfLine.W_TL_C:
                                Collections.reverse(currentStationOfLine_WC.Stations);
                                break;
                            case StationOfLine.W_TL_M:
                                Collections.reverse(currentStationOfLine_WC.Stations);
                                break;
                        }

                        if((currentStationOfLine_WC.LineNo.equals(begStationOfLine.LineNo))&&(!WC_ori_pass)) {
                            begIndex_WC = currentStationOfLine_WC.getStationIndexOfStationsByID(beginOfMainLine.StationID);
                            WC_ori_pass = true;
                        } else {
                            begIndex_WC = 0;
                        }
                        if(currentStationOfLine_WC.LineNo.equals(endStationOfLine.LineNo)) {
                            endIndex_WC = currentStationOfLine_WC.getStationIndexOfStationsByID(endOfMainLine.StationID);
                        } else {
                            endIndex_WC = currentStationOfLine_WC.Stations.size() - 1;
                        }

                        for(int i = begIndex_WC; i <= endIndex_WC; i++) {
                            railStationList_WC.add(RailStation.find(railStationList, currentStationOfLine_WC.Stations.get(i).StationID));
                            if(currentStationOfLine_WC.Stations.get(i).StationID.equals(endOfMainLine.StationID)) {
                                WC_finish = true;
                                break;
                            }
                        }

                        switch (currentStationOfLine_WC.LineNo) {
                            case StationOfLine.W_TL_C:
                            case StationOfLine.W_TL_M:
                                currentStationOfLine_WC = new StationOfLine(W_TL_N);
                                break;
                            case StationOfLine.W_TL_N:
                                currentStationOfLine_WC = new StationOfLine(E_EL);
                                break;
                            case StationOfLine.E_EL:
                                currentStationOfLine_WC = new StationOfLine(S_SL);
                                break;
                            case StationOfLine.S_SL:
                                currentStationOfLine_WC = new StationOfLine(W_PL);
                                break;
                            case StationOfLine.W_PL:
                                currentStationOfLine_WC = new StationOfLine(W_TL_S);
                                break;
                            case StationOfLine.W_TL_S:
                                currentStationOfLine_WC = new StationOfLine(W_TL_M);
                                break;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(!CC_finish) {
                        int begIndex_CC;
                        int endIndex_CC;
                        switch (currentStationOfLine_CC.LineNo) {
                            case StationOfLine.W_TL_N:
                                Collections.reverse(currentStationOfLine_CC.Stations);
                                break;
                            case StationOfLine.E_EL:
                                break;
                            case StationOfLine.S_SL:
                                Collections.reverse(currentStationOfLine_CC.Stations);
                                break;
                            case StationOfLine.W_PL:
                                Collections.reverse(currentStationOfLine_CC.Stations);
                                break;
                            case StationOfLine.W_TL_S:
                                Collections.reverse(currentStationOfLine_CC.Stations);
                                break;
                            case StationOfLine.W_TL_C:
                                Collections.reverse(currentStationOfLine_CC.Stations);
                                break;
                            case StationOfLine.W_TL_M:
                                Collections.reverse(currentStationOfLine_CC.Stations);
                                break;
                        }

                        if((currentStationOfLine_CC.LineNo.equals(begStationOfLine.LineNo))&&(!CC_ori_pass)) {
                            begIndex_CC = currentStationOfLine_CC.getStationIndexOfStationsByID(beginOfMainLine.StationID);
                            CC_ori_pass = true;
                        } else {
                            begIndex_CC = 0;
                        }
                        if(currentStationOfLine_CC.LineNo.equals(endStationOfLine.LineNo)) {
                            endIndex_CC = currentStationOfLine_CC.getStationIndexOfStationsByID(endOfMainLine.StationID);
                        } else {
                            endIndex_CC = currentStationOfLine_CC.Stations.size() - 1;
                        }

                        for(int i = begIndex_CC; i <= endIndex_CC; i++) {
                            railStationList_CC.add(RailStation.find(railStationList, currentStationOfLine_CC.Stations.get(i).StationID));
                            if(currentStationOfLine_CC.Stations.get(i).StationID.equals(endOfMainLine.StationID)) {
                                CC_finish = true;
                                break;
                            }
                        }

                        switch (currentStationOfLine_CC.LineNo) {
                            case StationOfLine.W_TL_C:
                            case StationOfLine.W_TL_M:
                                currentStationOfLine_CC = new StationOfLine(W_TL_N);
                                break;
                            case StationOfLine.W_TL_N:
                                currentStationOfLine_CC = new StationOfLine(E_EL);
                                break;
                            case StationOfLine.E_EL:
                                currentStationOfLine_CC = new StationOfLine(S_SL);
                                break;
                            case StationOfLine.S_SL:
                                currentStationOfLine_CC = new StationOfLine(W_PL);
                                break;
                            case StationOfLine.W_PL:
                                currentStationOfLine_CC = new StationOfLine(W_TL_S);
                                break;
                            case StationOfLine.W_TL_S:
                                currentStationOfLine_CC = new StationOfLine(W_TL_C);
                                break;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(!WCC_finish) {
                        int begIndex_WCC;
                        int endIndex_WCC;

                        switch (currentStationOfLine_WCC.LineNo) {
                            case StationOfLine.W_TL_N:
                                break;
                            case StationOfLine.E_EL:
                                Collections.reverse(currentStationOfLine_WCC.Stations);
                                break;
                            case StationOfLine.S_SL:
                                break;
                            case StationOfLine.W_PL:
                                break;
                            case StationOfLine.W_TL_S:
                                break;
                            case StationOfLine.W_TL_C:
                                break;
                            case StationOfLine.W_TL_M:
                                break;
                        }
                        if((currentStationOfLine_WCC.LineNo.equals(begStationOfLine.LineNo))&&(!WCC_ori_pass)) {
                            begIndex_WCC = currentStationOfLine_WCC.getStationIndexOfStationsByID(beginOfMainLine.StationID);
                            WCC_ori_pass = true;
                        } else {
                            begIndex_WCC = 0;
                        }
                        if(currentStationOfLine_WCC.LineNo.equals(endStationOfLine.LineNo)) {
                            endIndex_WCC = currentStationOfLine_WCC.getStationIndexOfStationsByID(endOfMainLine.StationID);
                        } else {
                            endIndex_WCC = currentStationOfLine_WCC.Stations.size() - 1;
                        }
                        for(int i = begIndex_WCC; i <= endIndex_WCC; i++) {
                            railStationList_WCC.add(RailStation.find(railStationList, currentStationOfLine_WCC.Stations.get(i).StationID));
                            if(currentStationOfLine_WCC.Stations.get(i).StationID.equals(endOfMainLine.StationID)) {
                                WCC_finish = true;
                                break;
                            }
                        }

                        switch (currentStationOfLine_WCC.LineNo) {
                            case StationOfLine.W_TL_C:
                            case StationOfLine.W_TL_M:
                                currentStationOfLine_WCC = new StationOfLine(W_TL_S);
                                break;
                            case StationOfLine.W_TL_S:
                                currentStationOfLine_WCC = new StationOfLine(W_PL);
                                break;
                            case StationOfLine.W_PL:
                                currentStationOfLine_WCC = new StationOfLine(S_SL);
                                break;
                            case StationOfLine.S_SL:
                                currentStationOfLine_WCC = new StationOfLine(E_EL);
                                break;
                            case StationOfLine.E_EL:
                                currentStationOfLine_WCC = new StationOfLine(W_TL_N);
                                break;
                            case StationOfLine.W_TL_N:
                                currentStationOfLine_WCC = new StationOfLine(W_TL_M);
                                break;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(!CCC_finish) {
                        int begIndex_CCC;
                        int endIndex_CCC;

                        switch (currentStationOfLine_CCC.LineNo) {
                            case StationOfLine.W_TL_N:
                                break;
                            case StationOfLine.E_EL:
                                Collections.reverse(currentStationOfLine_CCC.Stations);
                                break;
                            case StationOfLine.S_SL:
                                break;
                            case StationOfLine.W_PL:
                                break;
                            case StationOfLine.W_TL_S:
                                break;
                            case StationOfLine.W_TL_C:
                                break;
                            case StationOfLine.W_TL_M:
                                break;
                        }
                        if((currentStationOfLine_CCC.LineNo.equals(begStationOfLine.LineNo))&&(!CCC_ori_pass)) {
                            begIndex_CCC = currentStationOfLine_CCC.getStationIndexOfStationsByID(beginOfMainLine.StationID);
                            CCC_ori_pass = true;
                        } else {
                            begIndex_CCC = 0;
                        }
                        if(currentStationOfLine_CCC.LineNo.equals(endStationOfLine.LineNo)) {
                            endIndex_CCC = currentStationOfLine_CCC.getStationIndexOfStationsByID(endOfMainLine.StationID);
                        } else {
                            endIndex_CCC = currentStationOfLine_CCC.Stations.size() - 1;
                        }
                        for(int i = begIndex_CCC; i <= endIndex_CCC; i++) {
                            railStationList_CCC.add(RailStation.find(railStationList, currentStationOfLine_CCC.Stations.get(i).StationID));
                            if(currentStationOfLine_CCC.Stations.get(i).StationID.equals(endOfMainLine.StationID)) {
                                CCC_finish = true;
                                break;
                            }
                        }

                        switch (currentStationOfLine_CCC.LineNo) {
                            case StationOfLine.W_TL_C:
                            case StationOfLine.W_TL_M:
                                currentStationOfLine_CCC = new StationOfLine(W_TL_S);
                                break;
                            case StationOfLine.W_TL_S:
                                currentStationOfLine_CCC = new StationOfLine(W_PL);
                                break;
                            case StationOfLine.W_PL:
                                currentStationOfLine_CCC = new StationOfLine(S_SL);
                                break;
                            case StationOfLine.S_SL:
                                currentStationOfLine_CCC = new StationOfLine(E_EL);
                                break;
                            case StationOfLine.E_EL:
                                currentStationOfLine_CCC = new StationOfLine(W_TL_N);
                                break;
                            case StationOfLine.W_TL_N:
                                currentStationOfLine_CCC = new StationOfLine(W_TL_C);
                                break;
                        }
                    }
                }

                out = new ArrayList<>();
                List<RailStation> temp_WC = new ArrayList<>(railStationList_front);
                List<RailStation> temp_WCC = new ArrayList<>(railStationList_front);
                List<RailStation> temp_CC = new ArrayList<>(railStationList_front);
                List<RailStation> temp_CCC = new ArrayList<>(railStationList_front);

                temp_WC.addAll(railStationList_WC);
                temp_WCC.addAll(railStationList_WCC);
                temp_CC.addAll(railStationList_CC);
                temp_CCC.addAll(railStationList_CCC);

                temp_WC.addAll(railStationList_tail);
                temp_WCC.addAll(railStationList_tail);
                temp_CC.addAll(railStationList_tail);
                temp_CCC.addAll(railStationList_tail);

                out.add(temp_WC);
                out.add(temp_WCC);
                out.add(temp_CC);
                out.add(temp_CCC);
            }

        }

        for(List<RailStation> railStationList_temp:out) {
            for(int i = 0; i < railStationList_temp.size()-1; i++) {
                if(railStationList_temp.get(i).StationID.equals(railStationList_temp.get(i+1).StationID)) {
                    railStationList_temp.remove(i);
                    i--;
                }
            }
        }

        return out;
    }
}

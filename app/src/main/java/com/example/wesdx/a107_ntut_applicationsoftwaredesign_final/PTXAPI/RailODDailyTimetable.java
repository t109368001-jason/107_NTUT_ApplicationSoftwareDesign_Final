package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 臺鐵定期時刻表資料
 *
 * 20181121 1241 完成
 *Implementation Notes
 * 取得指定[日期],[起迄站間]之站間時刻表資料
 */
public class RailODDailyTimetable {
    public String TrainDate;
    public DailyTrainInfo DailyTrainInfo;
    public RailStopTime OriginStopTime;
    public RailStopTime DestinationStopTime;
    public String UpdateTime;
    public String VersionID;

    public static List<RailODDailyTimetable> filter(List<RailODDailyTimetable> railODDailyTimetableList, String startTime /*HH:mm*/, String lessThanEndTimeOfHours) {
        List<RailODDailyTimetable> railODDailyTimetableList_new = new ArrayList<>();
        Date start = new Date(0);
        Date endAdd = new Date(0);
        Date end = new Date(0);

        try {
            start = (new SimpleDateFormat("HH:mm")).parse(startTime);
            endAdd = (new SimpleDateFormat("HH:mm")).parse(lessThanEndTimeOfHours);
            if(endAdd.compareTo((new SimpleDateFormat("HH:mm")).parse("00:00")) == 0) {
                endAdd = (new SimpleDateFormat("HH:mm")).parse("24:00");
            }

            for(int i = 0; i < railODDailyTimetableList.size(); i++) {
                Date temp = new Date(0);
                temp = (new SimpleDateFormat("HH:mm")).parse(railODDailyTimetableList.get(i).OriginStopTime.DepartureTime);

                if(temp.after(start)) {
                    railODDailyTimetableList_new.add(railODDailyTimetableList.get(i));
                }
            }

            end = (new SimpleDateFormat("HH:mm")).parse(railODDailyTimetableList_new.get(0).DestinationStopTime.ArrivalTime);

            for(int i = 0; i < railODDailyTimetableList_new.size(); i++) {
                Date temp = new Date(0);
                temp = (new SimpleDateFormat("HH:mm")).parse(railODDailyTimetableList_new.get(i).DestinationStopTime.ArrivalTime);

                if(temp.before(end)&&temp.after(start)) {
                    end = temp;
                }
            }

            end = new Date(end.getTime() + endAdd.getTime());

            for(int i = 0; i < railODDailyTimetableList_new.size(); i++) {
                Date temp = new Date(0);
                temp = (new SimpleDateFormat("HH:mm")).parse(railODDailyTimetableList_new.get(i).DestinationStopTime.ArrivalTime);

                if(temp.after(end)) {
                    railODDailyTimetableList_new.remove(i);
                    i--;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return railODDailyTimetableList_new;
    }
}





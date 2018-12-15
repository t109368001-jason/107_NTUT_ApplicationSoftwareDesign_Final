package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 臺鐵定期時刻表資料
 *
 * 20181120 1342 完成
 *
 */
public class RailGeneralTimetable {
    public String UpdateTime;
    public String EffectiveDate;//高鐵
    public String VersionID;
    public GeneralTimetable GeneralTimetable;
    public static Date getNewestUpdateTime(List<RailGeneralTimetable> railGeneralTimetableList) throws ParseException {
        Date newest = null;
        for(RailGeneralTimetable railGeneralTimetable:railGeneralTimetableList) {
            Date temp = API.dateFormat.parse(railGeneralTimetable.UpdateTime);
            if(newest == null) newest = temp;
            if (temp.after(newest)) newest = temp;
        }
        return newest;
    }

}







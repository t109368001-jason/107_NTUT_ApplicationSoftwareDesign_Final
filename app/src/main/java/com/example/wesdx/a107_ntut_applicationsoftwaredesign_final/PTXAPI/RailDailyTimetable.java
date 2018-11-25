package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;
/**
 *20181125  1649  完成
 * GET /v2/Rail/THSR/DailyTimetable/Today
 *
 */
import java.util.List;

public class RailDailyTimetable
{
  public String TrainDate;
  public RailDailyTrainInfo DailyTrainInfo;
  public List<StopTime> StopTimes;
  public String  UpdateTime;
  public String  VersionID;
}

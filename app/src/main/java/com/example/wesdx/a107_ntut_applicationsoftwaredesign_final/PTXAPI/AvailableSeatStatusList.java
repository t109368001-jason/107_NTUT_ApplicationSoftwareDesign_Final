package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.util.List;

/**
 *20181125  1715  完成
 *GET /v2/Rail/THSR/AvailableSeatStatusList/{StationID}
 *
 */
public class AvailableSeatStatusList {
   public String UpdateTime;
   public List<AvailableSeat> AvailableSeats;
}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

/**
 * 台鐵車站基本資料
 * 
 * @author Lin
 *
 */
public class RailStation {

    public String StationID;
	public StationNameC StationName;
	public StationPositionC StationPosition;
    public String StationAddress;
    public String StationPhone;
    public String StationClass;

}

class StationNameC
{
	String Zh_tw;
	String En;
}
class StationPositionC
{
	float PositionLon;
	float PositionLat;
}
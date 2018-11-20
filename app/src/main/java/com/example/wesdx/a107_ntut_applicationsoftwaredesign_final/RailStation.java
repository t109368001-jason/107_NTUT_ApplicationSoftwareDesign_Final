package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

/**
 * 台鐵車站基本資料
 * 
 * @author Lin
 *
 */
public class RailStation {

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
    public String StationID;
	public StationNameC StationName;
	public StationPositionC StationPosition;
    public String StationAddress;
    public String StationPhone;
    public String StationClass;

	public String getStationID() {
		return StationID;
	}
	public void setStationID(String stationID) {
		StationID = stationID;
	}
	public String getStationNameZh() {
		return StationName.Zh_tw;
	}
	public void setStationNameZh(String stationNameZh) {
		StationName.Zh_tw = stationNameZh;
	}
	public String getStationNameEn() {
		return StationName.En;
	}
	public void setStationNameEn(String stationNameEn) {
		StationName.En = stationNameEn;
	}
	public float getStationLon() {
		return StationPosition.PositionLon;
	}
	public void setStationLon(float stationLon) {
		StationPosition.PositionLon = stationLon;
	}
	public float getStationLat() {
		return StationPosition.PositionLat;
	}
	public void setStationLat(float stationLat) {
		StationPosition.PositionLat = stationLat;
	}
	public String getStationAddress() {
		return StationAddress;
	}
	public void setStationAddress(String stationAddress) {
		StationAddress = stationAddress;
	}
	public String getStationPhone() {
		return StationPhone;
	}
	public void setStationPhone(String stationPhone) {
		StationPhone = stationPhone;
	}
	public String getStationClass() {
		return StationClass;
	}
	public void setStationClass(String stationClass) {
		StationClass = stationClass;
	}
}

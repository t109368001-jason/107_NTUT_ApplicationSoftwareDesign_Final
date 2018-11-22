package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

/**
 *
 * GET /v2/Rail/TRA/Network
 * j20181123
 */
public class Network
{
    public String NetworkID;
    public String NetworkNameZh;
    public String NetworkNameEn;
    public String OperatorID;
    public String OperatorNameZh;
    public String OperatorNameEn;
    public String NetworkMapUrl;
    public Line_SimpleC Lines;
    public String UpdateTime;

}
class Line_SimpleC
{
    public String LineNo;
    public String LineID;
}

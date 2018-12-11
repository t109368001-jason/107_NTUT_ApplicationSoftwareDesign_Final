package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

public class ServiceDay {
    public String Monday;
    public String Tuesday;
    public String Wednesday;
    public String Thursday;
    public String Friday;
    public String Saturday;
    public String Sunday;

    public boolean compare(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                if(Monday.equals("1")) {
                    return true;
                }
                break;
            case 2:
                if(Tuesday.equals("1")) {
                    return true;
                }
                break;
            case 3:
                if(Wednesday.equals("1")) {
                    return true;
                }
                break;
            case 4:
                if(Thursday.equals("1")) {
                return true;
                }
                break;
            case 5:
                if(Friday.equals("1")) {
                    return true;
                }
                break;
            case 6:
                if(Saturday.equals("1")) {
                    return true;
                }
                break;
            case 7:
                if(Sunday.equals("1")) {
                    return true;
                }
                break;
        }
        return false;
    }
}
package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegionalRailStation implements Comparable<RegionalRailStation> {
    public String regionName;
    public List<RailStation> railStationList;

    public RegionalRailStation(String regionName, RailStation railStation) {
        this.regionName = regionName;
        this.railStationList = new ArrayList<>();
        this.railStationList.add(railStation);
    }

    public static List<RegionalRailStation> convert(List<RailStation> railStationList) {
        List<RegionalRailStation> regionalRailStationList = new ArrayList<>();

        for(RailStation railStation:railStationList) {
            String regionName = null;

            if(railStation.OperatorID.equals(API.TRA)) {
                switch(railStation.StationName.Zh_tw) {
                    case "基隆": case "三坑": case "八堵": case "暖暖": case "七堵":
                    case "百福": case "福隆": case "貢寮": case "雙溪": case "牡丹":
                    case "三貂嶺": case "侯硐": case "瑞芳": case "四腳亭": case "五堵":
                    case "汐止": case "汐科": case "南港": case "松山": case "臺北":
                    case "萬華": case "板橋": case "浮洲": case "樹林": case "南樹林":
                    case "山佳": case "鶯歌": case "大華": case "十分": case "望古":
                    case "嶺腳": case "平溪": case "菁桐": case "海科館":
                        regionName = "臺北/基隆地區";
                        break;
                    case "桃園": case "內壢": case "中壢": case "埔心": case "楊梅":
                    case "富岡": case "新富":
                        regionName = "桃園地區";
                        break;
                    case "北湖": case "湖口": case "新豐": case "竹北": case "北新竹":
                    case "新竹": case "三姓橋": case "香山": case "千甲": case "新莊":
                    case "竹中": case "六家": case "上員": case "榮華": case "竹東":
                    case "橫山": case "九讚頭": case "合興": case "富貴(南河)": case "內灣":
                        regionName = "新竹地區";
                        break;
                    case "石城": case "大里": case "大溪": case "龜山": case "外澳":
                    case "頭城": case "頂埔": case "礁溪": case "四城": case "宜蘭":
                    case "二結": case "中里": case "羅東": case "冬山": case "新馬":
                    case "蘇澳新": case "蘇澳": case "永樂": case "東澳": case "南澳":
                    case "武塔": case "漢本":
                        regionName = "宜蘭地區";
                        break;
                    case "崎頂": case "竹南": case "造橋": case "豐富": case "苗栗":
                    case "南勢": case "談文": case "大山": case "後龍": case "龍港":
                    case "白沙屯": case "新埔": case "通霄": case "苑裡": case "銅鑼":
                    case "三義":
                        regionName = "苗栗地區";
                        break;
                    case "日南": case "大甲": case "臺中港": case "清水": case "沙鹿":
                    case "龍井": case "大肚": case "追分": case "泰安": case "后里":
                    case "豐原": case "栗林": case "潭子": case "頭家厝": case "松竹":
                    case "太原": case "精武": case "臺中": case "五權": case "大慶":
                    case "烏日": case "新烏日": case "成功":
                        regionName = "臺中地區";
                        break;
                    case "彰化": case "花壇": case "大村": case "員林": case "永靖":
                    case "社頭": case "田中": case "二水":
                        regionName = "彰化地區";
                        break;
                    case "源泉": case "濁水": case "龍泉": case "集集": case "水里":
                    case "車埕":
                        regionName = "南投地區";
                        break;
                    case "林內": case "石榴": case "斗六": case "斗南": case "石龜":
                        regionName = "雲林地區";
                        break;
                    case "和平": case "和仁": case "崇德": case "新城": case "景美":
                    case "北埔": case "花蓮": case "吉安": case "志學": case "平和":
                    case "壽豐": case "豐田": case "林榮新光": case "南平": case "鳳林":
                    case "萬榮": case "光復": case "大富": case "富源": case "瑞穗":
                    case "三民": case "玉里": case "東里": case "東竹": case "富里":
                        regionName = "花蓮地區";
                        break;
                    case "大林": case "民雄": case "嘉北": case "嘉義": case "水上":
                    case "南靖":
                        regionName = "嘉義地區";
                        break;
                    case "後壁": case "新營": case "柳營": case "林鳳營": case "隆田":
                    case "拔林": case "善化": case "南科": case "新市": case "永康":
                    case "大橋": case "臺南": case "保安": case "仁德": case "中洲":
                    case "長榮大學": case "沙崙":
                        regionName = "臺南地區";
                        break;
                    case "大湖": case "路竹": case "岡山": case "橋頭": case "楠梓":
                    case "新左營": case "左營": case "內惟": case "美術館": case "鼓山":
                    case "三塊厝": case "高雄": case "民族": case "科工館": case "正義":
                    case "鳳山": case "後庄": case "九曲堂":
                        regionName = "高雄地區";
                        break;
                    case "六塊厝": case "屏東": case "歸來": case "麟洛": case "西勢":
                    case "竹田": case "潮州": case "崁頂": case "南州": case "鎮安":
                    case "林邊": case "佳冬": case "東海": case "枋寮": case "加祿":
                    case "內獅": case "枋山":
                        regionName = "屏東地區";
                        break;
                    case "池上": case "海端": case "關山": case "瑞和": case "瑞源":
                    case "鹿野": case "山里": case "臺東": case "康樂": case "知本":
                    case "太麻里": case "金崙": case "瀧溪": case "大武":
                        regionName = "臺東地區";
                        break;
                }
            } else if(railStation.OperatorID.equals(API.THSR)) {
                regionName = "高鐵";
            }

            if(regionName != null) RegionalRailStation.add(regionalRailStationList, regionName, railStation);

            regionName = null;
            if(railStation.OperatorID.equals(API.TRA)) {
                switch (railStation.StationName.Zh_tw) {
                    case "瑞芳": case "侯硐": case "三貂嶺": case "菁桐": case "平溪":
                    case "嶺腳": case "望古": case "十分": case "大華": case "海科館":
                    case "八斗子":
                        regionName = "平溪/深奧線";
                        break;
                    case "新竹": case "北新竹": case "千甲": case "新莊": case "竹中":
                    case "六家": case "上員": case "榮華": case "竹東": case "橫山":
                    case "九讚頭": case "合興": case "富貴": case "內灣":
                        regionName = "內灣/六家線";
                        break;
                    case "二水": case "源泉": case "濁水": case "龍泉": case "集集":
                    case "水里": case "車埕":
                        regionName = "集集線";
                        break;
                    case "中洲": case "長榮大學": case "沙崙":
                        regionName = "沙崙線";
                        break;
                }
            } else continue;

            if(regionName != null) RegionalRailStation.add(regionalRailStationList, regionName, railStation);
        }
        Collections.sort(regionalRailStationList);
        return regionalRailStationList;
    }

    public static void add(List<RegionalRailStation> regionalRailStationList, String regionName, RailStation railStation) {
        boolean found = false;
        for(RegionalRailStation regionalRailStation:regionalRailStationList) {
            if(regionalRailStation.regionName.equals(regionName)) {
                regionalRailStation.railStationList.add(railStation);
                found  = true;
                break;
            }
        }

        if(!found) {
            regionalRailStationList.add(new RegionalRailStation(regionName, railStation));
        }
    }

    @Override
    public int compareTo(RegionalRailStation f) {
        int a;
        int b;

        switch(regionName) {
            case "臺北/基隆地區": a = 1;  break;
            case "新竹地區": a = 2;  break;
            case "桃園地區": a = 3;  break;
            case "苗栗地區": a = 4;  break;
            case "臺中地區": a = 5;  break;
            case "彰化地區": a = 6;  break;
            case "南投地區": a = 7;  break;
            case "雲林地區": a = 8;  break;
            case "嘉義地區": a = 9;  break;
            case "臺南地區": a = 10; break;
            case "高雄地區": a = 11; break;
            case "屏東地區": a = 12; break;
            case "臺東地區": a = 13; break;
            case "花蓮地區": a = 14; break;
            case "宜蘭地區": a = 15; break;
            case "平溪/深奧線": a = 16; break;
            case "內灣/六家線": a = 17; break;
            case "集集線": a = 18; break;
            case "沙崙線": a = 19; break;
            default: a = 0;  break;
        }

        switch(f.regionName) {
            case "臺北/基隆地區": b = 1;  break;
            case "新竹地區": b = 2;  break;
            case "桃園地區": b = 3;  break;
            case "苗栗地區": b = 4;  break;
            case "臺中地區": b = 5;  break;
            case "彰化地區": b = 6;  break;
            case "南投地區": b = 7;  break;
            case "雲林地區": b = 8;  break;
            case "嘉義地區": b = 9;  break;
            case "臺南地區": b = 10; break;
            case "高雄地區": b = 11; break;
            case "屏東地區": b = 12; break;
            case "臺東地區": b = 13; break;
            case "花蓮地區": b = 14; break;
            case "宜蘭地區": b = 15; break;
            case "平溪/深奧線": b = 16; break;
            case "內灣/六家線": b = 17; break;
            case "集集線": b = 18; break;
            case "沙崙線": b = 19; break;
            default: b = 0;  break;
        }

        return Integer.compare(a, b);
    }
}

package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI;

import java.util.List;

/**
 *20181125  1709  完成
 * GET /v2/Rail/THSR/News
 *台鐵沒有
 */
public class News {
   public String  NewsID;
   public String  Language;
   public String  NewsCategory;
   public String  Title;
   public String  Description;
   public String  NewsUrl;
   public List<String> AttachmentUrlList;    //AttachmentUrlList (Array[string]): 消息附件網址連結 ,
   public String  StartTime;
   public String  EndTime;
   public String  PublishTime;
   public String  UpdateTime;
}

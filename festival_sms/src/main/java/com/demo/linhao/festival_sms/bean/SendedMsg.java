package com.demo.linhao.festival_sms.bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 已经发送的短信的实体类
 */
public class SendedMsg {
    private int id;
    private String content;
    private String numbers;//发送的联系人号码（可能有多个联系人的号码，拼接成一个String）
    private String names;//发送的联系人名单（可能有多个联系人，拼接成一个String）
    private String festivalName;
    private Date date;
    private String dateStr;//主要为了方便
    private DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static final String TABLE_NAME="tb_sended_msg";
    public static final String COLUMN_CONTENT="content";
    public static final String COLUMN_NUMBERS="numbers";
    public static final String COLUMN_NAMES="names";
    public static final String COLUMN_FESTIVAL_NAME="festival_name";
    public static final String COLUMN_DATE="date_str";

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getFestivalName() {
        return festivalName;
    }

    public void setFestivalName(String festivalName) {
        this.festivalName = festivalName;
    }

    public String getDataStr() {
        dateStr=df.format(date);
        return dateStr;
    }
}

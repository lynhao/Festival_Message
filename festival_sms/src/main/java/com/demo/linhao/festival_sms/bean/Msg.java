package com.demo.linhao.festival_sms.bean;

/**
 * 短信实体类
 */
public class Msg {
    private int id;
    private int festivalId;//附属于某个节日的id
    private String content;//短信内容

    public Msg(int id, int festivalId, String content) {
        this.id = id;
        this.festivalId = festivalId;
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFestivalId(int festivalId) {
        this.festivalId = festivalId;
    }

    public int getId() {
        return id;
    }

    public int getFestivalId() {
        return festivalId;
    }

    public String getContent() {
        return content;
    }
}

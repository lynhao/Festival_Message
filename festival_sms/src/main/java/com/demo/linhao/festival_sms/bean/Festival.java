package com.demo.linhao.festival_sms.bean;

import java.util.Date;

/**
 * 节日实体类
 *
 */
public class Festival {
	private int id;
	private String name;
	private String desc;//对节日的描述
	private Date date;

	public Festival(int id,String name) {
		this.id=id;
		this.name=name;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

package com.didispace.domain;

import java.io.Serializable;

public class Hredo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8988377942053696796L;
	private Long id;
 
	private Integer bid;
 
 
	private Integer etype;
	 
	private String edate;
	 
	 

	 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getBid() {
		return bid;
	}

	public void setBid(Integer bid) {
		this.bid = bid;
	}

	public Integer getEtype() {
		return etype;
	}

	public void setEtype(Integer etype) {
		this.etype = etype;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}

}

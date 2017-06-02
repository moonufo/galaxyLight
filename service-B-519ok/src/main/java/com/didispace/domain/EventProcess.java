package com.didispace.domain;

import java.io.Serializable;

public class EventProcess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8988377942053696796L;
	private Long id;
	private String bapp;
	private Integer bid;
	private Integer sid;
	private String service;
	private Integer etype;
	private String paremater;
 	private String edate;
	private Integer method;
	private String uri;
	private String eresponse;
	private String pathParemater;
	private Integer etypec;
	private Integer serviceNumber;
	 

	public Integer getEtypec() {
		return etypec;
	}

	public void setEtypec(Integer etypec) {
		this.etypec = etypec;
	}

	public Integer getServiceNumber() {
		return serviceNumber;
	}

	public void setServiceNumber(Integer serviceNumber) {
		this.serviceNumber = serviceNumber;
	}

	public Integer getMethod() {
		return method;
	}

	public void setMethod(Integer method) {
		this.method = method;
	}

	

	 

	public String getPathParemater() {
		return pathParemater;
	}

	public void setPathParemater(String pathParemater) {
		this.pathParemater = pathParemater;
	}

	public String getEresponse() {
		return eresponse;
	}

	public void setEresponse(String eresponse) {
		this.eresponse = eresponse;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public EventProcess() {
	}

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBapp() {
		return bapp;
	}

	public void setBapp(String bapp) {
		this.bapp = bapp;
	}

	public Integer getBid() {
		return bid;
	}

	public void setBid(Integer bid) {
		this.bid = bid;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Integer getEtype() {
		return etype;
	}

	public void setEtype(Integer etype) {
		this.etype = etype;
	}

	public String getParemater() {
		return paremater;
	}

	public void setParemater(String paremater) {
		this.paremater = paremater;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}

}

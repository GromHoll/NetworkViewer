package com.gromholl.hibernate.entity;

import java.io.Serializable;

public class Link implements Serializable {	

	private static final long serialVersionUID = 9217314044566505393L;
	
	private Long id;
	private NetworkHardware fromId;
	private NetworkHardware toId;
	private String type;
	
	public Link() {
		id = new Long(0);
		type = "";		
	}

/*
 *   Getters & Setters
 */
	
	public Long getId() {
		return id;
	}
	public NetworkHardware getFromId() {
		return fromId;
	}
	public NetworkHardware getToId() {
		return toId;
	}
	public String getType() {
		return type;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public void setFromId(NetworkHardware fromId) {
		this.fromId = fromId;
	}
	public void setToId(NetworkHardware toId) {
		this.toId = toId;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}

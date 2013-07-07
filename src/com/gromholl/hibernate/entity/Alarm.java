package com.gromholl.hibernate.entity;

import java.io.Serializable;

public class Alarm implements Serializable {

	private static final long serialVersionUID = -764063531359680739L;
	
	private Long id;
	private String type;
	private NetworkHardware target;
	private String description;
		
	public Alarm() {
		description = "";
		id = new Long(0);
	}
	
/*
 *   Getters & Setters
 */

	public Long getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public NetworkHardware getTarget() {
		return target;
	}
	public String getDescription() {
		return description;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setTarget(NetworkHardware target) {
		this.target = target;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}

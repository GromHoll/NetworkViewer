package com.gromholl.hibernate.entity;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

public class NetworkHardware implements Serializable {
	
	private static final long serialVersionUID = -7439587781750406757L;
	
	public static final String IP_STR = "IP Address";
	public static final String NAME_STR = "Device name";
	public static final String DESC_STR = "Description";
	
	private Long id;
	private String ip;
	private String name;
	private String description;
	
	public NetworkHardware() {
		id = new Long(0);
		ip = "";
		name = "";
		description = "";		
	}
	
/*
 *   Getters & Setters
 */
	
	public Long getId() {  
		return id;
	}
	public String getIp() { 
		return ip;
	}
	public String getName() { 
		return name;
	}
	public String getDescription() { 
		return description;
	}
	
	public void setId(Long id) {  
		this.id = id;
	}
	public void setIp(String ip) { 
		this.ip = ip;
	}
	public void setName(String name) { 
		this.name = name;
	}
	public void setDescription(String description) { 
		this.description = description;
	}
	
/*
 *  Other Methods
 */

//	public getImage() {}
	
	public SortedMap<String, Object> getProperties() {
		SortedMap<String, Object> res = new TreeMap<String, Object>();
		
		res.put(IP_STR, ip);
		res.put(NAME_STR, name);
		res.put(DESC_STR, description);
		
		return res;
	}
	
	public void setProperty(Object value, String str) {
		if(value instanceof String) {
			if(str.equals(IP_STR)) {
				setIp((String) value);
			} else if(str.equals(NAME_STR)) {
				setName((String) value);
			} else if(str.equals(DESC_STR)) {
				setDescription((String) value);
			}
		}
	}
	
}

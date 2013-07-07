package com.gromholl.hibernate.entity.router;

import java.util.SortedMap;

import com.gromholl.hibernate.entity.NetworkHardware;

public class Router extends NetworkHardware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2803221840524770625L;
	
	private Long portnum;
	private String model;	

	public static final String PORT_NUM_STR = "Port number";
	public static final String MODEL_STR = "Model";
	
	public Router() {
		portnum = new Long(1);
	}
	
	public Router(String model) {
		this();
		this.model = model;
	}
	
	public Long getPortnum() { 
		return portnum;
	}
	public String getModel() { 
		return model;
	}
	
	public void setPortnum(Long portNum) {  
		this.portnum = portNum;
	}
	public void setModel(String model) {  
		this.model = model;
	}

	@Override
	public SortedMap<String, Object> getProperties() {
		SortedMap<String, Object> res = super.getProperties();
		res.put(PORT_NUM_STR, portnum);
		res.put(MODEL_STR, model);
		
		return res;
	}

	@Override
	public void setProperty(Object value, String str) {
		if(str.equals(MODEL_STR)) {
			System.out.println((String) value + " MODEL");
			setModel((String) value);
		} else if(str.equals(PORT_NUM_STR)) {
			setPortnum(Long.parseLong((String) value));
		} else {
			super.setProperty(value, str);
		}
	}

}

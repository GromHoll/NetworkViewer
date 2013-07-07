package com.gromholl.hibernate.entity.terminal;

import java.util.SortedMap;

import com.gromholl.hibernate.entity.NetworkHardware;

public class Terminal extends NetworkHardware {

	private static final long serialVersionUID = 3928466330661501747L;
	
	private String os;	

	public static final String OS_STR = "Operating system";
	
	public Terminal() {
		super();
		os = "";
	}
	
	public Terminal(String str) {
		os = str;
	}
	
	public String getOs() { 
		return os;
	}	
	public void setOs(String os) {  
		this.os = os;
	}

	@Override
	public SortedMap<String, Object> getProperties() {
		SortedMap<String, Object> res = super.getProperties();
		res.put(OS_STR, os);
		
		return res;
	}

	@Override
	public void setProperty(Object value, String str) {
		if(str.equals(OS_STR)) {
			setOs((String) value);
			return;
		}
		super.setProperty(value, str);
	}
	
	

}

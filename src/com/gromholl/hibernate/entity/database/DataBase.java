package com.gromholl.hibernate.entity.database;

import java.util.SortedMap;

import com.gromholl.hibernate.entity.NetworkHardware;

public class DataBase extends NetworkHardware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1891351345017207925L;
	
	private String dbname;
	
	public static final String DBNAME_STR = "DB name";
	
	public DataBase() {
		
	}
	
	public DataBase(String str) {
		dbname = str;
	}
	
	public String getDbname() {
		return dbname;
	}
	
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
	
	@Override
	public SortedMap<String, Object> getProperties() {
		SortedMap<String, Object> res = super.getProperties();
		res.put(DBNAME_STR, dbname);
		
		return res;
	}

	@Override
	public void setProperty(Object value, String str) {
		if(str.equals(DBNAME_STR)) {
			setDbname((String) dbname);
		} else {
			super.setProperty(value, str);
		}
	}

}

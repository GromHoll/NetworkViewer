package com.gromholl.hibernate.client.admin;

public class ElementViewHelper {

	private Class elementClass;
	
	public ElementViewHelper(Class c) {
		elementClass = c;
	}
	
	public Class getNetworkHardwareClass() {
		return elementClass;
	}
	
	public String toString() {
		return elementClass.getSimpleName();
	}
}

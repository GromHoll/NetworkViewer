package com.gromholl.hibernate.client.admin;

import com.gromholl.hibernate.entity.NetworkHardware;

public class ElementListViewHelper {
	private NetworkHardware nh;
	
	public ElementListViewHelper(NetworkHardware arg0) {
		nh = arg0;
	}
	
	public String toString() {
		return new String(nh.getId() + " - " + nh.getName() + " (" + nh.getClass().getSimpleName() + ")");
	}
	
	public NetworkHardware getNetworkHardware() {
		return nh;
	}
	
}

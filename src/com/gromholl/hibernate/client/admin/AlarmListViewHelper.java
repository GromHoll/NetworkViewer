package com.gromholl.hibernate.client.admin;

import com.gromholl.hibernate.entity.Alarm;;

public class AlarmListViewHelper {
	private Alarm alarm;
	
	public AlarmListViewHelper(Alarm arg0) {
		alarm = arg0;
	}
	
	public String toString() {
		return new String("(" + alarm.getType() + " #" + alarm.getId() + ") " +
							    alarm.getTarget().getName() + " - " +  alarm.getTarget().getId());

	}
	
	public Alarm getAlarm() {
		return alarm;
	}
}

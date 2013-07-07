package com.gromholl.hibernate.client.admin;

import com.gromholl.hibernate.entity.Link;

public class LinkListViewHelper {
	private Link l;
	
	public LinkListViewHelper(Link arg0) {
		l = arg0;
	}
	
	public String toString() {
		return new String("From " + l.getFromId().getName() +
						  " - to " + l.getToId().getName());
	}
	
	public Link getLink() {
		return l;
	}
}

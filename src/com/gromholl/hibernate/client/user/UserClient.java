package com.gromholl.hibernate.client.user;

import javax.swing.SwingUtilities;

public class UserClient {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
	    	public void run() {  
	    		new UserFrame();
	    	}
	    });
		
	}
	
}

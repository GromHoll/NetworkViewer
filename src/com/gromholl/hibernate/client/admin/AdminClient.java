package com.gromholl.hibernate.client.admin;

import javax.swing.SwingUtilities;

public class AdminClient {
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
        	public void run() {  
        		new AdminFrame();
        	}
        });
		
	}
}

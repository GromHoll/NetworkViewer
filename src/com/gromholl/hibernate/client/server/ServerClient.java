package com.gromholl.hibernate.client.server;

import javax.swing.SwingUtilities;

public class ServerClient {
	
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
        	public void run() {  
        		new ServerFrame();
        	}
        });
		
	}
	
}

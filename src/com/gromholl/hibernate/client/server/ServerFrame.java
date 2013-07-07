package com.gromholl.hibernate.client.server;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import com.gromholl.hibernate.entity.Alarm;
import com.gromholl.hibernate.entity.Link;
import com.gromholl.hibernate.entity.NetworkHardware;

public class ServerFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private JTextArea logTextArea;
	private JTextField connectIpTextField;
	private JTextField connectPortTextField;
	private JButton stopServerButton;
	private JButton clearLogButton;
	
	private WelcomeThread welcomeThread;
	private Stack<UserThread> activeTalk;
	
	public static int DEFAULT_PORT = 5050;
	

	public static final String getElementsListCmd = "getElementsList";
	public static final String getLinkListCmd = "getLinksList";
	public static final String getAlarmListCmd = "getAlarmsList";
	public static final String connectionOkCmd = "connection ok";
	public static final String acceptCmd = "accept!";
	public static final String failCmd = "fail!";
	
	public static final String USER_CMD = "User";
	public static final String ADMIN_CMD = "Admin";
	
	public static final String addElementCmd = "addElement";
	public static final String updateElementCmd = "updateElement";
	public static final String dropElementCmd = "dropElement";
	
	public static final String addLinkCmd = "addLink";
	public static final String updateLinkCmd = "updateLink";
	public static final String dropLinkCmd = "dropLink";
	
	public static final String addAlarmCmd = "addAlarm";
	public static final String updateAlarmCmd = "updateAlarm";
	public static final String dropAlarmCmd = "dropAlarm";

	public ServerFrame() {

		super("Hibernate server application");
		
		logTextArea = new JTextArea();
		connectIpTextField = new JTextField();
		connectPortTextField = new JTextField();
		stopServerButton = new JButton("Stop server");
		clearLogButton = new JButton("Clear");
		
		activeTalk = new Stack<UserThread>();
		
		initComponents();
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {}			
			@Override
			public void windowIconified(WindowEvent arg0) {}			
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}			
			@Override
			public void windowClosing(WindowEvent arg0) {
				stopServer();
			}			
			@Override
			public void windowClosed(WindowEvent arg0) {}			
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		this.setSize(400, 400);
		this.setVisible(true);
		
		startServer();
	}

/*
 *  GUI initialization
 */
	
	private void initComponents() {		
		JScrollPane logScrollPane = new JScrollPane();
		JPanel tempPanel = new JPanel();
		JLabel tempLabel;
		
		GridBagConstraints c = new GridBagConstraints();
    	GridBagLayout gbl = new GridBagLayout();
    	tempPanel.setLayout(gbl);
    	c.anchor = GridBagConstraints.NORTHWEST;
    	c.fill   = GridBagConstraints.HORIZONTAL;
    	c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;

    	c.gridx = 0;
    	c.gridy = 0;
    	tempLabel = new JLabel("IP:");
    	gbl.setConstraints(tempLabel, c);
    	tempPanel.add(tempLabel);

    	c.gridy = 1;
    	tempLabel = new JLabel("Port:");    	
    	gbl.setConstraints(tempLabel, c);
    	tempPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 0;
    	gbl.setConstraints(connectIpTextField, c);
    	tempPanel.add(connectIpTextField);
    	
    	c.gridy = 1;
    	gbl.setConstraints(connectPortTextField, c);
    	tempPanel.add(connectPortTextField);
    	
    	c.gridx = 2;
    	c.gridy = 0;
    	c.gridheight = 2;
    	c.fill = GridBagConstraints.BOTH;
    	gbl.setConstraints(stopServerButton, c);
    	tempPanel.add(stopServerButton);
    	
    	logScrollPane.setViewportView(logTextArea);
    	
    	this.setLayout(new BorderLayout());
    	this.add(tempPanel, "North");
    	this.add(logScrollPane, "Center");
    	this.add(clearLogButton, "South");

		connectPortTextField.setText(Integer.toString(DEFAULT_PORT));
    	try {
			connectIpTextField.setText(InetAddress.getLocalHost().getHostAddress());			
		} catch(Exception exc) {
			toLog(exc.getMessage());
		}
    	
		clearLogButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearLog();
			}
		});
		stopServerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stopServer();
			}
		});
    	
    	logTextArea.setEditable(false);
    	connectPortTextField.setEditable(false);
    	connectIpTextField.setEditable(false);
	}

/*
 *  Logic
 */
	
	private void startServer() {
		welcomeThread = new WelcomeThread();		
		welcomeThread.start();
	}

	private void stopServer() {
		if(welcomeThread.isAlive()) {
			welcomeThread.shutdown();
		}		
		for(UserThread ct : activeTalk) {
			ct.finishTalking();
		}		
		dispose();
	}
	
	private void toLog(String message) {
		logTextArea.append(message + "\n");
		logTextArea.moveCaretPosition(logTextArea.getText().length());
	}
	private void clearLog() {
		logTextArea.setText("");
	}

/*
 *	Thread's logic
 */
	
	class WelcomeThread extends Thread {
		ServerSocket welcomeServer;
		Socket incomingSocket;
		BufferedReader fromClient;
		
		boolean shutdownRequested = false;
		
		@Override
		public void run() {
			try {
				welcomeServer = new ServerSocket(DEFAULT_PORT);
				
				toLog("Server started.");
				
				while(true) {
					waitIncomingRequest();
				}
				
			} catch (IOException e) {
				toLog("Server can't start.\nError: " + e.getMessage());
			} catch (ShutdownRequestException e) {
				toLog("Server stopped.");
			}			
		}
		
		private void waitIncomingRequest() throws ShutdownRequestException {
			try {
				incomingSocket = welcomeServer.accept();
				
				if(shutdownRequested)
					throw new ShutdownRequestException();
				
				fromClient =  new BufferedReader(
						new InputStreamReader(incomingSocket.getInputStream()));
				String cmd = fromClient.readLine();
				
				if(cmd.equals(USER_CMD)) {
					activeTalk.add(new UserThread(incomingSocket));
					activeTalk.lastElement().start();
				} else if(cmd.equals(ADMIN_CMD)) {
					activeTalk.add(new AdminThread(incomingSocket));
					activeTalk.lastElement().start();
				} else {
					toLog("Incorrect incoming request format. '" + cmd + "'");
				}				
			} catch (IOException e) {
				toLog("ERROR: " + e.getMessage());
			}
		}
		
		public void shutdown() {
			shutdownRequested = true;
			
			try {
				new Socket(welcomeServer.getInetAddress(), welcomeServer.getLocalPort()).close();
			} catch (IOException e) {
				
			}
		}
		
		class ShutdownRequestException extends Exception {
			private static final long serialVersionUID = 1L;
		}
		
	}
	
	class UserThread extends Thread {

		protected Socket socket;
		protected boolean shutdownRequested = false;
		
		protected BufferedReader inReader;
		protected DataOutputStream out;
		
		public UserThread(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			String cmd;
			
			try {
				inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new DataOutputStream(socket.getOutputStream());
				
				toLog("Incoming user connect from " + socket.getInetAddress().getHostAddress());
				out.writeBytes(connectionOkCmd + "\n");
								
				while(true) {					
					if(shutdownRequested)
						break;
					
					cmd = inReader.readLine();
				
					if(cmd.equals(getElementsListCmd)) {
						sendElementsList();
					} else if(cmd.equals(getLinkListCmd)) {
						sendLinkList();
					} else if(cmd.equals(getAlarmListCmd)) {
						sendAlarmList();
					} else {
						throw new Exception("User socket error.");
					}					
				}

			} catch(Exception e) {
				try {
					socket.close();
				} catch (IOException e1) {}
			}
		}
		
		protected void sendElementsList() throws Exception {
			try {
				ObjectOutputStream ois = new ObjectOutputStream(socket.getOutputStream());
								
				Session s = HibernateUtil.getSessionFactory().getCurrentSession();

				s.beginTransaction();
				Criteria crit = s.createCriteria(NetworkHardware.class);
				List elements = crit.list();
				s.getTransaction().commit();
				
				ois.writeObject(elements);
					
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Data transfer error.");
			}			
		}
		protected void sendLinkList() throws Exception {
			try {
				ObjectOutputStream ois = new ObjectOutputStream(socket.getOutputStream());
								
				Session s = HibernateUtil.getSessionFactory().getCurrentSession();

				s.beginTransaction();
				Criteria crit = s.createCriteria(Link.class);
				List links = crit.list();
								
				s.getTransaction().commit();

				ois.writeObject(links);
									
			} catch(Exception e) {
				throw new Exception("Data transfer error.");
			}			
		}		
		protected void sendAlarmList() throws Exception {
			try {
				ObjectOutputStream ois = new ObjectOutputStream(socket.getOutputStream());
								
				Session s = HibernateUtil.getSessionFactory().getCurrentSession();

				s.beginTransaction();
				Criteria crit = s.createCriteria(Alarm.class);
				List alarms = crit.list();
								
				s.getTransaction().commit();

				ois.writeObject(alarms);
									
			} catch(Exception e) {
				throw new Exception("Data transfer error.");
			}	
		}
		
		public void finishTalking() {
			shutdownRequested = true;
			
			try {
				socket.close();
			} catch (IOException e) {}
		}
		
	}
	
	class AdminThread extends UserThread {
		
		public AdminThread(Socket socket) {
			super(socket);
		}
		
		@Override
		public void run() {
			String cmd;
						
			try {
				inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new DataOutputStream(socket.getOutputStream());
				
				toLog("Incoming admin connect from " + socket.getInetAddress().getHostAddress());
				out.writeBytes(connectionOkCmd + "\n");
								
				while(true) {					
					if(shutdownRequested)
						break;
					
					cmd = inReader.readLine();
				
					if(cmd.equals(addElementCmd)) {
						addElement();
					} else if(cmd.equals(getElementsListCmd)) {
						sendElementsList();
					} else if(cmd.equals(updateElementCmd)) {
						updateElement();
					} else if(cmd.equals(dropElementCmd)) {
						dropElement();
					} else if(cmd.equals(addLinkCmd)) {
						addLink();
					} else if(cmd.equals(getLinkListCmd)) {
						sendLinkList();
					} else if(cmd.equals(updateLinkCmd)) {
						updateLink();
					} else if(cmd.equals(dropLinkCmd)) {
						dropLink();
					} else if(cmd.equals(addAlarmCmd)) {
						addAlarm();
					} else if(cmd.equals(getAlarmListCmd)) {
						sendAlarmList();
					} else if(cmd.equals(updateAlarmCmd)) {
						updateAlarm();
					} else if(cmd.equals(dropAlarmCmd)) {
						dropAlarm();
					} else {
						throw new Exception("Admin socket error.");
					}					
				}

			} catch(Exception e) {
				try {
					socket.close();
				} catch (IOException e1) {}
				toLog("ERROR: " + e.getMessage());
			}
				
		}
		
		private void addElement() throws Exception {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof NetworkHardware) {
					Session s = HibernateUtil.getSessionFactory().getCurrentSession();
					
					s.beginTransaction();
					s.save(obj);				
					s.getTransaction().commit();
					
					toLog("Element added.");
				}
			} catch(Exception e) {
				throw new Exception("Data transfer error.");
			}
		}
		private void updateElement() throws Exception {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof NetworkHardware) {
					Session s = HibernateUtil.getSessionFactory().getCurrentSession();
					
					s.beginTransaction();
					s.update(obj);
					s.getTransaction().commit();
					
					toLog("Element updated.");
				}
			} catch(Exception e) {
				throw new Exception("Data transfer error.");
			}	
		}
		private void dropElement() throws Exception {
			Session s = HibernateUtil.getSessionFactory().getCurrentSession();
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof NetworkHardware) {
					
					s.beginTransaction();
					s.delete(obj);
					s.getTransaction().commit();
					
					out.writeBytes(acceptCmd + "\n");
					toLog("Element deleted.");
				}
			} catch(ConstraintViolationException e) {
				out.writeBytes(failCmd + "\n");
				s.getTransaction().rollback();
			} catch(Exception e) {
				throw new Exception("Data transfer error." );
			}				
		}
		
		private void addLink() throws Exception {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof Link) {					
					Session s = HibernateUtil.getSessionFactory().getCurrentSession();

					s.beginTransaction();
					s.save(obj);
					s.getTransaction().commit();
					
					toLog("Link added.");
				}
				
			} catch(Exception e) {
				throw new Exception("Data transfer error. " + e.getMessage());
			}
		}
		private void updateLink() throws Exception {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof Link) {
					Session s = HibernateUtil.getSessionFactory().getCurrentSession();
					
					s.beginTransaction();
					s.update(obj);
					s.getTransaction().commit();
					
					toLog("Link updated.");
				}
			} catch(Exception e) {
				throw new Exception("Data transfer error.");
			}				
		}
		private void dropLink() throws Exception {
			Session s = HibernateUtil.getSessionFactory().getCurrentSession();
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof Link) {
					
					s.beginTransaction();
					s.delete(obj);
					s.getTransaction().commit();
					
					out.writeBytes(acceptCmd + "\n");
					toLog("Link deleted.");
				}
			} catch(ConstraintViolationException e) {
				out.writeBytes(failCmd + "\n");
				s.getTransaction().rollback();
			} catch(Exception e) {
				throw new Exception("Data transfer error." );
			}
		}
		
		private void addAlarm() throws Exception {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof Alarm) {					
					Session s = HibernateUtil.getSessionFactory().getCurrentSession();
					
					s.beginTransaction();
					s.save(obj);
					s.getTransaction().commit();
					
					toLog("Alarm added.");
				}
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Data transfer error. " + e.getMessage());
			}
		}
		private void updateAlarm() throws Exception {
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof Alarm) {
					Session s = HibernateUtil.getSessionFactory().getCurrentSession();
					
					s.beginTransaction();
					s.update(obj);
					s.getTransaction().commit();
					
					toLog("Alarm updated.");
				}
			} catch(Exception e) {
				throw new Exception("Data transfer error.");
			}
		}
		private void dropAlarm() throws Exception {
			Session s = HibernateUtil.getSessionFactory().getCurrentSession();
			try {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				
				if(obj instanceof Alarm) {
					
					s.beginTransaction();
					s.delete(obj);
					s.getTransaction().commit();
					
					out.writeBytes(acceptCmd + "\n");
					toLog("Alarm deleted.");
				}
			} catch(ConstraintViolationException e) {
				out.writeBytes(failCmd + "\n");
				s.getTransaction().rollback();
			} catch(Exception e) {
				throw new Exception("Data transfer error." );
			}
		}
			
	}
}
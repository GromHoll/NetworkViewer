package com.gromholl.hibernate.client.admin;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.SortedMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import com.gromholl.hibernate.client.server.ServerFrame;
import com.gromholl.hibernate.entity.Alarm;
import com.gromholl.hibernate.entity.Link;
import com.gromholl.hibernate.entity.NetworkHardware;
import com.gromholl.hibernate.entity.database.DataBaseAccess;
import com.gromholl.hibernate.entity.database.DataBaseMongoDB;
import com.gromholl.hibernate.entity.database.DataBaseMySQL;
import com.gromholl.hibernate.entity.database.DataBasePostgreSQL;
import com.gromholl.hibernate.entity.database.DataBaseSQLite;
import com.gromholl.hibernate.entity.router.RouterCisco;
import com.gromholl.hibernate.entity.router.RouterLinksys;
import com.gromholl.hibernate.entity.router.RouterNetgear;
import com.gromholl.hibernate.entity.terminal.TerminalLinux;
import com.gromholl.hibernate.entity.terminal.TerminalMacOS;
import com.gromholl.hibernate.entity.terminal.TerminalWindows;

public class AdminFrame extends JFrame{
		
	private static final long serialVersionUID = 1L;

	Socket socket;
		
	private JComboBox<ElementViewHelper> addElementsComboBox;
	private JButton addElementButton;
	private JTable addElementTable;
	private ElementAdminTableModel addElementTableModel;
	
	private JComboBox<ElementListViewHelper> addLinkFromComboBox;
	private JComboBox<ElementListViewHelper> addLinkToComboBox;
	private JComboBox<String> addLinkTypeComboBox;
	private JButton addLinkButton;
	
	private JComboBox<ElementListViewHelper> addAlarmTargetComboBox;
	private JComboBox<String> addAlarmTypeComboBox;
	private JTextArea addAlarmDescTextArea;
	private JButton addAlarmButton;
	
	private JComboBox<ElementListViewHelper> updateElementsComboBox;
	private JButton updateElementButton;
	private JTable updateElementTable;
	private ElementAdminTableModel updateElementTableModel;

	private JComboBox<LinkListViewHelper> updateLinkListComboBox;
	private JComboBox<ElementListViewHelper> updateLinkFromComboBox;
	private JComboBox<ElementListViewHelper> updateLinkToComboBox;
	private JComboBox<String> updateLinkTypeComboBox;
	private JButton updateLinkButton;

	private JComboBox<AlarmListViewHelper> updateAlarmListComboBox;
	private JComboBox<ElementListViewHelper> updateAlarmTargetComboBox;
	private JComboBox<String> updateAlarmTypeComboBox;
	private JTextArea updateAlarmDescTextArea;
	private JButton updateAlarmButton;
	
	private JComboBox<ElementListViewHelper> dropElementComboBox;
	private JButton dropElementButton;
	private JComboBox<LinkListViewHelper> dropLinkComboBox;
	private JButton dropLinkButton;
	private JComboBox<AlarmListViewHelper> dropAlarmComboBox;
	private JButton dropAlarmButton;	
	
	public AdminFrame() {
		
		super("Hibernate admin application");
		
		addElementsComboBox = new JComboBox<ElementViewHelper>();
		addElementButton = new JButton("Add element");
		addElementTableModel = new ElementAdminTableModel();
		addElementTable = new JTable(addElementTableModel);
		
		addLinkFromComboBox = new JComboBox<ElementListViewHelper>();
		addLinkToComboBox = new JComboBox<ElementListViewHelper>();
		addLinkTypeComboBox = new JComboBox<String>();
		addLinkButton = new JButton("Add link");
		
		addAlarmTargetComboBox = new JComboBox<ElementListViewHelper>();
		addAlarmTypeComboBox = new JComboBox<String>();
		addAlarmDescTextArea = new JTextArea("Description...");
		addAlarmButton = new JButton("Add alarm");
		
		updateElementsComboBox = new JComboBox<ElementListViewHelper>();
		updateElementButton = new JButton("Update element");
		updateElementTableModel = new ElementAdminTableModel();
		updateElementTable = new JTable(updateElementTableModel);
		
		updateLinkListComboBox = new JComboBox<LinkListViewHelper>();
		updateLinkFromComboBox = new JComboBox<ElementListViewHelper>();
		updateLinkToComboBox = new JComboBox<ElementListViewHelper>();
		updateLinkTypeComboBox = new JComboBox<String>();
		updateLinkButton = new JButton("Update link");
		
		updateAlarmListComboBox = new JComboBox<AlarmListViewHelper>();
		updateAlarmTargetComboBox = new JComboBox<ElementListViewHelper>();
		updateAlarmTypeComboBox = new JComboBox<String>();
		updateAlarmDescTextArea = new JTextArea();
		updateAlarmButton = new JButton("Update alarm");
		
		dropElementComboBox = new JComboBox<ElementListViewHelper>();
		dropElementButton = new JButton("Drop element");
		dropLinkComboBox = new JComboBox<LinkListViewHelper>();
		dropLinkButton =    new JButton("Drop link");
		dropAlarmComboBox = new JComboBox<AlarmListViewHelper>();
		dropAlarmButton =   new JButton("Drop alarm");
		
		initComponents();
		
		addElementsComboBox.addItem(new ElementViewHelper(DataBaseAccess.class));
		addElementsComboBox.addItem(new ElementViewHelper(DataBaseMongoDB.class));
		addElementsComboBox.addItem(new ElementViewHelper(DataBaseMySQL.class));
		addElementsComboBox.addItem(new ElementViewHelper(DataBasePostgreSQL.class));
		addElementsComboBox.addItem(new ElementViewHelper(DataBaseSQLite.class));
		
		addElementsComboBox.addItem(new ElementViewHelper(RouterCisco.class));
		addElementsComboBox.addItem(new ElementViewHelper(RouterLinksys.class));
		addElementsComboBox.addItem(new ElementViewHelper(RouterNetgear.class));		

		addElementsComboBox.addItem(new ElementViewHelper(TerminalLinux.class));
		addElementsComboBox.addItem(new ElementViewHelper(TerminalMacOS.class));
		addElementsComboBox.addItem(new ElementViewHelper(TerminalWindows.class));
		
		addElementsComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ElementViewHelper evh = (ElementViewHelper) addElementsComboBox.getSelectedItem();
				NetworkHardware nw;
				try {
					nw = (NetworkHardware) evh.getNetworkHardwareClass().newInstance();
					addElementTableModel.setProperties(nw.getProperties());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		addElementsComboBox.setSelectedIndex(0);
		addElementButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addElement();
				getElementsList();
			}
		});
		
		updateElementsComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				ElementListViewHelper element = (ElementListViewHelper) updateElementsComboBox.getSelectedItem();
				NetworkHardware nw;
				
				try {
					if(element == null) {
						if(updateElementsComboBox.getItemCount() != 0)
							updateElementsComboBox.setSelectedIndex(0);
						return;
					}
					nw = element.getNetworkHardware();
					updateElementTableModel.setProperties(nw.getProperties());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		updateElementButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateElement();
				getElementsList();
			}
		});
		
		dropElementButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dropElement();
				getElementsList();
			}
		});
	
		addLinkTypeComboBox.addItem("56 Kbit");
		addLinkTypeComboBox.addItem("256 Kbit");
		addLinkTypeComboBox.addItem("768 Kbit");
		addLinkTypeComboBox.addItem("2 Mbit");
		addLinkTypeComboBox.addItem("10 Mbit");
		addLinkTypeComboBox.addItem("100 Mbit");
		
		updateLinkTypeComboBox.addItem("56 Kbit");
		updateLinkTypeComboBox.addItem("256 Kbit");
		updateLinkTypeComboBox.addItem("768 Kbit");
		updateLinkTypeComboBox.addItem("2 Mbit");
		updateLinkTypeComboBox.addItem("10 Mbit");
		updateLinkTypeComboBox.addItem("100 Mbit");
		
		addLinkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addLink();
				getLinkList();
			}
		});
		
		updateLinkListComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LinkListViewHelper helper = (LinkListViewHelper) updateLinkListComboBox.getSelectedItem();
				if(helper == null) {
					if(updateLinkListComboBox.getItemCount() != 0) 
						updateLinkListComboBox.setSelectedIndex(0);
					return;
				}
				
				Link l = helper.getLink();
				Long from = l.getFromId().getId();
				Long to = l.getToId().getId();
				
				for(int i = 0; i < updateLinkFromComboBox.getItemCount(); i++) {
					Long fromCB = ((ElementListViewHelper) updateLinkFromComboBox.getItemAt(i)).getNetworkHardware().getId();					
					if(fromCB.equals(from)) {
						updateLinkFromComboBox.setSelectedIndex(i);
						break;
					}
				}
				for(int i = 0; i < updateLinkToComboBox.getItemCount(); i++) {
					Long toCB = ((ElementListViewHelper) updateLinkToComboBox.getItemAt(i)).getNetworkHardware().getId();					
					if(toCB.equals(to)) {
						updateLinkToComboBox.setSelectedIndex(i);
						break;
					}
				}
			}
		});
		updateLinkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateLink();
				getLinkList();
			}
		});
		
		dropLinkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dropLink();
				getLinkList();
			}
		});
		
		addAlarmTypeComboBox.addItem("INFO");
		addAlarmTypeComboBox.addItem("WARNING");
		addAlarmTypeComboBox.addItem("ERROR");
		addAlarmTypeComboBox.addItem("FATAL");
		
		updateAlarmTypeComboBox.addItem("INFO");
		updateAlarmTypeComboBox.addItem("WARNING");
		updateAlarmTypeComboBox.addItem("ERROR");
		updateAlarmTypeComboBox.addItem("FATAL");
		
		addAlarmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAlarm();
				getAlarmList();
			}
		});
		
		updateAlarmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAlarm();
				getAlarmList();
			}
		});
		updateAlarmListComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AlarmListViewHelper helper = (AlarmListViewHelper) updateAlarmListComboBox.getSelectedItem();
				if(helper == null) {
					if(updateAlarmListComboBox.getItemCount() != 0) 
						updateAlarmListComboBox.setSelectedIndex(0);
					return;
				}
				
				Alarm a = helper.getAlarm();
				Long target = a.getTarget().getId();
				String type = a.getType();
				
				for(int i = 0; i < updateAlarmTargetComboBox.getItemCount(); i++) {
					Long t = ((ElementListViewHelper) updateAlarmTargetComboBox.getItemAt(i)).getNetworkHardware().getId();					
					if(t.equals(target)) {
						updateAlarmTargetComboBox.setSelectedIndex(i);
						break;
					}
				}
				for(int i = 0; i < updateAlarmTargetComboBox.getItemCount(); i++) {
					String t = ((String) updateAlarmTypeComboBox.getItemAt(i));					
					if(t.equals(type)) {
						updateAlarmTypeComboBox.setSelectedIndex(i);
						break;
					}
				}
			}
		});
		
		dropAlarmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dropAlarm();
				getAlarmList();
			}
		});
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(600, 350);
		this.setVisible(true);
		
		connect();
	}
	
/*
 *  GUI initialization
 */
	private void initComponents() {
		
		JTabbedPane mainTabbedPane = new JTabbedPane();

		JTabbedPane addTabbedPane = new JTabbedPane();
		JTabbedPane updateTabbedPane = new JTabbedPane();
		JPanel dropPane = new JPanel();
		
		this.add(mainTabbedPane);
		
		mainTabbedPane.addTab("Add", addTabbedPane);
		mainTabbedPane.addTab("Update", updateTabbedPane);
		mainTabbedPane.addTab("Drop", dropPane);
		
		addTabbedPane.addTab("Element", getAddElementPanel());
		addTabbedPane.addTab("Link", getAddLinkPanel());
		addTabbedPane.addTab("Alarm", getAddAlarmPanel());
		
		updateTabbedPane.addTab("Element", getUpdateElementPanel());
		updateTabbedPane.addTab("Link", getUpdateLinkPanel());
		updateTabbedPane.addTab("Alarm", getUpdateAlarmPanel());
		
		dropPane.setLayout(new BoxLayout(dropPane, BoxLayout.Y_AXIS));		
		dropPane.add(getDropElementPanel());
		dropPane.add(getDropLinkPanel());
		dropPane.add(getDropAlarmPanel());
		
	}
	
	private JPanel getAddElementPanel() {
		
		JPanel resPanel = new JPanel();	
		JPanel topPanel = new JPanel();
		
    	resPanel.setLayout(new BorderLayout());
    	
    	GridBagLayout gbl = new GridBagLayout();
		topPanel.setLayout(gbl);
    	
		GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	
    	c.gridx = 0;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	JLabel tempLabel = new JLabel("Type:");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(addElementsComboBox, c);
    	topPanel.add(addElementsComboBox);
    	
    	JScrollPane tableScrollPane = new JScrollPane();
    	tableScrollPane.setViewportView(addElementTable);
    	
    	resPanel.add(topPanel, BorderLayout.NORTH); 
    	resPanel.add(tableScrollPane, BorderLayout.CENTER);
    	resPanel.add(addElementButton, BorderLayout.SOUTH);
		
		return resPanel;
	}	
	private JPanel getAddLinkPanel() {

		JPanel resPanel = new JPanel();
		JPanel topPanel = new JPanel();
		JLabel tempLabel;
		
    	resPanel.setLayout(new BorderLayout());
    	
    	GridBagLayout gbl = new GridBagLayout();
		topPanel.setLayout(gbl);
    	
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	
    	c.gridx = 0;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("From: ");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(addLinkFromComboBox, c);
    	topPanel.add(addLinkFromComboBox);
    	
    	c.gridx = 0;
    	c.gridy = 1;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("To: ");    	
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 1;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(addLinkToComboBox, c);
    	topPanel.add(addLinkToComboBox);
    	
    	c.gridx = 0;
    	c.gridy = 2;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("Link type: ");    	
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 2;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(addLinkTypeComboBox, c);
    	topPanel.add(addLinkTypeComboBox);
    	
    	resPanel.add(topPanel, BorderLayout.NORTH);
    	resPanel.add(addLinkButton, BorderLayout.SOUTH);
		
		return resPanel;
	}	
	private JPanel getAddAlarmPanel() {
		
		JPanel resPanel = new JPanel();
		JPanel topPanel = new JPanel();
		JLabel tempLabel;
		
    	resPanel.setLayout(new BorderLayout());
    	
    	GridBagLayout gbl = new GridBagLayout();
		topPanel.setLayout(gbl);
		  	
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	c.gridx = GridBagConstraints.RELATIVE;
    	c.gridy = 0;
    	
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("Target: ");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(addAlarmTargetComboBox, c);
    	topPanel.add(addAlarmTargetComboBox);
    	
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("Type: ");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(addAlarmTypeComboBox, c);
    	topPanel.add(addAlarmTypeComboBox);
    	
    	resPanel.add(topPanel, BorderLayout.NORTH);
    	
    	JScrollPane textAreaScrollPane = new JScrollPane();
    	textAreaScrollPane.setViewportView(addAlarmDescTextArea);
    	resPanel.add(textAreaScrollPane, BorderLayout.CENTER);
    	
    	resPanel.add(addAlarmButton, BorderLayout.SOUTH);
		
		return resPanel;
	}
	
	private JPanel getUpdateElementPanel() {

		JPanel resPanel = new JPanel();	
		JPanel topPanel = new JPanel();
		
    	resPanel.setLayout(new BorderLayout());
    	
    	GridBagLayout gbl = new GridBagLayout();
		topPanel.setLayout(gbl);
    	
		GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	
    	c.gridx = 0;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	JLabel tempLabel = new JLabel("List:");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(updateElementsComboBox, c);
    	topPanel.add(updateElementsComboBox);
    	
    	JScrollPane tableScrollPane = new JScrollPane();
    	tableScrollPane.setViewportView(updateElementTable);
    	
    	resPanel.add(topPanel, BorderLayout.NORTH); 
    	resPanel.add(tableScrollPane, BorderLayout.CENTER);
    	resPanel.add(updateElementButton, BorderLayout.SOUTH);
		
		return resPanel;
	}	
	private JPanel getUpdateLinkPanel() {

		JPanel resPanel = new JPanel();
		JPanel topPanel = new JPanel();
		JLabel tempLabel;
		
    	resPanel.setLayout(new BorderLayout());
    	
    	GridBagLayout gbl = new GridBagLayout();
		topPanel.setLayout(gbl);
    	
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	
    	c.gridx = 0;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("List: ");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(updateLinkListComboBox, c);
    	topPanel.add(updateLinkListComboBox);
    	
    	c.gridx = 0;
    	c.gridy = 1;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("From: ");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 1;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(updateLinkFromComboBox, c);
    	topPanel.add(updateLinkFromComboBox);
    	
    	c.gridx = 0;
    	c.gridy = 2;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("To: ");    	
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 2;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(updateLinkToComboBox, c);
    	topPanel.add(updateLinkToComboBox);
    	
    	c.gridx = 0;
    	c.gridy = 3;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("Type: ");    	
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 3;
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(updateLinkTypeComboBox, c);
    	topPanel.add(updateLinkTypeComboBox);
    	
    	resPanel.add(topPanel, BorderLayout.NORTH);
    	resPanel.add(updateLinkButton, BorderLayout.SOUTH);
		
		return resPanel;
	}	
	private JPanel getUpdateAlarmPanel() {
		
		JPanel resPanel = new JPanel();
		JPanel topPanel = new JPanel();
		JLabel tempLabel;
		
    	resPanel.setLayout(new BorderLayout());
    	
    	GridBagLayout gbl = new GridBagLayout();
		topPanel.setLayout(gbl);
		  	
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	c.gridx = GridBagConstraints.RELATIVE;
    	c.gridy = 1;
    	
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("Target: ");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(updateAlarmTargetComboBox, c);
    	topPanel.add(updateAlarmTargetComboBox);
    	
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("Type: ");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(updateAlarmTypeComboBox, c);
    	topPanel.add(updateAlarmTypeComboBox);
    	
    	c.gridx = 0;
    	c.gridy = 0;
    	c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	tempLabel = new JLabel("List: ");
    	gbl.setConstraints(tempLabel, c);
    	topPanel.add(tempLabel);
    	
    	c.gridx = 1;
    	c.gridy = 0;    
    	c.gridwidth = 3;    	
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(updateAlarmListComboBox, c);
    	topPanel.add(updateAlarmListComboBox);
    	
    	resPanel.add(topPanel, BorderLayout.NORTH);
    	
    	JScrollPane textAreaScrollPane = new JScrollPane();
    	textAreaScrollPane.setViewportView(updateAlarmDescTextArea);
    	resPanel.add(textAreaScrollPane, BorderLayout.CENTER);
    	
    	resPanel.add(updateAlarmButton, BorderLayout.SOUTH);
		
		return resPanel;
	}
	
	private JPanel getDropElementPanel() {
		
		JPanel resPanel = new JPanel();
		
		resPanel.setBorder(BorderFactory.createTitledBorder("Drop Element"));
		    	
    	GridBagLayout gbl = new GridBagLayout();
		resPanel.setLayout(gbl);
		  	
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	c.gridx = GridBagConstraints.RELATIVE;
    	c.gridy = 0;
    	
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	JLabel tempLabel = new JLabel("Elements:");
    	gbl.setConstraints(tempLabel, c);
    	resPanel.add(tempLabel);
    	
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(dropElementComboBox, c);
    	resPanel.add(dropElementComboBox);
    	
    	gbl.setConstraints(dropElementButton, c);
    	resPanel.add(dropElementButton);
		
		return resPanel;
	}	
	private JPanel getDropLinkPanel() {
		
		JPanel resPanel = new JPanel();
		
		resPanel.setBorder(BorderFactory.createTitledBorder("Drop Link"));
		    	
    	GridBagLayout gbl = new GridBagLayout();
		resPanel.setLayout(gbl);
		  	
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	c.gridx = GridBagConstraints.RELATIVE;
    	c.gridy = 0;
    	
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	JLabel tempLabel = new JLabel("Links:");
    	gbl.setConstraints(tempLabel, c);
    	resPanel.add(tempLabel);
    	
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(dropLinkComboBox, c);
    	resPanel.add(dropLinkComboBox);
    	
    	gbl.setConstraints(dropLinkButton, c);
    	resPanel.add(dropLinkButton);
		
		return resPanel;
	}	
	private JPanel getDropAlarmPanel() {
		
		JPanel resPanel = new JPanel();
		
		resPanel.setBorder(BorderFactory.createTitledBorder("Drop Alarm"));
		    	
    	GridBagLayout gbl = new GridBagLayout();
		resPanel.setLayout(gbl);
		  	
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
    	c.gridwidth = 1;
    	c.insets = new Insets(2, 2, 2, 2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.weightx = 1.0;
    	c.weighty = 0.0;
    	c.gridx = GridBagConstraints.RELATIVE;
    	c.gridy = 0;
    	
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
    	JLabel tempLabel = new JLabel("Alarms:");
    	gbl.setConstraints(tempLabel, c);
    	resPanel.add(tempLabel);
    	
    	c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
    	gbl.setConstraints(dropAlarmComboBox, c);
    	resPanel.add(dropAlarmComboBox);
    	
    	gbl.setConstraints(dropAlarmButton, c);
    	resPanel.add(dropAlarmButton);
		
		return resPanel;
	}
	
/*
 * Logic
 */
	
	private void connect() {
		DataOutputStream out;
		BufferedReader in;
		String cmd;
		
		try {
			socket = new Socket("localhost", 5050);
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
			out.writeBytes(ServerFrame.ADMIN_CMD + "\n");			
			cmd = in.readLine();
			
			if(!cmd.equals(ServerFrame.connectionOkCmd)) {				
				throw new Exception("Server is fail");
			}
			
			getElementsList();
			getLinkList();
			getAlarmList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addElement() {
		try {
			SortedMap<String, Object> map = addElementTableModel.getProperties();
			ElementViewHelper evh = (ElementViewHelper) addElementsComboBox.getSelectedItem();
			NetworkHardware nh = (NetworkHardware) evh.getNetworkHardwareClass().newInstance();
						
			for(String s : map.keySet()) {
				nh.setProperty(map.get(s), s);
			}
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
					
			dos.writeBytes(ServerFrame.addElementCmd + "\n");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(nh);
	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	private void getElementsList() {
		try {
			List<NetworkHardware> elements;
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());			
			dos.writeBytes(ServerFrame.getElementsListCmd + "\n");
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			elements = (List<NetworkHardware>) ois.readObject();
			

			addLinkFromComboBox.removeAllItems();
			addLinkToComboBox.removeAllItems();
			addAlarmTargetComboBox.removeAllItems();
			updateElementsComboBox.removeAllItems();
			dropElementComboBox.removeAllItems();
			updateLinkFromComboBox.removeAllItems();
			updateLinkToComboBox.removeAllItems();
			updateAlarmTargetComboBox.removeAllItems();
			
			if(!elements.isEmpty()) {				
				for(NetworkHardware nh : elements) {
					ElementListViewHelper helper = new ElementListViewHelper(nh);
					
					addLinkFromComboBox.addItem(helper);
					addLinkToComboBox.addItem(helper);
					addAlarmTargetComboBox.addItem(helper);
					updateElementsComboBox.addItem(helper);
					dropElementComboBox.addItem(helper);
					updateLinkFromComboBox.addItem(helper);
					updateLinkToComboBox.addItem(helper);
					updateAlarmTargetComboBox.addItem(helper);
				}
			}
		} catch(Exception e) {
			connect();
		}
	}
	private void updateElement() {
		try {
			SortedMap<String, Object> map = updateElementTableModel.getProperties();
			ElementListViewHelper helper = (ElementListViewHelper) updateElementsComboBox.getSelectedItem();
			if(helper == null)
				return;
			
			NetworkHardware nh = (NetworkHardware) helper.getNetworkHardware();
						
			for(String s : map.keySet()) {
				nh.setProperty(map.get(s), s);
			}
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
					
			dos.writeBytes(ServerFrame.updateElementCmd + "\n");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(nh);
	
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	private void dropElement() {
		BufferedReader in;		
		try {
			ElementListViewHelper helper = (ElementListViewHelper) dropElementComboBox.getSelectedItem();
			if(helper == null)
				return;
			
			NetworkHardware nh = (NetworkHardware) helper.getNetworkHardware();

			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
			dos.writeBytes(ServerFrame.dropElementCmd + "\n");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(nh);
			
			String cmd = in.readLine();
			if(cmd.equals(ServerFrame.acceptCmd)) {
				return;
			} else if(cmd.equals(ServerFrame.failCmd)) {
				JOptionPane.showConfirmDialog(this, "Can't drop this element.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			} else {
				throw new Exception();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addLink() {
		try {			
			ElementListViewHelper to = (ElementListViewHelper) addLinkToComboBox.getSelectedItem();
			ElementListViewHelper from = (ElementListViewHelper) addLinkFromComboBox.getSelectedItem();
			
			if(!to.equals(from)) {
				Link link = new Link();
				
				link.setFromId(from.getNetworkHardware());
				link.setToId(to.getNetworkHardware());
				link.setType( (String) addLinkTypeComboBox.getSelectedItem());
				
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						
				dos.writeBytes(ServerFrame.addLinkCmd + "\n");

				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(link);
				
			} else {
				JOptionPane.showConfirmDialog(this, "Can't connect to itself.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void getLinkList() {
		try {
			List<Link> links;
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());			
			dos.writeBytes(ServerFrame.getLinkListCmd + "\n");
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			links = (List<Link>) ois.readObject();

			updateLinkListComboBox.removeAllItems();
			dropLinkComboBox.removeAllItems();
			if(!links.isEmpty()) {				
				for(Link l : links) {
					LinkListViewHelper helper = new LinkListViewHelper(l);
					updateLinkListComboBox.addItem(helper);
					dropLinkComboBox.addItem(helper);
				}
			}
		} catch(Exception e) {
			connect();
		}
	}
	private void updateLink() {
		try {
			LinkListViewHelper helper = (LinkListViewHelper) updateLinkListComboBox.getSelectedItem();
			if(helper == null)
				return;
			
			ElementListViewHelper to = (ElementListViewHelper) updateLinkToComboBox.getSelectedItem();
			ElementListViewHelper from = (ElementListViewHelper) updateLinkFromComboBox.getSelectedItem();
			
			if(!to.equals(from)) {
				Link link = helper.getLink();
				
				link.setFromId(from.getNetworkHardware());
				link.setToId(to.getNetworkHardware());
				link.setType( (String) updateLinkTypeComboBox.getSelectedItem());
				
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						
				dos.writeBytes(ServerFrame.updateLinkCmd + "\n");
				
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(link);				
			} else {
				JOptionPane.showConfirmDialog(this, "Can't connect to itself.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}			
	}
	private void dropLink() {
		BufferedReader in;		
		try {
			LinkListViewHelper helper = (LinkListViewHelper) dropLinkComboBox.getSelectedItem();
			if(helper == null)
				return;
			
			Link l = helper.getLink();

			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
			dos.writeBytes(ServerFrame.dropLinkCmd + "\n");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(l);
			
			String cmd = in.readLine();
			if(cmd.equals(ServerFrame.acceptCmd)) {
				return;
			} else if(cmd.equals(ServerFrame.failCmd)) {
				JOptionPane.showConfirmDialog(this, "Can't drop this link.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			} else {
				throw new Exception();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}

	private void addAlarm() {
		try {
			Alarm alarm = new Alarm();

			alarm.setType((String) addAlarmTypeComboBox.getSelectedItem());
			alarm.setDescription(addAlarmDescTextArea.getText());
			alarm.setTarget(((ElementListViewHelper) addAlarmTargetComboBox.getSelectedItem()).getNetworkHardware());
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
					
			dos.writeBytes(ServerFrame.addAlarmCmd + "\n");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(alarm);				

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void getAlarmList() {
		try {
			List<Alarm> alarms;
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());			
			dos.writeBytes(ServerFrame.getAlarmListCmd + "\n");
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			alarms = (List<Alarm>) ois.readObject();
			
			updateAlarmListComboBox.removeAllItems();
			dropAlarmComboBox.removeAllItems();
			if(!alarms.isEmpty()) {
				for(Alarm a : alarms) {
					AlarmListViewHelper helper = new AlarmListViewHelper(a);
					updateAlarmListComboBox.addItem(helper);
					dropAlarmComboBox.addItem(helper);
				}
			}
		} catch(Exception e) {
			connect();
		}		
	}
	private void updateAlarm() {
		try {
			AlarmListViewHelper helper = (AlarmListViewHelper) updateAlarmListComboBox.getSelectedItem();
			if(helper == null)
				return;
			
			ElementListViewHelper target = (ElementListViewHelper) updateAlarmTargetComboBox.getSelectedItem();			
			Alarm alarm = helper.getAlarm();
			
			alarm.setTarget(target.getNetworkHardware());
			alarm.setDescription(updateAlarmDescTextArea.getText());
			alarm.setType( (String) updateAlarmTypeComboBox.getSelectedItem());
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
					
			dos.writeBytes(ServerFrame.updateAlarmCmd + "\n");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(alarm);				

		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	private void dropAlarm() {
		BufferedReader in;		
		try {
			AlarmListViewHelper helper = (AlarmListViewHelper) dropAlarmComboBox.getSelectedItem();
			if(helper == null)
				return;
			
			Alarm a = helper.getAlarm();

			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
			dos.writeBytes(ServerFrame.dropAlarmCmd + "\n");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(a);
			
			String cmd = in.readLine();
			if(cmd.equals(ServerFrame.acceptCmd)) {
				return;
			} else if(cmd.equals(ServerFrame.failCmd)) {
				JOptionPane.showConfirmDialog(this, "Can't drop this alarm.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			} else {
				throw new Exception();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
}

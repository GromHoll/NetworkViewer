package com.gromholl.hibernate.client.user;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.gromholl.hibernate.client.server.ServerFrame;
import com.gromholl.hibernate.entity.Alarm;
import com.gromholl.hibernate.entity.Link;
import com.gromholl.hibernate.entity.NetworkHardware;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

public class UserFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private List<Alarm> alarms;
	private List<NetworkHardware> hardwares;
	private NetworkHardware selectedHW;
	private HashMap<Long, Object> idToVertexMap;
	private HashMap<Object, NetworkHardware> vertexToHardwareMap;
	private List<Link> links;
	
	Socket socket;
	
	private mxGraphComponent graphPane;
	private mxGraph graph;
	mxGraphLayout graphLayout;
	
	private JTable propertyTable;
	private ElementUserTableModel propertyTableModel;	
	private JTable alarmTable;
	private AlarmTableModel alarmTableModel;
	private JButton refreshButton;
	private JComboBox<String> alarmTypeComboBox;
	private JRadioButton allRadioButton;
	private JRadioButton selectedRadioButton;	

	public UserFrame() {
		super("Hibernate user client");
		
		alarms = new ArrayList<Alarm>();
		hardwares = new ArrayList<NetworkHardware>();
		links = new ArrayList<Link>();
		
		graph = new mxGraph();
		graphLayout = new mxCircleLayout(graph);		
		graphPane = new mxGraphComponent(graph);
		
		propertyTableModel = new ElementUserTableModel();
		propertyTable = new JTable(propertyTableModel);
		alarmTableModel = new AlarmTableModel();
		alarmTable = new JTable(alarmTableModel);
		refreshButton = new JButton("Refresh");
		alarmTypeComboBox = new JComboBox<String>();
		allRadioButton = new JRadioButton("All alarms");
		selectedRadioButton = new JRadioButton("Only for selected");
		
		initComponents();
		
		allRadioButton.setSelected(true);
		
    	alarmTypeComboBox.addItem("ALL");
    	alarmTypeComboBox.addItem("INFO");
    	alarmTypeComboBox.addItem("WARNING");
    	alarmTypeComboBox.addItem("ERROR");
    	alarmTypeComboBox.addItem("FATAL");
    	
    	refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
    	alarmTypeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshAlarmList();
			}
		});
    	graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
			@Override
			public void invoke(Object arg0, mxEventObject arg1) {
				mxCell cell = (mxCell) graph.getSelectionCell();
				if(cell == null)
					return;
				if(cell.isVertex()) {
					selectedHW = vertexToHardwareMap.get(cell);
					if(selectedHW != null) 
						propertyTableModel.setProperties(selectedHW.getProperties());
					else
						propertyTableModel.setProperties(new TreeMap<String, Object>());
					refreshAlarmList();
				}
			}
		});
    	allRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshAlarmList();
			}
		});
    	selectedRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshAlarmList();
			}
		});   	
    	
		refresh();
		
		alarmTypeComboBox.setSelectedIndex(0);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(905, 705);
		this.setResizable(false);
		this.setVisible(true);
	}

/*
 *  GUI initialization
 */
	
	private void initComponents() {
		this.setLayout(null);
		
		final int graphWidth  = 400;
		final int graphHeight = 400;
		final int propsWidth  = graphWidth;
		final int propsHeight = 300;
		final int alarmWidth  = 500;
		final int alarmHeight = graphHeight + propsHeight;
		
    	JScrollPane propertyScrollPane = new JScrollPane();
    	propertyScrollPane.setViewportView(propertyTable);			
    	JPanel alarmPanel = getAlarmPanel();

    	this.add(graphPane);
    	this.add(propertyScrollPane);	    	
    	this.add(alarmPanel);
    	
    	graphPane.setBounds(1, 1, graphWidth, graphHeight);
    	propertyScrollPane.setBounds(1, graphHeight+1, propsWidth, propsHeight);
    	alarmPanel.setBounds(graphWidth+1, 1, alarmWidth, alarmHeight);
 
	}	
	
	private JPanel getAlarmPanel() {
		JPanel res = new JPanel();		
		res.setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(refreshButton, BorderLayout.NORTH);		
		topPanel.add(alarmTypeComboBox, BorderLayout.CENTER);
		
    	ButtonGroup buttons = new ButtonGroup();
    	buttons.add(allRadioButton);
    	buttons.add(selectedRadioButton);
    	JPanel radioPanel = new JPanel();
    	radioPanel.add(allRadioButton);
    	radioPanel.add(selectedRadioButton);

		topPanel.add(radioPanel, BorderLayout.SOUTH);
		res.add(topPanel, BorderLayout.NORTH);		
		
    	JScrollPane alarmScrollPane = new JScrollPane();
    	alarmScrollPane.setViewportView(alarmTable);
    	res.add(alarmScrollPane, BorderLayout.CENTER);    	
		
		return res;
	}

/*
 *  Logic
 */
	
	private void refresh() {
		DataOutputStream out;
		BufferedReader in;
		String cmd;
		
		try {
			socket = new Socket("localhost", 5050);
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
			out.writeBytes(ServerFrame.USER_CMD + "\n");			
			cmd = in.readLine();
			
			if(!cmd.equals(ServerFrame.connectionOkCmd)) {				
				throw new Exception("Server is fail");
			}
			
			getElementsList();
			getLinkList();
			getAlarmList();
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		refreshGraph();
		refreshAlarmList();
	}
	
	private void getElementsList() {
		try {			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());			
			dos.writeBytes(ServerFrame.getElementsListCmd + "\n");
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			hardwares = (List<NetworkHardware>) ois.readObject();
		} catch(Exception e) {
			
		}
	}
	private void getLinkList() {
		try {			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());			
			dos.writeBytes(ServerFrame.getLinkListCmd + "\n");
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			links = (List<Link>) ois.readObject();

		} catch(Exception e) {

		}
	}
	private void getAlarmList() {
		try {			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());			
			dos.writeBytes(ServerFrame.getAlarmListCmd + "\n");
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			alarms = (List<Alarm>) ois.readObject();			
		} catch(Exception e) {
			
		}		
	}	
		
	private void refreshGraph() {
		
		Object parent = graph.getDefaultParent();
		graph.removeCells(graph.getChildVertices(parent));
		graph.setCellsLocked(false);
		
		graph.getModel().beginUpdate();		
		try	{
			idToVertexMap = new HashMap<Long, Object>();
			vertexToHardwareMap = new HashMap<Object, NetworkHardware>();
			
			for(NetworkHardware nh : hardwares) {
				Object vertex = graph.insertVertex(parent, null, nh.getName(), 0, 0, 80, 30);
				idToVertexMap.put(nh.getId(), vertex);
				vertexToHardwareMap.put(vertex, nh);
			}
			for(Link l : links) {
				graph.insertEdge(parent, null, l.getType(), idToVertexMap.get(l.getFromId().getId()),
															idToVertexMap.get(l.getToId().getId()),
															"startArrow=none;endArrow=none;strokeColor=#000000");
			}
			for(Alarm a : alarms) {
				((mxCell) idToVertexMap.get(a.getTarget().getId())).setStyle("strokeColor=#FF0000");
			}
		} finally {
			graph.getModel().endUpdate();
		}
		
		graphLayout.execute(parent);
		graph.setCellsLocked(true);
	}
	private void refreshAlarmList() {
		List<Alarm> newList = new ArrayList<Alarm>(alarms);
		
		if(selectedRadioButton.isSelected()) {
			if(selectedHW != null) {
				for(Iterator<Alarm> alarmIter = newList.iterator(); alarmIter.hasNext();) {
					Alarm a = alarmIter.next();
					if(!a.getTarget().getId().equals(selectedHW.getId())) {
						alarmIter.remove();
					}
				}
			}
		}
		
		if(alarmTypeComboBox.getSelectedIndex() != 0) {
			String alarmType = (String) alarmTypeComboBox.getSelectedItem();
			
			for(Iterator<Alarm> alarmIter = newList.iterator(); alarmIter.hasNext();) {
				Alarm a = alarmIter.next();
				if(!a.getType().equals(alarmType)) {
					alarmIter.remove();
				}
			}
		}
		
		alarmTableModel.setAlarmList(newList);		
	}

}

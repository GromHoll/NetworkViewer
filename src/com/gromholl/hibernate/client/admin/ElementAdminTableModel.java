package com.gromholl.hibernate.client.admin;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

public class ElementAdminTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	public static final String[] COL_NAMES = { "Property", "Value" };
	
	private String[] propNames = new String[0];
	private SortedMap<String, Object> props = new TreeMap<String, Object>();
	
	public ElementAdminTableModel() {
		super();
		updatePropNames();
	}

	@Override
	public int getColumnCount() {
		return COL_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return COL_NAMES[column];
	}

	public Object getProperty(String name) {
		return props.get(name);
	}

	@Override
	public int getRowCount() {
		return props.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		if (columnIndex < 0 || columnIndex >= COL_NAMES.length || rowIndex < 0
				|| rowIndex >= propNames.length) {
			return null;
		}

		if (columnIndex == 0) {
			return propNames[rowIndex];
		}

		if (columnIndex == 1) {
			return props.get(propNames[rowIndex]);
		}

		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		if (columnIndex == 1) {
			return true;
		}

		return false;
	}

	public void setProperties(SortedMap<String, Object> map) {
		props = map;
		updatePropNames();
	}
	
	public SortedMap<String, Object> getProperties() {
		return props;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex < 0 || columnIndex >= COL_NAMES.length || rowIndex < 0
				|| rowIndex >= propNames.length) {
			return;
		}

		if (columnIndex == 0) {
			return;
		}

		if (columnIndex == 1) {
			props.put(propNames[rowIndex], aValue);
		}
	}

	private void updatePropNames() {
		if (propNames.length != props.size()) {
			propNames = new String[props.size()];
		}
		propNames = props.keySet().toArray(propNames);

		fireTableStructureChanged();
		fireTableDataChanged();
	}
}

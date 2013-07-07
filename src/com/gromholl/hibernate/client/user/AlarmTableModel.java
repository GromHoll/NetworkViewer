package com.gromholl.hibernate.client.user;

import com.gromholl.hibernate.entity.Alarm;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

public class AlarmTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	public static final String[] COL_NAMES = { "Type", "Target", "Description" };
	
	private List<Alarm> list = new ArrayList<Alarm>();
	
	public AlarmTableModel() {
		super();
		fireTableStructureChanged();
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return COL_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return COL_NAMES[column];
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		if (columnIndex < 0 || columnIndex >= COL_NAMES.length || rowIndex < 0
				|| rowIndex >= list.size()) {
			return null;
		}

		if (columnIndex == 0) {
			return list.get(rowIndex).getType();
		}

		if (columnIndex == 1) {
			return list.get(rowIndex).getTarget().getName();
		}
		
		if (columnIndex == 2) {
			return list.get(rowIndex).getDescription();
		}

		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void setAlarmList(List<Alarm> list) {
		this.list = list;
		fireTableStructureChanged();
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return;
	}

}

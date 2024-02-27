/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2024 His Majesty the King in Right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package repicea.simulation.processsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import repicea.gui.CommonGuiUtility;
import repicea.gui.REpiceaAWTProperty;
import repicea.gui.Refreshable;

/**
 * A class to display the processors and their features in a JTable instance.
 * @author Mathieu Fortin - February 2024
 */
@SuppressWarnings("serial")
public class ProcessorListTable extends JTable implements Refreshable, CellEditorListener {
	
	/**
	 * A class that handles the information on a member of a particular.<p>
	 * 
	 * It contains the value, the type and the field name.
	 */
	public static class MemberInformation {
	
		final String fieldName;
		final Class<?> type;
		final Object value;
		
		/**
		 * General constructor.
		 * @param fieldName the field name
		 * @param type the type of the field (String, double, ...)
		 * @param value the value of the member
		 */
		public MemberInformation(String fieldName, Class<?> type, Object value) {
			this.fieldName = fieldName;
			this.type = type;
			this.value = value;
		}
	}
	
	/**
	 * An interface to ensure the instance can provide information on some members and eventually change their values.<p>
	 * 
	 * The ProcessorListTable class relies on this interface to populate the table and record the changes made.
	 */
	public interface MemberHandler {
		
		/**
		 * Provide the information related to members of the class.
		 * @return a List of MemberInformation instances.
		 */
		public List<MemberInformation> getInformationsOnMembers();

		/**
		 * Apply the changes made in a member. <p>
		 * 
		 * The fieldName arguement is used to find the proper member.
		 * 
		 * @param fieldName the field name
		 * @param value the new value
		 */
		public void processChangeToMember(String fieldName, Object value);

	}
	
	protected final SystemManager caller;
	private ProcessorListTableModel tableModel;
	
	public ProcessorListTable(SystemManager caller) {
		this.caller = caller;
		initUI();
	}
	
	protected void initUI() {
		tableModel = new ProcessorListTableModel();
		synchronizeTable(tableModel);
	}

	private class ProcessorListTableModel extends DefaultTableModel {
		
		final Map<Integer, List<Integer>> editableCellMap;
		final Map<Integer, Class<?>> columnClassMap;
		
		ProcessorListTableModel() {
			super();
			editableCellMap = new HashMap<Integer, List<Integer>>();
			columnClassMap = new HashMap<Integer, Class<?>>();
		}
		
		@Override
		public void setValueAt(Object o, int row, int column) {
			super.setValueAt(o, row, column);
			if (!columnClassMap.containsKey(column) && o != null) {
				columnClassMap.put(column, o.getClass());
			}
			if (!editableCellMap.containsKey(row)) {
				editableCellMap.put(row, new ArrayList<Integer>());
			}
			if (!editableCellMap.get(row).contains(column)) {
				editableCellMap.get(row).add(column);
			}
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			boolean isEditable = false;
			if (editableCellMap.containsKey(row) && caller.getGUIPermission().isEnablingGranted()) {
				 isEditable = editableCellMap.get(row).contains(column); 
			}
			return isEditable;
		}
		
		@Override
		public Class<?> getColumnClass(int c) {
			return columnClassMap.get(c);
		}

	} 
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void synchronizeTable(ProcessorListTableModel tableModel) {
		List<Processor> processors = getProcessorList(); 
		for (int row = 0; row < processors.size(); row++) {
			Processor p = processors.get(row);
			for (MemberInformation v : p.getInformationsOnMembers()) {
				if (tableModel.findColumn(v.fieldName) == -1) {
					tableModel.addColumn(v.fieldName);
				}
			}			
		}
		for (int row = 0; row < processors.size(); row++) {
			Processor p = processors.get(row);
			Object[] rowObject = new Object[tableModel.getColumnCount()];
			tableModel.addRow(rowObject);
			for (MemberInformation v : p.getInformationsOnMembers()) {
				tableModel.setValueAt(v.value, row, tableModel.findColumn(v.fieldName));
			}
		}
		
		setModel(tableModel);

		for (int i = 0; i < getColumnCount(); i++) {
			Class clazz = getColumnClass(i);
			TableColumn cModel = getColumnModel().getColumn(i);
			if (clazz.isEnum()) {
				JComboBox comboBox = new JComboBox(clazz.getEnumConstants());
				cModel.setCellEditor(new DefaultCellEditor(comboBox));
			}
		}
		
	}

	
	/**
	 * This method should be override in derived class.
	 * @return a List of Processor instances
	 */
	protected List<Processor> getProcessorList() {
		return caller.getList();
	}

	@Override
	public void refreshInterface() {
		initUI();
	}

	@Override
	public void tableChanged(TableModelEvent evt) {
		super.tableChanged(evt);
		int columnIndex = evt.getColumn();
		int rowIndex = evt.getFirstRow();
		if (columnIndex >= 0 & rowIndex >= 0) {
			Processor p = getProcessorList().get(rowIndex);
			String columnName = ((DefaultTableModel) evt.getSource()).getColumnName(columnIndex);
			Object newValue = ((DefaultTableModel) evt.getSource()).getValueAt(rowIndex, columnIndex);
			p.processChangeToMember(columnName, newValue);
			// record a change
			SystemManagerDialog systemDlg = (SystemManagerDialog) CommonGuiUtility.getParentComponent(this, SystemManagerDialog.class);
			if (systemDlg != null) {
				systemDlg.firePropertyChange(REpiceaAWTProperty.ActionPerformed, this, null);
			}
		}
	}
	
}

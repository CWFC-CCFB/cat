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

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import repicea.gui.Refreshable;

/**
 * A class to display the processors and their features in a JTable instance.
 */
@SuppressWarnings("serial")
public class ProcessorListTable extends JTable implements Refreshable {
	
	public static class ProcessorListTableCellValue {
	
		final String fieldName;
		final Class<?> type;
		final Object value;
		
		public ProcessorListTableCellValue(String fieldName, Class<?> type, Object value) {
			this.fieldName = fieldName;
			this.type = type;
			this.value = value;
		}
	}
	
	final SystemManager caller;
	
	public ProcessorListTable(SystemManager caller) {
		this.caller = caller;
	}
	
	private void synchronizeTable(DefaultTableModel tableModel) {
		List<Processor> processors = caller.getList(); 
		for (int row = 0; row < processors.size(); row++) {
			Processor p = processors.get(row);
			for (ProcessorListTableCellValue v : p.getFieldsToDisplay()) {
				if (tableModel.findColumn(v.fieldName) == -1) {
					tableModel.addColumn(v.fieldName);
				}
				tableModel.setValueAt(v.value, row, tableModel.findColumn(v.fieldName));
			}
		}
	}

	@Override
	public void refreshInterface() {
		DefaultTableModel tableModel = new DefaultTableModel();
		synchronizeTable(tableModel);
		setModel(tableModel);
	}

}

package com;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class TableModel1 extends AbstractTableModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4742088303112093068L;
	Vector rowData;
	Vector colName;
	public TableModel1(){
		
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}

package com;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.util.*;
public class Demo extends JFrame implements ActionListener {
	// define component
	JLabel jlabel;
	JTextField jtf;
	
	JButton jbQuery;
	JButton jb1;
	JButton jbOk;

	JPanel jpNorth;
	JPanel jpSouth;
	
	JScrollPane jsp;
	JTable jt;
	Vector rowData,colName;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Demo demo = new Demo();

	}
	void query(){
		
	}
	void initTable(){
		colName=new Vector();
		colName.add("id");
		colName.add("title");
		Vector hang=new Vector();
		hang.add("1");
		hang.add("hello");
		rowData=new Vector();
		rowData.add(hang);
		jt=new JTable(rowData,colName);
		jsp=new JScrollPane(jt);
		this.add(jsp, BorderLayout.CENTER);
	}
	public Demo() {
		// create
		jlabel=new JLabel("query");
		jtf=new JTextField(10);
		jbQuery=new JButton("go");
		jbQuery.addActionListener(this);
		jbQuery.setActionCommand("go");
		
		jpNorth = new JPanel();
		jpSouth = new JPanel();

		jbOk = new JButton("È·¶¨");
		jb1 = new JButton("1");
		jb1.addActionListener(this);
		jb1.setActionCommand("jb1");
		
	
		// add
		// jp
		jpNorth.add(jlabel);
		jpNorth.add(jtf);
		jpNorth.add(jbQuery);
		
		jpSouth.add(jb1);
		jpSouth.add(jbOk);
		
		
		// this
		this.add(jpNorth, BorderLayout.NORTH);
		this.add(jpSouth, BorderLayout.SOUTH);
		initTable();
		// layout manager
		// this.setLayout(new FlowLayout(FlowLayout.LEFT));

		// set
		this.setTitle("Hello world!");
		this.setSize(500, 500);
		this.setResizable(false);
		this.setLocation(100, 100);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// System.out.println(e.getActionCommand());
		if(e.getActionCommand().equals("go")){
			onClickBtquery();
		}
		if (e.getActionCommand().equals("jb1"))
			onClickBt1();
	}

	void onClickBt1() {
		System.out.println("lala");
	}
	void onClickBtquery(){
		System.out.println("query");
	}

}

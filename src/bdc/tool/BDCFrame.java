package bdc.tool;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BDCFrame extends JFrame{
	public JTextField fileName;
	public JTextField backupName;
	private JButton selectFile;
	private JButton selectBackup;
	private JButton backup;
	private JButton revert;
	private ActionListener eventHandler;
	
	public BDCFrame(){
		super("BDC Backup Manager");
		eventHandler = new BDCEventHandler(this);
		add(initCenterPanel(), BorderLayout.CENTER);
		add(initActionPanel(), BorderLayout.SOUTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
	}
	
	private JPanel initCenterPanel(){
		JPanel center = new JPanel(new GridLayout(2,1));
		center.add(initFilePanel());
		center.add(initBackupPanel());
		return center;
	}
	
	private JPanel initActionPanel(){
		backup = new JButton("Backup");
		backup.setActionCommand("backup");
		backup.addActionListener(eventHandler);
		
		revert = new JButton("Revert");
		revert.setActionCommand("revert");
		revert.addActionListener(eventHandler);
		
		JPanel actionPanel = new JPanel(new FlowLayout());
		actionPanel.add(revert);
		actionPanel.add(backup);
		return actionPanel;
	}
	
	private JPanel initFilePanel(){
		fileName = new JTextField(25);
		selectFile = new JButton("Select");
		selectFile.setActionCommand("selectFile");
		selectFile.addActionListener(eventHandler);
		
		JPanel filePanel = new JPanel(new FlowLayout());
		filePanel.add(new JLabel("File:"));
		filePanel.add(fileName);
		filePanel.add(selectFile);
		
		return filePanel;
	}
	
	private JPanel initBackupPanel(){
		backupName = new JTextField(25);
		selectBackup = new JButton("Select");
		selectBackup.setActionCommand("selectBackup");
		selectBackup.addActionListener(eventHandler);
		
		JPanel backupPanel = new JPanel(new FlowLayout());
		backupPanel.add(new JLabel("Backup:"));
		backupPanel.add(backupName);
		backupPanel.add(selectBackup);
		
		return backupPanel;
	}
}

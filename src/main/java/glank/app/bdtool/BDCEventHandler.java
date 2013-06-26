package glank.app.bdtool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import glank.app.compress.BackupFile;

public class BDCEventHandler implements ActionListener{
	
	private BDCFrame parent;
	
	public BDCEventHandler(BDCFrame parent){
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="backup") backup();
		else if(e.getActionCommand()=="revert") revert();
		else if(e.getActionCommand()=="selectFile") selectFile();
		else if(e.getActionCommand()=="selectBackup") selectBackup();
		else
			JOptionPane.showMessageDialog(parent, 
			"Invalid Action Command: " + e.getActionCommand());
	}

	private void selectBackup() {
		JFileChooser jfc = new JFileChooser();
		FileFilter filter = new FileFilter(){
			public boolean accept(File path) {
				return path.getAbsolutePath().endsWith(".bdc");
			}

			public String getDescription() {
				return "BDC Backup Files";
			}
		};
		jfc.setFileFilter(filter);
		int result = jfc.showOpenDialog(parent);
		if(result==JFileChooser.CANCEL_OPTION)
			return;
		File selected = jfc.getSelectedFile();
		if(selected!=null){
			String backupName = selected.getAbsolutePath();
			if(!backupName.endsWith(".bdc"))
				backupName+=".bdc";
			parent.backupName.setText(backupName);
		}
	}

	private void selectFile() {
		JFileChooser jfc = new JFileChooser();
		int result = jfc.showOpenDialog(parent);
		if(result==JFileChooser.CANCEL_OPTION)
			return;
		File selected = jfc.getSelectedFile();
		if(selected!=null){
			parent.fileName.setText(selected.getAbsolutePath());
			String backupName = selected.getAbsolutePath();
			int i = backupName.lastIndexOf(".");
			if(i!=-1)
				backupName = backupName.substring(0, i)+".bdc";
			else
				backupName+=".bdc";
			parent.backupName.setText(backupName);
		}
	}

	private void revert() {
		try{
			File file = new File(parent.backupName.getText());
			if(!file.exists()){
				JOptionPane.showMessageDialog(parent, "No Backup Exists.");
				return;
			}
			int result = JOptionPane.showConfirmDialog(parent,"Are you sure you want to overwrite \""+parent.fileName.getText()+"\" with the backup version? This cannot be undone.", "Revert.", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_OPTION){
				BackupFile backup = new BackupFile(file);
				backup.revertTo(new File(parent.fileName.getText()));
			}
		}catch(Throwable t){
			JOptionPane.showMessageDialog(parent, "Error Reverting File.");
		}
	}

	private void backup() {
		try{
			BackupFile backup = new BackupFile(new File(parent.backupName.getText()));
			backup.backup(new File(parent.fileName.getText()));
			JOptionPane.showMessageDialog(parent, "File Successfully Backed Up");
		}catch(Throwable t){
			JOptionPane.showMessageDialog(parent, "Error Backing Up File.");
		}
	}
}

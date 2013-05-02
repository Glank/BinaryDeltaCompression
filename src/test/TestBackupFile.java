package test;

import java.io.File;

import kirstein.compress.BackupFile;
import kirstein.compress.ZipUtil;

public class TestBackupFile {
	public static void main(String[] args) throws Throwable{
		/*
		byte[] testBytes = "This is a test.".getBytes();
		testBytes = ZipUtil.compress(testBytes);
		testBytes = ZipUtil.decompress(testBytes);
		System.out.println(new String(testBytes));
		//*/
		
		File file = new File("testFiles/backup.dat");
		if(file.exists())
			file.delete();
		System.out.println("Creating Backup File");
		BackupFile backup = new BackupFile(file);
		System.out.println("Backing Up File 1");
		backup.backup(new File("testFiles/imgs/test1_init.jpg"));
		//*
		System.out.println("Backing Up File 2");
		backup.backup(new File("testFiles/imgs/test1_changed.jpg"));
		System.out.println("Backing Up File 3");
		backup.backup(new File("testFiles/imgs/test2_changed.jpg"));
		System.out.println("Reverting File 3");
		backup.revertTo(new File("testFiles/test2_changed.jpg"));
		System.out.println("Reverting File 2");
		backup.revertTo(new File("testFiles/test1_changed.jpg"));
		//*/
		System.out.println("Reverting File 1");
		backup.revertTo(new File("testFiles/test1_init.jpg"));
	}
}

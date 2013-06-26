package test;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;
import org.junit.Assert;

import glank.app.compress.BackupFile;
import glank.app.compress.ZipUtil;

public class TestBackupFile extends TestCase{
	
	public TestBackupFile(String name){
		super(name);
	}

	public void testBF() throws Exception{
		File file = getFile("backup.dat");
		if(file.exists())
			file.delete();
		BackupFile backup = new BackupFile(file);
		backup.backup(getFile("imgs/test1_init.jpg"));
		backup.backup(getFile("imgs/test1_changed.jpg"));
		backup.backup(getFile("imgs/test2_changed.jpg"));
		backup.revertTo(getFile("reversion.dat"));
		assertFileEquals("reversion.dat", "imgs/test2_changed.jpg");
		backup.revertTo(getFile("reversion.dat"));
		assertFileEquals("reversion.dat", "imgs/test1_changed.jpg");
		backup.revertTo(getFile("reversion.dat"));
		assertFileEquals("reversion.dat", "imgs/test1_init.jpg");
	}

	public File getFile(String name) throws Exception{
		return new File(getClass().getClassLoader().getResource(name).toURI());
	}

	public void assertFileEquals(String f1name, String f2name) throws Exception{
		File f1 = getFile(f1name);
		File f2 = getFile(f2name);
		FileInputStream in1 = new FileInputStream(f1);
		FileInputStream in2 = new FileInputStream(f2);
		byte[] buff1 = new byte[1024];
		byte[] buff2 = new byte[1024];
		int read1 = 0, read2 = 0;

		while(read1 != -1){
			read1 = in1.read(buff1);
			read2 = in2.read(buff2);
			Assert.assertEquals(read1, read2);
			Assert.assertArrayEquals(buff1, buff2);
		}
	}
}

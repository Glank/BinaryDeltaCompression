package glank.app.compress;

import java.io.File;

import junit.framework.TestCase;
import org.junit.Assert;

import glank.app.test.TestTools;

//import glank.app.compress.BackupFile;
//import glank.app.compress.ZipUtil;

public class TestBackupFile extends TestCase{
	
	public TestBackupFile(String name){
		super(name);
	}

	public void testBF() throws Exception{
		TestTools tools = TestTools.get();
		File file = tools.getFile("backup.dat");
		if(file.exists())
			file.delete();
		BackupFile backup = new BackupFile(file);
		backup.backup(tools.getFile("imgs/test1_init.jpg"));
		backup.backup(tools.getFile("imgs/test1_changed.jpg"));
		backup.backup(tools.getFile("imgs/test2_changed.jpg"));
		backup.revertTo(tools.getFile("reversion.dat"));
		tools.assertFileEquals("reversion.dat", "imgs/test2_changed.jpg");
		backup.revertTo(tools.getFile("reversion.dat"));
		tools.assertFileEquals("reversion.dat", "imgs/test1_changed.jpg");
		backup.revertTo(tools.getFile("reversion.dat"));
		tools.assertFileEquals("reversion.dat", "imgs/test1_init.jpg");
	}

}

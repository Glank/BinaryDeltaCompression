package glank.app.test;

import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

public class TestTools{
	
	private static TestTools instance = new TestTools();

	private TestTools(){}

	public static TestTools get(){
		return instance;
	}

	public File getFile(String name) throws Exception{
		return new File(getClass().getClassLoader().getResource(name).toURI());
	}

	public File getDirectory(String name) throws Exception{
		File home = getFile(".");
		String path = home.getAbsolutePath()+File.separator+name;
		return new File(path);	
	}

	public void assertFileEquals(String f1name, String f2name) throws Exception{
		File f1 = getFile(f1name);
		File f2 = getFile(f2name);
		assertFileEquals(f1,f2);
	}

	public void assertFileEquals(File f1, File f2) throws Exception{
		FileInputStream in1 = new FileInputStream(f1);
		FileInputStream in2 = new FileInputStream(f2);
		byte[] buff1 = new byte[1024];
		byte[] buff2 = new byte[1024];
		int read1 = 0, read2 = 0;

		while(read1 != -1){
			read1 = in1.read(buff1);
			read2 = in2.read(buff2);
			Assert.assertEquals(
				f1.getName()+"!="+f2.getName()
				,read1, read2);
			Assert.assertArrayEquals(
				f1.getName()+"!="+f2.getName(),
				buff1, buff2);
		}
	}

	public void assertDirEquals(String dir1name, String dir2name) throws Exception{
		File dir1 = getFile(dir1name);
		File dir2 = getFile(dir2name);
		assertDirEquals(dir1,dir2);	
	}
	
	public void assertDirEquals(File dir1, File dir2) throws Exception{
		File[] files1 = dir1.listFiles();
		File[] files2 = dir2.listFiles();
		HashMap files2ByName = new HashMap();
		for(int i = 0; i < files2.length; i++){
			File file = files2[i];
			files2ByName.put(file.getName(), file);
		}
		
		for(int i = 0; i < files1.length; i++){
			File file1 = files1[i];
			File file2 = (File)files2ByName.remove(file1.getName());
			Assert.assertTrue(file2 != null);
			if(file1.isDirectory())
				assertDirEquals(file1, file2);
			else
				assertFileEquals(file1, file2);
		}
		Assert.assertTrue(files2ByName.isEmpty());
	}
}

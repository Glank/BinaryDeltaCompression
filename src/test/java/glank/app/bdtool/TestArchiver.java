package glank.app.bdtool;

import java.io.File;

import junit.framework.TestCase;
import org.junit.Assert;

import glank.app.test.TestTools;

public class TestArchiver extends TestCase{

	public TestArchiver(String name){
		super(name);
	}

	public void testA() throws Exception{
		TestTools tools = TestTools.get();
		File insurance = tools.getDirectory("insurance");
		File insurance2 = tools.getDirectory("insurance2");
		File archive = tools.getDirectory("archive");

		String insuranceP = insurance.getAbsolutePath();
		String insurance2P = insurance2.getAbsolutePath();
		String archiveP = archive.getAbsolutePath();

		String[] argsArchive = new String[]{
			"-a", insuranceP, archiveP
		};

		String[] argsDearchive = new String[]{
			"-d", insurance2P, archiveP
		};
		
		try{
			Archiver.main(argsArchive);
		//	Archiver.main(argsDearchive);
		}
		catch(Exception e){
			throw e;
		}
		catch(Throwable t){
			throw new Exception(t.getMessage());
		}

		tools.assertDirEquals(insurance, insurance2);
	}
}

package glank.app.bdtool;

import glank.app.compress.BackupFile;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class Archiver{

	public static void main(String[] args) throws Throwable{
		if(args.length != 3)
			printUsage();
		else if(args[0].equals("-a"))
			archive(args[1], args[2]);
		else if(args[1].equals("-d"))
			dearchive(args[2], args[1]);
		else
			printUsage();
	}

	public static void archive(String dirIn, String dirOut) throws Throwable{
		File source = new File(dirIn);
		File target = new File(dirOut);

		if(!source.isDirectory())
			throw new RuntimeException(dirIn+" is not a directory.");
		else if(target.exists() && !target.isDirectory())
			throw new RuntimeException(dirOut+" is not a directory.");
		
		if(!target.exists())
			target.mkdir();

		BackupFile archive = new BackupFile(new File(dirOut+File.separator+dirIn+".arc"));
		ObjectOutputStream nameFile = new ObjectOutputStream(new FileOutputStream(dirOut+File.separator+"names.log"));

		File[] files = source.listFiles();
		for(int i = 0; i < files.length; i++){
			File file = files[i];
			if(file.isDirectory())
				archive(file.getAbsolutePath(),dirOut+File.separator+file.getName());
			else{
				archive.backup(file);
				nameFile.writeObject(file.getName());
			}
		}
		nameFile.close();
	}

	public static void dearchive(String dirIn, String dirOut) throws Throwable{
		File archive = new File(dirIn);
		File target = new File(dirOut);

		if(!target.exists())
			target.mkdir();

		BackupFile backup =	new BackupFile(new File(dirIn+File.separator+dirOut+".arc"));
		ObjectInputStream nameFile = new ObjectInputStream(new FileInputStream(dirIn+File.separator+"names.log"));

		while(backup.getBackups() > 0)
			backup.revertTo(new File(dirOut+File.separator+(String)nameFile.readObject()));
		nameFile.close();

		File[] files = archive.listFiles();
		for(int i = 0; i < files.length; i++){
			File file = files[i];
			if(file.isDirectory())
				dearchive(file.getAbsolutePath(),dirOut+File.separator+file.getName());
		}
	}

	public static void printUsage(){
		System.out.println("...Archiver (-a|-d) <directory> <archive>");
		System.out.println("\t-a:\tCreate a new archive.");
		System.out.println("\t-d:\tRevert from an existing archive.");
	}
}

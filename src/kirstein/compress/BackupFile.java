package kirstein.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.zip.DataFormatException;


public class BackupFile {
	private File file;
	
	public BackupFile(File file) throws IOException{
		this.file = file;
		if(!file.exists())
			file.createNewFile();
	}
	
	public int getBackups() throws IOException{
		if(file.length()==0)
			return 0;
		RandomAccessFile rand = new RandomAccessFile(file, "rw");
		int backups = rand.readInt();
		rand.close();
		return backups;
	}
	
	public void backup(File other) throws IOException, DataFormatException{
		//get the current file data
		RandomAccessFile rando = new RandomAccessFile(other, "r");
		if(rando.length()>Integer.MAX_VALUE)
			throw new IOException("Input file too big.");
		byte[] current = new byte[(int)rando.length()];
		int read = 0;
		while(read<current.length)
			read+=rando.read(current, read, current.length-read);
		rando.close();
		
		backup(current);
	}
	
	public void backup(byte[] bytes) throws IOException, DataFormatException{
		byte[] current = bytes;
		
		RandomAccessFile rand = new RandomAccessFile(file, "rw");
		if(rand.length()==0){
			
			rand.writeInt(1); //write the number of backups
			rand.writeLong(12); //write the location of the last backup
			//compress and store the current file data
			current = ZipUtil.compress(current);
			writeBackupData(rand, -1, current);
		}
		else{
			int backups = rand.readInt();
			long pos = rand.readLong();
			//get the old data
			rand.seek(pos);
			long oldPos = rand.readLong(); //previous backups header
			int length = rand.readInt();
			byte[] old = new byte[length];
			int read = 0;
			while(read<length)
				read+=rand.read(old, read, length-read);
			//decompress the old data
			old = ZipUtil.decompress(old);
			
			//create delta backup data
			Backup backup = new Backup(old,current);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(backup);
			byte[] backupBytes = baos.toByteArray();
			//compress the backup with zip
			backupBytes = ZipUtil.compress(backupBytes);
			//store the backup bytes
			rand.seek(pos);
			writeBackupData(rand, oldPos, backupBytes);
			//update the header
			oldPos = pos;
			pos = rand.getFilePointer();
			rand.seek(0);
			rand.writeInt(++backups);
			rand.writeLong(pos);
			//compress and store the current file data
			rand.seek(pos);
			current = ZipUtil.compress(current);
			writeBackupData(rand, oldPos, current);
		}
		rand.close();
	}
	
	private void writeBackupData(RandomAccessFile rand, long oldPos, byte[] data) throws IOException{
		System.out.println("Writing Backup to: " + rand.getFilePointer());
		rand.writeLong(oldPos); //location of prev backup head
		rand.writeInt(data.length);
		rand.write(data);
	}
	
	public byte[] getReversion() throws IOException, DataFormatException, ClassNotFoundException{
		RandomAccessFile rand = new RandomAccessFile(file, "rw");
		int backups = rand.readInt();
		long pos = rand.readLong();
		rand.seek(pos);
		
		//### do the revert ###
		long oldPos = rand.readLong();
		int length = rand.readInt();
		byte[] old = new byte[length];
		int read = 0;
		while(read<length)
			read+=rand.read(old, read, length-read);
		//decompress backup
		old = ZipUtil.decompress(old);
		byte[] ret = old;
		
		//### update this file ###
		if(backups==1){ //delete the file
			rand.setLength(0);
			rand.close();
			return ret;
		}
		else{
			//undo the last backup compression
			rand.seek(oldPos);
			pos = oldPos;
			oldPos = rand.readLong();
			length = rand.readInt();
			byte[] backupBytes = new byte[length];
			read = 0;
			while(read<length)
				read+=rand.read(backupBytes, read, length-read);
			//unzip the backup
			backupBytes = ZipUtil.decompress(backupBytes);
			ByteArrayInputStream bais = new ByteArrayInputStream(backupBytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Backup backup = (Backup)ois.readObject();
			old = backup.revert(old);
			//rewrite the old backup
			old = ZipUtil.compress(old);
			rand.seek(pos);
			writeBackupData(rand, oldPos, old);
			long newLength = rand.getFilePointer();
			//update header
			rand.seek(0);
			rand.writeInt(backups-1);
			rand.writeLong(pos);
			//truncate
			rand.setLength(newLength);
			rand.close();
		}
		return ret;
	}
	
	public void revertTo(File other) throws IOException, DataFormatException, ClassNotFoundException{
		byte[] old = getReversion();
		FileOutputStream fos = new FileOutputStream(other);
		fos.write(old);
		fos.close();
	}
}

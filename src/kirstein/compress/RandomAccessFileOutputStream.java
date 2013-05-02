package kirstein.compress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileOutputStream extends OutputStream{
	private RandomAccessFile file;
	
	public RandomAccessFileOutputStream(File file) throws FileNotFoundException{
		this.file = new RandomAccessFile(file, "w");
	}
	
	@Override
	public void write(int b) throws IOException {
		file.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException{
		file.write(b,off,len);
	}
	
	@Override
	public void write(byte[] b) throws IOException{
		file.write(b);
	}
	
	public void seek(long pos) throws IOException{
		file.seek(pos);
	}
	
	public long getPosition() throws IOException{
		return file.getFilePointer();
	}
	
	public long getLength() throws IOException{
		return file.length();
	}
	
	@Override
	public void close(){
		try {
			file.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}

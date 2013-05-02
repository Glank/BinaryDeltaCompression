package kirstein.compress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileInputStream extends InputStream{
	private RandomAccessFile file;
	
	public RandomAccessFileInputStream(File file) throws FileNotFoundException{
		this.file = new RandomAccessFile(file, "r");
	}

	@Override
	public int read() throws IOException {
		return file.read();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException{
		return file.read(b,off,len);
	}
	
	@Override
	public int read(byte[] b) throws IOException{
		return file.read(b);
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

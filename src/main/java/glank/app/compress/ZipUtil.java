package glank.app.compress;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipUtil {
	public static byte[] compress(byte[] bytes){
		Deflater deflater = new Deflater();
        deflater.setInput(bytes);
        deflater.finish();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length*7/10);
        byte[] buffer = new byte[1024];
        while(!deflater.finished())
        {
        	int bytesCompressed = deflater.deflate(buffer);
        	baos.write(buffer,0,bytesCompressed);
        }
        return baos.toByteArray();
	}
	
	public static byte[] decompress(byte[] bytes) throws DataFormatException{
		Inflater inflater = new Inflater();
        inflater.setInput(bytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while(!inflater.finished())
        {
        	int bytesDecompressed = inflater.inflate(buffer);
        	baos.write(buffer,0,bytesDecompressed);
        }
        return baos.toByteArray();
	}
}

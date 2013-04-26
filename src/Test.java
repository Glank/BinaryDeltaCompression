import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

public class Test {
	public static void main(String[] args) throws Throwable{
		doTextTests();
		System.out.println("--------------------------------------------------\n");
		doImgTests();
		System.out.println("--------------------------------------------------\n");
		doRandTests();
	}
	
	public static void doTextTests() throws IOException{
		doTest(1,"txt", "text");
		doTest(2,"txt", "text");
		doTest(3,"txt", "text");
		doTest(4,"txt", "text");
	}
	
	public static void doImgTests() throws IOException{
		doTest(1,"jpg", "imgs");
		doTest(2,"jpg", "imgs");
		doTest(3,"jpg", "imgs");
		doTest(4,"jpg", "imgs");
	}
	
	public static void doRandTests() throws IOException{
		doTest(1,"rnd", "rand");
		doTest(2,"rnd", "rand");
		doTest(3,"rnd", "rand");
		doTest(4,"rnd", "rand");
		doTest(5,"rnd", "rand");
		doTest(6,"rnd", "rand");
	}
	
	public static void createRandomFile(int length, int testNumber, String type) throws IOException{
		byte[] rand = new byte[length];
		for(int i = 0; i < length; i++){
			rand[i] = (byte)(Math.random()*256);
		}
		writeBytesToFile("testFiles/rand/test"+testNumber+"_"+type+".rnd",rand);
	}
	
	public static void doTest(int n, String fileType, String subfolder) throws IOException{
		System.out.println("## "+subfolder+" Test "+n+" ##");
		byte[] first = getFileBytes("testFiles/"+subfolder+"/test"+n+"_init."+fileType);
		byte[] second = getFileBytes("testFiles/"+subfolder+"/test"+n+"_changed."+fileType);
		byte[] firstZipped = zipCompress(first);
		long time = System.nanoTime();
		Reversion r = new Reversion(first, second);
		byte[] rbytes = r.toBytes();
		byte[] rzipped = zipCompress(rbytes);
		time = System.nanoTime()-time;
		System.out.println("First: " + first.length);
		System.out.println("First Zipped: " + firstZipped.length);
		System.out.println("Second: " + second.length);
		System.out.println("Revision: " + rbytes.length);
		System.out.println("Revision Zipped: " + rzipped.length);
		System.out.println("Milliseconds To Create Revision: " + time/1000000.0);
		System.out.println("Space Saved: " + (first.length-rbytes.length));
		System.out.println("Percent Size: " + (r.size()/(double)first.length));
		System.out.println("w/ Zip Space Saved: " + (first.length-rzipped.length));
		System.out.println("w/ Zip Percent Size: " + (rzipped.length/(double)first.length));
		System.out.println("Comp Zip Space Saved: " + (firstZipped.length-rzipped.length));
		System.out.println("Comp Zip Percent Size: " + (rzipped.length/(double)firstZipped.length));
		byte[] reversion = r.revert(second);
		//verify
		if(reversion.length!=first.length)
			System.err.println("ERROR");
		for(int i = 0; i < first.length; i++){
			if(first[i]!=reversion[i])
				System.err.println("ERROR");
		}
		System.out.println();
	}
	
	public static byte[] getFileBytes(String fileName) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(fileName);
		byte[] buffer = new byte[256];
		int read;
		
		while((read=fis.read(buffer))>0){
			baos.write(buffer,0,read);
		}
		fis.close();
		return baos.toByteArray();
	}
	
	public static void writeBytesToFile(String fileName, byte[] bytes) throws IOException{
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(bytes);
		fos.close();
	}
	
	public static byte[] zipCompress(byte[] bytes){
		Deflater deflater = new Deflater();
		deflater.setInput(bytes);
		deflater.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
		byte[] buffer = new byte[1024];
		while(!deflater.finished())
		{            
			int bytesCompressed = deflater.deflate(buffer);
			bos.write(buffer,0,bytesCompressed);
		}
		return bos.toByteArray();
	}
}

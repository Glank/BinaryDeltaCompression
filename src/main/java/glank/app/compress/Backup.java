package glank.app.compress;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class Backup implements Serializable{
	private static final long serialVersionUID = 3997730568675235487L;
	private static final int FLAG = 192;
	private static final int ADD = 64;
	private static final int COPY = 128;
	private static final int COPY_WITH_OVERFLOW = 0;
	private static final int SKIP = 192;
	
	private byte[] commands;
	private byte[] oldData;
	
	public Backup(byte[] old, byte[] current){
		//match old and new data as closely as possible
		int oldIndex = 0;
		int currentIndex = 0;
		int[] oldEquivIndexes = new int[old.length];
		int[] curEquivIndexes = new int[current.length];
		//-1 means no match was found for that byte
		for(int i = 0; i < old.length; i++)
			oldEquivIndexes[i] = -1;
		for(int i = 0; i < current.length; i++)
			curEquivIndexes[i] = -1;
		while(oldIndex < old.length && currentIndex < current.length){
			int od = 0; //old bytes removed
			int cd = 1; //current bytes added
			//look for a match within 128 bytes
			inner:while(od < 128 && (oldIndex+od<old.length || currentIndex+cd<current.length)){
				//if old data was removed
				if(oldIndex+od<old.length && old[oldIndex+od]==current[currentIndex]){
					oldEquivIndexes[oldIndex+od] = currentIndex;
					curEquivIndexes[currentIndex] = oldIndex+od;
					oldIndex+=od;
					break inner;
				}
				//if new data was added
				else if(currentIndex+cd<current.length && old[oldIndex]==current[currentIndex+cd]){
					oldEquivIndexes[oldIndex] = currentIndex+cd;
					curEquivIndexes[currentIndex+cd] = oldIndex;
					currentIndex+=cd;
					break inner;
				}
				od++;
				cd++;
			}
			oldIndex++;
			currentIndex++;
		}
		//compile an alternating list of new/old lengths
		ByteArrayOutputStream commands = new ByteArrayOutputStream();
		ByteArrayOutputStream oldData = new ByteArrayOutputStream();
		currentIndex = 0;
		oldIndex = 0;
		top:while(oldIndex<old.length){
			int i = 0;
			//if the data needs to be copied from the old data
			while(i<64 && oldIndex+i<old.length && oldEquivIndexes[oldIndex+i]==-1){
				oldData.write(old[oldIndex+i]);
				i++;
			}
			if(i!=0){
				oldIndex+=i;
				int com = ADD|(i-1);
				commands.write(com);
				continue top;
			}
			//if some current data needs to be skipped
			if(oldEquivIndexes[oldIndex]!=currentIndex){
				int skip = oldEquivIndexes[oldIndex]-currentIndex;
				if(skip>64)
					skip = 64;
				currentIndex+=skip;
				int com = SKIP|(skip-1);
				commands.write(com);
				continue top;
			}
			//if some data needs to be copied from the current data
			while(i<16384 && oldIndex+i<old.length && oldEquivIndexes[oldIndex+i]==currentIndex){
				i++;
				currentIndex++;
			}
			if(i!=0){
				//could be a large number of copied bytes
				//so we use an overflow stack
				oldIndex+=i;
				if(i<=64){
					int com = COPY|(i-1);
					commands.write(com);
				}
				else{
					int com = COPY_WITH_OVERFLOW|((i-1)&63);
					int of = (i-1)>>6;
					commands.write(com);
					commands.write(of);
				}
			}
		}
		this.commands = commands.toByteArray();
		this.oldData = oldData.toByteArray();
	}
	
	public byte[] toBytes(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(commands.length);
			dos.write(commands);
			dos.write(oldData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public byte[] revert(byte[] current){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream inO = new ByteArrayInputStream(oldData);
		ByteArrayInputStream inC = new ByteArrayInputStream(current);
		for(int c = 0; c < commands.length; c++){
			int flag = FLAG&commands[c];
			int n = (~FLAG)&commands[c];
			if(n<0)
				n+=256;
			n++;
			if(flag==ADD){
				//System.out.println("Add: " + n);
				for(int i = 0; i < n; i++)
					out.write(inO.read());
			}
			else if(flag==SKIP){
				//System.out.println("Skip: " + n);
				inC.skip(n);
			}
			else if(flag==COPY){
				//System.out.println("Copy: " + n);
				for(int i = 0; i < n; i++)
					out.write(inC.read());
			}
			else if(flag==COPY_WITH_OVERFLOW){
				n+=(255&commands[++c])<<6;
				//System.out.println("Copy: " + n);
				for(int i = 0; i < n; i++)
					out.write(inC.read());
			}
		}
		return out.toByteArray();
	}
}

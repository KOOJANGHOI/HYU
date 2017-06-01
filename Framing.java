package proj3_1;


public class Framing {
	
	public byte[] outdata;
	public static CRC c;
	
	public Framing(byte seq, byte ack, byte flag, byte[] data) {
		c = new CRC();
		outdata = new byte[136];
		// seq , ack , flag , data.length 저장
		outdata[0] = seq;
		outdata[1] = ack;
		outdata[2] = flag;
		outdata[3] = (byte)data.length;
		// CRC를 outdata에 복사
		System.arraycopy(c.CRCmake(data), 0, outdata, 4, 4);
		// dara를 outdata에 복사
		System.arraycopy(data, 0, outdata, 8, data.length);
	}
	public Framing(byte seq, byte ack, byte flag){
		outdata = new byte[4];
		outdata[0] = seq;
		outdata[1] = ack;
		outdata[2] = flag;
		outdata[3] = 0;
	}
	public byte[] framing(){
		return outdata;
	}
}

package proj3_1;

import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.nio.*;

public class CRC {

	Checksum checksum = new CRC32();

	// data로 checksum을 만들어 반환 
	public byte[] CRCmake(byte[] data) {
		checksum.update(data, 0, data.length);
		long chksum = checksum.getValue();
		byte[] buff = new byte[4];
		buff = longToBytes(chksum);
		return buff;
	}

	// byte[]형의 checksum 2개를 비교
	public boolean isequalCRC(byte[] checksum1, byte[] checksum2) {
		long num1, num2;
		num1 = bytesToLong(checksum1);
		num2 = bytesToLong(checksum2);
		if (num1 == num2)
			return true;
		else
			return false;
	}

	static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] ped = { 0x00, 0x00, 0x00, 0x00 };
		buffer.put(ped);
		buffer.put(bytes);
		buffer.flip();
		return buffer.getLong();
	}
}

package proj3_1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class RcvThread extends Thread {
	
	DatagramSocket socket;		// 소켓
	DatagramPacket rcv_packet;	// 받은 패킷
	Signaling signal;			// Signaling 변수 signal
	boolean temp = true;		// RcvThread를 제어할 boolean 변수

	// FLAGs
	public static final byte FRAME_FLAG_NOARQ_DATA = 1;
	public static final byte FRAME_FLAG_NOARQ_ACK = 2;
	public static final byte FRAME_FLAG_STOPNWAIT_DATA = 11;
	public static final byte FRAME_FLAG_STOPNWAIT_ACK = 12;
	public static final byte FRAME_FLAG_NAK = 99;

	// RcvThread 생성자
	RcvThread(DatagramSocket socket, Signaling signal) {
		this.socket = socket;
		this.signal = signal;
	}

	// RcvThread 동작
	public void run() {
		
		byte buff[] = new byte[512];
		rcv_packet = new DatagramPacket(buff, buff.length);

		byte seq, ack, flag, len;	// sequence , ack , flag , length
		byte[] crc = new byte[4];	// CRC
		byte[] rcv_msg = new byte[136];	// 받은 메시지
		byte[] msg = new byte[128];	// Error Detection Message
		
		while (temp) {
			// 패킷을 받고 , IP,PORT를 추출
			try {
				socket.receive(rcv_packet);
				UDPChatting.remoteport = rcv_packet.getPort();
				UDPChatting.remoteaddr = rcv_packet.getAddress();
			} catch (IOException e) {
				System.out.println("Thread exception " + e);
			}
			
			// 패킷에서 메시지 추출
			rcv_msg = rcv_packet.getData();
			
			// sequence , ack , flag , length 추출
			seq = rcv_msg[0];
			ack = rcv_msg[1];
			flag = rcv_msg[2];
			len = rcv_msg[3];
			
			// 송신에 대한 ACK가 왔을때
			if (flag == FRAME_FLAG_STOPNWAIT_ACK) {
				// ACK가 sequence에 맞을때
				if (ack == UDPChatting.seq) {
					System.out.println("\nSTATE : Receiving ACK...");
					System.out.println("ACK(no." + ack + ") Received!!!");
					UDPChatting.seq = (byte) ((UDPChatting.seq+ (byte)1)%2); 		// seq증가
					signal.ACKnotifying();	// ACK notify
					printField(rcv_msg);
				}// ACK가 sequence에 맞지 않을때 
				else {
					System.out.println("\nSTATE : Receiving ACK...");
					System.out.println("ACK and seq are not equal!!!");
					printField(rcv_msg);
				}
			}
			// data를 수신할때
			else if (flag == FRAME_FLAG_STOPNWAIT_DATA) {
				System.arraycopy(rcv_msg, 4, crc, 0, 4);
				System.arraycopy(rcv_msg, 8, msg, 0, len);
				// 수신된 data에 Error가 없을때
				if (corrupted(crc, msg)) {
					// sequence가 맞지 않을때
					if (seq != UDPChatting.seq) {
						sendACK(); 		// ACK 전송
						System.out.println();
						System.out.println("\nSTATE : Receiving Data...");
						System.out.println("CRC Correct");
						System.out.println("ACK is not equal!!!");
						UDPChatting.ack = seq;
						printField(rcv_msg);
					}
					// sequence가 맞을때
					else {
						String result = new String(msg).substring(0, msg.length);
						System.out.println();
						System.out.println("\nSTATE : Receiving Data...");
						System.out.println("CRC Correct");
						System.out.println("Received Data : " + result);
						UDPChatting.seq = (byte) ((UDPChatting.seq+ (byte)1)%2);	// seq증가
						UDPChatting.ack = seq;
						sendACK();	// ACK 전송
						printField(rcv_msg);
					}
				}
				// 수신된 data에 Error가 있을때 NAK를 송신
				else {
					System.out.println("Error Detected!!!");
					sendNAK();
					printField(rcv_msg);
				}

			}
			// NAK가 왔을때
			else if (flag == FRAME_FLAG_NAK) {
				System.out.println("Data corrupted!");
				printField(rcv_msg);
			}
			// 예외 상황
			else
				System.out.println("?");
		}

		System.out.println("RcvThread End!!");
	}

	// Error Detection
	public boolean corrupted(byte[] crc, byte[] rsvmsg) {
		byte[] tmp = crc;
		byte[] buffer = rsvmsg;
		byte[] check = new CRC().CRCmake(buffer);
		return (new CRC().isequalCRC(tmp, check));
	}

	// monitoring
	public void printField(byte[] frame) {
		byte tmp = frame[2];
		byte[] buffer = new byte[4];
		
		if (tmp == (byte) FRAME_FLAG_NAK || tmp == (byte) FRAME_FLAG_NOARQ_ACK
				|| tmp == (byte) FRAME_FLAG_STOPNWAIT_ACK) {
			System.out.println("Seq : " + frame[0] + " ACK : " + frame[1] + " Flag : " + frame[2]);
		} else {
			System.arraycopy(frame, 4, buffer, 0, 4);
			System.out.println("Seq : " + frame[0] + " ACK : " + frame[1] + " Flag : " + frame[2] + "\n" + "Length : "
					+ frame[3] + " CRC : " + buffer);
		}
		System.out.println();
	}

	// ACK 전송
	public void sendACK() {
		/* Timeout Test
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		System.out.println("STATE : Sending ACK");
		byte[] ACK = new byte[4];
		ACK = new Framing(UDPChatting.seq, UDPChatting.ack, FRAME_FLAG_STOPNWAIT_ACK).framing();
		DatagramPacket ack_packet = new DatagramPacket(ACK, ACK.length, UDPChatting.remoteaddr,
				UDPChatting.remoteport);
		try {
			socket.send(ack_packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// NAK 전송
	public void sendNAK() {
		System.out.println("STATE : Sending NAK");
		byte[] NAK = new byte[4];
		NAK = new Framing(UDPChatting.seq, UDPChatting.ack, FRAME_FLAG_NAK).framing();
		DatagramPacket nak_packet = new DatagramPacket(NAK, NAK.length, UDPChatting.remoteaddr,
				UDPChatting.remoteport);
		try {
			socket.send(nak_packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void graceout() {
		temp = false;
	}

}

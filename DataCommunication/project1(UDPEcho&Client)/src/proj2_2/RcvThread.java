package proj2_2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class RcvThread extends Thread {

	DatagramSocket socket; // 소켓 선언
	DatagramPacket rcvpacket; // 패킷(받는) 선언
	DatagramPacket ackpacket; // 패킷(ACK를 보내는) 선언
	Signaling signal; // Signaling 변수 signal
	public static String ACKMessage = "ACK"; // ACK 메시지
	public static byte buffer[] = new byte[512]; // 버퍼
	boolean check = true; // RcvThread를 제어하기 위한 boolean 변수

	// RcvThread 생성자
	RcvThread(DatagramSocket socket, Signaling signal) {
		this.socket = socket;
		this.signal = signal;
	}

	// RcvThread 의 동작
	public void run() {
		// 패킷 생성
		byte buff[] = new byte[100];
		rcvpacket = new DatagramPacket(buff, buff.length);

		// check 가 true 이면 계속 패킷을 기다린다.
		while (check) {
			try {
				// 받은 패킷으로 소켓 생성
				socket.receive(rcvpacket);

				// 클라이언트의 IP 주소와 PORT번호를 확인
				UDPChatting.remoteport = rcvpacket.getPort();
				UDPChatting.remoteaddr = rcvpacket.getAddress();

			} catch (IOException e) {
				System.out.println("IOException : " + e);
			}
			// 받은 패킷을 String 변수로 만들고 , 출력하는 부분
			String ReceivedMessage = new String(rcvpacket.getData()).substring(0, rcvpacket.getLength());

			// 받은 패킷의 메시지가 "ACK"일때 ACK로 판단
			if (ReceivedMessage.equalsIgnoreCase("ACK")) {
				// ACK를 받았으니 ACKnotify() 호출
				signal.ACKnotify();
			} else {
				// 그냥 메시지일때 그대로 콘솔에 출력
				System.out.println("\n Receive Message : " + ReceivedMessage + " and Transmit ACK");
				// ACK을 위한 패킷을 만든다
				buffer = ACKMessage.getBytes();
				ackpacket = new DatagramPacket(buffer, buffer.length, UDPChatting.remoteaddr, UDPChatting.remoteport);
				
				/*
				//Timeout 검사를 위해 Timeout보다 긴 시간의 딜레이를 준다
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				*/
				
				// 만들어진 ACK패킷을 상대방에게 보낸다
				try {
					socket.send(ackpacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		// RcvThread 종료
		System.out.println("RcvThread off");
	}

	public void graceout() {
		check = false;
	}

}

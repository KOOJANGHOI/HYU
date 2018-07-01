package proj2_2;

import java.io.*;
import java.net.*;

public class UDPChatting {

	final static int MAXBUFFER = 512; // MAXINUM OF BUFFER
	static RcvThread rcvThread; // RcvThread
	public static DatagramSocket socket; // 소켓
	public static DatagramPacket packet; // 패킷
	public static DatagramPacket ACKpacket; // 패킷
	public static InetAddress remoteaddr; // remote IP주소
	public static int remoteport = 0; // remote PORT번호
	public InetAddress myinetaddr; // my IP주소
	public static int myport = 0; // my PORT번호
	static Signaling signal = new Signaling(); // signal
	static Timeout ticks; // Timeout 변수
	public static boolean check = false; // 패킷의 재전송을 제어하기 위한 boolean 변수
	public static String sendMessage; // 보낼 메시지를 담을 String 변수
	public static String ACKMessage = "ACK";
	public static byte buffer[] = new byte[512];

	// main thread is send thread
	public static void main(String[] args) {
		// if argc == 2 , remote IP , PORT
		if (args.length == 2) {
			remoteport = Integer.parseInt(args[1]);
			try {
				remoteaddr = InetAddress.getByName(args[0]);
			} catch (UnknownHostException e) {
				System.out.println("Error on port" + remoteport);
				e.printStackTrace();
			}
			// if argc == 1 ,my PORT
		} else if (args.length == 1) {
			myport = Integer.parseInt(args[0]);
		} else {
			System.out.println("Usage: args must be localhost port or port");
			System.exit(0);
		}

		try {
			// 소켓 생성
			if (myport == 0) {
				socket = new DatagramSocket();
			} else {
				socket = new DatagramSocket(myport);
			}
			System.out.println("Datageam socket is created");

			ticks = new Timeout(); // Timeout 인스턴스 생성
			rcvThread = new RcvThread(socket, signal); // RcvThread 인스턴스 생성
			rcvThread.start(); // RcvThread 동작

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			check = false;
			while (true) {
				// ACK 수신을 체크하는 If 문
				if (check) {
					if (Signaling.ACKNOTIFY) {
						// ACK를 수신했을때 , Timeout을 초기화
						System.out.println("\n ACK Reecived!! Reset Timeout");
						ticks.TimeoutReset(0);
					} else {
						// ACK를 수신하지 못하였을때 , 패킷 재전송 후 Timeout을 재설정
						System.out.println("\n Retransmission!!(Message : " + sendMessage + " )");
						socket.send(packet);
						ticks.TimeoutSet(0, 3000, signal);
					}
				}
				check= true;

				// keyboard로 message를 입력받아서
				System.out.print("Input Data : ");
				sendMessage = br.readLine();
				
				// remote IP 주소가 null이 아닐때
				if ((remoteaddr != null)) {
					// 패킷 생성
					buffer = sendMessage.getBytes();
					packet = new DatagramPacket(buffer, buffer.length, remoteaddr, remoteport);
					// 패킷 전송
					socket.send(packet);
					// 패킷 전송 하면서 3000ms로 Timeout를 set
					ticks.TimeoutSet(0, 3000, signal);
					// RcvThread는 ACK를 기다린다
					signal.waitingACK();

				} else {
					// remote IP 주소가 부적절
					System.out.println("remote IP address is not appropriate");
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		rcvThread.graceout();
		System.out.println("grace out called");
		socket.close();
	}
}

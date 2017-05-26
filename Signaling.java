package proj3_1;

public class Signaling {

	public static boolean ACKNOTIFY = false;	// ACK를 받았는지 체크
	public static boolean TIMENOTIFY = false;	// Timeout 인지 체크

	// Timeout 발생시
	public synchronized void Timeoutnotifying() {
		TIMENOTIFY = false;
		notify();
	}

	// ACK 초기화
	public synchronized void initACK() {
		ACKNOTIFY = false;
	}

	// ACK를 받았을때
	public synchronized void ACKnotifying() { 							
		ACKNOTIFY = true;
		notify();
	}

	// ACK를 기다릴때
	public synchronized void waitingACK() { 						
		try {
			initACK();
			wait();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException : " + e);
		}
	}
}

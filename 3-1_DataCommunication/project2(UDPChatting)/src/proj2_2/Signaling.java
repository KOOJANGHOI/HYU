package proj2_2;

public class Signaling {

	public static boolean ACKNOTIFY = false; // Message를 받았는지 체크하는 boolean 변수
	public static boolean TIMENOTIFY = false; // Timeout 인지 체크하는 boolean 변수

	// Timeout 발생시
	public synchronized void Timeoutnotify() {
		TIMENOTIFY = false;
		notify();
	}

	// MSG를 초기화
	public synchronized void initACK() {
		ACKNOTIFY = false;
	}

	// ACK를 받았을때
	public synchronized void ACKnotify() {
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

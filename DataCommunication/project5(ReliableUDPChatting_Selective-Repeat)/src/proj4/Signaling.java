package proj4;

public class Signaling {

	public static boolean ACKNOTIFY = false; // ACK를 받았는지 체크
	public byte ACKNO = 1;
	public static boolean NAKNOTIFY = false; // NAK를 받았는지 체크
	public byte NAKNO = 1;
	public static boolean TIMENOTIFY = false; // Timeout 인지 체크
	public static boolean SABMENOTIFY = false;
	public static boolean SABMEUANOTIFY = false;
	public static boolean DISCNOTIFY = false;
	public static boolean DISCUANOTIFY = false;

	public synchronized void Timeoutnotifying() { // Timeout 발생시
		TIMENOTIFY = false;
		notify();
	}

	public synchronized void initACK() { // ACKNOTIFY 초기화
		ACKNOTIFY = false;
	}

	public synchronized void ACKnotifying(byte RN) { // ACK를 받았을때
		ACKNOTIFY = true;
		ACKNO = RN;
		notify();
	}

	public synchronized void waitingACK() { // ACK를 기다릴때
		try {
			initACK();
			wait();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException : " + e);
		}
	}
	
	public synchronized void initNAK() { // NAKNOTIFY 초기화
		NAKNOTIFY = false;
	}

	public synchronized void NAKnotifying(byte RN) { // NAK를 받았을때
		NAKNOTIFY = true;
		NAKNO = RN;
		notify();
	}

	public synchronized void waitingNAK() { // NAK를 기다릴때
		try {
			initNAK();
			wait();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException : " + e);
		}
	}

	public synchronized void initSABMEUA() { // SABMEUANOTIFY 초기화
		SABMEUANOTIFY = false;
	}

	public synchronized void SABMEUAnotifying() { // SABMEUA를 받았을때
		SABMEUANOTIFY = true;
		notify();
	}

	public synchronized void waitingSABMEUA() { // SABMEUA를 기다릴때
		try {
			initSABMEUA();
			wait();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException : " + e);
		}
	}

	public synchronized void initSABME() { // SABMENOTIFY 초기화
		SABMENOTIFY = false;
	}

	public synchronized void SABMEnotifying() { // SABME를 받았을때
		SABMENOTIFY = true;
		notify();
	}

	public synchronized void waitingSABME() { // SABME를 기다릴때
		try {
			initSABME();
			wait();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException : " + e);
		}
	}

	public synchronized void initDISC() { // DISC 초기화
		DISCNOTIFY = false;
	}

	public synchronized void DISCnotifying() { // DISC를 받았을때
		DISCNOTIFY = true;
		notify();
	}

	public synchronized void waitingDISC() { // DISC를 기다릴때
		try {
			initDISC();
			wait();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException : " + e);
		}
	}
	
	public synchronized void initDISCUA() { // DISCUA 초기화
		DISCUANOTIFY = false;
	}

	public synchronized void DISCUAnotifying() { // DISCUA를 받았을때
		DISCUANOTIFY = true;
		notify();
	}

	public synchronized void waitingDISCUA() { // DISCUA를 기다릴때
		try {
			initDISCUA();
			wait();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException : " + e);
		}
	}
}

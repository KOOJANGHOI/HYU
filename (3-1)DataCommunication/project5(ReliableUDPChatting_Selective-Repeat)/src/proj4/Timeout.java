package proj4;

import java.util.Timer;
import java.util.TimerTask;

public class Timeout {

	Timer timer = new Timer(); // Timeout 인스턴스 생성
	TimeoutTask[] TimerArr = new TimeoutTask[1000]; // TimeoutTask 인스턴스 생성
	Signaling signal;
	boolean temp = false;

	// Timeout 설정
	public void TimeoutSet(byte index, int ms, Signaling signal) {
		this.signal = signal;
		this.TimerArr[index] = new TimeoutTask(index);
		timer.schedule(this.TimerArr[index], ms);
		System.out.println("Set Timer[No."+index+"]");
	}

	// Timeout 초기화
	public void TimeoutReset(byte index) {
		this.TimerArr[index].cancel();
	}

	class TimeoutTask extends TimerTask {
		int num;
		TimeoutTask(int num) {
			this.num = num;
		}

		public void run() {
			if (temp)
				System.out.println("Time's up! ");
			signal.Timeoutnotifying();
			this.cancel();
		}
	}
}

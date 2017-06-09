package proj2_2;

import java.util.Timer;
import java.util.TimerTask;

public class Timeout {
	
	Timer timer = new Timer();								// Timer 객체 생성
	TimeoutTask[] TimerArr = new TimeoutTask[16];		// TimeoutTask 객체 생성
	Signaling signal;

	boolean temp = false;

	// Timeout 설정
	public void TimeoutSet(int index , int ms , Signaling signal ) {
		this.signal = signal;
		this.TimerArr[index] = new TimeoutTask(index);
		timer.schedule(TimerArr[index], ms);
	}
	
	// Timeout 설정 해제 
	public void TimeoutReset(int index) {
		int i = index;
		if (temp)
			System.out.println("Timer reset!(no." + i + ")");
		TimerArr[i].cancel();
	}

	// TimeoutTask 클래스
	class TimeoutTask extends TimerTask {
		int num;

		TimeoutTask(int num) {
			this.num = num;
		}

		public void run() {
			if (temp)
				System.out.println("Time's up! ");
			signal.Timeoutnotify();
			this.cancel();
		}
	}
}

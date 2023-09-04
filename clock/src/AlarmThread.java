import java.util.Arrays;

import clock.io.ClockOutput;

public class AlarmThread extends Thread{
	private ClockOutput output;
	private Monitor monitor;
	
	public AlarmThread(ClockOutput output, Monitor monitor) {
		this.output = output;
		this.monitor = monitor;
	}
	@Override
	public void run() {
		try {
			while(true) {
				int[] currentTime = monitor.getTime();
				int[] alarmTime = monitor.getAlarmTime();
				if (Arrays.equals(currentTime, alarmTime)) {
					long t0 = System.currentTimeMillis();
					long t1 = t0;
						
					while(monitor.getAlarmStatus() && t1-t0 < 21000) {
						output.alarm();
						long t_temp = System.currentTimeMillis();
						Thread.sleep(1000 - (t_temp - t1));
						t1 = System.currentTimeMillis();
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
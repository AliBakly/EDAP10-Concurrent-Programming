import java.util.concurrent.Semaphore;

import clock.io.ClockOutput;

public class Monitor {
	private int h;
	private int m;
	private int s;
	private int h_alarm = 0;
	private int m_alarm = 0;
	private int s_alarm = 0;
	private boolean alarm_status = false;
	private Semaphore mutex = new Semaphore(1);
	
	public Monitor(int h, int m, int s) {
		this.h = h;
		this.m = m;
		this.s = s;
	}
	
	public int[] getTime() throws InterruptedException{
		mutex.acquire();
		int[] time = {this.h, this.m, this.s};
		mutex.release();
		return time;
	}
	
	public int[] getAlarmTime() throws InterruptedException{
		mutex.acquire();
		int[] alarmTime = {this.h_alarm, this.m_alarm, this.s_alarm};
		mutex.release();
		return alarmTime;
	}
	
	public boolean getAlarmStatus() throws InterruptedException {
		mutex.acquire();
		boolean status = alarm_status;
		mutex.release();
		return status;
	}
	
	public void updateTime(int h, int m, int s) throws InterruptedException {
		mutex.acquire();
		this.h = h;
		this.m = m;
		this.s = s;
		mutex.release();
	}
	
	public void updateAlarmTime(int h_alarm, int m_alarm, int s_alarm) throws InterruptedException {
		mutex.acquire();
		this.h_alarm = h_alarm;
		this.m_alarm = m_alarm;
		this.s_alarm = s_alarm;
		mutex.release();
	}
	
	public void setAlarmStatus(ClockOutput output) throws InterruptedException {
		mutex.acquire();
		this.alarm_status = ! alarm_status;
		output.setAlarmIndicator(this.alarm_status);
		mutex.release();
	}
	
    public void timeTick(ClockOutput output) throws InterruptedException {
    	mutex.acquire();
    	if (s == 59 && m == 59 && h == 23) {
    		s = 0;
    		m = 0;
    		h = 0;
    	} else if(s == 59 && m == 59) {
    		s = 0;
    		m = 0;
    		h++;
    	} else if(s == 59) {
    		s = 0;
    		m++;
    	} else {
    		s++;
    	}
    	
    	output.displayTime(h, m, s);
    	mutex.release();
    }
}

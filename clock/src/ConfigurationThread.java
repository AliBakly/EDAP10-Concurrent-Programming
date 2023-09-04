import java.util.concurrent.Semaphore;


import clock.io.Choice;
import clock.io.ClockInput;
import clock.io.ClockOutput;
import clock.io.ClockInput.UserInput;

public class ConfigurationThread extends Thread {
	private ClockOutput output;
	private ClockInput input;
    private Monitor monitor;
    private Semaphore mutex;
    
    public ConfigurationThread(ClockOutput output, ClockInput input, Monitor monitor) {
    	this.output = output;
    	this.input = input;
    	this.monitor = monitor;
    	mutex = this.input.getSemaphore();
    }
    
    @Override
    public void run() {
    	while (true) {
        	try {
				mutex.acquire();
	            UserInput userInput = input.getUserInput();
	            Choice c = userInput.choice();
	            int h = userInput.hours();
	            int m = userInput.minutes();
	            int s = userInput.seconds();
	            if (c == clock.io.Choice.SET_TIME) {
	            	monitor.updateTime(h, m, s);
	            } else if (c == clock.io.Choice.SET_ALARM) {
	            	monitor.updateAlarmTime(h, m, s);
	            } else {
	            	monitor.setAlarmStatus(output);
	            }
	            System.out.println("choice=" + c + " h=" + h + " m=" + m + " s=" + s);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


        }
    }
}
    

import clock.io.ClockOutput;

public class TimeThread extends Thread{
	private ClockOutput output;
    private Monitor monitor;
    
    public TimeThread(ClockOutput output, Monitor monitor) {
    	this.output = output;
    	this.monitor = monitor;
    }
    
    
    @Override
    public void run() {
    	long t1 = System.currentTimeMillis();
    	int i = 1;
    	while (true) {
    		//long t1 = System.currentTimeMillis();
	    	try {
				monitor.timeTick(output);
	    		long t0 = System.currentTimeMillis();
	    		Thread.sleep(1000-(t0 -t1 - i*1000 ));
	    		
				//TimeThread.sleep(1000 - (t0- t1)); // Code above takes some time, so we adjust for it
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	i++;
    	}
    }
}

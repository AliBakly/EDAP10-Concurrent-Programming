import lift.LiftView;
import lift.Passenger;

public class PassengerThread extends Thread{
	private PassengerLiftMonitor monitor;
	private Passenger pass;
	private final LiftView view;
	public PassengerThread(PassengerLiftMonitor monitor, LiftView view) {
		this.monitor = monitor;
		this.view = view;
		pass = view.createPassenger();
	}
	 
	@Override
	public void run() {
		while(true) {
			pass.begin();
			int fromFloor = pass.getStartFloor();
	        int toFloor = pass.getDestinationFloor();
	        
	        try {
	        	monitor.addToEnter(fromFloor);
				monitor.waitForLift(fromFloor, pass);
				pass.enterLift();
				monitor.notifyEntry(fromFloor);
				
				monitor.addToExit(toFloor);
				monitor.waitForDestination(toFloor, pass);
				pass.exitLift();
				monitor.notifyExit(toFloor);
				
				//FIX INTERRUPTED EXC
	        } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        pass.end();
	        pass = view.createPassenger();
		}
	}
}

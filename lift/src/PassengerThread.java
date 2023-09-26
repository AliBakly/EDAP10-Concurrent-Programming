import lift.LiftView;
import lift.Passenger;

public class PassengerThread extends Thread{
	private PassengerLiftMonitor monitor;
	private final Passenger pass;

	public PassengerThread(PassengerLiftMonitor monitor, LiftView view) {
		this.monitor = monitor;
		pass = view.createPassenger();
	}
	 
	@Override
	public void run() {
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
        
	}
}

import lift.LiftView;

public class LiftThread extends Thread{
	
	private PassengerLiftMonitor monitor;
	private final int NBR_FLOORS;
	private final int MAX_PASSENGERS;
	private final LiftView view;
	public LiftThread(PassengerLiftMonitor monitor, int NBR_FLOORS, int MAX_PASSENGERS, LiftView view) {
		this.monitor = monitor;
		this.NBR_FLOORS = NBR_FLOORS;
		this.MAX_PASSENGERS = MAX_PASSENGERS;
		this.view = view;
	}
	 
	@Override
	public void run() {
		int nextFloor = 0;
		boolean direction = true; //true= upp, false = down
		while(true) {
			int currentFloor = nextFloor;   
			
			try {
				monitor.openDoors(view, currentFloor);
				monitor.halt(view, currentFloor);
				monitor.closeDoors(view, currentFloor);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Keeping track if we go up or down

			if(direction) {
				nextFloor++;
			} else {
				nextFloor--;
			}
			
			monitor.updateCurrentFloor(nextFloor); // change function name if not using dooropened
			view.moveLift(currentFloor, nextFloor);
			
			if (nextFloor % (NBR_FLOORS-1) == 0) {
				direction = !direction;
			}
			 
		 }
	}
}

import java.util.Arrays;
import lift.LiftView;
import lift.Passenger;

public class PassengerLiftMonitor {
	private int[] toEnter; // number of passengers waiting to enter the lift at each floor
	private int[] toExit; // number of passengers (in lift) waiting to exit at each floor
	private int floorNbr = 0; //  the floor the lift is currently on,
	private int passengersNbr; //the number of passengers currently in the lift (0 .. MAX_PASSENGERS),
	private int MAX_PASSENGERS;
	private boolean doorOpened = false;
	private int currentlyEntering = 0;
	private int currentlyExiting = 0;

	public PassengerLiftMonitor(int NBR_FLOORS, int MAX_PASSENGERS) {
		this.toEnter = new int[NBR_FLOORS];
		this.toExit = new int[NBR_FLOORS];
		this.MAX_PASSENGERS = MAX_PASSENGERS;
	}
	
	public synchronized void addToEnter(int fromFloor) {
		toEnter[fromFloor]++;
		notifyAll();
	}
	
	public synchronized void addToExit(int toFloor) {
		toExit[toFloor]++; 
		notifyAll();
	}
	
	public synchronized void updateCurrentFloor(int toFloor) {
		floorNbr = toFloor;
	}

	public synchronized void halt(LiftView view, int fromFloor) throws InterruptedException {
		while(Arrays.stream(toEnter).sum() == 0 &&  Arrays.stream(toExit).sum()==toExit[fromFloor]) {
			wait();
		}		
	}
	
	public synchronized void openDoors(LiftView view, int fromFloor) throws InterruptedException {
		if((!(toEnter[fromFloor] == 0 && toExit[fromFloor] == 0) || Arrays.stream(toEnter).sum() == 0 && Arrays.stream(toExit).sum()==toExit[fromFloor]) && doorOpened == false) { // Only open doors if someone to enter/exit
			view.openDoors(fromFloor);																							 // == 0 should be enough tbh
			doorOpened = true;
			notifyAll();
		}
	}
	
	public synchronized void closeDoors(LiftView view, int fromFloor) throws InterruptedException {
		if(doorOpened == false) { // if doors not open, skip
			return;
		}
		
		if(passengersNbr >= MAX_PASSENGERS && toExit[fromFloor] == 0) {
			view.closeDoors();
			doorOpened = false;
			return;
		}
		// FIXTHIS, edit: maybe correct now?
		while((toEnter[fromFloor] != 0 && passengersNbr < MAX_PASSENGERS) || toExit[fromFloor] != 0) { //While method so passengers can enter (and doors not close)
			wait();  
		}
		view.closeDoors();
		doorOpened = false;
	}
	
	public synchronized void waitForLift(int fromFloor, Passenger pass) throws InterruptedException { //Waits until you can enter the lift 
		while(floorNbr != fromFloor || doorOpened == false ||  currentlyEntering + 1 > MAX_PASSENGERS - passengersNbr + currentlyExiting ) { // can use moving == true
			wait();
		}
		currentlyEntering++;
		notifyAll();
	}
	
	public synchronized void waitForDestination(int toFloor, Passenger pass) throws InterruptedException {
		while(floorNbr != toFloor || doorOpened == false) { // can use moving == true
			wait();
		}
		currentlyExiting++;
		notifyAll();

	}
	
	public synchronized void notifyExit(int toFloor) {
		passengersNbr--;
		toExit[toFloor]--;
		currentlyExiting--;
		notifyAll();
	}
	
	public synchronized void notifyEntry(int fromFloor) throws InterruptedException {
		passengersNbr++;
		toEnter[fromFloor]--;
		currentlyEntering--;
		notifyAll();
	}

}

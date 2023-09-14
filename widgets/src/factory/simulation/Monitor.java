package factory.simulation;

import factory.model.Conveyor;

public class Monitor {
	private int counter;
	
	public Monitor() {
		this.counter = 0;
	}
	
	public synchronized void turnOff(Conveyor conveyor) {
		conveyor.off();
		this.counter++;
	}
	
	public synchronized void finishedAction() {
		this.counter--;
		notifyAll();
	}
	
	public synchronized void turnOn(Conveyor conveyor) throws InterruptedException {
		while(counter != 0) {
			wait();
		}
		conveyor.on();
	}
}
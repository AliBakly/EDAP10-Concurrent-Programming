package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

public class FactoryController {
    
    public static void main(String[] args) {
        Factory factory = new Factory();

        Conveyor conveyor = factory.getConveyor();
        
        Tool press = factory.getPressTool();
        Tool paint = factory.getPaintTool();
        Monitor monitor = new Monitor();
    	Thread thread1  = new Thread(() -> factorySim(conveyor, press, Widget.GREEN_BLOB, monitor));
    	
    	Thread thread2  = new Thread(() -> factorySim(conveyor, paint, Widget.BLUE_MARBLE, monitor));
    	thread1.start();
    	thread2.start();

    }
    private static void factorySim(Conveyor conveyor, Tool tool, Widget widget, Monitor monitor) {
    	while (true) {
			tool.waitFor(widget);
	        monitor.turnOff(conveyor);;
	        tool.performAction();
	        monitor.finishedAction();
	        try {
				monitor.turnOn(conveyor);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    
    }
}

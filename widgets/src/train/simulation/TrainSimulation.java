package train.simulation;

import java.util.ArrayDeque;
import java.util.Deque;

import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

public class TrainSimulation {

    public static void main(String[] args) {
        TrainView view = new TrainView();
    	Monitor monitor = new Monitor();
        for(int i = 0; i<20; i++) {
        	Thread thread  = new Thread(() -> trainsim(view, monitor));
        	thread.start();
        }
    }
    private static void trainsim(TrainView view, Monitor monitor) {
    	Deque<Segment> queue = new ArrayDeque<Segment>();
        Route route = view.loadRoute();
        for (int i = 0; i < 4; i++) {
        	Segment seg = route.next();
        	queue.addLast(seg);
        	try {
    			monitor.enterSegment(seg);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }

        while(true) {
        	Segment last = route.next();
            try {
    			monitor.enterSegment(last);
    			last.enter();

    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            queue.addFirst(last);
            
            Segment first = queue.pollLast();
            monitor.exitSegment(first);
            
        }
    }

}










/*
Segment first = route.next();
Segment second = route.next();
Segment third = route.next();
queue.addFirst(third);
queue.addFirst(second);
queue.addFirst(first);

try {
	monitor.enterSegment(first);
	monitor.enterSegment(second);
	monitor.enterSegment(third);
} catch (InterruptedException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
*/

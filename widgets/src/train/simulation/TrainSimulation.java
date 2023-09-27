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
        
        //Init
        for (int i = 0; i < 7; i++) {
        	Segment seg = route.next();
        	queue.addLast(seg);
        	try {
    			monitor.enterSegment(seg);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
    			while(queue.size()!=0) {
    				Segment first = queue.pollLast();
                    monitor.exitSegment(first);
    			}
    			return;
    		}
        }

        while(true) {
        	Segment last = route.next();
            try {
    			monitor.enterSegment(last);
    			queue.addFirst(last);
    			last.enter();
                Segment first = queue.pollLast();
                monitor.exitSegment(first);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
    			while(queue.size()!=0) {
    				Segment first = queue.pollLast();
                    monitor.exitSegment(first);
    			}
    			return;
    		}

            
        }
    }

}
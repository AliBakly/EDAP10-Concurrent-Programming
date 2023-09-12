package train.simulation;

import java.util.HashSet;
import java.util.Set;

import train.model.Segment;
public class MonitorTest {
	private final Set<Segment> busy;
	
	public MonitorTest() {
		busy = new HashSet<Segment>();
	}
	
	
	public synchronized void exitSegment(Segment seg) {
        seg.exit();
		busy.remove(seg);
		notifyAll();
	}
	
	public synchronized void enterSegment(Segment seg) throws InterruptedException {
		while (busy.contains(seg)) {
			wait();
		}
		busy.add(seg);
	}

}
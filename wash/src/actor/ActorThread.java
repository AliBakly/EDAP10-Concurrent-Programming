package actor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ActorThread<M> extends Thread {
	private final BlockingQueue<M> queue = new LinkedBlockingQueue<M>();
    /** Called by another thread, to send a message to this thread. 
     * @throws InterruptedException */
    public void send(M message)  {
        // TODO: implement this method (one or a few lines)
    	try {
			queue.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Queue is full."); // Should never happen
		} // should be ok with offer
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
        // TODO: implement this method (one or a few lines)
    	return queue.take();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
        // TODO: implement this method (one or a few lines)
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }
}
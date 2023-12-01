package wash.control;


import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private long counter;
	private int dt = 1;
	private Order order;
	private ActorThread<WashingMessage> t;
	private boolean first;
	private boolean done;
	private boolean filling;

	
    // TODO: add attributes

    public WaterController(WashingIO io) {
        // TODO
    	this.io = io;  	
    }
   // WATER_IDLE, 
   // WATER_FILL,
   // WATER_DRAIN,
    
    @Override
    public void run() {
    	counter = System.currentTimeMillis();
    	try {
	    	while (true) {
	            // wait for up to a (simulated) minute for a WashingMessage
	    		WashingMessage m = receiveWithTimeout(dt / Settings.SPEEDUP);
	            
	            // if m is null, it means a minute passed and no message was received
	            if (m != null) {
	            	t = m.sender();
	                System.out.println("got " + m);
	                order = m.order();
	                	              
	                if (order == Order.WATER_IDLE) {
	                	io.fill(false);
	                	io.drain(false);
	                	WashingMessage ack = new WashingMessage(this, Order.ACKNOWLEDGMENT); 
	                    t.send(ack);
	                    first = false;
	                    done = false;
	                    filling = false;
	                } else if (order == Order.WATER_FILL){
	                	filling = true;
	                	first = true;
	                	io.drain(false);
	                	water_control(order);
	                } else if (order == Order.WATER_DRAIN){
	                	first = true;
	                	water_control(order);
	                }
	                
	                } else if (order == Order.WATER_DRAIN || order == Order.WATER_FILL)  { //If m null
	                	
	                	water_control(order);
	            	
                } 
	            
	    	}
    	} catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
    
    private void water_control(Order order) {
    	long current = (System.currentTimeMillis() - counter)/ Settings.SPEEDUP;
    	
    	if (current >= dt*1000/ Settings.SPEEDUP) {

    		if (order == Order.WATER_FILL) {
    			
    			if (io.getWaterLevel() < 10) {
        			io.fill(true);
        			
        		} else {
        			filling = false;
        			io.fill(false);
        			done = true;
        			
        			if (done && first) {
        				WashingMessage ack = new WashingMessage(this, Order.ACKNOWLEDGMENT); 
                        t.send(ack);
                        first = false;
        			}
        			 
        		}
    		} else { //If drain
    			if(!filling) {  
        			io.drain(true);
    			}
        		if (io.getWaterLevel() <= 0){
        			done = true;
        			
        			if (done && first) {
        				WashingMessage ack = new WashingMessage(this, Order.ACKNOWLEDGMENT); 
                        t.send(ack);
                        first = false;
        			}
        		}
    		}
    	}
    }
}
   
        	   	
        
        
 


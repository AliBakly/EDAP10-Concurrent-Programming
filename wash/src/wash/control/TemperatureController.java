package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;
import static wash.control.WashingMessage.Order.*;


public class TemperatureController extends ActorThread<WashingMessage> {

    // TODO: add attributes
	// dt = 10s for your temperature regulator, Add an extra safety margin (say, 0.2◦C) t
	// mu = dt·0.0478
	// ml = dt·9.52·10−4
	private int dt = 10;
	private Order order;
	private WashingIO io;
	private double mu = dt*0.0478;
	private double ml = dt*0.00952;
	private long counter;
	private ActorThread<WashingMessage> t;
	private boolean heated;
	private boolean first;


	/*
	 *  TEMP_IDLE,
        TEMP_SET_40,
        TEMP_SET_60,
	 */

    public TemperatureController(WashingIO io) {
    	this.io = io;
    	}		

    @Override
    public void run() {
    	counter = System.currentTimeMillis();
    	heated = false;
    	first = false;
    	try {
	    	while (true) {
	            // wait for up to a (simulated) minute for a WashingMessage
	    		WashingMessage m = receiveWithTimeout(dt / Settings.SPEEDUP);
	
	            // if m is null, it means a minute passed and no message was received
	            if (m != null) {
	            	first = true;
	            	t = m.sender();
	                System.out.println("got " + m);
	                order = m.order();
	                
	                if (order == TEMP_IDLE) {
	                	io.heat(false);
	                	WashingMessage ack = new WashingMessage(this, ACKNOWLEDGMENT); 
	                	first = false;
	                	heated = false;
	                    t.send(ack);
	                } else {
	                	if (io.getWaterLevel() > 0) { // SR1
	                		temp_control(order);
	                	}	
	                }
                    
	            } else if (order == TEMP_SET_40 || order == TEMP_SET_60) {
	            	temp_control(order);
                }
	    	}
    	} catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
    
    private void temp_control(Order order) {
    	long current = (System.currentTimeMillis() - counter);

    	if (current >= dt*1000/Settings.SPEEDUP) { //Period of 10 sec
    		counter = System.currentTimeMillis();	

    		if (order == TEMP_SET_40) {
    			if (io.getTemperature() <= 38 + ml) {
        			io.heat(true);
        			
        		} else if (io.getTemperature() >= 40 - mu){
        			io.heat(false);
        			heated = true;
        			
        			if (heated && first) {
        				WashingMessage ack = new WashingMessage(this, ACKNOWLEDGMENT); 
                        t.send(ack);
                        first = false;
        			}
        			
        		}
    		} else {
    			if (io.getTemperature() <= 58 + ml) {
        			io.heat(true);
        			
        		} else if (io.getTemperature() >= 60 - mu){
        			io.heat(false);
        			heated = true;
        			
        			if (heated && first) {
        				WashingMessage ack = new WashingMessage(this, ACKNOWLEDGMENT); 
                        t.send(ack);
                        first = false;

        			}
        		}
    		}	
    	}	
    }
}

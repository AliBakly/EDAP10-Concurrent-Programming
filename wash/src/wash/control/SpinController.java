package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;
import wash.io.WashingIO.Spin;

public class SpinController extends ActorThread<WashingMessage> {
	private WashingIO io;
	
    // TODO: add attributes

    public SpinController(WashingIO io
    		) {
        // TODO
    	this.io = io;
    	
    	
    }

    @Override
    public void run() {
    	 try {
             WashingIO.Spin currentDirection = WashingIO.Spin.IDLE;

             while (true) {
                 // wait for up to a (simulated) minute for a WashingMessage
                 WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);
                 

                 if (m != null) {
                     currentDirection = switch (m.order()) {
                         case SPIN_SLOW -> WashingIO.Spin.LEFT;
                         case SPIN_FAST -> WashingIO.Spin.FAST;
                         case SPIN_OFF -> WashingIO.Spin.IDLE;
                         default -> currentDirection;
                     };

                     io.setSpinMode(currentDirection);
                     m.sender().send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                 } else {
                     currentDirection = switch (currentDirection) { 
                         case LEFT -> WashingIO.Spin.RIGHT;
                         case RIGHT -> WashingIO.Spin.LEFT;
                         default -> currentDirection;
                     };
                     
                     io.setSpinMode(currentDirection);
                 }

             }
         } catch (InterruptedException unexpected) {
             // we don't expect this thread to be interrupted,
             // so throw an error if it happens anyway
             throw new Error(unexpected);
         } 
     
        // this is to demonstrate how to control the barrel spin:
        // io.setSpinMode(Spin.IDLE);
   
    	
    }
}

package wash.control;

import static wash.control.WashingMessage.Order.SPIN_FAST;
import static wash.control.WashingMessage.Order.SPIN_OFF;
import static wash.control.WashingMessage.Order.SPIN_SLOW;
import static wash.control.WashingMessage.Order.TEMP_IDLE;
import static wash.control.WashingMessage.Order.TEMP_SET_40;
import static wash.control.WashingMessage.Order.WATER_DRAIN;
import static wash.control.WashingMessage.Order.WATER_FILL;
import static wash.control.WashingMessage.Order.WATER_IDLE;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class WashingProgram2 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    
    public WashingProgram2(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {
        	// Lock the hatch
            io.lock(true);

            this.sendAndWait(water, WATER_FILL);
            this.sendAndWait(water, WATER_IDLE);

            // Washing
            this.sendAndWait(temp, TEMP_SET_40);
            this.sendAndWait(spin, SPIN_SLOW);
            this.sleepMinutes(20);
            
            sendAndWait(spin, SPIN_OFF);
            sendAndWait(temp, TEMP_IDLE);
            sendAndWait(water, WATER_DRAIN);
            
            
            //Main wash
            
            this.sendAndWait(water, WATER_FILL);
            this.sendAndWait(water, WATER_IDLE);
            
            sendAndWait(temp, Order.TEMP_SET_60);
            sendAndWait(spin, SPIN_SLOW);
            sleepMinutes(30);
            
            sendAndWait(spin, SPIN_OFF);
            sendAndWait(temp, TEMP_IDLE);
            sendAndWait(water, WATER_DRAIN);
            
         // Rinse
            for (int i = 0; i < 5; i++) {
                this.sendAndWait(water, WATER_FILL);
                this.sendAndWait(water, WATER_IDLE);

                this.sleepMinutes(2);

                this.sendAndWait(water, WATER_DRAIN);
                this.sendAndWait(water, WATER_IDLE);
            }

            // Centrifuge
            this.sendAndWait(water, WATER_DRAIN);
            this.sendAndWait(spin, SPIN_FAST);

            this.sleepMinutes(5);

            // Stop
            this.sendAndWait(spin, SPIN_OFF);
            this.sendAndWait(water, WATER_IDLE);
            
            
            
            
            
        	io.lock(false);
            System.out.println("washing program 3 finished");
        } catch (InterruptedException e) {
            
            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, TEMP_IDLE));
            water.send(new WashingMessage(this, WATER_IDLE));
            spin.send(new WashingMessage(this, SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
    
    private void sendAndWait(ActorThread<WashingMessage> actor, Order order) throws InterruptedException {
        System.out.println("setting " + order + "...");
        actor.send(new WashingMessage(this, order));
        WashingMessage ack = receive();
        System.out.println("washing program 3 got " + ack);
    }
    
    private void sleepMinutes(int minutes) throws InterruptedException {
        Thread.sleep(minutes * 60000 / Settings.SPEEDUP);
    }
    
    
}

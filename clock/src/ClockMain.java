import clock.AlarmClockEmulator;
import clock.io.Choice;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();

        out.displayTime(15, 2, 37);   // arbitrary time: just an example
        Monitor monitor = new Monitor(15, 57, 30);
        TimeThread timeThread = new TimeThread(out, monitor);
        timeThread.start();
        
        ConfigurationThread confThread = new ConfigurationThread(out, in, monitor);
        confThread.start();
        
        AlarmThread alarmThread = new AlarmThread(out, monitor);
        alarmThread.start();

    }
}

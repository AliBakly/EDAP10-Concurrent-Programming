import lift.LiftView;

public class GeneralLiftSimulation {
	public static int i =0;
	public static void main(String args[]) {
		LiftView view = new LiftView(10, 10);

		PassengerLiftMonitor monitor = new PassengerLiftMonitor(10, 10);
		LiftThread thread = new LiftThread(monitor, 10, 10, view);
		thread.start();

		for (int i =0; i<700;  i++) {
			PassengerThread thread2 = new PassengerThread(monitor, view);
			thread2.start();
		}
	}
}
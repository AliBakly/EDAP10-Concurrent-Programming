import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import rsa.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    private final ExecutorService threadPool =  Executors.newFixedThreadPool(2);
    private final JProgressBar mainProgressBar;

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();
        w.enableErrorChecks();
        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {

        /*
         * Most Swing operations (such as creating view elements) must be performed in
         * the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
            new Sniffer(codeBreaker).start();
        });
    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
        //System.out.println("message intercepted (N=" + n + ")...");
    	SwingUtilities.invokeLater(() -> {
	        JButton breakButton = new JButton("Break");
	        		
	        WorklistItem workItem = new WorklistItem(n, message);
	        ProgressItem ProgressItem = new ProgressItem(n, message);
	        
	        ProgressTracker tracker = new Tracker(ProgressItem);
	
	        workList.add(workItem);
	        workList.add(breakButton);
	        
	        breakButton.addActionListener(e -> progressList.add(ProgressItem));
	        breakButton.addActionListener(e -> mainProgressBar.setMaximum(1000000 + mainProgressBar.getMaximum())); // increase
	        
	        breakButton.addActionListener(e -> workList.remove(workItem)); // remove from work side
	        breakButton.addActionListener(e -> workList.remove(breakButton));  // remove breakbutton
	    	
	        Runnable task = () -> {
	            try {
	                String plainText = Factorizer.crack(message, n, tracker);
	                
	                SwingUtilities.invokeLater(() -> {
		                ProgressItem.getTextArea().setText(plainText);
	                });
	                
	            } catch (InterruptedException E) {
	
	                System.out.println(E.getMessage());
	            }
	        	};
	        
	        breakButton.addActionListener(e -> {
	        	Future<?> future = threadPool.submit(task);
	        	
	        	JButton cancelButton = new JButton("Cancel"); // create cancel button for progressitem
	        	ProgressItem.add(cancelButton);
	        	
	        	cancelButton.addActionListener(E ->{ // cancel button pressed
	        		future.cancel(true);
	        		tracker.onProgress(1000000 - ProgressItem.getProgressBar().getValue()); // Increase with diff
	        		ProgressItem.remove(cancelButton);
	        		ProgressItem.getTextArea().setText("Cancelled");
	        	});
	        	
	        	ProgressItem.getProgressBar().addChangeListener(e3 ->{ // listens to changes of progressbar
	        		if(ProgressItem.getProgressBar().getValue() == 1000000) { //if cancelled or finished value will be 1000000
	        			ProgressItem.remove(cancelButton);					  //remove cancel button and add remove button
	        			JButton removeButton = new JButton("Remove");
	                    ProgressItem.add(removeButton);
	                    
	                    removeButton.addActionListener(e2 -> {	//remove buttton pressed
	                        progressList.remove(ProgressItem);
	                        mainProgressBar.setValue(mainProgressBar.getValue()-1000000);
	                        mainProgressBar.setMaximum(mainProgressBar.getMaximum()-1000000);
	                    });
	        		}
	        	});
	        });
    	});
    }

    private class Tracker implements ProgressTracker {
        private int totalProgress = 0;
        private ProgressItem item;
        Tracker(ProgressItem item){
        	this.item = item;
        }
        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
        	SwingUtilities.invokeLater(() -> {
        		JProgressBar bar  = item.getProgressBar(); 
        		
                int delta = Math.min(ppmDelta, 1000000 - bar.getValue());
                totalProgress += delta;
	            bar.setMaximum(1000000);
	            bar.setValue(totalProgress);
	            mainProgressBar.setValue(delta + mainProgressBar.getValue());
        	});
        }
    }
}

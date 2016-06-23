package game;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class Job extends CaveElement implements Runnable {

	static Random rn = new Random ();
	JPanel parent;
	Creature worker = null;
	int jobIndex;
	long jobTime;
	String jobName = "";
	JProgressBar pm = new JProgressBar ();
	boolean goFlag = true, noKillFlag = true;
	JButton jbGo   = new JButton ("Stop");
	JButton jbKill = new JButton ("Cancel");
	Status status = Status.SUSPENDED;

	enum Status {RUNNING, SUSPENDED, WAITING, DONE};

	public Job (HashMap <Integer, CaveElement> hc, JPanel cv, Scanner sc) {
		parent = cv;
		sc.next (); // dump first field, j
		jobIndex = sc.nextInt ();
		jobName = sc.next ();
		int target = sc.nextInt ();
		worker = (Creature) hc.get(target);
		jobTime = (int)(sc.nextDouble ());
		pm = new JProgressBar ();
		pm.setStringPainted (true);
		parent.add (pm);
		parent.add (new JLabel (worker.getName(), SwingConstants.CENTER));
		parent.add (new JLabel (jobName    , SwingConstants.CENTER));
		(new Thread (this, worker.getName() + " " + jobName)).start();

		parent.add (jbGo);
		parent.add (jbKill);

		jbGo.addActionListener (e -> toggleGoFlag());
		jbKill.addActionListener (e -> setKillFlag ());

	} // end constructor

	public void toggleGoFlag () {
		goFlag = !goFlag; // ND; should be synced, and notify waiting sync in running loop
	} // end method toggleRunFlag

	public void setKillFlag () {
		noKillFlag = false;
		jbKill.setBackground (Color.red);
	} // end setKillFlag

	void showStatus (Status st) {
		status = st;
		switch (status) {
		case RUNNING:
			jbGo.setBackground (Color.green);
			jbGo.setText ("Running");
			break;
		case SUSPENDED:
			jbGo.setBackground (Color.yellow);
			jbGo.setText ("Suspended");
			break;
		case WAITING:
			jbGo.setBackground (Color.orange);
			jbGo.setText ("Waiting turn");
			break;
		case DONE:
			jbGo.setBackground (Color.red);
			jbGo.setText ("Done");
			break;
		} // end switch on status
	} // end showStatus

	public void run () {
		long time = System.currentTimeMillis();
		long startTime = time;
		long stopTime = time + 1000 * jobTime;
		double duration = stopTime - time;

		synchronized (worker.getParty()) {
			while (worker.busyFlag) {
				showStatus (Status.WAITING);
				try {
					worker.getParty().wait();
				}
				catch (InterruptedException e) {
				} // end try/catch block
			} // end while waiting for worker to be free
			worker.busyFlag = true;
		} // end synchronized on worker

		while (time < stopTime && noKillFlag) {
			try {
				Thread.sleep (100);
			} catch (InterruptedException e) {}
			if (goFlag) {
				showStatus (Status.RUNNING);
				time += 100;
				pm.setValue ((int)(((time - startTime) / duration) * 100));
			} else {
				showStatus (Status.SUSPENDED); // should wait here, not busy loop
			} // end if stepping
		} // end running

		pm.setValue (100);
		showStatus (Status.DONE);
		synchronized (worker.getParty()) {
			worker.busyFlag = false; 
			worker.getParty().notifyAll ();
		}

	} // end method run - implements runnable

	public String toString () {
		String sr = String.format ("j:%7d:%15s:%7d:%5d", jobIndex, jobName, worker.index, jobTime);
		return sr;
	} //end method toString

} // end class Job

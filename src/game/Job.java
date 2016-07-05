/*
 * FILE: 	Job.java
 * DATE:  	06/23/2016
 * AUTHOR: 	Eric Sabelhaus
 * PURPOSE:	Store and manage Job threads, provide GUI elements to manage jobs
 */
package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class Job extends CaveElement implements Runnable {
	// link to JPanel which displays jobs
	JPanel parent;
	// Creature performing work
	Creature worker = null;
	Party workParty = null;
	
	int jobIndex;
	long jobTime;
	String jobName = "";
	
	// display thread progress as it advances
	JProgressBar pm = new JProgressBar ();
	
	// process thread or not
	boolean goFlag = true; 
	// kill job?
    boolean noKillFlag = true;
    
    // JButtons used to suspend, resume, or cancel Job
	JButton jbGo   = new JButton ("Stop");
	JButton jbKill = new JButton ("Cancel");
	
	// arraylist filled with keys and values for artifacts needed
	ArrayList<Resource> resourcesNeeded = new ArrayList<Resource>();
	
	// arraylist to store resources that have been gathered
	ArrayList<Artifact> myResources = new ArrayList<Artifact>();
	
	// preset status to suspended
	Status status = Status.SUSPENDED;

	// enumerate possible Job statuses
	enum Status {RUNNING, SUSPENDED, WAITING, DONE};

	public Job (HashMap <Integer, CaveElement> hc, JPanel cv, Scanner sc) {
		parent = cv;
		sc.next (); // dump first field, j
		jobIndex = sc.nextInt ();
		jobName = sc.next ();
		int target = sc.nextInt ();
		worker = (Creature) hc.get(target);
		workParty = worker.getParty();
		jobTime = (int)(sc.nextDouble ());
		while (sc.hasNext()) {
			// add every resource from this line to the artifact
			resourcesNeeded.add(new Resource(sc.next(), sc.nextInt()));
		}
		pm = new JProgressBar ();
		pm.setStringPainted (true);
		parent.add (pm);
		parent.add (new JLabel (worker.getName(), SwingConstants.CENTER));
		parent.add (new JLabel (jobName    , SwingConstants.CENTER));
		(new Thread (this, worker.getName() + " " + jobName)).start();

		// TODO add components to be populated with resources (have/need)
		
		parent.add (jbGo);
		parent.add (jbKill);

		jbGo.addActionListener (e -> toggleGoFlag());
		jbKill.addActionListener (e -> setKillFlag ());

	} // end constructor

	
	private void getResources() {
		
	}
	
	// TODO create method to get resources
	private void getResource(Resource myResource) throws InterruptedException {
		for (Artifact myArtifact: workParty.getKnownResourceByType(myResource.getName())){
			if (!(myArtifact.isLocked())) {
				myArtifact.lock();
				myResource.addedOne();
				myResources.add(myArtifact);
				return;
			}
		}
	}
	
	// TODO create method for "have all resources by type"
	private boolean haveAllResources() {
		Boolean haveAll = true;
		for (Resource myResource: resourcesNeeded) {
			if (!(myResource.haveAllResources())) {
				haveAll = false;
			}
		}
		return haveAll;
	}
	
	// TODO create method to release resources
	private void releaseResources() {
		for (Artifact myArtifact: myResources) {
			myArtifact.unlock();
		}
	}
	
	// set to either waiting or running
	public void toggleGoFlag () {
		goFlag = !goFlag; // ND; should be synced, and notify waiting sync in running loop
	} // end method toggleRunFlag

	// cancel Job
	public void setKillFlag () {
		noKillFlag = false;
		jbKill.setBackground (Color.red);
	} // end setKillFlag

	// display status of Job
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

	// run Job
	// TODO rewrite to only become busy once resources gathered, and otherwise release them
	public void run () {
		long time = System.currentTimeMillis();
		long startTime = time;
		long stopTime = time + 1000 * jobTime;
		double duration = stopTime - time;

		// perform synchronized work processing on Job
		synchronized (worker.getParty()) {
			while (!haveAllResources()) {
				
			}
			
			while (worker.busyFlag) {
				showStatus (Status.WAITING);
				try {
					worker.getParty().wait();
				}
				catch (InterruptedException e) {
					// TODO handle exception
				} // end try/catch block
			} // end while waiting for worker to be free
			worker.busyFlag = true;
		} // end synchronized on worker

		// wait until job can run
		while (time < stopTime && noKillFlag) {
			try {
				Thread.sleep (100);
			} catch (InterruptedException e) {
				// TODO handle exception
			}
			if (goFlag) {
				showStatus (Status.RUNNING);
				time += 100;
				pm.setValue ((int)(((time - startTime) / duration) * 100));
			} else {
				showStatus (Status.SUSPENDED); // should wait here, not busy loop
			} // end if stepping
		} // end running

		// complete job
		pm.setValue (100);
		showStatus (Status.DONE);
		// notify worker of available thread to perform next Job
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

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
import javax.swing.JTextPane;
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
	JTextPane whatINeed = new JTextPane();
	JTextPane whatIGot = new JTextPane();
	
	// arraylist filled with keys and values for artifacts needed
	ArrayList<Resource> resourcesNeeded = new ArrayList<Resource>();
	
	private String resourcesNeededText = "Resources Needed: ";
	private String myResourcesText = "Resources Acquired: ";
	
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
			Resource myResource = new Resource(sc.next(), sc.nextInt());
			// add every resource from this line to the artifact
			resourcesNeeded.add(myResource);
			resourcesNeededText += myResource.toString();
		}
		pm = new JProgressBar ();
		pm.setStringPainted (true);
		parent.add (pm);
		parent.add (new JLabel (worker.getName(), SwingConstants.CENTER));
		parent.add (new JLabel (jobName    , SwingConstants.CENTER));
		(new Thread (this, worker.getName() + " " + jobName)).start();

		whatINeed.setText(resourcesNeededText);
		
		parent.add(whatINeed);
		parent.add(whatIGot);
		parent.add(jbGo);
		parent.add(jbKill);

		jbGo.addActionListener (e -> toggleGoFlag());
		jbKill.addActionListener (e -> setKillFlag ());

	} // end constructor
	
	private void rewriteResourceDetails() {
		resourcesNeededText = "Resources Needed: ";
		for (Resource resource: resourcesNeeded) {
			if (!(resource.haveAllResources())) {
				resourcesNeededText += resource.toString();
			}
		}
		whatINeed.setText(resourcesNeededText);
		parent.validate();
	}
	
	private void getResources() {
		for(Resource resource: resourcesNeeded) {
			try {
				getResource(resource);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!haveAllResources()) {
			releaseResources();
		}
	}
	
	// this method allocates one resource to this job
	private void getResource(Resource myResource) throws InterruptedException {
		for (Artifact myArtifact: workParty.getKnownResourceByType(myResource.getName())){
			if (!(myArtifact.isLocked())) {
				myArtifact.lock();
				String thisResource = myArtifact.getName().toString() + " : " + myArtifact.getType().toString() + " ";
				myResourcesText += thisResource;
				myResource.addedOne();
				myResources.add(myArtifact);
				whatIGot.setText(myResourcesText);
				rewriteResourceDetails();
				parent.validate();
				return;
			}
		}
	}
	
	// checks to see whether all of the resources needed are gathered
	private boolean haveAllResources() {
		Boolean haveAll = true;
		for (Resource myResource: resourcesNeeded) {
			if (!(myResource.haveAllResources())) {
				haveAll = false;
			}
		}
		return haveAll;
	}
	
	// if this job cannot gather all of its resources, it should release
	// the ones it has to prevent deadlock, and allow other jobs to progress
	private void releaseResources() {
		myResourcesText = "Resources Acquired: ";
		for (Artifact myArtifact: myResources) {
			myArtifact.unlock();
		}
		whatIGot.setText(myResourcesText);
		parent.validate();
	}
	
	// set to either waiting or running
	public void toggleGoFlag () {
		goFlag = !goFlag; // ND; should be synced, and notify waiting sync in running loop
	} // end method toggleRunFlag

	// cancel Job
	public void setKillFlag () {
		noKillFlag = false;
		jbKill.setBackground (Color.red);
		releaseResources();
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
		// only perform once all resources are gathered
		synchronized (worker.getParty()) {
			while (!(haveAllResources()) && worker.busyFlag) {
				showStatus (Status.WAITING);
				try {
					worker.getParty().wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				} // end try/catch block
				getResources();
			} // end while waiting for worker to be free
			worker.busyFlag = true;
		} // end synchronized on worker

		// wait until job can run
		while (time < stopTime && noKillFlag) {
			try {
				Thread.sleep (100);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		releaseResources();
		showStatus (Status.DONE);
		// notify worker of available thread to perform next Job
		synchronized (worker.getParty()) {
			worker.busyFlag = false; 
			worker.getParty().notifyAll();
		}

	} // end method run - implements runnable

	public String toString () {
		String sr = String.format ("j:%7d:%15s:%7d:%5d", jobIndex, jobName, worker.index, jobTime);
		return sr;
	} //end method toString

} // end class Job

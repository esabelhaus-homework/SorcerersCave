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

	int jobIndex;
	long jobTime;
	String jobName = "";

	// display thread progress as it advances
	JProgressBar pm = new JProgressBar ();

	// process thread or not
	boolean goFlag = true; 
	// kill job?
	boolean noKillFlag = true;

	// have all resources?
	boolean haveAll = false;

	// JButtons used to suspend, resume, or cancel Job
	JButton jbGo   = new JButton ("Stop");
	JButton jbKill = new JButton ("Cancel");
	JTextPane whatINeed = new JTextPane();
	JTextPane whatIGot = new JTextPane();

	// hashmap filled with keys and values for artifacts needed
	HashMap<String,Integer> resourcesNeeded = new HashMap<String,Integer>();
	
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
		jobTime = (int)(sc.nextDouble ());
		while (sc.hasNext()) {
			String type = sc.next(); 
			switch (type) {
				case "Stone"   : break;
				case "Weapons" : type = "Weapon"; break;
				case "Potions" : type = "Potion"; break;
				case "Wands"   : type = "Wand"; break;
			}
			resourcesNeeded.put(type, sc.nextInt());
		}

		pm = new JProgressBar ();
		pm.setStringPainted (true);
		parent.add (pm);
		parent.add (new JLabel (worker.getName(), SwingConstants.CENTER));
		parent.add (new JLabel (jobName    , SwingConstants.CENTER));
		setResourceDisplay();
		parent.add(whatINeed);
		parent.add(whatIGot);
		parent.add(jbGo);
		parent.add(jbKill);

		(new Thread (this, worker.getName() + " " + jobName)).start();

		jbGo.addActionListener (e -> toggleGoFlag());
		jbKill.addActionListener (e -> setKillFlag ());

	} // end constructor


	public void setResourceDisplay() {
		String resourcesNeededText = "Resources Needed: ";
		String myResourcesText = "Resources Acquired: ";

		for(HashMap.Entry<String, Integer> entry : resourcesNeeded.entrySet()) { 
		    resourcesNeededText += "\n" + entry.getKey() + " : " + entry.getValue();
		}
		whatINeed.setText(resourcesNeededText);
		whatIGot.setText(myResourcesText);
		parent.validate();
	}
	
	public synchronized void updateResourceDisplay(ArrayList<Artifact> resources) {
		String resourcesNeededText = "All Resources Acquired";
		whatINeed.setText(resourcesNeededText);
		String myResourcesText = "Acquired:\n";
		for (Artifact artifact: resources) {
			myResourcesText += artifact.getName() + "\n";
		}
		whatIGot.setText(myResourcesText);
		parent.validate();
	}

	public synchronized boolean partyHasResources() {	
        boolean result = true;
        for (HashMap.Entry<String, Integer> entry : resourcesNeeded.entrySet()) {    
            if (worker.getParty().resourceAvailableCountByType(entry.getKey()) - entry.getValue() < 0) {
                result = false;
            }
        }
        return result;
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
	public void run () {
		ArrayList<Artifact> runtimeResources = new ArrayList<Artifact>();
		long time = System.currentTimeMillis();
		long startTime = time;
		long stopTime = time + 1000 * jobTime;
		double duration = stopTime - time;

		// perform synchronized work processing on Job
		// only perform once all resources are gathered
		synchronized (worker.getParty()) {
			while (worker.busyFlag || !partyHasResources()) {
				showStatus (Status.WAITING);
				try {
					worker.getParty().wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				} // end try/catch block
			} // end while waiting for worker to be free
			worker.busyFlag = true;
			// reserve resources, return resources reserved as string
			
			runtimeResources = worker.getParty().reserveResources(resourcesNeeded);
			updateResourceDisplay(runtimeResources);
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
		showStatus (Status.DONE);
		// notify worker of available thread to perform next Job
		synchronized (worker.getParty()) {
			worker.getParty().releaseResources(resourcesNeeded, runtimeResources);
			worker.busyFlag = false; 
			worker.getParty().notifyAll();
		}
		whatIGot.setText("Job Completed, resources dropped!");
	} // end method run - implements runnable

	public String toString () {
		String sr = String.format ("j:%7d:%15s:%7d:%5d", jobIndex, jobName, worker.index, jobTime);
		return sr;
	} //end method toString

} // end class Job

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
		jobTime = (int)(sc.nextDouble ());
		while (sc.hasNext()) {
			String type = sc.next();
			int howMany = sc.nextInt();
			
			String parsedType = "";
			
			// since it is stored as plural, I parse the plurality since it is handled
			// using the number required
			switch (type) {
				case "Wands":
					parsedType = "Wand"; break;
				case "Weapons":
					parsedType = "Weapon"; break;
				case "Potions":
					parsedType = "Potion"; break;
				case "Stone":
					parsedType = type;
			}
			
			Resource myResource = new Resource(parsedType, howMany);
			// add every resource from this line to the artifact
			resourcesNeeded.add(myResource);
			resourcesNeededText += "\n" + myResource.toString();
		}
		
		pm = new JProgressBar ();
		pm.setStringPainted (true);
		parent.add (pm);
		JLabel workerLabel = new JLabel (worker.getName(), SwingConstants.CENTER);
		JLabel jobLabel = new JLabel (jobName    , SwingConstants.CENTER);
		parent.add (workerLabel);
		parent.add (jobLabel);
		(new Thread (this, worker.getName() + " " + jobName)).start();
		
		
		whatINeed.setText(resourcesNeededText);
		whatIGot.setText(myResourcesText);
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
				resourcesNeededText += "\n" + resource.toString();
			}
		}
		whatINeed.setText(resourcesNeededText);
		whatIGot.setText(myResourcesText);
		parent.validate();
	}
	
	private void getResources() throws InterruptedException {
		for(Resource resource: resourcesNeeded) {
			try {
				int counter = 0;
				while(counter != resource.howManyNeeded() - 1) {
					System.out.println("get this type of resource: " + resource);
					getResource(resource);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				releaseResources();
			}
		}
	}
	
	// this method allocates one resource to this job if available
	private void getResource(Resource myResource) throws InterruptedException {
		// iterate over all resources by type
		Artifact resourceNeeded = null; 
		for (Artifact resourceArtifact: worker.getParty().getKnownResourceByType(myResource.getType())){
			// if one is available, increment the count contained by the creature
			if (resourceArtifact.state()) {
				resourceNeeded = resourceArtifact;
			}
			if (resourceNeeded != null) {
				synchronized (worker.getParty()) {
					resourceNeeded.acquire();	
				}
				myResource.addedOne();
				String thisResource = "\n" + resourceNeeded.getName() + " : " + resourceNeeded.getType() + " ";
				myResourcesText += thisResource;
				myResources.add(resourceNeeded);
				rewriteResourceDetails();
			} else {
				System.out.println("No " + myResource.getType() + "s available");
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
			synchronized (worker.getParty()) {
				myArtifact.release();
			}
			System.out.println("is it actually available: " + myArtifact.state());
		}
		myResources.clear();
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
					getResources();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				} // end try/catch block
			} // end while waiting for worker to be free
			worker.busyFlag = true;
		} // end synchronized on worker

		resourcesNeeded.clear();
				
		resourcesNeededText = "All resources aquired";
		
		whatINeed.setText(resourcesNeededText);
		whatIGot.setText(myResourcesText);
		
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
			worker.busyFlag = false; 
			worker.getParty().notifyAll();
		}
		releaseResources();
		whatIGot.setText("Job Completed, resources dropped!");
	} // end method run - implements runnable

	public String toString () {
		String sr = String.format ("j:%7d:%15s:%7d:%5d", jobIndex, jobName, worker.index, jobTime);
		return sr;
	} //end method toString

} // end class Job

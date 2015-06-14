package distributor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import run.*;
import event.*;

public class SingleQueueDistributor extends EventDistributor {	
		
	public SingleQueueDistributor (AtomicInteger dp, HashMap<Double,Long> distrProgrPerSec, String f, HashMap<RunID,Run> rs, RunQueues rq, AtomicInteger x1, AtomicInteger x2, int last, long start) {
		super(dp, distrProgrPerSec, f, rs, rq, x1, x2, last, start);		 
	}

	/** 
	 * Read the input file, parse the events, 
	 * generate new runs if they do not exist yet and
	 * distribute events into run task queues.
	 */	
	public void run() {	
		
		Scanner scanner;
		try {
			// Input file
			scanner = new Scanner(new File(filename));
			
			// Time
			double prev_sec = -1;
			Random random = new Random();
			int min = 6;
			int max = 14;
			
			double batch_limit = 1; //random.nextInt(max - min + 1) + min;
 			if (batch_limit > lastSec) batch_limit = lastSec;
 			
 			long curr_ms = System.currentTimeMillis() - startOfSimulation;
 		
 			// Sleep if curr_ms is smaller than batch_limit ms		 		
 			if (curr_ms < batch_limit*1000) {
 			
 				int sleep_time = new Double(batch_limit*1000 - curr_ms).intValue();
 			
 				System.out.println("Driver sleeps " + sleep_time + " ms");
 			
 				Thread.sleep(sleep_time);
 			} 			
																
			// First event
			String line = scanner.nextLine();
	 		PositionReport event = PositionReport.parse(line);	
	 								
			while (true) {
				
				// Put events within the current batch into the run queue 		
		 		while (event != null && event.sec <= batch_limit) {
		 			
		 			if (event.correctPositionReport()) {	 				
						
						/*** Create run if it does not exist yet ***/
						RunID runid = new RunID (event.xway, event.dir, event.seg); 
						      		
						if (!runs.containsKey(runid)) {
							
							AtomicInteger firstHPseg = (runid.dir == 0) ? xway0dir0firstHPseg : xway0dir1firstHPseg;
							Run run = new Run(runid, event.sec, event.min, firstHPseg);
							runs.put(runid, run);
							
							//System.out.println("Run " + runid.toString() + " is created.");
						}  			 	
						/*** Put the event into the run queue ***/
						event.distributorTime = (System.currentTimeMillis() - startOfSimulation)/1000;
						
						LinkedBlockingQueue<PositionReport> runtaskqueue = runqueues.contents.get(runid);
						if (runtaskqueue == null) {    
							runtaskqueue = new LinkedBlockingQueue<PositionReport>();
							runqueues.contents.put(runid, runtaskqueue);		 				
						}
						runtaskqueue.add(event);
						
						System.out.println(event.toString());
					}		 			
		 			// Update distributer progress
		 			if (event.sec > prev_sec) {
		 				
		 				curr_ms = System.currentTimeMillis() - startOfSimulation;
		 				distributorProgressPerSec.put(prev_sec, curr_ms);		 		
		 				runqueues.setDistributorProgress(prev_sec);		 				
		 				System.out.println("Distr progr:" + prev_sec + " Distr ms: " + curr_ms);
		 				
		 				prev_sec = event.sec;
		 			}		 		
			 		// Reset event
		 			if (scanner.hasNextLine()) {		 				
		 				line = scanner.nextLine();   
		 				event = PositionReport.parse(line);		 				
		 			} else {
		 				event = null;		 				
		 			}
		 		}
		 		// Update distributer progress
		 		curr_ms = System.currentTimeMillis() - startOfSimulation;
 				distributorProgressPerSec.put(batch_limit, curr_ms);		 		
 				runqueues.setDistributorProgress(batch_limit); 				
 				System.out.println("Distr progr:" + batch_limit + " Distr ms: " + curr_ms); 				
		 		
		 		if (batch_limit == lastSec) {
		 			break;
		 		} else {
		 		
		 			// Rest prev_sec and batch_limit
		 			prev_sec = event.sec;
		 			
		 			batch_limit += 1; //random.nextInt(max - min + 1) + min;		 			
		 			if (batch_limit > lastSec) batch_limit = lastSec;
		 		
		 			// Sleep if curr_ms is smaller than batch_limit ms		 		
		 			if (curr_ms < batch_limit*1000) {
		 			
		 				int sleep_time = new Double(batch_limit*1000 - curr_ms).intValue();
		 			
		 				System.out.println("Driver sleeps " + sleep_time + " ms");
		 			
		 				Thread.sleep(sleep_time);
		 			}		 			
		 		}
		 	}			
			/*** Clean-up ***/		
			scanner.close();				
			System.out.println("Distributor is done.");
		}
		catch (InterruptedException e) { e.printStackTrace(); }
		catch (FileNotFoundException e1) { e1.printStackTrace(); }				 						 
	}
}

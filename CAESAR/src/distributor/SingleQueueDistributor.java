package distributor;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import run.*;
import event.*;

public class SingleQueueDistributor extends EventDistributor {
		
	public SingleQueueDistributor (AtomicInteger dp, EventQueue e, HashMap<RunID,Run> rs, RunQueues rq, AtomicInteger x1, AtomicInteger x2) {
		super(dp, e, rs, rq, x1, x2);
	}

	/** 
	 * Read the input file, parse the events, 
	 * generate new runs if they do not exist yet and
	 * distribute events into run task queues.
	 */	
	public void run() {	
		
		// Local variables
		Double curr_sec = new Double(-1);
		int event_count = 0;
						
		while (!shutdown) {				
			while (events.getDriverProgress(curr_sec)) {
					
				/**************************************** Event ****************************************/
				PositionReport event = events.contents.peek();					
				if (event!=null) {						
					events.contents.poll();	   			 	
					if (event.type == 0) {
					
						/******************************************* Run *******************************************/
						RunID runid = new RunID (event.xway, event.dir, event.seg); 
						Run run;        		
						if (runs.containsKey(runid)) {
							run = runs.get(runid);             			          			
						} else {
							AtomicInteger firstHPseg = (runid.dir == 0) ? xway0dir0firstHPseg : xway0dir1firstHPseg;
							run = new Run(runid, event.sec, event.min, firstHPseg);
							runs.put(runid, run);
						}  			 	
						/************************************* Run task queues *************************************/
						LinkedBlockingQueue<PositionReport> runtaskqueue = runqueues.contents.get(runid);
						if (runtaskqueue == null) {    
							runtaskqueue = new LinkedBlockingQueue<PositionReport>();
							runqueues.contents.put(runid, runtaskqueue);		 				
						}
						runtaskqueue.add(event);	 			
				
						// Max number of stored events per run
						int size = runtaskqueue.size();
						if (run.output.maxNumberOfStoredEvents < size) run.output.maxNumberOfStoredEvents = size;	
					
						if (event.sec > curr_sec) {	
							
							System.out.println(event.sec);
								
							// Set driver progress
							runqueues.setDistributorProgress(curr_sec);
		 					 				
							// Min and max stream rate
							if (curr_sec >= 0) {
								if (min_stream_rate > event_count) min_stream_rate = event_count;
								if (max_stream_rate < event_count) max_stream_rate = event_count;
							}		 				
							curr_sec++;
							event_count = 1;
									 				
						} else { 
							event_count++;							
						}	 			
					}
				} else {
					curr_sec++;
			}}
			// Set driver progress to the time stamp of the last event in the batch
			runqueues.setDistributorProgress(curr_sec);						
		 	
			// Min and max stream rate
			if (min_stream_rate > event_count) min_stream_rate = event_count;
			if (max_stream_rate < event_count) max_stream_rate = event_count;				
		}				
		System.out.println("Distributor is done.");		 						 
	}
}

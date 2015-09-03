package transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import run.*;
import event.*;


/** 
 * A transaction has a sequence of events with the same time stamp and belonging to the same run,
 * a set of all runs, start of simulation and distributor progress per second. 
 * @author Olga Poppe
 */
public abstract class Transaction implements Runnable {
	
	ArrayList<PositionReport> events;		
	HashMap<RunID,Run> runs;
	
	long startOfSimulation;
	AtomicLong total_exe_time;
	
	public CountDownLatch transaction_number;
				
	public Transaction (ArrayList<PositionReport> eventList, HashMap<RunID,Run> rs, long start, AtomicLong tet) {
		
		events = eventList;		
		runs = rs;
		startOfSimulation = start;
		total_exe_time = tet;		
	}	
}

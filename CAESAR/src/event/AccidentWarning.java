package event;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * In addition to type, time stamp and vehicle identifier, 
 * an accident warning has an emission time stamp and a segment. 
 * @author Olga Poppe
 */
public class AccidentWarning extends Event {
	
	public double emit;
	double seg;
	
	public double totalProcessingTime;
	
	/**
	 * Construct accident warning when there is an accident on a road.
	 * @param p					position report
	 * @param s					segment with accident ahead
	 * @param delay				scheduler wake-up time after waiting for distributor
	 * @param startOfSimulation	start of simulation to generate the emission time
	 * @param awf				indicates whether accident warning failed already
	 */
	public AccidentWarning (PositionReport p, double s, 
			HashMap<Double,Double> distrFinishTimes, HashMap<Double,Double> schedStartTimes, long startOfSimulation, AtomicBoolean awf) {
		
		super(1,p.sec,p.vid);	
		
		emit = (System.currentTimeMillis() - startOfSimulation)/new Double(1000);
		seg = s;
		
		double delay = 0;
		if (schedStartTimes.containsKey(p.sec) && distrFinishTimes.containsKey(p.sec)) {
			delay = schedStartTimes.get(p.sec) - distrFinishTimes.get(p.sec);
			//if (delay>1) System.out.println("Scheduler wait time for distributor at second " + p.sec + " is " + delay + " seconds too long.");
		} else {
			System.out.println("For the second " + p.sec + 
					" distribution time is missing: " + !distrFinishTimes.containsKey(p.sec) +
					" and scheduling time is missing: " + !schedStartTimes.containsKey(p.sec));
		}	
		totalProcessingTime =  emit - p.distributorTime - delay;
				
		printError (p, totalProcessingTime, awf, "ACCIDENT WARNINGS");			
	}	

	/** 
	 * Print this accident warning.
	 */
	public String toString () {
		return new Double(type).intValue() + ","
				+ new Double(sec).intValue() + ","
				+ emit + "," 
				+ new Double(vid).intValue() + ","
				+ new Double(seg).intValue() + "\n";			
	}
}

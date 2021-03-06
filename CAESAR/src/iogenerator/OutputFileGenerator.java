package iogenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import run.*;
import window.*;

/**
 * Output file generator generates files for validation of the output and performance charts.
 * @author Olga Poppe
 */
public class OutputFileGenerator {
	
	/**
	 * Generates output files 
	 * @param runs
	 * @param HP_frequency
	 * @param LP_frequency
	 */
	public static void write2File (HashMap<RunID,Run> runs, double lastSec, boolean count_and_rate, 
			int max_xway, boolean both_dirs, 
			int center, int lambda, int window_distribution, int window_length, int window_number, ArrayList<TimeInterval> expensive_windows,
			int query_number, AtomicInteger total_exe_time) { 
		
		try {
			/*
			long total_garbageCollectionTime = 0;
			long total_priorityMaintenanceTime = 0;*/
			
			/*** Output files for validation ***/
			String output = "../../output/";
			String counts = "../../output/counts/";
			String rates = "../../output/rates/";
			
			// Total event counts
			File pr_counts_file = new File(counts + "pr_counts.dat");
			BufferedWriter pr_counts_output = new BufferedWriter(new FileWriter(pr_counts_file));
			
			File max_num_stored_events_file = new File(counts + "max_num_stored_events.dat");
			BufferedWriter max_num_stored_events_output = new BufferedWriter(new FileWriter(max_num_stored_events_file));		
			
			File rtn_counts_file = new File(counts + "rtn_counts.dat");
			BufferedWriter rtn_counts_output = new BufferedWriter(new FileWriter(rtn_counts_file));
			
			File ztn_counts_file = new File(counts + "ztn_counts.dat");
			BufferedWriter ztn_counts_output = new BufferedWriter(new FileWriter(ztn_counts_file));
			
			File aw_counts_file = new File(counts + "aw_counts.dat");
			BufferedWriter aw_counts_output = new BufferedWriter(new FileWriter(aw_counts_file));
			
			// Event rates
			File pr_rates_file = new File(rates + "pr_rates.dat");
			BufferedWriter pr_rates_output = new BufferedWriter(new FileWriter(pr_rates_file));
			
			File rtn_rates_file = new File(rates + "rtn_rates.dat");
			BufferedWriter rtn_rates_output = new BufferedWriter(new FileWriter(rtn_rates_file));
			
			File ztn_rates_file = new File(rates + "ztn_rates.dat");
			BufferedWriter ztn_rates_output = new BufferedWriter(new FileWriter(ztn_rates_file));
			
			File aw_rates_file = new File(rates + "aw_rates.dat");
			BufferedWriter aw_rates_output = new BufferedWriter(new FileWriter(aw_rates_file));
			
			// Complex events
			File tollalerts_file = new File(output + "tollalerts.dat");
			BufferedWriter tollalerts_output = new BufferedWriter(new FileWriter(tollalerts_file));		

			File accidentalerts_file = new File(output + "accidentalerts.dat");
			BufferedWriter accidentalerts_output = new BufferedWriter(new FileWriter(accidentalerts_file)); 

			/*** Output files for experiments ***/
			File results_file = new File(output + "results.dat");
			BufferedWriter results_output = new BufferedWriter(new FileWriter(results_file,true));
			
			/*File eventstorage_file = new File("../../eventstorage.dat");
			BufferedWriter eventstorage_output = new BufferedWriter(new FileWriter(eventstorage_file));

			File eventProcessingTimes_file = new File("../../eventprocessingtimes.dat");
			BufferedWriter eventProcessingTimes_output = new BufferedWriter(new FileWriter(eventProcessingTimes_file));

			File accidentProcessingTimes_file = new File("../../accidentprocessingtimes.dat");
			BufferedWriter accidentProcessingTimes_output = new BufferedWriter(new FileWriter(accidentProcessingTimes_file));  
			
			File times_file = new File("../../times.dat");
			BufferedWriter times_output = new BufferedWriter(new FileWriter(times_file));*/
			
			// Events processed and stored by runs
			double max_latency = 0;
			double sum = 0;
			double count = 0;
			
			int real_size = 0;
			int fake_size = 0;
			int real_complex_event_number = 0;
			int fake_complex_event_number = 0;
			
			Set<RunID> runids = runs.keySet();
				
			for (RunID runid : runids) {
	     		
				Run run = runs.get(runid);		
				int seg = new Double(runid.seg).intValue();
				int lastMin =  new Double(Math.floor(lastSec/60) + 1).intValue();
					
				if (count_and_rate) {
					if (runid.xway == 0 && runid.dir == 0) 
						run.output.writeEventCounts2File(seg, pr_counts_output, max_num_stored_events_output, rtn_counts_output, ztn_counts_output, aw_counts_output);	
					if (runid.xway == 0 && runid.dir == 1 && runid.seg == 85) 
						run.output.writeStreamRates2File(pr_rates_output, rtn_rates_output, ztn_rates_output, aw_rates_output, lastMin);
				}
				max_latency = run.output.writeTollNotifications2File(tollalerts_output, max_latency);
				max_latency = run.output.writeAccidentWarnings2File(accidentalerts_output, max_latency);	
				
				sum += run.output.sum;
				count += run.output.count;
				
				real_size += run.getRealSize();
				fake_size += run.getFakeSize();
				real_complex_event_number += run.output.getSize();
				fake_complex_event_number += run.fake_output.getSize();
	     		
				/*run.write2FileEventStorage(eventstorage_output);
	     		run.output.write2FileEventProcessingTimes(eventProcessingTimes_output);
	     		run.write2FileAccidentProcessingTimes(accidentProcessingTimes_output);
	     		
	     		total_garbageCollectionTime += run.time.garbageCollectionTime;
	     		total_priorityMaintenanceTime += run.time.priorityMaintenanceTime;*/
	     	}
			double avg_latency = new Double(sum)/new Double(count);
			
			String start = 	"\nMax xway: " + max_xway + 
							"\nLast xway is two-directional: " + both_dirs +
							"\nCenter: " + center +
							"\nLambda: " + lambda +	
							"\nWindow distribution: " + window_distribution +
							"\nWindow length: " + window_length + 
							"\nWindow number: " + window_number + 
							"\nExpensive windows: " + expensive_windows.toString() +
							"\nQuery replications: " + query_number;							
							
			String end =	//"\nTotal execution time in seconds: " + (total_exe_time.get()/new Double(1000)) +
							"\nAvg latency: " + avg_latency +
							"\n--- Max latency: " + max_latency + " ---" +						
							"\nReal size: " + real_size +
							"\nReal complex event number: " + real_complex_event_number +
							"\nFake size: " + fake_size +
							"\nFake complex event number: " + fake_complex_event_number;
			results_output.write(start);
			results_output.write(end);
			System.out.println(end);
			
	        // Number of runs, total processing time, scheduling overhead, garbage collection overhead, priority maintenance overhead
	       /* String line = 	min_stream_rate + " " + max_stream_rate + " " + runs.size() + " " + 
	        				total_time + " " + total_garbageCollectionTime + " " + total_priorityMaintenanceTime + " " +
	        				HP_frequency + " " + LP_frequency + "\n";
	        times_output.write(line);*/
		
	        /*** Clean-up ***/
			pr_counts_output.close();
			max_num_stored_events_output.close();
			rtn_counts_output.close();
			ztn_counts_output.close();
			aw_counts_output.close();
			
			pr_rates_output.close();
			rtn_rates_output.close();
			ztn_rates_output.close();
			aw_rates_output.close();
			
	       	tollalerts_output.close();
	       	accidentalerts_output.close();
	       	results_output.close();
	       	/*eventstorage_output.close();
	       	eventProcessingTimes_output.close();
	       	accidentProcessingTimes_output.close();
	       	times_output.close();*/
	       	
		} catch (IOException e) { e.printStackTrace(); }
	}
}

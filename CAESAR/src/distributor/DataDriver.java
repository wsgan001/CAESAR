package distributor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import event.PositionReport;

public class DataDriver implements Runnable {
	
	String filename;
	EventQueue events;
		
	public DataDriver(String f, EventQueue e) {
		
		filename = f;
		events = e;
	}
	
	public void run() {
		
		Scanner scanner;
		try {
			// Input file
			scanner = new Scanner(new File(filename));
			
			// Current second
			int curr_sec = -1;
			ArrayList<PositionReport> batch = new ArrayList<PositionReport>();
			
			// Output file
			//File input_file = new File("../../input_till_sec_10784.dat");
			//BufferedWriter input = new BufferedWriter(new FileWriter(input_file));			
						
			// First event
			String line = scanner.nextLine();
	 		PositionReport event = PositionReport.parse(line);		
			
			while (scanner.hasNextLine()) {
				
				/*************************************** Event number ***************************************/
				Random random = new Random();
				int min = 6;
				int max = 14;	
				int number = random.nextInt(max - min + 1) + min;
				
				// Arrival time
				curr_sec += number;	
				batch.clear();
				
				/****************************************** Event batch *******************************************/		 		
		 		while (scanner.hasNextLine() && event.sec <= curr_sec) {
		 			
		 			// Write the event to the output file and append its arrival time
		 			event.arrivalTime = curr_sec;
		 			batch.add(event);
		 			
		 			// Reset event
		 			line = scanner.nextLine();   
			 		event = PositionReport.parse(line);	
		 		}	
		 		events.put(batch);
		 				 		
		 		System.out.println("-----------------------\nDriver: " + curr_sec + " " + batch.size());
		 		
				Thread.sleep(number * 1000);
			}			
			/*** Clean-up ***/		
			scanner.close();	
			
			System.out.println("driver done");
		}
		catch (InterruptedException e) { e.printStackTrace(); }
		catch (FileNotFoundException e1) { e1.printStackTrace(); }		
	}
}

package iogenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import event.*;

/***
 * Input file generator parses the input file and copies certain tuples to the output file.  
 * @author Olga Poppe
 */
public class InputFileGenerator {
	
	public static void main (String[] args) {
		
	}
	
	/****************************************************************************
	 * Select correct position reports and change xway
	 * @param inputfilename
	 * @param outputfilename
	 * @param xway
	 */
	public static void cleanFile (String inputfilename, String outputfilename, int xway) {
		
		Scanner input = null;
		try {		
			/*** Input file ***/
			File input_file = new File(inputfilename);
			input = new Scanner(input_file);  			
								
			/*** Output file ***/
            File output_file = new File(outputfilename);
            BufferedWriter output = new BufferedWriter(new FileWriter(output_file));            
              
            /*** Call method ***/            
            changeXway(input,output,xway);
            
            /*** Close the files ***/
       		input.close();
       		output.close();        		
        
		} catch (IOException e) { System.err.println(e); }		  
	}
	
	/***
	 * Copy position reports from input to output and change their xway to the given value 
	 * @param input
	 * @param output
	 * @param new xway
	 */
	public static void changeXway (Scanner input, BufferedWriter output, int newXway) {
		
		String eventString = "";
		int count = 0; 
		try {
			while (input.hasNextLine()) {         	
        			
				eventString = input.nextLine();
				PositionReport event = PositionReport.parse(eventString);
				
				if (event.correctPositionReport()) {
					
					count++;
					output.write(event.toString(newXway) + "\n");            	            	            	         	
			}}			
		} catch (IOException e) { System.err.println(e); }
		System.out.println("Count: " + count + " Last event: " + eventString);
	}
	
	/****************************************************************************
	 * Merge 2 files
	 * @param filename1
	 * @param filename2
	 */
	public static void mergeFiles (String inputfilename1, String inputfilename2, String outputfilename) {
		
		int lastSec = 10784;
		Scanner input1 = null;
		Scanner input2 = null;
		try {		
			/*** Input file ***/
			File input_file_1 = new File(inputfilename1);
			File input_file_2 = new File(inputfilename2);
			input1 = new Scanner(input_file_1);  			
			input2 = new Scanner(input_file_2);
					
			/*** Output file ***/
            File output_file = new File(outputfilename);
            BufferedWriter output = new BufferedWriter(new FileWriter(output_file));
            
            /*** Call method ***/            
            merge(input1,input2,output,lastSec);
                       
            /*** Close the files ***/
       		input1.close();
       		input2.close();
       		output.close();        		
        
		} catch (IOException e) { System.err.println(e); }		  
	}
	
	/***
	 * Merges 2 sorted files input1 and input2 into one sorted file output. The files are sorted by time stamps. 
	 * @param input1
	 * @param input2
	 * @param output
	 * @param lastSec last second in both input files
	 */
	public static void merge (Scanner input1, Scanner input2, BufferedWriter output, int lastSec) {
		
		String eventString1 = input1.nextLine();
		String eventString2 = input2.nextLine();
		PositionReport event1 = PositionReport.parse(eventString1);
		PositionReport event2 = PositionReport.parse(eventString2);
		double curr_sec = 0;
		int count = 0; 
		
		try {
			
			while (curr_sec <= lastSec) {							
				
				while (event1 != null && event1.sec == curr_sec) {
					
					count++;
						
					// Write event1
					output.write(eventString1 + "\n");
						
					// Reset event1
					if (input1.hasNextLine()) {
						eventString1 = input1.nextLine();
						event1 = PositionReport.parse(eventString1);
					} else {
						event1 = null;
					}
				} 		
				while (event2 != null && event2.sec == curr_sec) {
					
					count++;
					
					// Write event2
					output.write(eventString2 + "\n");
					
					// Reset event2
					if (input2.hasNextLine()) {
						eventString2 = input2.nextLine();
						event2 = PositionReport.parse(eventString2);
					} else {
						event2 = null;
					}
				} 	
				curr_sec++;
			}
		} catch (IOException e) { System.err.println(e); }	
		System.out.println("Count: " + count + " Last event: " + eventString2);
	}
	
	/****************************************************************************
	 * Count number of tuples in the merged file
	 * @param inputfilename
	 */
	public static void countTuples (String inputfilename) {
	
		Scanner input = null;
		try {		
			/*** Input file ***/
			File input_file = new File(inputfilename);
            input = new Scanner(input_file);           
            
            /*** Call method ***/                      
            String eventString = "";	
    		int count = 0; 
    		while (input.hasNextLine()) {         	
            			
    			count++;
    			eventString = input.nextLine();
    		} 
    		System.out.println("Count: " + count + " Last event: " + eventString);	
            
            /*** Close the files ***/       		
       		input.close();       		       		
        
		} catch (IOException e) { System.err.println(e); }		  
	}	
	
	/****************************************************************************
	 * Copy all tuples with given direction or just a given number of tuples
	 * @param inputfilename
	 * @param outputfilename
	 * @param dir
	 */
	public static void getTuples (int choice, String inputfilename, String outputfilename, int n) {
		
		Scanner input = null;
		try {		
			/*** Input file ***/
			File input_file = new File(inputfilename);
	        input = new Scanner(input_file);     
	        
	        /*** Output file ***/
            File output_file = new File(outputfilename);
            BufferedWriter output = new BufferedWriter(new FileWriter(output_file));
	            
	        /*** Call method ***/    
            if (choice==1) {
            	selectTuples(input,output,n);
            } else {
            	copyTuples(input,output,n);
            }
	            
	        /*** Close the files ***/       		
	       	input.close();       		       		
	       	output.close();
	        
		} catch (IOException e) { System.err.println(e); }		  
	}	
	
	/**
	 * Select position reports that have the given direction from input to output
	 * @param input
	 * @param output
	 * @param dir
	 */
	public static void selectTuples (Scanner input, BufferedWriter output, int dir) {
		
		String eventString = "";
		int count = 0; 
		try {
			while (input.hasNextLine()) {         	
        			
				eventString = input.nextLine();
				PositionReport event = PositionReport.parse(eventString);
				
				if (event.correctPositionReport() && event.dir == 0) {
					
					count++;
					output.write(eventString + "\n");            	            	            	         	
				}
			}   
		} catch (IOException e) { System.err.println(e); }	
		System.out.println("Count: " + count + " Last event: " + eventString);
	}
	
	/***
	 * Copy the given number of tuples from input to output
	 * @param input
	 * @param output
	 * @param tuple number
	 */
	public static void copyTuples (Scanner input, BufferedWriter output, int tupleNumber) {
		
		String eventString = "";
		int count = 0; 
		try {
			while (input.hasNextLine() && count < tupleNumber) {         	
        		
				count++;
				eventString = input.nextLine();
				output.write(eventString + "\n");            	            	            	         	
			}   
		} catch (IOException e) { System.err.println(e); }				
	}
}

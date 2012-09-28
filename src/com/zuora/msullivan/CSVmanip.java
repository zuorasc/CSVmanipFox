/**
 * 
 */
package com.zuora.msullivan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * @author msullivan
 *
 */
public class CSVmanip {
	private static CsvReader lookupReader;
	private static CsvReader inputReader;
	
	private static CsvWriter outputWriter;
	static private String outputFile;
	
	private static int lookupColumn;
	private static int inputColumn;
	private static int valueColumn;
	
	private static boolean hasOptions = false;
	
	private static boolean variableOut = false;
	private static ArrayList<String> lookupOutputColumns;
	private static ArrayList<String> valueOutputColumns;
	
	private static boolean replaceValue = false;
	private static int replaceColumn;
	
	private static HashMap<String, ArrayList<String>> lookupValueHash;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if(args.length < 3 || args.length > 4){
			System.out.println("Usage: <options> <CSV look up file> <CSV input file> <output file>");
			return;
		}
		
		lookupValueHash = new HashMap<String, ArrayList<String>>();
		
		if(args[0].charAt(0) == '-'){ parseOptions(args[0].charAt(1));}
		
		getLookup(args);
		getInput(args);
		doLookUp();
		
		if(!variableOut || replaceValue) {
			outputFile = args[2];
		} else { outputFile = args[3]; }
		writeOutput();

		System.out.println("Happily Ever After");


	}
	
	private static void parseOptions(Character flag){
		
		switch (flag) {
		
		case 'v':   variableOut = hasOptions = true;
					lookupOutputColumns = new ArrayList<String>(5);
					break;
		case 'r':   replaceValue = hasOptions = true;
					lookupOutputColumns = new ArrayList<String>(5);
					break;
		
		}
		
	}
	
	private static void getLookup(String[] args){
		
		Scanner in = new Scanner(System.in);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			if(!hasOptions) {
				lookupReader = new CsvReader(args[0]);
			} else { lookupReader = new CsvReader(args[1]);}
			System.out.println("Headers Found in Lookup: ");
			if(lookupReader.readHeaders()){
				String[] headers = lookupReader.getHeaders();
				for(int i =0; i < headers.length; i++){
					System.out.println( i + ") " + headers[i]);
				}
			} else { System.out.println("Error Reading lookup headers"); return;}
			
			System.out.println("Enter Column number to use for lookup");
			lookupColumn = 6;
			System.out.println("DEBUG "+lookupColumn);
			//lookupColumn = in.nextInt();
			
			if(replaceValue){
				System.out.println("Enter Column to replace");
				
				replaceColumn = in.nextInt();
			}
			
			if(replaceValue){
				System.out.println("Enter Column numbers to replace");
				replaceColumn = in.nextInt();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("Error reading Lookup file");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
		
	}

	
	private static void getInput(String[] args){
		
		Scanner in = new Scanner(System.in);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		
		try {
			if(!hasOptions) {
				inputReader = new CsvReader(args[1]);
			} else { inputReader = new CsvReader(args[2]); }
			if(inputReader.readHeaders()){
				System.out.println("Headers Found in Input: ");
				for(int i =0; i < inputReader.getHeaderCount(); i++){
					System.out.println( i + ") " + inputReader.getHeader(i));
				}
			} else { System.out.println("Error Reading input headers"); return;}
			
			System.out.println("Enter Column number to use for input");
			
			inputColumn = in.nextInt();
			
			System.out.println("Enter Column number to use for value");
			
			valueColumn = in.nextInt();
			
			if(variableOut){
				System.out.println("Enter Column numbers to print, in order");
				
				String input = br.readLine();
				for(String s : input.split(",")){
					s = s.trim();
					valueOutputColumns.add(s);
				}
				
				System.out.println("Columns to print from lookup:");
				for(String s : valueOutputColumns){
					System.out.println(inputReader.getHeader(Integer.parseInt(s)));
				}
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("Error reading input file");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
	}
	
	static void doLookUp(){
		
		String key;
		
		try {
			// read the lookup file pull out keys put in hashmap
			while(lookupReader.readRecord()){
				key = lookupReader.get(lookupColumn);
				lookupValueHash.put(key, new ArrayList<String>(4));
			}
			Set<String> keys = lookupValueHash.keySet();
			String value;
			//read input file if key is in hashmap add to arraylist
			while(inputReader.readRecord()){
				key = inputReader.get(inputColumn);
				if(keys.contains(key)){
					value = inputReader.get(valueColumn);
					lookupValueHash.get(key).add(value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return;
	}
	
	static private void writeOutput(){
		
		if(replaceValue){
			try {
				outputWriter = new CsvWriter(new FileWriter(outputFile, true), ',');
				// Reprint Headers
				for(String header: lookupReader.getHeaders()){
					outputWriter.write(header);
				}
				outputWriter.endRecord();
				// read through the lookup file
				while(lookupReader.readRecord()){
					//go through the headers and check to see if it is the one to replace
					//print value accordingly
					for(String header: lookupReader.getHeaders()){
						if(header.equals(lookupReader.get(replaceColumn))){
							outputWriter.write(lookupValueHash.get(lookupReader.get(replaceColumn)).get(0));
						}
						else {
							outputWriter.write(lookupReader.get(header));
						}
						outputWriter.endRecord();
					}
				}
				outputWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			outputWriter = new CsvWriter(new FileWriter(outputFile, true), ',');
			
			String values;
				
			outputWriter.write(lookupReader.getHeader(lookupColumn));
			outputWriter.write(inputReader.getHeader(inputColumn));
			outputWriter.endRecord();
		
			for(String s : lookupValueHash.keySet()){
				outputWriter.write(s);
				values = "";
				for(String r : lookupValueHash.get(s)){
					values = values + r + " ";
				}
				outputWriter.write(values);
				outputWriter.endRecord();
			}
			
			outputWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	

	
}

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
	static private String lookupFile;
	private static CsvReader lookupReader;
	static private String inputFile;
	private static CsvReader inputReader;
	
	private static CsvWriter outputWriter;
	static private String outputFile;
	static private File logFile;
	
	private static int lookupColumn;
	private static int inputColumn;
	private static int valueColumn;
	
	private static boolean hasOptions = false;
	
	private static boolean variableOut = false;
	private static ArrayList<String> lookupOutputColumns;
	private static ArrayList<String> valueOutputColumns;
	
	private static boolean replaceValue = false;
	private static int replaceColumn;
	
	private static HashMap<String, String> lookupValueHash;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Add logFile writer
		
		if(args.length < 3 || args.length > 4){
			System.out.println("Usage: <options> <CSV look up file> <CSV input file> <output file>");
			return;
		}
		
		logFile = new File("log.out");
		
		lookupValueHash = new HashMap<String, String>();
		
		if(args[0].charAt(0) == '-'){ 
			parseOptions(args[0].charAt(1));
			hasOptions = true;
		}
		
		if(!hasOptions) {
			lookupFile = args[0];
			inputFile = args[1];
		} else { 
			lookupFile = args[1];
			inputFile = args[2];
		}
		
		getLookup();
		getInput();
		doLookUp();
		
		if(hasOptions) {
			outputFile = args[1].toString() + ".out";
		} else { outputFile = args[3]; }
		writeOutput();

		System.out.println("Happily Ever After");


	}
	
	private static void parseOptions(Character flag){
		
		switch (flag) {
		
		case 'v':   variableOut = hasOptions = true;
					lookupOutputColumns = new ArrayList<String>(5);
					break;
		case 'r':   replaceValue = true;
					//lookupOutputColumns = new ArrayList<String>(5);
					break;
		
		}
		
	}
	
	private static void getLookup(){
		
		Scanner in = new Scanner(System.in);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			lookupReader = new CsvReader(lookupFile);
			System.out.println("Headers Found in Lookup: ");
			if(lookupReader.readHeaders()){
				String[] headers = lookupReader.getHeaders();
				for(int i =0; i < headers.length; i++){
					System.out.println( i + ") " + headers[i]);
				}
			} else { System.out.println("Error Reading lookup headers"); return;}
			
			System.out.println("Enter Column number to use for lookup");
//			lookupColumn = 4;
//			System.out.println("DEBUG "+lookupColumn);
			lookupColumn = in.nextInt();
			
			if(replaceValue){
				System.out.println("Enter Column to replace");
//				replaceColumn = lookupColumn;
//				System.out.println("DEBUG " + replaceColumn);
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

	
	private static void getInput(){
		
		Scanner in = new Scanner(System.in);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		
		try {
			
			inputReader = new CsvReader(inputFile);
		
			if(inputReader.readHeaders()){
				System.out.println("Headers Found in Input: ");
				for(int i =0; i < inputReader.getHeaderCount(); i++){
					System.out.println( i + ") " + inputReader.getHeader(i));
				}
			} else { System.out.println("Error Reading input headers"); return;}
			
			System.out.println("Enter Column number to use for input");
//			inputColumn = 1;
//			System.out.println("DEBUG " + inputColumn);
			inputColumn = in.nextInt();
			
			System.out.println("Enter Column number to use for value");
//			valueColumn = 14;
//			System.out.println("DEBUG " + valueColumn);
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
	/*
	 * Uses the lookupColumn to get a set of Keys for the lookupFile 
	 * then uses the inputColumn and valueColumn to populate a hashTable, from the inputFile
	 * 
	 */
	static void doLookUp(){
		
		String key;
		ArrayList<String> keys = new ArrayList<String>();
		try {
			// read the lookup file pull out keys put in hashmap
			while(lookupReader.readRecord()){
				key = lookupReader.get(lookupColumn);
				if (key.length() > 0 && !key.isEmpty()) {
					//if key is not null or empty
					keys.add(key);
				}
			}
			//Set<String> keys = lookupValueHash.keySet();
			String value;
			//read input file if key is in hashmap add to arraylist
			while(inputReader.readRecord()){
				key = inputReader.get(inputColumn);
				if(keys.contains(key)){
					value = inputReader.get(valueColumn);
					if(value.length() > 0 && !value.isEmpty()) {
						//if the value is not null or empty	
						lookupValueHash.put(key, value);
					}
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return;
	}
	
	static private void writeOutput(){
		
		String cardNumber;
		String customerID;
		String ccType;
		String exprYear;
		String exprMonth;
		String name;
		String addr1;
		String addr2;
		String state;
		String city;
		String zip;
		
		if(replaceValue){
			try {
				outputWriter = new CsvWriter(new FileWriter(outputFile, true), ',');
				// Reprint Headers
				for(String header: lookupReader.getHeaders()){
					outputWriter.write(header);
				}
				outputWriter.endRecord();
				// read through the lookup file
				lookupReader.close();
				lookupReader = new CsvReader(lookupFile);
				lookupReader.readHeaders();
				//TODO: should only print matches
				Integer line = 0;
				/*
				 * Read lookupFile oneLine at a time
				 * get the card number and card type, then write out.
				 * 
				 */
				while(lookupReader.readRecord()){
					
					customerID = lookupReader.get(1);
					cardNumber = lookupValueHash.get(lookupReader.get(lookupColumn));
					ccType = getCCType(cardNumber);
					exprYear= lookupReader.get(6);
					exprMonth = lookupReader.get(7);
					name = lookupReader.get(8);
					addr1 = lookupReader.get(9);
					addr2 = lookupReader.get(10);
					state = lookupReader.get(12);
					city = lookupReader.get(13);
					zip = lookupReader.get(14);
					
					write(customerID, cardNumber, ccType, exprYear, exprMonth, name, addr1, addr2, state, city, zip);
				}
				outputWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
					/* OLD Code had checks for null AccountID and CCnum, now done in shell scripts
					//go through the headers and check to see if it is the one to replace
					//print value accordingly
					for(String header: lookupReader.getHeaders()){
						if (lookupReader.get(replaceColumn) == null ) {
							System.out.println("Account: " + lookupReader.get(1) + " not found @ input line " + line);
							outputWriter.write(""); 
						} 
						else if (lookupValueHash.get(lookupReader.get(replaceColumn)) == null) {
							System.out.println("null card for PTProfile:" + lookupReader.get(replaceColumn) + "@ input line " + line);
							outputWriter.write("");
						}
						else if(header.equals(lookupReader.getHeader(2))){
							outputWriter.write("CreditCard");
						}
						else if(header.equals(lookupReader.getHeader(3))){
							//if(lookupValueHash.get(lookupReader.get(replaceColumn)).get(0) != null) {
								//String ccNum = lookupValueHash.get(lookupReader.get(replaceColumn)).get(0);
								try { 
									if (lookupReader.get(replaceColumn) == null ) {
										System.out.println("null PTProfile for Account @input line " + line);
										outputWriter.write(""); 
									}
									String ccNum = lookupValueHash.get(lookupReader.get(replaceColumn));
									if(ccNum == null ) { 
										System.out.println("null card for PTProfileId @input line " + line);
										//outputWriter.write("");
										ccNum = "100";
									}
									
							} catch (Exception e) {
								System.out.println("Exception in Card Type Lookup for PTProfileID @ input line " + line);
							}
						}
						else if(header.equals(lookupReader.getHeader(replaceColumn))){
							//replace field
							try { 
								if (lookupReader.get(replaceColumn) == null ) {
									System.out.println("null PTProfile for Account @line " + line);
									//break; 
								}
								String out = lookupValueHash.get(lookupReader.get(replaceColumn));
							outputWriter.write(out);
							} catch (Exception e) {
								System.out.println("Exception in Card Type Lookup for PTProfileID @line " + line);
								outputWriter.write("");
							}
						}
						else {
							//else copy to output
							outputWriter.write(lookupReader.get(header));
						}
					}
					outputWriter.endRecord();
					line++;//*/
			
		}
		
		try {
			outputWriter = new CsvWriter(new FileWriter(outputFile, true), ',');
			
			String values;
				
			outputWriter.write(lookupReader.getHeader(lookupColumn));
			outputWriter.write(inputReader.getHeader(inputColumn));
			outputWriter.endRecord();
		
			for(String s : lookupValueHash.keySet()){
				outputWriter.write(s);
//				values = "";
//				for(String r : lookupValueHash.get(s)){
//					values = values + r + " ";
//				}
				outputWriter.write(lookupValueHash.get(s));
				outputWriter.endRecord();
			}
			
			outputWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	private static boolean write(String customerAccount, String ccNumber, String ccType, String exprMonth, String exprYear, String name, String addr1, String addr2, String city, String state, String zip){
		boolean result = true;
		
		try {
			outputWriter.write("");
			outputWriter.write(customerAccount);
			outputWriter.write("CreditCard");
			outputWriter.write(ccType);
			outputWriter.write(ccNumber);
			outputWriter.write("");
			outputWriter.write(exprYear);
			outputWriter.write(exprMonth);
			outputWriter.write(name);
			outputWriter.write(addr1);
			outputWriter.write(addr2);
			outputWriter.write("USA");
			outputWriter.write(state);
			outputWriter.write(city);
			outputWriter.write(zip);
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("TRUE");
			outputWriter.write("");
			outputWriter.write("Active");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.endRecord();
			
		} catch (IOException e) {
			
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	
	private static String getCCType(String ccNum){
		
		String ccTypeCode =  ccNum.substring(0, 1);
		int ccTypeCodeInt = Integer.parseInt(ccTypeCode);
		String result;
		
		switch (ccTypeCodeInt) {
		case 4 : 
			result = "Visa";
			break;
		case 5 : 
			result = "MasterCard";
			break;
		case 6 :
			result = "Discover";
			break;
		case 3 :
			result = "AmericanExpress";
			break;
		default :
			result = "UNKNOWN";
		
		}
		return result;
		
	}

	
}

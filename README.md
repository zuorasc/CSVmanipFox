#CSVmanip
===
***
simple csv manipulating program to do simple lookup for CA

Usage: Usage: <CSV look up file> <CSV input file> <output file>

##TODO
* Add -v option for variable output	
* Add -f option to print whole row from input file
* Add -r option to replace value in lookup file, (Payment Method skeleton file use case) 

##DONE
* initial set up and IO
* Add input for value to lookup
* Add hash of <Lookup> <Value> from input file
* print hash to file
* moved functional code to functions

##Questions
1.


***

###Mapping Logic
lookupFile:  
Column0, Column1, Column2  
`            *  `
			  
valueFile:  
Column0, Column1, Column2  
`    *               $  `
	  
*: lookup field  
$: value field  
  
Goal: Hash lookupValueHash<String lookup, String[] values>  
	  
	
`for(* in lookupFile)  
	lookupFile.Read()  
	key = lookupFile.get(*);  
	keys.add(key);  
for(row in valueFile)  
	valueFile.read()  
	if(vauleFile.get(*) exists in keys)  
		value = valueFile.get($)  
		lookupValueHash.add(key, value)  
`  



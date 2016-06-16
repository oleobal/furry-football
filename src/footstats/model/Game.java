package footstats.model;

import footstats.view.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class Game
{
	private ArrayList<Snapshot> snapshots;

	/**
	 * just dump the source file as input
	 */
	public Game(String input)
	{
		snapshots = new ArrayList<Snapshot>();
		
		long start = System.currentTimeMillis();
		
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
		    String line;
		    
		    System.out.println("Parsing data from " + input + "...");
		    
		    // Initialize date parser for every format possibilities
		    SimpleDateFormat dateParserNoMillisec  = new SimpleDateFormat("\"yyyy-mm-dd hh:mm:ss\"");
		    SimpleDateFormat dateParserOneMillisec = new SimpleDateFormat("\"yyyy-mm-dd hh:mm:ss.S\"");
		    SimpleDateFormat dateParserTwoMillisec = new SimpleDateFormat("\"yyyy-mm-dd hh:mm:ss.SS\"");
		    
		    Snapshot currentSnapshot = null;
		    Instant currentTimestamp = null;
		    
		    // for each line of the file
		    while ((line = br.readLine()) != null) {
		       
		    	String data[] = line.split(",");
		    
		    	// choose the correct date parser for the timestamp
		    	if(data[0].length() == dateParserNoMillisec.toPattern().length())
		    	{
		    		currentTimestamp = dateParserNoMillisec.parse(data[0]).toInstant();
		    	}
		    	else if(data[0].length() == dateParserOneMillisec.toPattern().length())
		    	{
		    		currentTimestamp = dateParserOneMillisec.parse(data[0]).toInstant();
		    	}
		    	else if(data[0].length() == dateParserTwoMillisec.toPattern().length())
		    	{
		    		currentTimestamp = dateParserTwoMillisec.parse(data[0]).toInstant();
		    	}
		    	else throw new ParseException("Unsupported date format in file " + input, 0);
		    	
		    	// if the parsed timestamp is different from the current snapshot's, create a new snapshot with that timestamp and add it to the list of snapshots
		    	if(currentSnapshot == null || currentTimestamp.compareTo(currentSnapshot.getTimestamp()) > 0)
		    	{
		    		currentSnapshot = new Snapshot(currentTimestamp);
		    		snapshots.add(currentSnapshot);
		    	}
		    	
		    	// create and add trace to the current snapshot
		    	currentSnapshot.addTrace(new Trace(Integer.parseInt(data[1]),
		    									   Float.parseFloat(data[2]),
		    									   Float.parseFloat(data[3]),
		    									   Float.parseFloat(data[4]),
		    									   Float.parseFloat(data[5]),
		    									   Float.parseFloat(data[6]),
		    									   Float.parseFloat(data[7]),
		    									   Float.parseFloat(data[8])));
		   
		    }
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Couldn't find file " + input);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		
		System.out.println("Finished in " + (System.currentTimeMillis() - start) + " ms. " + snapshots.size() + " snapshot entries added.");
	}
	
	/**
	 * returns a copy
	 */
	public ArrayList<Snapshot> getSnapshots()
	{
		@SuppressWarnings("unchecked")
		ArrayList<Snapshot> lol = (ArrayList<Snapshot>)snapshots.clone();
		return lol;
	}
	
	public static void main(String[] args)
	{		
		Game g = new Game("../data/2013-11-03_tromso_stromsgodset_first.csv");
		Thermap m = new Thermap(2, g.getSnapshots());
		System.out.println(m);
		
		MyFrame frame = new MyFrame("Lenny");
	}

}

package footstats.model;

import java.util.Date;
import java.util.HashMap;

/**
 * how the field is at a specific time
 */
public class Snapshot
{
	private Date timestamp;
	private HashMap<Integer, Trace> data;

	public Snapshot(Date time, Trace[] input)
	{
		timestamp = time;
	
		for (Trace lol: input)
			data.add(lol.player, lol);

	}

}

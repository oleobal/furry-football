package footstats.model;

import java.time.Instant;
import java.util.HashMap;

/**
 * how the field is at a specific time
 */
public class Snapshot
{
	private Instant timestamp;
	private HashMap<Integer, Trace> data; // data for each player

	public Snapshot(Instant time)
	{
		timestamp = time;
		data = new HashMap<Integer, Trace>();
	}
	
	/**
	 * Add the given trace to the Trace map of this snapshot.
	 * @param t Trace to add
	 */
	public void addTrace(Trace t)
	{
		data.put(t.player, t);
	}
	
	/**
	 * Returns the Trace object describing the given player for this snapshot.
	 * @param playerID ID of the player
	 * @return Trace of the player, null if player isn't present in the snapshot
	 */
	public Trace getTraceOfPlayer(int playerID)
	{
		return data.get(playerID);
	}
	
	/**
	 * Getter.
	 * @return Timestamp of the snapshot
	 */
	public Instant getTimestamp()
	{
		return timestamp;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Snapshot " + timestamp + "\n");
		for(Trace t : data.values())
		{
			sb.append("	 " + t + "\n");
		}
		return sb.toString();
	}

}

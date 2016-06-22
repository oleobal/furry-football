package footstats.model;

import java.util.ArrayList;

import com.sun.xml.internal.ws.wsdl.parser.MexEntityResolver;

/**
 *	         x  +------------------------------------------105->
 *
 *		y   +-------------------------+------------------------+
 *			|                         |                        |
 *		+   |                         |                        |
 *		|   |                         |                        |
 *		|   |                         |                        |
 *		|   +--+                   +-----+                  +--+
 *		|   |  |                   |  |  |                  |  |
 *		|   |  |                   |  |  |                  |  |
 *		|   |  |                   |  |  |                  |  |
 *		|   +--+                   +-----+                  +--+
 *		|   |                         |                        |
 *		|   |                         |                        |
 *	   68   |                         |                        |
 *		|   |                         |                        |
 *		v   +-------------------------+------------------------+
 *
 */
public class Thermap
{
	private int[][] terrain;
	
	/**
	 * whose thermical map it is 
	 */
	public final int playerId;
	private int maxHeat;
	
	public Thermap(Integer player, ArrayList<Snapshot> s)
	{
		maxHeat = 0;
		terrain = new int[105][68];
		playerId = player;
		Trace lol;
		for (Snapshot i : s)
		{
			lol = i.getTraceOfPlayer(player);
			if (lol != null) //joueur not found
			{
				if (lol.posX>=0 && lol.posX<105 && lol.posY>=0 && lol.posY<68) // banc de touche = osef
				{
					//System.err.println(i.getTimestamp()+"  "+terrain[(int)lol.posX][(int)lol.posY]);
					terrain[(int)lol.posX][(int)lol.posY]++;
					if(terrain[(int)lol.posX][(int)lol.posY] > maxHeat)
					{
						maxHeat = terrain[(int)lol.posX][(int)lol.posY];
					}
				}
			}
		}
		
	}
	
	/**
	 * returns a copy of the map as an array of ints
	 */
	public int[][] getMap()
	{
		int[][] lol = new int[105][68];
		int i=0,j=0;
		while(j<68)
		{
			while(i<105)
			{
				lol[i][j] = terrain[i][j];
				i++;
			}
			i=0;j++;
		}
		return lol;
	}
	
	public int getMaxHeat()
	{
		return maxHeat;
	}
	
	
	/**
	 * returns a grid of numbers from 0 to nine
	 * coloured, even
	 * not windows-compatible, I hear
	 */
	public String toString()
	{
		int highestValue = 0;
		int i=0,j=0;
		while(j<68)
		{
			while(i<105)
			{
				if (terrain[i][j] > highestValue)
					highestValue=terrain[i][j];
				i++;
			}
			i=0;j++;
		}

		if (highestValue == 0)
		{
			return "The player "+playerId+" has not appeared.";
		}
		
		
		//merci l'internet		
		String ANSI_RESET  = "\u001B[0m" ;
		String ANSI_BLACK  = "\u001B[30m";
		String ANSI_RED    = "\u001B[31m";
		String ANSI_GREEN  = "\u001B[32m";
		String ANSI_YELLOW = "\u001B[33m";
		String ANSI_BLUE   = "\u001B[34m";
		String ANSI_PURPLE = "\u001B[35m";
		String ANSI_CYAN   = "\u001B[36m";
		String ANSI_WHITE  = "\u001B[37m";


		String returned = "";
		i=0 ;j=0;
		while(j<68)
		{
			while(i<105)
			{
				if ((int)(terrain[i][j]*9/highestValue) > 0)
					returned+=ANSI_GREEN;
				if ((int)(terrain[i][j]*9/highestValue) > 5)
					returned+=ANSI_YELLOW;
				if ((int)(terrain[i][j]*9/highestValue) > 8)
					returned+=ANSI_RED;
				returned+=(int)(terrain[i][j]*9/highestValue);
				returned+=ANSI_RESET;
				i++;
			}
			returned+="\n";
			i=0;j++;
		}
		
		return returned;
	}
}

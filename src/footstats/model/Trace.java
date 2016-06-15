package footstats.model;

/**
 * each line of the source files
 */
public class Trace
{
	public final Integer player;
	/**
	 * current location
	 */
	public final Float posX, posY;

	/**
	 * direction we're facing
	 */
	public final Float heading;

	/**
	 * direction we're going
	 */
	public final Float direction;

	/**
	 * consumed energy
	 */
	public final Float energy;

	/**
	 * in fraction of the speed of light
	 */
	public final Float speed;

	/**
	 * distance travelled since start of match
	 */
	public final Float totalDistance;


	public Trace(Integer p, Float x, Float y, Float h, Float d, Float e, Float s, Float td)
	{
		player = p;
		posX = x;
		posY = y;

		heading = h;
		direction = d;
		energy = e;
		speed = s;
		totalDistance = td;


	}
	
	public String toString()
	{
		return "Player " + player +
			   "\n	 Position X:               " + posX +
			   "\n	 Position Y:               " + posY +
			   "\n	 Direction facée:          " + heading +
			   "\n	 Direction de déplacement: " + direction +
			   "\n	 Energie:                  " + energy +
			   "\n	 Vitesse:                  " + speed +
			   "\n	 Déplacement total:        " + totalDistance + "\n";
	}
}

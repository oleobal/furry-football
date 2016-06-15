package footstats.model;

/**
 * each line of the source files
 */
public class Trace
{
	public final int player;
	/**
	 * current location
	 */
	public final float posX, posY;

	/**
	 * direction we're facing
	 */
	public final float heading;

	/**
	 * direction we're going
	 */
	public final float direction;

	/**
	 * consumed energy
	 */
	public final float energy;

	/**
	 * in fraction of the speed of light
	 */
	public final float speed;

	/**
	 * distance travelled since start of match
	 */
	public final float totalDistance;


	public Trace(int p, float x, float y, float h, float d, float e, float s, float td)
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

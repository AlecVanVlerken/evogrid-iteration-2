package sim.naturalselection;


import sim.World;
import util.Point;

/**
 * @immutable
 */
public interface NaturalSelection
{
	/**
	 * @inspects | world
     * @inspects | position
	 * @pre | Point.isWithin(position, world.getWidth(), world.getHeight())
	 * @pre | world != null && position != null
	 */
    public boolean survives(World world, Point position);
}

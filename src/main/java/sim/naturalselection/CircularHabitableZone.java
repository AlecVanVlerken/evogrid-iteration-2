package sim.naturalselection;

import sim.World;
import util.Point;

/**
 * @immutable
 */
public class CircularHabitableZone implements NaturalSelection
{
	/**
	 * @invar | center != null
	 */
    private final Point center;

    /**
	 * @invar | radiusSquared >= 0
	 */
    private final int radiusSquared;

    /**
     * Initializes a new CircularHabitableZone object.
     *
     * @throws IllegalArgumentException | center == null
     * @post | this.getRadiusSquared() == radius * radius
     * @post | this.getCenter() == center
     */
    public CircularHabitableZone(Point center, int radius)
    {
    	if (center == null) {throw new IllegalArgumentException(); }
        this.center = center;
        this.radiusSquared = radius * radius;
    }
    
    /**
     * @post | result != null
     */
	public Point getCenter() {
		return this.center;
	}
	
	/**
     * @post | result >= 0
     */
	public int getRadiusSquared() {
		return this.radiusSquared;
	}

    @Override
    /**
     * Determines whether a creature survives in the given world at the specified position based on the circle size.
     * A creature survives if its position is within a certain distance from the center of the circle.
     * 
     * @inspects | world
     * @inspects | position
     * @pre | Point.isWithin(position, world.getWidth(), world.getHeight())
     * @pre | world != null && position != null
     * @post | result == getCenter().distanceSquared(position) <= getRadiusSquared()
     */
    public boolean survives(World world, Point position)
    {
    	if (center.distanceSquared(position) <= radiusSquared) {
    		return true;
    	}
        return false;
    }
}

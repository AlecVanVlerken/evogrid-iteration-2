package sim.naturalselection;

import sim.World;
import util.Point;

/**
 * @immutable
 */
public class Disjunction implements NaturalSelection
{
	/**
	 * @invar | area1 != null
	 */
	private final NaturalSelection area1;
	/**
	 * @invar | area2 != null
	 */
	private final NaturalSelection area2;
	
	/**
     * Initializes a new Disjunction object.
     *
     * @throws IllegalArgumentException | area1 == null || area2 == null
     * @post | this.getArea1() == area1
     * @post | this.getArea2() == area2
     */
	public Disjunction(NaturalSelection area1, NaturalSelection area2)
	{
		if (area1 == null || area2 == null) {throw new IllegalArgumentException(); }
		this.area1 = area1;
		this.area2 = area2;
	}
	
	/**
     * @post | result != null
     */
	public NaturalSelection getArea1() {
		return this.area1;
	}
	
	/**
     * @post | result != null
     */
	public NaturalSelection getArea2() {
		return this.area2;
	}
	
	/**
     * Determines whether a creature survives in the given world at the specified position based on the conjunction of the two areas.
     * A creature survives if its position is within a certain distance from the the two areas.
     * 
     * @inspects | world
     * @inspects | position
     * @pre | Point.isWithin(position, world.getWidth(), world.getHeight())
     * @pre | world != null && position != null
     * @post | result == (getArea1().survives(world, position) || getArea2().survives(world, position))
     */
	public boolean survives(World world, Point position)
	{
		if (area1.survives(world, position) || area2.survives(world, position)) {
			return true;
		}
		return false;
	}
}

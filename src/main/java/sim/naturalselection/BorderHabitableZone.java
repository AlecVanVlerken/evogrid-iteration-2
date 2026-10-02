package sim.naturalselection;

import sim.World;
import util.Point;

/**
 * @immutable
 */
public class BorderHabitableZone implements NaturalSelection
{
	/**
	 * @invar | borderSize >= 0
	 */
    private final int borderSize;

    /**
     * Initializes a new BorderHabitableZone object.
     *
     * @throws IllegalArgumentException | borderSize < 0
     * @post | this.getBorderSize() == borderSize
     */
    public BorderHabitableZone(int borderSize)
    {
    	if (borderSize < 0) {throw new IllegalArgumentException(); }
        this.borderSize = borderSize;
    }

    /**
     * @post | result >= 0
     */
	public int getBorderSize() {
		return borderSize;
	}
    
    @Override
    /**
    * Determines whether a creature survives in the given world at the specified position based on the border size.
    * Each edge strip contains exactly borderSize cells unless strips overlap.
    * A zero-sized border selects no cells; sufficiently large borders select the entire world.
    * 
    * @inspects | world
    * @inspects | position
    * @pre | Point.isWithin(position, world.getWidth(), world.getHeight())
    * @pre | world != null && position != null
    * @post | result == getBorderSize() > position.getX() || position.getX() >= world.getWidth() - getBorderSize() || getBorderSize() > position.getY() || position.getY() >= world.getHeight() - getBorderSize()
    */
    public boolean survives(World world, Point position)
    {
        if ((borderSize > position.getX() || position.getX() >= world.getWidth() - borderSize || borderSize > position.getY() || position.getY() >= world.getHeight() - borderSize)) {
        	return true;
        }
    	return false;

    }
}

package sim.naturalselection;

import sim.World;
import util.Point;

public class SouthEastHabitableZone implements NaturalSelection {

	@Override
	/**
     * Determines whether a creature survives in the given world at the specified position based on the SouthEast size.
     * A creature survives if its position is within a certain distance from the SouthEastHabitableZone.
     * 
     * @inspects | world
     * @inspects | pos
     * @pre | Point.isWithin(pos, world.getWidth(), world.getHeight())
     * @pre | world != null && pos != null
     * @post | result == ((pos.getX() >= 2 * world.getWidth() / 3) && (pos.getY() >= 2* world.getHeight() / 3))
     */
	public boolean survives(World world, Point pos) {
		return (pos.getX() >= 2 * world.getWidth() / 3) && (pos.getY() >= 2* world.getHeight() / 3);
	}

}

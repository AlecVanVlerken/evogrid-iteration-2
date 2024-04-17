package sim.naturalselection;

import sim.World;
import util.Point;

public class SouthEastHabitableZone implements NaturalSelection {

	@Override
	public boolean survives(World world, Point pos) {
		//return (pos.getX() > world.getWidth() - 5) && (pos.getY() > world.getHeight() - 5);
    	return (pos.getX() >= 2 * world.getWidth() / 3) &&
    			(pos.getY() >= 2* world.getHeight() / 3);
	}

}

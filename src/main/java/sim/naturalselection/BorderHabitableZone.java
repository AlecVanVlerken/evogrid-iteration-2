package sim.naturalselection;

import sim.World;
import util.Point;

public class BorderHabitableZone implements NaturalSelection
{

    private final int borderSize;

    public BorderHabitableZone(int borderSize)
    {
    	
        this.borderSize = borderSize;
    }

    @Override
    public boolean survives(World world, Point position)
    {
        if (world.isInside(position) && (borderSize > position.getX() || position.getX() > world.getWidth() - borderSize || borderSize > position.getY() || position.getY() > world.getHeight() - borderSize)) {
        	return true;
        }
    	return false;

    }
}

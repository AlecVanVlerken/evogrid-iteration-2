package sim.neuralnet;

import sim.Creature;
import sim.World;
import util.Orientation;


public class FreePassageSensorNeuron extends BinarySensorNeuron
{
    private final Orientation orientation;

    public FreePassageSensorNeuron(Orientation orientation)
    {
        this.orientation = orientation;
    }

    @Override
    public boolean detect(World world, Creature creature)
    {
    	Creature dummy_creature = creature.giveCopy();
    	
    	if (orientation.isEqual(Orientation.northWest())) {
    		dummy_creature.turnCounterclockwise();
    	} else if (orientation.isEqual(Orientation.northEast())) {
    		dummy_creature.turnClockwise();
    	}

		if (world.isFree(dummy_creature.getPosition().move(dummy_creature.getOrientation().toVector()))) {
			return true;
		} else {
			return false;
		}
    }
}

package sim.neuralnet;

import sim.Creature;
import sim.World;
import util.Orientation;
import util.Point;

/**
 * @immutable
 */
public class FreePassageSensorNeuron extends BinarySensorNeuron
{
	/**
	 * @invar | orientation != null
	 */
    private final Orientation orientation;
    
    /**
     * Initializes a new FreePassageSensorNeuron object.
     *
     * @throws IllegalArgumentException | orientation == null
     * @post | this.getOrientation() == orientation
     */
    public FreePassageSensorNeuron(Orientation orientation)
    {
    	if (orientation == null) {throw new IllegalArgumentException(); }
        this.orientation = orientation;
    }
    
    /**
     * @post | result != null
     */
	public Orientation getOrientation() {
		return this.orientation;
	}

    @Override
    /**
     * Detects a free cell ahead or one eighth-turn left or right relative to the heading.
     * Out-of-bounds cells and occupied cells are blocked.
     * 
     * @inspects | world
     * @inspects | creature
     * @pre | world != null
     * @pre | creature != null
     */
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

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
     * Detects if the space is free in the given Orientations north, northwest and northeast from the creaturs point of view.
     * 
     * @inspects | world
     * @inspects | creature
     * @pre | world != null
     * @pre | creature != null
     */
     //@post | result == (((getOrientation().isEqual(Orientation.north())) && (world.isFree(creature.getPosition().move(creature.getOrientation().toVector())))) || ((getOrientation().isEqual(Orientation.northWest())) && (world.isFree(creature.getPosition().move(creature.getOrientation().toVector()).move(Orientation.northWest().toVector())))) || ((getOrientation().isEqual(Orientation.northEast())) && (world.isFree(creature.getPosition().move(creature.getOrientation().toVector()).move(Orientation.northEast().toVector())))))
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
